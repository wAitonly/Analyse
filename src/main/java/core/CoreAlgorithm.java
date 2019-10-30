package core;


import Bean.Ratings;
import dao.InfoGetUtil;

import java.sql.SQLException;
import java.util.*;

/**
 * 核心算法
 * zhaobenquan
 */
public class CoreAlgorithm {

    private static Map<Integer,Map<Integer,Integer>> overAllMap;

    static {
        try {
            overAllMap = buildUserMovieRatingTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建用户对电影的评分map
     * 形如{u:{m1:rat,m2:rat,...},...}
     * @return
     */
    public static Map<Integer,Map<Integer,Integer>> buildUserMovieRatingTable() throws SQLException {
        Map<Integer,Map<Integer,Integer>> resultMap = new HashMap<>();
        Map<Integer,Integer> tempMap;
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getBaseRatings();
        for(Ratings ratings : ratingsList){
            if(!resultMap.containsKey(ratings.getUserId())){
                //结果集中不包含该用户的键值对
                tempMap = new HashMap<>();
                tempMap.put(ratings.getMovieId(),ratings.getRating());
                resultMap.put(ratings.getUserId(),tempMap);
            }else {
                resultMap.get(ratings.getUserId()).put(ratings.getMovieId(),ratings.getRating());
            }
        }
        return resultMap;
    }

    /**
     * 求用户所有评分的平均评分
     * @param userId
     * @return
     */
    public double averageRating(Integer userId) throws SQLException {
        double averageScore = 0;
        int size = 0;
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getBaseRatings();
        for(Ratings ratings : ratingsList){
            if(ratings.getUserId() == userId){
                averageScore += ratings.getRating();
                size ++;
            }
        }
        return  (averageScore * 1.0) / size;
    }

    /**
     * 建立电影和用户的倒排表，即一部电影被哪些用户看过
     * 形如
     * m1:u1,u2,u3
     * m2:u1,u2
     * m3:u2,u3
     * @return
     */
    public Map<Integer, Set<Integer>> buildMovieUserTable() throws SQLException {
        Map<Integer, Set<Integer>> movieUserTable = new HashMap<>();
        Set<Integer> userSet;
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getBaseRatings();
        for(Ratings ratings : ratingsList){
            if(!movieUserTable.containsKey(ratings.getMovieId())){
                //倒排表中没有该电影
                userSet = new HashSet<>();
                userSet.add(ratings.getUserId());
                movieUserTable.put(ratings.getMovieId(),userSet);
            }else {
                movieUserTable.get(ratings.getMovieId()).add(ratings.getUserId());
            }
        }
        return movieUserTable;
    }

    /**
     * 建立用户和电影的倒排表，即一位用户看过哪些电影
     * 形如
     * u1:m1,m2,m3
     * u2:m1,m2
     * u3:m2,m3
     * @return
     */
    public Map<Integer, Set<Integer>> buildUserMovieTable() throws SQLException {
        Map<Integer, Set<Integer>> userMovieTable = new HashMap<>();
        Set<Integer> movieSet;
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getBaseRatings();
        for(Ratings ratings : ratingsList){
            if(!userMovieTable.containsKey(ratings.getUserId())){
                //倒排表中没有该用户
                movieSet = new HashSet<>();
                movieSet.add(ratings.getMovieId());
                userMovieTable.put(ratings.getUserId(),movieSet);
            }else {
                userMovieTable.get(ratings.getUserId()).add(ratings.getMovieId());
            }
        }
        return userMovieTable;
    }


    /**
     * 建立用户间共同看过的电影的集合
     * 形如{u:{n1:[movie1,movie2],n2:[movie2,movie4]},...}
     * @return
     */
    public Map<Integer,Map<Integer,Set<Integer>>> buildUserUserMovieTable() throws SQLException {
        Map<Integer,Map<Integer,Set<Integer>>> resultMap = new HashMap<>();
        Map.Entry<Integer, Set<Integer>> tempOutEntryMap;Set<Integer> tempOutSet;
        Map.Entry<Integer, Set<Integer>> tempInnEntryMap;Set<Integer> tempInnSet;
        Map<Integer,Set<Integer>> tempMap;
        Set<Integer> sameMovieTempSet;
        //拿到用户和电影的倒排表
        Map<Integer, Set<Integer>> userMovieTable = buildUserMovieTable();
        Iterator<Map.Entry<Integer, Set<Integer>>> iteratorUOut = userMovieTable.entrySet().iterator();
        Iterator<Map.Entry<Integer, Set<Integer>>> iteratorUInn;
        while (iteratorUOut.hasNext()){
            //单条信息为一位用户看过哪些电影
            tempOutEntryMap = iteratorUOut.next();
            tempOutSet = tempOutEntryMap.getValue();
            //初始化内层map
            tempMap = new HashMap<>();
            iteratorUInn = userMovieTable.entrySet().iterator();
            while (iteratorUInn.hasNext()){
                //单条信息为一位用户看过哪些电影
                tempInnEntryMap = iteratorUInn.next();
                tempInnSet = tempInnEntryMap.getValue();
                if(tempInnEntryMap.getKey() == tempOutEntryMap.getKey()){
                    //同一用户不统计
                    continue;
                }
                //计算两个用户看过电影的交集
                sameMovieTempSet = new HashSet<>();
                sameMovieTempSet.addAll(tempOutSet);
                sameMovieTempSet.retainAll(tempInnSet);
                //构建内层map
                tempMap.put(tempInnEntryMap.getKey(),sameMovieTempSet);
            }
            resultMap.put(tempOutEntryMap.getKey(),tempMap);
        }
        return resultMap;
    }

    /**
     * 构建用户之间相似度的集合(皮尔逊相关系数)
     * 形如：{u:{n1:sim1,n2:sim2,...},...}
     * @return
     */
    public Map<Integer,Map<Integer,Double>> buildUserUserSimTable() throws SQLException {
        Map<Integer,Map<Integer,Double>> resultMap = new HashMap<>();
        Map<Integer,Double> tempMap;
        Double tempSim;
        //拿到用户间共同看过的电影的集合
        Map<Integer,Map<Integer,Set<Integer>>> userSameMovieMap = buildUserUserMovieTable();
        //使用迭代器遍历
        Iterator<Map.Entry<Integer,Map<Integer,Set<Integer>>>> iteratorOut = userSameMovieMap.entrySet().iterator();
        Map<Integer,Set<Integer>> tempMapInn;
        Map.Entry<Integer,Map<Integer,Set<Integer>>> tempEntryOut;
        Iterator<Map.Entry<Integer,Set<Integer>>> iteratorInn;
        Map.Entry<Integer,Set<Integer>> tempEntryInn;
        //遍历外层
        while (iteratorOut.hasNext()){
            tempEntryOut = iteratorOut.next();
            tempMapInn = tempEntryOut.getValue();
            iteratorInn = tempMapInn.entrySet().iterator();
            tempMap = new HashMap<>();
            //遍历内层
            while (iteratorInn.hasNext()){
                tempEntryInn = iteratorInn.next();
                tempSim = countSimBySameMovie(tempEntryOut.getKey(),tempEntryInn.getKey(),tempEntryInn.getValue());
                //构建内层map
                tempMap.put(tempEntryInn.getKey(),tempSim);
            }
            //构建外层map
            resultMap.put(tempEntryOut.getKey(),tempMap);
        }
        return resultMap;
    }

    /**
     * 计算两个用户的相似度
     * @param uA
     * @param uB
     * @param sameMovies
     * 例子：
     * {A:{B:[m1,m2]}
     * r_m1_A表示A对m1的评分
     * @return
     */
    public Double countSimBySameMovie(Integer uA,Integer uB,Set<Integer> sameMovies){
        Integer rmA = 0;//A对电影的评分
        Integer rmB = 0;//B对电影的评分
        Integer N = sameMovies.size();//都看过的电影数量

        double EuA = 0.0;//用户A对共同看过的电影所有评分之和,即 r_m1_A+r_m2_A
        double EuB = 0.0;//用户B对共同看过的电影所有评分之和,即 r_m1_B+r_m2_B
        double EuA_sq = 0.0;//（用户A对共同看过的电影各个评分）的二次方之和,即 (r_m1_A)²+(r_m2_A)²
        double EuB_sq = 0.0;//（用户B对共同看过的电影各个评分）的二次方之和,即 (r_m1_B)²+(r_m2_B)²
        double EuAuB = 0.0;//（用户A对各电影评分*用户B对对应电影评分）之和,即 r_m1_A*r_m1_B + r_m2_A*r_m2_B
        for(Integer movieId : sameMovies){
            rmA = overAllMap.get(uA).get(movieId);
            rmB = overAllMap.get(uB).get(movieId);
            EuA += rmA;
            EuB += rmB;
            EuA_sq += Math.pow(rmA,2);
            EuB_sq += Math.pow(rmB,2);
            EuAuB += rmA * rmB;
        }
        double numerator = EuAuB - EuA * EuB / N;
        double denominator = Math.sqrt((EuA_sq - EuA * EuA / N) * (EuB_sq - EuB * EuB / N));
        // 分母不能为0
        if (denominator == 0) {
            return 0.0;
        }
        return numerator / denominator;
    }

    /**
     * 找出最相似用户
     * @return
     * @throws SQLException
     */
    public Map<Integer,Integer> buildsimUserList() throws SQLException {
        Map<Integer,Integer> resultMap = new HashMap<>();
        Double tempDouble;//相似度
        //拿到用户之间相似度的集合
        Map<Integer,Map<Integer,Double>> userSimMap = buildUserUserSimTable();
        //遍历
        Iterator<Map.Entry<Integer,Map<Integer,Double>>> iteratorOut = userSimMap.entrySet().iterator();
        Map.Entry<Integer,Map<Integer,Double>> tempEntryOut;
        Map<Integer,Double> tempMapInn;
        Iterator<Map.Entry<Integer,Double>> iteratorInn;
        Map.Entry<Integer,Double> tempEntryInn;
        //外层遍历
        while (iteratorOut.hasNext()){
            tempEntryOut = iteratorOut.next();
            tempMapInn = tempEntryOut.getValue();
            iteratorInn = tempMapInn.entrySet().iterator();
            //内层遍历寻找最相似的用户
            tempDouble = 0.0;
            while (iteratorInn.hasNext()){
                tempEntryInn = iteratorInn.next();
                if(tempEntryInn.getValue() > tempDouble){
                    resultMap.put(tempEntryOut.getKey(),tempEntryInn.getKey());
                }
            }
        }
        return resultMap;
    }

    /**
     * 构建推荐map
     * 默认相似用户喜欢的都是该用户喜欢的，只推荐该用户没评论过的，即没看过的
     * @return
     */
    public Map<Integer,List<Integer>> buildRecommendList() throws SQLException {
        Map<Integer,List<Integer>> resultMap = new HashMap<>();
        List<Integer> tempList;
        List<Integer> tempListSimUser;
        List<Integer> tempListUser;
        //拿到相似用户map
        Map<Integer,Integer> simUser = buildsimUserList();
        Iterator<Map.Entry<Integer,Integer>> iterator = simUser.entrySet().iterator();
        Map.Entry<Integer,Integer> tempEntry;
        while(iterator.hasNext()){
            tempList = new ArrayList<>();
            tempEntry = iterator.next();
            //查询该相似用户看过的电影集合
            tempListSimUser = new InfoGetUtil().selectMoviesByUserId(tempEntry.getValue());
            //查询该用户看过的电影集合
            tempListUser = new InfoGetUtil().selectMoviesByUserId(tempEntry.getKey());
            //遍历删选
            for(Integer tempInt : tempListSimUser){
                if(!tempListUser.contains(tempInt)){
                    //不包含即没看过
                    tempList.add(tempInt);
                }
            }
            resultMap.put(tempEntry.getKey(),tempList);
        }
        return resultMap;
    }






}
