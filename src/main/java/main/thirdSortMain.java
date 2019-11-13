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

        for(int i = 1; i < 5;i ++){
            N = 5 * i;
            int fileN = (i -1)*5 + 10;
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
                FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\100K\\newsort\\resultThirdSortTop"+N+".txt", true);
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
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\100K\\newsort\\resultAgainSortTop"+N+".txt")));
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
            if(Collections.max(CWeight) - Collections.min(CWeight) > 1){
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
            if(tempMovieKind.equals("Action") && C1 < CWeight.get(0)){
                resultList.add(movieId);
                C1 ++ ;
            }else if(tempMovieKind.equals("Adventure") && C2 < CWeight.get(1)){
                resultList.add(movieId);
                C2 ++ ;
            }else if(tempMovieKind.equals("Animation") && C3 < CWeight.get(2)){
                resultList.add(movieId);
                C3 ++ ;
            }else if(tempMovieKind.equals("Children's") && C4 < CWeight.get(4)){
                resultList.add(movieId);
                C4 ++ ;
            }else if(tempMovieKind.equals("Comedy") && C5 < CWeight.get(5)){
                resultList.add(movieId);
                C5 ++ ;
            }else if(tempMovieKind.equals("Crime") && C6 < CWeight.get(6)){
                resultList.add(movieId);
                C6 ++ ;
            }else if(tempMovieKind.equals("Documentary") && C7 < CWeight.get(7)){
                resultList.add(movieId);
                C7 ++ ;
            }else if(tempMovieKind.equals("Drama") && C8 < CWeight.get(8)){
                resultList.add(movieId);
                C8 ++ ;
            }else if(tempMovieKind.equals("Fantasy") && C9 < CWeight.get(9)){
                resultList.add(movieId);
                C9 ++ ;
            }else if(tempMovieKind.equals("Film-Noir") && C10 < CWeight.get(10)){
                resultList.add(movieId);
                C10 ++ ;
            }else if(tempMovieKind.equals("Horror") && C11 < CWeight.get(11)){
                resultList.add(movieId);
                C11 ++ ;
            }else if(tempMovieKind.equals("Musical") && C12 < CWeight.get(12)){
                resultList.add(movieId);
                C12 ++ ;
            }else if(tempMovieKind.equals("Mystery") && C13 < CWeight.get(13)){
                resultList.add(movieId);
                C13 ++ ;
            }else if(tempMovieKind.equals("Romance") && C14 < CWeight.get(14)){
                resultList.add(movieId);
                C14 ++ ;
            }else if(tempMovieKind.equals("Sci-Fi") && C15 < CWeight.get(15)){
                resultList.add(movieId);
                C15 ++ ;
            }else if(tempMovieKind.equals("Thriller") && C16 < CWeight.get(16)){
                resultList.add(movieId);
                C16 ++ ;
            }else if(tempMovieKind.equals("War") && C17 < CWeight.get(17)){
                resultList.add(movieId);
                C17 ++ ;
            }else if(tempMovieKind.equals("Western") && C18 < CWeight.get(18)){
                resultList.add(movieId);
                C18 ++ ;
            }
        }
        return resultList;

    }



}
