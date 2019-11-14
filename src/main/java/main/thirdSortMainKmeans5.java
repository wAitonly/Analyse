package main;

import dao.InfoGetUtil;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * 根据权重第二次处理推荐列表
 */
public class thirdSortMainKmeans5 {
    private static Map<Integer,Integer> movieKindMap;
    private static Map<Integer,List<Integer>> KindMovieMap;
    private static List<Integer> movieKind;
    private static Integer N = 0;
    private static Integer thresold = 0;
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
        //读取文件拿到各用户重排序后的推荐列表
        int fileN;
        for(int i = 1; i <= 1;i ++){
            N = 5;
            fileN = (i -1)*5 + 10;
            for(thresold = 1; thresold < 5; thresold++){
                Map<Integer, List<Integer>> resultMap = readFile(fileN);
                Iterator<Map.Entry<Integer, List<Integer>>> iterator = resultMap.entrySet().iterator();
                Map.Entry<Integer, List<Integer>> tempEntry;
                Integer tempUserId;
                List<Integer> tempMoviesIdList;
                List<Integer> tempSelectMoviesIdList;
                //遍历拿到权重选择后的推荐列表
                Map<Integer, List<Integer>> afterSelectMap = new HashMap<>();
                while (iterator.hasNext()){
                    tempEntry = iterator.next();
                    tempUserId = tempEntry.getKey();
                    tempMoviesIdList = tempEntry.getValue();
                    //根据用户id以及其推荐列表还有阈值重新权重选择推荐列表
                    tempSelectMoviesIdList = thirdSelectByUserIdAndList(tempUserId,tempMoviesIdList,thresold);
                    afterSelectMap.put(tempUserId,tempSelectMoviesIdList);
                }
                //输出到文件
                //将结果输出到文件
                StringBuffer str = new StringBuffer();
                FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\sort\\resultThirdSortTop"+N+"Threshold"+thresold+"Len"+fileN+".txt", true);
                Set set = afterSelectMap.entrySet();
                Iterator iter = set.iterator();
                while(iter.hasNext()){
                    Map.Entry entry = (Map.Entry)iter.next();
                    str.append(entry.getKey()+" : "+entry.getValue()).append("\n");
                }
                fw.write(str.toString());
                fw.close();
                tempMoviesIdList = null;
                tempSelectMoviesIdList = null;
                afterSelectMap = null;
                resultMap = null;
            }
        }
    }

    /**
     * 读取文件拿到重排序后的各用户推荐列表用户id，电影id
     * 形如{u1:[m1,m2,m3],u2:[m1,m2,m3]}
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private static Map<Integer, List<Integer>> readFile(Integer N) throws IOException{
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\sort\\resultAgainSortTop"+N+".txt")));
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

    /**
     * 根据用户id以及其推荐列表还有阈值重新权重选择推荐列表
     * @param userId
     * @param moviesIdList
     * @param threshold
     * @return
     */
    public static List<Integer> thirdSelectByUserIdAndList(Integer userId,List<Integer> moviesIdList,Integer threshold) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        //遍历推荐列表，计算出各聚类的初始化权重,即各C类有多少个
        Integer tempMovieKind;
        List<Integer> C1List = new ArrayList<>();
        List<Integer> C2List = new ArrayList<>();
        List<Integer> C3List = new ArrayList<>();
        List<Integer> C4List = new ArrayList<>();
        List<Integer> C5List = new ArrayList<>();
        //TOP-N
        int n = 1;
        for(Integer movieId : moviesIdList){
            //取top-N列表初始化权限列表
            if(n > N){
                break;
            }
            n ++;
            //查询该电影类型
            tempMovieKind = movieKindMap.get(movieId);
            //根据C类别分类
            switch (tempMovieKind){
                case 0:
                    C1List.add(movieId);
                    break;
                case 1:
                    C2List.add(movieId);
                    break;
                case 2:
                    C3List.add(movieId);
                    break;
                case 3:
                    C4List.add(movieId);
                    break;
                case 4:
                    C5List.add(movieId);
                    break;
            }
        }
        //初始化C类权重列表
        List<Integer> CWeight = new ArrayList<>();
        CWeight.add(C1List.size());
        CWeight.add(C2List.size());
        CWeight.add(C3List.size());
        CWeight.add(C4List.size());
        CWeight.add(C5List.size());
        //遍历权重列表进行权重均衡
        while (true){
            for(int i = 0 ; i < 5 ; i ++){
                //如果该C类权重大于阈值
                if(CWeight.get(i) > threshold){
                    CWeight.set(CWeight.indexOf(Collections.min(CWeight)),Collections.min(CWeight)+1);
                    CWeight.set(i,CWeight.get(i)-1);
                }
            }
            if(Collections.max(CWeight) - Collections.min(CWeight) == 1){
                break;
            }
        }

        //显示释放内存
        C1List = null;
        C2List = null;
        C3List = null;
        C4List = null;
        C5List = null;
        //根据权重重新选择，即C1类选CWeight.get(0)个,C2类选CWeight.get(1)个,C3类选CWeight.get(2)个
        //遍历重排序后的推荐列表
        int C1 = 0;
        int C2 = 0;
        int C3 = 0;
        int C4 = 0;
        int C5 = 0;
        for(Integer movieId : moviesIdList){
            //查询该电影类型
            tempMovieKind = movieKindMap.get(movieId);
            if(tempMovieKind == null){
                continue;
            }
            switch (tempMovieKind){
                case 0:
                    if(C1 < CWeight.get(0)){
                        resultList.add(movieId);
                        C1 ++ ;
                    }
                    break;
                case 1:
                    if(C2 < CWeight.get(1)){
                        resultList.add(movieId);
                        C2 ++ ;
                    }
                    break;
                case 2:
                    if(C3 < CWeight.get(2)){
                        resultList.add(movieId);
                        C3 ++ ;
                    }
                    break;
                case 3:
                    if(C4 < CWeight.get(3)){
                        resultList.add(movieId);
                        C4 ++ ;
                    }
                    break;
                case 4:
                    if(C5 < CWeight.get(4)){
                        resultList.add(movieId);
                        C5 ++ ;
                    }
                    break;
            }
        }
        return resultList;

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
