package fi.metatavu.famifarm.test.functional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Facility facility) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSize), false, facility);
    return createSowingEvent(builder, product, facility);
  }

    /**
   * Creates test event
   * 
   * @param builder test builder
   * @param product product to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product, Facility facility) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    
    return createSowingEvent(builder, product, startTime, endTime, facility);
  }

  /**
   * Creates test event
   *
   * @param builder test builder
   * @param product product to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Product product, Facility facility, int seconds) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);

    return createSowingEvent(builder, product, startTime, endTime, facility);
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
  protected Event createSowingEvent(TestBuilder builder, Product product, OffsetDateTime startTime, OffsetDateTime endTime, Facility facility) throws IOException {
    Integer amount = 12;
    
    return createSowingEvent(builder, product, amount, startTime, endTime, facility);
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
  protected Event createSowingEvent(TestBuilder builder, Product product, int amount, OffsetDateTime startTime, OffsetDateTime endTime, Facility facility) throws IOException {
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"), facility);
    ProductionLine productionLine = builder.admin().productionLines().create("4", 8, facility);
    SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime, facility);
    
    return builder.admin().events().createSowing(product, startTime, endTime, amount, productionLine, Arrays.asList(seedBatch), facility);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createTableSpreadEvent(TestBuilder builder, Facility facility) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);
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
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder, Facility facility) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);
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
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN),
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"), Facility.JOROINEN)
    );
    
    List<Pest> pests = Arrays.asList(
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 1"), Facility.JOROINEN),
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 2"), Facility.JOROINEN)
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
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN),
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"), Facility.JOROINEN)
    );
    
    List<Pest> pests = Arrays.asList(
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 1"), Facility.JOROINEN),
      builder.admin().pests().create(builder.createLocalizedEntry("Pest 2"), Facility.JOROINEN)
    );
    
    return builder.admin().events().createCultivationObservation(product, startTime, endTime, luminance, pests, weight, performedActions);
  }

  /**
   * Creates test event
   *
   * @param builder test builder
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, Facility facility) throws IOException {
    HarvestEventType harvestType = HarvestEventType.BAGGING;
    return createHarvestEvent(builder, harvestType, facility);
  }

  /**
   * Creates test harvest events
   *
   * @param builder test builder
   * @param facility facility
   * @param amount amount of events to create
   * @return list of created events
   * @throws IOException thrown when event creation fails
   */
  protected List<Event> createHarvestEvents(TestBuilder builder, Facility facility, int amount) throws IOException {
    HarvestEventType harvestType = HarvestEventType.BAGGING;
    List<Event> createdEvents = Lists.newArrayList();
    for (int i = 0; i < amount; i++) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name " + i, "Tuotteen nimi " + i), Lists.newArrayList(createdPackageSize), false, facility);
      Event newEvent = createHarvestEvent(builder, harvestType, product, 50, facility, i);
      createdEvents.add(newEvent);
    }

    return createdEvents;
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param harvestType harvestType
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Facility facility) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSize), false, facility);
    return createHarvestEvent(builder, harvestType, product, facility);
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

  protected List<Packing> createPackingEvents(TestBuilder builder, Facility facility, int amount) throws IOException {
    List<Packing> createdEvents = Lists.newArrayList();
    for (int i = 0; i < amount; i++) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, facility);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, facility);
      Product product1 = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, facility);
      Packing newEvent = createPackingEvent(builder, List.of(product, product1), facility, i, testEntry);
      createdEvents.add(newEvent);
    }

    return createdEvents;
  }

  /**
   * Creates test of packing events of N size.
   * Each packing event creates new peoduct.
   * Each packing event has +1 of weight verifications
   *
   * @param builder test builder
   * @param facility facility
   * @param amount event amount
   * @return
   * @throws IOException
   */
  protected List<Packing> createPackingEventsWithVerificationWeightings(TestBuilder builder, Facility facility, int amount) throws IOException {
    List<Packing> createdEvents = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();
      testValue.setLanguage("en");
      testValue.setValue("test value " + i);
      testEntry.add(testValue);

      //a new product for each of the 9 events
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, facility);
      Product product = builder.admin().products().create(testEntry, Collections.singletonList(size), false, facility);

      // Create different amounts of verification weightings for different products
      List<PackingVerificationWeighing> weightings = new ArrayList<>();
      for (int j = 0; j <= i; j++) {
        weightings.add(new PackingVerificationWeighing().time(OffsetDateTime.now()).weight((float) i));
      }

      List<PackingUsedBasket> baskets = new ArrayList<>();
      baskets.add(new PackingUsedBasket().basketCount(1).productId(product.getId()));

      OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, i % 60, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, i % 60, 0, ZoneOffset.UTC);
      Integer packedCount = 50;

      Packing packing = builder.admin().packings().create(
        product.getId(),
        null, PackingType.BASIC,
        OffsetDateTime.now(),
        packedCount,
        PackingState.IN_STORE,
        size,
        facility,
        weightings,
        baskets,
        null,
        startTime,
        endTime,
        "additional info " + i
      );
      assertNotNull(packing);
      createdEvents.add(packing);
    }
    return createdEvents;
  }

  protected Packing createPackingEvent(TestBuilder builder, List<Product> products, Facility facility, int seconds, List<LocalizedValue> localizedValues) throws IOException {
    seconds = Math.min(seconds, 59);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);
    Integer packedCount = 50;


    List<PackingVerificationWeighing> weightings = new ArrayList<>();
    weightings.add(new PackingVerificationWeighing().time(OffsetDateTime.now()).weight(100.0f));

    List<PackingUsedBasket> baskets = new ArrayList<>();
    products.forEach(product -> {
      baskets.add(new PackingUsedBasket().basketCount(1).productId(product.getId()));
    });

    PackageSize size = builder.admin().packageSizes().create(localizedValues, 100, facility);

    Packing packing = builder.admin().packings().create(
      products.get(0).getId(),
      null, PackingType.BASIC,
      OffsetDateTime.now(),
      packedCount,
      PackingState.IN_STORE,
      size,
      facility,
      weightings,
      baskets,
      null,
      startTime,
      endTime,
      "additional info");
    assertNotNull(packing);

    return packing;
  }

  protected Packing createPackingEventWithProductIds(TestBuilder builder, List<UUID> products, Facility facility, int seconds) throws IOException {
    List<LocalizedValue> testEntry = new ArrayList<>();
    LocalizedValue testValue = new LocalizedValue();

    testValue.setLanguage("en");
    testValue.setValue("test value");
    testEntry.add(testValue);

    seconds = Math.min(seconds, 59);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);
    Integer packedCount = 2;

    List<PackingVerificationWeighing> weightings = new ArrayList<>();
    weightings.add(new PackingVerificationWeighing().time(OffsetDateTime.now()).weight(100.0f));

    List<PackingUsedBasket> baskets = new ArrayList<>();
    products.forEach(productId -> {
      baskets.add(new PackingUsedBasket().basketCount(1).productId(productId));
    });

    PackageSize size = builder.admin().packageSizes().create(testEntry, 100, facility);

    Packing packing = builder.admin().packings().create(
      products.get(0),
      null, PackingType.BASIC,
      OffsetDateTime.now(),
      packedCount,
      PackingState.IN_STORE,
      size,
      facility,
      weightings,
      baskets,
      null,
      startTime,
      endTime,
      "additional info");
    assertNotNull(packing);

    return packing;
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
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Facility facility) throws IOException {
    Integer amount = 50;

    return createHarvestEvent(builder, harvestType, product, amount, facility, 6);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @param facility facility
   * @param seconds seconds
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount, Facility facility, int seconds) throws IOException {
    Integer gutterHoleCount = 50;
    Integer cuttingHeight = 60;
    List<HarvestBasket> baskets = List.of(
      new HarvestBasket().weight(10f),
      new HarvestBasket().weight(20f),
      new HarvestBasket().weight(30f)
    );

    return createHarvestEvent(builder, harvestType, product, amount, gutterHoleCount, cuttingHeight, baskets, facility, seconds);
  }

  /**
   * Creates test harvest event
   *
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @param amount amount
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount) throws IOException {
    Integer gutterHoleCount = 50;
    Integer cuttingHeight = 150;
    List<HarvestBasket> baskets = Collections.emptyList();

    return createHarvestEvent(builder, harvestType, product, amount, gutterHoleCount, cuttingHeight, baskets);
  }


  /**
   * Creates test harvest event
   *
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @param amount amount
   * @param gutterHoleCount gutterHoleCount
   * @param baskets baskets
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount, Integer gutterHoleCount, Integer cuttingHeight, List<HarvestBasket> baskets) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    ProductionLine productionLine = builder.admin().productionLines().create("4", 8, Facility.JOROINEN);
    
    return builder.admin().events().createHarvest(product, amount, gutterHoleCount, cuttingHeight, startTime, endTime, productionLine, sowingTime, harvestType, baskets);
  }


  /**
   * Creates test harvest event
   *
   * @param builder test builder
   * @param harvestType harvestType
   * @param product product
   * @param amount amount
   * @param gutterHoleCount gutterHoleCount
   * @param baskets baskets
   * @param facility facility
   * @param seconds seconds part of created timestamps
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder, HarvestEventType harvestType, Product product, Integer amount, Integer gutterHoleCount, Integer cuttineHeight, List<HarvestBasket> baskets, Facility facility, int seconds) throws IOException {
    seconds = Math.min(seconds, 59);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, seconds, 0, ZoneOffset.UTC);

    ProductionLine productionLine = builder.admin().productionLines().create("4", 8, facility);

    return builder.admin().events().createHarvest(product, amount, gutterHoleCount, cuttineHeight, startTime, endTime, productionLine, sowingTime, harvestType, baskets, facility);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Facility facility) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 12, facility);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);
    return createPlantingEvent(builder, product, facility);
  }

  /**
   * Creates a number of test planting events
   *
   * @param builder test builder
   * @param facility facility
   * @param amount amount of events to create
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected List<Event> createPlantingEvents(TestBuilder builder, Facility facility, Integer amount) throws IOException {
    List<Event> createdEvents = Lists.newArrayList();
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 12, facility);
    for (int i = 0; i < amount; i++) {
      List<LocalizedValue> name = builder.createLocalizedEntry("Product name " + i, "Tuotteen nimi " + i);
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);
      Event newEvent = createPlantingEvent(builder, product, facility, i);
      createdEvents.add(newEvent);
    }

    return createdEvents;
  }

  /**
   * Creates a number of test sowing events
   *
   * @param builder test builder
   * @param facility facility
   * @param amount amount of events to create
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected List<Event> createSowingEvents(TestBuilder builder, Facility facility, Integer amount) throws IOException {
    List<Event> createdEvents = Lists.newArrayList();
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 12, facility);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);

    for (int i = 0; i < amount; i++) {
      Event newEvent = createSowingEvent(builder, product, facility, i);
      createdEvents.add(newEvent);
    }

    return createdEvents;
  }

  /**
   * Creates test event
   *
   * @param builder test builder
   * @param product product
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product, Facility facility) throws IOException {
    Integer gutterSize = 2;
    Integer gutterCount = 2;

    return createPlantingEvent(builder, product, gutterSize, gutterCount, facility);
  }


  /**
   * Creates test event
   *
   * @param builder test builder
   * @param product product
   * @param facility facility
   * @param seconds seconds part of created timestamps
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product, Facility facility, int seconds) throws IOException {
    Integer gutterSize = 2;
    Integer gutterCount = 2;

    return createPlantingEvent(builder, product, gutterSize, gutterCount, facility, seconds);
  }
  

  /**
   * Creates test event
   *
   * @param builder test builder
   * @param product product
   * @param gutterSize gutterSize
   * @param gutterCount gutterCount
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product, Integer gutterSize, Integer gutterCount, Facility facility) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, 6, 0, ZoneOffset.ofHours(2));
    ProductionLine productionLine = builder.admin().productionLines().create("4" , 8, facility);
    Integer trayCount = 50;
    Integer workerCount = 2;
    
    return builder.admin().events().createPlanting(product, startTime, endTime, gutterCount, gutterSize, productionLine, sowingTime, trayCount, workerCount, facility);
  }


  /**
   * Creates test event
   *
   * @param builder test builder
   * @param product product
   * @param gutterSize gutterSize
   * @param gutterCount gutterCount
   * @param facility facility
   * @param seconds seconds part of created timestamps
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder, Product product, Integer gutterSize, Integer gutterCount, Facility facility, int seconds) throws IOException {
    seconds = Math.min(seconds, 59);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime sowingTime = OffsetDateTime.of(2020, 1, 3, 4, 10, seconds, 0, ZoneOffset.ofHours(2));
    ProductionLine productionLine = builder.admin().productionLines().create("4" , 8, facility);
    Integer trayCount = 50;
    Integer workerCount = 2;

    return builder.admin().events().createPlanting(product, startTime, endTime, gutterCount, gutterSize, productionLine, sowingTime, trayCount, workerCount, facility);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param facility facility
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder, Facility facility) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"), facility);
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, facility);
    List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, facility);
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7, Facility.JOROINEN);
    
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
   * @param facility facility
   * @param productId product id
   * @param seconds seconds part of events for sorting stability
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder, Facility facility, UUID productId, UUID productLineId, int seconds) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason " + seconds, "Testi syy"), facility);

    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, seconds, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, seconds, 0, ZoneOffset.UTC);

    Integer amount = 1;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(productId, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productLineId, facility);
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
    Facility facility = Facility.JOROINEN;
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"), facility);
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7, Facility.JOROINEN);
    
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
    Facility facility = Facility.JOROINEN;
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"), facility);
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", 7, Facility.JOROINEN);

    Integer amount = 20;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(product, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productionLine.getId());
  }

  /**
   * Get number of plants in tray depending on pot type
   * 
   * @param potType potType
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
