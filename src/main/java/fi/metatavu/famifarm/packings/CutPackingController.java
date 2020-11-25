package fi.metatavu.famifarm.packings;

import fi.metatavu.famifarm.persistence.dao.CutPackingDAO;
import fi.metatavu.famifarm.persistence.dao.ProductDAO;
import fi.metatavu.famifarm.persistence.dao.ProductionLineDAO;
import fi.metatavu.famifarm.persistence.model.CutPacking;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.persistence.model.ProductionLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CutPackingController {
    @Inject
    private CutPackingDAO cutPackingDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private ProductionLineDAO productionLineDAO;

    /**
     * Creates
     *
     * @param productId the id of a product to which this cut packing belongs
     * @param productionLineId the id of a production line to which this cut packing belongs
     * @param weight weight in kilograms
     * @param sowingDay a day on which these plants were sowed
     * @param cuttingDay a day on which these plants were cut
     * @param producer producer name
     * @param contactInformation contact information
     * @param gutterCount gutter count
     * @param gutterHoleCount gutter hole count
     * @param storageCondition storage condition
     * @param creatorId the id of the user who is creating this cut packing
     */
    public CutPacking create (
            UUID productId,
            UUID productionLineId,
            double weight,
            OffsetDateTime sowingDay,
            OffsetDateTime cuttingDay,
            String producer,
            String contactInformation,
            int gutterCount,
            int gutterHoleCount,
            String storageCondition,
            UUID creatorId) throws CutPackingInvalidParametersException {

        Product product = productDAO.findById(productId);
        ProductionLine productionLine = productionLineDAO.findById(productionLineId);

        if (product == null) {
            throw new CutPackingInvalidParametersException("Product with id " + productId + " not found!");
        }

        if (productionLine == null) {
            throw new CutPackingInvalidParametersException("Production line with id " + productionLineId + " not found!");
        }

        return cutPackingDAO.create(UUID.randomUUID(), product, productionLine, weight, sowingDay, cuttingDay, producer, contactInformation, gutterCount, gutterHoleCount, storageCondition, creatorId);
    }

    /**
     * Updates a cut packing
     *
     * @param cutPacking a cut packing to update
     * @param productId the id of a new product
     * @param productionLineId the id of a new production line
     * @param weight a new weight
     * @param sowingDay a new sowing day
     * @param cuttingDay a new cutting day
     * @param producer a new producer
     * @param contactInformation a new contact information
     * @param gutterCount a new gutter count
     * @param gutterHoleCount a new gutter count
     * @param storageCondition a new storage condition
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return a cut packing to update
     */
    public CutPacking update (
            CutPacking cutPacking,
            UUID productId,
            UUID productionLineId,
            double weight,
            OffsetDateTime sowingDay,
            OffsetDateTime cuttingDay,
            String producer,
            String contactInformation,
            int gutterCount,
            int gutterHoleCount,
            String storageCondition,
            UUID modifierId) throws CutPackingInvalidParametersException {

        Product product = productDAO.findById(productId);
        ProductionLine productionLine = productionLineDAO.findById(productionLineId);

        if (product == null) {
            throw new CutPackingInvalidParametersException("Product with id " + productId + " not found!");
        }

        if (productionLine == null) {
            throw new CutPackingInvalidParametersException("Production line with id " + productionLineId + " not found!");
        }

        cutPackingDAO.updateProduct(cutPacking, product, modifierId);
        cutPackingDAO.updateProductionLine(cutPacking, productionLine, modifierId);
        cutPackingDAO.updateWeight(cutPacking, weight, modifierId);
        cutPackingDAO.updateSowingDay(cutPacking, sowingDay, modifierId);
        cutPackingDAO.updateCuttingDay(cutPacking, cuttingDay, modifierId);
        cutPackingDAO.updateProducer(cutPacking, producer, modifierId);
        cutPackingDAO.updateContactInformation(cutPacking, contactInformation, modifierId);
        cutPackingDAO.updateGutterCount(cutPacking, gutterCount, modifierId);
        cutPackingDAO.updateGutterHoleCount(cutPacking, gutterHoleCount, modifierId);
        cutPackingDAO.updateStorageCondition(cutPacking, storageCondition, modifierId);

        return cutPacking;
    }

    /**
     * Finds a cut packing
     *
     * @param cutPackingId the id of a cut packing to find
     *
     * @return found packing or null if not found
     */
    public CutPacking find(UUID cutPackingId) {
        return cutPackingDAO.findById(cutPackingId);
    }

    /**
     * Lists cut packings
     *
     * @param firstResult the index of the first result
     * @param maxResults limit results to this amount
     * @param product return only packings belonging to this product
     * @param productionLine return only packings belonging to this production line
     * @param createdBefore return only packing created after this date
     * @param createdAfter return only packing created before this date
     *
     * @return cut packings
     */
    public List<CutPacking> list(Integer firstResult, Integer maxResults, Product product, ProductionLine productionLine, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
        return cutPackingDAO.list(firstResult, maxResults, product, productionLine, createdBefore, createdAfter);
    }

    /**
     * Deletes a cut packing
     *
     * @param cutPacking cut packing to delete
     */
    public void delete(CutPacking cutPacking) {
        cutPackingDAO.delete(cutPacking);
    }
}
