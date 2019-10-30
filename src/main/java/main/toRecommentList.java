package main;

import core.CoreAlgorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 輸入推薦列表
 */
public class toRecommentList {
    private static CoreAlgorithm coreAlgorithm = new CoreAlgorithm();
    public static void main(String[] args) throws SQLException, IOException {
        Map<Integer, List<Integer>> recommendListMap =coreAlgorithm.buildRecommendList();
        //輸出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\recommentList.txt", true);
        Set set = recommendListMap.entrySet();
        Iterator iter = set.iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            str.append(entry.getKey()+" : "+entry.getValue()).append("\n");
        }
        fw.write(str.toString());
        fw.close();
    }
}
