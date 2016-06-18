package xmlsample;

import xmlsample.model.Street;
import xmlsample.model.Building;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author ilmir
 */
public class StreetXmlHandler implements ContentHandler {

  private boolean isBuilding;
  private boolean isStreet;
  private boolean wayTag;
  private boolean nodeTag;
  private boolean busStopTag;
  private final List<String> busStops = new ArrayList<>();
  private final Map<String, Street> streets = new HashMap<>();
  private String buildingStreet;
  private final List<Building> buildings = new ArrayList<>();
  private String wayId;

  public List<Building> getBuildings() {
    return buildings;
  }

  public Map<String, Street> getStreets() {
    return streets;
  }

  public List<String> getBusStops() {
    return busStops;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    switch (qName) {
      case "node":
        nodeTag = true;
        break;
      case "tag":
        if (nodeTag) {
          if (checkForBusTag(atts)) {
            busStopTag = true;
          }
          if (busStopTag && getTagAttributeValue(atts, "name") != null) {
            String name = getTagAttributeValue(atts, "name");
            busStops.add(name);
          }
        }
        if (wayTag) {
          if (getTagAttributeValue(atts, "highway") != null) {
            isStreet = true;
          }
          if (isStreet) {
            String name = getTagAttributeValue(atts, "name");
            if (name != null) {
              Street street = new Street();
              street.setName(name);
              if (streets.containsKey(name)) {
                street = streets.get(name);
              }
              street.setParts(street.getParts() + 1);
              streets.put(name, street);
            }
          }
          if (getTagAttributeValue(atts, "building") != null) {
            isBuilding = true;
          }
          if (buildingStreet == null) {
            buildingStreet = getTagAttributeValue(atts, "addr:street");
          }
        }
        break;
      case "way":
        wayTag = true;
        wayId = getAttributeValue(atts, "id");
        break;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    switch (qName) {
      case "node":
        busStopTag = false;
        nodeTag = false;
        break;
      case "way":
        wayTag = false;
        isStreet = false;
        if (isBuilding) {
          if (buildingStreet != null) {
            buildings.add(new Building(wayId, buildingStreet));
          }
          isBuilding = false;
          buildingStreet = null;
        }
        break;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void setDocumentLocator(Locator locator) {

  }

  @Override
  public void startDocument() throws SAXException {

  }

  @Override
  public void endDocument() throws SAXException {
    if (!buildings.isEmpty()) {
      buildings.stream().forEach((building) -> {
        String streetName = building.getStreet();
        if (streets.containsKey(streetName)) {
          Street street = streets.get(streetName);
          street.setHouses(street.getHouses() + 1);
        } else {
          System.err.println("No srteet with name: " + streetName + ", building with id = " + building.getId());
        }
      });
    }
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {

  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {

  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {

  }

  @Override
  public void skippedEntity(String name) throws SAXException {

  }

  private boolean checkForBusTag(Attributes atts) {
    int n = atts.getLength();
    if (n > 0) {
      boolean isKHighway = false;
      boolean isVBusStop = false;
      for (int i = 0; i < n; i++) {
        String attrName = atts.getLocalName(i);
        String attrValue = atts.getValue(i);
        if ("k".equals(attrName) && "highway".equals(attrValue)) {
          isKHighway = true;
        } else if ("v".equals(attrName) && "bus_stop".equals(attrValue)) {
          isVBusStop = true;
        }
      }
      if (isKHighway && isVBusStop) {
        return true;
      }
    }
    return false;
  }

  private String getTagAttributeValue(Attributes atts, String key) {
    int n = atts.getLength();
    if (n > 0) {
      boolean isKName = false;
      String value = null;
      for (int i = 0; i < n; i++) {
        String attrName = atts.getLocalName(i);
        String attrValue = atts.getValue(i);
        if ("k".equals(attrName) && key.equals(attrValue)) {
          isKName = true;
        } else if ("v".equals(attrName)) {
          value = attrValue;
        }
      }
      if (isKName) {
        return value;
      }
    }
    return null;
  }

  private String getAttributeValue(Attributes atts, String key) {
    int n = atts.getLength();
    if (n > 0) {
      for (int i = 0; i < n; i++) {
        String attrName = atts.getLocalName(i);
        String attrValue = atts.getValue(i);
        if (key.equals(attrName)) {
          return attrValue;
        }
      }
    }
    return null;
  }

}
