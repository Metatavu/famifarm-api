package fi.metatavu.famifarm.test.functional;

import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Test class for printing
 */
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
                PackageSize packageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
                Product product = builder.admin().products().create(builder.createLocalizedEntry("Valkokärpässieni"), packageSize);
                Packing packing = builder.admin().packings().create(product.getId(), OffsetDateTime.now(), 5, PackingState.IN_STORE, packageSize);
                builder.admin().printers().print(packing.getId(), printers.get(0).getId());
            }
        }
    }
}
