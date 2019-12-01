package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 批量执行SQL
 */
public class BatchExcuteUtil {
    //批量执行
    private static PreparedStatement preparedStatement;
    //数据库连接
    private static Connection connection;

    //查询影评信息
    private static final String selectRatings = "select UserID,MovieID,Rating from ratings";

    //查询影评信息
    private static final String selectAllRatings = "select UserID,MovieID,Rating from allratings";

    //查询影评信息
    private static final String selectBaseRatings = "select UserID,MovieID,Rating from baseratings";

    //查询有影评的用户总数
    private static final String selectUserCount = "select count(distinct UserID) as num from ratings";

    //查询电影信息
    //private static final String selectMovies = "select MovieID,Title,Genres from movies";
    private static final String selectMovies = "  select distinct m.MovieID,m.Title,m.Genres from movies m join baseratings br on br.MovieID = m.MovieID";

    //查询电影根据用户id
    private static final String selectMoviesByUserId = "select MovieID from ratings where UserID = ";

    //查询电影根据用户id
    private static final String selectMoviesByUserIdAll = "select MovieID from allratings where UserID = ";

    //查询电影根据用户id
    private static final String selectMoviesByUserIdBase = "select MovieID from baseratings where UserID = ";

    //查询用户id根据电影
    private static final String selectUsersByMovieId = "select UserID from ratings where MovieID = ";

    //查询用户id根据电影
    private static final String selectUserByMovieIdAll = "select UserID from allratings where MovieID = ";



    //构造方法初始化连接
    public BatchExcuteUtil(Connection connection){
        this.connection = connection;
    }



    /**
     * 查询影评信息
     * @return
     */
    public PreparedStatement selectRatings() throws SQLException {
        preparedStatement = connection.prepareStatement(selectRatings);
        return preparedStatement;
    }

    /**
     * 查询影评信息
     * @return
     */
    public PreparedStatement selectAllRatings() throws SQLException {
        preparedStatement = connection.prepareStatement(selectAllRatings);
        return preparedStatement;
    }

    /**
     * 查询影评信息
     * @return
     */
    public PreparedStatement selectBaseRatings() throws SQLException {
        preparedStatement = connection.prepareStatement(selectBaseRatings);
        return preparedStatement;
    }

    /**
     * 查询电影信息
     * @return
     */
    public PreparedStatement selectMovies() throws SQLException {
        preparedStatement = connection.prepareStatement(selectMovies);
        return preparedStatement;
    }

    /**
     * 查询有影评的用户总数
     * @return
     */
    public PreparedStatement selectUserCount() throws SQLException {
        preparedStatement = connection.prepareStatement(selectUserCount);
        return preparedStatement;
    }

    /**
     * 查询电影根据用户id
     * @return
     */
    public PreparedStatement selectMoviesByUserIdAll(Integer userId) throws SQLException {
        preparedStatement = connection.prepareStatement(selectMoviesByUserIdAll+userId);
        return preparedStatement;
    }

    /**
     * 查询电影根据用户id
     * @return
     */
    public PreparedStatement selectMoviesByUserId(Integer userId) throws SQLException {
        preparedStatement = connection.prepareStatement(selectMoviesByUserId+userId);
        return preparedStatement;
    }

    /**
     * 查询电影根据用户id
     * @return
     */
    public PreparedStatement selectMoviesByUserIdBase(Integer userId) throws SQLException {
        preparedStatement = connection.prepareStatement(selectMoviesByUserIdBase+userId);
        return preparedStatement;
    }

    /**
     * //查询用户id根据电影
     * @return
     */
    public PreparedStatement selectUsersByMovieId(Integer movieId) throws SQLException {
        preparedStatement = connection.prepareStatement(selectUsersByMovieId+movieId);
        return preparedStatement;
    }

    /**
     * //查询用户id根据电影
     * @return
     */
    public PreparedStatement selectUserByMovieIdAll(Integer movieId) throws SQLException {
        preparedStatement = connection.prepareStatement(selectUserByMovieIdAll+movieId);
        return preparedStatement;
    }

    //------------------------------------------------

    /**
     * 根据用户id查询出该用户评论过点电影的类型列表
     * @param userId
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectMovieKindByUserId(Integer userId) throws SQLException{
        String str = " select"
                   + " m.Genres"
                   + " from movies m"
                   + " left join ratings r on m.MovieID = r.MovieID"
                   + " where r.UserID = " + userId;
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据用户id查询出该用户评论过点电影的类型列表
     * @param userId
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectMovieKindByUserIdBase(Integer userId) throws SQLException{
        String str = " select"
                + " m.Genres"
                + " from movies m"
                + " left join baseratings r on m.MovieID = r.MovieID"
                + " where r.UserID = " + userId;
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据电影id查询电影类型
     * @param movieId
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectMovieKindByMovieId(Integer movieId) throws SQLException{
        String str = " select"
                + " Genres"
                + " from movies "
                + " where MovieId = " + movieId;
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据电影id获取该电影的流行度，即该电影被多少人评分过
     * @param movieId
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectRageByMovieId(Integer movieId) throws SQLException{
        String str = " select "
                + " count(MovieId) as rage"
                + " from ratings "
                + " where MovieId = " + movieId;
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据电影id获取该电影的流行度，即该电影被多少人评分过
     * @param movieId
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectRageByMovieIdBase(Integer movieId) throws SQLException{
        String str = " select "
                + " count(MovieId) as rage"
                + " from baseratings "
                + " where MovieId = " + movieId;
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据电影名称查询电影id
     * @param nameList
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectMovieIdByMovieName(List<String> nameList) throws SQLException{
        String str = " select "
                + " MovieID"
                + " from movies "
                + " where Title in(";
        for(String name : nameList){
            if(name.contains("'")){
                name = name.replace("'","''");
            }
            str += "'"+name+"',";
        }
        str = str.substring(0,str.length()-1);
        str += ")";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    /**
     * 根据电影id查询电影名称
     * @param idList
     * @return
     * @throws SQLException
     */
    public PreparedStatement selectMovieNameByMovieId(List<Integer> idList) throws SQLException{
        String str = " select "
                + " Title"
                + " from movies "
                + " where MovieID in(";
        for(Integer id : idList){
            str += id+",";
        }
        str = str.substring(0,str.length()-1);
        str += ")";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }


    public PreparedStatement selectUserCountMovie() throws SQLException{
        String str = "SELECT count(UserID) AS num,UserID from allratings group by UserID order by num desc";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    public PreparedStatement selectMovieCountRating() throws SQLException{
        String str = "SELECT count(MovieID) AS num,MovieID from allratings group by MovieID order by num desc";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    public PreparedStatement selectBaseUser() throws SQLException{
        String str = "SELECT UserID from baseratings group by UserID";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }

    public PreparedStatement selectAllUser() throws SQLException{
        String str = "SELECT UserID from allratings group by UserID";
        preparedStatement = connection.prepareStatement(str);
        return preparedStatement;
    }


}
