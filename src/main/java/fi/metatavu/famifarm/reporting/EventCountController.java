package fi.metatavu.famifarm.reporting;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.discards.StorageDiscardController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.PlantingEventController;
import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.PackingType;
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

  @Inject
  private PlantingEventController plantingEventController;

  @Inject
  private CampaignController campaingController;

  @Inject
  private StorageDiscardController storageDiscardController;

  /**
   * Counts packed units by product
   *
   * @param packings list of packings to count from
   * @param product product to count by
   * @return number of packed units
   */
  public Double countPackedUnitsByProduct(List<Packing> packings, Product product) {
    List<Packing> productPackings = packings.stream().filter(packing -> packing.getType() == PackingType.BASIC && packing.getProduct().getId().equals(product.getId())).collect(Collectors.toList());
    Double count = 0d;
    for (Packing packing : productPackings) {
      if (packing.getPackedCount() != null &&
          packing.getPackageSize() != null &&
          packing.getPackageSize().getSize() != null) {
            count+= packing.getPackedCount() * packing.getPackageSize().getSize();
          }
    }
    List<Packing> campaingPackings = packings.stream().filter(packing -> packing.getType() == PackingType.CAMPAIGN).collect(Collectors.toList());
    for (Packing campaingPacking : campaingPackings) {
      Campaign campaing = campaingPacking.getCampaign();
      if (campaing != null) {
        List<CampaignProduct> campaignProducts = campaingController.listCampaingProductsByCampaign(campaing);
        Integer campaingProductCount = campaignProducts
          .stream()
          .filter(campaingProduct -> campaingProduct.getProduct() != null && campaingProduct.getProduct().getId().equals(product.getId()))
          .mapToInt(CampaignProduct::getCount)
          .sum();
          count += campaingProductCount;
      }
    }
    return count;
  }

  /**
   * Counts wasted packed units by product
   * 
   * @param product product to count by
   * @param fromTime time to count from
   * @param toTime time to count to
   * @param facility facility
   * @return number of wasted, already packed units
   */
  public Double countWastedPackedUnitsByProduct(Product product, OffsetDateTime fromTime, OffsetDateTime toTime, Facility facility) {
    List<StorageDiscard> storageDiscards = storageDiscardController.listStorageDiscards(null, null, fromTime, toTime, product, facility);
    Double wastedPackedUnits = 0d;
    for (StorageDiscard storageDiscard : storageDiscards) {
      wastedPackedUnits += (storageDiscard.getPackageSize().getSize() * storageDiscard.getDiscardAmount());
    }
    return wastedPackedUnits;
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
      case WASTAGE:
        return countWastedUnits(events);
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
        count += (sowingEvent.getAmount() * getTraySizeForPotType(sowingEvent));
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
        HarvestEvent harvestEvent = (HarvestEvent) event;
        Integer gutterHoleCount = getGutterHoleCount(harvestEvent);
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
   * Gets gutter hole count for harvest event
   * if it is not available, tries to make quess by checking last planting event
   * 
   * @param event harvest event
   * @return gutter hole count
   */
  private Integer getGutterHoleCount(HarvestEvent event) {
    if (event.getGutterHoleCount() != null && event.getGutterHoleCount() > 0) {
      return event.getGutterHoleCount();
    }

    return guessGutterHoleCount(event);
  } 

  /**
   * Tries to guess gutter hole count for harvest event
   * 
   * @param event harvest event
   * @return gutter hole count
   */
  private Integer guessGutterHoleCount(HarvestEvent event) {
    List<PlantingEvent> latestPlantings = plantingEventController.listLatestPlatingEventByProductAndProductionLine(event.getProduct(), event.getProductionLine(), event.getStartTime());
    if (latestPlantings.isEmpty()) {
      Integer defaultGutterHoleCount = event.getProductionLine().getDefaultGutterHoleCount();
      return defaultGutterHoleCount != null ? defaultGutterHoleCount : 0;
    }

    PlantingEvent latestPlanting = latestPlantings.get(0);
    Integer gutterHoleCount = latestPlanting.getGutterHoleCount();
    return gutterHoleCount != null ? gutterHoleCount : 0;
  } 

  /**
   * Gets amount of plants in one tray with specified pot type
   * 
   * @param sowingEvent sowing event
   * @return number of plants in one tray
   */
  private int getTraySizeForPotType(SowingEvent sowingEvent) {
    var potType = sowingEvent.getPotType();
    if (PotType.LARGE == potType) {
      return 35;
    }

    if (isJuvaFacility(sowingEvent)) {
      return 50;
    } else {
      return 54;
    }
  }

  /**
   * Get tray type as int
   * 
   * @param event
   * @return amount
   */
  private int getPotTypeAmount(Event event) {
    List<Event> batchEvents = eventController.listEvents(event.getProduct(), null, null);
    SowingEvent sowingEvent = (SowingEvent) batchEvents.stream().filter(batchEvent -> batchEvent.getType() == EventType.SOWING).findFirst().orElse(null);
    if (sowingEvent == null) {
      return 0;
    }

    if (PotType.LARGE == sowingEvent.getPotType()) {
      return 35;
    }

    if (isJuvaFacility(event)) {
      return 50;
    } else {
      return 54;
    }
  }

  /**
   * Checks if event took place in Juva facility
   *
   * @param event event
   * @return true if juva
   */
  private boolean isJuvaFacility(Event event) {
    return event.getProduct().getFacility() == Facility.JUVA;
  }
}
