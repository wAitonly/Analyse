package main;

import dao.InfoGetUtil;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 计算用户对各类型关注概率分布图
 */
public class UserKindPKmeansMain {

    private static Map<Integer,List<Integer>> KindMovieMap;
    private static Map<Integer,Integer> movieKindMap;
    private static List<Integer> movieKind;
    private static InfoGetUtil util = new InfoGetUtil();
    static {
        try {
            KindMovieMap = getKindMovieByReadFile(5);
            movieKindMap = getMovieKindByReadFile(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化movieKind
        Set<Integer> keySet = KindMovieMap.keySet();
        movieKind = new ArrayList<>(keySet);
    }


    /**
     * 每个概率出现的次数
     * @param args
     */
    public static void main(String[] args) throws SQLException, IOException {
        Map<Double,Integer> tempPCount;
        //查询用户列表
        List<Integer> userIdList = util.selectBaseUser();
        Map<Integer,Double> tempUserKindP;
        Collection<Double> tempValues;
        DefaultCategoryDataset tempDateset;
        Map.Entry<Double,Integer> tempEntry;
        Iterator<Map.Entry<Double,Integer>> iterator;
        int size = userIdList.size();
        int num = 1;
        for(Integer uesrID : userIdList){
            System.out.println("共"+size+"，正在进行"+(num++));
            if(uesrID != 643){
                continue;
            }
            //画sse折线图
            tempDateset = new DefaultCategoryDataset();
            tempPCount = new TreeMap<>();
            //计算用户关注各类型概率
            tempUserKindP = userKindP(uesrID);
            tempValues = tempUserKindP.values();
            for(Double p : tempValues){
                if(tempPCount.containsKey(p)){
                    tempPCount.put(p,tempPCount.get(p)+1);
                }else {
                    tempPCount.put(p,1);
                }
            }
            //根据tempPCount画图
            iterator = tempPCount.entrySet().iterator();
            //将tempPCount按key的大小排序
            while (iterator.hasNext()){
                tempEntry = iterator.next();
                tempDateset.setValue(tempEntry.getValue(), "count", tempEntry.getKey()+"");
            }
            JFreeChart chart= ChartFactory.createLineChart(
                    "", //图表标题
                    "P", //X轴lable
                    "COUNT", //Y轴lable
                    tempDateset, //数据集
                    PlotOrientation.VERTICAL, //图表放置模式水平/垂直 
                    false, //显示lable
                    false, //显示提示
                    false //显示urls
            );
            chart.setBackgroundPaint(ChartColor.WHITE);
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(ChartColor.WHITE);
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setLowerBound(0);
            rangeAxis.setUpperBound(5);
            rangeAxis.setAutoTickUnitSelection(false);
            NumberTickUnit unit = new NumberTickUnit(1);
            rangeAxis.setTickUnit(unit);
            OutputStream os=new FileOutputStream("D:\\OldRecommentAlgorithmWithoutAverage\\KmeansPCount\\pCount"+uesrID+".jpg");
            ChartUtilities.writeChartAsJPEG(os, chart, 500, 500);
            os.close();
        }
    }

    /**
     * 计算用户对各类型关注概率
     * 原始类
     * @param userId
     * @return
     * @throws SQLException
     */
    private static Map<Integer,Double> userKindP(Integer userId) throws SQLException {
        //查询出该用户评论过的电影
        List<Integer> movieList = util.selectMoviesByUserIdBase(userId);
        //该用户评论过的总数
        Integer userTotalMark = movieList.size();
        Double userTotalMarkDouble = userTotalMark.doubleValue();
        //遍历用户电影列表和电影类型列表,分别计算出各类型被用户评论了多少次
        Map<Integer,Integer> movieKindMarkCount = new HashMap<>();
        int count;
        for(Integer kindId : movieKind){
            count = 0;
            for(Integer movieId : movieList){
                if(KindMovieMap.get(kindId).contains(movieId)){
                    count ++;
                }
            }
            movieKindMarkCount.put(kindId,count);
        }
        //显示释放movieListt占用内存
        movieList = null;
        //--------根据movieKindMarkCount计算各类型被该用户关注的概率
        Map<Integer,Double> movieKindP = new HashMap<>();
        Iterator<Map.Entry<Integer,Integer>> iterator = movieKindMarkCount.entrySet().iterator();
        Map.Entry<Integer,Integer> tempEntry;
        Integer tempKind;
        Integer tempCount;
        BigDecimal b;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempKind = tempEntry.getKey();
            tempCount = tempEntry.getValue();
            b = new BigDecimal(tempCount.doubleValue()/userTotalMarkDouble);
            movieKindP.put(tempKind,b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        //显示释放movieKindMarkCount占用内存
        movieKindMarkCount = null;
        return movieKindP;
    }

    /**
     * 通过读文件给电影分类
     * @return
     */
    public static Map<Integer,List<Integer>> getKindMovieByReadFile(Integer K) throws IOException {
        Map<Integer, List<Integer>> kindMovieMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Kmeans\\Kmeans"+K+".txt")));
        String data;
        Integer tempMovieId = -1;
        Integer tempKindId = -1;
        while((data = br.readLine())!=null){
            data = data.trim().replace(" ","");
            if(data.startsWith("聚簇ID")){
                tempKindId = Integer.valueOf(data.substring(data.indexOf("=")+1,data.indexOf("，")).trim());
                kindMovieMap.put(tempKindId,new ArrayList<>());
            }
            if(data.startsWith("点ID")){
                tempMovieId = Integer.valueOf(data.substring(data.indexOf("=")+1,data.indexOf("，")).trim());
                kindMovieMap.get(tempKindId).add(tempMovieId);
            }
        }
        return kindMovieMap;
    }

    /**
     * 通过读文件给电影分类
     * @return
     */
    public static Map<Integer,Integer> getMovieKindByReadFile(Integer K) throws IOException {
        Map<Integer, Integer> movieKindMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Kmeans\\Kmeans"+K+".txt")));
        String data;
        Integer tempMovieId = -1;
        Integer tempKindId = -1;
        while((data = br.readLine())!=null){
            data = data.trim().replace(" ","");
            if(data.startsWith("聚簇ID")){
                tempKindId = Integer.valueOf(data.substring(data.indexOf("=")+1,data.indexOf("，")).trim());
            }
            if(data.startsWith("点ID")){
                tempMovieId = Integer.valueOf(data.substring(data.indexOf("=")+1,data.indexOf("，")).trim());
                movieKindMap.put(tempMovieId,tempKindId);
            }
        }
        return movieKindMap;
    }
}
