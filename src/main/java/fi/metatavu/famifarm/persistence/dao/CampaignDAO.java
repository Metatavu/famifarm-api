package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.Campaign;
import fi.metatavu.famifarm.persistence.model.Campaign_;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * A DAO class for campaigns
 */
@ApplicationScoped
public class CampaignDAO extends AbstractDAO<Campaign> {
  /**
   * Adds a new campaign to the database
   *
   * @param id        unique UUID for separation
   * @param name      campaign name
   * @param facility  facility
   * @param creatorId user id of the creator of this campaign
   * @return created campaign
   */
  public Campaign create(UUID id, String name, Facility facility, UUID creatorId) {
    Campaign campaign = new Campaign();
    campaign.setId(id);
    campaign.setName(name);
    campaign.setFacility(facility);
    campaign.setCreatorId(creatorId);
    campaign.setLastModifierId(creatorId);

    return persist(campaign);
  }

  /**
   * Updates the name value of a campaign to the database
   *
   * @param campaign   campaign to be updated
   * @param name       new campaign name
   * @param modifierId id of the user who is updating the campaign name
   * @return updated campaign
   */
  public Campaign updateName(Campaign campaign, String name, UUID modifierId) {
    campaign.setName(name);
    campaign.setLastModifierId(modifierId);

    return persist(campaign);
  }

  /**
   * Lists all campaigns by facility
   *
   * @param facility facility name
   * @return campaigns
   */
  public List<Campaign> listByFacility(Facility facility) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Campaign> criteria = criteriaBuilder.createQuery(Campaign.class);
    Root<Campaign> root = criteria.from(Campaign.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Campaign_.facility), facility));
    criteria.orderBy(criteriaBuilder.desc(root.get(Campaign_.createdAt)));

    TypedQuery<Campaign> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }
}
