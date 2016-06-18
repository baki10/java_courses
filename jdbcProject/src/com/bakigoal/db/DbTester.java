package com.bakigoal.db;

import com.bakigoal.db.model.Group;
import com.bakigoal.db.model.Item;
import com.bakigoal.db.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ilmir
 */
public class DbTester {

  private final static String GROUPS_SQL = "SELECT id, title FROM ItemGroup";
  private final static String ITEMS_SQL = "SELECT id, title, groupid FROM Item";
  private final static String GROUP_ID_SELECT
          = "SELECT ID FROM ITEMGROUP WHERE TITLE = ?";
  private final static String ITEMS_IN_GROUP_ID
          = "SELECT id, title, groupid FROM Item where groupid = ?";
  private final static String CREATE_GROUPS = "CREATE TABLE ITEMGROUP ("
          + "ID INTEGER PRIMARY KEY generated always as identity,  "
          + "TITLE VARCHAR(100)  UNIQUE NOT NULL)";
  private final static String CREATE_ITEMS = "CREATE TABLE ITEM ("
          + "ID INTEGER PRIMARY KEY generated always as identity,"
          + "TITLE VARCHAR(100) UNIQUE NOT NULL, "
          + "GROUPID INTEGER,"
          + "FOREIGN KEY (GROUPID) REFERENCES ITEMGROUP(ID) )";
  private final static String INSERT_GROUP
          = "INSERT INTO ITEMGROUP(TITLE) VALUES(?)";
  private final static String INSERT_ITEM
          = "INSERT INTO ITEM(TITLE,GROUPID) VALUES(?,?)";
  private final static String REMOVE_ITEM
          = "DELETE FROM ITEM WHERE TITLE = ? AND GROUPID = ?";
  private final static String FIND_ITEM
          = "SELECT id FROM ITEM WHERE TITLE = ? AND GROUPID = ?";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new DbTester().test();
  }

  private void test() {
    try (Connection conn = DbUtil.getDbConnection()) {
      doWork(conn);
    } catch (SQLException ex) {
      System.err.println("SQL Error: " + ex);
    }
  }

  private void doWork(Connection conn) throws SQLException {
    createTablesIfNeeded(conn);
    populateTables(conn);
    addItemToGroup(conn, "САМСУНГ", "ТЕЛЕФОНЫ");
    addItemToGroup(conn, "САМСУНГ2", "КОМПЬЮТЕРЫ");
    removeItemFromGroup(conn, "САМСУНГ2", "ТЕЛЕФОНЫ");
    System.out.println("---------------------------------");
    viewGroups(conn);
    System.out.println("---------------------------------");
    viewItems(conn);
    System.out.println("---------------------------------");
    int groupId = getGroupId(conn, "ТЕЛЕФОНЫ");
    System.out.println("###Group id with title 'ТЕЛЕФОНЫ': " + groupId);
    System.out.println("---------------------------------");
    System.out.println("###items in group with id = " + groupId);
    viewItemsInGroup(conn, groupId);
    System.out.println("---------------------------------");
    String groupTitle = "КОМПЬЮТЕРЫ";
    System.out.println("###items in group with group = " + groupTitle);
    viewItemsInGroup(conn, groupTitle);
    System.out.println("---------------------------------");
    groupTitle = "ТЕЛЕФОНЫ";
    System.out.println("###items in group with group = " + groupTitle);
    viewItemsInGroup(conn, groupTitle);
    System.out.println("---------------------------------");
    groupTitle = "ТЕЛЕВИЗОРЫ";
    System.out.println("###items in group with group = " + groupTitle);
    viewItemsInGroup(conn, groupTitle);
    System.out.println("---------------------------------");
  }

  private void viewGroups(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      try (ResultSet rs = stmt.executeQuery(GROUPS_SQL)) {
        while (rs.next()) {
          int id = rs.getInt("id");
          String title = rs.getString("title");
          Group group = new Group(id, title);
          System.out.println(group);
        }
      }
    }
  }

  private void viewItems(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      try (ResultSet rs = stmt.executeQuery(ITEMS_SQL)) {
        while (rs.next()) {
          int id = rs.getInt("id");
          String title = rs.getString("title");
          int groupId = rs.getInt("groupid");
          Item item = new Item(id, title, groupId);
          System.out.println(item);
        }
      }
    }
  }

  private int getGroupId(Connection conn, String title) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(GROUP_ID_SELECT)) {
      ps.setString(1, title);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          return rs.getInt("id");
        }
      }
    }
    throw new SQLException("there is no group with title name: " + title);
  }

  private void viewItemsInGroup(Connection conn, int groupId) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(ITEMS_IN_GROUP_ID)) {
      ps.setInt(1, groupId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int id = rs.getInt("id");
          String title = rs.getString("title");
          Item item = new Item(id, title, groupId);
          System.out.println(item);
        }
      }
    }
  }

  private void viewItemsInGroup(Connection conn, String title) throws SQLException {
    int groupId = getGroupId(conn, title);
    viewItemsInGroup(conn, groupId);
  }

  private void createTablesIfNeeded(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      try (ResultSet rs = stmt.executeQuery(GROUPS_SQL)) {
      } catch (SQLException exception) {
        createTable(conn, CREATE_GROUPS);
      }
      try (ResultSet rs = stmt.executeQuery(ITEMS_SQL)) {
      } catch (SQLException exception) {
        createTable(conn, CREATE_ITEMS);
      }
    }
  }

  private void createTable(Connection conn, String sql) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.executeUpdate();
    }
  }

  private void populateTables(Connection conn) throws SQLException {
    addGroup(conn, "КОМПЬЮТЕРЫ");
    addGroup(conn, "ТЕЛЕФОНЫ");
    addGroup(conn, "ТЕЛЕВИЗОРЫ");
    removeItem(conn, "Apple", 1);
    addItem(conn, "Apple", 1);
    removeItem(conn, "Dell", 1);
    addItem(conn, "Dell", 1);
    removeItem(conn, "HTC", 2);
    addItem(conn, "HTC", 2);
    removeItem(conn, "Nokia", 2);
    addItem(conn, "Nokia", 2);
    removeItem(conn, "LG", 3);
    addItem(conn, "LG", 3);
  }

  private boolean addGroup(Connection conn, String title) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_GROUP)) {
      ps.setString(1, title);
      ps.executeUpdate();
      System.out.println("added group: " + title);
    } catch (SQLException e) {
      System.err.println("CAN NOT ADD GROUP:" + title + ", error:" + e);
      return false;
    }
    return true;
  }

  private boolean addItem(Connection conn, String title, int groupId) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_ITEM)) {
      ps.setString(1, title);
      ps.setInt(2, groupId);
      ps.executeUpdate();
      System.out.println("added item: " + title + " to group:" + groupId);
    } catch (SQLException e) {
      System.err.println("CAN NOT ADD ITEM:" + title + ", error:" + e);
      return false;
    }
    return true;
  }

  private boolean addItemToGroup(Connection conn, String itemName, String groupName) throws SQLException {
    int groupId;
    try {
      groupId = getGroupId(conn, groupName);
    } catch (SQLException e) {
      System.err.println("No group with title: " + groupName);
      return false;
    }
    return addItem(conn, itemName, groupId);
  }

  private boolean removeItemFromGroup(Connection conn, String itemName, String groupName) throws SQLException {
    int groupId;
    try {
      groupId = getGroupId(conn, groupName);
    } catch (SQLException e) {
      System.err.println("No group with title: " + groupName);
      return false;
    }

    return removeItem(conn, itemName, groupId);
  }

  private boolean removeItem(Connection conn, String itemName, int groupId) throws SQLException {
    boolean findItem = findItem(conn, itemName, groupId);
    if (!findItem) {
      System.err.println("CAN NOT REMOVE ITEM, Could not find proper item: " + itemName + " in group " + groupId);
      return false;
    }
    try (PreparedStatement ps = conn.prepareStatement(REMOVE_ITEM)) {
      ps.setString(1, itemName);
      ps.setInt(2, groupId);
      int executeUpdate = ps.executeUpdate();

      System.out.println("removed item: " + itemName + " from group:" + groupId);
    } catch (SQLException e) {
      System.err.println("CAN NOT REMOVE ITEM:" + itemName + ", error:" + e);
      return false;
    }
    return true;
  }

  private boolean findItem(Connection conn, String itemName, int groupId) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(FIND_ITEM)) {
      ps.setInt(2, groupId);
      ps.setString(1, itemName);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          System.out.println("found item: " + itemName + " in group: " + groupId);
          return true;
        }
      }
    }
    return false;
  }

}
