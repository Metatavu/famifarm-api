package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;
import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for cut packings
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class CutPackingTestIT extends AbstractFunctionalTest {
    @Test
    public void testCreateCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            List<LocalizedValue> testEntry = new ArrayList<>();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, Lists.newArrayList(size), false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().withYear(2019);
            OffsetDateTime cuttingDay = OffsetDateTime.now().withYear(2020);

            CutPacking cutPacking = testBuilder.admin().cutPackings().create(
                    10,
                    product.getId(),
                    productionLine.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage condition",
                    10,
                    100);

            assertNotNull(cutPacking);
            assertEquals(10, cutPacking.getWeight(), 0.00001);
            assertEquals(product.getId(), cutPacking.getProductId());
            assertEquals(productionLine.getId(), cutPacking.getProductionLineId());
            assertEquals(2019, cutPacking.getSowingDay().getYear());
            assertEquals(2020, cutPacking.getCuttingDay().getYear());
            assertEquals("Producer", cutPacking.getProducer());
            assertEquals("Contact information", cutPacking.getContactInformation());
            assertEquals("Storage condition", cutPacking.getStorageCondition());
            assertEquals(10, (int) cutPacking.getGutterCount());
            assertEquals(100, (int) cutPacking.getGutterHoleCount());

            //verify that cannot create cut packings from products of different facilities
            Product juvaProduct = testBuilder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JUVA);
            cutPacking.setProductId(juvaProduct.getId());
            testBuilder.admin().cutPackings().assertCreateFailStatus(cutPacking, 400);

            //verify that cannot create cut packings from production line of different facilities
            ProductionLine juvaProductionLine = testBuilder.admin().productionLines().create("1", 100, Facility.JUVA);
            cutPacking.setProductId(product.getId());
            cutPacking.setProductionLineId(juvaProductionLine.getId());
            testBuilder.admin().cutPackings().assertCreateFailStatus(cutPacking, 400);
        }
    }

    @Test
    public void testUpdateCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            List<LocalizedValue> testEntry = new ArrayList<>();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            ArrayList<PackageSize> packageSizes = Lists.newArrayList(size);
            Product product = testBuilder.admin().products().create(testEntry, packageSizes, false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            Product product2 = testBuilder.admin().products().create(testEntry, packageSizes, false);
            ProductionLine productionLine2 = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30).withYear(2019);
            OffsetDateTime cuttingDay = OffsetDateTime.now().withYear(2019);

            CutPacking createdCutPacking = testBuilder.admin().cutPackings().create(
                    10,
                    product.getId(),
                    productionLine.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage condition",
                    10,
                    100);

            assertEquals(2019, createdCutPacking.getSowingDay().getYear());
            assertEquals(2019, createdCutPacking.getCuttingDay().getYear());

            createdCutPacking.setWeight(100.0);
            createdCutPacking.setProductionLineId(productionLine2.getId());
            createdCutPacking.setProductId(product2.getId());
            createdCutPacking.setSowingDay(sowingDay.withYear(2020));
            createdCutPacking.setCuttingDay(cuttingDay.withYear(2020));
            createdCutPacking.setProducer("Producer 2");
            createdCutPacking.setContactInformation("Contact information 2");
            createdCutPacking.setGutterCount(20);
            createdCutPacking.setGutterHoleCount(200);
            createdCutPacking.setStorageCondition("New storage condition");

            CutPacking updatedCutPacking = testBuilder.admin().cutPackings().update(createdCutPacking);

            assertNotNull(updatedCutPacking);
            assertNotNull(updatedCutPacking.getId());
            assertEquals(100, updatedCutPacking.getWeight(), 0.00001);
            assertEquals(product2.getId(), updatedCutPacking.getProductId());
            assertEquals(productionLine2.getId(), updatedCutPacking.getProductionLineId());
            assertEquals(2020, updatedCutPacking.getSowingDay().getYear());
            assertEquals(2020, updatedCutPacking.getCuttingDay().getYear());
            assertEquals("Producer 2", updatedCutPacking.getProducer());
            assertEquals("Contact information 2", updatedCutPacking.getContactInformation());
            assertEquals(20, (int) updatedCutPacking.getGutterCount());
            assertEquals(200, (int) updatedCutPacking.getGutterHoleCount());
            assertEquals("New storage condition", updatedCutPacking.getStorageCondition());

            Product juvaProduct = testBuilder.admin().products().create(testEntry, Lists.newArrayList(size), false, Facility.JUVA);
            updatedCutPacking.setProductId(juvaProduct.getId());
            testBuilder.admin().cutPackings().assertUpdateFailStatus(updatedCutPacking, 400);

            updatedCutPacking.setProductId(UUID.randomUUID());
            testBuilder.admin().cutPackings().assertUpdateFailStatus(updatedCutPacking, 400);
            updatedCutPacking.setId(null);
            testBuilder.admin().cutPackings().assertCreateFailStatus(updatedCutPacking, 400);
        }
    }

    @Test
    public void testListCutPackings() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            List<LocalizedValue> testEntry = new ArrayList<>();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            ArrayList<PackageSize> packageSizes = Lists.newArrayList(size);

            Product product = testBuilder.admin().products().create(testEntry, packageSizes, false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

            Product product2 = testBuilder.admin().products().create(testEntry, packageSizes, false);
            ProductionLine productionLine2 = testBuilder.admin().productionLines().create("1", 100);

            testBuilder.admin().cutPackings().create(
                    10,
                    product.getId(),
                    productionLine.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100);

            testBuilder.admin().cutPackings().create(
                    10,
                    product2.getId(),
                    productionLine2.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100);

            testBuilder.admin().cutPackings().create(
                    10,
                    product2.getId(),
                    productionLine2.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100);

            testBuilder.admin().cutPackings().create(
                    10,
                    product2.getId(),
                    productionLine2.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100);

            assertEquals(4, testBuilder.admin().cutPackings().list(null, null, null, null, null, Facility.JOROINEN).size());
            assertEquals(3, testBuilder.admin().cutPackings().list(null, null, product2.getId(), null, null, Facility.JOROINEN).size());
            assertEquals(2, testBuilder.admin().cutPackings().list(null, 2, product2.getId(), null, null, Facility.JOROINEN).size());
            assertEquals(1, testBuilder.admin().cutPackings().list(2, 2, product2.getId(), null, null, Facility.JOROINEN).size());
            assertEquals(0, testBuilder.admin().cutPackings().list(null, null, null, null, null, Facility.JUVA).size());

            testBuilder.admin().cutPackings().assertListFailStatus(400);
        }
    }

    @Test
    public void testFindCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            List<LocalizedValue> testEntry = new ArrayList<>();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, Lists.newArrayList(size), false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

            UUID cutPackingId = testBuilder.admin().cutPackings().create(
                    10,
                    product.getId(),
                    productionLine.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100).getId();

            CutPacking foundPacking = testBuilder.admin().cutPackings().find(cutPackingId);
            assertNotNull(foundPacking);

            testBuilder.admin().cutPackings().assertFindFailStatus(cutPackingId, Facility.JUVA, 404);
            testBuilder.admin().cutPackings().assertFindFailStatus(UUID.randomUUID(), Facility.JOROINEN,404);
        }

    }

    @Test
    public void testDeleteCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            List<LocalizedValue> testEntry = new ArrayList<>();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, Lists.newArrayList(size), false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

            UUID cutPackingId = testBuilder.admin().cutPackings().create(
                    10,
                    product.getId(),
                    productionLine.getId(),
                    cuttingDay,
                    sowingDay,
                    "Producer",
                    "Contact information",
                    "Storage information",
                    10,
                    100).getId();

            testBuilder.admin().cutPackings().delete(cutPackingId);
            testBuilder.admin().cutPackings().assertFindFailStatus(cutPackingId, Facility.JOROINEN, 404);
            testBuilder.admin().cutPackings().assertDeleteFailStatus(cutPackingId, 404);
        }
    }
}
