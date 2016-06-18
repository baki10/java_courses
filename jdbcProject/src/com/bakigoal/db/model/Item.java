package com.bakigoal.db.model;

/**
 *
 * @author ilmir
 */
public class Item {
  private final int id;
  private String title;
  private int groupId;

  public Item(int id) {
    this.id = id;
  }

  public Item(int id, String title, int groupId) {
    this.id = id;
    this.title = title;
    this.groupId = groupId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "Item{" + "id=" + id + ", title=" + title + ", groupId=" + groupId + '}';
  }
  
}
