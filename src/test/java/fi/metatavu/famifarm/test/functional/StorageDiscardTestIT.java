package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class StorageDiscardTestIT extends AbstractFunctionalTest {

    @Test
    public void testCreateStorageDiscard() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize createdPackageSizeJoroinen = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
            PackageSize createdPackageSizeJoroinen1 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize Unused"), 4, Facility.JOROINEN);
            PackageSize createdPackageSizeJuva = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JUVA);
            PackageSize createdPackageSizeJuva1 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize Unused"), 4, Facility.JUVA);
            List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
            Product productJoroinen = builder.admin().products().create(name, Lists.newArrayList(createdPackageSizeJoroinen), false, Facility.JOROINEN);
            Product productJuva = builder.admin().products().create(name, Lists.newArrayList(createdPackageSizeJuva), false, Facility.JUVA);

            StorageDiscard created = builder.workerJoroinen().storageDiscards().create(OffsetDateTime.now(), 30, productJoroinen.getId(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            StorageDiscard createdJuva = builder.workerJoroinen().storageDiscards().create(OffsetDateTime.now(), 30, productJuva.getId(), createdPackageSizeJuva.getId(), Facility.JUVA);

            StorageDiscard foundJoroinen = builder.workerJoroinen().storageDiscards().find(created.getId(), Facility.JOROINEN);
            StorageDiscard foundJuva = builder.workerJoroinen().storageDiscards().find(createdJuva.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().assertEquals(created, foundJoroinen);
            builder.workerJoroinen().storageDiscards().assertEquals(createdJuva, foundJuva);

            builder.workerJoroinen().storageDiscards().assertCreateFail(400, OffsetDateTime.now(), 30, productJoroinen.getId(), createdPackageSizeJoroinen1.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().assertCreateFail(404, OffsetDateTime.now(), 10, UUID.randomUUID(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().assertCreateFail(400, OffsetDateTime.now(), 30, productJuva.getId(), createdPackageSizeJuva1.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().assertCreateFail(404, OffsetDateTime.now(), 10, UUID.randomUUID(), createdPackageSizeJuva.getId(), Facility.JUVA);

            builder.workerJoroinen().storageDiscards().assertCreateFail(400, productJoroinen.getId(), Facility.JUVA);
        }
    }

    @Test
    public void testUpdateStorageDiscard() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
            List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
            Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);

            StorageDiscard originalStorageDiscard = builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-02-01T10:15:30+01:00"), 10, product.getId(), createdPackageSize.getId(), Facility.JOROINEN);
            StorageDiscard updated = builder.managerJoroinen().storageDiscards().update(originalStorageDiscard.getId(), OffsetDateTime.parse("2020-02-01T10:15:30+01:00"), 20, product.getId(), createdPackageSize.getId());

            Assertions.assertEquals(20, updated.getDiscardAmount());
            builder.managerJoroinen().storageDiscards().assertUpdateFail(404, originalStorageDiscard, Facility.JUVA, UUID.randomUUID());
            builder.managerJoroinen().storageDiscards().assertUpdateFail(400, originalStorageDiscard, Facility.JUVA, originalStorageDiscard.getId());
        }
    }

    @Test
    public void testListStorageDiscards() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize createdPackageSizeJoroinen = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
            PackageSize createdPackageSizeJuva = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JUVA);
            Product productJoroinen = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSizeJoroinen), false, Facility.JOROINEN);
            Product productJoroinen1 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name1", "Tuotteen nimi1"), Lists.newArrayList(createdPackageSizeJoroinen), false, Facility.JOROINEN);
            Product productJoroinen2 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name2", "Tuotteen nimi2"), Lists.newArrayList(createdPackageSizeJoroinen), false, Facility.JOROINEN);
            Product productJuva = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSizeJuva), false, Facility.JUVA);
            Product productJuva1 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name1", "Tuotteen nimi1"), Lists.newArrayList(createdPackageSizeJuva), false, Facility.JUVA);
            Product productJuva2 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name2", "Tuotteen nimi2"), Lists.newArrayList(createdPackageSizeJuva), false, Facility.JUVA);

            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-02-01T10:15:30+01:00"), 10, productJoroinen.getId(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-03-01T10:15:30+01:00"), 20, productJoroinen.getId(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-03-02T10:15:30+01:00"), 30, productJoroinen1.getId(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-04-01T10:15:30+01:00"), 40, productJoroinen2.getId(), createdPackageSizeJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-02-01T10:15:30+01:00"), 10, productJuva.getId(), createdPackageSizeJuva.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-03-01T10:15:30+01:00"), 20, productJuva.getId(), createdPackageSizeJuva.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-03-02T10:15:30+01:00"), 30, productJuva1.getId(), createdPackageSizeJuva.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().create(OffsetDateTime.parse("2021-04-01T10:15:30+01:00"), 40, productJuva2.getId(), createdPackageSizeJuva.getId(), Facility.JUVA);

            builder.workerJoroinen().storageDiscards().assertCount(4, null, null, null, Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().assertCount(2, null, null, productJoroinen.getId(), Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().assertCount(3, "2021-02-28T10:15:30+01:00", "2021-04-05T10:15:30+01:00", null, Facility.JOROINEN);
            builder.workerJoroinen().storageDiscards().assertCount(4, null, null, null, Facility.JUVA);
            builder.workerJoroinen().storageDiscards().assertCount(2, null, null, productJuva.getId(), Facility.JUVA);
            builder.workerJoroinen().storageDiscards().assertCount(3, "2021-02-28T10:15:30+01:00", "2021-04-05T10:15:30+01:00", null, Facility.JUVA);

            builder.workerJoroinen().storageDiscards().assertListFail(Facility.JUVA, productJoroinen.getId(), 400);
            builder.workerJoroinen().storageDiscards().assertListFail(Facility.JOROINEN, productJuva.getId(), 400);
        }
    }

    @Test
    public void testFindStorageDiscard() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
            Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), Lists.newArrayList(packageSize), false, Facility.JOROINEN);
            StorageDiscard storageDiscard = builder.workerJoroinen().storageDiscards().create(OffsetDateTime.now(), 10, product.getId(), packageSize.getId(), Facility.JOROINEN);

            builder.workerJoroinen().storageDiscards().assertFindFail(404, Facility.JOROINEN, UUID.randomUUID());
            builder.workerJoroinen().storageDiscards().assertFindFail(400, Facility.JUVA, storageDiscard.getId());
        }
    }
}
