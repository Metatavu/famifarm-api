package fi.metatavu.famifarm.campaigns;

import fi.metatavu.famifarm.persistence.dao.CampaignDAO;
import fi.metatavu.famifarm.persistence.dao.CampaignProductDAO;
import fi.metatavu.famifarm.persistence.model.Campaign;
import fi.metatavu.famifarm.persistence.model.CampaignProduct;
import fi.metatavu.famifarm.persistence.model.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A controller class for campaigns
 */
@ApplicationScoped
public class CampaignController {
  @Inject
  private CampaignDAO campaignDAO;

  @Inject
  CampaignProductDAO campaignProductDAO;

  /**
   * Adds a new campaign to the database
   *
   * @param name campaign name
   * @param campaignProducts campaign products
   * @param creatorId user id of the creator of this campaign
   *
   * @return created campaign
   */
  public Campaign create (String name, HashMap<Product, Integer> campaignProducts, UUID creatorId) {
    Campaign createdCampaign = campaignDAO.create(UUID.randomUUID(), name, creatorId);
    for (Map.Entry<Product, Integer> campaignProduct : campaignProducts.entrySet()) {
      campaignProductDAO.create(UUID.randomUUID(), campaignProduct.getValue(), campaignProduct.getKey(), createdCampaign, creatorId);
    }

    return createdCampaign;
  }

  /**
   * Searches for a campaign with the given id from the database
   *
   * @param campaignId id of the campaign to find
   * @return found campaign or null if not found
   */
  public Campaign find (UUID campaignId) {
    return campaignDAO.findById(campaignId);
  }

  /**
   * Lists all campaigns in the database
   *
   * @return all campaigns in the database
   */
  public List<Campaign> list () {
    return campaignDAO.listAll();
  }

  /**
   * Updates the name value and campaign products of a campaign to the database
   *
   * @param campaign campaign to be updated
   * @param name new campaign name
   * @param campaignProducts new campaign products
   * @param modifierId id of the user who is updating the campaign name
   *
   * @return updated campaign
   */
  public Campaign update (Campaign campaign, String name, HashMap<Product, Integer> campaignProducts, UUID modifierId) {
    List<CampaignProduct> existingCampaignProducts = campaignProductDAO.listByCampaign(campaign);

    for (CampaignProduct campaignProduct: existingCampaignProducts) {
      campaignProductDAO.delete(campaignProduct);
    }

    for (Map.Entry<Product, Integer> campaignProduct : campaignProducts.entrySet()) {
      campaignProductDAO.create(UUID.randomUUID(), campaignProduct.getValue(), campaignProduct.getKey(), campaign, modifierId);
    }

    return campaignDAO.updateName(campaign, name, modifierId);
  }

  /**
   * Deletes a campaign
   *
   * @param campaign campaign to be deleted
   */
  public void delete (Campaign campaign) {
    List<CampaignProduct> existingCampaignProducts = campaignProductDAO.listByCampaign(campaign);

    for (CampaignProduct campaignProduct: existingCampaignProducts) {
      campaignProductDAO.delete(campaignProduct);
    }
    campaignDAO.delete(campaign);
  }
}