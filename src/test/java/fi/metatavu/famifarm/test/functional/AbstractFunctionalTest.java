package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.EventType;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.client.model.Pest;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Abstract base class for functional tests
 * 
 * @author Antti Leppä
 */
public abstract class AbstractFunctionalTest {

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createdPackageSize);
    Batch batch = builder.admin().batches().create(product);
    return createSowingEvent(builder, batch);

  }

    /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Batch batch) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    
    return createSowingEvent(builder, batch, startTime, endTime);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch to attach the event to
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createSowingEvent(TestBuilder builder, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    Integer amount = 12;
    CellType cellType = CellType.LARGE;
    ProductionLine productionLine = builder.admin().productionLines().create("4", null);
    SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
    
    return builder.admin().events().createSowing(batch, startTime, endTime, amount, cellType, productionLine, seedBatch);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createTableSpreadEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    return createTableSpreadEvent(builder, batch);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch to attach the event to
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createTableSpreadEvent(TestBuilder builder, Batch batch) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    String location = "Location";
    Integer tableCount = 15;
    
    return builder.admin().events().createTableSpread(batch, startTime, endTime, location, tableCount);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);

    Batch batch = builder.admin().batches().create(product);

    return createCultivationObservationEvent(builder, batch);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder, Batch batch) throws IOException {
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
    
    return builder.admin().events().createCultivationObservation(batch, startTime, endTime, luminance, pests, weight, performedActions);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch
   * @param weight weight
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createCultivationObservationEvent(TestBuilder builder, Batch batch, Double weight) throws IOException {
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
    
    return builder.admin().events().createCultivationObservation(batch, startTime, endTime, luminance, pests, weight, performedActions);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createHarvestEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    ProductionLine productionLine = builder.admin().productionLines().create("4", null);
    
    Team team = builder.admin().teams().create(builder.createLocalizedEntry("Team name", "Tiimin nimi"));
    fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum harvestType = fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum.BAGGING;

    return builder.admin().events().createHarvest(batch, startTime, endTime, productionLine, team, harvestType);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPlantingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);
    
    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    Integer gutterNumber = 2;
    ProductionLine productionLine = builder.admin().productionLines().create("4", null);
    
    Integer gutterCount = 2;
    Integer trayCount = 50;
    Integer workerCount = 2;
    
    return builder.admin().events().createPlanting(batch, startTime, endTime, gutterCount, gutterNumber, productionLine, trayCount, workerCount);
  }

  /**
   * Creates test event
   * 
   * @param builder test builder
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPackingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);
    
    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);    
    Integer packedAmount = 80;
    
    return builder.admin().events().createPacking(batch, startTime, endTime, createdPackageSize, packedAmount);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPackingEvent(TestBuilder builder, Batch batch) throws IOException {
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);    
    
    return createPackingEvent(builder, batch, startTime, endTime);
  }
  
  /**
   * Creates test event
   * 
   * @param builder test builder
   * @param batch batch
   * @param startTime startTime
   * @param endTime endTime
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createPackingEvent(TestBuilder builder, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
    Integer packedAmount = 80;
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    
    return builder.admin().events().createPacking(batch, startTime, endTime, createdPackageSize, packedAmount);
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);
    Team team = builder.admin().teams().create(builder.createLocalizedEntry("Team 1", "Tiimi 1"));
    ProductionLine productionLine = builder.admin().productionLines().create("1 A", team);
    Batch batch = builder.admin().batches().create(product);
    
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Integer amount = 150;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(batch, startTime, endTime, amount, wastageReason, description, EventType.HARVEST, productionLine.getId());
  }

  /**
   * Sets remaining units field value for event
   * 
   * @param event event
   * @param remainingUnits value
   */
  protected void setEventRemainingUnits(Event event, Integer remainingUnits) {
    try {
      Field field = Event.class.getDeclaredField("remainingUnits");
      field.setAccessible(true);
      field.set(event, remainingUnits);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      fail(e.getMessage());
    }
  }
}
