package com.bakigoal.db.model;

/**
 *
 * @author ilmir
 */
public class Group {
  private final int id;
  private String title;

  public Group(int id) {
    this.id = id;
  }

  public Group(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "Group{" + "id=" + id + ", title=" + title + '}';
  }
  
}
