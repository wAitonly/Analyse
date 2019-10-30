package core;


import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;


public class forwardMain {
    public static void main(String[] args) throws IOException, URISyntaxException, ParseException {
        //电影类型id,18个大类
//        List<Integer> kindList = new ArrayList<Integer>(){{
//            add(1);add(2);add(3);add(4);add(5);add(6);
//            add(7);add(8);add(9);add(10);add(11);add(12);
//            add(13);add(14);add(15);add(16);add(17);add(18);
//        }};
        List<Integer> kindList = new ArrayList<Integer>(){{
            add(1);add(2);
        }};
        //两个矩阵相乘
        Map<Integer,Map<Integer,Integer>> resultMap = new HashMap<>();
        Map<Integer,Integer> tempResultMap;
        Integer tempResultInteger;

        //用户-电影评分矩阵
        Map<Integer,Map<Integer,Integer>> UserMovieMap = new HashMap<>();
        //初始化
        Map<Integer,Integer> u1Map = new HashMap<>();
        u1Map.put(1,3);
        u1Map.put(2,2);
        u1Map.put(3,1);
        Map<Integer,Integer> u2Map = new HashMap<>();
        u2Map.put(1,1);
        u2Map.put(2,2);
        u2Map.put(3,3);
        UserMovieMap.put(1,u1Map);
        UserMovieMap.put(2,u2Map);
        //电影-类型矩阵
        Map<Integer,Map<Integer,Integer>> MovieKindMap = new HashMap<>();
        //初始化
        Map<Integer,Integer> m1Map = new HashMap<>();
        m1Map.put(1,1);
        m1Map.put(2,0);
        Map<Integer,Integer> m2Map = new HashMap<>();
        m2Map.put(1,0);
        m2Map.put(2,1);
        Map<Integer,Integer> m3Map = new HashMap<>();
        m3Map.put(1,1);
        m3Map.put(2,1);
        MovieKindMap.put(1,m1Map);
        MovieKindMap.put(2,m2Map);
        MovieKindMap.put(3,m3Map);

        Iterator<Map.Entry<Integer,Map<Integer,Integer>>> iteratorUserMovieMap = UserMovieMap.entrySet().iterator();
        Map.Entry<Integer,Map<Integer,Integer>> tempEntryUserMovieMap;
        Integer tempKeyUserMovieMap;
        Map<Integer,Integer> tempMapUserMovieMap;
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
            for(Integer kindId : kindList){
                Iterator<Map.Entry<Integer,Map<Integer,Integer>>> iteratorMovieKindMap = MovieKindMap.entrySet().iterator();
                Map.Entry<Integer,Map<Integer,Integer>> tempEntryMovieKindMap;
                Integer tempKeyMovieKindMap;
                Map<Integer,Integer> tempMapMovieKindMap;
                tempResultInteger = 0;
                while (iteratorMovieKindMap.hasNext()){
                    tempEntryMovieKindMap = iteratorMovieKindMap.next();
                    //当前遍历电影
                    tempKeyMovieKindMap = tempEntryMovieKindMap.getKey();
                    //当前遍历的电影的类型map
                    tempMapMovieKindMap = tempEntryMovieKindMap.getValue();
                    tempEntryMovieKindMap = null;
                    //计算该用户对该类型的评分
                    tempResultInteger += tempMapUserMovieMap.get(tempKeyMovieKindMap)*tempMapMovieKindMap.get(kindId);
                }
                tempEntryMovieKindMap = null;
                tempResultMap.put(kindId,tempResultInteger);
            }
            tempMapUserMovieMap = null;
            resultMap.put(tempKeyUserMovieMap,tempResultMap);
        }
        System.out.println(resultMap.toString());
    }
}
