package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertNotNull;

import java.time.OffsetDateTime;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Packing;
import fi.metatavu.famifarm.client.model.PackingState;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

public class PackingTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreatePacking() throws Exception{
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, size);
      Packing packing = builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      
      assertNotNull(packing);
      builder.admin().packings().assertPackingsEqual(packing, builder.admin().packings().find(packing.getId()));
    }
  }
  
  @Test
  public void testListPackings() {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, size);
      Product product2 = builder.admin().products().create(testEntry, size);
      
      builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 0, PackingState.REMOVED, size);
      
      builder.admin().packings().create(product2.getId(), OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      builder.admin().packings().create(product2.getId(), OffsetDateTime.now(), 0, PackingState.REMOVED, size);
      
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
  public void testUpdatePacking() {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, size);
      Packing packing = builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      
      packing.setPackingState(PackingState.REMOVED);
      assertNotNull(builder.admin().packings().update(packing));
      Packing foundPacking = builder.admin().packings().find(packing.getId());
      builder.admin().packings().assertPackingsEqual(packing, foundPacking);
    }
  }
  
  @Test
  public void testDeletePacking() {
    try (TestBuilder builder = new TestBuilder()) {
      LocalizedEntry testEntry = new LocalizedEntry();
      LocalizedValue testValue = new LocalizedValue();
      
      testValue.setLanguage("en");
      testValue.setValue("test value");
      testEntry.add(testValue);
      
      PackageSize size = builder.admin().packageSizes().create(testEntry, 100);
      Product product = builder.admin().products().create(testEntry, size);
      Packing packing = builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 0, PackingState.IN_STORE, size);
      
      builder.admin().packings().delete(packing);
      builder.admin().packings().assertFindFailStatus(404, packing.getId());
    }
  }
  
}
