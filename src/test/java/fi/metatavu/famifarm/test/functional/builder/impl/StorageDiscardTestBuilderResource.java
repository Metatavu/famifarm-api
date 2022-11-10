package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.StorageDiscardsApi;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.StorageDiscard;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test builder resource for StorageDiscardsAPI
 */
public class StorageDiscardTestBuilderResource extends AbstractTestBuilderResource<StorageDiscard, StorageDiscardsApi> {

    private final HashMap<UUID, Facility> storageDiscardFacilityMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param apiClient API client
     */
    public StorageDiscardTestBuilderResource(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Cleans given resource
     *
     * @param storageDiscard resource
     */
    @Override
    public void clean(StorageDiscard storageDiscard) {
        if (storageDiscardFacilityMap.containsKey(storageDiscard.getId())) {
            getApi().deleteStorageDiscard(storageDiscardFacilityMap.get(storageDiscard.getId()), storageDiscard.getId());
            storageDiscardFacilityMap.remove(storageDiscard.getId());
        }
    }

    /**
     * Creates storage discard event
     *
     * @param discardTime time when the product was discarded
     * @param discardAmount amount of product to be discarded
     * @param productId product id
     * @param packageSizeId package size id of discarded product
     * @param facility facility
     * @return new storage discard object
     */
    public StorageDiscard create(OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId, Facility facility) {
        StorageDiscard createdStorageDiscard = getApi().createStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId), facility);
        storageDiscardFacilityMap.put(createdStorageDiscard.getId(), facility);
        return addClosable(createdStorageDiscard);
    }

    /**
     * Finds storage discard event by id
     *
     * @param storageDiscardId storage discard id
     * @param facility facility
     * @return found entity
     */
    public StorageDiscard find(UUID storageDiscardId, Facility facility) {
        return getApi().getStorageDiscard(facility, storageDiscardId);
    }

    /**
     * Updates storage discard object
     *
     * @param storageDiscardId id of entityt to update
     * @param discardTime new discard time
     * @param discardAmount new discard amount
     * @param productId new product id
     * @param packageSizeId new package size id
     * @return updated entity
     */
    public StorageDiscard update(UUID storageDiscardId, OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId) {
        return getApi().updateStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId), Facility.JOROINEN, storageDiscardId);
    }

    /**
     * Asserts that actual storage discard event equals expected one when both are serialized into JSON
     *
     * @param expected expected entity
     * @param actual actual entity
     * @throws JSONException thrown when JSON serialization error occurs
     * @throws IOException thrown when IO Exception occurs
     */
    public void assertEquals(StorageDiscard expected, StorageDiscard actual) throws IOException, JSONException {
        assertJsonsEqual(expected, actual);
    }
    /**
     * Builds storage discard object
     *
     * @param discardTime discard time
     * @param discardAmount discard amount
     * @param productId product id
     * @param packageSizeId package size id
     * @return StorageDiscard
     */
    public StorageDiscard createObject(OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId) {
        StorageDiscard storageDiscard = new StorageDiscard();
        storageDiscard.setDiscardDate(discardTime);
        storageDiscard.setDiscardAmount(discardAmount);
        storageDiscard.setProductId(productId);
        storageDiscard.setPackageSizeId(packageSizeId);
        return storageDiscard;
    }

    /**
     * Asserts that creating new package discard object fails with given status
     *
     * @param expectedStatus expected status
     * @param discardAmount amount of product to be discarded
     * @param productId product id
     * @param packageSizeId package size id of discarded product
     * @param facility facility
     */
    public void assertCreateFail(int expectedStatus, OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId, Facility facility) {
        try {
            getApi().createStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId), facility);
            Assertions.fail(String.format("Expected create to fail with status %d", expectedStatus));
        } catch (FeignException e) {
            Assertions.assertEquals(expectedStatus, e.status());
        }
    }
    /**
     * Asserts that creating new package discard object fails with given status
     *
     * @param expectedStatus expected status
     * @param productId product id
     * @param facility facility
     */
    public void assertCreateFail(int expectedStatus, UUID productId, Facility facility) {
        try {
            getApi().createStorageDiscard(createObject(OffsetDateTime.now(), 1, productId, UUID.randomUUID()), facility);
            Assertions.fail(String.format("Expected create to fail with status %d", expectedStatus));
        } catch (FeignException e) {
            Assertions.assertEquals(expectedStatus, e.status());
        }
    }

    /**
     * Asserts that updating package discard object fails with given status
     *
     * @param expectedStatus expected status
     * @param payload payload
     * @param facility facility
     * @param storageDiscardId storage discard id
     */
    public void assertUpdateFail(int expectedStatus, StorageDiscard payload, Facility facility, UUID storageDiscardId) {
        try {
            getApi().updateStorageDiscard(payload, facility, storageDiscardId);
            Assertions.fail(String.format("Expected update to fail with status %d", expectedStatus));
        } catch (FeignException e) {
            Assertions.assertEquals(expectedStatus, e.status());
        }
    }

    /**
     * Asserts that finding package discard object fails with given status
     *
     * @param expectedStatus expected status
     * @param facility facility
     * @param storageDiscardId storage discard id
     */
    public void assertFindFail(int expectedStatus, Facility facility, UUID storageDiscardId) {
        try {
            getApi().getStorageDiscard(facility, storageDiscardId);
            Assertions.fail(String.format("Expected find to fail with status %d", expectedStatus));
        } catch (FeignException e) {
            Assertions.assertEquals(expectedStatus, e.status());
        }
    }

    /**
     * Asserts listing fails with given status
     *
     * @param facility facility
     * @param productId product id
     * @param expectedStatus expected status code
     */
    public void assertListFail(Facility facility, UUID productId, int expectedStatus) {
        try {
            getApi().listStorageDiscards(facility, null, null, null, null, productId);
            Assertions.fail(String.format("Expected list to fail with status %d", expectedStatus));
        } catch (FeignException e) {
            Assertions.assertEquals(expectedStatus, e.status());
        }
    }

    /**
     * Asserts number of filtered storage discounts
     *
     * @param expected expected number
     * @param fromTime from time
     * @param toTime to time
     * @param productId product id
     * @param facility facility
     */
    public void assertCount(int expected, String fromTime, String toTime, UUID productId, Facility facility) {
        Assertions.assertEquals(expected, getApi().listStorageDiscards(facility, null, null, fromTime, toTime, productId).size());
    }

    /**
     * Lists all storage discards based on parameters
     *
     * @param facility facility
     * @param first first result
     * @param max max results
     * @param fromTime created after
     * @param toTime created before
     * @param productId product id
     * @return list of all fitting storeage discard events
     */
    public List<StorageDiscard> list(Facility facility, int first, int max, String fromTime, String toTime, UUID productId) {
        return getApi().listStorageDiscards(facility, first, max, fromTime, toTime, productId);
    }
}
