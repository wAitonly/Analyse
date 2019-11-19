package main;

import dao.InfoGetUtil;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * 根据权重第二次处理推荐列表
 */
public class thirdSortMain {
    private static List<String> movieKind;
    private static Integer N = 0;
    private static Integer thresold = 0;
    private static InfoGetUtil util = new InfoGetUtil();
    static {
        //静态获取数据库连接
        //初始化movieKind
        movieKind = new ArrayList<>();
        movieKind.add("Action");
        movieKind.add("Adventure");
        movieKind.add("Animation");
        movieKind.add("Children's");
        movieKind.add("Comedy");
        movieKind.add("Crime");
        movieKind.add("Documentary");
        movieKind.add("Drama");
        movieKind.add("Fantasy");
        movieKind.add("Film-Noir");
        movieKind.add("Horror");
        movieKind.add("Musical");
        movieKind.add("Mystery");
        movieKind.add("Romance");
        movieKind.add("Sci-Fi");
        movieKind.add("Thriller");
        movieKind.add("War");
        movieKind.add("Western");
    }

    public static void main(String[] args) throws IOException, SQLException {
        //读取文件拿到各用户重排序后的推荐列表

        for(int i = 1; i <= 5;i ++){
            N = 5;
            int fileN = (i -1)*5 + 10;
            Map<Integer, List<Integer>> candicacyMap = readCandicacy(fileN);
            for(thresold = 1; thresold < 5; thresold++){
                Map<Integer, List<Integer>> resultMap = readFile(fileN);
                Iterator<Map.Entry<Integer, List<Integer>>> iterator = resultMap.entrySet().iterator();
                Map.Entry<Integer, List<Integer>> tempEntry;
                Integer tempUserId;
                List<Integer> tempMoviesIdList;
                List<Integer> tempSelectMoviesIdList;
                //遍历拿到权重选择后的推荐列表
                Map<Integer, List<Integer>> afterSelectMap = new HashMap<>();
                int size = resultMap.size();
                int count = 1;
                while (iterator.hasNext()){
                    tempEntry = iterator.next();
                    tempUserId = tempEntry.getKey();
                    tempMoviesIdList = tempEntry.getValue();
                    //根据用户id以及其推荐列表还有阈值重新权重选择推荐列表
                    tempSelectMoviesIdList = thirdSelectByUserIdAndList(tempUserId,tempMoviesIdList,thresold,candicacyMap.get(tempUserId));
                    afterSelectMap.put(tempUserId,tempSelectMoviesIdList);
                    System.out.println("正在权重选择Top"+N+"Threshold"+thresold+"Len"+fileN+"推荐列表，共"+size+"用户的候选列表待选择，已完成"+(count++));
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
    public static List<Integer> thirdSelectByUserIdAndList(Integer userId,List<Integer> moviesIdList,Integer threshold,List<Integer> recList) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        //遍历推荐列表，计算出各C类的初始化权重,即各C类有多少个
        String tempMovieKind;
        List<Integer> C1List = new ArrayList<>();
        List<Integer> C2List = new ArrayList<>();
        List<Integer> C3List = new ArrayList<>();
        List<Integer> C4List = new ArrayList<>();
        List<Integer> C5List = new ArrayList<>();
        List<Integer> C6List = new ArrayList<>();
        List<Integer> C7List = new ArrayList<>();
        List<Integer> C8List = new ArrayList<>();
        List<Integer> C9List = new ArrayList<>();
        List<Integer> C10List = new ArrayList<>();
        List<Integer> C11List = new ArrayList<>();
        List<Integer> C12List = new ArrayList<>();
        List<Integer> C13List = new ArrayList<>();
        List<Integer> C14List = new ArrayList<>();
        List<Integer> C15List = new ArrayList<>();
        List<Integer> C16List = new ArrayList<>();
        List<Integer> C17List = new ArrayList<>();
        List<Integer> C18List = new ArrayList<>();
        //TOP-N
        int n = 1;
        for(Integer movieId : moviesIdList){
            //取top-N列表初始化权限列表
            if(n > N){
                break;
            }
            n ++;
            //查询该电影类型
            tempMovieKind = util.selectMovieKindByMovieId(movieId);
            //根据C类别分类
            switch (tempMovieKind){
                case "Action":
                    C1List.add(movieId);
                    break;
                case "Adventure":
                    C2List.add(movieId);
                    break;
                case "Animation":
                    C3List.add(movieId);
                    break;
                case "Children's":
                    C4List.add(movieId);
                    break;
                case "Comedy":
                    C5List.add(movieId);
                    break;
                case "Crime":
                    C6List.add(movieId);
                    break;
                case "Documentary":
                    C7List.add(movieId);
                    break;
                case "Drama":
                    C8List.add(movieId);
                    break;
                case "Fantasy":
                    C9List.add(movieId);
                    break;
                case "Film-Noir":
                    C10List.add(movieId);
                    break;
                case "Horror":
                    C11List.add(movieId);
                    break;
                case "Musical":
                    C12List.add(movieId);
                    break;
                case "Mystery":
                    C13List.add(movieId);
                    break;
                case "Romance":
                    C14List.add(movieId);
                    break;
                case "Sci-Fi":
                    C15List.add(movieId);
                    break;
                case "Thriller":
                    C16List.add(movieId);
                    break;
                case "War":
                    C17List.add(movieId);
                    break;
                case "Western":
                    C18List.add(movieId);
                    break;
            }
        }
        //初始化C类权重列表
        List<Integer> CWeight = new ArrayList<>();
        CWeight.add(C1List.size());CWeight.add(C2List.size());CWeight.add(C3List.size());
        CWeight.add(C4List.size());CWeight.add(C5List.size());CWeight.add(C6List.size());
        CWeight.add(C7List.size());CWeight.add(C8List.size());CWeight.add(C9List.size());
        CWeight.add(C10List.size());CWeight.add(C11List.size());CWeight.add(C12List.size());
        CWeight.add(C13List.size());CWeight.add(C14List.size());CWeight.add(C15List.size());
        CWeight.add(C16List.size());CWeight.add(C17List.size());CWeight.add(C18List.size());
        //遍历权重列表进行权重均衡
        while (true){
            for(int i = 0 ; i < 18 ; i ++){
                //如果该C类权重大于阈值
                if(CWeight.get(i) > threshold){
                    CWeight.set(CWeight.indexOf(Collections.min(CWeight)),Collections.min(CWeight)+1);
                    CWeight.set(i,CWeight.get(i)-1);
                }
            }
            if(Collections.max(CWeight) - Collections.min(CWeight) <= threshold){
                break;
            }
        }
        //显示释放内存
        C1List = null;C2List = null;C3List = null;
        C4List = null;C5List = null;C6List = null;
        C7List = null;C8List = null;C9List = null;
        C10List = null;C11List = null;C12List = null;
        C13List = null;C14List = null;C15List = null;
        C16List = null;C17List = null;C18List = null;
        //根据权重重新选择，即C1类选CWeight.get(0)个,C2类选CWeight.get(1)个,C3类选CWeight.get(2)个
        //遍历重排序后的推荐列表
        int C1 = 0;int C2 = 0;int C3 = 0;
        int C4 = 0;int C5 = 0;int C6 = 0;
        int C7 = 0;int C8 = 0;int C9 = 0;
        int C10 = 0;int C11 = 0;int C12 = 0;
        int C13 = 0;int C14 = 0;int C15 = 0;
        int C16 = 0;int C17 = 0;int C18 = 0;
        for(Integer movieId : moviesIdList){
            //查询该电影类型
            tempMovieKind = util.selectMovieKindByMovieId(movieId);
            switch (tempMovieKind){
                case "Action":
                    if(C1 < CWeight.get(0)){
                        resultList.add(movieId);
                        C1 ++ ;
                    }
                    break;
                case "Adventure":
                    if(C2 < CWeight.get(1)){
                        resultList.add(movieId);
                        C2 ++ ;
                    }
                    break;
                case "Animation":
                    if(C3 < CWeight.get(2)){
                        resultList.add(movieId);
                        C3 ++ ;
                    }
                    break;
                case "Children's":
                    if(C4 < CWeight.get(3)){
                        resultList.add(movieId);
                        C4 ++ ;
                    }
                    break;
                case "Comedy":
                    if(C5 < CWeight.get(4)){
                        resultList.add(movieId);
                        C5 ++ ;
                    }
                    break;
                case "Crime":
                    if(C6 < CWeight.get(5)){
                        resultList.add(movieId);
                        C6 ++ ;
                    }
                    break;
                case "Documentary":
                    if(C7 < CWeight.get(6)){
                        resultList.add(movieId);
                        C7 ++ ;
                    }
                    break;
                case "Drama":
                    if(C8 < CWeight.get(7)){
                        resultList.add(movieId);
                        C8 ++ ;
                    }
                    break;
                case "Fantasy":
                    if(C9 < CWeight.get(8)){
                        resultList.add(movieId);
                        C9 ++ ;
                    }
                    break;
                case "Film-Noir":
                    if(C10 < CWeight.get(9)){
                        resultList.add(movieId);
                        C10 ++ ;
                    }
                    break;
                case "Horror":
                    if(C11 < CWeight.get(10)){
                        resultList.add(movieId);
                        C11 ++ ;
                    }
                    break;
                case "Musical":
                    if(C12 < CWeight.get(11)){
                        resultList.add(movieId);
                        C12 ++ ;
                    }
                    break;
                case "Mystery":
                    if(C13 < CWeight.get(12)){
                        resultList.add(movieId);
                        C13 ++ ;
                    }
                    break;
                case "Romance":
                    if(C14 < CWeight.get(13)){
                        resultList.add(movieId);
                        C14 ++ ;
                    }
                    break;
                case "Sci-Fi":
                    if(C15 < CWeight.get(14)){
                        resultList.add(movieId);
                        C15 ++ ;
                    }
                    break;
                case "Thriller":
                    if(C16 < CWeight.get(15)){
                        resultList.add(movieId);
                        C16 ++ ;
                    }
                    break;
                case "War":
                    if(C17 < CWeight.get(16)){
                        resultList.add(movieId);
                        C17 ++ ;
                    }
                    break;
                case "Western":
                    if(C18 < CWeight.get(17)){
                        resultList.add(movieId);
                        C18 ++ ;
                    }
                    break;
            }
        }
        //如果权重选择完后的推荐列表不足N个，取候选列表前几个补
        int resultSize = resultList.size();
        if(resultSize < N){
            for(Integer movieId : recList){
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
     * 读取原始候选列表
     * @param N
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private static Map<Integer, List<Integer>> readCandicacy(Integer N) throws IOException, SQLException {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\candidacyTop"+N+".txt")));
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
}
