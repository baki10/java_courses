package com.bakigoal.oktmo.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ilmir
 * @param <T>
 */
public abstract class Partition<T> {

  private final long code;
  private String name;
  private final Map<Long, T> places;

  public Partition(long code) {
    this.code = code;
    places = new HashMap<>();
  }

  public void addPlace(long key, T t) {
    places.put(key, t);
  }

  public T getPlace(long key) {
    return places.get(key);
  }

  public Map<Long, T> getPlaces() {
    return places;
  }

  public long getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+"{" + "code=" + code + ", name=" + name+"}";
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
    final Partition<?> other = (Partition<?>) obj;
    if (this.code != other.code) {
      return false;
    }
    return true;
  }

}
