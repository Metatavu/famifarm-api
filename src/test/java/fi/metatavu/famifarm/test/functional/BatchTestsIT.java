package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.Event;
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      assertNotNull(builder.admin().batches().create(product));
    }
  }

  @Test
  public void testCreateBatchPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      builder.worker1().batches().assertCreateFailStatus(403, product);
      builder.anonymous().batches().assertCreateFailStatus(401, product);
      builder.invalid().batches().assertCreateFailStatus(401, product);
    }
  }
  
  @Test
  public void testFindBatch() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), createdPackageSize);
      Seed seed = builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      ProductionLine productionLine = builder.admin().productionLines().create(4);
      SeedBatch seedBatch = builder.admin().seedBatches().create("123", seed, OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC));
      WastageReason wastageReason = builder.admin().wastageReasons().create(builder.createLocalizedEntry("Test WastageReason", "Testi Syy"));
      
      
      Batch openBatch1 = builder.admin().batches().create(product);
      builder.admin().events().createSowing(openBatch1, OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 200d, CellType.LARGE, productionLine, seedBatch);

      builder.admin().batches().assertCount(1);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");

      Batch openBatch2 = builder.admin().batches().create(product);
      builder.admin().events().createSowing(openBatch2, OffsetDateTime.of(2020, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 10d, CellType.LARGE, productionLine, seedBatch);
      builder.admin().events().createSowing(openBatch2, OffsetDateTime.of(2020, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 3, 4, 10, 6, 0, ZoneOffset.UTC), 30d, CellType.LARGE, productionLine, seedBatch);
      builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), 35, wastageReason, null);

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(2, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");
      
      Event updateWasteage = builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 5, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), 5, wastageReason, null);

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(1, "CLOSED");
      builder.admin().batches().assertCountByStatus(0, "NEGATIVE");

      builder.admin().events().createWastage(openBatch2, OffsetDateTime.of(2020, 2, 6, 4, 5, 6, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 2, 4, 4, 5, 6, 0, ZoneOffset.UTC), 3, wastageReason, null);

      builder.admin().batches().assertCount(2);
      builder.admin().batches().assertCountByStatus(1, "OPEN");
      builder.admin().batches().assertCountByStatus(0, "CLOSED");
      builder.admin().batches().assertCountByStatus(1, "NEGATIVE");
      
      updateWasteage.setData(builder.admin().events().createWastageEventData(2, wastageReason, null));
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch createdBatch = builder.admin().batches().create(product);
      
      Batch updateBatch = new Batch(); 
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch batch = builder.admin().batches().create(product);
      builder.worker1().batches().assertUpdateFailStatus(403, batch);
      builder.anonymous().batches().assertUpdateFailStatus(401, batch);
      builder.invalid().batches().assertUpdateFailStatus(401, batch);
    }
  }
  
  @Test
  public void testDeletebatches() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create("Test PackageSize");
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      
      Batch packageSize = builder.admin().batches().create(product);
      builder.worker1().batches().assertDeleteFailStatus(403, packageSize);
      builder.anonymous().batches().assertDeleteFailStatus(401, packageSize);
      builder.invalid().batches().assertDeleteFailStatus(401, packageSize);
    }
  }
  
}