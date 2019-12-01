package baseInfo;

import Bean.Ratings;
import dao.InfoGetUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预处理之后的数据集基本信息
 */
public class DataBaseInfoMain {
    private static InfoGetUtil util = new InfoGetUtil();

    private static Map<Integer,List<Integer>> userMovieMap;
    private static Map<Integer,List<Integer>> movieUserMap;
    private static List<Integer> userList;
    private static List<Integer> movieList;

    static {
        try {
            //查询所有用户
            userList = util.selectAllUser();
            movieList = util.getMovies().stream().map(a -> a.getMovieId()).collect(Collectors.toList());
            userMovieMap = new HashMap<>();
            movieUserMap = new HashMap<>();
            int i = 1;
            for(Integer userId : userList){
                System.out.println("正在初始化userMovieMap，共"+userList.size()+"个，已完成"+(i++));
                userMovieMap.put(userId,util.selectMoviesByUserIdAll(userId));
            }
            for(Integer movieId : movieList){
                System.out.println("movieUserMap，共"+movieList.size()+"个，已完成"+(i++));
                movieUserMap.put(movieId,util.selectUserByMovieIdAll(movieId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
//        commontMovieInUser();
//        commontUserInMovie();
        System.out.println("每部电影的平均用户数"+averageUserCount());
        System.out.println("每用户的平均电影数"+averageMovieCount());
    }

    /**
     * 评分总数
     * @return
     */
    public static Integer ratingCount() throws SQLException {
        List<Ratings> ratings = util.getBaseRatings();
        return ratings.size();
    }

    /**
     * 任意两用户间共同评分的电影数均值
     * @return
     */
    public static void commontMovieInUser() throws SQLException, IOException {
        //利用fileChannel写文件
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\OldRecommentAlgorithmWithoutAverage\\datainfo\\commontMovieInUser.txt","rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(100000);
        String tempData;
        Map<Integer, Map<Integer,Integer>> commontMap = new HashMap<>();
        //查询所有用户
        int size = userList.size();
        List<Integer> tempResultList;
        List<Integer> tempListA;
        List<Integer> tempListB;
        Map<Integer,Integer> tempMap;
        Integer count;
        Integer sumCount = 0;
        Integer userCount = 0;
        Integer tempUserIdA;
        Integer tempUserIdB;
        for(int i = 0; i < size;i ++){
            tempUserIdA = userList.get(i);
            tempData = tempUserIdA+":[";
            System.out.println("正在计算用户"+tempUserIdA+"与其他用户的共同电影个数，共"+(size-i-1)+"，已计算");
            tempMap = new HashMap<>();
            for(int j = i + 1;j < size;j ++){
                 tempUserIdB = userList.get(j);
                 //分别查询出两个用户看过的电影集合
                 tempListA = userMovieMap.get(tempUserIdA);
                 tempListB = userMovieMap.get(tempUserIdB);
                 //计算相同个数
                tempResultList = new ArrayList<>();
                tempResultList.addAll(tempListA);
                tempResultList.retainAll(tempListB);
                count = tempResultList.size();
                tempData += tempUserIdB+":"+count+",";
                sumCount += count;
                userCount ++;
                tempMap.put(userList.get(j),count);
                System.out.print("\r");
                System.out.print(j-i);
                tempResultList = null;
                tempListA = null;
                tempListB = null;
            }
            commontMap.put(userList.get(i),tempMap);
            tempData = tempData.substring(0,tempData.length()-1) + "]\n";
            byteBuffer.clear();
            byteBuffer.put(tempData.getBytes());
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                fileChannel.write(byteBuffer);
            }
            System.out.println("------完成构建用户"+tempUserIdA+"的共同电影个数，共"+size+"，已构建"+i);
        }
        tempData = "平均值："+sumCount/userCount+";sumCount="+sumCount+";userCount="+userCount;
        byteBuffer.clear();
        byteBuffer.put(tempData.getBytes());
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()){
            fileChannel.write(byteBuffer);
        }
        //计算平均值
        fileChannel.close();
    }

    /**
     * 任意两电影间共同评分的用户数均值
     * @return
     */
    public static void commontUserInMovie() throws SQLException, IOException {
        //利用fileChannel写文件
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\OldRecommentAlgorithmWithoutAverage\\datainfo\\commontUserInMovie.txt","rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(100000);
        String tempData;
        Map<Integer, Map<Integer,Integer>> commontMap = new HashMap<>();
        //查询所有电影
        int size = movieList.size();
        List<Integer> tempResultList;
        List<Integer> tempListA;
        List<Integer> tempListB;
        Map<Integer,Integer> tempMap;
        Integer count;
        Integer sumCount = 0;
        Integer movieCount = 0;
        Integer tempMovieIdA;
        Integer tempMovieIdB;
        for(int i = 0; i < size;i ++){
            tempMovieIdA = movieList.get(i);
            tempData = tempMovieIdA+":[";
            System.out.println("正在计算电影"+tempMovieIdA+"与其他电影的共同用户个数，共"+(size-i-1)+"，已计算");
            tempMap = new HashMap<>();
            for(int j = i + 1;j < size;j ++){
                tempMovieIdB = movieList.get(j);
                //分别查询出两个电影的看过用户集合
                tempListA = movieUserMap.get(tempMovieIdA);
                tempListB = movieUserMap.get(tempMovieIdB);
                //计算相同个数
                tempResultList = new ArrayList<>();
                tempResultList.addAll(tempListA);
                tempResultList.retainAll(tempListB);
                count = tempResultList.size();
                tempData += tempMovieIdB+":"+count+",";
                sumCount += count;
                movieCount ++;
                tempMap.put(movieList.get(j),count);
                System.out.print("\r");
                System.out.print(j-i);
                tempResultList = null;
                tempListA = null;
                tempListB = null;
            }
            commontMap.put(movieList.get(i),tempMap);
            tempData = tempData.substring(0,tempData.length()-1) + "]\n";
            byteBuffer.clear();
            byteBuffer.put(tempData.getBytes());
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                fileChannel.write(byteBuffer);
            }
            System.out.println("------完成构建电影"+tempMovieIdA+"的共同电影个数，共"+size+"，已构建"+i);
        }
        tempData = "平均值："+sumCount/movieCount+";sumCount="+sumCount+";movieCount="+movieCount;
        byteBuffer.clear();
        byteBuffer.put(tempData.getBytes());
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()){
            fileChannel.write(byteBuffer);
        }
        //计算平均值
        fileChannel.close();
    }

    /**
     * 每部电影的平均用户数
     */
    private static Integer averageUserCount(){
        //拿到每部电影的评论用户数map
        Iterator<Map.Entry<Integer,List<Integer>>> iterator = movieUserMap.entrySet().iterator();
        Map.Entry<Integer,List<Integer>> tempEntry;
        int sum = 0;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            sum += tempEntry.getValue().size();
        }
        System.out.println("电影数量："+movieUserMap.size()+";sum="+sum);
        return sum/movieUserMap.size();
    }

    /**
     * 每用户的平均电影数
     */
    private static Integer averageMovieCount(){
        //拿到每部电影的评论用户数map
        Iterator<Map.Entry<Integer,List<Integer>>> iterator = userMovieMap.entrySet().iterator();
        Map.Entry<Integer,List<Integer>> tempEntry;
        int sum = 0;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            sum += tempEntry.getValue().size();
        }
        System.out.println("用户数量："+userMovieMap.size()+";sum="+sum);
        return sum/userMovieMap.size();
    }
}
