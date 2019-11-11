package dao;

import Bean.Movies;
import Bean.Ratings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库中数据获取工具类
 */
public class InfoGetUtil {
    //静态获取数据库连接
    private static Connection connection;
    static {
        connection = new ConnectUtil().getConnection();
    }

    /**
     * 获取电影信息
     * @return
     */
    public static List<Movies> getMovies() throws SQLException {
        List<Movies> list = new ArrayList<>();
        Movies movie;
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovies();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            movie = new Movies();
            movie.setMovieId(resultSet.getInt("MovieID"));
            movie.setTitle(resultSet.getString("Title"));
            movie.setGenres(resultSet.getString("Genres"));
            list.add(movie);
        }
        return list;
    }

    /**
     * 获取影评信息
     * @return
     */
    public static List<Ratings> getAllRatings() throws SQLException {
        List<Ratings> list = new ArrayList<>();
        Ratings rating;
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectAllRatings();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            rating = new Ratings();
            rating.setMovieId(resultSet.getInt("MovieID"));
            rating.setUserId(resultSet.getInt("UserID"));
            rating.setRating(resultSet.getInt("Rating"));
            list.add(rating);
        }
        return list;
    }

    /**
     * 获取影评信息
     * @return
     */
    public static List<Ratings> getRatings() throws SQLException {
        List<Ratings> list = new ArrayList<>();
        Ratings rating;
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectRatings();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            rating = new Ratings();
            rating.setMovieId(resultSet.getInt("MovieID"));
            rating.setUserId(resultSet.getInt("UserID"));
            rating.setRating(resultSet.getInt("Rating"));
            list.add(rating);
        }
        return list;
    }

    /**
     * 获取影评信息
     * @return
     */
    public static List<Ratings> getBaseRatings() throws SQLException {
        List<Ratings> list = new ArrayList<>();
        Ratings rating;
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectBaseRatings();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            rating = new Ratings();
            rating.setMovieId(resultSet.getInt("MovieID"));
            rating.setUserId(resultSet.getInt("UserID"));
            rating.setRating(resultSet.getInt("Rating"));
            list.add(rating);
        }
        return list;
    }

    /**
     * 查询有影评的用户总数
     * @return
     */
    public static int getUserCount() throws SQLException {
        int num = 0;
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectRatings();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            num = resultSet.getInt("num");
        }
        return num;
    }

    /**
     * 查询出该用户评论过的电影id
     * @param userId
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectMoviesByUserId(Integer userId) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMoviesByUserId(userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("MovieID"));
        }
        return resultList;
    }

    /**
     * 查询出该用户评论过的电影id
     * @param userId
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectMoviesByUserIdBase(Integer userId) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMoviesByUserIdBase(userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("MovieID"));
        }
        return resultList;
    }

    /**
     * 查询出评论过指定电影的用户集合
     * @param movieId
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectUsersByMovieId(Integer movieId) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectUsersByMovieId(movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("UserID"));
        }
        return resultList;
    }

    //-------------------------------

    /**
     * 查询出该用户评论过的电影的类型
     * @param userId
     * @return
     * @throws SQLException
     */
    public static List<String> selectMovieKindByUserId(Integer userId) throws SQLException {
        List<String> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieKindByUserId(userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getString("Genres"));
        }
        return resultList;
    }

    /**
     * 查询出该用户评论过的电影的类型
     * @param userId
     * @return
     * @throws SQLException
     */
    public static List<String> selectMovieKindByUserIdBase(Integer userId) throws SQLException {
        List<String> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieKindByUserIdBase(userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getString("Genres"));
        }
        return resultList;
    }


    /**
     * 根据电影id查询电影类型
     * @param movieId
     * @return
     * @throws SQLException
     */
    public static String selectMovieKindByMovieId(Integer movieId) throws SQLException {
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieKindByMovieId(movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        String genres = "";
        while (resultSet.next()){
            genres = resultSet.getString("Genres");
        }
        if(genres.contains("|")){
            return genres.substring(0,genres.indexOf("|"));
        }else {
            return genres;
        }
    }

    /**
     * 根据电影id获取该电影的流行度，即该电影被多少人评分过
     * @param movieId
     * @return
     * @throws SQLException
     */
    public static Integer selectRageByMovieId(Integer movieId) throws SQLException {
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectRageByMovieId(movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer rage = 0;
        while (resultSet.next()){
            rage = resultSet.getInt("rage");
        }
        return rage;
    }

    /**
     * 根据电影id获取该电影的流行度，即该电影被多少人评分过
     * @param movieId
     * @return
     * @throws SQLException
     */
    public static Integer selectRageByMovieIdBase(Integer movieId) throws SQLException {
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectRageByMovieIdBase(movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer rage = 0;
        while (resultSet.next()){
            rage = resultSet.getInt("rage");
        }
        return rage;
    }

    /**
     * 根据电影名称获取电影id
     * @param nameList
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectMovieIdByMovieName(List<String> nameList) throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieIdByMovieName(nameList);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("MovieID"));
        }
        return resultList;
    }

    /**
     * 根据电影id获取电影名称
     * @param idList
     * @return
     * @throws SQLException
     */
    public static List<String> selectMovieNameByMovieId(List<Integer> idList) throws SQLException {
        List<String> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieNameByMovieId(idList);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getString("Title"));
        }
        return resultList;
    }

    /**
     * 获取所有用户
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectBaseUser() throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectBaseUser();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("UserID"));
        }
        return resultList;
    }

    /**
     * 按用户评论次数排序用户
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectUserCountMovie() throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectUserCountMovie();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("UserID"));
        }
        return resultList;
    }

    /**
     * 获取用户
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectUser() throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectUser();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("UserID"));
        }
        return resultList;
    }

    /**
     * 按被评论次数排序电影
     * @return
     * @throws SQLException
     */
    public static List<Integer> selectMovieCountRating() throws SQLException {
        List<Integer> resultList = new ArrayList<>();
        PreparedStatement preparedStatement = new BatchExcuteUtil(connection).selectMovieCountRating();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            resultList.add(resultSet.getInt("MovieID"));
        }
        return resultList;
    }
}
