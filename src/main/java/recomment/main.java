package recomment;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {
    public static void main(String[] args) throws IOException, SQLException {
        OldRecommentAlgorithmByThreshold oldRecommentAlgorithm = new OldRecommentAlgorithmByThreshold();
        //拿到用户之间综合相似度的集合
        //Map<Integer, Map<Integer,Double>> userSimMap = oldRecommentAlgorithm.buildUserUserSimTable();
        //读文件拿综合相似度的集合
        Map<Integer, Map<Integer,Double>> userSimMap = readFileGetSimMap();
        //拿到推荐列表
        //oldRecommentAlgorithm.buildRecommendList(userSimMap,50);
        //int movieNumber;
//        for(int i = 1; i <= 5; i++){
//            movieNumber = i * 5 *3;
//            oldRecommentAlgorithm.buildRecommendList(userSimMap,movieNumber);
//        }
        double threshold;
          for(int i = 34; i <= 38; i++){
              threshold = i * (0.1);
              oldRecommentAlgorithm.buildRecommendList(userSimMap,threshold,41);
        }
    }

    private static Map<Integer, Map<Integer,Double>> readFileGetSimMap() throws IOException {
        Map<Integer, Map<Integer,Double>> userSimMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\simMap.txt")));
        String data;
        Integer tempUserId;
        List<String> tempList;
        Map<Integer,Double> tempMap;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
            tempList = Arrays.asList(data.substring(data.indexOf("[")+1,data.indexOf("]")).trim().split(","));
            tempMap = new HashMap<>();
            for(String tempStr : tempList){
                tempMap.put(Integer.valueOf(tempStr.substring(0,tempStr.indexOf(":"))),Double.valueOf(tempStr.substring(tempStr.indexOf(":")+1)));
            }
            tempList = null;
            userSimMap.put(tempUserId,tempMap);
            //显示释放tempList占用内存
            tempMap = null;
        }
        System.out.println("simMap文件读完毕，共"+userSimMap.size()+"位用户，每位用户与"+userSimMap.get(593).size()+"位用户相似");
        return userSimMap;
    }
}
