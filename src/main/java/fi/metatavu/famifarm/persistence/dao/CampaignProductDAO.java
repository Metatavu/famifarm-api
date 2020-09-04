package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * A DAO class for campaign products
 */
@ApplicationScoped
public class CampaignProductDAO extends AbstractDAO<CampaignProduct> {
  /**
   * Adds a campaign product to the database
   *
   * @param id an unique UUID for separation
   * @param count amount of products
   * @param product product
   * @param campaign campaign
   * @param creatorId user id of the creator of this campaign
   *
   * @return created campaign product
   */
  public CampaignProduct create (UUID id, int count, Product product, Campaign campaign, UUID creatorId) {
    CampaignProduct campaignProduct = new CampaignProduct();
    campaignProduct.setId(id);
    campaignProduct.setCount(count);
    campaignProduct.setProduct(product);
    campaignProduct.setCampaign(campaign);
    campaignProduct.setCreatorId(creatorId);
    campaignProduct.setLastModifierId(creatorId);

    return persist(campaignProduct);
  }

  /**
   * Lists campaign products belonging to a specified campaign
   *
   * @param campaign campaign to filter by
   *
   * @return campaign products belonging to a specified campaign
   */
  public List<CampaignProduct> listByCampaign(Campaign campaign) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CampaignProduct> criteria = criteriaBuilder.createQuery(CampaignProduct.class);
    Root<CampaignProduct> root = criteria.from(CampaignProduct.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(CampaignProduct_.campaign), campaign));

    TypedQuery<CampaignProduct> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }

  /**
   * Lists campaign products belonging to a specified product
   *
   * @param product product to filter by
   *
   * @return campaign products belonging to a specified product
   */
  public List<CampaignProduct> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CampaignProduct> criteria = criteriaBuilder.createQuery(CampaignProduct.class);
    Root<CampaignProduct> root = criteria.from(CampaignProduct.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(CampaignProduct_.product), product));

    TypedQuery<CampaignProduct> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }
}
