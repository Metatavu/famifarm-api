package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

/**
 * Tests for products
 * 
 * @author Ville Koivukangas
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class ProductTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSizeJoroinen = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      PackageSize createdPackageSizeJuva = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JUVA);

      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      List<HarvestEventType> allowedHarvestTypes = Lists.newArrayList(HarvestEventType.BAGGING);
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSizeJoroinen, createdPackageSizeJuva), allowedHarvestTypes, false);
      assertNotNull(product);
      assertNotNull(product.getDefaultPackageSizeIds());
      //Verify that only the package size of correct facility got assigned
      assertEquals(1, product.getDefaultPackageSizeIds().size());
      assertEquals(createdPackageSizeJoroinen.getId(), product.getDefaultPackageSizeIds().get(0));
      assertEquals(1, product.getAllowedHarvestTypes().size());
      assertEquals(HarvestEventType.BAGGING, product.getAllowedHarvestTypes().get(0));
      assertEquals(false, product.getIsSubcontractorProduct());
    }
  }

  @Test
  public void testCreateProductNoPackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, null, false, Facility.JOROINEN);
      assertNotNull(product);
      assertNotNull(product.getDefaultPackageSizeIds());
      assertEquals(0, product.getDefaultPackageSizeIds().size());
      assertEquals(false, product.getIsSubcontractorProduct());
    }
  }

  @Test
  public void testCreateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize);

      builder.workerJoroinen().products().assertCreateFailStatus(403, name, packageSizes);
      builder.anonymous().products().assertCreateFailStatus(401, name, packageSizes);
      builder.invalid().products().assertCreateFailStatus(401, name, packageSizes);
    }
  }
  
  @Test
  public void testFindProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().assertFindFailStatus(404, UUID.randomUUID(), Facility.JOROINEN);
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JUVA);
      builder.admin().products().assertFindFailStatus(404, createdProduct.getId(), Facility.JOROINEN);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId(), Facility.JUVA);
      assertEquals(createdProduct.getId(), foundProduct.getId());
    }
  }
  
  @Test
  public void testFindProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      assertNotNull(builder.admin().products().findProduct(packageSize.getId()));
      assertNotNull(builder.managerJoroinen().products().findProduct(packageSize.getId()));
      assertNotNull(builder.workerJoroinen().products().findProduct(packageSize.getId()));
      builder.invalid().products().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
      builder.anonymous().products().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testListProducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize8 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize8"), 8, Facility.JOROINEN);
      PackageSize createdPackageSize16 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize16"), 16, Facility.JOROINEN);

      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize8, createdPackageSize16);

      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().create(name, packageSizes, false, Facility.JOROINEN);
      builder.admin().products().assertCount(1, Facility.JOROINEN);
      builder.admin().products().create(name, packageSizes, false, Facility.JOROINEN);
      builder.admin().products().assertCount(2, Facility.JOROINEN);
      builder.admin().products().create(name, packageSizes, true, Facility.JUVA);
      builder.admin().products().assertCountWithSubcontractors(1, Facility.JUVA);

      builder.admin().products().create(name, packageSizes, null, false, false, Facility.JUVA, false);
      builder.admin().products().assertCountWithInactive(1, Facility.JUVA);
      builder.admin().products().assertCountWithInactiveAndSubcontractors(2, Facility.JUVA);
      builder.admin().products().assertCountWithInactiveAndSubcontractors(2, Facility.JOROINEN);
    }
  }

  @Test
  public void testIsEndProducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize8 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize8"), 8, Facility.JOROINEN);
      PackageSize createdPackageSize16 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize16"), 16, Facility.JOROINEN);

      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize8, createdPackageSize16);

      List<LocalizedValue> name = builder.createLocalizedEntry("Product name", "Tuotteen nimi");

      Product product1 = builder.admin().products().create(name, packageSizes, null, false, true, Facility.JOROINEN, true);
      Product product2 = builder.admin().products().create(name, packageSizes, null, false, true, Facility.JOROINEN, false);

      builder.admin().products().assertCount(2, Facility.JOROINEN);
      builder.admin().products().assertCountWithEndProduct(1, Facility.JOROINEN);
      assertEquals(true, product1.getIsEndProduct());
      assertEquals(false, product2.getIsEndProduct());
    }
  }

  @Test
  public void testListProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      builder.workerJoroinen().products().assertCount(1, Facility.JOROINEN);
      builder.managerJoroinen().products().assertCount(1, Facility.JOROINEN);
      builder.admin().products().assertCount(1, Facility.JOROINEN);
      builder.invalid().products().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
      builder.anonymous().products().assertFindFailStatus(401, packageSize.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testUpdateProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize8 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize8"), 8, Facility.JOROINEN);

      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize8), false, Facility.JOROINEN);
      
      Product updateProduct = new Product(); 
      updateProduct.setId(createdProduct.getId());
      updateProduct.setIsSubcontractorProduct(true);
      
      name = builder.createLocalizedEntry("Updated name", "Tuotteen nimi");
      updateProduct.setName(name);
      updateProduct.setActive(true);
      updateProduct.setAllowedHarvestTypes(Lists.newArrayList(HarvestEventType.BOXING, HarvestEventType.CUTTING));
      updateProduct.setDefaultPackageSizeIds(Lists.newArrayList());
      updateProduct.setIsEndProduct(false);

      Product updatedProduct = builder.admin().products().updateProduct(updateProduct, Facility.JOROINEN);
      assertEquals(updateProduct.getId(), builder.admin().products().findProduct(createdProduct.getId()).getId());
      assertNotNull(updatedProduct.getDefaultPackageSizeIds());
      assertEquals(2, updatedProduct.getAllowedHarvestTypes().size());
      assertTrue(updateProduct.getAllowedHarvestTypes().contains(HarvestEventType.BOXING));
      assertTrue(updateProduct.getAllowedHarvestTypes().contains(HarvestEventType.CUTTING));
      assertEquals(0, updatedProduct.getDefaultPackageSizeIds().size());
      assertEquals(true, updatedProduct.getIsSubcontractorProduct());

      updatedProduct.setAllowedHarvestTypes(Lists.newArrayList(HarvestEventType.BAGGING));
      updatedProduct = builder.admin().products().updateProduct(updatedProduct, Facility.JOROINEN);
      assertEquals(1, updatedProduct.getAllowedHarvestTypes().size());
      assertTrue(updatedProduct.getAllowedHarvestTypes().contains(HarvestEventType.BAGGING));

      updatedProduct.setAllowedHarvestTypes(ImmutableList.of());
      builder.admin().products().assertUpdateFailStatus(404, updatedProduct, Facility.JUVA);
      updatedProduct = builder.admin().products().updateProduct(updatedProduct, Facility.JOROINEN);
      assertEquals(0, updatedProduct.getAllowedHarvestTypes().size());
    }
  }

  @Test
  public void testUpdateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      builder.workerJoroinen().products().assertUpdateFailStatus(403, packageSize, Facility.JOROINEN);
      builder.anonymous().products().assertUpdateFailStatus(401, packageSize, Facility.JOROINEN);
      builder.invalid().products().assertUpdateFailStatus(401, packageSize, Facility.JOROINEN);
    }
  }
  
  @Test
  public void testDeleteproducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId());
      assertEquals(createdProduct.getId(), foundProduct.getId());
      builder.admin().products().assertDeleteFailStatus(404, createdProduct, Facility.JUVA);
      builder.admin().products().delete(createdProduct, Facility.JOROINEN);
      builder.admin().products().assertFindFailStatus(404, createdProduct.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testDeleteProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      builder.workerJoroinen().products().assertDeleteFailStatus(403, packageSize, Facility.JOROINEN);
      builder.anonymous().products().assertDeleteFailStatus(401, packageSize, Facility.JOROINEN);
      builder.invalid().products().assertDeleteFailStatus(401, packageSize, Facility.JOROINEN);
    }
  }

}