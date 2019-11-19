package main;

import dao.InfoGetUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 论文外的方法一
 * 没有权重选择
 */
public class NoThirdSortMain {

    private static InfoGetUtil util = new InfoGetUtil();

    private static Comparator<Map.Entry<Integer,Integer>> valueComparator;

    static {
        //排序规则
        valueComparator = (o1, o2) -> o2.getValue().compareTo(o1.getValue());
    }

    public static void main(String[] args) {
        try {
            int fileN;
            for(int i = 1; i <= 5; i++){
                fileN = (i -1)*5 + 10;
                assembRecommentList(5,fileN);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装候选列表
     * @return
     */
    public static void assembRecommentList(Integer N,Integer fieldN) throws IOException, SQLException {
        Map<Integer,List<Integer>> resultMap = new HashMap<>();
        //读取文件拿到评分大于Tr的候选列表
        Map<Integer,List<Integer>> beforeList = readBefore(fieldN);
        //对评分大于Tr的候选列表按流行度倒排序，即流行度高的往前排
        Iterator<Map.Entry<Integer,List<Integer>>> iterator = beforeList.entrySet().iterator();
        Map.Entry<Integer,List<Integer>> tempEntry;
        Integer tempUserId;
        List<Integer> tempList;
        List<Integer> tempResultList;
        Integer size = beforeList.size();
        Integer i = 0;
        while (iterator.hasNext()){
            tempEntry = iterator.next();
            tempUserId = tempEntry.getKey();
            tempList = tempEntry.getValue();
            //按流行度倒排序
            tempResultList = order(tempList);
            //将排序好的推荐列表替换原来的推荐列表
            beforeList.put(tempUserId,tempResultList);
            System.out.println("正在进行Field"+fieldN+"Top"+N+"文件处理，共"+size+"个用户的推荐列表需要重排序,已完成"+(i++));
        }
        //读取文件拿到评分小于tr大于th的列表
        Map<Integer,List<Integer>> followList = readFollow(fieldN);
        //读取文件拿到评分小于th的列表
        Map<Integer,List<Integer>> abandonList = readAbandon(fieldN);
        //组装两个list
        List<Integer> resultList;
        List<Integer> tempresultList;
        Set<Integer> userIdList = beforeList.keySet();
        int beforeListSize;
        int followListSize;
        for(Integer userId : userIdList){
            resultList = new ArrayList<>();
            beforeListSize = beforeList.get(userId).size();
            followListSize = followList.get(userId).size();
            if(beforeListSize >= N){
                //如果评分大于Tr的电影数量大于等于N个,直接取满N个
                resultList.addAll(beforeList.get(userId).stream().limit(N).collect(Collectors.toList()));
            }else if(beforeListSize < N && (beforeListSize+followListSize) >= N){
                //如果评分大于Tr的电影数量小于N个，但是before和follow加起来够N个
                resultList.addAll(beforeList.get(userId));
                tempresultList = followList.get(userId).stream().limit(N-beforeListSize).collect(Collectors.toList());
                resultList.addAll(tempresultList);
            }else if(beforeListSize < N && (beforeListSize+followListSize) < N){
                //如果before和follow加起来都不够，只能从abandon中去取
                resultList.addAll(beforeList.get(userId));
                resultList.addAll(followList.get(userId));
                tempresultList = abandonList.get(userId).stream().limit(N-beforeListSize-followListSize).collect(Collectors.toList());
                resultList.addAll(tempresultList);
            }
            resultMap.put(userId,resultList);
        }
        //将结果打印出来
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\nothird\\resultAgainSortTop"+N+"Len"+fieldN+".txt", true);
        Set set = resultMap.entrySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            str.append(entry.getKey() + " : " + entry.getValue()).append("\n");
        }
        fw.write(str.toString());
        fw.close();
    }

    /**
     * 给候选列表按流行度倒排序
     * @param recommentList
     * @return
     */
    private static List<Integer> order(List<Integer> recommentList) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        //电影及其流行度Map
        Map<Integer,Integer> map = new HashMap<>();
        for(Integer movieId : recommentList){
            map.put(movieId,util.selectRageByMovieIdBase(movieId));
        }
        List<Map.Entry<Integer,Integer>> sortList = new ArrayList<>(map.entrySet());
        Collections.sort(sortList,valueComparator);
        Iterator<Map.Entry<Integer,Integer>> iterator = sortList.iterator();
        Map.Entry<Integer,Integer> tempEntryInn;
        while (iterator.hasNext()){
            tempEntryInn = iterator.next();
            resultList.add(tempEntryInn.getKey());
        }
        return resultList;
    }

    /**
     * 读取文件拿到评分大于Tr的候选列表
     * @param fieldN
     * @return
     */
    private static Map<Integer,List<Integer>> readBefore(Integer fieldN) throws IOException {
        //读取文件拿到候选列表
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\noThirdCandidacyTop"+fieldN+".txt")));
        String data;
        Integer tempUserId;
        List<String> tempList;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
            tempList = Arrays.asList(data.substring(data.indexOf("before:[")+8,data.indexOf("];follow")).trim().split(","));
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
            tempMovieIdList = new ArrayList<>();
            for(String str : tempMovieNameList){
                if(str.trim().length() > 0){
                    tempMovieIdList.add(Integer.valueOf(str.trim()));
                }
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
     * 读取文件拿到评分小于Tr大于Th的候选列表
     * @param fieldN
     * @return
     */
    private static Map<Integer,List<Integer>> readFollow(Integer fieldN) throws IOException {
        //读取文件拿到候选列表
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\noThirdCandidacyTop"+fieldN+".txt")));
        String data;
        Integer tempUserId;
        List<String> tempList;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
            tempList = Arrays.asList(data.substring(data.indexOf("follow:[")+8,data.indexOf("];abandon")).trim().split(","));
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
            tempMovieIdList = new ArrayList<>();
            for(String str : tempMovieNameList){
                if(str.trim().length() > 0){
                    tempMovieIdList.add(Integer.valueOf(str.trim()));
                }
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
     * 读取文件拿到评分小于Th的候选列表
     * @param fieldN
     * @return
     */
    private static Map<Integer,List<Integer>> readAbandon(Integer fieldN) throws IOException {
        //读取文件拿到候选列表
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Map<Integer, List<String>> resultMapTemp = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\OldRecommentAlgorithmWithoutAverage\\1M\\noThirdCandidacyTop"+fieldN+".txt")));
        String data;
        Integer tempUserId;
        List<String> tempList;
        while((data = br.readLine())!=null){
            data.trim().replace(" ","");
            tempUserId = Integer.valueOf(data.substring(0,data.indexOf(":")).trim());
            tempList = Arrays.asList(data.substring(data.indexOf("abandon:[")+9,data.length()-1).trim().split(","));
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
            tempMovieIdList = new ArrayList<>();
            for(String str : tempMovieNameList){
                if(str.trim().length() > 0){
                    tempMovieIdList.add(Integer.valueOf(str.trim()));
                }
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
