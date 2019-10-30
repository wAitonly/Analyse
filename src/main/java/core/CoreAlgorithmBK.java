package core;

import Bean.Ratings;
import dao.InfoGetUtil;

import java.sql.SQLException;
import java.util.*;

/**
 * 核心算法
 */
public class CoreAlgorithmBK {
    //喜欢的阈值
    private static final Integer likeRating = 3;
    /**
     * 指定用户喜欢的电影总数
     */
    public static Integer getUserItemToatal(Integer userId) throws SQLException {
        Map<Integer, Integer> userItemTotalMap = new HashMap<>();
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getRatings();
        for(Ratings ratings : ratingsList){
            if(ratings.getRating() > likeRating){
                //影评4星5星为喜欢
                if(userItemTotalMap.containsKey(ratings.getUserId())){
                    //如果map中已经有该用户的统计信息，直接+1
                    userItemTotalMap.put(ratings.getUserId(),userItemTotalMap.get(ratings.getUserId())+1);
                }else {
                    //没有的话直接初始化为1
                    userItemTotalMap.put(ratings.getUserId(),1);
                }
            }
        }
        return userItemTotalMap.get(userId);
    }


    /**
     * 建立倒排表
     * 形如{u:{n1:[movie1,movie2],n2:[movie2,movie4]},...}  存储的是用户间共同看过的电影的集合
     * Set无序散列均匀
     */
    public static Map<Integer, Set<Integer>> buildItemUserFalshBackTable() throws SQLException {
        Map<Integer, Set<Integer>> itemUserFalshBackTable = new HashMap<>();
        Set<Integer> userSet;
        //拿到用户影评信息
        List<Ratings> ratingsList = new InfoGetUtil().getRatings();
        for(Ratings ratings : ratingsList){
            if(itemUserFalshBackTable.containsKey(ratings.getMovieId())){
                //如果倒排表中已经有该电影的统计信息
                if(ratings.getRating() > likeRating){
                    //如果喜欢直接增加喜欢该电影的用户
                    itemUserFalshBackTable.get(ratings.getMovieId()).add(ratings.getUserId());
                }
            }else {
                //没有的话先判断这条影评是否为该用户喜欢这部电影
                if(ratings.getRating() > likeRating){
                    //如果喜欢
                    userSet = new HashSet<>();
                    userSet.add(ratings.getUserId());
                    itemUserFalshBackTable.put(ratings.getMovieId(),userSet);
                }
            }
        }
        return itemUserFalshBackTable;
    }



    /**
     * 建立用户相似度矩阵
     */
    public static int[][] buildSimilarMatrix() throws SQLException {
        //参与影评用户总数量
        int num = InfoGetUtil.getUserCount();
        //初始化矩阵
        int[][] similarMatrix = new int[num][num];
        //拿到电影-用户的倒排表
        Map<Integer,Set<Integer>> itemUserFalshBackTable = buildItemUserFalshBackTable();
        //计算相似度矩阵【稀疏】
        Set<Map.Entry<Integer, Set<Integer>>> entrySet = itemUserFalshBackTable.entrySet();
        Iterator<Map.Entry<Integer, Set<Integer>>> iterator = entrySet.iterator();
        while(iterator.hasNext()){
            Set<Integer> likeUsers = iterator.next().getValue();
            for (Integer user_row : likeUsers) {
                //第一次遍历形成列用户
                for (Integer user_line : likeUsers) {
                    //第二次遍历形成行用户
                    if(user_row.equals(user_line)){
                        //同一用户不计算，即该格的值肯定为该用户喜欢的电影数
                        continue;
                    }
                    //计算两个用户之间都喜欢的电影总数，即每次遍历+1
                    similarMatrix[user_row][user_line] += 1;
                }
            }
        }
        return similarMatrix;
    }

    /**
     * 计算指定用户与其他用户之间的相似度【余弦相似性】
     */
    public static Map<Integer,Double> getUserSimilarity(Integer userId) throws SQLException {
        //相似度
        Double similarity;
        //存入Map
        Map<Integer,Double> similarityMap = new HashMap<>();
        //获取用户相似度矩阵
        int[][] similarMatrix = buildSimilarMatrix();
        //行长度
        int size = similarMatrix.length;
        //二维数组几行几列即对应着userid
        for(int i = 0;i < size;i ++){
            if(i != userId){
                similarity = similarMatrix[userId][i]/Math.sqrt(getUserItemToatal(userId)*getUserItemToatal(i));
                similarityMap.put(i,similarity);
                System.out.println(userId+"--"+i+"相似度:"+similarMatrix[userId][i]/Math.sqrt(getUserItemToatal(userId)*getUserItemToatal(i)));
            }
        }
        return similarityMap;
    }

}
