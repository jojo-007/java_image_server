package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL="jdbc:mysql://127.0.0.1:3306/java_image_server?characterEncoding=utf8&useSSL=true";
    private static final String USERNAME="root";
    private static final String PASSWORD="root";
     //加上Volatile关键字使得操作的内存可见
    private static volatile DataSource dataSource=null;

    public static DataSource getDataSource() {
        //通过这个方法来创建DataSource的实例
        if (dataSource == null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    MysqlDataSource temDataSource=(MysqlDataSource) dataSource;
                    temDataSource.setURL(URL);
                    temDataSource.setUser(USERNAME);
                    temDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection() {
        try {
            return  getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return null;
    }
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
        try {
            if(resultSet!=null){
                resultSet.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


