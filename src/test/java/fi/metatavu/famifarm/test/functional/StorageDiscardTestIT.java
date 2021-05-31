package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
import fi.metatavu.famifarm.client.model.StorageDiscard;
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
            PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
            PackageSize createdPackageSize1 = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize Unused"), 4);
            List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
            Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);

            StorageDiscard created = builder.worker1().storageDiscards().create(OffsetDateTime.now(), 30, product.getId(), createdPackageSize.getId());

            StorageDiscard found = builder.worker1().storageDiscards().find(created.getId());
            builder.worker1().storageDiscards().assertEquals(created, found);

            builder.worker1().storageDiscards().assertCreateFail(400, OffsetDateTime.now(), 30, product.getId(), createdPackageSize1.getId());
            builder.worker1().storageDiscards().assertCreateFail(404, OffsetDateTime.now(), 10, UUID.randomUUID(), createdPackageSize.getId());
        }
    }

    @Test
    public void testUpdateStorageDiscard() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
            List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
            Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false);

            StorageDiscard originalStorageDiscard = builder.worker1().storageDiscards().create(OffsetDateTime.parse("2021-02-01T10:15:30+01:00"), 10, product.getId(), createdPackageSize.getId());
            StorageDiscard updated = builder.manager().storageDiscards().update(originalStorageDiscard.getId(), OffsetDateTime.parse("2020-02-01T10:15:30+01:00"), 20, product.getId(), createdPackageSize.getId());

            Assertions.assertEquals(20, updated.getDiscardAmount());
        }
    }

    @Test
    public void testListStorageDiscards() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {
            PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
            Product product = builder.admin().products().create(builder.createLocalizedEntry("Porduct name", "Tuotteen nimi"), Lists.newArrayList(createdPackageSize), false);
            Product product1 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name1", "Tuotteen nimi1"), Lists.newArrayList(createdPackageSize), false);
            Product product2 = builder.admin().products().create(builder.createLocalizedEntry("Porduct name2", "Tuotteen nimi2"), Lists.newArrayList(createdPackageSize), false);

            builder.worker1().storageDiscards().create(OffsetDateTime.parse("2021-02-01T10:15:30+01:00"), 10, product.getId(), createdPackageSize.getId());
            builder.worker1().storageDiscards().create(OffsetDateTime.parse("2021-03-01T10:15:30+01:00"), 20, product.getId(), createdPackageSize.getId());
            builder.worker1().storageDiscards().create(OffsetDateTime.parse("2021-03-02T10:15:30+01:00"), 30, product1.getId(), createdPackageSize.getId());
            builder.worker1().storageDiscards().create(OffsetDateTime.parse("2021-04-01T10:15:30+01:00"), 40, product2.getId(), createdPackageSize.getId());

            builder.worker1().storageDiscards().assertCount(4, null, null, null);
            builder.worker1().storageDiscards().assertCount(2, null, null, product.getId());
            builder.worker1().storageDiscards().assertCount(3, "2021-02-28T10:15:30+01:00", "2021-04-05T10:15:30+01:00", null);
        }
    }
}
