package fi.metatavu.famifarm.test.functional;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Lists;
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
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSize), false);
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
   * @param product product
   * @param amount amount
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product, int amount, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    ProductionLine productionLine = builder.admin().productionLines().create("4", 8);
    SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
    
    return builder.admin().events().createSowing(product, startTime, endTime, amount, productionLine, Arrays.asList(seedBatch));
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
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
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
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
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
    HarvestEventType harvestType = HarvestEventType.BAGGING;
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
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSize), false);
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
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product) throws IOException {
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
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount) throws IOException {
    Integer gutterHoleCount = 50;
    
    return createHarvestEvent(builder, harvestType, product, amount, gutterHoleCount);
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
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount, Integer gutterHoleCount) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    ProductionLine productionLine = builder.admin().productionLines().create("4", 8);
    
    return builder.admin().events().createHarvest(product, amount, gutterHoleCount, startTime, endTime, productionLine, sowingTime, harvestType);
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
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
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
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
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
    if (PotType.LARGE == potType) {
      return 35;
    }
    return 54;
  }

  /**
   * Gets object mapper with parameters
   *
   * @return object mapper
   */
  public ObjectMapper getObjectMapper() {
    return JsonMapper.builder()
      .addModule(new ParameterNamesModule())
      .addModule(new Jdk8Module())
      .addModule(new JavaTimeModule())
      .configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true)
      .build();
  }
  
}
