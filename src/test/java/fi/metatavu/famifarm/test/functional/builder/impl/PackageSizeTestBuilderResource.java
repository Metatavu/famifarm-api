package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.PackageSizesApi;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

public class PackageSizeTestBuilderResource  extends AbstractTestBuilderResource<PackageSize, PackageSizesApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public PackageSizeTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new package size
   * 
   * @param name name
   * @return created seed
   */
  public PackageSize create(LocalizedEntry name, Integer size) {
    PackageSize packageSize = new PackageSize();
    packageSize.setName(name);
    packageSize.setSize(size);
    return addClosable(getApi().createPackageSize(packageSize));
  }
  
  /**
   * Finds a PackageSize
   * 
   * @param packageSizeId PackageSize id
   * @return found PackageSize
   */
  public PackageSize findPackageSize(UUID packageSizeId) {
    return getApi().findPackageSize(packageSizeId);
  }

  /**
   * Updates a PackageSize into the API
   * 
   * @param body body payload
   */
  public PackageSize updatePackageSize(PackageSize body) {
    return getApi().updatePackageSize(body, body.getId());
  }
  
  /**
   * Deletes a PackageSize from the API
   * 
   * @param packageSize PackageSize to be deleted
   */
  public void delete(PackageSize packageSize) {
    getApi().deletePackageSize(packageSize.getId());  
    removeClosable(closable -> !closable.getId().equals(packageSize.getId()));
  }
  
  /**
   * Asserts PackageSize count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listPackageSizes(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, LocalizedEntry name) {
    try {
      PackageSize packageSize = new PackageSize();
      packageSize.setName(name);
      getApi().createPackageSize(packageSize);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID packageSizeId) {
    try {
      getApi().findPackageSize(packageSizeId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, PackageSize packageSize) {
    try {
      getApi().updatePackageSize(packageSize, packageSize.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, PackageSize packageSize) {
    try {
      getApi().deletePackageSize(packageSize.getId());
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
      getApi().listPackageSizes(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual packageSize equals expected packageSize when both are serialized into JSON
   * 
   * @param expected expected packageSize
   * @param actual actual packageSize
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertPackageSizeEqual(PackageSize expected, PackageSize actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(PackageSize packageSize) {
    getApi().deletePackageSize(packageSize.getId());  
  }

}
