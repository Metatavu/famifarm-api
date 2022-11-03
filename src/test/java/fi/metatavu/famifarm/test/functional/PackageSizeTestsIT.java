package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

/**
 * Tests for packageSizes
 * 
 * @author Ville Koivukangas
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class PackageSizeTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN));
    }
  }

  @Test
  public void testCreatePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().packageSizes().assertCreateFailStatus(403, builder.createLocalizedEntry("Test PackageSize"), Facility.JOROINEN);
      builder.anonymous().packageSizes().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PackageSize"), Facility.JOROINEN);
      builder.invalid().packageSizes().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PackageSize"), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testFindPackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().packageSizes().assertFindFailStatus(404, UUID.randomUUID(), Facility.JOROINEN);
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      PackageSize foundPackageSize = builder.admin().packageSizes().findPackageSize(createdPackageSize.getId(), Facility.JOROINEN);
      builder.admin().packageSizes().assertFindFailStatus(404, createdPackageSize.getId(), Facility.JUVA);
      assertEquals(createdPackageSize.getId(), foundPackageSize.getId());
      builder.admin().packageSizes().assertPackageSizeEqual(createdPackageSize, foundPackageSize);
    }
  }
  
  @Test
  public void testFindPackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      assertNotNull(builder.admin().packageSizes().findPackageSize(packageSize.getId(), Facility.JOROINEN));
      assertNotNull(builder.manager().packageSizes().findPackageSize(packageSize.getId(), Facility.JOROINEN));
      assertNotNull(builder.worker1().packageSizes().findPackageSize(packageSize.getId(), Facility.JOROINEN));
      builder.invalid().packageSizes().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
      builder.anonymous().packageSizes().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testListpackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize 2"), 8, Facility.JUVA);
      builder.admin().packageSizes().assertCount(2, Facility.JOROINEN);
      builder.admin().packageSizes().assertCount(1, Facility.JUVA);
    }
  }
  
  @Test
  public void testListPackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.worker1().packageSizes().assertCount(1, Facility.JOROINEN);
      builder.manager().packageSizes().assertCount(1, Facility.JOROINEN);
      builder.admin().packageSizes().assertCount(1, Facility.JOROINEN);
      builder.invalid().packageSizes().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
      builder.anonymous().packageSizes().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
    }
  }
  
  @Test
  public void testUpdatePackageSize() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.admin().packageSizes().assertPackageSizeEqual(createdPackageSize, builder.admin().packageSizes().findPackageSize(createdPackageSize.getId(), Facility.JOROINEN));
      
      PackageSize updatePackageSize = new PackageSize(); 
      updatePackageSize.setId(createdPackageSize.getId());
      updatePackageSize.setName(builder.createLocalizedEntry("Updated PackageSize"));
      updatePackageSize.setSize(12);

      builder.admin().packageSizes().updatePackageSize(updatePackageSize, Facility.JOROINEN);
      builder.admin().packageSizes().assertUpdateFailStatus(404, updatePackageSize, Facility.JUVA);
      builder.admin().packageSizes().assertPackageSizeEqual(updatePackageSize, builder.admin().packageSizes().findPackageSize(createdPackageSize.getId(), Facility.JOROINEN));
    }
  }
  
  @Test
  public void testUpdatePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.worker1().packageSizes().assertUpdateFailStatus(403, packageSize, Facility.JOROINEN);
      builder.anonymous().packageSizes().assertUpdateFailStatus(401, packageSize, Facility.JOROINEN);
      builder.invalid().packageSizes().assertUpdateFailStatus(401, packageSize, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testDeletepackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      PackageSize foundPackageSize = builder.admin().packageSizes().findPackageSize(createdPackageSize.getId(), Facility.JOROINEN);
      assertEquals(createdPackageSize.getId(), foundPackageSize.getId());
      builder.admin().packageSizes().assertDeleteFailStatus(404, createdPackageSize, Facility.JUVA);
      builder.admin().packageSizes().delete(createdPackageSize, Facility.JOROINEN);
      builder.admin().packageSizes().assertFindFailStatus(404, createdPackageSize.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testDeletePackageSizePermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      builder.worker1().packageSizes().assertDeleteFailStatus(403, packageSize, Facility.JOROINEN);
      builder.anonymous().packageSizes().assertDeleteFailStatus(401, packageSize, Facility.JOROINEN);
      builder.invalid().packageSizes().assertDeleteFailStatus(401, packageSize, Facility.JOROINEN);
    }
  }
  
}