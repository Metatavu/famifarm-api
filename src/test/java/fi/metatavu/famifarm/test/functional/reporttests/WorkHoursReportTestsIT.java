package fi.metatavu.famifarm.test.functional.reporttests;

import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.Packing;
import fi.metatavu.famifarm.test.functional.AbstractFunctionalTest;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for Juva Summary reports
 *
 * @author Antti Leinonen
 */
@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class WorkHoursReportTestsIT extends AbstractFunctionalTest {

  @Test
  public void SowingWorkHoursReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createSowingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_SOWING_WORK_HOURS_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 4, 1);
        builder.admin().reports().assertCellValue(12, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(0.375, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(144, workbook, 0, 4, 5);
        builder.admin().reports().assertCellValue(4.5, workbook, 0, 4, 6);
      }
    }
  }

  @Test
  public void PlantingWorkHoursReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createPlantingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PLANTING_WORK_HOURS_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name 0", workbook, 0, 4, 1);
        builder.admin().reports().assertCellValue(2, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(0.125, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(1.5, workbook, 0, 4, 5);
      }
    }
  }

  @Test
  public void PackingWorkHoursReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Packing> createdEvents = createPackingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PACKING_WORK_HOURS_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("test value", workbook, 0, 4, 1);
        builder.admin().reports().assertCellValue(5000, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(50, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(60000, workbook, 0, 4, 5);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4, 6);
      }
    }
  }

  @Test
  public void HarvestWorkHoursReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createHarvestEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_HARVEST_WORK_HOURS_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name 0", workbook, 0, 4, 1);
        builder.admin().reports().assertCellValue(50, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(60, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4, 5);
        builder.admin().reports().assertCellValue(720, workbook, 0, 4, 6);
      }
    }
  }

  @Test
  public void PlantingWorkHourSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createPlantingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PLANTING_WORK_HOUR_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name 0", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(2, workbook, 0, 4, 2);
        builder.admin().reports().assertCellValue(0.125, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(24, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(1.5, workbook, 0, 4, 5);

        builder.admin().reports().assertCellValue("Total", workbook, 0, 4 + eventCount, 0);
        builder.admin().reports().assertCellValue(2 * eventCount, workbook, 0, 4 + eventCount, 2);
        builder.admin().reports().assertCellValue(0.125 * eventCount, workbook, 0, 4 + eventCount, 3);
        builder.admin().reports().assertCellValue(24, workbook, 0, 4 + eventCount, 4);
        builder.admin().reports().assertCellValue(1.5, workbook, 0, 4 + eventCount, 5);
      }
    }
  }

  @Test
  public void HarvestWorkHoursSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createHarvestEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_HARVEST_WORK_HOUR_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name 0", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(50, workbook, 0, 4, 2);
        builder.admin().reports().assertCellValue(60, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(720, workbook, 0, 4, 5);

        builder.admin().reports().assertCellValue("Total", workbook, 0, 4 + eventCount, 0);
        builder.admin().reports().assertCellValue(50 * eventCount, workbook, 0, 4 + eventCount, 2);
        builder.admin().reports().assertCellValue(60 * eventCount, workbook, 0, 4 + eventCount, 3);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4 + eventCount, 4);
        builder.admin().reports().assertCellValue(720, workbook, 0, 4 + eventCount, 5);
      }
    }
  }

  @Test
  public void PackingWorkHoursSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Packing> createdEvents = createPackingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_PACKING_WORK_HOUR_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("test value", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(5000, workbook, 0, 4, 2);
        builder.admin().reports().assertCellValue(50, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(60000, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4, 5);

        builder.admin().reports().assertCellValue("Total", workbook, 0, 4 + eventCount, 0);
        builder.admin().reports().assertCellValue(5000 * eventCount, workbook, 0, 4 + eventCount, 2);
        builder.admin().reports().assertCellValue(50 * eventCount, workbook, 0, 4 + eventCount, 3);
        builder.admin().reports().assertCellValue(60000, workbook, 0, 4 + eventCount, 4);
        builder.admin().reports().assertCellValue(600, workbook, 0, 4 + eventCount, 5);
      }
    }
  }

  @Test
  public void SowingWorkHoursSummaryReportTest() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      int eventCount = 9;
      Facility facility = Facility.JUVA;
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      List<Event> createdEvents = createSowingEvents(builder, facility, eventCount);
      List<Event> createdEvents2 = createSowingEvents(builder, facility, eventCount);

      System.out.println(createdEvents);
      System.out.println(createdEvents2);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(facility, "JUVA_SOWING_WORK_HOUR_SUMMARY_REPORT", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(108, workbook, 0, 4, 2);
        builder.admin().reports().assertCellValue(3.375, workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue(144, workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue(4.5, workbook, 0, 4, 5);

        builder.admin().reports().assertCellValue("Total", workbook, 0, 4 + 2, 0);
        builder.admin().reports().assertCellValue(108 * 2, workbook, 0, 4 + 2, 2);
        builder.admin().reports().assertCellValue(3.375 * 2, workbook, 0, 4 + 2, 3);
        builder.admin().reports().assertCellValue(144, workbook, 0, 4 + 2, 4);
        builder.admin().reports().assertCellValue(4.5, workbook, 0, 4 + 2, 5);
      }
    }
  }
}
