package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for packageSizes
 * 
 * @author Ville Koivukangas
 */
public class PackageSizeTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8));
    }
  }

  @Test
  public void testCreatePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().packageSizes().assertCreateFailStatus(403, builder.createLocalizedEntry("Test PackageSize"));
      builder.anonymous().packageSizes().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PackageSize"));
      builder.invalid().packageSizes().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PackageSize"));
    }
  }
  
  @Test
  public void testFindPackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().packageSizes().assertFindFailStatus(404, UUID.randomUUID());
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      PackageSize foundPackageSize = builder.admin().packageSizes().findPackageSize(createdPackageSize.getId());
      assertEquals(createdPackageSize.getId(), foundPackageSize.getId());
      builder.admin().packageSizes().assertPackageSizeEqual(createdPackageSize, foundPackageSize);
    }
  }
  
  @Test
  public void testFindPackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      assertNotNull(builder.admin().packageSizes().findPackageSize(packageSize.getId()));
      assertNotNull(builder.manager().packageSizes().findPackageSize(packageSize.getId()));
      assertNotNull(builder.worker1().packageSizes().findPackageSize(packageSize.getId()));
      builder.invalid().packageSizes().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().packageSizes().assertFindFailStatus(401, packageSize.getId());
    }
  }
  
  @Test
  public void testListpackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      builder.admin().packageSizes().assertCount(1);
      builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize 2"), 8);
      builder.admin().packageSizes().assertCount(2);
    }
  }
  
  @Test
  public void testListPackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      builder.worker1().packageSizes().assertCount(1);
      builder.manager().packageSizes().assertCount(1);
      builder.admin().packageSizes().assertCount(1);
      builder.invalid().packageSizes().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().packageSizes().assertFindFailStatus(401, packageSize.getId());
    }
  }
  
  @Test
  public void testUpdatePackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      builder.admin().packageSizes().assertPackageSizeEqual(createdPackageSize, builder.admin().packageSizes().findPackageSize(createdPackageSize.getId()));
      
      PackageSize updatePackageSize = new PackageSize(); 
      updatePackageSize.setId(createdPackageSize.getId());
      updatePackageSize.setName(builder.createLocalizedEntry("Updated PackageSize"));
     
      builder.admin().packageSizes().updatePackageSize(updatePackageSize);
      builder.admin().packageSizes().assertPackageSizeEqual(updatePackageSize, builder.admin().packageSizes().findPackageSize(createdPackageSize.getId()));
    }
  }
  
  @Test
  public void testUpdatePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      builder.worker1().packageSizes().assertUpdateFailStatus(403, packageSize);
      builder.anonymous().packageSizes().assertUpdateFailStatus(401, packageSize);
      builder.invalid().packageSizes().assertUpdateFailStatus(401, packageSize);
    }
  }
  
  @Test
  public void testDeletepackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      PackageSize foundPackageSize = builder.admin().packageSizes().findPackageSize(createdPackageSize.getId());
      assertEquals(createdPackageSize.getId(), foundPackageSize.getId());
      builder.admin().packageSizes().delete(createdPackageSize);
      builder.admin().packageSizes().assertFindFailStatus(404, createdPackageSize.getId());     
    }
  }

  @Test
  public void testDeletePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      builder.worker1().packageSizes().assertDeleteFailStatus(403, packageSize);
      builder.anonymous().packageSizes().assertDeleteFailStatus(401, packageSize);
      builder.invalid().packageSizes().assertDeleteFailStatus(401, packageSize);
    }
  }
  
}