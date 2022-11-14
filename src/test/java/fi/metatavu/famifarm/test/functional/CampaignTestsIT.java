package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Campaign tests
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class CampaignTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreateAndUpdateCampaign() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProduct campaignProduct = new CampaignProduct();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      Campaign campaign = builder.admin().campaigns().create(campaignToCreate, Facility.JOROINEN);

      assertNotNull(campaign);
      assertEquals(campaignToCreate.getName(), campaign.getName());
      assertEquals(campaignToCreate.getProducts().get(0).getCount(), campaign.getProducts().get(0).getCount());
      assertEquals(campaignToCreate.getProducts().get(0).getProductId(), campaign.getProducts().get(0).getProductId());

      campaign.setName("Winter campaign");

      CampaignProduct campaignProduct2 = new CampaignProduct();
      campaignProduct2.setCount(20);
      campaignProduct2.setProductId(product.getId());
      ArrayList<CampaignProduct> campaignProducts = new ArrayList<>();
      campaignProducts.add(campaignProduct2);
      campaign.setProducts(campaignProducts);

      builder.admin().campaigns().assertUpdateFailStatus(404, campaign, Facility.JUVA);
      Campaign updatedCampaign = builder.admin().campaigns().update(campaign, Facility.JOROINEN);
      assertNotNull(updatedCampaign);
      assertEquals(campaign.getName(), updatedCampaign.getName());
      assertEquals(campaign.getProducts().get(0).getCount(), updatedCampaign.getProducts().get(0).getCount());
      assertEquals(campaign.getProducts().get(0).getProductId(), updatedCampaign.getProducts().get(0).getProductId());
    }
  }

  @Test
  public void testListCampaigns() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProduct campaignProduct = new CampaignProduct();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      builder.admin().campaigns().create(campaignToCreate, Facility.JOROINEN);
      builder.admin().campaigns().create(campaignToCreate, Facility.JOROINEN);
      builder.admin().campaigns().create(campaignToCreate, Facility.JUVA);

      assertEquals(2, builder.admin().campaigns().list(Facility.JOROINEN).size());
      assertEquals(1, builder.admin().campaigns().list(Facility.JUVA).size());
    }
  }

  @Test
  public void testFindCampaign() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("Apple");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 10);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Summer");

      CampaignProduct campaignProduct = new CampaignProduct();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      UUID campaignId = builder.admin().campaigns().create(campaignToCreate, Facility.JUVA).getId();
      assertNotNull(builder.admin().campaigns().find(campaignId, Facility.JUVA));
      builder.admin().campaigns().assertFindFailStatus(404, Facility.JOROINEN, campaignId);
    }
  }
}
