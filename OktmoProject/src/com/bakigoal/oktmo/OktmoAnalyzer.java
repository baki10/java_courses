package com.bakigoal.oktmo;

import com.bakigoal.oktmo.model.District;
import com.bakigoal.oktmo.model.Place;
import com.bakigoal.oktmo.model.Region;
import com.bakigoal.oktmo.model.Settlement;
import java.util.HashMap;
import java.util.Map;

/**
 * просматривает объекты в
 *
 * @see OktmoData и проводит анализ
 *
 * @author ilmir
 */
public class OktmoAnalyzer {

  public static String findMostPopularPlaceName(long regionCode, OktmoData data) {
    Region region = data.findRegion(regionCode);

    if (region == null) {
      return null;
    }
    Map<String, Integer> names = new HashMap<>();
    String maxName = "";
    int max = 0;

    Map<Long, District> districts = region.getPlaces();
    for (District d : districts.values()) {
      Map<Long, Settlement> settlements = d.getPlaces();
      for (Settlement s : settlements.values()) {
        Map<Long, Place> places = s.getPlaces();
        for (Place p : places.values()) {
          String name = p.getName();
          if (!names.containsKey(name)) {
            names.put(name, 1);
          } else {
            Integer counter = names.get(name);
            names.put(name, ++counter);
            if (counter > max) {
              maxName = name;
              max = counter;
            }
          }
        }
      }
    }
    return maxName + " " + max;
  }

  public static String findDistrictWithMaxPlaces(long regionCode, OktmoData data) {
    Region region = data.findRegion(regionCode);
    if (region == null) {
      return null;
    }
    District maxDistrict = null;
    int maxSize = 0;

    Map<Long, District> districts = region.getPlaces();
    for (District d : districts.values()) {
      int districtPlaces = 0;
      Map<Long, Settlement> settlements = d.getPlaces();
      for (Settlement s : settlements.values()) {
        Map<Long, Place> places = s.getPlaces();
        int size = places.size();
        districtPlaces += size;
      }
      if (maxSize < districtPlaces) {
        maxDistrict = d;
        maxSize = districtPlaces;
      }
    }
    if (maxDistrict != null) {
      return "район с наибольшим количеством населённых пунктов(" + maxSize + "): " + maxDistrict;
    }
    return null;
  }

  public static String displaySummary(long regionCode, OktmoData data) {
    Region region = data.findRegion(regionCode);
    if (region == null) {
      return null;
    }
    Map<String, Integer> statuses = new HashMap<>();
    Map<Long, District> districts = region.getPlaces();
    for (District d : districts.values()) {
      Map<Long, Settlement> settlements = d.getPlaces();
      for (Settlement s : settlements.values()) {
        Map<Long, Place> places = s.getPlaces();
        for (Place p : places.values()) {
          if (!statuses.containsKey(p.getStatus())) {
            statuses.put(p.getStatus(), 1);
          } else {
            Integer count = statuses.get(p.getStatus());
            statuses.put(p.getStatus(), ++count);
          }
        }
      }
    }
    StringBuilder sb = new StringBuilder(region.getName()).append("-->");
    for (String key : statuses.keySet()) {
      sb.append(key).append(":").append(statuses.get(key)).append("; ");
    }
    return sb.toString();
  }

}
