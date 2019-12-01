package main;

import dao.InfoGetUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class againSortMainKmeans5 {

    private static Map<Integer,List<Integer>> KindMovieMap;
    private static Map<Integer,Integer> movieKindMap;
    private static List<Integer> movieKind;
    private static InfoGetUtil util = new InfoGetUtil();
    static {
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

    public static void main(String[] args) throws SQLException, IOException {
        int N = 5;
        for(int i = 1; i < 5; i++){
            N = ((i -1)*5 + 10)*3;
            actionAfterSort(N);
        }

        //actionBeforeSort();
    }


    /**
     * 处理排序后的数据
     * 将1M重排序为resultAgainSort，再权重选择得到resultThirdSortTopN
     * 输出resultAgainSort
     */
    private static void actionAfterSort(Integer N) throws IOException, SQLException {
        Map<Integer, List<Integer>> recommentMap = readFile(N);
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = recommentMap.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> tempEntry;
        List<Integer> tempRecommentList;
        List<Integer> tempResultRecommentList;
        Integer tempUserId;
        Integer size = recommentMap.size();
        Integer i = 0;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            //该用户id
            tempUserId = tempEntry.getKey();
            //该用户的推荐列表,目的给它重排序后塞回map
            tempRecommentList = tempEntry.getValue();
            //重排序后
            tempResultRecommentList = order(tempRecommentList,tempUserId);
            recommentMap.put(tempUserId,tempResultRecommentList);
            System.out.println("正在进行Top"+N+"文件处理，共"+size+"个用户的推荐列表需要重排序,已完成"+(i++));
        }
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\sort\\resultAgainSortTop"+N+".txt", true);
        Set set = recommentMap.entrySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            str.append(entry.getKey() + " : " + entry.getValue()).append("\n");
        }
        fw.write(str.toString());
        fw.close();
        recommentMap = null;
    }

    /**
     * 处理排序前的数据,不排序选择
     * 即分别直接输出top5,10,15,20的候选列表为resultThirdSortTopN
     */
    private static void actionBeforeSort() throws IOException, SQLException {
        String testStr = new InfoGetUtil().selectMovieKindByMovieId(2);
        System.out.println(testStr);
        for(int i = 1; i <= 1;i ++) {
            int N = 5 * i;
            //读排序前
            Map<Integer, List<Integer>> recommentMap = readFile(N);
            //将结果输出到文件
            StringBuffer str = new StringBuffer();
            FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\unsort\\resultThirdSortTop"+N+".txt", true);
            Set set = recommentMap.entrySet();
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                str.append(entry.getKey() + " : " + entry.getValue()).append("\n");
            }
            fw.write(str.toString());
            fw.close();
            recommentMap = null;
        }
    }

    /*
     * 计算该用户对于K个聚类的C分类情况
     * C1=1 C2=2 C3=3
     * 形如
     * {K1:1,K2:2,K3:1,......}
     */
    private static Map<Integer,Integer> splitKindByUserId(Integer userId) throws SQLException {
        Map<Integer,Integer> userSplitKindC = new HashMap<>();
        //查询出该用户评论过的电影
        List<Integer> movieList = util.selectMoviesByUserIdBase(userId);
        //该用户评论过的总数
        Integer userTotalMark = movieList.size();
        Double userTotalMarkDouble = userTotalMark.doubleValue();
        //遍历用户电影列表和电影类型列表,分别计算出各类型被用户评论了多少次
        Map<Integer,Integer> movieKindMarkCount = new HashMap<>();
        int count;
        for(Integer kindId : movieKind){
            count = 0;
            for(Integer movieId : movieList){
                if(KindMovieMap.get(kindId).contains(movieId)){
                    count ++;
                }
            }
            movieKindMarkCount.put(kindId,count);
        }
        //显示释放movieListt占用内存
        movieList = null;
        //--------根据movieKindMarkCount计算各类型被该用户关注的概率
        Map<Integer,Double> movieKindP = new HashMap<>();
        Iterator<Map.Entry<Integer,Integer>> iterator = movieKindMarkCount.entrySet().iterator();
        Map.Entry<Integer,Integer> tempEntry;
        Integer tempKind;
        Integer tempCount;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempKind = tempEntry.getKey();
            tempCount = tempEntry.getValue();
            movieKindP.put(tempKind,tempCount.doubleValue()/userTotalMarkDouble);
        }
        //显示释放movieKindMarkCount占用内存
        movieKindMarkCount = null;
        //--------利用movieKindP得到该用户对于K个聚类的C分类情况
        //拿到概率list
        Iterator<Map.Entry<Integer,Double>> iteratorP = movieKindP.entrySet().iterator();
        List<Double> Plist = new ArrayList<>();
        Map.Entry<Integer,Double> tempEntryP;
        Double tempP;
        //拿到概率之和，计算平均值，标准差用
        Double sum = 0.0;
        while (iteratorP.hasNext()){
            tempEntryP = iteratorP.next();
            tempP = tempEntryP.getValue();
            sum += tempP;
            Plist.add(tempP);
        }
        int size = Plist.size();
        //--------利用Plist计算
        //1.计算概率平均值
        Double average = sum/size;
        //2.计算标准差
        Double total=0.0;//方差
        for(int i=0;i<size;i++){
            total += (Plist.get(i)-average)*(Plist.get(i)-average);
        }
        //显示释放Plist占用内存
        Plist = null;
        Double standardDeviation = Math.sqrt(total/size);
        //利用每个类的关注概率，平均值和标准差分C类
        //再遍历一遍各类型被该用户关注的概率Map
        Iterator<Map.Entry<Integer,Double>> iteratorPForResult = movieKindP.entrySet().iterator();
        Map.Entry<Integer,Double> tempEntryPForResult;
        Double tempPForResult;
        Integer tempKindName;
        Double tempC;
        //测试集合
        while (iteratorPForResult.hasNext()){
            tempEntryPForResult = iteratorPForResult.next();
            tempPForResult = tempEntryPForResult.getValue();
            tempKindName = tempEntryPForResult.getKey();
            tempC = (tempPForResult - average)/standardDeviation;
            if(tempC > 1){
                //受用户关注类
                userSplitKindC.put(tempKindName,1);
            }else if(tempC <=1 && tempC >= -1) {
                //中间类
                userSplitKindC.put(tempKindName,2);
            }else {
                //不受用户关注类
                userSplitKindC.put(tempKindName,3);
            }
        }
        return userSplitKindC;
    }

    /**
     * 给推荐列表重排序,推荐列表里是电影id
     * 分C类根据关注度排序
     */
    private static List<Integer> order(List<Integer> recommentList,Integer userId) throws SQLException {
        //拿到该用户对于K个聚类的C分类情况
        Map<Integer,Integer> splitKindMap = splitKindByUserId(userId);
        //遍历推荐电影id列表
        Integer tempMovieKind;
        Integer tempC;
        Integer tempRage;
        //遍历推荐列表，被初始排序下的每个推荐标记排序前位置，同时几下每部电影的C类和流行度
        List<Integer> C1List = new ArrayList<>();
        List<Integer> C2List = new ArrayList<>();
        List<Integer> C3List = new ArrayList<>();
        Map<Integer,Integer> movieRage = new HashMap<>();
        Map<Integer,Integer> startSort = new HashMap<>();
        int sort = 1;
        for(Integer movieId : recommentList){
            startSort.put(movieId,sort);
            sort ++;
            //查询该电影类型
            tempMovieKind = movieKindMap.get(movieId);
            //获取该电影的C型号
            tempC = splitKindMap.get(tempMovieKind);
            if(null == tempC){
                C3List.add(movieId);
            }else {
                //根据C类别分类
                switch (tempC){
                    case 1:
                        C1List.add(movieId);
                        break;
                    case 2:
                        C2List.add(movieId);
                        break;
                    case 3:
                        C3List.add(movieId);
                        break;
                }
            }
            //获取该电影的流行度，即该电影被多少人评分过
            tempRage =util.selectRageByMovieIdBase(movieId);
            movieRage.put(movieId,tempRage);
        }
        //显示释放splitKindMap占用内存
        splitKindMap = null;
        //开始重排序
        //给三个C类别list分别重排序
        //C1流行性倒排序，即流行度越高的越往后,大小表现为升序
        //先拿到排序之前每个位置的对应sort
        List<Integer> sortC1 = new ArrayList<>();
        for(Integer movieId : C1List){
            sortC1.add(startSort.get(movieId));
        }
        int sizeC1 = C1List.size();
        Integer tempMovieIda;
        Integer tempMovieIdb;
        //----新start----
        Integer tempindex;
        Integer tempKinda;
        Integer tempKindb;
        //----新end----
        for(int i = 0 ; i < sizeC1-1 ; i ++){
            for(int j = 0 ; j < sizeC1-1-i ; j ++){
                tempMovieIda = C1List.get(j);
                tempMovieIdb = C1List.get(j+1);
                //----新start----
                tempindex = j+1;
                tempKinda = movieKindMap.get(tempMovieIda);
                for(int a = j+1;a < sizeC1-1-i;a++){
                    tempKindb = movieKindMap.get(C1List.get(a));
                    if(tempKinda == tempKindb){
                        tempMovieIdb = C1List.get(a);
                        tempindex = a;
                        break;
                    }
                }
                //遍历之后仍然找不到同类型的电影,即tempindex没变
                if(tempindex == j+1 && !(movieKindMap.get(tempMovieIda) == movieKindMap.get(C1List.get(j+1)))){
                    continue;
                }
                //----新end----
                if(movieRage.get(tempMovieIda) > movieRage.get(tempMovieIdb)){
                    C1List.set(j,tempMovieIdb);
                    //----新start----
                    C1List.set(tempindex,tempMovieIda);
                    //----新end----
                    //C1List.set(j+1,tempMovieIda);
                }
            }
            //System.out.println("正在排序C1,共"+sizeC1+"次循环,已完成"+i);
        }
        //移植两个list对应位置上的sort
        for (int i = 0 ; i < sizeC1 ; i ++){
            startSort.put(C1List.get(i),sortC1.get(i));
        }
        //显示释放C1List和sortC1占用内存
        C1List = null;
        sortC1 = null;
        //C2顺序不变
        //C3流行性正排序，即流行度越高的越往前,大小表现为降序
        //先拿到排序之前每个位置的对应sort
        List<Integer> sortC3 = new ArrayList<>();
        for(Integer movieId : C3List){
            sortC3.add(startSort.get(movieId));
        }
        int sizeC3 = C3List.size();
        Integer tempMovieIdaC3;
        Integer tempMovieIdbC3;
        //----新start----
        Integer tempindexC3;
        Integer tempKindaC3;
        Integer tempKindbC3;
        //----新end----
        for(int i = 0 ; i < sizeC3-1 ; i ++){
            for(int j = 0 ; j < sizeC3-1-i ; j ++){
                tempMovieIdaC3 = C3List.get(j);
                tempMovieIdbC3 = C3List.get(j+1);
                //----新start----
                tempindexC3 = j+1;
                tempKindaC3 = movieKindMap.get(tempMovieIdaC3);
                for(int a = j+1;a < sizeC3-1-i;a++){
                    tempKindbC3 = movieKindMap.get(C3List.get(a));
                    if(tempKindaC3 == tempKindbC3){
                        tempMovieIdbC3 = C3List.get(a);
                        tempindexC3 = a;
                        break;
                    }
                }
                //遍历之后仍然找不到同类型的电影,即tempindex没变
                if(tempindexC3 == j+1 && !(movieKindMap.get(tempMovieIdaC3) == movieKindMap.get(C3List.get(j+1)))){
                    continue;
                }
                //----新end----
                if(movieRage.get(tempMovieIdaC3) < movieRage.get(tempMovieIdbC3)){
                    C3List.set(j,tempMovieIdbC3);
                    //----新start----
                    C3List.set(tempindexC3,tempMovieIdaC3);
                    //----新end----
                    //C3List.set(j+1,tempMovieIdaC3);
                }
            }
            //System.out.println("正在排序C3,共"+sizeC3+"次循环,已完成"+i);
        }
        //移植两个list对应位置上的sort
        for (int i = 0 ; i < sizeC3 ; i ++){
            startSort.put(C3List.get(i),sortC3.get(i));
        }
        //显示释放C3List和movieRage和sortC3占用内存
        C3List = null;
        movieRage =null;
        sortC3 = null;
        //给recommentList重排序,根据每个位置上的sort升序
        int recommentSize = recommentList.size();
        Integer tempRecMovIda;
        Integer tempRecMovIdb;
        for(int i = 0 ; i < recommentSize-1 ; i ++){
            for(int j = 0 ; j < recommentSize-1-i ; j ++){
                tempRecMovIda = recommentList.get(j);
                tempRecMovIdb = recommentList.get(j+1);
                if(startSort.get(tempRecMovIda) > startSort.get(tempRecMovIdb)){
                    recommentList.set(j,tempRecMovIdb);
                    recommentList.set(j+1,tempRecMovIda);
                }
            }
            //System.out.println("正在给推荐列表重排序,共"+recommentSize+"次循环,已完成"+i);
        }
        //显示释放startSort占用内存
        startSort = null;
        //---------------------------------第一次排序结束-----------------------开始第二次权重选择-----------
        return recommentList;
    }

    private static Map<Integer, List<Integer>> readFile(Integer N) throws IOException, SQLException {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br;
        if(N == 0){
            br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\candidacyTop50.txt")));
        }else {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\candidacyTop"+N+".txt")));
        }
        String data;
        Integer tempUserId;
        List<String> tempList;
//        while((data = br.readLine())!=null){
//            data.trim().replace(" ","");
//            if(data.contains("rating")){
//                tempUserId = Integer.valueOf(data.substring(data.indexOf("user:")+5,data.indexOf("film:")-3).trim());
//                tempMovieName = data.substring(data.indexOf("film:")+5,data.indexOf("rating:")-3).trim();
//                if(resultMapTemp.containsKey(tempUserId)){
//                    resultMapTemp.get(tempUserId).add(tempMovieName);
//                }else {
//                    tempList = new ArrayList<>();
//                    tempList.add(tempMovieName);
//                    resultMapTemp.put(tempUserId,tempList);
//                }
//            }
//            //显示释放tempList占用内存
//            tempList = null;
//        }
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
}
