package fi.metatavu.famifarm.test.functional;

import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import io.quarkus.test.common.QuarkusTestResource;

/**
 * Test class for printing
 */
@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class PrintingTestsIT extends AbstractFunctionalTest {
    @Test
    public void testPrinters() throws Exception {
        try (TestBuilder builder = new TestBuilder()) {

            List<Printer> printers = builder.admin().printers().getPrinters();
            assertNotNull(printers);
            if (System.getenv("TEST_FAMIFARM_PRINTING") != null && System.getenv("TEST_FAMIFARM_PRINTING").equals("TRUE")) {
                assertTrue(printers.size() > 0);
                assertNotNull(printers.get(0).getName());
                assertNotNull(printers.get(0).getId());
                PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
                Product product = builder.admin().products().create(builder.createLocalizedEntry("Valkokärpässieni"), Lists.newArrayList(packageSize), false, Facility.JOROINEN);
                Packing packing = builder.admin().packings().create(product.getId(), null, PackingType.BASIC, OffsetDateTime.now(), 5, PackingState.IN_STORE, packageSize, Facility.JOROINEN);
                builder.admin().printers().print(packing.getId(), printers.get(0).getId());
            }
        }
    }
}
