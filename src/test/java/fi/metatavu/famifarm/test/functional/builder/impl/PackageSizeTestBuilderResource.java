package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.*;

import fi.metatavu.famifarm.client.model.Facility;
import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.PackageSizesApi;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

public class PackageSizeTestBuilderResource  extends AbstractTestBuilderResource<PackageSize, PackageSizesApi> {

  private final HashMap<UUID, Facility> packageFacilityMap = new HashMap<>();

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
   * @param facility facility
   * @return created package size
   */
  public PackageSize create(List<LocalizedValue> name, Integer size, Facility facility) {
    PackageSize packageSize = new PackageSize();
    packageSize.setName(name);
    packageSize.setSize(size);
    PackageSize created = getApi().createPackageSize(packageSize, facility);
    packageFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  /**
   * Finds a PackageSize
   * 
   * @param packageSizeId PackageSize id
   * @return found PackageSize
   */
  public PackageSize findPackageSize(UUID packageSizeId, Facility facility) {
    return getApi().findPackageSize(facility, packageSizeId);
  }

  /**
   * Updates a PackageSize into the API
   * 
   * @param body body payload
   */
  public PackageSize updatePackageSize(PackageSize body, Facility facility) {
    return getApi().updatePackageSize(body, facility, body.getId());
  }
  
  /**
   * Deletes a PackageSize from the API
   * 
   * @param packageSize PackageSize to be deleted
   */
  public void delete(PackageSize packageSize, Facility facility) {
    getApi().deletePackageSize(facility, packageSize.getId());
    removeClosable(closable -> !closable.getId().equals(packageSize.getId()));
  }
  
  /**
   * Asserts PackageSize count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listPackageSizes(facility, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, List<LocalizedValue> name, Facility facility) {
    try {
      PackageSize packageSize = new PackageSize();
      packageSize.setName(name);
      getApi().createPackageSize(packageSize, facility);
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
  public void assertFindFailStatus(int expectedStatus, UUID packageSizeId, Facility facility) {
    try {
      getApi().findPackageSize(facility, packageSizeId);
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
  public void assertUpdateFailStatus(int expectedStatus, PackageSize packageSize, Facility facility) {
    try {
      getApi().updatePackageSize(packageSize, facility, packageSize.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, PackageSize packageSize, Facility facility) {
    try {
      getApi().deletePackageSize(facility, packageSize.getId());
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
      getApi().listPackageSizes(facility, Collections.emptyMap());
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
    getApi().deletePackageSize(packageFacilityMap.get(packageSize.getId()), packageSize.getId());
  }
}
