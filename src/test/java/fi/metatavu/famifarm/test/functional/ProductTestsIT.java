package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      assertNotNull(product);
      assertNotNull(product.getDefaultPackageSizeIds());
      assertEquals(1, product.getDefaultPackageSizeIds().size());
      assertEquals(false, product.getIsSubcontractorProduct());
    }
  }

  @Test
  public void testCreateProductNoPackageSizes() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, null, false);
      assertNotNull(product);
      assertNotNull(product.getDefaultPackageSizeIds());
      assertEquals(0, product.getDefaultPackageSizeIds().size());
      assertEquals(false, product.getIsSubcontractorProduct());
    }
  }

  @Test
  public void testCreateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize);

      builder.worker1().products().assertCreateFailStatus(403, name, packageSizes);
      builder.anonymous().products().assertCreateFailStatus(401, name, packageSizes);
      builder.invalid().products().assertCreateFailStatus(401, name, packageSizes);
    }
  }
  
  @Test
  public void testFindProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().assertFindFailStatus(404, UUID.randomUUID());
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId());
      assertEquals(createdProduct.getId(), foundProduct.getId());
    }
  }
  
  @Test
  public void testFindProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      assertNotNull(builder.admin().products().findProduct(packageSize.getId()));
      assertNotNull(builder.manager().products().findProduct(packageSize.getId()));
      assertNotNull(builder.worker1().products().findProduct(packageSize.getId()));
      builder.invalid().products().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().products().assertFindFailStatus(401, packageSize.getId());
    }
  }

  @Test
  public void testListProducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize8 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize8"), 8);
      PackageSize createdPackageSize16 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize16"), 16);

      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize8, createdPackageSize16);

      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().create(name, packageSizes, false);
      builder.admin().products().assertCount(1);
      builder.admin().products().create(name, packageSizes, false);
      builder.admin().products().assertCount(2);
      builder.admin().products().create(name, packageSizes, true);
      builder.admin().products().assertCount(2);
      builder.admin().products().assertCountWithSubcontractors(3);
      builder.admin().products().create(name, packageSizes, false, false);
      builder.admin().products().assertCount(2);
      builder.admin().products().assertCountWithInactive(3);
      builder.admin().products().assertCountWithInactiveAndSubcontractors(4);
    }
  }

  @Test
  public void testListProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      builder.worker1().products().assertCount(1);
      builder.manager().products().assertCount(1);
      builder.admin().products().assertCount(1);
      builder.invalid().products().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().products().assertFindFailStatus(401, packageSize.getId());
    }
  }

  @Test
  public void testUpdateProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize8 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize8"), 8);

      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize8), false);
      
      Product updateProduct = new Product(); 
      updateProduct.setId(createdProduct.getId());
      updateProduct.setIsSubcontractorProduct(true);
      
      name = builder.createLocalizedEntry("Updated name", "Tuotteen nimi");
      updateProduct.setName(name);
      updateProduct.setActive(true);
      updateProduct.setDefaultPackageSizeIds(Lists.newArrayList());

      Product updatedProduct = builder.admin().products().updateProduct(updateProduct);
      assertEquals(updateProduct.getId(), builder.admin().products().findProduct(createdProduct.getId()).getId());
      assertNotNull(updatedProduct.getDefaultPackageSizeIds());
      assertEquals(0, updatedProduct.getDefaultPackageSizeIds().size());
      assertEquals(true, updatedProduct.getIsSubcontractorProduct());
    }
  }

  @Test
  public void testUpdateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      builder.worker1().products().assertUpdateFailStatus(403, packageSize);
      builder.anonymous().products().assertUpdateFailStatus(401, packageSize);
      builder.invalid().products().assertUpdateFailStatus(401, packageSize);
    }
  }
  
  @Test
  public void testDeleteproducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product createdProduct = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId());
      assertEquals(createdProduct.getId(), foundProduct.getId());
      builder.admin().products().delete(createdProduct);
      builder.admin().products().assertFindFailStatus(404, createdProduct.getId());     
    }
  }

  @Test
  public void testDeleteProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);
      builder.worker1().products().assertDeleteFailStatus(403, packageSize);
      builder.anonymous().products().assertDeleteFailStatus(401, packageSize);
      builder.invalid().products().assertDeleteFailStatus(401, packageSize);
    }
  }

}