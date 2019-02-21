package fi.metatavu.famifarm.test.functional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    Double amount = 12d;
    CellType cellType = CellType.LARGE;
    ProductionLine productionLine = builder.admin().productionLines().create(4, null);
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Double luminance = 44d;
    String pests = "Pests";
    Double weight = 22d;
    List<PerformedCultivationAction> performedActions = Arrays.asList(
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely")),
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"))
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    Product product = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    ProductionLine productionLine = builder.admin().productionLines().create(4, null);
    
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);
    
    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    Integer gutterNumber = 2;
    ProductionLine productionLine = builder.admin().productionLines().create(4, null);
    
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
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
   * @return created event
   * @throws IOException thrown when event creation fails
   */
  protected Event createWastageEvent(TestBuilder builder) throws IOException {
    WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test reason", "Testi syy"));
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);

    Integer amount = 150;
    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus mollis felis non dapibus. In at eros magna. Suspendisse finibus ut nunc et volutpat. Etiam sollicitudin tristique enim et rhoncus. Pellentesque quis elementum nisl. Integer at velit in sapien porttitor eleifend. Phasellus eleifend suscipit sapien eu elementum. Pellentesque et nunc a sapien tincidunt rhoncus. Vestibulum a tincidunt eros, molestie lobortis purus. Integer dignissim dignissim mauris a viverra. Etiam ut libero sit amet erat dapibus volutpat quis vel ipsum.";

    return builder.admin().events().createWastage(batch, startTime, endTime, amount, wastageReason, description);
  }
  
}
