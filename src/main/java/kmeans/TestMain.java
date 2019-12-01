package kmeans;

import Bean.Movies;
import Bean.Ratings;
import dao.InfoGetUtil;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TestMain {

    private static InfoGetUtil util;
    private static List<Ratings> ratingsList;
    private static Map<Integer,Map<Integer,Integer>> userMovieRatingMap;

    static {
        util = new InfoGetUtil();
        try {
            ratingsList = util.getBaseRatings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userMovieRatingMap = buildUserMovieRatingTable();
    }


    public static void main(String[] args) throws SQLException, IOException {
        Map<Integer,float[]> dataSet = buildMovieRatingTable();
        int kMax = 40;
        //画sse折线图
        DefaultCategoryDataset dateset = new DefaultCategoryDataset();
        for(int k = 10; k <= kMax ; k=k+10){
            KMeansRun kRun =new KMeansRun(k, dataSet);
            Set<Cluster> clusterSet = kRun.run();
            //----------
            //将结果输出到文件
            StringBuffer str = new StringBuffer();
            FileWriter fw = new FileWriter("D:\\Kmeans\\Kmeans"+k+".txt", true);
            str.append("迭代次数："+kRun.getIterTimes()+"\n");
            str.append("\n");
            //计算SSE
            List<Point> members;
            float distAll = 0.0f;
            for (Cluster cluster : clusterSet) {
                str.append(cluster.toString());
                members = cluster.getMembers();
                for(Point point : members){
                    distAll += point.getDist();
                }
            }
            str.append("\n");
            str.append("\n");
            str.append("SSE = "+distAll+"   K = "+ k);
            dateset.setValue(distAll, "sse", k+"");
            fw.write(str.toString());
            fw.close();
        }

        JFreeChart chart= ChartFactory.createLineChart(
                "", //图表标题
                "K", //X轴lable
                "SSE", //Y轴lable
                 dateset, //数据集
                 PlotOrientation.VERTICAL, //图表放置模式水平/垂直 
                false, //显示lable
                false, //显示提示
                false //显示urls
        );
        chart.setBackgroundPaint(ChartColor.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.WHITE);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(28000);
        rangeAxis.setUpperBound(32000);
        rangeAxis.setAutoTickUnitSelection(false);
        NumberTickUnit unit = new NumberTickUnit(200);
        rangeAxis.setTickUnit(unit);
        OutputStream os=new FileOutputStream("D:\\Kmeans\\sse.jpg");
        ChartUtilities.writeChartAsJPEG(os, chart, 1000, 1000);
        os.close();

    }


    /**
     * 构建用户对电影的评分map
     * 形如{u:{m1:rat,m2:rat,...},...}
     * @return
     */
    public static Map<Integer,Map<Integer,Integer>> buildUserMovieRatingTable(){
        Map<Integer,Map<Integer,Integer>> resultMap = new HashMap<>();
        Map<Integer,Integer> tempMap;
        for(Ratings ratings : ratingsList){
            if(!resultMap.containsKey(ratings.getUserId())){
                //结果集中不包含该用户的键值对
                tempMap = new HashMap<>();
                tempMap.put(ratings.getMovieId(),ratings.getRating());
                resultMap.put(ratings.getUserId(),tempMap);
                tempMap = null;
            }else {
                resultMap.get(ratings.getUserId()).put(ratings.getMovieId(),ratings.getRating());
            }
        }
        return resultMap;
    }

    /**
     * 构建电影和用户评分的矩阵
     * @return
     */
    @Test
    public static Map<Integer,float[]> buildMovieRatingTable() throws SQLException {
        //ArrayList<float[]> dataSet = new ArrayList<>();
        Map<Integer,float[]> dataSet = new HashMap<>();
        //拿到有效电影
        List<Movies> movies = util.getMovies();
        //拿到有效用户
        List<Integer> users = util.selectBaseUser();
        //生成矩阵
        float[] floats;
        int size = users.size();
        Integer rating;
        for(Movies movie : movies){
            floats = new float[100];
            //floats = new float[495];
            int j = 0;
            for(int i = 0 ; i < size; i ++){
                rating = userMovieRatingMap.get(users.get(i)).get(movie.getMovieId());
                if(j < 100 && null != rating && 0 != rating){
                    floats[j++] = (float)rating;
                }
//                if(null != rating){
//                    floats[i] = (float)rating;
//                }else {
//                    floats[i] = 0;
//                }
            }
            dataSet.put(movie.getMovieId(),floats);
        }
        return dataSet;
    }
}
