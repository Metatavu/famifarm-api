package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.CutPackingsApi;
import fi.metatavu.famifarm.client.model.CutPacking;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

public class CutPackingTestBuilderResource extends AbstractTestBuilderResource<CutPacking, CutPackingsApi> {

    private final HashMap<UUID, Facility> cutPackingFacilityMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param apiClient initialized API client
     */
    public CutPackingTestBuilderResource(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Sends a request to the API to create a cut packing
     *
     * @param weight weight in kilograms
     * @param productId the id of a product to which this cut packing belongs
     * @param productionLineId the id of a production line to which this cut packing belongs
     * @param cuttingDay a day on which these plants were cut
     * @param sowingDay a day on which these plants were sowed
     * @param producer producer name
     * @param contactInformation contact information
     * @param gutterCount gutter count
     * @param gutterHoleCount gutter hole count
     * @param facility facility
     * @return created cut packing
     */
    public CutPacking create(
            double weight,
            UUID productId,
            UUID productionLineId,
            OffsetDateTime cuttingDay,
            OffsetDateTime sowingDay,
            String producer,
            String contactInformation,
            String storageCondition,
            int gutterCount,
            int gutterHoleCount,
            Facility facility
    ) {
        CutPacking cutPacking = new CutPacking();
        cutPacking.setContactInformation(contactInformation);
        cutPacking.setWeight(weight);
        cutPacking.setProductId(productId);
        cutPacking.setProductionLineId(productionLineId);
        cutPacking.setCuttingDay(cuttingDay);
        cutPacking.setSowingDay(sowingDay);
        cutPacking.setProducer(producer);
        cutPacking.setGutterCount(gutterCount);
        cutPacking.setGutterHoleCount(gutterHoleCount);
        cutPacking.setStorageCondition(storageCondition);
        CutPacking created = getApi().createCutPacking(cutPacking, facility);
        cutPackingFacilityMap.put(created.getId(), facility);
        return addClosable(created);
    }

    /**
     * Asserts that create request fails correctly
     *
     * @param cutPacking cut packing to test
     * @param expectedStatus expected HTTP status code after failure
     * @param facility facility
     */
    public void assertCreateFailStatus(CutPacking cutPacking, int expectedStatus, Facility facility) {
        try {
            getApi().createCutPacking(cutPacking, facility);
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    /**
     * Sends a request to the API to update a cut packing
     *
     * @param cutPacking a cut packing to update
     * @param facility facility
     * @return updated cut packing
     */
    public CutPacking update(CutPacking cutPacking, Facility facility) {
        return getApi().updateCutPacking(cutPacking, facility, cutPacking.getId());
    }

    /**
     * Asserts that update request fails correctly
     *
     * @param cutPacking cut packing to test
     * @param expectedStatus expected HTTP status code after failure
     * @param facility facility
     */
    public void assertUpdateFailStatus(CutPacking cutPacking, int expectedStatus, Facility facility) {
        try {
            getApi().updateCutPacking(cutPacking, facility, cutPacking.getId());
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    /**
     * Sends a request to the API to list cut packings
     *
     * @param firstResult the index of the first result
     * @param maxResults limit results to this amount
     * @param productId return only packings belonging to this product
     * @param createdBefore return only packing created after this date
     * @param createdAfter return only packing created before this date
     * @param facility facility
     * @return cut packings
     */
    public List<CutPacking> list(Integer firstResult, Integer maxResults, UUID productId, OffsetDateTime createdBefore, OffsetDateTime createdAfter, Facility facility) {
        String createdBeforeString = null;

        if (createdBefore != null) {
            createdBeforeString = createdBefore.toString();
        }

        String createdAfterString = null;

        if (createdAfter != null) {
            createdAfterString = createdAfter.toString();
        }

        return getApi().listCutPackings(facility, firstResult, maxResults, productId, createdBeforeString, createdAfterString);
    }

    /**
     * Asserts that list request fails correctly
     *
     * @param expectedStatus expected HTTP status code after failure
     * @param productId product id
     * @param facility facility
     */
    public void assertListFailStatus(int expectedStatus, UUID productId, Facility facility) {
        try {
            getApi().listCutPackings(facility, null, null, productId, null, null);
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    /**
     * Asserts that list request fails correctly
     *
     * @param expectedStatus expected HTTP status code after failure
     * @param facility facility
     */
    public void assertListFailStatus(int expectedStatus, Facility facility) {
        try {
            getApi().listCutPackings(facility, null, null, null, null, null);
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    /**
     * Sends a request to the API to find a cut packing
     *
     * @param cutPackingId the id of a cut packing to find
     * @param facility facility
     * @return found cut packing
     */
    public CutPacking find(UUID cutPackingId, Facility facility) {
        return getApi().findCutPacking(facility, cutPackingId);
    }

    /**
     * Asserts that find request fails correctly
     *
     * @param cutPackingId an id to test
     * @param facility facility
     * @param expectedStatus expected HTTP status code after failure
     */
    public void assertFindFailStatus(UUID cutPackingId, Facility facility, int expectedStatus) {
        try {
            getApi().findCutPacking(facility, cutPackingId);
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    /**
     * Sends a request to the API to delete a cut packing
     *
     * @param facility facility
     * @param cutPackingId the id of a cut packing to delete
     */
    public void delete(UUID cutPackingId, Facility facility) {
        getApi().deleteCutPacking(facility, cutPackingId);
        removeClosable(closable -> !closable.getId().equals(cutPackingId));
    }

    /**
     * Asserts that delete request fails correctly
     *
     * @param cutPackingId an id to test
     * @param expectedStatus expected HTTP status code after failure
     * @param facility facility
     */
    public void assertDeleteFailStatus(UUID cutPackingId, int expectedStatus, Facility facility) {
        try {
            getApi().deleteCutPacking(facility, cutPackingId);
            fail("Expected to fail with status" + expectedStatus);
        } catch (FeignException exception) {
            assertEquals(expectedStatus, exception.status());
        }
    }

    @Override
    public void clean(CutPacking cutPacking) {
        if (cutPackingFacilityMap.containsKey(cutPacking.getId())) {
            getApi().deleteCutPacking(cutPackingFacilityMap.get(cutPacking.getId()), cutPacking.getId());
        }
    }
}
