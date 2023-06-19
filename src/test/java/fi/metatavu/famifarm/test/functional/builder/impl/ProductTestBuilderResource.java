package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import fi.metatavu.famifarm.client.model.*;
import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.ProductsApi;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for products
 * 
 * @author Ville Koivukangas
 */
public class ProductTestBuilderResource extends AbstractTestBuilderResource<Product, ProductsApi> {

  private final HashMap<UUID, Facility> productFacilityMap = new HashMap<>();

  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public ProductTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
   /**
   * Creates new product at default facility
   * 
   * @param name name
   * @param packageSizes package size list
   * @param isSubcontractorProduct is subcontractor product
   * @param facility facility
   * @return created product
   */
  public Product create(List<LocalizedValue> name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, Facility facility) {
    return create(name, packageSizes, null, isSubcontractorProduct, true, facility, false, false);
  }

//  /**
//   * Creates new product
//   *
//   * @param name name
//   * @param packageSizes package size list
//   * @param isSubcontractorProduct is subcontractor product
//   * @param facility facility
//   * @return created product
//   */
//  public Product create(List<LocalizedValue> name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, Facility facility) {
//    return create(name, packageSizes, null, isSubcontractorProduct, true, facility);
//  }

  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSizes package size list
   * @param allowedHarvestTypes allowed harvest type list
   * @param isSubcontractorProduct is subcontractor product
   * @return created product
   */
  public Product create(List<LocalizedValue> name, List<PackageSize> packageSizes, List<HarvestEventType> allowedHarvestTypes, boolean isSubcontractorProduct) {
    return create(name, packageSizes, allowedHarvestTypes, isSubcontractorProduct, true, Facility.JOROINEN, false, false);
  }

  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSizes package size list
   * @param allowedHarvestTypes allowed harvest type list
   * @param isSubcontractorProduct is subcontractor product
   * @param isActive is active product
   * @param facility facility
   * @param isEndProduct is end product
   * @param isRawMaterial is raw material
   * @return created product
   */
  public Product create(List<LocalizedValue> name, List<PackageSize> packageSizes, List<HarvestEventType> allowedHarvestTypes, boolean isSubcontractorProduct,  boolean isActive, Facility facility, boolean isEndProduct, boolean isRawMaterial) {
    Product product = new Product();
    product.setName(name);
    if (packageSizes != null) {
      product.setDefaultPackageSizeIds(packageSizes.stream().map(PackageSize::getId).collect(Collectors.toList()));
    }
    if (allowedHarvestTypes != null) {
      product.setAllowedHarvestTypes(allowedHarvestTypes);
    }

    product.setIsSubcontractorProduct(isSubcontractorProduct);
    product.setIsEndProduct(isEndProduct);
    product.setActive(isActive);
    product.setIsRawMaterial(isRawMaterial);
    Product created = getApi().createProduct(product, facility);
    productFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  /**
   * Finds a product at default facility
   *
   * @param productId product id
   * @return found product
   */
  public Product findProduct(UUID productId) {
    return getApi().findProduct(Facility.JOROINEN, productId);
  }

  /**
   * Finds a product
   * 
   * @param productId product id
   * @return found product
   */
  public Product findProduct(UUID productId, Facility facility) {
    return getApi().findProduct(facility, productId);
  }

  /**
   * Updates a product into the API
   * 
   * @param body body payload
   */
  public Product updateProduct(Product body, Facility facility) {
    return getApi().updateProduct(body, facility, body.getId());
  }
  
  /**
   * Deletes a product from the API
   * 
   * @param product product to be deleted
   */
  public void delete(Product product, Facility facility) {
    getApi().deleteProduct(facility, product.getId());
    removeClosable(closable -> !closable.getId().equals(product.getId()));
  }
  
  /**
   * Asserts product count within the system (with subcontractor products excluded)
   * 
   * @param expected expected count
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listProducts(facility, Collections.emptyMap()).size());
  }

  /**
   * Asserts product count within the system (with subcontractor products included)
   *
   * @param expected expected count
   */
  public void assertCountWithSubcontractors(int expected, Facility facility) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeSubcontractorProducts", true);
    assertEquals(expected, getApi().listProducts(facility, queryParameters).size());
  }

  /**
   * Asserts product count within the system (with inactive products included)
   *
   * @param expected expected count
   */
  public void assertCountWithInactive(int expected, Facility facility) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeInActiveProducts", true);
    assertEquals(expected, getApi().listProducts(facility, queryParameters).size());
  }

  /**
   * Asserts product count with isEndProduct
   *
   * @param expected expected count
   * @param facility facility
   */
  public void assertCountWithEndProduct(int expected, Facility facility) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("filterByEndProducts", true);
    assertEquals(expected, getApi().listProducts(facility, queryParameters).size());
  }

  /**
   * Asserts product count with isRawMaterial
   *
   * @param expected expected count
   * @param facility facility
   */
  public void assertCountWithRawMaterial(int expected, Facility facility) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("filterByRawMaterials", true);
    assertEquals(expected, getApi().listProducts(facility, queryParameters).size());
  }

  /**
   * Asserts product count within the system (with inactive and sub contractor products included)
   *
   * @param expected expected count
   */
  public void assertCountWithInactiveAndSubcontractors(int expected, Facility facility) {
    HashMap<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("includeInActiveProducts", true);
    queryParameters.put("includeSubcontractorProducts", true);
    assertEquals(expected, getApi().listProducts(facility, queryParameters).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID productId, Facility facility) {
    try {
      getApi().findProduct(facility, productId);
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
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name, List<PackageSize> packageSizes) {
    try {
      Product product = new Product();
      product.setName(name);
      product.setDefaultPackageSizeIds(packageSizes.stream().map(PackageSize::getId).collect(Collectors.toList()));
      product.setActive(true);
      getApi().createProduct(product, Facility.JOROINEN);
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
  public void assertUpdateFailStatus(int expectedStatus, Product product, Facility facility) {
    try {
      getApi().updateProduct(product, facility, product.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, Product product, Facility facility) {
    try {
      getApi().deleteProduct(facility, product.getId());
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
  public void assertListFailStatus(int expectedStatus, Facility facility) {
    try {
      getApi().listProducts(facility, Collections.emptyMap());
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
    getApi().deleteProduct(productFacilityMap.get(product.getId()), product.getId());
  }

}
