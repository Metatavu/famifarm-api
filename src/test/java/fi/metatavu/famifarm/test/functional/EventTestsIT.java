package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.CultivationObservationEventData;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.SowingEventData;
import fi.metatavu.famifarm.client.model.TableSpreadEventData;
import fi.metatavu.famifarm.client.model.Event.TypeEnum;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for seeds
 * 
 * @author Antti Leppä
 */
public class EventTestsIT {

  @Test
  public void testCreateSowingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createSowingEvent(builder));
    }
  }
  
  @Test
  public void testFindSowingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createSowingEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @Test
  public void testUpdateSowingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createSowingEvent(builder);
      
      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create("New Test PackageSize");
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket new", "Rucola uusi"));
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Double updateAmount = 14d;
      CellType updateCellType = CellType.SMALL;
      Integer updateGutterNumber = 3;
      ProductionLine updateProductionLine = builder.admin().productionLines().create(7);
      SeedBatch updateSeedBatch = builder.admin().seedBatches().create("123", seed, updateStartTime);
      
      SowingEventData updateData = new SowingEventData();
      updateData.setAmount(updateAmount);
      updateData.setCellType(updateCellType);
      updateData.setGutterNumber(updateGutterNumber);
      updateData.setProductionLineId(updateProductionLine.getId());
      updateData.setSeedBatchId(updateSeedBatch.getId());
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(TypeEnum.SOWING);
      updateEvent.setUserId(createdEvent.getUserId());
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }
  
  @Test
  public void testDeleteSowingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createSowingEvent(builder);
      Event foundSeed = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundSeed.getId());
      builder.admin().events().delete(createdEvent);
      builder.admin().events().assertFindFailStatus(404, createdEvent.getId());     
    }
  }

  @Test
  public void testCreateTableSpreadEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createTableSpreadEvent(builder));
    }
  }
  
  @Test
  public void testFindTableSpreadEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createTableSpreadEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @Test
  public void testUpdateTableSpreadEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createTableSpreadEvent(builder);
      
      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create("New Test PackageSize");
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      String updateLocation = "updated location";
      Integer updateTableCount = 82;
      
      TableSpreadEventData updateData = new TableSpreadEventData();
      updateData.setLocation(updateLocation);
      updateData.setTableCount(updateTableCount);

      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }
  
  @Test
  public void testDeleteTableSpreadEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event created = createTableSpreadEvent(builder);
      Event found = builder.admin().events().findEvent(created.getId());
      assertEquals(created.getId(), found.getId());
      builder.admin().events().delete(created);
      builder.admin().events().assertFindFailStatus(404, created.getId());     
    }
  }

  @Test
  public void testCreateCultivationObservationEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createCultivationObservationEvent(builder));
    }
  }
  
  @Test
  public void testFindCultivationObservationEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createCultivationObservationEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateCultivationObservationEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createCultivationObservationEvent(builder);
      Map<String, Object> createdData = (Map<String, Object>) createdEvent.getData();
      
      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create("New Test PackageSize");
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      
      List<UUID> updatePerformedActionIds = new ArrayList<>(); 
      
      updatePerformedActionIds.add(UUID.fromString(((List<String>) createdData.get("performedActionIds")).get(0)));
      updatePerformedActionIds.add(builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 3", "Testi viljely 3")).getId());

      Double updateLuminance = 123d;
      String updatePests = "No no";
      Double updateWeight = 8882d;
      
      CultivationObservationEventData updateData = new CultivationObservationEventData();
      updateData.setLuminance(updateLuminance);
      updateData.setPerformedActionIds(updatePerformedActionIds);
      updateData.setPests(updatePests);
      updateData.setWeight(updateWeight);
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }
  
  @Test
  public void testDeleteCultivationObservationEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event created = createCultivationObservationEvent(builder);
      Event found = builder.admin().events().findEvent(created.getId());
      assertEquals(created.getId(), found.getId());
      builder.admin().events().delete(created);
      builder.admin().events().assertFindFailStatus(404, created.getId());     
    }
  }
  
  @Test
  public void testListEvents() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions();
      
      createSowingEvent(builder);
      builder.admin().events().assertCount(1);
      createTableSpreadEvent(builder);
      builder.admin().events().assertCount(2);
      createCultivationObservationEvent(builder);
      builder.admin().events().assertCount(3);
    }
  }
  
  @Test
  public void testDeleteSeedPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createSowingEvent(builder);
      builder.anonymous().events().assertDeleteFailStatus(401, createdEvent);
      builder.invalid().events().assertDeleteFailStatus(401, createdEvent);
    }
  }
  
  @Test
  public void testUpdateEventPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event event = createSowingEvent(builder);
      builder.anonymous().events().assertUpdateFailStatus(401, event);
      builder.invalid().events().assertUpdateFailStatus(401, event);
    }
  }
  
  @Test
  public void testListPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      createSowingEvent(builder);
      
      builder.worker1().events().assertCount(1);
      builder.manager().events().assertCount(1);
      builder.admin().events().assertCount(1);
      builder.invalid().events().assertListFailStatus(401);
      builder.anonymous().events().assertListFailStatus(401);
    }
  }
  
  @Test
  public void testFindEventPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event event = createSowingEvent(builder);
      
      assertNotNull(builder.admin().events().findEvent(event.getId()));
      assertNotNull(builder.manager().events().findEvent(event.getId()));
      assertNotNull(builder.worker1().events().findEvent(event.getId()));
      builder.invalid().seeds().assertFindFailStatus(401, event.getId());
      builder.anonymous().seeds().assertFindFailStatus(401, event.getId());
    }
  }

  @Test
  public void testCreateEventPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      
      Batch batch = builder.admin().batches().create(product);
      OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Double amount = 12d;
      CellType cellType = CellType.LARGE;
      Integer gutterNumber = 2;
      ProductionLine productionLine = builder.admin().productionLines().create(4);
      SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
      
      builder.anonymous().events().assertCreateFailStatus(401, batch, startTime, endTime, amount, cellType, gutterNumber, productionLine, seedBatch);
      builder.invalid().events().assertCreateFailStatus(401, batch, startTime, endTime, amount, cellType, gutterNumber, productionLine, seedBatch);
    }
  }

  private Event createSowingEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);
    Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
    
    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    Double amount = 12d;
    CellType cellType = CellType.LARGE;
    Integer gutterNumber = 2;
    ProductionLine productionLine = builder.admin().productionLines().create(4);
    SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
    
    return builder.admin().events().createSowing(batch, startTime, endTime, amount, cellType, gutterNumber, productionLine, seedBatch);
  }

  private Event createTableSpreadEvent(TestBuilder builder) throws IOException {
    PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
    LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
    Product product = builder.admin().products().create(name, createdPackageSize);

    Batch batch = builder.admin().batches().create(product);
    OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
    OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
    String location = "Location";
    Integer tableCount = 15;
    
    return builder.admin().events().createTableSpread(batch, startTime, endTime, location, tableCount);
  }

  private Event createCultivationObservationEvent(TestBuilder builder) throws IOException {
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
  
}