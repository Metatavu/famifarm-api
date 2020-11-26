package fi.metatavu.famifarm.test.functional;

import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for cut packings
 */
public class CutPackingTestIT extends AbstractFunctionalTest {
    @Test
    public void testCreateCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            LocalizedEntry testEntry = new LocalizedEntry();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, size, false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

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
            assertEquals(getTestableDateTimeString(sowingDay), getTestableDateTimeString(cutPacking.getSowingDay()));
            assertEquals(getTestableDateTimeString(cuttingDay), getTestableDateTimeString(cutPacking.getCuttingDay()));
            assertEquals("Producer", cutPacking.getProducer());
            assertEquals("Contact information", cutPacking.getContactInformation());
            assertEquals("Storage condition", cutPacking.getStorageCondition());
            assertEquals(10, (int) cutPacking.getGutterCount());
            assertEquals(100, (int) cutPacking.getGutterHoleCount());
        }
    }

    @Test
    public void testUpdateCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            LocalizedEntry testEntry = new LocalizedEntry();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, size, false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            Product product2 = testBuilder.admin().products().create(testEntry, size, false);
            ProductionLine productionLine2 = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

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

            createdCutPacking.setWeight(100.0);
            createdCutPacking.setProductionLineId(productionLine2.getId());
            createdCutPacking.setProductId(product2.getId());
            createdCutPacking.setSowingDay(sowingDay.plusDays(1));
            createdCutPacking.setCuttingDay(cuttingDay.plusDays(1));
            createdCutPacking.setProducer("Producer 2");
            createdCutPacking.setContactInformation("Contact information 2");
            createdCutPacking.setGutterCount(20);
            createdCutPacking.setGutterHoleCount(200);
            createdCutPacking.setStorageCondition("New storage condition");

            CutPacking updatedCutPacking = testBuilder.admin().cutPackings().update(createdCutPacking);

            assertNotNull(updatedCutPacking);
            assertEquals(100, updatedCutPacking.getWeight(), 0.00001);
            assertEquals(product2.getId(), updatedCutPacking.getProductId());
            assertEquals(productionLine2.getId(), updatedCutPacking.getProductionLineId());
            assertEquals(getTestableDateTimeString(sowingDay.plusDays(1)), getTestableDateTimeString(updatedCutPacking.getSowingDay()));
            assertEquals(getTestableDateTimeString(cuttingDay.plusDays(1)), getTestableDateTimeString(updatedCutPacking.getCuttingDay()));
            assertEquals("Producer 2", updatedCutPacking.getProducer());
            assertEquals("Contact information 2", updatedCutPacking.getContactInformation());
            assertEquals(20, (int) updatedCutPacking.getGutterCount());
            assertEquals(200, (int) updatedCutPacking.getGutterHoleCount());
            assertEquals("New storage condition", updatedCutPacking.getStorageCondition());

            CutPacking cutPacking2 = updatedCutPacking;
            cutPacking2.setProductId(UUID.randomUUID());
            testBuilder.admin().cutPackings().assertUpdateFailStatus(cutPacking2, 400);
            cutPacking2.setId(null);
            testBuilder.admin().cutPackings().assertCreateFailStatus(cutPacking2, 400);

            CutPacking cutPacking3 = updatedCutPacking;
            cutPacking3.setProductionLineId(UUID.randomUUID());
            testBuilder.admin().cutPackings().assertUpdateFailStatus(cutPacking3, 400);
            cutPacking3.setId(null);
            testBuilder.admin().cutPackings().assertCreateFailStatus(cutPacking3, 400);
        }
    }

    @Test
    public void testListCutPackings() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            LocalizedEntry testEntry = new LocalizedEntry();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, size, false);
            ProductionLine productionLine = testBuilder.admin().productionLines().create("1", 100);

            OffsetDateTime sowingDay = OffsetDateTime.now().minusDays(30);
            OffsetDateTime cuttingDay = OffsetDateTime.now();

            Product product2 = testBuilder.admin().products().create(testEntry, size, false);
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

            assertEquals(4, testBuilder.admin().cutPackings().list(null, null, null, null, null).size());
            assertEquals(3, testBuilder.admin().cutPackings().list(null, null, product2.getId(), null, null).size());
            assertEquals(2, testBuilder.admin().cutPackings().list(null, 2, product2.getId(), null, null).size());
            assertEquals(1, testBuilder.admin().cutPackings().list(2, 2, product2.getId(), null, null).size());

            testBuilder.admin().cutPackings().assertListFailStatus(400);
        }
    }

    @Test
    public void testFindCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            LocalizedEntry testEntry = new LocalizedEntry();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, size, false);
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

            testBuilder.admin().cutPackings().assertFindFailStatus(UUID.randomUUID(), 404);
        }

    }

    @Test
    public void testDeleteCutPacking() throws Exception {
        try (TestBuilder testBuilder = new TestBuilder()) {
            LocalizedEntry testEntry = new LocalizedEntry();
            LocalizedValue testValue = new LocalizedValue();

            testValue.setLanguage("en");
            testValue.setValue("test value");
            testEntry.add(testValue);

            PackageSize size = testBuilder.admin().packageSizes().create(testEntry, 100);
            Product product = testBuilder.admin().products().create(testEntry, size, false);
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
            testBuilder.admin().cutPackings().assertFindFailStatus(cutPackingId, 404);
            testBuilder.admin().cutPackings().assertDeleteFailStatus(cutPackingId, 404);
        }
    }
}
