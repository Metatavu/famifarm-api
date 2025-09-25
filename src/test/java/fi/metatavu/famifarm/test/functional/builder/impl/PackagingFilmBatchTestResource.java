package fi.metatavu.famifarm.test.functional.builder.impl;

import fi.metatavu.famifarm.client.api.PackagingFilmBatchesApi;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.PackagingFilmBatch;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PackagingFilmBatchTestResource extends AbstractTestBuilderResource<PackagingFilmBatch, PackagingFilmBatchesApi> {

  private final java.util.Map<UUID, Facility> packagingFilmBatchFacilityMap = new java.util.HashMap<>();

  @Override
  public void clean(PackagingFilmBatch packagingFilmBatch) {
    deletePackagingFilmBatch(packagingFilmBatch.getId(), packagingFilmBatchFacilityMap.get(packagingFilmBatch.getId()));
  }

  public PackagingFilmBatchTestResource(fi.metatavu.famifarm.client.ApiClient apiClient) {
    super(apiClient);
  }

  public PackagingFilmBatch create(String name, boolean active, OffsetDateTime time, Facility facility) {
    PackagingFilmBatch packagingFilmBatch = new PackagingFilmBatch();
    packagingFilmBatch.setName(name);
    packagingFilmBatch.setActive(active);
    packagingFilmBatch.setTime(time);
    var created = getApi().createPackagingFilmBatch(packagingFilmBatch, facility);
    packagingFilmBatchFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  public PackagingFilmBatch findPackagingFilmBatch(UUID packagingFilmBatchId, Facility facility) {
    return getApi().findPackagingFilmBatch(facility, packagingFilmBatchId);
  }

  public void deletePackagingFilmBatch(UUID packagingFilmBatchId, Facility facility) {
    getApi().deletePackagingFilmBatch(facility, packagingFilmBatchId);
    removeClosable(closable -> !closable.getId().equals(packagingFilmBatchId));
  }

  public PackagingFilmBatch updatePackagingFilmBatch(UUID packagingFilmBatchId, String name, boolean active, OffsetDateTime time, Facility facility) {
    PackagingFilmBatch packagingFilmBatch = new PackagingFilmBatch();
    packagingFilmBatch.setName(name);
    packagingFilmBatch.setActive(active);
    packagingFilmBatch.setTime(time);
    return getApi().updatePackagingFilmBatch(packagingFilmBatch, facility, packagingFilmBatchId);
  }

  public List<PackagingFilmBatch> listPackagingFilmBatches(Facility facility, Boolean includePassive) {
    return getApi().listPackagingFilmBatches(facility, null, null, includePassive);
  }

  public void assertCreateFailStatus(Facility facility, PackagingFilmBatch packagingFilmBatch, int status) {
    try {
      getApi().createPackagingFilmBatch(packagingFilmBatch, facility);
      fail("Expected create to fail with status " + status);
    } catch (feign.FeignException e) {
      assertEquals(status, e.status());
    }
  }

  public void assertFindFailStatus(int expectedStatus, UUID id, Facility facility) {
    try {
      getApi().findPackagingFilmBatch(facility, id);
      fail("Expected find to fail with status " + expectedStatus);
    } catch (feign.FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
}
