package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.ProductsApi;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for products
 * 
 * @author Ville Koivukangas
 */
public class ProductTestBuilderResource extends AbstractTestBuilderResource<Product, ProductsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public ProductTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSize package size
   * @param isSubcontractorProduct is subcontractor product
   * @return created product
   */
  public Product create(List<LocalizedValue> name, PackageSize packageSize, boolean isSubcontractorProduct) {
    return create(name, packageSize, isSubcontractorProduct, true);
  }

  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSize package size
   * @param isSubcontractorProduct is subcontractor product
   * @return created product
   */
  public Product create(List<LocalizedValue> name, PackageSize packageSize, boolean isSubcontractorProduct,  boolean isActive) {
    Product product = new Product();
    product.setName(name);
    product.setDefaultPackageSizeId(packageSize.getId());
    product.setIsSubcontractorProduct(isSubcontractorProduct);
    product.setActive(isActive);
    return addClosable(getApi().createProduct(product));
  }


  /**
   * Finds a product
   * 
   * @param productId product id
   * @return found product
   */
  public Product findProduct(UUID productId) {
    return getApi().findProduct(productId);
  }

  /**
   * Updates a product into the API
   * 
   * @param body body payload
   */
  public Product updateProduct(Product body) {
    return getApi().updateProduct(body, body.getId());
  }
  
  /**
   * Deletes a product from the API
   * 
   * @param product product to be deleted
   */
  public void delete(Product product) {
    getApi().deleteProduct(product.getId());  
    removeClosable(closable -> !closable.getId().equals(product.getId()));
  }
  
  /**
   * Asserts product count within the system (with subcontractor products excluded)
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listProducts(Collections.emptyMap()).size());
  }

  /**
   * Asserts product count within the system (with subcontractor products included)
   *
   * @param expected expected count
   */
  public void assertCountWithSubcontractors(int expected) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeSubcontractorProducts", true);
    assertEquals(expected, getApi().listProducts(queryParameters).size());
  }

  /**
   * Asserts product count within the system (with inactive products included)
   *
   * @param expected expected count
   */
  public void assertCountWithInactive(int expected) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeInActiveProducts", true);
    assertEquals(expected, getApi().listProducts(queryParameters).size());
  }

  /**
   * Asserts product count within the system (with inactive and sub contractor products included)
   *
   * @param expected expected count
   */
  public void assertCountWithInactiveAndSubcontractors(int expected) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeInActiveProducts", true);
    queryParameters.put("includeSubcontractorProducts", true);
    assertEquals(expected, getApi().listProducts(queryParameters).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID productId) {
    try {
      getApi().findProduct(productId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name, PackageSize packageSize) {
    try {
      Product product = new Product();
      product.setName(name);
      product.setDefaultPackageSizeId(packageSize.getId());
      product.setActive(true);
      getApi().createProduct(product);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, Product product) {
    try {
      getApi().updateProduct(product, product.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertDeleteFailStatus(int expectedStatus, Product product) {
    try {
      getApi().deleteProduct(product.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listProducts(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual products equals expected seed when both are serialized into JSON
   * 
   * @param expected expected product
   * @param actual actual product
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertProductsEqual(Product expected, Product actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Product product) {
    getApi().deleteProduct(product.getId());  
  }

}
