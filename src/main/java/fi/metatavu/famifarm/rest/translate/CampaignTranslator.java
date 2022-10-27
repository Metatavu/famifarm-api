package fi.metatavu.famifarm.rest.translate;

import fi.metatavu.famifarm.persistence.dao.CampaignProductDAO;

import fi.metatavu.famifarm.rest.model.Campaign;
import fi.metatavu.famifarm.rest.model.CampaignProducts;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A REST translator class for campaigns
 */
@ApplicationScoped
public class CampaignTranslator extends AbstractTranslator {
  @Inject
  private CampaignProductDAO campaignProductDAO;

  /**
   * Translates JPA campaigns into REST campaigns
   *
   * @param jpaCampaign JPA campaign
   *
   * @return REST campaign
   */
  public Campaign translate (fi.metatavu.famifarm.persistence.model.Campaign jpaCampaign) {
    Campaign campaign = new Campaign();
    campaign.setId(jpaCampaign.getId());
    campaign.setName(jpaCampaign.getName());

    List<CampaignProducts> campaignProducts = campaignProductDAO.listByCampaign(jpaCampaign).stream().map(this::translateCampaignProduct).collect(Collectors.toList());
    campaign.setProducts(campaignProducts);

    return campaign;
  }

  /**
   * Translates JPA campaign products into rest campaign products
   *
   * @param jpaCampaignProduct JPA campaign product
   *
   * @return REST campaign products
   */
  private CampaignProducts translateCampaignProduct (fi.metatavu.famifarm.persistence.model.CampaignProduct jpaCampaignProduct) {
    CampaignProducts campaignProduct = new CampaignProducts();
    campaignProduct.setProductId(jpaCampaignProduct.getProduct().getId());
    campaignProduct.setCount(jpaCampaignProduct.getCount());

    return campaignProduct;
  }
}
