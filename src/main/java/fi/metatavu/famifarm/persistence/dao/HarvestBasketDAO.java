package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for harvest baskets
 */
@ApplicationScoped
public class HarvestBasketDAO extends AbstractDAO<HarvestBasket> {

    /**
     * Creates new harvest basket
     *
     * @param id id
     * @param weight weight
     * @param harvestEvent harvest event
     * @return created harvest basket
     */
    public HarvestBasket create(UUID id, Float weight, HarvestEvent harvestEvent) {
        HarvestBasket harvestBasket = new HarvestBasket();
        harvestBasket.setId(id);
        harvestBasket.setWeight(weight);
        harvestBasket.setHarvestEvent(harvestEvent);
        return persist(harvestBasket);
    }

    /**
     * Lists harvest baskets by harvest event
     *
     * @param harvestEvent harvest event
     * @return list of harvest baskets
     */
    public List<HarvestBasket> listByHarvestEvent(HarvestEvent harvestEvent) {
        EntityManager entityManager = getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<HarvestBasket> criteria = criteriaBuilder.createQuery(HarvestBasket.class);
        Root<HarvestBasket> root = criteria.from(HarvestBasket.class);
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get(HarvestBasket_.harvestEvent), harvestEvent));
        return entityManager.createQuery(criteria).getResultList();
    }
}
