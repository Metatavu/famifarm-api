package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class PackingTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreatePacking() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      List<LocalizedValue> testEntry = new ArrayList<>();
      LocalizedValue testValue = new LocalizedValue();

      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);

      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, Facility.JOROINEN);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JOROINEN);

      List<PackingVerificationWeighing> weighings = new ArrayList<>();
      weighings.add(new PackingVerificationWeighing().time(OffsetDateTime.now()).weight(100.0f));

      List<PackingUsedBasket> baskets = new ArrayList<>();
      baskets.add(new PackingUsedBasket().basketCount(1).productId(product.getId()));

      Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size, Facility.JOROINEN, weighings, baskets, OffsetDateTime.now(), null, "additional info");
      assertNotNull(packing);
      assertEquals(1, packing.getVerificationWeightings().size());
      assertEquals(1, packing.getBasketsUsed().size());

      builder.admin().packings().assertPackingsEqual(packing, builder.admin().packings().find(packing.getId(), Facility.JOROINEN));
      builder.admin().packings().assertFindFailStatus(404, packing.getId(), Facility.JUVA);

      PackingUsedBasket invalidBasket = new PackingUsedBasket().basketCount(1).productId(UUID.randomUUID());
      builder.admin().packings().assertCreateFailStatus(404, product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size, Facility.JOROINEN, Lists.newArrayList(invalidBasket));

      //Assert that cannot create packings with objects of other facilities
      Product juvaProduct = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JUVA);
      builder.admin().packings().assertCreateFailStatus(404, juvaProduct.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size, Facility.JOROINEN, null);
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

      PackageSize packingSizeJoroinen = builder.admin().packageSizes().create(testEntry, 100, Facility.JOROINEN);
      PackageSize packingSizeJuva = builder.admin().packageSizes().create(testEntry, 100, Facility.JUVA);
      ArrayList<PackageSize> packageSizes = Lists.newArrayList(packingSizeJoroinen);
      Product productJoroinen = builder.admin().products().create(testEntry, packageSizes, false, Facility.JOROINEN);
      Product product2Joroinen = builder.admin().products().create(testEntry, packageSizes, false, Facility.JOROINEN);
      Product productJuva = builder.admin().products().create(testEntry, packageSizes, false, Facility.JUVA);

      builder.admin().packings().create(productJoroinen.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, packingSizeJoroinen, Facility.JOROINEN);
      builder.admin().packings().create(productJoroinen.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.REMOVED, packingSizeJoroinen, Facility.JOROINEN);

      builder.admin().packings().create(product2Joroinen.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, packingSizeJoroinen, Facility.JOROINEN);
      builder.admin().packings().create(product2Joroinen.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.REMOVED, packingSizeJoroinen, Facility.JOROINEN);

      builder.admin().packings().create(productJuva.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.REMOVED, packingSizeJuva, Facility.JUVA);
      List<Packing> packings = builder.admin().packings().list(null, null, null, null, null, null, Facility.JOROINEN);
      assertNotNull(packings);
      assertEquals(4, packings.size());
      packings.forEach(packing -> assertNotNull(packing));

      packings = builder.admin().packings().list(null, 3, null, null, null, null, Facility.JOROINEN);
      assertEquals(3, packings.size());

      packings = builder.admin().packings().list(null, null, productJoroinen.getId(), null, null, null, Facility.JOROINEN);
      assertEquals(2, packings.size());
      packings.forEach(packing -> assertEquals(productJoroinen.getId(), packing.getProductId()));

      packings = builder.admin().packings().list(null, null, null, PackingState.IN_STORE, null, null, Facility.JOROINEN);
      assertEquals(2, packings.size());
      packings.forEach(packing -> assertEquals(PackingState.IN_STORE, packing.getState()));

      assertEquals(1, builder.admin().packings().list(null, null, null, null, null, null, Facility.JUVA).size());
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
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, Facility.JOROINEN);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JOROINEN);
      List<PackingVerificationWeighing> weighings = new ArrayList<>();
      weighings.add(new PackingVerificationWeighing().time(OffsetDateTime.now()).weight(100.0f));

      List<PackingUsedBasket> baskets = new ArrayList<>();
      baskets.add(new PackingUsedBasket().basketCount(1).productId(product.getId()));
      Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size, Facility.JOROINEN, weighings, baskets, OffsetDateTime.now(), null, "additional info");

      packing.setState(PackingState.REMOVED);
      packing.setVerificationWeightings(null);
      packing.setBasketsUsed(null);
      packing.setAdditionalInformation(null);
      packing.setStartTime(null);
      packing.setEndTime(null);
      assertNotNull(builder.admin().packings().update(packing, Facility.JOROINEN));
      Packing foundPacking = builder.admin().packings().find(packing.getId(), Facility.JOROINEN);
      assertTrue(foundPacking.getVerificationWeightings().isEmpty());
      assertTrue(foundPacking.getBasketsUsed().isEmpty());
      assertNull(foundPacking.getAdditionalInformation());
      assertNull(foundPacking.getStartTime());
      assertNull(foundPacking.getEndTime());
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
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, Facility.JOROINEN);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JOROINEN);
      Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 0, PackingState.IN_STORE, size, Facility.JOROINEN);

      builder.admin().packings().delete(packing, Facility.JOROINEN);
      builder.admin().packings().assertDeleteFailStatus(404, packing.getId(), Facility.JUVA);
      builder.admin().packings().assertFindFailStatus(404, packing.getId(), Facility.JOROINEN);
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

      PackageSize size = builder.admin().packageSizes().create(testEntry, 100, Facility.JOROINEN);
      Product product = builder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JOROINEN);

      Campaign campaignToCreate = new Campaign();
      campaignToCreate.setName("Autumn campaign for apples");

      CampaignProduct campaignProduct = new CampaignProduct();
      campaignProduct.setCount(100);
      campaignProduct.setProductId(product.getId());
      campaignToCreate.addProductsItem(campaignProduct);

      Campaign campaign = builder.admin().campaigns().create(campaignToCreate, Facility.JOROINEN);

      Packing packing = builder.admin().packings().create(null, campaign.getId(), PackingType.CAMPAIGN, OffsetDateTime.now(), null, PackingState.IN_STORE, null, Facility.JOROINEN);
      assertNotNull(packing);
      assertEquals(PackingType.CAMPAIGN, packing.getType());
      packing.setState(PackingState.REMOVED);
      Packing updatedPacking = builder.admin().packings().update(packing, Facility.JOROINEN);
      assertNotNull(updatedPacking);
      assertEquals(PackingState.REMOVED, updatedPacking.getState());
    }
  }
  
}
