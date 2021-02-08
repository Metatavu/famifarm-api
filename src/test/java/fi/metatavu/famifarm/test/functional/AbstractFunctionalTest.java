package fi.metatavu.famifarm.test.functional;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Abstract base class for functional tests
 * 
 * @author Antti Leppä
 */
public abstract class AbstractFunctionalTest {
  /**
   * Returns a testable datetime string
   *
   * @param dateTime date time to convert
   * @return a testable datetime string
   */
  protected String getTestableDateTimeString (OffsetDateTime dateTime) {
    String dateTimeString = dateTime.toString();
    int dotPosition = dateTimeString.lastIndexOf("T");
    return dateTimeString.substring(0, dotPosition);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createdPackageSize, false);
    return createSowingEvent(builder, product);
  }

    /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    
    return createSowingEvent(builder, product, startTime, endTime);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product to attach the event to
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    Integer amount = 12;
    
    return createSowingEvent(builder, product, amount, startTime, endTime);
  }
  

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch to attach the event to
   * @param amount amount
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product, int amount, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    PotType potType = PotType.LARGE;
    ProductionLine productionLine = builder.admin().productionLines().create("4", 8);
    SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
    
    return builder.admin().events().createSowing(product, startTime, endTime, amount, potType, productionLine, Arrays.asList(seedBatch));
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createTableSpreadEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize, false);
    return createTableSpreadEvent(builder, product);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createTableSpreadEvent(TestBuilder builder, Product product) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    Integer tableCount = 15;
    
    return builder.admin().events().createTableSpread(product, startTime, endTime, tableCount);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize, false);
    return createCultivationObservationEvent(builder, product);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder, Product product) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Double luminance = 44d;
    Double weight = 22d;
    List<PerformedCultivationAction> performedActions = Arrays.asList(
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely")),
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"))
    );
    
    List<Pest> pests = Arrays.asList(
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 1")),
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 2"))   
    );
    
    return builder.admin().events().createCultivationObservation(product, startTime, endTime, luminance, pests, weight, performedActions);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @param weight weight
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder, Product product, Double weight) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Double luminance = 44d;
    List<PerformedCultivationAction> performedActions = Arrays.asList(
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely")),
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"))
    );
    
    List<Pest> pests = Arrays.asList(
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 1")),
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 2"))   
    );
    
    return builder.admin().events().createCultivationObservation(product, startTime, endTime, luminance, pests, weight, performedActions);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder) throws IOException {
    fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum harvestType = fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum.BAGGING;

    return createHarvestEvent(builder, harvestType);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param harvestType harvestType
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum harvestType) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createdPackageSize, false);
    return createHarvestEvent(builder, harvestType, product);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum harvestType, Product product) throws IOException {
    Integer amount = 50;
    
    return createHarvestEvent(builder, harvestType, product, amount);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum harvestType, Product product, Integer amount) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    ProductionLine productionLine = builder.admin().productionLines().create("4", 8);
    
    return builder.admin().events().createHarvest(product, amount, startTime, endTime, productionLine, sowingTime, harvestType);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 12);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize, false);
    return createPlantingEvent(builder, product);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product) throws IOException {
    Integer gutterSize = 2;
    Integer gutterCount = 2;
    
    return createPlantingEvent(builder, product, gutterSize, gutterCount);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product, Integer gutterSize, Integer gutterCount) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, 6, 0, ZoneOffset.ofHours(2));
    ProductionLine productionLine = builder.admin().productionLines().create("4" , 8);
    Integer trayCount = 50;
    Integer workerCount = 2;
    
    return builder.admin().events().createPlanting(product, startTime, endTime, gutterCount, gutterSize, productionLine, sowingTime, trayCount, workerCount);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"));
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize, false);
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7);
    
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Integer amount = 150;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";
    return builder.admin().events().createWastage(product, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productionLine.getId());
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder, Product product) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"));
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7);
    
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Integer amount = 150;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(product, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productionLine.getId());
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder, Product product, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"));
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7);

    Integer amount = 20;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(product, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productionLine.getId());
  }

  /**
   * Get number of plants in tray depending on pot type
   * 
   * @param potType, potType
   * @return amount
   */
  protected int getPotTypeAmount(PotType potType) {
    if (PotType.SMALL == potType) {
      return 54;
    }
    return 35;
  }
  
}
