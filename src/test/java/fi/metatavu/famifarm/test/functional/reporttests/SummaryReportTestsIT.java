package fi.metatavu.famifarm.test.functional.reporttests;

import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.HarvestEventData;
import fi.metatavu.famifarm.client.model.Packing;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.test.functional.AbstractFunctionalTest;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for Juva Summary reports
 *
 * @author Antti Leinonen
 */
@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class SummaryReportTestsIT extends AbstractFunctionalTest {
  @Test
  public void SowingSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createSowingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_SOWING_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(12 * 9, workbook, 0,  4, 1);
      }
    }
  }

  @Test
  public void PlantingSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createPlantingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PLANTING_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
          builder.admin().reports().assertCellValue("Product name " + i, workbook, 0, 4 + i, 0);
          builder.admin().reports().assertCellValue(2, workbook, 0,  4 + i, 1);
        }
        builder.admin().reports().assertCellValue(2 * eventCount, workbook, 0,  4 + eventCount, 1);
      }
    }
  }

  @Test
  public void PackingSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Packing> createdEvents = createPackingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PACKING_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
          builder.admin().reports().assertCellValue("test value", workbook, 0, 4 + i, 0);
          builder.admin().reports().assertCellValue(50 * 100, workbook, 0,  4 + i, 1);
          builder.admin().reports().assertCellValue(50, workbook, 0,  4 + i, 2);
        }
        builder.admin().reports().assertCellValue(100 * 50 * eventCount, workbook, 0,  4 + eventCount, 1);
        builder.admin().reports().assertCellValue(50 * eventCount, workbook, 0,  4 + eventCount, 2);
      }
    }
  }

  @Test
  public void HarvestSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createHarvestEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_HARVEST_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
          builder.admin().reports().assertCellValue("Product name " + i, workbook, 0, 4 + i, 0);
          builder.admin().reports().assertCellValue(50, workbook, 0,  4 + i, 1);
          builder.admin().reports().assertCellValue(60, workbook, 0,  4 + i, 2);
          builder.admin().reports().assertCellValue(3, workbook, 0,  4 + i, 3);
        }
        builder.admin().reports().assertCellValue(50 * eventCount, workbook, 0,  4 + eventCount, 1);
        builder.admin().reports().assertCellValue(60 * eventCount, workbook, 0,  4 + eventCount, 2);
      }
    }
  }

  @Test
  public void YieldSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();

      List<Event> createdEvents = createHarvestEvents(builder, facility, eventCount);
      List<Event> createdWastageEvents = new ArrayList<>();
      for (int i = 0; i < eventCount; i++) {
        HarvestEventData data = builder.admin().events().readHarvestEventData(createdEvents.get(0));
        createdWastageEvents.add(createWastageEvent(builder, facility, createdEvents.get(i).getProductId(), data.getProductionLineId(), i));
      }

      for (int i = 0; i < eventCount; i++) {
        createPackingEventWithProductIds(builder, List.of(createdEvents.get(i).getProductId()), facility, i);
      }

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_YIELD_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        for (int i = 0; i < eventCount; i++) {
          builder.admin().reports().assertCellValue("Product name " + i, workbook, 0, 4 + i, 0);
          builder.admin().reports().assertCellValue(3, workbook, 0,  4 + i, 1);
          builder.admin().reports().assertCellValue(1, workbook, 0,  4 + i, 2);
          builder.admin().reports().assertCellValue(2, workbook, 0,  4 + i, 3);
          //TODO truncate the output value to 2 decimal places
          builder.admin().reports().assertCellValue("66.66666666666666%", workbook, 0,  4 + i, 4);
          builder.admin().reports().assertCellValue("100.0%", workbook, 0,  4 + i, 5);
        }
        builder.admin().reports().assertCellValue(3 * eventCount, workbook, 0,  4 + eventCount, 1);
        builder.admin().reports().assertCellValue(eventCount, workbook, 0,  4 + eventCount, 2);
        builder.admin().reports().assertCellValue(2 * eventCount, workbook, 0,  4 + eventCount, 3);
        builder.admin().reports().assertCellValue("66.66666666666666%", workbook, 0,  4 + eventCount, 4);
        builder.admin().reports().assertCellValue("100.0%", workbook, 0,  4 + eventCount, 5);
      }

      // Wastage reason and event won't auto close correctly
      for (Event event : createdWastageEvents) {
        builder.admin().events().delete(event);
      }
    }
  }
}
