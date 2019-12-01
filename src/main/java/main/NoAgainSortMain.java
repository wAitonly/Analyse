package main;

import dao.InfoGetUtil;
import org.omg.CORBA.INTERNAL;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 论文外的方法一
 * 没有权重选择
 */
public class NoAgainSortMain {

    private static Map<Integer,Integer> movieKindMap;
    private static Map<Integer,List<Integer>> KindMovieMap;
    private static List<Integer> movieKind;
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
        int N = 5;
        int fileN;
        for(int i = 1; i <= 5; i++){
            fileN = (i -1)*5 + 10;
            //读取文件拿到候选列表
            Map<Integer, List<Integer>> recommentListMap = readFile(fileN);
            Map<Integer, List<Integer>> resultListMap;
            //将候选列表权重选择
            Iterator<Map.Entry<Integer, List<Integer>>> iterator;
            Map.Entry<Integer, List<Integer>> tempEnty;
            Integer tempUserId;
            List<Integer> tempMovieList;
            List<Integer> tempResultList;
            for(int threshold = 1; threshold <=1;threshold++){
                iterator = recommentListMap.entrySet().iterator();
                resultListMap = new HashMap<>();
                while (iterator.hasNext()){
                    tempEnty = iterator.next();
                    tempUserId = tempEnty.getKey();
                    tempMovieList = tempEnty.getValue();
                    tempResultList = thirdSelectByUserIdAndList(tempMovieList,threshold,N);
                    resultListMap.put(tempUserId,tempResultList);
                }
                //将recommentListMap打印
                StringBuffer str = new StringBuffer();
                FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\noagain\\resultThirdSortThreshold"+threshold+"Top"+N+"Len"+fileN+".txt", true);
                Set set = resultListMap.entrySet();
                Iterator iter = set.iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    str.append(entry.getKey() + " : " + entry.getValue()).append("\n");
                }
                fw.write(str.toString());
                fw.close();
            }
        }
    }

    private static Map<Integer, List<Integer>> readFile(Integer N) throws IOException, SQLException {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\noAgainCandidacyTop"+N+".txt")));
        String data;
        Integer tempUserId;
        List<String> tempList;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
            tempList = Arrays.asList(data.substring(data.indexOf("[")+1,data.indexOf("]")).trim().split(","));
            resultMapTemp.put(tempUserId,tempList);
            //显示释放tempList占用内存
            tempList = null;
        }
        //遍历resultMapTemp
        Iterator<Map.Entry<Integer, List<String>>> iterator = resultMapTemp.entrySet().iterator();
        Map.Entry<Integer, List<String>> tempEntry;
        Integer tempUserIdResult;
        List<String> tempMovieNameList;
        List<Integer> tempMovieIdList;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempUserIdResult = tempEntry.getKey();
            tempMovieNameList = tempEntry.getValue();
            //根据电影名称列表查询电影id列表
            //tempMovieIdList = util.selectMovieIdByMovieName(tempMovieNameList);
            tempMovieIdList = new ArrayList<>();
            for(String str : tempMovieNameList){
                tempMovieIdList.add(Integer.valueOf(str.trim()));
            }
            resultMap.put(tempUserIdResult,tempMovieIdList);
        }
        //显示释放resultMapTemp占用内存
        resultMapTemp = null;
        tempMovieNameList = null;
        tempMovieIdList = null;
        return  resultMap;
    }

    /**
     * 根据用户id以及其推荐列表还有阈值重新权重选择推荐列表
     * @param moviesIdList
     * @param threshold
     * @return
     */
    public static List<Integer> thirdSelectByUserIdAndList(List<Integer> moviesIdList, Integer threshold, Integer N){
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
            if(Collections.max(CWeight) - Collections.min(CWeight) <= 1){
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
        //如果权重选择完后的推荐列表不足N个，取候选列表前几个补
        int resultSize = resultList.size();
        if(resultSize < N){
            for(Integer movieId : moviesIdList){
                if(!resultList.contains(movieId)){
                    resultList.add(movieId);
                }
                if(resultList.size() == N){
                    break;
                }
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
