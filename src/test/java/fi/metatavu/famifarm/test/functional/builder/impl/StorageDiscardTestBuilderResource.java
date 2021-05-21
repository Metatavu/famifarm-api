package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.StorageDiscardsApi;
import fi.metatavu.famifarm.client.model.StorageDiscard;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test builder resource for StorageDiscardsAPI
 */
public class StorageDiscardTestBuilderResource extends AbstractTestBuilderResource<StorageDiscard, StorageDiscardsApi> {

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
        getApi().deleteStorageDiscard(storageDiscard.getId());
    }

    /**
     * Creates storage discard event
     *
     * @param discardTime time when the product was discarded
     * @param discardAmount amount of product to be discarded
     * @param productId product id
     * @param packageSizeId package size id of discarded product
     * @return new storage discard object
     */
    public StorageDiscard create(OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId) {
        return addClosable(getApi().createStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId)));
    }

    /**
     * Finds storage discard event by id
     *
     * @param storageDiscardId storage discard id
     * @return found entity
     */
    public StorageDiscard find(UUID storageDiscardId) {
        return getApi().getStorageDiscard(storageDiscardId);
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
        return getApi().updateStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId), storageDiscardId);
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
     * @return
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
     */
    public void assertCreateFail(int expectedStatus, OffsetDateTime discardTime, Integer discardAmount, UUID productId, UUID packageSizeId) {
        try {
            getApi().createStorageDiscard(createObject(discardTime, discardAmount, productId, packageSizeId));
            Assertions.fail(String.format("Expected create to fail with status %d", expectedStatus));
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
     */
    public void assertCount(int expected, String fromTime, String toTime, UUID productId) {
        Assertions.assertEquals(expected, getApi().listStorageDiscards(null, null, fromTime, toTime, productId).size());
    }

    /**
     * Lists all storage discards based on parameters
     *
     * @param first first result
     * @param max max results
     * @param fromTime created after
     * @param toTime created before
     * @param productId product id
     * @return list of all fitting storeage discard events
     */
    public List<StorageDiscard> list(int first, int max, String fromTime, String toTime, UUID productId) {
        return getApi().listStorageDiscards(first, max, fromTime, toTime, productId);
    }
}
