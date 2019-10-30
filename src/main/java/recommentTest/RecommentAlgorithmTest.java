package recommentTest;


import java.io.FileWriter;
import java.io.IOException;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.*;

/**
 * 核心算法
 * zhaobenquan
 */
public class RecommentAlgorithmTest {

    private static Map<Integer,Map<Integer,Integer>> overAllMap;
    private static Map<Integer,Map<String,Double>> kindOverAllMap;
    private static Comparator<Map.Entry<Integer,Double>> valueComparator;
    private static Integer simNumber = 2;
    private static  Map<Integer, List<Integer>> userMovieTable;
    private static List<String> movieKind;
    //拿到用户影评信息
    private static List<Ratings> ratingsList;
    private static List<Movies> moviesList;
    //所有参评用户
    private static List<Integer> userList;
    //所有电影
    private static Movies movie1;
    private static Movies movie2;
    private static Movies movie3;
    private static Movies movie4;
    private static Movies movie5;
    static {
        //评分list
        ratingsList = new ArrayList<>();
        Ratings r11 = new Ratings(1,1,3);
        Ratings r12 = new Ratings(1,2,2);
        Ratings r13 = new Ratings(1,3,3);
        Ratings r14 = new Ratings(1,4,3);

        Ratings r21 = new Ratings(2,1,2);
        Ratings r22 = new Ratings(2,2,3);
        Ratings r23 = new Ratings(2,3,4);
        Ratings r24 = new Ratings(2,4,2);

        Ratings r31 = new Ratings(3,1,3);
        Ratings r32 = new Ratings(3,2,1);
        Ratings r33 = new Ratings(3,3,2);
        Ratings r34 = new Ratings(3,4,3);

        ratingsList.add(r11);ratingsList.add(r12);
        ratingsList.add(r13);ratingsList.add(r14);

        ratingsList.add(r21);ratingsList.add(r22);
        ratingsList.add(r23);ratingsList.add(r24);

        ratingsList.add(r31);ratingsList.add(r32);
        ratingsList.add(r33);ratingsList.add(r34);
        //评分map
        overAllMap = buildUserMovieRatingTable();
        //排序规则
        valueComparator = new Comparator<Map.Entry<Integer,Double>>() {
            @Override
            public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        };
        //初始化movieKind
        movieKind = new ArrayList<>();
        movieKind.add("Adventure");
        movieKind.add("Animation");
        movieKind.add("Crime");
        movieKind.add("Drama");
        movieKind.add("Horror");
        movieKind.add("Musical");
        movieKind.add("Romance");
        movieKind.add("Thriller");
        movieKind.add("War");

        //所有电影
        moviesList = new ArrayList<>();
        movie1 = new Movies(1,"Thriller|War|Drama");
        movie2 = new Movies(2,"Romance|War|Musical");
        movie3 = new Movies(3,"Horror|Crime|Thriller|Romance");
        movie4 = new Movies(4,"Animation|Crime");
        movie5 = new Movies(5,"Adventure|War|Crime|Thriller");
        moviesList.add(movie1);
        moviesList.add(movie2);
        moviesList.add(movie3);
        moviesList.add(movie4);
        moviesList.add(movie5);

        //所有参评用户
        userList = new ArrayList<>();
        userList.add(1);
        userList.add(2);
        userList.add(3);

        //拿到用户和电影的倒排表
        userMovieTable = buildUserMovieTable();
        //类型map
        kindOverAllMap = buildUserKindRatingTable();
    }

    /**
     * 查询该用户评论过的电影类型
     */
    public List<String> selectMovieKindByUserId(Integer userA){
        List<String> MovieKindByUserId = new ArrayList<>();
        //该用户评论过哪些电影
        List<Integer> movieList = userMovieTable.get(userA);
        for(Integer movie : movieList){
            switch (movie){
                case 1 :
                    MovieKindByUserId.add(movie1.getGenres());
                    break;
                case 2 :
                    MovieKindByUserId.add(movie2.getGenres());
                    break;
                case 3 :
                    MovieKindByUserId.add(movie3.getGenres());
                    break;
                case 4 :
                    MovieKindByUserId.add(movie4.getGenres());
                    break;
                case 5 :
                    MovieKindByUserId.add(movie5.getGenres());
                    break;
            }
        }
        return MovieKindByUserId;
    }

