package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for products
 * 
 * @author Ville Koivukangas
 */
public class ProductTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      assertNotNull(builder.admin().products().create(name, createdPackageSize));
    }
  }

  @Test
  public void testCreateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.worker1().products().assertCreateFailStatus(403, name, createdPackageSize);
      builder.anonymous().products().assertCreateFailStatus(401, name, createdPackageSize);
      builder.invalid().products().assertCreateFailStatus(401, name, createdPackageSize);
    }
  }
  
  @Test
  public void testFindProduct() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().assertFindFailStatus(404, UUID.randomUUID());
      Product createdProduct = builder.admin().products().create(name, createdPackageSize);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId());
      assertEquals(createdProduct.getId(), foundProduct.getId());
    }
  }
  
  @Test
  public void testFindProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, createdPackageSize);
      assertNotNull(builder.admin().products().findProduct(packageSize.getId()));
      assertNotNull(builder.manager().products().findProduct(packageSize.getId()));
      assertNotNull(builder.worker1().products().findProduct(packageSize.getId()));
      builder.invalid().products().assertFindFailStatus(401, packageSize.getId());
      builder.anonymous().products().assertFindFailStatus(401, packageSize.getId());
    }
  }
  
  @Test
  public void testListproducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      builder.admin().products().create(name, createdPackageSize);
      builder.admin().products().assertCount(1);
      builder.admin().products().create(name, createdPackageSize);
      builder.admin().products().assertCount(2);
    }
  }
  
  @Test
  public void testListProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, createdPackageSize);
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
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product createdProduct = builder.admin().products().create(name, createdPackageSize);
      
      Product updateProduct = new Product(); 
      updateProduct.setId(createdProduct.getId());
      
      name = builder.createLocalizedEntry("Updated name", "Tuotteen nimi");
      updateProduct.setName(name);
      updateProduct.setDefaultPackageSizeId(createdPackageSize.getId());
     
      builder.admin().products().updateProduct(updateProduct);
      assertEquals(updateProduct.getId(), builder.admin().products().findProduct(createdProduct.getId()).getId());
    }
  }
  
  @Test
  public void testUpdateProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, createdPackageSize);
      builder.worker1().products().assertUpdateFailStatus(403, packageSize);
      builder.anonymous().products().assertUpdateFailStatus(401, packageSize);
      builder.invalid().products().assertUpdateFailStatus(401, packageSize);
    }
  }
  
  @Test
  public void testDeleteproducts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product createdProduct = builder.admin().products().create(name, createdPackageSize);
      Product foundProduct = builder.admin().products().findProduct(createdProduct.getId());
      assertEquals(createdProduct.getId(), foundProduct.getId());
      builder.admin().products().delete(createdProduct);
      builder.admin().products().assertFindFailStatus(404, createdProduct.getId());     
    }
  }

  @Test
  public void testDeleteProductPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"));
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      
      Product packageSize = builder.admin().products().create(name, createdPackageSize);
      builder.worker1().products().assertDeleteFailStatus(403, packageSize);
      builder.anonymous().products().assertDeleteFailStatus(401, packageSize);
      builder.invalid().products().assertDeleteFailStatus(401, packageSize);
    }
  }
  
}