package xmlsample.model;

/**
 *
 * @author ilmir
 */
public class Building {

  private final String id;
  private String street;

  public Building(String id, String street) {
    this.id = id;
    this.street = street;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "{" + "id=" + id + ", street=" + street + '}';
  }
  
}
