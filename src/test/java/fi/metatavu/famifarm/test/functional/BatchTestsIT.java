package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for batches
 * 
 * @author Ville Koivukangas
 */
public class BatchTestsIT {

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