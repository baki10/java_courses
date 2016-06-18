package com.bakigoal.oktmo.model;

import java.util.Objects;

/**
 *
 * @author ilmir
 */
public class Place {

  private long code;
  private String name;
  private String status;

  public Place(long code) {
    this.code = code;
  }

  public Place(long code, String name, String status) {
    this.code = code;
    this.name = name;
    this.status = status;
  }

  public long getCode() {
    return code;
  }

  public void setCode(long code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Place{" + "code=" + code + ", name=" + name + ", status=" + status + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + (int) (this.code ^ (this.code >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Place other = (Place) obj;
    if (this.code != other.code) {
      return false;
    }
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.status, other.status)) {
      return false;
    }
    return true;
  }

}