    /**
     * 构建电影类型矩阵
     * 形如{m1:{k1:0,k2:1,...},...}
     * @return
     */
    public static Map<Integer,Map<String,Integer>> buildMovieKindTable(){
        Map<Integer,Map<String,Integer>> resultMap = new HashMap<>();
        //构建矩阵
        Map<String,Integer> innerMap;
        for(Movies movie : moviesList){
            innerMap = new HashMap<>();
            for (String kind : movieKind){
                if(movie.getGenres().contains(kind)){
                    innerMap.put(kind,1);
                }else {
                    innerMap.put(kind,0);
                }
            }
            resultMap.put(movie.getMovieId(),innerMap);
        }
        return resultMap;
    }

    /**
     * 构建用户对所有电影的评分map
     * 形如{u:{m1:rat,m2:rat,...},...}
     * @return
     */
    public static Map<Integer,Map<Integer,Integer>> buildUserAllMovieRatingTable(){
        Map<Integer,Map<Integer,Integer>> resultMap = new HashMap<>();
        Map<Integer,Integer> innerMap;
        for(Integer userId : userList){
            innerMap = new HashMap<>();
            for(Movies movies : moviesList){
                if(null == overAllMap.get(userId).get(movies.getMovieId())){
                    innerMap.put(movies.getMovieId(),0);
                }else {
                    innerMap.put(movies.getMovieId(),overAllMap.get(userId).get(movies.getMovieId()));
                }
            }
            resultMap.put(userId,innerMap);
        }
        return resultMap;
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
     * 构建用户对类型的评分map
     * 形如{u:{k1:rat,k2:rat,...},...}
     * @return
     */
    public static Map<Integer,Map<String,Double>> buildUserKindRatingTable(){
        Map<Integer,Map<String,Double>> resultMap = new HashMap<>();
        Map<String,Double> tempResultMap;
        Double tempResultDouble;
        //拿到用户-电影评分矩阵
        Map<Integer,Map<Integer,Integer>> UserMovieMap = buildUserAllMovieRatingTable();
        //拿到电影-类型矩阵
        Map<Integer,Map<String,Integer>> MovieKindMap = buildMovieKindTable();
        //矩阵相乘
        Iterator<Map.Entry<Integer,Map<Integer,Integer>>> iteratorUserMovieMap = UserMovieMap.entrySet().iterator();
        Map.Entry<Integer,Map<Integer,Integer>> tempEntryUserMovieMap;
        Integer tempKeyUserMovieMap;
        Map<Integer,Integer> tempMapUserMovieMap;
        //找出最大值和最小值用于归一化处理
        double maxRat = 0.0;
        double minRat = 99999.0;
        while (iteratorUserMovieMap.hasNext()){
            tempEntryUserMovieMap = iteratorUserMovieMap.next();
            //当前遍历用户
            tempKeyUserMovieMap = tempEntryUserMovieMap.getKey();
            //当前遍历用户对每部电影的评分map
            tempMapUserMovieMap = tempEntryUserMovieMap.getValue();
            tempEntryUserMovieMap = null;
            //子map
            tempResultMap = new HashMap<>();
            //遍历18个大类
            for(String kindStr : movieKind){
                Iterator<Map.Entry<Integer,Map<String,Integer>>> iteratorMovieKindMap = MovieKindMap.entrySet().iterator();
                Map.Entry<Integer,Map<String,Integer>> tempEntryMovieKindMap;
                Integer tempKeyMovieKindMap;
                Map<String,Integer> tempMapMovieKindMap;
                tempResultDouble = 0.0;
                while (iteratorMovieKindMap.hasNext()){
                    tempEntryMovieKindMap = iteratorMovieKindMap.next();
                    //当前遍历电影
                    tempKeyMovieKindMap = tempEntryMovieKindMap.getKey();
                    //当前遍历的电影的类型map
                    tempMapMovieKindMap = tempEntryMovieKindMap.getValue();
                    tempEntryMovieKindMap = null;
                    //计算该用户对该类型的评分
                    tempResultDouble += tempMapUserMovieMap.get(tempKeyMovieKindMap)*tempMapMovieKindMap.get(kindStr);
                }
                tempEntryMovieKindMap = null;
                tempResultMap.put(kindStr,tempResultDouble);
                //记录最大最小值
                if(tempResultDouble >= maxRat){
                    maxRat = tempResultDouble;
                }else if(tempResultDouble <= minRat){
                    minRat = tempResultDouble;
                }
            }
            tempMapUserMovieMap = null;
            resultMap.put(tempKeyUserMovieMap,tempResultMap);
        }
        //将矩阵归一化
        Iterator<Map.Entry<Integer,Map<String,Double>>> resultMapIterator = resultMap.entrySet().iterator();
        Map.Entry<Integer,Map<String,Double>> tempEntry;
        Map<String,Double> tempInnMap;
        Iterator<Map.Entry<String,Double>> tempInnMapIterator;
        Map.Entry<String,Double> tempInnMapEntry;
        double tempKinfRating;
        //遍历每个用户
        while (resultMapIterator.hasNext()){
            tempEntry = resultMapIterator.next();
            tempInnMap = tempEntry.getValue();
            tempInnMapIterator = tempInnMap.entrySet().iterator();
            //遍历该用户的所有类型
            while (tempInnMapIterator.hasNext()){
                tempInnMapEntry = tempInnMapIterator.next();
                tempKinfRating = tempInnMapEntry.getValue().doubleValue();
                tempInnMap.put(tempInnMapEntry.getKey(),(tempKinfRating-minRat)/(maxRat-minRat));
            }
        }
        return resultMap;
    }

    /**
     * 求用户所有评分的平均评分
     * @param userId
     * @return
     */
    public double averageRating(Integer userId){
        double averageScore = 0;
        //拿到该用户对电影的评分map
        Map<Integer,Integer> ratingsMap = overAllMap.get(userId);
        List<Integer> ratingsList = new ArrayList<>(ratingsMap.values());
        for(Integer rating : ratingsList){
            averageScore += rating;
        }
        ratingsMap = null;
        return  (averageScore * 1.0) / ratingsList.size();
    }

    /**
     * 求用户所有评分的平均评分
     * @param userId
     * @return
     */
    public double averageKindRating(Integer userId){
        double averageScore = 0.0;
        //拿到该用户对类型的评分map
        Map<String,Double> ratingsMap = kindOverAllMap.get(userId);
        List<Double> ratingsList = new ArrayList<>(ratingsMap.values());
        for(Double rating : ratingsList){
            averageScore += rating;
        }
        ratingsMap = null;
        return  (averageScore * 1.0) / ratingsList.size();
    }

    /**
     * 求用户对其看过的所有电影的二次方之和
     * @param userId
     * @return
     */
    public double sumRating(Integer userId){
        double sumScore = 0;
        //拿到该用户对电影的评分map
        Map<Integer,Integer> ratingsMap = overAllMap.get(userId);
        List<Integer> ratingsList = new ArrayList<>(ratingsMap.values());
        for(Integer rating : ratingsList){
            sumScore += Math.pow(rating,2);
        }
        ratingsMap = null;
        return sumScore;
    }

    /**
     * 建立电影和用户的倒排表，即一部电影被哪些用户看过
     * 形如
     * m1:u1,u2,u3
     * m2:u1,u2
     * m3:u2,u3
     * @return
     */
    public Map<Integer, List<Integer>> buildMovieUserTable(){
        Map<Integer, List<Integer>> movieUserTable = new HashMap<>();
        List<Integer> userSet;
        //拿到用户影评信息
        for(Ratings ratings : ratingsList){
            if(!movieUserTable.containsKey(ratings.getMovieId())){
                //倒排表中没有该电影
                userSet = new ArrayList<>();
                userSet.add(ratings.getUserId());
                movieUserTable.put(ratings.getMovieId(),userSet);
                userSet = null;
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
    public static Map<Integer, List<Integer>> buildUserMovieTable(){
        Map<Integer, List<Integer>> userMovieTable = new HashMap<>();
        List<Integer> movieSet;
        //拿到用户影评信息
        for(Ratings ratings : ratingsList){
            if(!userMovieTable.containsKey(ratings.getUserId())){
                //倒排表中没有该用户
                movieSet = new ArrayList<>();
                movieSet.add(ratings.getMovieId());
                userMovieTable.put(ratings.getUserId(),movieSet);
                movieSet = null;
            }else {
                userMovieTable.get(ratings.getUserId()).add(ratings.getMovieId());
            }
        }
        return userMovieTable;
    }


    /**
     * 两用户共同看过的电影
     * @return
     */
    public List<Integer> buildUserUserMovie(Integer userA,Integer userB){
        List<Integer> resultList = new ArrayList<>();
        //拿到用户AB
        List<Integer> userAMovie = userMovieTable.get(userA);
        List<Integer> userBMovie = userMovieTable.get(userB);
        //求交集
        for(Integer movieId : userAMovie){
            if(userBMovie.contains(movieId)){
                resultList.add(movieId);
            }
        }
        userAMovie = null;
        userBMovie = null;
        return resultList;
    }

    /**
     * 找出前N个最相似用户
     * @return
     * //拿到用户之间相似度的集合
     * //Map<Integer,Map<Integer,Double>> userSimMap = buildUserUserSimTable();
     * @throws SQLException
     */
    public Map<Integer,List<Integer>> buildsimUserList(Map<Integer,Map<Integer,Double>> userSimMap){
        Map<Integer,List<Integer>> resultMap = new HashMap<>();
        List<Integer> simUserList;//每个用户的相似用户集合
        //遍历
        Iterator<Map.Entry<Integer,Map<Integer,Double>>> iteratorOut = userSimMap.entrySet().iterator();
        Map.Entry<Integer,Map<Integer,Double>> tempEntryOut;
        Map<Integer,Double> tempMapInn;
        Iterator<Map.Entry<Integer,Double>> iteratorInn;
        Map.Entry<Integer,Double> tempEntryInn;
        List<Map.Entry<Integer,Double>> sortList;
        //外层遍历
        while (iteratorOut.hasNext()){
            tempEntryOut = iteratorOut.next();
            tempMapInn = tempEntryOut.getValue();
            //map转换为list进行排序
            sortList = new ArrayList<>(tempMapInn.entrySet());
            Collections.sort(sortList,valueComparator);
            //内层遍历寻找前N最相似的用户
            iteratorInn = sortList.iterator();
            simUserList = new ArrayList<>();
            while (iteratorInn.hasNext()){
                tempEntryInn = iteratorInn.next();
                if(simUserList.size() < simNumber){
                    simUserList.add(tempEntryInn.getKey());
                }
            }
            resultMap.put(tempEntryOut.getKey(),simUserList);
            simUserList = null;
            sortList = null;
            tempMapInn = null;
        }
        return resultMap;
    }

    /**+
     * 构建推荐map
     * //拿到用户之间相似度的集合
     * //Map<Integer,Map<Integer,Double>> userSimMap = buildUserUserSimTable();
     * @return
     */
    public void buildRecommendList(Map<Integer,Map<Integer,Double>> userSimMap,Integer movieNumber) throws IOException {
        System.out.println("-------开始构建top"+movieNumber+"候选列表");
        //直接打印到文件
        StringBuffer stringBuffer = new StringBuffer();
        FileWriter fileWriter = new FileWriter("D:\\RecommentAlgorithmTest\\candidacyTop"+movieNumber+".txt", true);
        List<Integer> tempMovieList;
        //获取每个用户的前N和最相似用户
        Map<Integer,List<Integer>> simUserMap = buildsimUserList(userSimMap);
        Iterator<Map.Entry<Integer,List<Integer>>> iteratorMap = simUserMap.entrySet().iterator();
        Map.Entry<Integer,List<Integer>> tempEntry;
        Integer tempUserId;
        List<Integer> tempSimUserList;
        //每个用户自己看过的电影集合及评分
        Map<Integer,Integer> userRatingMap;
        //用户的每个相似用户看过的电影及其评分
        Map<Integer,Integer> simUserRatingMap;
        Iterator<Map.Entry<Integer,Integer>>  tempIteratorSimUserRatingMap;
        Map.Entry<Integer,Integer> tempEntrySimUserRatingMap;
        //用户之间的相似度
        double tempSim;
        //相似度总和
        double tempSumSim;
        //推荐电影map
        Map<Integer,Double> tempRecommentMovieMap;
        Iterator<Map.Entry<Integer,Double>> tempIteratorRecommentMovieMap;
        Map.Entry<Integer,Double> tempEntryRecommentMovieMap;
        //最终的推荐电影map
        Map<Integer,Double> tempResultRecommentMovieMap;
        //重新计算该电影的评分
        double tempMovieNewRating;
        //map转换为list进行排序
        List<Map.Entry<Integer,Double>> sortList;
        Iterator<Map.Entry<Integer,Double>> iteratorInn;
        Map.Entry<Integer,Double> tempEntryInn;
        //外层遍历，每个用户
        System.out.println("-------共"+simUserMap.size()+"推荐列表，已构建");
        int i = 0;
        while (iteratorMap.hasNext()){
            tempEntry = iteratorMap.next();
            tempUserId = tempEntry.getKey();
            //该用户看过的电影及评分
            userRatingMap = overAllMap.get(tempUserId);
            //该用户的推荐列表
            tempResultRecommentMovieMap = new HashMap<>();
            tempRecommentMovieMap = new HashMap<>();
            tempSimUserList = tempEntry.getValue();
            //该用户与所有相似用户的相似度总和
            tempSumSim = 0;
            for(Integer simUserId : tempSimUserList){
                //该相似用户与用户之间的相似度
                tempSim = userSimMap.get(tempUserId).get(simUserId);
                tempSumSim += tempSim;
                //该相似用户看过的电影以及评分
                simUserRatingMap = overAllMap.get(simUserId);
                tempIteratorSimUserRatingMap = simUserRatingMap.entrySet().iterator();
                while (tempIteratorSimUserRatingMap.hasNext()){
                    tempEntrySimUserRatingMap = tempIteratorSimUserRatingMap.next();
                    if(userRatingMap.containsKey(tempEntrySimUserRatingMap.getKey())){
                        //如果用户已经看过这部电影就不推荐
                        continue;
                    }
                    //没看过就重新计算该电影的评分  sim*(rating - aveRating)
                    tempMovieNewRating = tempSim * (simUserRatingMap.get(tempEntrySimUserRatingMap.getKey()) - averageRating(simUserId));
                    if(tempRecommentMovieMap.containsKey(tempEntrySimUserRatingMap.getKey())){
                        //如果该推荐列表中已经有该电影
                        tempRecommentMovieMap.put(tempEntrySimUserRatingMap.getKey(),tempMovieNewRating + tempRecommentMovieMap.get(tempEntrySimUserRatingMap.getKey()));
                    }else {
                        tempRecommentMovieMap.put(tempEntrySimUserRatingMap.getKey(),tempMovieNewRating);
                    }
                }
                simUserRatingMap = null;
            }
            //重新计算该用户推荐列表中的评分
            tempIteratorRecommentMovieMap = tempRecommentMovieMap.entrySet().iterator();
            while (tempIteratorRecommentMovieMap.hasNext()){
                tempEntryRecommentMovieMap = tempIteratorRecommentMovieMap.next();
                tempResultRecommentMovieMap.put(tempEntryRecommentMovieMap.getKey(),averageRating(tempUserId)+(tempEntryRecommentMovieMap.getValue()/tempSumSim));
            }
            tempSimUserList = null;
            userRatingMap = null;
            tempRecommentMovieMap = null;
            //排序最终的推荐列表tempResultRecommentMovieMap
            //map转换为list进行排序
            sortList = new ArrayList<>(tempResultRecommentMovieMap.entrySet());
            Collections.sort(sortList,valueComparator);
            //内层遍历寻找前N最相似的用户
            iteratorInn = sortList.iterator();
            //最终的推荐电影
            tempMovieList = new ArrayList<>();
            while (iteratorInn.hasNext()){
                tempEntryInn = iteratorInn.next();
                if(tempMovieList.size() < movieNumber){
                    tempMovieList.add(tempEntryInn.getKey());
                }
            }
            tempResultRecommentMovieMap = null;
            sortList = null;
            //直接打印
            stringBuffer.append(tempUserId+":"+tempMovieList.toString()+"\n");
            tempMovieList = null;
            i++;
            System.out.print("\r");
            System.out.print(i);
        }
        fileWriter.write(stringBuffer.toString());
        fileWriter.close();
    }

    /**
     * 构建用户之间相似度的集合(sim1)
     * 形如：{u:{n1:sim1,n2:sim2,...},...}
     * @return
     */
    public Map<Integer,Map<Integer,Double>> buildUserUserSim1Table() throws SQLException {
        Map<Integer,Map<Integer,Double>> resultMap = new HashMap<>();
        Map<Integer,Double> tempMap;
        Double tempSim;
        Integer userIdA;
        Integer userIdB;
        int size = userList.size();
        for(int i = 0; i < size; i ++){
            userIdA = userList.get(i);
            tempMap = new HashMap<>();
            System.out.println("正在计算用户"+userIdA+"与其他用户的相似度，共"+(size-1)+"，已计算");
            for(int j = 0; j < size; j ++){
                userIdB = userList.get(j);
                if(userIdA == userIdB){
                    continue;
                }
                tempSim = countSim1BySameMovie(userIdA,userIdB);
                tempMap.put(userIdB,tempSim);
                System.out.print("\r");
                System.out.print(j);
            }
            resultMap.put(userIdA,tempMap);
            System.out.println("------完成构建用户"+userIdA+"的相似用户列表，共"+size+"，已构建"+i);
            tempMap = null;
        }
        System.out.println("用户之间相似度的集合构建完成");
        userList = null;
        return resultMap;
    }

    public Map<Integer,Map<Integer,Double>> buildUserUserSim2Table() throws SQLException {
        Map<Integer,Map<Integer,Double>> resultMap = new HashMap<>();
        Map<Integer,Double> tempMap;
        Double tempSim;
        Integer userIdA;
        Integer userIdB;
        int size = userList.size();
        for(int i = 0; i < size; i ++){
            userIdA = userList.get(i);
            tempMap = new HashMap<>();
            System.out.println("正在计算用户"+userIdA+"与其他用户的相似度，共"+(size-1)+"，已计算");
            for(int j = 0; j < size; j ++){
                userIdB = userList.get(j);
                if(userIdA == userIdB){
                    continue;
                }
                tempSim = countSim2BySameMovie(userIdA,userIdB);
                tempMap.put(userIdB,tempSim);
                System.out.print("\r");
                System.out.print(j);
            }
            resultMap.put(userIdA,tempMap);
            System.out.println("------完成构建用户"+userIdA+"的相似用户列表，共"+size+"，已构建"+i);
            tempMap = null;
        }
        System.out.println("用户之间相似度的集合构建完成");
        userList = null;
        return resultMap;
    }

    /**
     * 计算用户类型多样性偏好的值
     * @return
     */
    public double countKindLike(Integer u) throws SQLException {
        double resultCount = 0.0;
        //拿到该用户评论过的电影类型
        List<String> userKindList = selectMovieKindByUserId(u);
        //用户评分总数
        double sumRatingCount = userKindList.size();
        //遍历18个电影类型
        //用户对该类型的评分数
        double sumKindRatingCount;
        double tempPC;
        for(String kindStr : movieKind){
            sumKindRatingCount = 0.0;
            for(String userKindStr : userKindList){
                if(userKindStr.contains(kindStr)){
                    sumKindRatingCount ++;
                }
            }
            tempPC = sumKindRatingCount/sumRatingCount;
            if(tempPC == 0){
                resultCount += 0;
            }else {
                resultCount += (Math.log(tempPC)/Math.log(movieKind.size()))*tempPC;
            }
            System.out.println(tempPC+"      "+Math.log(tempPC)+"        "+Math.log(movieKind.size())+"    "+(Math.log(tempPC)/Math.log(movieKind.size()))*tempPC+"    "+resultCount);
        }
        return resultCount*(-1);
    }

    /**
     * 计算两个用户的相似度 sim1
     * @param uA
     * @param uB
     * 例子：
     * {A:{B:[m1,m2]}
     * r_m1_A表示A对m1的评分
     * @return
     */
    public Double countSim1BySameMovie(Integer uA,Integer uB){
        //两个用户之间共同看过的电影
        List<Integer> sameMovies = buildUserUserMovie(uA,uB);
        //计算用户AB对共同看过的电影评分的乘积之和
        double rmA;//A对电影的评分 - 平均分
        double rmB;//B对电影的评分 - 平均分
        double EuAuB = 0.0;//（用户A对各电影评分*用户B对对应电影评分）之和,即 r_m1_A*r_m1_B + r_m2_A*r_m2_B
        //分别计算用户AB对其看过的所有电影评分的二次方之和
        double powSumA = 0.0;
        double powSumB = 0.0;
        for(Integer movieId : sameMovies){
            //皮尔逊
            rmA = overAllMap.get(uA).get(movieId);
            rmB = overAllMap.get(uB).get(movieId);
            EuAuB += (rmA-averageRating(uA)) * (rmB-averageRating(uB));
            powSumA += Math.pow(rmA,2);
            powSumB += Math.pow(rmB,2);
        }
        sameMovies = null;
        double numerator = EuAuB;
        double denominator = Math.sqrt(powSumA*powSumB);
        // 分母不能为0
        if (denominator == 0) {
            return 0.0;
        }
        return numerator/denominator;
    }

    public Double countSim2BySameMovie(Integer uA,Integer uB) throws SQLException {
        //归一化之后，两个用户之间共同看过的电影类型即所有类型movieKind
        //计算用户AB对共同看过的电影类型评分的乘积之和
        double rmA;//A对电影的评分 - 平均分
        double rmB;//B对电影的评分 - 平均分
        double EuAuB = 0.0;//（用户A对各电影评分*用户B对对应电影评分）之和,即 r_m1_A*r_m1_B + r_m2_A*r_m2_B
        //分别计算用户AB对其看过的所有电影评分的二次方之和
        double powSumA = 0.0;
        double powSumB = 0.0;
        for(String movieKind : movieKind){
            //皮尔逊
            rmA = kindOverAllMap.get(uA).get(movieKind);
            rmB = kindOverAllMap.get(uB).get(movieKind);
            EuAuB += (rmA-averageKindRating(uA)) * (rmB-averageKindRating(uB));
            powSumA += Math.pow(rmA,2);
            powSumB += Math.pow(rmB,2);
        }
        double numerator = EuAuB;
        double denominator = Math.sqrt(powSumA*powSumB);
        // 分母不能为0
        if (denominator == 0) {
            return 0.0;
        }
        return numerator/denominator;
    }

    public Double countSimBySameMovie(Integer uA,Integer uB) throws SQLException {
        double resultSim;
        //sim1
        double sim1 = countSim1BySameMovie(uA,uB);
        //sim2
        double sim2 = countSim2BySameMovie(uA,uB);
        //计算用户uA的类型多样性偏好的值
        double KindLike = countKindLike(uA);
        resultSim =  KindLike*sim1 + (1-KindLike)*sim2;
        return resultSim;
    }

    /**
     * 构建用户之间相似度的集合(综合相似度sim)
     * 形如：{u:{n1:sim1,n2:sim2,...},...}
     * @return
     */
    public Map<Integer,Map<Integer,Double>> buildUserUserSimTable() throws SQLException {
        Map<Integer,Map<Integer,Double>> resultMap = new HashMap<>();
        Map<Integer,Double> tempMap;
        Double tempSim;
        Integer userIdA;
        Integer userIdB;
        int size = userList.size();
        for(int i = 0; i < size; i ++){
            userIdA = userList.get(i);
            tempMap = new HashMap<>();
            System.out.println("正在计算用户"+userIdA+"与其他用户的相似度，共"+(size-1)+"，已计算");
            for(int j = 0; j < size; j ++){
                userIdB = userList.get(j);
                if(userIdA == userIdB){
                    continue;
                }
                tempSim = countSimBySameMovie(userIdA,userIdB);
                tempMap.put(userIdB,tempSim);
                System.out.print("\r");
                System.out.print(j);
            }
            resultMap.put(userIdA,tempMap);
            System.out.println("------完成构建用户"+userIdA+"的相似用户列表，共"+size+"，已构建"+i);
            tempMap = null;
        }
        System.out.println("用户之间相似度的集合构建完成");
        userList = null;
        return resultMap;
    }


}
