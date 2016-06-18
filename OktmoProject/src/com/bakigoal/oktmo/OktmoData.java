package com.bakigoal.oktmo;

import com.bakigoal.oktmo.model.District;
import com.bakigoal.oktmo.model.Partition;
import com.bakigoal.oktmo.model.Place;
import com.bakigoal.oktmo.model.Region;
import com.bakigoal.oktmo.model.Settlement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Будет хранить всю считанную информацию (списки) и содержать методы обращения
 * к ним
 *
 * @author ilmir
 */
public class OktmoData {

  private final List<Place> places;
  private final Set<String> allStatuses;
  private List<Place> sortedPlaces;
  private final List<Region> regions;

  public OktmoData() {
    places = new ArrayList<>();
    allStatuses = new HashSet<>();
    regions = new ArrayList<>();
  }

  public void addPlace(Place place) {
    if (place == null) {
      return;
    }
    places.add(place);
    if (place.getStatus() != null) {
      addStatus(place.getStatus());
    }
  }

  public int size() {
    return places.size();
  }

  public Place getPlace(int index) {
    return places.get(index);
  }

  public void addStatus(String status) {
    allStatuses.add(status);
  }

  public Set<String> getAllStatuses() {
    return allStatuses;
  }

  public List<Place> getPlaces() {
    return places;
  }

  public void displayPlaces() {
    displayPlaces(places);
  }

  public void displaySortedPlaces() {
    displayPlaces(getSortedPlaces());
  }

  public void displayPlaces(Collection places) {
    places.stream().forEach(System.out::println);
  }

  public void displayStatuses() {
    allStatuses.stream().forEach(System.out::println);
  }

  public List<Place> getSortedPlaces() {
    if (sortedPlaces == null || sortedPlaces.size() != places.size()) {
      sortedPlaces = new ArrayList<>(places);
      Collections.sort(sortedPlaces, (Place p1, Place p2) -> compare(p1, p2));
    }
    return sortedPlaces;
  }

  private int compare(Place p1, Place p2) {
    if (p1 == null || p2 == null) {
      return -1;
    }
    return p1.getName().compareTo(p2.getName());
  }

  public List<Place> getMatchedPlaces(String pattern) {
    return places.stream()
            .filter(place -> !(place.getName() == null))
            .filter(place -> (OktmoData.checkWithRegExp(place.getName(), pattern)))
            .collect(Collectors.toList());
  }

  public static boolean checkWithRegExp(String placeName, String regExp) {
    Pattern p = Pattern.compile(regExp);
    Matcher m = p.matcher(placeName);
    return m.matches();
  }

  public void associatePlaces() {
    for (Place place : places) {
      Settlement settlement = findSettlement(place);
      if (settlement == null) {
        return;
      }
      settlement.addPlace(place.getCode(), place);
    }
  }

  public void associatePartition(Partition partition) {
    if (partition instanceof Region) {
      regions.add((Region) partition);
    } else if (partition instanceof District) {
      Region region = findRegion(partition.getCode());
      if (region != null) {
        region.addPlace(partition.getCode(), (District) partition);
      }
    } else if (partition instanceof Settlement) {
      District district = findDistrict(partition.getCode());
      if (district != null) {
        district.addPlace(partition.getCode(), (Settlement) partition);
      }
    }
  }

  public Region findRegion(long code) {
    for (Region region : regions) {
      if ((region.getCode() / 1000000) == code / 1000000) {
        return region;
      }
    }
    return null;
  }

  public District findDistrict(long code) {
    Region region = findRegion(code);
    if (region == null) {
      return null;
    }
    Map<Long, District> districts = region.getPlaces();
    for (Long key : districts.keySet()) {
      if (key / 1000 == code / 1000) {
        return districts.get(key);
      }
    }
    return null;
  }

  public Settlement findSettlement(long code) {
    District district = findDistrict(code);
    if (district == null) {
      return null;
    }
    Map<Long, Settlement> map = district.getPlaces();
    for (Long key : map.keySet()) {
      if (key == code) {
        return map.get(key);
      }
    }
    return null;
  }

  public List<Region> getRegions() {
    return regions;
  }

  private Region findRegion(Place place) {
    for (Region region : regions) {
      if (place != null
              && (place.getCode() / 1000000000) * 1000000 == region.getCode()) {
        return region;
      }
    }
    return null;
  }

  private District findDistrict(Place place) {
    Region region = findRegion(place);
    if (region != null) {
      Map<Long, District> map = region.getPlaces();
      for (Long key : map.keySet()) {
        if ((place.getCode() / 1000000) * 1000 == key) {
          return map.get(key);
        }
      }
    }
    return null;
  }

  private Settlement findSettlement(Place place) {
    District district = findDistrict(place);
    if (district != null) {
      Map<Long, Settlement> map = district.getPlaces();
      for (Long key : map.keySet()) {
        if (place.getCode() / 1000 == key) {
          return map.get(key);
        }
      }
    }
    return null;
  }

  public void displaySettlementsWithDistrictCode(long districtCode) {
    District settlement = findDistrict(districtCode);
    if (settlement == null) {
      return;
    }
    Map<Long, Settlement> map = settlement.getPlaces();
    map.values().stream().forEach(s -> {
      Map<Long, Place> m = s.getPlaces();
      m.values().stream().forEach(System.out::println);
    });
  }
}
