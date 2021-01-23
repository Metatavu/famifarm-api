package fi.metatavu.famifarm.test.functional.builder.impl;

import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.CampaignsApi;
import fi.metatavu.famifarm.client.model.Campaign;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

import java.util.List;
import java.util.UUID;

/**
 * A test builder resource for campaigns
 */
public class CampaignTestBuilderResource extends AbstractTestBuilderResource<Campaign, CampaignsApi> {
  /**
   * Constructor
   *
   * @param apiClient API-client
   */
  public CampaignTestBuilderResource(ApiClient apiClient) { super(apiClient); };

  /**
   * Sends a request to the API to create a campaign
   *
   * @param campaign campaign to create
   *
   * @return created campaign
   */
  public Campaign create (Campaign campaign) {
    return addClosable(getApi().createCampaign(campaign));
  }

  /**
   * Sends a request to the API to update a campaign
   *
   * @param campaign updated campaign
   *
   * @return updated campaign
   */
  public Campaign update (Campaign campaign) {
    return getApi().updateCampaign(campaign, campaign.getId());
  }

  /**
   * Sends a request to the API to find a campaign with the given id
   *
   * @param campaignId id of the campaign to find
   *
   * @return found campaign
   */
  public Campaign find (UUID campaignId) {
    return getApi().findCampaign(campaignId);
  }

  /**
   * Sends a request to the API to list all campaigns
   *
   * @return all campaigns
   */
  public List<Campaign> list() {
    return getApi().listCampaigns();
  }

  /**
   * Deletes a campaign
   *
   * @param campaign to be deleted
   */
  public void delete(Campaign campaign) {
    getApi().deleteCampaign(campaign.getId());
    removeClosable(closable -> !closable.getId().equals(campaign.getId()));
  }

  @Override
  public void clean(Campaign campaign) {
    getApi().deleteCampaign(campaign.getId());
  }
}
