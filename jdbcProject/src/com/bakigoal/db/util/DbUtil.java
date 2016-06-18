package com.bakigoal.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ilmir
 */
public class DbUtil {
  // JDBC driver name and database URL

  private static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
  private static final String DB_URL = "jdbc:derby://localhost:1527/groupdb";

  //  Database credentials
  static final String USER = "app";
  static final String PASSWORD = "app";

  public static Connection getDbConnection() {
    Connection conn = null;
    try {
      //STEP 1: Register JDBC driver
      Class.forName(JDBC_DRIVER);
      //STEP 2: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    } catch (ClassNotFoundException | SQLException ex) {
      System.err.println("Error connecting: " + ex);
    }
    return conn;
  }
}
