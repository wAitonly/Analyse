package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectUtil {
    //数据库连接
    public static Connection connection;
    //连接url
    private static String URL = "jdbc:sqlserver://localhost:1433;database=1M";
    //驱动地址
    private static String DRIVERPATH = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //数据库用户名和密码
    private static String user = "sa";
    private static String password = "admin";
    //静态代码块加载驱动
    static {
        try {
            Class.forName(DRIVERPATH);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得数据库连接
     * @return
     */
    public static Connection getConnection(){
        try {
            connection = DriverManager.getConnection(URL, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
