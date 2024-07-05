package fi.metatavu.famifarm.test.functional.reporttests;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.test.functional.AbstractFunctionalTest;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for Juva Listing reports
 *
 * @author Antti Leinonen
 */
@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class ListReportTestsIT extends AbstractFunctionalTest {

  @Test
  public void PlantingListReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvent = createPlantingEvents(builder, facility, eventCount);

      System.out.println(createdEvent);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PLANTING_LIST_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
          builder.admin().reports().assertCellValue("Product name " + i, workbook, 0, i + 4, 0);
          builder.admin().reports().assertCellValue(2, workbook, 0, i + 4, 5);
        }
      }
    }
  }

  @Test
  public void HarvestListReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createHarvestEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_HARVEST_LIST_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
            builder.admin().reports().assertCellValue("Product name " + i, workbook, 0, i + 4, 0);
            builder.admin().reports().assertCellValue(60, workbook, 0, i + 4, 6);
            builder.admin().reports().assertCellValue(20, workbook, 0, i + 4, 10);
        }
      }
    }
  }

}