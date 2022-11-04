package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CutPackingDAO extends AbstractDAO<CutPacking> {

    /**
     * Creates a cut packing
     *
     * @param id an UUID for separation
     * @param product a product to which this cut packing belongs
     * @param productionLine a production line to which this cut packing belongs
     * @param weight weight in kilograms
     * @param sowingDay a day on which these plants were sowed
     * @param cuttingDay a day on which these plants were cut
     * @param producer producer name
     * @param contactInformation contact information
     * @param gutterCount gutter count
     * @param gutterHoleCount gutter hole count
     * @param storageCondition storage condition
     * @param creatorId the id of the user who is creating this cut packing
     *
     * @return a created cut packing
     */
    public CutPacking create (
            UUID id,
            Product product,
            ProductionLine productionLine,
            double weight,
            OffsetDateTime sowingDay,
            OffsetDateTime cuttingDay,
            String producer,
            String contactInformation,
            int gutterCount,
            int gutterHoleCount,
            String storageCondition,
            UUID creatorId) {

        CutPacking cutPacking = new CutPacking();
        cutPacking.setId(id);
        cutPacking.setProduct(product);
        cutPacking.setProducer(producer);
        cutPacking.setProductionLine(productionLine);
        cutPacking.setWeight(weight);
        cutPacking.setSowingDay(sowingDay);
        cutPacking.setCuttingDay(cuttingDay);
        cutPacking.setContactInformation(contactInformation);
        cutPacking.setGutterCount(gutterCount);
        cutPacking.setGutterHoleCount(gutterHoleCount);
        cutPacking.setCreatorId(creatorId);
        cutPacking.setLastModifierId(creatorId);
        cutPacking.setStorageCondition(storageCondition);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing product
     *
     * @param cutPacking a cut packing to update
     * @param product a new product
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateProduct(CutPacking cutPacking, Product product, UUID modifierId) {
        cutPacking.setProduct(product);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing production line
     *
     * @param cutPacking a cut packing to update
     * @param productionLine a new production line
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateProductionLine(CutPacking cutPacking, ProductionLine productionLine, UUID modifierId) {
        cutPacking.setProductionLine(productionLine);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing weight
     *
     * @param cutPacking a cut packing to update
     * @param weight a new weight
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateWeight(CutPacking cutPacking, double weight, UUID modifierId) {
        cutPacking.setWeight(weight);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing sowing day
     *
     * @param cutPacking a cut packing to update
     * @param sowingDay a new sowing day
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateSowingDay(CutPacking cutPacking, OffsetDateTime sowingDay, UUID modifierId) {
        cutPacking.setSowingDay(sowingDay);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing cutting day
     *
     * @param cutPacking a cut packing to update
     * @param cuttingDay a new cutting day
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateCuttingDay(CutPacking cutPacking, OffsetDateTime cuttingDay, UUID modifierId) {
        cutPacking.setCuttingDay(cuttingDay);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing producer
     *
     * @param cutPacking a cut packing to update
     * @param producer a new producer
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateProducer(CutPacking cutPacking, String producer, UUID modifierId) {
        cutPacking.setProducer(producer);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing contact information
     *
     * @param cutPacking a cut packing to update
     * @param contactInformation a new contact information
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateContactInformation(CutPacking cutPacking, String contactInformation, UUID modifierId) {
        cutPacking.setContactInformation(contactInformation);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing gutter count
     *
     * @param cutPacking a cut packing to update
     * @param gutterCount a new gutter count
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateGutterCount(CutPacking cutPacking, int gutterCount, UUID modifierId) {
        cutPacking.setGutterCount(gutterCount);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing gutter hole count
     *
     * @param cutPacking a cut packing to update
     * @param gutterHoleCount a new gutter hole count
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateGutterHoleCount(CutPacking cutPacking, int gutterHoleCount, UUID modifierId) {
        cutPacking.setGutterHoleCount(gutterHoleCount);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Updates a cut packing storage condition
     *
     * @param cutPacking a cut packing to update
     * @param storageCondition a new storage condition
     * @param modifierId the id of the user who is modifying this cut packing
     *
     * @return updated cut packing
     */
    public CutPacking updateStorageCondition(CutPacking cutPacking, String storageCondition, UUID modifierId) {
        cutPacking.setStorageCondition(storageCondition);
        cutPacking.setLastModifierId(modifierId);

        return persist(cutPacking);
    }

    /**
     * Lists cut packings
     *
     * @param facility       facility of the products
     * @param firstResult    the index of the first result
     * @param maxResults     limit results to this amount
     * @param product        return only packings belonging to this product
     * @param productionLine return only packings belonging to this production line
     * @param createdBefore  return only packing created after this date
     * @param createdAfter   return only packing created before this date
     * @return cut packings
     */
    public List<CutPacking> list(Facility facility, Integer firstResult, Integer maxResults, Product product, ProductionLine productionLine, OffsetDateTime createdBefore, OffsetDateTime createdAfter) {
        EntityManager entityManager = getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CutPacking> criteria = criteriaBuilder.createQuery(CutPacking.class);
        Root<CutPacking> root = criteria.from(CutPacking.class);

        criteria.select(root);

        List<Predicate> restrictions = new ArrayList<>();

        if (facility != null) {
            root.fetch(CutPacking_.product, JoinType.LEFT);
            restrictions.add(criteriaBuilder.equal(root.get(CutPacking_.product).get(Product_.facility), facility));
        }

        if (product != null) {
            restrictions.add(criteriaBuilder.equal(root.get(CutPacking_.product), product));
        }

        if (productionLine != null) {
            restrictions.add(criteriaBuilder.equal(root.get(CutPacking_.productionLine), productionLine));
        }

        if (createdBefore != null) {
            restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(CutPacking_.createdAt), createdBefore));
        }

        if (createdAfter != null) {
            restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(CutPacking_.createdAt), createdAfter));
        }

        criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
        criteria.orderBy(criteriaBuilder.desc(root.get(CutPacking_.createdAt)));

        TypedQuery<CutPacking> query = entityManager.createQuery(criteria);

        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }
}
