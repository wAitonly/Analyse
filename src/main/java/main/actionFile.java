package main;

import Bean.Ratings;
import dao.InfoGetUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * 處理文件
 */
public class actionFile {

    public static void main(String[] args) throws IOException, SQLException {
        //actionRatingsDat();
        //actionMoviesDat();
        //actionuItem();
        //actionRatingsDatFromDB();
        actionRatingsDatFromDB60();
    }

    /**
     * 處理100K里的u.item,使之成為1m里的電影標準格式
     */
    private static void actionuItem() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Python\\data\\ml-100k\\u.txt")));
        String data;
        String lineStr;
        List<String> tempList;
        List<String> resultStrList = new ArrayList<>();
        String tempResultStr;
        while((data = br.readLine())!=null){
            lineStr = data.trim();
            tempList = Arrays.asList(lineStr.split("#"));
            if(tempList.get(0).trim().length() == 0 || tempList.get(1).trim().length() == 0){
                continue;
            }
            tempResultStr = tempList.get(0)+"::"+tempList.get(1)+"::";
            for(int i = 6; i < tempList.size();i++){
                if(tempList.get(i).trim().length() > 0 && Integer.valueOf(tempList.get(i).trim()) == 1){
                    switch (i){
                        case 6:
                            tempResultStr += "Action|";
                            break;
                        case 7:
                            tempResultStr += "Adventure|";
                            break;
                        case 8:
                            tempResultStr += "Animation|";
                            break;
                        case 9:
                            tempResultStr += "Children's|";
                            break;
                        case 10:
                            tempResultStr += "Comedy|";
                            break;
                        case 11:
                            tempResultStr += "Crime|";
                            break;
                        case 12:
                            tempResultStr += "Documentary|";
                            break;
                        case 13:
                            tempResultStr += "Drama|";
                            break;
                        case 14:
                            tempResultStr += "Fantasy|";
                            break;
                        case 15:
                            tempResultStr += "Film-Noir|";
                            break;
                        case 16:
                            tempResultStr += "Horror|";
                            break;
                        case 17:
                            tempResultStr += "Musical|";
                            break;
                        case 18:
                            tempResultStr += "Mystery|";
                            break;
                        case 19:
                            tempResultStr += "Romance|";
                            break;
                        case 20:
                            tempResultStr += "Sci-Fi|";
                            break;
                        case 21:
                            tempResultStr += "Thriller|";
                            break;
                        case 22:
                            tempResultStr += "War|";
                            break;
                        case 23:
                            tempResultStr += "Western|";
                            break;
                        default:
                            tempResultStr += "WARNINGGGGGGGGGG";

                    }
                }
            }
            tempResultStr = tempResultStr.substring(0,tempResultStr.length()-1);
            resultStrList.add(tempResultStr);
        }
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\Python\\data\\ml-100k\\movies.dat", true);
        for(String tempstr : resultStrList){
            str.append(tempstr).append("\n");
        }
        fw.write(str.toString());
        fw.close();
    }

    /**
     * 处理movies.dat和ratings.dat
     * 只输出
     * 前1919个被评论最多的电影为newmovies.txt
     * 前2830个评论电影最多的用户的评分数据为newratings.txt
     */
    private static void actionRatingsDat() throws IOException, SQLException {
        InfoGetUtil util = new InfoGetUtil();
        //处理ratings.dat
        //拿到按前5到2830个评论电影最多的用户
        List<Integer> sortUsers = util.selectUserCountMovie().subList(5,500);
        //拿到按被评论次数排序电影,前1919个
        List<Integer> sortMovies = util.selectMovieCountRating().subList(0,840);
        //读写文件
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Python\\data\\ml-100k\\u.data")));
        String data;
        String lineStr;
        List<String> tempList;
        Set<String> resultStrList = new HashSet<>();
        StringBuffer tempResultStr;
        while((data = br.readLine())!=null){
            tempResultStr = new StringBuffer();
            lineStr = data.trim();
            tempList = Arrays.asList(lineStr.split(" "));
            if(tempList.get(0).trim().length() != 0 && tempList.get(1).trim().length() != 0
                    && sortUsers.contains(Integer.valueOf(tempList.get(0).trim()))
                    && sortMovies.contains(Integer.valueOf(tempList.get(1).trim()))){
                tempResultStr.append(tempList.get(0)).append(" ").append(tempList.get(1)).append(" ").append(tempList.get(2)).append("   ").append(tempList.get(3));
                resultStrList.add(tempResultStr.toString());
            }
        }
        tempList = null;
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\Python\\data\\ml-100k\\ratings.txt", true);
        for(String tempstr : resultStrList){
            str.append(tempstr).append("\n");
        }
        resultStrList = null;
        fw.write(str.toString());
        fw.close();
    }

    /**
     * 处理movies.dat和ratings.dat
     * 只输出
     * 前1919个被评论最多的电影为newmovies.txt
     * 前2830个评论电影最多的用户的评分数据为newratings.txt
     */
    private static void actionMoviesDat() throws IOException, SQLException {
        InfoGetUtil util = new InfoGetUtil();
        //处理movies.dat
        //拿到按被评论次数排序电影,前1919个
        List<Integer> sortMovies = util.selectMovieCountRating().subList(0,840);
        //读写文件
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Python\\data\\ml-100k\\movies.dat")));
        String data;
        String lineStr;
        List<String> tempList;
        List<String> resultStrList = new ArrayList<>();
        while((data = br.readLine())!=null){
            lineStr = data.trim();
            tempList = Arrays.asList(lineStr.split("::"));
            if(tempList.get(0).trim().length() == 0 || tempList.get(1).trim().length() == 0 || !sortMovies.contains(Integer.valueOf(tempList.get(0).trim()))){
                continue;
            }
            resultStrList.add(lineStr);
        }
        tempList = null;
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\Python\\data\\ml-100k\\movies.txt", true);
        for(String tempstr : resultStrList){
            str.append(tempstr).append("\n");
        }
        resultStrList = null;
        fw.write(str.toString());
        fw.close();
    }

    /**
     * 从数据库中读出Allratings
     * 只输出
     * 前1919个被评论最多的电影为newmovies.txt
     * 前2830个评论电影最多的用户的评分数据为newratings.txt
     */
    private static void actionRatingsDatFromDB() throws IOException, SQLException {
        InfoGetUtil util = new InfoGetUtil();
        //处理ratings.dat
        //拿到按前5到2830个评论电影最多的用户
        List<Integer> sortUsers = util.selectUserCountMovie().subList(5,500);
        //拿到按被评论次数排序电影,前1919个
        List<Integer> sortMovies = util.selectMovieCountRating().subList(0,840);
        //从数据库中读出所有评分
        List<Ratings> allratings = new InfoGetUtil().getAllRatings();
        //输出到文件
        Set<String> resultStrList = new HashSet<>();
        StringBuffer tempResultStr;
        for(Ratings rating : allratings){
            tempResultStr = new StringBuffer();
            if(sortUsers.contains(rating.getUserId()) && sortMovies.contains(rating.getMovieId())){
                tempResultStr.append(rating.getUserId()).append(" ")
                             .append(rating.getMovieId()).append(" ")
                             .append(rating.getRating());
                resultStrList.add(tempResultStr.toString());
            }
        }
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\Python\\data\\ml-100k\\ratings.txt", true);
        for(String tempstr : resultStrList){
            str.append(tempstr).append("\n");
        }
        resultStrList = null;
        fw.write(str.toString());
        fw.close();
    }

    /**
     * 从数据库中读出Allratings
     * 只输出
     * 前60%个评论电影最多的用户的评分数据为newratings.txt
     */
    private static void actionRatingsDatFromDB60() throws IOException, SQLException {
        InfoGetUtil util = new InfoGetUtil();
        //处理ratings.dat
        //拿到按前5到2830个评论电影最多的用户
        List<Integer> sortUsers = util.selectUserCountMovie().subList(0,3624);
        //从数据库中读出所有评分
        List<Ratings> allratings = new InfoGetUtil().getAllRatings();
        //输出到文件
        Set<String> resultStrList = new HashSet<>();
        StringBuffer tempResultStr;
        for(Ratings rating : allratings){
            tempResultStr = new StringBuffer();
            if(sortUsers.contains(rating.getUserId())){
                tempResultStr.append(rating.getUserId()).append(" ")
                        .append(rating.getMovieId()).append(" ")
                        .append(rating.getRating());
                resultStrList.add(tempResultStr.toString());
            }
        }
        //将结果输出到文件
        StringBuffer str = new StringBuffer();
        FileWriter fw = new FileWriter("D:\\Python\\data\\ml-1m\\percentratings.txt", true);
        for(String tempstr : resultStrList){
            str.append(tempstr).append("\n");
        }
        resultStrList = null;
        fw.write(str.toString());
        fw.close();
    }


}
