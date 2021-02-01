package fi.metatavu.famifarm.reporting;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.PotType;

/**
 * Utility methods for counting events
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class EventCountController {

  @Inject
  private EventController eventController;

  /**
   * Counts packed units by product
   *
   * @param packings list of packings to count from
   * @param product product to count by
   * @return number of packed units
   */
  public Double countPackedUnitsByProduct(List<Packing> packings, Product product) {
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
  public Double countUnitsByProductAndEventType(List<Event> events, Product product, EventType eventType) {
    List<Event> productEvents = events.stream()
        .filter(event -> event.getProduct().getId().equals(product.getId()))
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
  public Double countUnitsByEventType(List<Event> events, EventType eventType) {
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
  public Double countSowedUnits(List<Event> events) {
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
  public Double countSpreadUnits(List<Event> events) {
    Double count = 0d;
    for (Event event : events) {
      if (event.getType() == EventType.TABLE_SPREAD) {
        TableSpreadEvent tableSpreadEvent = (TableSpreadEvent) event;
        count += (tableSpreadEvent.getTrayCount() * getPotTypeAmount(event));
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
  public Double countPlantedUnits(List<Event> events) {
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
  public Double countHarvestedUnits(List<Event> events) {
    Double count = 0d;

    for (Event event : events) {
      if (event.getType() == EventType.HARVEST) {
        Double gutterHoleCount = getAverageGutterHoleCount(event);
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
  public Double countWastedUnits(List<Event> events) {
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
   * @param eventParam
   * @return weighted average gutter hole count
   */
  private Double getAverageGutterHoleCount(Event eventParam) {
    Double totalWeightedSize = 0d;
    Double totalGutterCount = 0d;
    List<Event> batchEvents = eventController.listEvents(eventParam.getProduct(), null, null);

    for (Event event : batchEvents) {
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
  private int getPotTypeAmount(Event event) {
    List<Event> batchEvents = eventController.listEvents(event.getProduct(), null, null);
    SowingEvent sowingEvent = (SowingEvent) batchEvents.stream().filter(batchEvent -> batchEvent.getType() == EventType.SOWING).findFirst().orElse(null);
    if (sowingEvent == null) {
      return 0;
    }

    if (PotType.SMALL == sowingEvent.getPotType()) {
      return 54;
    }
    return 35;
  }

}
