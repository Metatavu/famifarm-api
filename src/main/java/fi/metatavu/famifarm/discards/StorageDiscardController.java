package fi.metatavu.famifarm.discards;

import fi.metatavu.famifarm.persistence.dao.StorageDiscardDAO;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.StorageDiscard;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for Storage discard events
 */
@ApplicationScoped
public class StorageDiscardController {

    @Inject
    private StorageDiscardDAO storageDiscardDAO;

    /**
     * Creates discard event
     *
     * @param product product to discard
     * @param packageSize package size
     * @param discardAmount discard amount
     * @param discardDate discard date
     * @param creatorId creator id
     * @return new storage discard entity
     */
    public StorageDiscard create(
        Product product,
        PackageSize packageSize,
        Integer discardAmount,
        OffsetDateTime discardDate,
        UUID creatorId
    ) {
        return storageDiscardDAO.create(UUID.randomUUID(), product, packageSize, discardAmount, discardDate, creatorId, creatorId);
    }

    /**
     * Finds storage discard entity by id
     *
     * @param storageDiscardId id
     * @return found storage discard
     */
    public StorageDiscard findById(UUID storageDiscardId) {
        return storageDiscardDAO.findById(storageDiscardId);
    }

    /**
     * Deletes storage discard entity
     *
     * @param storageDiscard storage discard to remove
     */
    public void deleteStorageDiscard(StorageDiscard storageDiscard) {
        storageDiscardDAO.delete(storageDiscard);
    }

    /**
     * Lists storage discards according to filters
     *
     * @param firstResult first index
     * @param maxResults max results
     * @param fromTime from time
     * @param toTime to time
     * @param productId product id
     * @param facility facility
     * @return list of all fitting storage discard entities
     */
    public List<StorageDiscard> listStorageDiscards(
        Integer firstResult,
        Integer maxResults,
        OffsetDateTime fromTime,
        OffsetDateTime toTime,
        Product productId,
        Facility facility
    ) {
        return storageDiscardDAO.list(firstResult, maxResults, fromTime, toTime, productId, facility);
    }

    /**
     * Updates storage discard object
     *
     * @param original original entity
     * @param discardAmount new discard amount
     * @param discardDate new discard date
     * @param product new product
     * @param packageSize new package size
     * @param userId modifier id
     * @return updated entity
     */
    public StorageDiscard updateStorageDiscard(StorageDiscard original, Integer discardAmount, OffsetDateTime discardDate, Product product, PackageSize packageSize, UUID userId) {
        StorageDiscard updated = storageDiscardDAO.updateDiscardAmount(original, discardAmount, userId);
        updated = storageDiscardDAO.updateDiscardDate(updated, discardDate, userId);
        updated = storageDiscardDAO.updateProduct(updated, product, userId);
        updated = storageDiscardDAO.updatePackageSize(updated, packageSize, userId);
        return updated;
    }
}
