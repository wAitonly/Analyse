package main;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * 二八分測試集和訓練集
 */
public class splitList {

    public static void main(String[] args) throws IOException, SQLException {
        splitFile();
    }

    private static void splitFile() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\movielens\\ml-10M100K\\ratings.dat")));
        String data;
        String lineStr;
        List<String> tempList = new ArrayList<>();
        while((data = br.readLine())!=null){
            lineStr = data.trim().replace(" ","");
            tempList.add(lineStr);
        }
        List<String> littleList = new ArrayList<>();
        //開始二八分
        int size = tempList.size();
        for(int i = 0 ; i < size ; i ++){
            if(i == 3 || i == 7 || (i-3)%10 == 0 || (i-7)%10 == 0){
                littleList.add(tempList.get(i));
            }
        }
        tempList.removeAll(littleList);
        //打印兩個list
        //输出到文件
        //将结果输出到文件
        StringBuffer bufStrBase = new StringBuffer();
        StringBuffer bufStrTest = new StringBuffer();
        FileWriter fwBase = new FileWriter("D:\\base.txt", true);
        FileWriter fwTest = new FileWriter("D:\\test.txt", true);
        for(String strBase : tempList){
            bufStrBase.append(strBase).append("\n");
        }
        tempList = null;
        fwBase.write(bufStrBase.toString());
        fwBase.close();

        for(String strTest : littleList){
            bufStrTest.append(strTest).append("\n");
        }
        littleList = null;
        fwTest.write(bufStrTest.toString());
        fwTest.close();


    }
}
