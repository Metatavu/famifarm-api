package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class PackingTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreatePacking() throws Exception{
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);

      Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size);

      assertNotNull(packing);
      builder.admin().packings().assertPackingsEqual(packing, builder.admin().packings().find(packing.getId()));
    }
  }

  @Test
  public void testListPackings() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      ArrayList<PackageSize> packageSizes = Lists.newArrayList(size);
      Product product = builder.admin().products().create(testEntry, packageSizes, false);
      Product product2 = builder.admin().products().create(testEntry, packageSizes, false);
      
      builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.REMOVED, size);
      
      builder.admin().packings().create(product2.getId(),null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      builder.admin().packings().create(product2.getId(), null, PackingType.BASIC,OffsetDateTime.now(), 0, PackingState.REMOVED, size);
      List<Packing> packings = builder.admin().packings().list(null, null, null, null, null, null);
      assertNotNull(packings);
      assertEquals(4, packings.size());
      packings.forEach(packing -> assertNotNull(packing));
      
      packings = builder.admin().packings().list(null, 3, null, null, null, null);
      assertEquals(3, packings.size());
      
      packings = builder.admin().packings().list(null, null, product.getId(), null, null, null);
      assertEquals(2, packings.size());
      packings.forEach(packing -> assertEquals(product.getId(), packing.getProductId()));
      
      packings = builder.admin().packings().list(null, null, null, PackingState.IN_STORE, null, null);
      assertEquals(2, packings.size());
      packings.forEach(packing -> assertEquals(PackingState.IN_STORE, packing.getState()));
    }
  }
  
  @Test
  public void testUpdatePacking() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);
      Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      
      packing.setState(PackingState.REMOVED);
      assertNotNull(builder.admin().packings().update(packing));
      Packing foundPacking = builder.admin().packings().find(packing.getId());
      builder.admin().packings().assertPackingsEqual(packing, foundPacking);
    }
  }
  
  @Test
  public void testDeletePacking() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);
      Packing packing = builder.admin().packings().create(product.getId(),null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      
      builder.admin().packings().delete(packing);
      builder.admin().packings().assertFindFailStatus(404, packing.getId());
    }
  }

  @Test
  public void testCreateAndUpdateCampaignPacking() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("apples");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProduct campaignProduct = new CampaignProduct();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      Campaign campaign = builder.admin().campaigns().create(campaignToCreate);

      Packing packing = builder.admin().packings().create(null, campaign.getId(), PackingType.CAMPAIGN, OffsetDateTime.now(), null, PackingState.IN_STORE, null);
      assertNotNull(packing);
      assertEquals(PackingType.CAMPAIGN, packing.getType());
      packing.setState(PackingState.REMOVED);
      Packing updatedPacking = builder.admin().packings().update(packing);
      assertNotNull(updatedPacking);
      assertEquals(PackingState.REMOVED, updatedPacking.getState());
    }
  }
  
}
