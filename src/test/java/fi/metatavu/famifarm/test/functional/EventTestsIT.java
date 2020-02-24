package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.PotType;
import fi.metatavu.famifarm.client.model.CultivationObservationEventData;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.EventType;
import fi.metatavu.famifarm.client.model.HarvestEventData;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.PackingEventData;
import fi.metatavu.famifarm.client.model.PlantingEventData;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.SowingEventData;
import fi.metatavu.famifarm.client.model.TableSpreadEventData;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.client.model.WastageEventData;
import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for seeds
 * 
 * @author Antti Leppä
 */
public class EventTestsIT extends AbstractFunctionalTest {

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
  
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateSowingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket new", "Rucola uusi"));

      SeedBatch seedBatch1 = builder.admin().seedBatches().create("123", seed, OffsetDateTime.now());
      SeedBatch seedBatch2 = builder.admin().seedBatches().create("123", seed, OffsetDateTime.now());
      SeedBatch seedBatch3 = builder.admin().seedBatches().create("123", seed, OffsetDateTime.now());
      
      PackageSize createPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 8);
      Product createProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name", "Tuotteen nimi"), createPackageSize);
      Batch createBatch = builder.admin().batches().create(createProduct);
      ProductionLine createProductionLine = builder.admin().productionLines().create("5", null, 7);
      Event createdEvent = builder.admin().events().createSowing(createBatch, OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 1, PotType.LARGE, createProductionLine, Arrays.asList(seedBatch1, seedBatch2));

      Map<String, Object> createdData = (Map<String, Object>) createdEvent.getData();
      List<String> createdSeedBatchIds = (List<String>) createdData.get("seedBatchIds");
      assertEquals(2, createdSeedBatchIds.size());      
      assertThat(createdSeedBatchIds, containsInAnyOrder(seedBatch1.getId().toString(), seedBatch2.getId().toString()));
      
      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 8);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Integer updateAmount = 14;
      PotType updatePotType = PotType.SMALL;
      ProductionLine updateProductionLine = builder.admin().productionLines().create("7", null, 8);
      
      SowingEventData updateData = new SowingEventData();
      updateData.setAmount(updateAmount);
      updateData.setPotType(updatePotType);
      updateData.setProductionLineId(updateProductionLine.getId());
      updateData.setSeedBatchIds(Arrays.asList(seedBatch2.getId(), seedBatch3.getId()));
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(EventType.SOWING);
      updateEvent.setUserId(createdEvent.getUserId());
      setEventRemainingUnits(updateEvent, (updateAmount * getPotTypeAmount(updatePotType)));
      
      builder.admin().events().updateEvent(updateEvent);
      Event updatedEvent = builder.admin().events().findEvent(createdEvent.getId());
      builder.admin().events().assertEventsEqual(updateEvent, updatedEvent);
      
      Map<String, Object> updatedData = (Map<String, Object>) updatedEvent.getData();
      List<String> updatedSeedBatchIds = (List<String>) updatedData.get("seedBatchIds");
      assertEquals(2, updatedSeedBatchIds.size());      
      assertThat(updatedSeedBatchIds, containsInAnyOrder(seedBatch2.getId().toString(), seedBatch3.getId().toString()));
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
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 7);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Integer updateTrayCount = 82;
      
      TableSpreadEventData updateData = new TableSpreadEventData();
      updateData.setTrayCount(updateTrayCount);

      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      setEventRemainingUnits(updateEvent, createdEvent.getRemainingUnits());
      
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
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 6);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      
      List<UUID> updatePerformedActionIds = new ArrayList<>(); 
      
      updatePerformedActionIds.add(UUID.fromString(((List<String>) createdData.get("performedActionIds")).get(0)));
      updatePerformedActionIds.add(builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 3", "Testi viljely 3")).getId());

      List<UUID> updatedPestIds = new ArrayList<>();
      updatedPestIds.add(UUID.fromString(((List<String>) createdData.get("pestIds")).get(0)));
      updatedPestIds.add(builder.admin().pests().create(builder.createLocalizedEntry("Test PerformedCultivationAction 3", "Testi viljely 3")).getId());
      
      Double updateLuminance = 123d;
      Double updateWeight = 8882d;
      
      CultivationObservationEventData updateData = new CultivationObservationEventData();
      updateData.setLuminance(updateLuminance);
      updateData.setPerformedActionIds(updatePerformedActionIds);
      updateData.setPestIds(updatedPestIds);
      updateData.setWeight(updateWeight);
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      setEventRemainingUnits(updateEvent, createdEvent.getRemainingUnits());
      
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
  public void testCreateHarvestEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createHarvestEvent(builder));
    }
  }
  
  @Test
  public void testFindHarvestEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createHarvestEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @Test
  public void testUpdateHarvestEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createHarvestEvent(builder);
      @SuppressWarnings("unchecked") Map<String, Object> createdData = (Map<String, Object>) createdEvent.getData();
      Integer createdAmount = (Integer) createdData.get("gutterCount");

      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 8);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);

      Team updateTeam = builder.admin().teams().create(builder.createLocalizedEntry("new team name", "uusi tiimi"));

      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      ProductionLine updateProductionLine = builder.admin().productionLines().create("7", null, 8);
      
      HarvestEventData updateData = new HarvestEventData();
      updateData.setProductionLineId(updateProductionLine.getId());
      updateData.setType(fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum.CUTTING);
      updateData.setTeamId(updateTeam.getId());
      updateData.setGutterCount(createdAmount);

      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      setEventRemainingUnits(updateEvent, createdEvent.getRemainingUnits());
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }

  @Test
  public void testDeleteHarvestEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createHarvestEvent(builder);
      Event foundSeed = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundSeed.getId());
      builder.admin().events().delete(createdEvent);
      builder.admin().events().assertFindFailStatus(404, createdEvent.getId());     
    }
  }
  
  @Test
  public void testCreatePlantingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createPlantingEvent(builder));
    }
  }
  
  @Test
  public void testFindPlantingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createPlantingEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @Test
  public void testUpdatePlantingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createPlantingEvent(builder);
      
      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));
      
      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 7);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);
     
      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Integer updateGutterSize = 24;
      ProductionLine updateProductionLine = builder.admin().productionLines().create("7", null, 7);
      
      PlantingEventData updateData = new PlantingEventData();
      updateData.setGutterCount(6);
      updateData.setGutterHoleCount(updateGutterSize);
      updateData.setProductionLineId(updateProductionLine.getId());
      updateData.setTrayCount(7);
      updateData.setWorkerCount(8);
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      setEventRemainingUnits(updateEvent, createdEvent.getRemainingUnits());
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }
  
  @Test
  public void testDeleteWastageEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createWastageEvent(builder);
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().delete(createdEvent);
      builder.admin().events().assertFindFailStatus(404, createdEvent.getId());     
    }
  }
  

  @Test
  public void testCreateWastageEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(createWastageEvent(builder));
    }
  }
  
  @Test
  public void testFindWastageEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createWastageEvent(builder);
      builder.admin().events().assertFindFailStatus(404, UUID.randomUUID());
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().assertEventsEqual(createdEvent, foundEvent);
    }
  }
  
  @Test
  public void testUpdateWastageEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createWastageEvent(builder);

      builder.admin().events().assertEventsEqual(createdEvent, builder.admin().events().findEvent(createdEvent.getId()));

      PackageSize updatePackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("New Test PackageSize"), 8);
      Product updateProduct = builder.admin().products().create(builder.createLocalizedEntry("Product name new", "Tuotteen nimi uusi"), updatePackageSize);

      Batch updateBatch = builder.admin().batches().create(updateProduct);
      OffsetDateTime updateStartTime = OffsetDateTime.of(2020, 3, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime updateEndTime = OffsetDateTime.of(2020, 3, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      WastageReason updateWastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("New reason", "Uusi syy"));
      Integer updateAmount = 222;
      String updateAdditionalInformation = "New description";

      WastageEventData updateData = new WastageEventData();
      updateData.setAmount(updateAmount);
      updateData.setReasonId(updateWastageReason.getId());
      
      Event updateEvent = new Event(); 
      updateEvent.setId(createdEvent.getId());
      updateEvent.setBatchId(updateBatch.getId());
      updateEvent.setData(updateData);
      updateEvent.setEndTime(updateStartTime);
      updateEvent.setStartTime(updateEndTime);
      updateEvent.setType(createdEvent.getType());
      updateEvent.setUserId(createdEvent.getUserId());
      updateEvent.setAdditionalInformation(updateAdditionalInformation);
      setEventRemainingUnits(updateEvent, -updateAmount);
      
      builder.admin().events().updateEvent(updateEvent);
      builder.admin().events().assertEventsEqual(updateEvent, builder.admin().events().findEvent(createdEvent.getId()));
    }
  }
  
  @Test
  public void testDeletePlantingEvent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Event createdEvent = createPlantingEvent(builder);
      Event foundEvent = builder.admin().events().findEvent(createdEvent.getId());
      assertEquals(createdEvent.getId(), foundEvent.getId());
      builder.admin().events().delete(createdEvent);
      builder.admin().events().assertFindFailStatus(404, createdEvent.getId());     
    }
  }
  
  @Test
  public void testListEvents() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      builder.admin().teams();
      
      createSowingEvent(builder);
      builder.admin().events().assertCount(1);
      createTableSpreadEvent(builder);
      builder.admin().events().assertCount(2);
      createCultivationObservationEvent(builder);
      builder.admin().events().assertCount(3);
      createHarvestEvent(builder);
      builder.admin().events().assertCount(4);
      createPlantingEvent(builder);
      builder.admin().events().assertCount(5);
      createPackingEvent(builder);
      builder.admin().events().assertCount(6);
    }
  }

  @Test
  public void testEventListFilters() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions();
      builder.admin().teams();
      
      createSowingEvent(builder);
      builder.admin().events().assertCount(1);
      createTableSpreadEvent(builder);
      builder.admin().events().assertCount(2);

      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      createSowingEvent(builder, batch);
      createTableSpreadEvent(builder, batch);

      builder.admin().events().assertCount(4);
      builder.admin().events().assertCount(batch.getId(), 2);
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      
      Batch batch = builder.admin().batches().create(product);
      OffsetDateTime startTime = OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC);
      Integer amount = 12;
      PotType potType = PotType.LARGE;
      ProductionLine productionLine = builder.admin().productionLines().create("4", null, 8);
      SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, startTime);
      
      builder.anonymous().events().assertCreateFailStatus(401, batch, startTime, endTime, amount, potType, productionLine, Arrays.asList(seedBatch));
      builder.invalid().events().assertCreateFailStatus(401, batch, startTime, endTime, amount, potType, productionLine, Arrays.asList(seedBatch));
    }
  }

}