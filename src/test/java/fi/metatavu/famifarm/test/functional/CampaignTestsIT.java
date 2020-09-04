package fi.metatavu.famifarm.test.functional;

import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Campaign tests
 */
public class CampaignTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreateAndUpdateCampaign() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, size);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProducts campaignProduct = new CampaignProducts();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      Campaign campaign = builder.admin().campaigns().create(campaignToCreate);

      assertNotNull(campaign);
      assertEquals(campaignToCreate.getName(), campaign.getName());
      assertEquals(campaignToCreate.getProducts().get(0).getCount(), campaign.getProducts().get(0).getCount());
      assertEquals(campaignToCreate.getProducts().get(0).getProductId(), campaign.getProducts().get(0).getProductId());

      campaign.setName("Winter campaign");

      CampaignProducts campaignProduct2 = new CampaignProducts();
      campaignProduct2.setCount(20);
      campaignProduct2.setProductId(product.getId());
      ArrayList<CampaignProducts> campaignProducts = new ArrayList<>();
      campaignProducts.add(campaignProduct2);
      campaign.setProducts(campaignProducts);

      Campaign updatedCampaign = builder.admin().campaigns().update(campaign);
      assertNotNull(updatedCampaign);
      assertEquals(campaign.getName(), updatedCampaign.getName());
      assertEquals(campaign.getProducts().get(0).getCount(), updatedCampaign.getProducts().get(0).getCount());
      assertEquals(campaign.getProducts().get(0).getProductId(), updatedCampaign.getProducts().get(0).getProductId());
    }
  }

  @Test
  public void testListCampaigns() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, size);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProducts campaignProduct = new CampaignProducts();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      builder.admin().campaigns().create(campaignToCreate);
      builder.admin().campaigns().create(campaignToCreate);
      builder.admin().campaigns().create(campaignToCreate);

      List<Campaign> campaigns = builder.admin().campaigns().list();
      assertNotNull(campaigns);
      assertEquals(3, campaigns.size());
    }
  }

  @Test
  public void testFindCampaign() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, size);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Summer");

      CampaignProducts campaignProduct = new CampaignProducts();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      UUID campaignId = builder.admin().campaigns().create(campaignToCreate).getId();
      Campaign foundCampaign = builder.admin().campaigns().find(campaignId);
      assertNotNull(foundCampaign);
    }
  }
}
