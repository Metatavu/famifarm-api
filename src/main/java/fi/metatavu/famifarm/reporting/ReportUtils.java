package fi.metatavu.famifarm.reporting;

import java.util.List;
import java.util.stream.Collectors;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Utility methods for reports
 * 
 * @author Heikki Kurhinen
 */
public class ReportUtils {

  private ReportUtils(){
    //Private constructor
  }

  /**
   * Counts packed units by product
   *
   * @param packings list of packings to count from
   * @param product product to count by
   * @return number of packed units
   */
  public static Double countPackedUnitsByProduct(List<Packing> packings, Product product) {
    List<Packing> productPackings = packings.stream().filter(packing -> packing.getProduct().getId().equals(product.getId())).collect(Collectors.toList());
    Double count = 0d;
    for (Packing packing : productPackings) {
      count+= packing.getPackedCount() * packing.getPackageSize().getSize();;
    }
    return count;
  }

  /**
   * Counts units from list of events by event type and product
   * 
   * @param events list of events to count from
   * @param product product to count by
   * @param eventType event type to count by
   * @return  number of processed units
   */
  public static Double countUnitsByProductAndEventType(List<Event> events, Product product, EventType eventType) {
    List<Event> productEvents = events.stream()
        .filter(event -> event.getBatch().getProduct().getId().equals(product.getId()))
        .collect(Collectors.toList());
    
    return countUnitsByEventType(productEvents, eventType);
  }

  /**
   * Counts units from list of events by event type
   * 
   * @param events list of events to count from
   * @param eventType event type to count from
   * @return number of processed units
   */
  public static Double countUnitsByEventType(List<Event> events, EventType eventType) {
    switch (eventType) {
      case SOWING:
        return countSowedUnits(events);
      case TABLE_SPREAD:
        return countSpreadUnits(events);
      case PLANTING:
        return countPlantedUnits(events);
      case CULTIVATION_OBSERVATION:
        return 0d; //Cultivation observation events dont store amount
      case HARVEST:
        return countHarvestedUnits(events);
      default:
        return 0d;
    }
  }

  /**
   * Counts sowed units from list of events
   * 
   * @param events list of events to count sowed units from
   * @return number of sowed units
   */
  public static Double countSowedUnits(List<Event> events) {
    Double count = 0d;
    for (Event event : events) {
      if (event.getType() == EventType.SOWING) {
        SowingEvent sowingEvent = (SowingEvent) event;
        count += sowingEvent.getAmount();
      }
    }

    return count;
  }

  /**
   * Counts spread units from list of events
   * 
   * @param events list of events to count spread units from
   * @return number of spread units
   */
  public static Double countSpreadUnits(List<Event> events) {
    Double count = 0d;
    
    SowingEvent sowingEvent = (SowingEvent) events.stream().filter(event -> event.getType() == EventType.SOWING).findFirst().orElse(null);
    if (sowingEvent == null) {
      return 0d;
    }
    
    PotType potType = sowingEvent.getPotType();

    for (Event event : events) {
      if (event.getType() == EventType.TABLE_SPREAD) {
        TableSpreadEvent tableSpreadEvent = (TableSpreadEvent) event;
        count += (tableSpreadEvent.getTrayCount() * getPotTypeAmount(potType));
      }
    }
    
    return count;
  }

  /**
   * Counts planted units from the list of events
   * 
   * @param events events to count the planted units from 
   * @return number of planted units
   */
  public static Double countPlantedUnits(List<Event> events) {
    Double count = 0d;
    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        count += (plantingEvent.getGutterCount() * plantingEvent.getGutterHoleCount());
      }
    }

    return count;
  }

  /**
   * Counts harvested units from the list of events
   * 
   * @param events list of events to count the harvested units from
   * @return number of harvested units
   */
  public static Double countHarvestedUnits(List<Event> events) {
    Double count = 0d;
    Double gutterHoleCount = getAverageGutterHoleCount(events);

    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        HarvestEvent harvestEvent = (HarvestEvent) event;
        count += (harvestEvent.getGutterCount() * gutterHoleCount);
      }
    }
    
    return count;
  }

  /**
   * Counts number of wasted units from the list of events
   * 
   * @param events list of events to count the packed units from
   * @return number of packed events
   */
  public static Double countWastedUnits(List<Event> events) {
    Double count = 0d;
    for (Event event : events) {
      if (event.getType() == EventType.WASTAGE) {
        WastageEvent wastageEvent = (WastageEvent) event;
        count += wastageEvent.getAmount();
      }
    }

    return count;
  }

  /**
   * Get weighted average gutter hole count
   * 
   * @param events
   * @return weighted average gutter hole count
   */
  private static Double getAverageGutterHoleCount(List<Event> events) {
    Double totalWeightedSize = 0d;
    Double totalGutterCount = 0d;
    
    for (Event event : events) {
      if (event.getType() == EventType.PLANTING) {
        PlantingEvent plantingEvent = (PlantingEvent) event;
        totalWeightedSize += (plantingEvent.getGutterHoleCount() * plantingEvent.getGutterCount());
        totalGutterCount += plantingEvent.getGutterCount();
      }
    }

    if (totalWeightedSize == 0 || totalGutterCount == 0) {
      return 0d;
    }

    return totalWeightedSize / totalGutterCount;
  }

  /**
   * Get tray type as int
   * 
   * @param potType, potType
   * @return amount
   */
  private static int getPotTypeAmount(PotType potType) {
    if (PotType.SMALL == potType) {
      return 54;
    }
    return 35;
  }

}
