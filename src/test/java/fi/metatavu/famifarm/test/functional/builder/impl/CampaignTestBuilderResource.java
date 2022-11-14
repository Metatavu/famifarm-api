package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.CampaignsApi;
import fi.metatavu.famifarm.client.model.Campaign;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A test builder resource for campaigns
 */
public class CampaignTestBuilderResource extends AbstractTestBuilderResource<Campaign, CampaignsApi> {

  private final HashMap<UUID, Facility> campaignFacilityMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param apiClient API-client
   */
  public CampaignTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }

  /**
   * Sends a request to the API to create a campaign
   *
   * @param campaign campaign to create
   * @return created campaign
   */
  public Campaign create(Campaign campaign, Facility facility) {
    Campaign createdCampaign = getApi().createCampaign(campaign, facility);
    campaignFacilityMap.put(createdCampaign.getId(), facility);
    return addClosable(createdCampaign);
  }

  /**
   * Sends a request to the API to update a campaign
   *
   * @param campaign updated campaign
   * @return updated campaign
   */
  public Campaign update(Campaign campaign, Facility facility) {
    return getApi().updateCampaign(campaign, facility, campaign.getId());
  }

  /**
   * Asserts update status fails with given status code
   *
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, Campaign campaign, Facility facility) {
    try {
      getApi().updateCampaign(campaign, facility, campaign.getId());
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   *
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, Facility facility, UUID campaignId) {
    try {
      getApi().findCampaign(facility, campaignId);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Sends a request to the API to find a campaign with the given id
   *
   * @param campaignId id of the campaign to find
   * @param facility
   * @return found campaign
   */
  public Campaign find(UUID campaignId, Facility facility) {
    return getApi().findCampaign(facility, campaignId);
  }

  /**
   * Sends a request to the API to list campaigns
   *
   * @param facility
   * @return all campaigns
   */
  public List<Campaign> list(Facility facility) {
    return getApi().listCampaigns(facility);
  }

  /**
   * Deletes a campaign
   *
   * @param facility
   * @param campaign to be deleted
   */
  public void delete(Facility facility, Campaign campaign) {
    getApi().deleteCampaign(facility, campaign.getId());
    campaignFacilityMap.remove(campaign.getId(), facility);
    removeClosable(closable -> !closable.getId().equals(campaign.getId()));
  }

  @Override
  public void clean(Campaign campaign) {
    if (campaignFacilityMap.containsKey(campaign.getId())) {
      getApi().deleteCampaign(campaignFacilityMap.get(campaign.getId()), campaign.getId());
    }
  }
}
