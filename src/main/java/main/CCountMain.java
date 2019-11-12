package main;

import dao.InfoGetUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.*;

public class CCountMain {
    private static Map<Integer,Integer> movieKindMap;
    private static Map<Integer,List<Integer>> KindMovieMap;
    private static List<Integer> movieKind;
    private static InfoGetUtil util = new InfoGetUtil();
    static {
        //静态获取数据库连接
        //初始化movieKind
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

    public static void main(String[] args) throws IOException, SQLException {
        //利用fileChannel写文件
        RandomAccessFile randomAccessFile;
        FileChannel fileChannel;
        ByteBuffer byteBuffer = ByteBuffer.allocate(100000);
        String tempData;
        int tempFileN;
        Integer tempMovieKind;
        Integer tempC;
        Map.Entry<Integer, List<Integer>> tempEntry;
        Integer tempUserId;
        List<Integer> tempMoviesIdList;
        Map<Integer, List<Integer>> recommentMap;
        Iterator<Map.Entry<Integer, List<Integer>>> iterator;
        for(int i = 1; i < 5;i ++){
            tempFileN = (i -1)*5 + 10;
            randomAccessFile = new RandomAccessFile("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\CCountResultAgainSortTop"+tempFileN+".txt","rw");
            fileChannel = randomAccessFile.getChannel();
            //读取重排序后的推荐列表文件
            recommentMap = readFile(tempFileN);
            iterator = recommentMap.entrySet().iterator();
            while (iterator.hasNext()){
                tempEntry = iterator.next();
                tempUserId = tempEntry.getKey();
                tempMoviesIdList = tempEntry.getValue();
                //拿到该用户对于聚类的C分类情况
                Map<Integer,Integer> splitKindMap = splitKindByUserId(tempUserId);
                List<Integer> C1List = new ArrayList<>();
                List<Integer> C2List = new ArrayList<>();
                List<Integer> C3List = new ArrayList<>();
                for(Integer movieId : tempMoviesIdList){
                    //查询该电影类型
                    tempMovieKind = movieKindMap.get(movieId);
                    //获取该电影的C型号
                    tempC = splitKindMap.get(tempMovieKind);
                    if(tempC == null){
                        C3List.add(movieId);
                    }else {
                        //根据C类别分类
                        switch (tempC){
                            case 1:
                                C1List.add(movieId);
                                break;
                            case 2:
                                C2List.add(movieId);
                                break;
                            case 3:
                                C3List.add(movieId);
                                break;
                        }
                    }
                }
                //打印每个用户的推荐列表C分类情况
                tempData = tempUserId+"---C1("+C1List.size()+"):[";
                //打印C1
                for(Integer mId : C1List){
                    tempData += mId + ",";
                }
                tempData = tempData.substring(0,tempData.length()-1) + "]---C2("+C2List.size()+"):[";
                //打印C2
                for(Integer mId : C2List){
                    tempData += mId + ",";
                }
                tempData = tempData.substring(0,tempData.length()-1) + "]---C3("+C3List.size()+"):[";
                //打印C3
                for(Integer mId : C3List){
                    tempData += mId + ",";
                }
                tempData = tempData.substring(0,tempData.length()-1) + "]\n";
                byteBuffer.clear();
                byteBuffer.put(tempData.getBytes());
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){
                    fileChannel.write(byteBuffer);
                }
            }
            fileChannel.close();
        }
    }

    /**
     * 读取文件拿到最终的推荐列表
     * 形如{u1:[m1,m2,m3],u2:[m1,m2,m3]}
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private static Map<Integer, List<Integer>> readFile(Integer fileN) throws IOException{
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\sort\\resultAgainSortTop"+fileN+".txt")));
        String data;
        Integer tempUserId;
        String tempMovieIds;
        List<String> tempidListStr;
        List<Integer> tempidListInt;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            if(data.contains(":")){
                tempidListInt = new ArrayList<>();
                tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
                tempMovieIds = data.substring(data.indexOf("[")+1,data.indexOf("]")).trim();
                tempidListStr = Arrays.asList(tempMovieIds.split(","));
                for (String str : tempidListStr){
                    tempidListInt.add(Integer.valueOf(str.trim()));
                }
                //显示释放内存
                tempidListStr = null;
                resultMap.put(tempUserId,tempidListInt);
            }
        }
        tempidListInt = null;
        return  resultMap;
    }

    /*
     * 计算该用户对于19个大类的C分类情况
     * C1=1 C2=2 C3=3
     * 形如
     * {K1:1,K2:2,K3:1,......}
     */
    private static Map<Integer,Integer> splitKindByUserId(Integer userId) throws SQLException {
        Map<Integer,Integer> userSplitKindC = new HashMap<>();
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
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempKind = tempEntry.getKey();
            tempCount = tempEntry.getValue();
            movieKindP.put(tempKind,tempCount.doubleValue()/userTotalMarkDouble);
        }
        //显示释放movieKindMarkCount占用内存
        movieKindMarkCount = null;
        //--------利用movieKindP得到该用户对于K个聚类的C分类情况
        //拿到概率list
        Iterator<Map.Entry<Integer,Double>> iteratorP = movieKindP.entrySet().iterator();
        List<Double> Plist = new ArrayList<>();
        Map.Entry<Integer,Double> tempEntryP;
        Double tempP;
        //拿到概率之和，计算平均值，标准差用
        Double sum = 0.0;
        while (iteratorP.hasNext()){
            tempEntryP = iteratorP.next();
            tempP = tempEntryP.getValue();
            sum += tempP;
            Plist.add(tempP);
        }
        int size = Plist.size();
        //--------利用Plist计算
        //1.计算概率平均值
        Double average = sum/size;
        //2.计算标准差
        Double total=0.0;//方差
        for(int i=0;i<size;i++){
            total += (Plist.get(i)-average)*(Plist.get(i)-average);
        }
        //显示释放Plist占用内存
        Plist = null;
        Double standardDeviation = Math.sqrt(total/size);
        //利用每个类的关注概率，平均值和标准差分C类
        //再遍历一遍各类型被该用户关注的概率Map
        Iterator<Map.Entry<Integer,Double>> iteratorPForResult = movieKindP.entrySet().iterator();
        Map.Entry<Integer,Double> tempEntryPForResult;
        Double tempPForResult;
        Integer tempKindName;
        Double tempC;
        //测试集合
        while (iteratorPForResult.hasNext()){
            tempEntryPForResult = iteratorPForResult.next();
            tempPForResult = tempEntryPForResult.getValue();
            tempKindName = tempEntryPForResult.getKey();
            tempC = (tempPForResult - average)/standardDeviation;
            if(tempC > 1){
                //受用户关注类
                userSplitKindC.put(tempKindName,1);
            }else if(tempC <=1 && tempC >= -1) {
                //中间类
                userSplitKindC.put(tempKindName,2);
            }else {
                //不受用户关注类
                userSplitKindC.put(tempKindName,3);
            }
        }
        return userSplitKindC;
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
}
