package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.BatchPhase;
import fi.metatavu.famifarm.client.model.PotType;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.EventType;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for batches
 * 
 * @author Ville Koivukangas
 */
public class BatchTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      assertNotNull(builder.admin().batches().create(product));
    }
  }

  @Test
  public void testCreateBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      builder.anonymous().batches().assertCreateFailStatus(401, product);
      builder.invalid().batches().assertCreateFailStatus(401, product);
    }
  }
  
  @Test
  public void testFindBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      builder.admin().batches().assertFindFailStatus(404, UUID.randomUUID());
      Batch createdBatch = builder.admin().batches().create(product);
      Batch foundBatch = builder.admin().batches().findBatch(createdBatch.getId());
      assertEquals(createdBatch.getId(), foundBatch.getId());
    }
  }
  
  @Test
  public void testFindBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch packageSize = builder.admin().batches().create(product);
      assertNotNull(builder.admin().batches().findBatch(packageSize.getId()));
      assertNotNull(builder.manager().batches().findBatch(packageSize.getId()));
      assertNotNull(builder.worker1().batches().findBatch(packageSize.getId()));
      builder.invalid().batches().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().batches().assertFindFailStatus(401, packageSize.getId());
    }
  }
  
  @Test
  public void testListbatches() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      builder.admin().batches().create(product);
      builder.admin().batches().assertCount(1);
      builder.admin().batches().create(product);
      builder.admin().batches().assertCount(2);
      
      builder.admin().batches().assertCountWithCreatedTimes(2, null, null, null, OffsetDateTime.now().plus(Period.ofDays(1)), null);
      builder.admin().batches().assertCountWithCreatedTimes(0, null, null, null, OffsetDateTime.now().minus(Period.ofDays(1)), null);
      builder.admin().batches().assertCountWithCreatedTimes(0, null, null, null, null, OffsetDateTime.now().plus(Period.ofDays(1)));
      builder.admin().batches().assertCountWithCreatedTimes(2, null, null, null, null, OffsetDateTime.now().minus(Period.ofDays(1)));
      builder.admin().batches().assertCountWithCreatedTimes(2, null, null, null, OffsetDateTime.now().plus(Period.ofDays(1)), OffsetDateTime.now().minus(Period.ofDays(1)));
    }
  }
  
  @Test
  public void testListBatchesByProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product1 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      Product product2 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      Product product3 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      
      builder.admin().batches().create(product1);
      builder.admin().batches().create(product2);
      builder.admin().batches().create(product2);

      builder.admin().batches().assertCountByProduct(1, product1);
      builder.admin().batches().assertCountByProduct(2, product2);
      builder.admin().batches().assertCountByProduct(0, product3);
    }
  }

  @Test
  public void testListBatchesByPhase() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      
      Batch sowingPhaseBatch = builder.admin().batches().create(product, BatchPhase.SOWING);
      builder.admin().batches().create(product, BatchPhase.HARVEST);
      builder.admin().batches().create(product, BatchPhase.HARVEST);
      
      builder.admin().batches().assertCountByPhase(1, BatchPhase.SOWING);
      builder.admin().batches().assertCountByPhase(2, BatchPhase.HARVEST);

      sowingPhaseBatch.setPhase(BatchPhase.HARVEST);
      builder.admin().batches().updateBatch(sowingPhaseBatch);

      builder.admin().batches().assertCountByPhase(3, BatchPhase.HARVEST);
    }
  }
  
  @Test
  public void testListBatchesByStatus() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().packageSizes();
      builder.admin().products();
      builder.admin().productionLines();
      builder.admin().seeds();
      builder.admin().seedBatches();
      builder.admin().wastageReasons();
      
      builder.admin().batches().assertCount(0);
      builder.admin().batches().assertCountByStatus(0, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      ProductionLine productionLine = builder.admin().productionLines().create("4", 8);
      SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC));
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      
      Batch openBatch1 = builder.admin().batches().create(product);
      builder.admin().events().createSowing(openBatch1, OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 200, PotType.LARGE, productionLine, Arrays.asList(seedBatch));

      builder.admin().batches().assertCount(1);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");

      Batch openBatch2 = builder.admin().batches().create(product);
      builder.admin().events().createSowing(openBatch2, OffsetDateTime.of(2020, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 10, PotType.LARGE, productionLine, Arrays.asList(seedBatch));
      builder.admin().events().createSowing(openBatch2, OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 30, PotType.LARGE, productionLine, Arrays.asList(seedBatch));
      builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), (40 * getPotTypeAmount(PotType.LARGE) - 5), wastageReason, null, EventType.HARVEST, productionLine.getId());

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(2, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");
      
      Event updateWasteage = builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 5, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), 5, wastageReason, null, EventType.HARVEST, productionLine.getId());

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(1, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");

      builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 6, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), 3, wastageReason, null, EventType.HARVEST, productionLine.getId());

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(1, "NEGATIVE");
      
      updateWasteage.setData(builder.admin().events().createWastageEventData(2, wastageReason, EventType.HARVEST, productionLine.getId()));
      builder.admin().events().updateEvent(updateWasteage);
      
      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");
      builder.admin().batches().assertCountByStatus(1, "CLOSED");
    }
  }
  
  @Test
  public void testListBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch packageSize = builder.admin().batches().create(product);
      builder.worker1().batches().assertCount(1);
      builder.manager().batches().assertCount(1);
      builder.admin().batches().assertCount(1);
      builder.invalid().batches().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().batches().assertFindFailStatus(401, packageSize.getId());
    }
  }
  
  @Test
  public void testUpdateBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch createdBatch = builder.admin().batches().create(product);
      
      Batch updateBatch = new Batch();
      updateBatch.setPhase(BatchPhase.PLANTING); 
      updateBatch.setId(createdBatch.getId());
      
      name = builder.createLocalizedEntry("Updated name", "Tuotteen nimi");
      Product updatedProduct = builder.admin().products().create(name, createdPackageSize);
      
      updateBatch.setProductId(updatedProduct.getId());
     
      builder.admin().batches().updateBatch(updateBatch);
      assertEquals(updateBatch.getId(), builder.admin().batches().findBatch(createdBatch.getId()).getId());
    }
  }
  
  @Test
  public void testUpdateBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch batch = builder.admin().batches().create(product);
      builder.anonymous().batches().assertUpdateFailStatus(401, batch);
      builder.invalid().batches().assertUpdateFailStatus(401, batch);
    }
  }
  
  @Test
  public void testDeletebatches() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch createdBatch = builder.admin().batches().create(product);
      Batch foundBatch = builder.admin().batches().findBatch(createdBatch.getId());
      assertEquals(createdBatch.getId(), foundBatch.getId());
      builder.admin().batches().delete(createdBatch);
      builder.admin().batches().assertFindFailStatus(404, createdBatch.getId());     
    }
  }

  @Test
  public void testDeleteBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch packageSize = builder.admin().batches().create(product);
      builder.worker1().batches().assertDeleteFailStatus(403, packageSize);
      builder.anonymous().batches().assertDeleteFailStatus(401, packageSize);
      builder.invalid().batches().assertDeleteFailStatus(401, packageSize);
    }
  }

  @Test
  public void testBatchSowingLineNumbers() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      ProductionLine productionLine6a = builder.admin().productionLines().create("6a", 8);
      ProductionLine productionLine7a = builder.admin().productionLines().create("7a", 8);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Seed"));
      SeedBatch seedBatch = builder.admin().seedBatches().create("Code", seed, OffsetDateTime.now());
      Batch batch = builder.admin().batches().create(product, BatchPhase.SOWING);
      assertEquals(0, batch.getSowingLineNumbers().size());
      
      builder.admin().events().createSowing(batch, OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 200, PotType.LARGE, productionLine6a, Arrays.asList(seedBatch));
      batch = builder.admin().batches().findBatch(batch.getId());
      assertEquals(1, batch.getSowingLineNumbers().size());
      assertEquals("6a", batch.getSowingLineNumbers().get(0));

      builder.admin().events().createSowing(batch, OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 200, PotType.LARGE, productionLine7a, Arrays.asList(seedBatch));
      batch = builder.admin().batches().findBatch(batch.getId());
      assertEquals(2, batch.getSowingLineNumbers().size());
      assertThat(batch.getSowingLineNumbers(), containsInAnyOrder("6a", "7a"));

      builder.admin().events().createSowing(batch, OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 200, PotType.LARGE, productionLine7a, Arrays.asList(seedBatch));
      batch = builder.admin().batches().findBatch(batch.getId());
      assertEquals(2, batch.getSowingLineNumbers().size());
      assertThat(batch.getSowingLineNumbers(), containsInAnyOrder("6a", "7a"));
    }
  }
  
}