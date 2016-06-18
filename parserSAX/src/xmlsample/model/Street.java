package xmlsample.model;

/**
 *
 * @author ilmir
 */
public class Street {

  private String name;
  private int parts;
  private int houses;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getParts() {
    return parts;
  }

  public void setParts(int parts) {
    this.parts = parts;
  }

  public int getHouses() {
    return houses;
  }

  public void setHouses(int houses) {
    this.houses = houses;
  }

  @Override
  public String toString() {
    return "{parts=" + parts + ", houses=" + houses + '}';
  }
}
