import com.mysql.jdbc.Connection;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) throws IOException, SQLException {
        String JDBC_URL = "jdbc:mysql://localhost:3306/test";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "password";
// 获取连接:
        Connection conn = (Connection) DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
// TODO: 访问数据库...
// 关闭连接:
        conn.close();
        Server server=new Server();
        server.init();
    }
}
