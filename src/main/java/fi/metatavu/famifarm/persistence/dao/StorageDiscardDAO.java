package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for Storage discard events
 */
@ApplicationScoped
public class StorageDiscardDAO extends AbstractDAO<StorageDiscard> {

    /**
     * Creates storage discard event
     *
     * @param id id
     * @param product product
     * @param packageSize package size
     * @param discardAmount amount
     * @param discardDate discard date
     * @return new storage discard entity
     */
    public StorageDiscard create(UUID id, Product product, PackageSize packageSize, Integer discardAmount, OffsetDateTime discardDate, UUID creatorId, UUID lastModifierId) {
        StorageDiscard storageDiscard = new StorageDiscard();
        storageDiscard.setId(id);
        storageDiscard.setProduct(product);
        storageDiscard.setPackageSize(packageSize);
        storageDiscard.setDiscardAmount(discardAmount);
        storageDiscard.setDiscardDate(discardDate);
        storageDiscard.setLastModifierId(lastModifierId);
        storageDiscard.setCreatorId(creatorId);
        return persist(storageDiscard);
    }

    /**
     * Lists storage discard events based on filters
     *
     * @param firstResult first result
     * @param maxResults max results
     * @param fromTime created after
     * @param toTime created before
     * @param product product
     * @return list of storage discard events
     */
    public List<StorageDiscard> list(Integer firstResult, Integer maxResults, OffsetDateTime fromTime, OffsetDateTime toTime, Product product) {
        EntityManager entityManager = getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StorageDiscard> criteria = criteriaBuilder.createQuery(StorageDiscard.class);
        Root<StorageDiscard> root = criteria.from(StorageDiscard.class);
        criteria.select(root);

        List<Predicate> restrictions = new ArrayList<>();

        if (product != null) {
            restrictions.add(criteriaBuilder.equal(root.get(StorageDiscard_.product), product));
        }

        if (fromTime != null) {
            restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(StorageDiscard_.discardDate), fromTime));
        }

        if (toTime != null) {
            restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(StorageDiscard_.discardDate), toTime));
        }

        criteria.where(criteriaBuilder.and(restrictions.toArray(new Predicate[0])));
        TypedQuery<StorageDiscard> query = entityManager.createQuery(criteria);

        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    /**
     * Updates discard amount field
     *
     * @param entity original storage discard entity
     * @param discardAmount new discard amount
     * @param modifierId modifier id
     * @return updated entity
     */
    public StorageDiscard updateDiscardAmount(StorageDiscard entity, Integer discardAmount, UUID modifierId) {
        entity.setDiscardAmount(discardAmount);
        entity.setLastModifierId(modifierId);
        return persist(entity);
    }

    /**
     * Updates discard date field
     *
     * @param entity original entity
     * @param discardDate new discard date
     * @param userId modifier id
     * @return updated entity
     */
    public StorageDiscard updateDiscardDate(StorageDiscard entity, OffsetDateTime discardDate, UUID userId) {
        entity.setLastModifierId(userId);
        entity.setDiscardDate(discardDate);
        return persist(entity);
    }

    /**
     * Updates product field
     *
     * @param entity original entity
     * @param product new product field
     * @param userId modifier id
     * @return updated entity
     */
    public StorageDiscard updateProduct(StorageDiscard entity, Product product, UUID userId) {
        entity.setLastModifierId(userId);
        entity.setProduct(product);
        return persist(entity);
    }

    /**
     * Updates package size
     *
     * @param entity original entity
     * @param packageSize new package size field
     * @param userId modifier id
     * @return updated entity
     */
    public StorageDiscard updatePackageSize(StorageDiscard entity, PackageSize packageSize, UUID userId) {
        entity.setLastModifierId(userId);
        entity.setPackageSize(packageSize);
        return persist(entity);
    }
}
