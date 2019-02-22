package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;


/**
 * Tests for reports
 * 
 * @author Antti Leppä
 */
public class ReportTestsIT extends AbstractFunctionalTest {

  @Test
  public void testXlsxExampleReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      builder.admin().teams();
      
      createSowingEvent(builder);
      Event tableSpreadEvent = createTableSpreadEvent(builder);
      Event cultivationObservationEvent = createCultivationObservationEvent(builder);
      createHarvestEvent(builder);
      createPlantingEvent(builder);
      createPackingEvent(builder);
  
      byte[] data = builder.admin().reports().createReport("XLS_EXAMPLE", null, null);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Batch", workbook, 0, 0, 0);
        builder.admin().reports().assertCellValue(String.format("%s - Product name", new SimpleDateFormat("yyyy-MM-dd").format(new Date())), workbook, 0, 1, 0);
        builder.admin().reports().assertCellValue(tableSpreadEvent.getStartTime(), workbook, 0, 1, 1);
        builder.admin().reports().assertCellValue(cultivationObservationEvent.getEndTime(), workbook, 0, 2, 2);
        builder.admin().reports().assertCellValue("User, Admin", workbook, 0, 3, 3);
      }
    }
  }
  
  @Test
  public void testXlsxWastageReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().wastageReasons();
      createWastageEvent(builder);
      createWastageEvent(builder);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2020, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("WASTAGE", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 0);
        builder.admin().reports().assertCellValue("Worker", workbook, 0, 3, 2);
        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 3);
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue("Test reason", workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 5, 3);
        builder.admin().reports().assertCellValue("Test reason", workbook, 0, 5, 4);
        builder.admin().reports().assertCellValue("User, Admin", workbook, 0, 4, 2);
      }
    }
  }

}