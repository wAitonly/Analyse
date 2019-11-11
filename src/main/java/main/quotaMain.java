package main;

import Bean.Ratings;
import dao.InfoGetUtil;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 指标计算
 */
public class quotaMain {
    private static InfoGetUtil util = new InfoGetUtil();
    private static Integer N = 0;
    private static Map<Integer,Map<Integer,Integer>> overAllMap;
    static {
        overAllMap = new HashMap<>();
        //构建用户对电影的评分Map
        Map<Integer,Integer> tempMap;
        //拿到用户影评信息
        List<Ratings> ratingsList = null;
        try {
            ratingsList = new InfoGetUtil().getRatings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(Ratings ratings : ratingsList){
            if(!overAllMap.containsKey(ratings.getUserId())){
                //结果集中不包含该用户的键值对
                tempMap = new HashMap<>();
                tempMap.put(ratings.getMovieId(),ratings.getRating());
                overAllMap.put(ratings.getUserId(),tempMap);
            }else {
                overAllMap.get(ratings.getUserId()).put(ratings.getMovieId(),ratings.getRating());
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        String testStr = new InfoGetUtil().selectMovieKindByMovieId(2);
        System.out.println(testStr);
        for(int threshold = 38; threshold < 39; threshold++){
            for(int i = 1; i < 5;i ++){
                if(threshold == 38 && i < 3){
                    continue;
                }
                N = 5 * i;
                //讀取推薦列表
                Map<Integer, List<Integer>> recommentMap = readFile(threshold,N);
                printHit(recommentMap,N,threshold);
                printPrecision(recommentMap,N,threshold);
                printRecall(recommentMap,N,threshold);
                printHD(recommentMap,N,threshold);
                printNovelty(recommentMap,N,threshold);
                printCoverage(recommentMap,N,threshold);
                printILD(recommentMap,N,threshold);
                printAverageHD(N,threshold);
                printCoverageAll(recommentMap,N,threshold);
                recommentMap = null;
            }
        }

    }

    /**
     * 读取文件拿到最终的推荐列表
     * 形如{u1:[m1,m2,m3],u2:[m1,m2,m3]}
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private static Map<Integer, List<Integer>> readFile(Integer threshold,Integer N) throws IOException{
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\resultThirdSortThreshold"+threshold+"Top"+N+".txt")));
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

    private static void printCoverageAll(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws IOException, SQLException {
        Double coverageAll = CoverageAll(recommentMap);
        //输出到文件
        //将结果输出到文件
        StringBuffer strCoverageAll = new StringBuffer();
        strCoverageAll.append("該算法整体覆盖率為："+strCoverageAll).append(coverageAll).append("\n");
        FileWriter fwCoverageAll = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\CoverageAll.txt", true);
        fwCoverageAll.write(strCoverageAll.toString());
        fwCoverageAll.close();
    }

    private static void printHit(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws IOException, SQLException {
        //計算命中個數
        Map<Integer,Integer> Hit = hit(recommentMap);
        Set<Integer> userIdList = Hit.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += Hit.get(userId);
        }
        double percision = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strPrecision = new StringBuffer();
        strPrecision.append("該算法平均命中個數為："+percision).append("\n");
        strPrecision.append("以下是各推薦列表命中個數").append("\n");
        FileWriter fwPrecision = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\Hit.txt", true);
        Set set = Hit.entrySet();
        Iterator iterPrecision = set.iterator();
        while(iterPrecision.hasNext()){
            Map.Entry entry = (Map.Entry)iterPrecision.next();
            strPrecision.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwPrecision.write(strPrecision.toString());
        fwPrecision.close();
        Hit = null;
        userIdList = null;
    }

    private static void printPrecision(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException {
        //計算準確率
        Map<Integer,Double> Precision = Precision(recommentMap);
        Set<Integer> userIdList = Precision.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += Precision.get(userId);
        }
        double percision = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strPrecision = new StringBuffer();
        strPrecision.append("該算法準確性為："+percision).append("\n");
        strPrecision.append("以下是各推薦列表準確率").append("\n");
        FileWriter fwPrecision = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\Precision.txt", true);
        Set set = Precision.entrySet();
        Iterator iterPrecision = set.iterator();
        while(iterPrecision.hasNext()){
            Map.Entry entry = (Map.Entry)iterPrecision.next();
            strPrecision.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwPrecision.write(strPrecision.toString());
        fwPrecision.close();
        Precision = null;
        userIdList = null;
    }

    private static void printRecall(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException{
        //計算召回率
        Map<Integer,Double> Recall = Recall(recommentMap);
        Set<Integer> userIdList = Recall.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += Recall.get(userId);
        }
        double recall = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strRecall = new StringBuffer();
        strRecall.append("該算法召回率為："+recall).append("\n");
        strRecall.append("以下是各推薦列表召回率").append("\n");
        FileWriter fwRecall = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\Recall.txt", true);
        Set setRecall = Recall.entrySet();
        Iterator iterRecall = setRecall.iterator();
        while(iterRecall.hasNext()){
            Map.Entry entry = (Map.Entry)iterRecall.next();
            strRecall.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwRecall.write(strRecall.toString());
        fwRecall.close();
        Recall = null;
        userIdList = null;
    }

    private static void printHD(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException{
        //計算海明距離
        Map<Integer,Map<Integer,Double>> HD = HD(recommentMap);
        Set<Integer> userIdList = HD.keySet();
        Map<Integer,Double> tempMap;
        Iterator<Map.Entry<Integer,Double>> tempIterator;
        double per;
        Map<Integer,Double> averageHD = new HashMap<>();
        for(Integer userId : userIdList){
            per = 0.0;
            tempMap = HD.get(userId);
            tempIterator = tempMap.entrySet().iterator();
            while (tempIterator.hasNext()){
                per += tempIterator.next().getValue();
            }
            averageHD.put(userId,per/tempMap.size());
        }
        //输出到文件
        //将结果输出到文件
        StringBuffer strHD = new StringBuffer();
        FileWriter fwHD = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\HD.txt", true);
        Set set = HD.entrySet();
        Iterator iterHD = set.iterator();
        while(iterHD.hasNext()){
            Map.Entry entry = (Map.Entry)iterHD.next();
            strHD.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwHD.write(strHD.toString());
        fwHD.close();
        HD = null;
        userIdList = null;
    }

    private static void printNovelty(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException{
        //計算新新型
        Map<Integer,Double> Novelty = Novelty(recommentMap);
        Set<Integer> userIdList = Novelty.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += Novelty.get(userId);
        }
        double novelty = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strNovelty = new StringBuffer();
        strNovelty.append("該算法平均新新型為："+novelty).append("\n");
        strNovelty.append("以下是各推薦列表新新型").append("\n");
        FileWriter fwNovelty = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\Novelty.txt", true);
        Set setNovelty = Novelty.entrySet();
        Iterator iterNovelty = setNovelty.iterator();
        while(iterNovelty.hasNext()){
            Map.Entry entry = (Map.Entry)iterNovelty.next();
            strNovelty.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwNovelty.write(strNovelty.toString());
        fwNovelty.close();
        Novelty = null;
        userIdList = null;
    }

    private static void printCoverage(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException{
        //計算覆蓋率
        Map<Integer,Double> Coverage = Coverage(recommentMap);
        Set<Integer> userIdList = Coverage.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += Coverage.get(userId);
        }
        double coverage = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strCoverage = new StringBuffer();
        strCoverage.append("該算法平均覆蓋率為："+coverage).append("\n");
        strCoverage.append("以下是各推薦列表覆蓋率").append("\n");
        FileWriter fwCoverage = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\Coverage.txt", true);
        Set setCoverage = Coverage.entrySet();
        Iterator iterCoverage = setCoverage.iterator();
        while(iterCoverage.hasNext()){
            Map.Entry entry = (Map.Entry)iterCoverage.next();
            strCoverage.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwCoverage.write(strCoverage.toString());
        fwCoverage.close();
        Coverage = null;
        userIdList = null;
    }

    private static void printILD(Map<Integer, List<Integer>> recommentMap,Integer N,Integer threshold) throws SQLException, IOException{
        //計算ILD
        Map<Integer,Double> ILD = countILD(recommentMap);
        Set<Integer> userIdList = ILD.keySet();
        double per = 0.0;
        for(Integer userId : userIdList){
            per += ILD.get(userId);
        }
        double coverage = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strILD = new StringBuffer();
        strILD.append("該算法平均ILD為："+coverage).append("\n");
        strILD.append("以下是各推薦列表ILD").append("\n");
        FileWriter fwILD = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\ILD.txt", true);
        Set setILD = ILD.entrySet();
        Iterator iterILD = setILD.iterator();
        while(iterILD.hasNext()){
            Map.Entry entry = (Map.Entry)iterILD.next();
            strILD.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwILD.write(strILD.toString());
        fwILD.close();
        ILD = null;
        userIdList = null;
    }

    private static void printAverageHD(Integer N,Integer threshold) throws IOException{
        //計算平均海明距離
        Map<Integer,Double> averageHD = averageHD(N,threshold);
        Set<Integer> userIdList = averageHD.keySet();
        double per = 0.00;
        for(Integer userId : userIdList){
            per += averageHD.get(userId);
        }
        double hd = per/userIdList.size();
        //输出到文件
        //将结果输出到文件
        StringBuffer strHd = new StringBuffer();
        strHd.append("該算法平均海明距離為："+hd).append("\n");
        strHd.append("以下是各推薦列表海明距離").append("\n");
        FileWriter fwHd = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\AverageHD.txt", true);
        Set set = averageHD.entrySet();
        Iterator iterHd = set.iterator();
        while(iterHd.hasNext()){
            Map.Entry entry = (Map.Entry)iterHd.next();
            strHd.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fwHd.write(strHd.toString());
        fwHd.close();
        averageHD = null;
        userIdList = null;
    }


    /**
     * 计算各用户推荐的准确率
     * 将系统中所有用户的准确率求平均得到系统整体的推荐准确率
     * 即用户喜欢的物品占所推荐物品的比例,用户看过即喜欢
     * @param recommentList
     * @return
     */
    private static Map<Integer,Double> Precision(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Double> PrecisionMap = new HashMap<>();
        //计算各用户推荐的准确率
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Integer tempUserId;
        List<Integer> tempRecommentList;
        List<Integer> tempLikeMoviesList;
        Integer tempLikeCount;
        Map.Entry<Integer, List<Integer>> tempEntry;
        BigDecimal bigDecimal;
        double tempPrecision;
        while (iterator.hasNext()){
            tempLikeCount = 0;
            tempEntry = iterator.next();
            tempUserId = tempEntry.getKey();
            tempRecommentList = tempEntry.getValue();
            //查询该用户看过哪些电影
            tempLikeMoviesList = util.selectMoviesByUserId(tempUserId);
            if(tempLikeMoviesList.size() <= 0){
                continue;
            }
            for(Integer movieId : tempRecommentList){
                if(tempLikeMoviesList.contains(movieId)){
                    tempLikeCount ++ ;
                }
            }
            //顯示釋放內存
            tempLikeMoviesList = null;
            tempPrecision = tempLikeCount/(tempRecommentList.size()*1.000);
            bigDecimal = new BigDecimal(tempPrecision);
            PrecisionMap.put(tempUserId,bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() );
            tempRecommentList = null;
        }
        return PrecisionMap;
    }

    /**
     * 计算各用户推荐的召回率
     * @param recommentList
     * @return
     */
    private static Map<Integer,Double> Recall(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Double> RecallMap = new HashMap<>();
        //计算各用户推荐的召回率
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Integer tempUserId;
        List<Integer> tempRecommentList;
        List<Integer> tempLikeMoviesList;
        Integer tempLikeCount;
        Map.Entry<Integer, List<Integer>> tempEntry;
        BigDecimal bigDecimal;
        double tempRecall;
        while (iterator.hasNext()){
            tempLikeCount = 0;
            tempEntry = iterator.next();
            tempUserId = tempEntry.getKey();
            tempRecommentList = tempEntry.getValue();
            //查询该用户看过哪些电影
            tempLikeMoviesList = util.selectMoviesByUserId(tempUserId);
            if(tempLikeMoviesList.size() <= 0){
                continue;
            }
            for(Integer movieId : tempRecommentList){
                if(tempLikeMoviesList.contains(movieId)){
                    tempLikeCount ++ ;
                }
            }
            //顯示釋放內存
            tempRecommentList = null;
            tempRecall = tempLikeCount/(tempLikeMoviesList.size()*1.000);
            bigDecimal = new BigDecimal(tempRecall);
            RecallMap.put(tempUserId,bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() );
            tempLikeMoviesList = null;
        }
        return RecallMap;
    }

    /**
     * 计算各用户推荐的命中個數
     * @param recommentList
     * @return
     */
    private static Map<Integer,Integer> hit(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Integer> HitMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Integer tempUserId;
        List<Integer> tempRecommentList;
        List<Integer> tempLikeMoviesList;
        Integer tempLikeCount;
        Map.Entry<Integer, List<Integer>> tempEntry;
        while (iterator.hasNext()){
            tempLikeCount = 0;
            tempEntry = iterator.next();
            tempUserId = tempEntry.getKey();
            tempRecommentList = tempEntry.getValue();
            //查询该用户看过哪些电影
            tempLikeMoviesList = util.selectMoviesByUserId(tempUserId);
            if(tempLikeMoviesList.size() <= 0){
                continue;
            }
            for(Integer movieId : tempRecommentList){
                if(tempLikeMoviesList.contains(movieId)){
                    tempLikeCount ++ ;
                }
            }
            //顯示釋放內存
            tempRecommentList = null;
            tempLikeMoviesList = null;
            HitMap.put(tempUserId,tempLikeCount);
        }
        return HitMap;
    }

    /**
     * 计算各用户之間的海明距離HD
     * @param recommentList
     * @return
     */
    private static Map<Integer,Map<Integer,Double>> HD(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Map<Integer,Double>> resultMap = new HashMap<>();
        Set<Integer> userIdList = recommentList.keySet();
        List<Integer> temprecommentA;
        List<Integer> temprecommentB;
        Integer tempUnionNum;
        Double tempHD;
        Map<Integer,Double> tempMap;
        BigDecimal bigDecimal;
        for(Integer userIDA : userIdList){
            temprecommentA = recommentList.get(userIDA);
            tempMap = new HashMap<>();
            for(Integer userIDB : userIdList){
                if(userIDA == userIDB){
                    continue;
                }
                temprecommentB = recommentList.get(userIDB);
                //計算這兩個推薦列表交集個數
                tempUnionNum = unionCount(temprecommentA,temprecommentB);
                //顯示釋放內存
                temprecommentB = null;
                //計算遮兩個用戶之間的海明距離
                tempHD = 1-(tempUnionNum/(N*1.0));
                bigDecimal = new BigDecimal(tempHD);
                tempMap.put(userIDB,bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            temprecommentA = null;
            resultMap.put(userIDA,tempMap);
        }
        return resultMap;
    }


    /**
     * 計算兩個list的交集個數
     * @param listA
     * @param listB
     * @return
     */
    private static Integer unionCount(List<Integer> listA,List<Integer> listB){
        List result = new ArrayList();
        for (Integer num : listA) {//遍历list1
            if (listB.contains(num)) {//如果存在这个数
                result.add(num);//放进一个list里面，这个list就是交集
            }
        }
        return result.size();
    }

    /**
     * 計算推薦給各用戶的新新型
     * @param recommentList
     * @return
     */
    private static Map<Integer,Double> Novelty(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Double> resultMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Integer tempUserId;
        double tempMoviePop;
        List<Integer> tempRecommentList;
        Map.Entry<Integer, List<Integer>> tempEntry;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempUserId = tempEntry.getKey();
            tempRecommentList = tempEntry.getValue();
            //遍歷推薦列表，拿到ln(1+Pop(i))之和
            tempMoviePop = 0;
            for(Integer movieId : tempRecommentList){
                tempMoviePop += Math.log(util.selectRageByMovieId(movieId)+1);
            }
            tempRecommentList = null;
            resultMap.put(tempUserId,tempMoviePop/N);
        }
        return resultMap;
    }

    /**
     * 計算海明距離平均值
     *
     */
    private static Map<Integer,Double> averageHD(Integer N,Integer threshold) throws IOException {
        Map<Integer,Double> resultMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\newsort\\Threshold"+threshold+"Top"+N+"\\HD.txt")));
        String data;
        String tempUserId;
        String tempHD;
        List<String> tempStrList;
        Double tempDouble;
        while((data = br.readLine())!=null){
            tempDouble = 0.00;
            data.trim().replace(" ","");
            tempUserId = data.substring(0,data.indexOf(":"));
            tempHD = data.substring(data.indexOf("{")+1,data.lastIndexOf("}"));
            tempStrList = Arrays.asList(tempHD.split(","));
            for(String str : tempStrList){
                tempDouble += Double.valueOf(str.substring(str.indexOf("=")+1));
            }
            tempDouble = tempDouble/tempStrList.size();
            resultMap.put(Integer.valueOf(tempUserId.trim()),tempDouble);
        }
        return  resultMap;
    }

    /**
     * 癥對用戶的覆蓋率
     * @param recommentList
     * @return
     */
    private static Map<Integer,Double> Coverage(Map<Integer, List<Integer>> recommentList){
        Map<Integer,Double> resultMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> tempEntry;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            resultMap.put(tempEntry.getKey(),tempEntry.getValue().size()/(3883*1.000));
        }
        return resultMap;
    }

    /**
     * 整體的覆蓋率
     * @param recommentList
     * @return
     */
    private static Double CoverageAll(Map<Integer, List<Integer>> recommentList){
        double result;
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> tempEntry;
        List<Integer> tempList;
        List<Integer> movieIdList = new ArrayList<>();
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempList = tempEntry.getValue();
            for(Integer integer : tempList){
                if(!movieIdList.contains(integer)){
                    movieIdList.add(integer);
                }
            }
        }
        return movieIdList.size()/(3883*1.00);
    }

    /**
     * 计算两个电影的相似度
     * 例子：
     * {A:{B:[m1,m2]}
     * r_m1_A表示A对m1的评分
     * @return
     */
    public static Double countSimBySameUser(Integer mA,Integer mB) throws SQLException {
        //找出同时对这两步电影评过分的用户集合
        List<Integer> AUser = new InfoGetUtil().selectUsersByMovieId(mA);
        List<Integer> BUser = new InfoGetUtil().selectUsersByMovieId(mB);
        double EuA = 0.0;//所有用户对电影A评分的2次方之和
        double EuB = 0.0;//所有用户对电影B评分的2次方之和
        Integer tempRmA = 0;
        Integer tempRmB = 0;
        for(Integer userId : AUser){
            tempRmA = overAllMap.get(userId).get(mA);
            EuA += Math.pow(tempRmA,2);
        }
        for(Integer userId : BUser){
            tempRmB = overAllMap.get(userId).get(mB);
            EuB += Math.pow(tempRmB,2);
        }
        //取两个集合的交集
        AUser.retainAll(BUser);
        BUser = null;
        Integer rmA = 0;//当前用户对电影A的评分
        Integer rmB = 0;//当前用户对电影B的评分
        double EuAB = 0.0;//所有用户对电影AB评分的乘积之和
        for(Integer userId : AUser){
            rmA = overAllMap.get(userId).get(mA);
            rmB = overAllMap.get(userId).get(mB);
            EuAB += rmA*rmB;
        }
        AUser = null;
        double numerator = EuAB;
        double denominator = Math.sqrt(EuA*EuB);
        // 分母不能为0
        if (denominator == 0) {
            return 0.0;
        }
        return numerator / denominator;
    }

    /**
     * 计算ILD
     * @param recommentList
     * @return
     */
    public static Map<Integer,Double> countILD(Map<Integer, List<Integer>> recommentList) throws SQLException {
        Map<Integer,Double> resultMap = new HashMap<>();
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentList.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> tempEntry;
        List<Integer> tempList;
        Integer tempSize;
        double tempILD;
        double simSum;
        while (iterator.hasNext()){
            simSum = 0.0;
            tempEntry = iterator.next();
            tempList = tempEntry.getValue();
            tempSize = tempList.size();
            for(int i = 0;i < tempSize;i ++){
                for(int j = 0;j < tempSize;j ++){
                    if(i > j){
                        simSum += countSimBySameUser(tempList.get(i),tempList.get(j));
                    }
                }
            }
            tempList = null;
            if(tempSize-1 > 0){
                tempILD = 1 - (2*simSum)/(tempSize*(tempSize-1));
                resultMap.put(tempEntry.getKey(),tempILD);
            }
        }
        return resultMap;
    }
}
