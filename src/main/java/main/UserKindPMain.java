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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 计算用户对各类型关注概率分布图
 */
public class UserKindPMain {

    private static List<String> movieKind;
    private static InfoGetUtil util = new InfoGetUtil();
    static {
        //静态获取数据库连接
        //初始化movieKind
        movieKind = new ArrayList<>();
        movieKind.add("Action");
        movieKind.add("Adventure");
        movieKind.add("Animation");
        movieKind.add("Children's");
        movieKind.add("Comedy");
        movieKind.add("Crime");
        movieKind.add("Documentary");
        movieKind.add("Drama");
        movieKind.add("Fantasy");
        movieKind.add("Film-Noir");
        movieKind.add("Horror");
        movieKind.add("Musical");
        movieKind.add("Mystery");
        movieKind.add("Romance");
        movieKind.add("Sci-Fi");
        movieKind.add("Thriller");
        movieKind.add("War");
        movieKind.add("Western");
    }

    /**
     * 每个概率出现的次数
     * @param args
     */
    public static void main(String[] args) throws SQLException, IOException {
        Map<Double,Integer> tempPCount;
        //查询用户列表
        List<Integer> userIdList = util.selectBaseUser();
        Map<String,Double> tempUserKindP;
        Collection<Double> tempValues;
        DefaultCategoryDataset tempDateset;
        Map.Entry<Double,Integer> tempEntry;
        Iterator<Map.Entry<Double,Integer>> iterator;
        System.out.println(userIdList.size());
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
            rangeAxis.setUpperBound(8);
            rangeAxis.setAutoTickUnitSelection(false);
            NumberTickUnit unit = new NumberTickUnit(1);
            rangeAxis.setTickUnit(unit);
            OutputStream os=new FileOutputStream("D:\\OldRecommentAlgorithmWithoutAverage\\PCount\\pCount"+uesrID+".jpg");
            ChartUtilities.writeChartAsJPEG(os, chart, 1800, 1000);
            os.close();
        }
    }

    /**
     * 计算用户对各类型关注概率
     * y原始类
     * @param userId
     * @return
     * @throws SQLException
     */
    private static Map<String,Double> userKindP(Integer userId) throws SQLException {
        //查询出该用户评论过的电影的类型
        List<String> movieKindList = util.selectMovieKindByUserIdBase(userId);
        //该用户评论过的总数
        Integer userTotalMark = movieKindList.size();
        Double userTotalMarkDouble = userTotalMark.doubleValue();
        //遍历用户类型列表,分别计算出各类型被用户评论了多少次
        Map<String,Integer> movieKindMarkCount = new HashMap<>();
        int count;
        for(String kind : movieKind){
            count = 0;
            for(String kinds : movieKindList){
                if(kinds.contains(kind)){
                    count ++;
                }
            }
            movieKindMarkCount.put(kind,count);
        }
        //显示释放movieKindList占用内存
        movieKindList = null;
        //--------根据movieKindMarkCount计算各类型被该用户关注的概率
        Map<String,Double> movieKindP = new HashMap<>();
        Iterator<Map.Entry<String,Integer>> iterator = movieKindMarkCount.entrySet().iterator();
        Map.Entry<String,Integer> tempEntry;
        String tempKind;
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
}
