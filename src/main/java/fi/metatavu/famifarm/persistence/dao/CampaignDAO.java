package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.Campaign;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

/**
 * A DAO class for campaigns
 */
@ApplicationScoped
public class CampaignDAO extends AbstractDAO<Campaign> {
  /**
   * Adds a new campaign to the database
   *
   * @param id unique UUID for separation
   * @param name campaign name
   * @param creatorId user id of the creator of this campaign
   *
   * @return created campaign
   */
  public Campaign create (UUID id, String name, UUID creatorId) {
    Campaign campaign = new Campaign();
    campaign.setId(id);
    campaign.setName(name);
    campaign.setCreatorId(creatorId);
    campaign.setLastModifierId(creatorId);

    return persist(campaign);
  }

  /**
   * Updates the name value of a campaign to the database
   *
   * @param campaign campaign to be updated
   * @param name new campaign name
   * @param modifierId id of the user who is updating the campaign name
   *
   * @return updated campaign
   */
  public Campaign updateName (Campaign campaign, String name, UUID modifierId) {
    campaign.setName(name);
    campaign.setLastModifierId(modifierId);

    return persist(campaign);
  }
}
