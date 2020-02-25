package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.Product;
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
  
      byte[] data = builder.admin().reports().createReport("XLS_EXAMPLE", null, null);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Batch", workbook, 0, 0, 0);
        builder.admin().reports().assertCellValue(String.format("%s - Product name", new SimpleDateFormat("yyyy-MM-dd").format(new Date())), workbook, 0, 1, 0);
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
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("WASTAGE", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 0);
        builder.admin().reports().assertCellValue("Worker", workbook, 0, 3, 2);
        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 3);
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 4, 3);
        builder.admin().reports().assertCellValue("HARVEST", workbook, 0, 4, 4);
        builder.admin().reports().assertCellValue("Test reason", workbook, 0, 4, 5);
        builder.admin().reports().assertCellValue("Additional information", workbook, 0, 3, 6);
        builder.admin().reports().assertCellValue("Product name", workbook, 0, 5, 3);
        builder.admin().reports().assertCellValue("Test reason", workbook, 0, 5, 5);
      }
    }
  }
  
  @Test
  public void testXlsxGrowthTimeReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      builder.admin().teams();
      
      createSowingEvent(builder, batch, startTime, endTime);
      createSowingEvent(builder, batch, startTime, endTime);
      
      createCultivationObservationEvent(builder, batch, 20d);
      createCultivationObservationEvent(builder, batch, 10d);
      
      startTime = OffsetDateTime.of(2022, 2, 9, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 10, 4, 5, 6, 0, ZoneOffset.UTC);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("GROWTH_TIME", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Line", workbook, 0, 3, 0);
        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 1);
        builder.admin().reports().assertCellValue("Packaging date", workbook, 0, 3, 2);
        builder.admin().reports().assertCellValue("Sowing date", workbook, 0, 3, 3);
        builder.admin().reports().assertCellValue("Average weight", workbook, 0, 3, 4);
        builder.admin().reports().assertCellValue("Growth time", workbook, 0, 3, 5);
      }
    }
  }
  
  @Test
  public void testXlsxYieldReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      builder.admin().teams();
      builder.admin().wastageReasons();
      
      createSowingEvent(builder, batch, 1, startTime, endTime);
      createSowingEvent(builder, batch, 1, startTime, endTime);
      createPlantingEvent(builder, batch, 10, 3);
      
      startTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      
      createWastageEvent(builder, batch, startTime, endTime);
      createHarvestEvent(builder, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum.BOXING, batch);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("YIELD", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Team", workbook, 0, 3, 0);
        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 1);
        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 2);
        builder.admin().reports().assertCellValue("Harvested", workbook, 0, 3, 3);
        builder.admin().reports().assertCellValue("In boxes", workbook, 0, 3, 4);
        builder.admin().reports().assertCellValue("Yield %", workbook, 0, 3, 5);
      }
    }
  }
  
  @Test
  public void testXlsxPlantingYieldReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      LocalizedEntry name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      builder.admin().teams();
      builder.admin().wastageReasons();
      
      createSowingEvent(builder, batch, 1, startTime, endTime);
      createSowingEvent(builder, batch, 1, startTime, endTime);
      
      createPlantingEvent(builder, batch, 25, 2);
      
      startTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      
      createHarvestEvent(builder, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum.BOXING, batch);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("PLANTING_YIELD", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("Team", workbook, 0, 3, 0);
        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 1);
        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 2);
      }
    }
  }

  @Test
  public void testXlsxSowedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      createSowingEvent(builder, batch, 10, startTime, endTime);
      createSowingEvent(builder, batch, 10, startTime, endTime);

      Batch batch2 = builder.admin().batches().create(product);
      createSowingEvent(builder, batch2, 10, startTime, endTime);

      Product product2 = builder.admin().products().create(builder.createLocalizedEntry("B - product", "B - tuote"), createdPackageSize);
      Batch batch3 = builder.admin().batches().create(product2);
      createSowingEvent(builder, batch3, 25, startTime, endTime);
      
      startTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("SOWED", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue("30.0", workbook, 0, 4, 1);
        builder.admin().reports().assertCellValue("B - product", workbook, 0, 5, 0);
        builder.admin().reports().assertCellValue("25.0", workbook, 0, 5, 1);
      }
    }
  }

  @Test
  public void testXlsxPlantedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      createSowingEvent(builder, batch, 10, startTime, endTime);
      createPlantingEvent(builder, batch, 10, 3);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("PLANTED", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue("30.0", workbook, 0, 4, 1);
      }
    }
  }

  @Test
  public void testXlsxSpreadReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      createSowingEvent(builder, batch, 10, startTime, endTime);
      createTableSpreadEvent(builder, batch);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("SPREAD", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue("525.0", workbook, 0, 4, 1);
      }
    }
  }

  @Test
  public void testXlsxHarvestedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), createdPackageSize);
      Batch batch = builder.admin().batches().create(product);
      
      createSowingEvent(builder, batch, 10, startTime, endTime);
      createPlantingEvent(builder, batch, 10, 3);
      createHarvestEvent(builder, TypeEnum.BOXING, batch, 3, null);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport("HARVESTED", fromTime, toTime);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue("30.0", workbook, 0, 4, 1);
      }
    }
  }

}