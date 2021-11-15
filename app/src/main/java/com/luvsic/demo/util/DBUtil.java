package com.luvsic.demo.util;

import android.content.Context;

import com.luvsic.demo.bean.AfterServiceHeader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author zyy
 * @since 2021/10/11 08:32
 */
public class DBUtil {
    private static Connection getSQLConnection(String ip, String user, String pwd, String db) {
        Connection con = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String url = "jdbc:jtds:sqlserver://" + ip + ":1433/" + db + ";charset=utf8";
            System.out.println(url + "   " + user + "  " + pwd);
            con = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void genDataSource(Properties properties) {
//        DataSource ds = null;
//        try {
//            ds = DruidDataSourceFactory.createDataSource(properties);
//            System.out.println(ds.getConnection().getMetaData().getUserName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static Properties loadProperties(Context context) {
        Properties properties = new Properties();
        try {
            InputStream in = context.getAssets().open("druid.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    public static <T> T QuerySQL(String sqlQuery, String[] columnName) {
        String result = "";
        List<AfterServiceHeader> afterServiceHeaders = new ArrayList<>();

        try {
            Connection conn = getSQLConnection("ip", "user", "pwd", "db");
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlQuery);
                while (rs.next()) {
                    AfterServiceHeader afterServiceHeader = new AfterServiceHeader(rs);
                    afterServiceHeaders.add(afterServiceHeader);
                }
                rs.close();
                stmt.close();
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (T) afterServiceHeaders;
    }
}
