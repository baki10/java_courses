package com.bakigoal.oktmo;

import com.bakigoal.oktmo.model.District;
import com.bakigoal.oktmo.model.Partition;
import com.bakigoal.oktmo.model.Place;
import com.bakigoal.oktmo.model.Region;
import com.bakigoal.oktmo.model.Settlement;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Считывает текстовые файлы .csv и добавляет их содержимое в разобранном виде в
 *
 * @see OktmoData
 *
 * @author ilmir
 */
public class OktmoReader {

  private Set<String> excludeStatuses = new HashSet<>();

  {
    excludeStatuses.add("Населенные");
    excludeStatuses.add("Муниципальные");
  }

  public void readPlaces(String fileName, OktmoData data) {

    int lineCount = 0;
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(fileName), "utf-8"))) {
      String s;
      while ((s = br.readLine()) != null) {
        lineCount++;
        Place place = convertLineToPlace(s);
        data.addPlace(place);
        if (lineCount == 20) {
          break; // пока частично
        }
      }
    } catch (IOException ex) {
      System.out.println("Reading error in line " + lineCount);
    }
  }

  public void readPlacesExp(String fileName, OktmoData data) {

    int lineCount = 0;
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(fileName), "utf-8"))) {
      String s;
      while ((s = br.readLine()) != null) {
        lineCount++;
        Place place = convertLineToPlaceRegExp(s);
        data.addPlace(place);
      }
    } catch (IOException ex) {
      System.out.println("Reading error in line " + lineCount);
    }
  }

  private Place convertLineToPlace(String s) {
    if (s == null) {
      return null;
    }
    String[] split = s.split(";");
    String codeString = split[0].replace(" ", "");
    if (codeString == null) {
      return null;
    }
    Long code = Long.parseLong(codeString);
    Place place = new Place(code);

    if (split.length >= 3) {
      String statusAndName = split[2];
      if (statusAndName == null || statusAndName.startsWith("Населенные пункты")) {
        return null;
      }
      int spaceIndex = statusAndName.indexOf(" ");
      if (spaceIndex == -1) {
        place.setName(statusAndName);
      } else {
        String status = statusAndName.substring(0, spaceIndex);
        String name = statusAndName.substring(spaceIndex + 1);
        place.setStatus(status);
        place.setName(name);
      }
    }

    return place;
  }

  private Place convertLineToPlaceRegExp(String data) {
    if (data == null) {
      return null;
    }
    Pattern p = Pattern.compile("([0-9\\s]{0,15});([0-9]);([А-Яа-я]+)\\s([\\d\\D\\w\\s\\W]+)");
    Matcher m = p.matcher(data);
    if (m.matches()) {
      String codeString = m.group(1).replaceAll(" ", "");
      if (codeString == null) {
        return null;
      }
      Long code = Long.parseLong(codeString);
      Place place = new Place(code);

      String status = m.group(3);
      if (status == null || excludeStatuses.contains(status)) {
        return null;
      }
      String name = m.group(4);
      place.setStatus(status);
      place.setName(name);

      return place;
    }
    return null;
  }

  public void readPartitions(String fileName, OktmoData data) {
    int lineCount = 0;
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(fileName), "Cp1251"))) {
      String s;
      while ((s = br.readLine()) != null) {
        lineCount++;
        Partition partition = convertLineToPartitionsRegExp(s);
        if (partition != null) {
          data.associatePartition(partition);
        }
      }
    } catch (IOException ex) {
      System.out.println("Reading error in line " + lineCount);
    }
  }

  private Partition convertLineToPartitionsRegExp(String data) {
    if (data == null) {
      return null;
    }
    Pattern p = Pattern.compile("([0-9\\s]{0,15});([0-9]);([\\d\\D\\w\\s\\W]+);([\\d\\D\\w\\s\\W]*)");
    Matcher m = p.matcher(data);
    if (m.matches()) {
      String codeString = m.group(1).replaceAll(" ", "");
      if (codeString == null) {
        return null;
      }
      Long code = Long.parseLong(codeString);
      Partition place;
      if (code % 1000000 == 0) {
        place = new Region(code);
      } else if (code % 1000 == 0 && code % 10000 != 0) {
        place = new District(code, m.group(4));
      } else if (code % 1000 != 0) {
        place = new Settlement(code, m.group(4));
      } else {
        return null;
      }

      place.setName(m.group(3));

      return place;
    }
    return null;
  }
}
