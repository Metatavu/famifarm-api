package fi.metatavu.famifarm.test.functional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Lists;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.reporting.ReportFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for reports
 * 
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class ReportTestsIT extends AbstractFunctionalTest {

  @Test
  public void testXlsxExampleReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      
      createSowingEvent(builder, Facility.JOROINEN);
      Event tableSpreadEvent = createTableSpreadEvent(builder, Facility.JOROINEN);
      Event cultivationObservationEvent = createCultivationObservationEvent(builder, Facility.JOROINEN);
      createHarvestEvent(builder, Facility.JOROINEN);
      createPlantingEvent(builder, Facility.JOROINEN);
  
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "XLS_EXAMPLE", null, null, null);
      assertNotNull(data);
    }
  }
  
  @Test
  public void testXlsxWastageReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      builder.admin().wastageReasons();
      createWastageEvent(builder, facility);
      createWastageEvent(builder, facility);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "WASTAGE", fromTime, toTime, null);
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
  public void testJsonWastageReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Facility facility = Facility.JOROINEN;
      builder.admin().wastageReasons();
      createWastageEvent(builder, facility);
      createWastageEvent(builder, facility);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "WASTAGE", fromTime, toTime, ReportFormat.JSON);
      assertNotNull(data);
      try {
        ObjectMapper objectMapper = getObjectMapper();
        CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, fi.metatavu.famifarm.reporting.json.models.Event.class);
        List<fi.metatavu.famifarm.reporting.json.models.Event> events = objectMapper.readValue(data, type);
        assertNotNull(events);
      }
      catch (Exception ex) {
        assertNull(ex);
      }
    }
  }

  @Test
  public void testXlsxGrowthTimeReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      
      builder.admin().performedCultivationActions();
      builder.admin().pests();
      
      createSowingEvent(builder, product, startTime, endTime);
      createSowingEvent(builder, product, startTime, endTime);
      
      createCultivationObservationEvent(builder, product, 20d);
      createCultivationObservationEvent(builder, product, 10d);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "GROWTH_TIME", fromTime, toTime, null);
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
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);

      builder.admin().wastageReasons();
      
      createSowingEvent(builder, product, 1, startTime, endTime);
      createSowingEvent(builder, product, 1, startTime, endTime);
      createPlantingEvent(builder, product, 10, 3);
      
      startTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      
      createWastageEvent(builder, product, startTime, endTime);
      createHarvestEvent(builder, HarvestEventType.BOXING, product);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "YIELD", fromTime, toTime, null);
      assertNotNull(data);
      
//      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
//        builder.admin().reports().assertCellValue("Team", workbook, 0, 3, 0);
//        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 1);
//        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 2);
//        builder.admin().reports().assertCellValue("Harvested", workbook, 0, 3, 3);
//        builder.admin().reports().assertCellValue("In boxes", workbook, 0, 3, 4);
//        builder.admin().reports().assertCellValue("Yield %", workbook, 0, 3, 5);
//      }
    }
  }
  
  @Test
  public void testXlsxPlantingYieldReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      List<LocalizedValue> name = builder.createLocalizedEntry("Porduct name", "Tuotteen nimi");
      Product product = builder.admin().products().create(name, Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);

      builder.admin().wastageReasons();
      
      createSowingEvent(builder, product, 1, startTime, endTime);
      createSowingEvent(builder, product, 1, startTime, endTime);
      
      createPlantingEvent(builder, product, 25, 2);
      
      startTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      endTime = OffsetDateTime.of(2022, 2, 2, 4, 5, 6, 0, ZoneOffset.UTC);
      
      createHarvestEvent(builder, HarvestEventType.BOXING, product);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2021, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "PLANTING_YIELD", fromTime, toTime, null);
      assertNotNull(data);
      
//      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
//        builder.admin().reports().assertCellValue("Team", workbook, 0, 3, 0);
//        builder.admin().reports().assertCellValue("Product", workbook, 0, 3, 1);
//        builder.admin().reports().assertCellValue("Date", workbook, 0, 3, 2);
//      }
    }
  }

  @Test
  public void testXlsxSowedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      ArrayList<PackageSize> packageSizes = Lists.newArrayList(createdPackageSize);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), packageSizes, false, Facility.JOROINEN);
      
      createSowingEvent(builder, product, 10, startTime, endTime);
      createSowingEvent(builder, product, 10, startTime, endTime);

      Product product2 = builder.admin().products().create(builder.createLocalizedEntry("B - product", "B - tuote"), packageSizes, false, Facility.JOROINEN);
      createSowingEvent(builder, product2, 10, startTime, endTime);

      Product product3 = builder.admin().products().create(builder.createLocalizedEntry("C - product", "C - tuote"), packageSizes, false, Facility.JOROINEN);
      createSowingEvent(builder, product3, 25, startTime, endTime);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "SOWED", fromTime, toTime, null);
      assertNotNull(data);

//       try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
//        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
//        builder.admin().reports().assertCellValue(20.0, workbook, 0, 4, 1);
//        builder.admin().reports().assertCellValue("B - product", workbook, 0, 5, 0);
//        builder.admin().reports().assertCellValue(10.0, workbook, 0, 5, 1);
//      }
    }
  }

  @Test
  public void testXlsxPlantedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);

      // Create product in Joroinen
      PackageSize createdPackageSizeJoroinen = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      Product productJoroinen = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), Lists.newArrayList(createdPackageSizeJoroinen), false, Facility.JOROINEN);
      createSowingEvent(builder, productJoroinen, 10, startTime, endTime);
      createPlantingEvent(builder, productJoroinen, 10, 3);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "PLANTED", fromTime, toTime, null);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(30.0, workbook, 0, 4, 1);
      }

      // Check that Joroinen events do not show in Juva report
      byte[] dataJuva = builder.admin().reports().createReport(Facility.JUVA, "PLANTED", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(dataJuva)) {
        builder.admin().reports().assertRowNull(workbook, 0, 4);
      }
    }
  }

  @Test
  public void testXlsxSpreadReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      
      createSowingEvent(builder, product, 10, startTime, endTime);
      createTableSpreadEvent(builder, product);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "SPREAD", fromTime, toTime, null);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(810.0, workbook, 0, 4, 1);
      }
    }
  }

  @Test
  public void testXlsxHarvestedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
      
      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      
      createSowingEvent(builder, product, 10, startTime, endTime);
      createPlantingEvent(builder, product, 10, 3);
      createHarvestEvent(builder, HarvestEventType.BOXING, product, 3, 10, 0);
      
      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      
      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "HARVESTED", fromTime, toTime, null);
      assertNotNull(data);
      
      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(30.0, workbook, 0, 4, 1);
      }
    }
  }

  @Test
  public void testXlsxPackedReport() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OffsetDateTime startTime = OffsetDateTime.of(2022, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC);
      OffsetDateTime endTime = OffsetDateTime.of(2022, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);

      PackageSize createdPackageSize = builder.admin().packageSizes().create(builder.createLocalizedEntry("Test PackageSize"), 8, Facility.JOROINEN);
      Product product = builder.admin().products().create(builder.createLocalizedEntry("A - product", "A - tuote"), Lists.newArrayList(createdPackageSize), false, Facility.JOROINEN);
      builder.admin().packings().create(product.getId(), null, PackingType.BASIC, startTime, 10, PackingState.IN_STORE, createdPackageSize, Facility.JOROINEN);

      String fromTime = OffsetDateTime.of(2018, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();
      String toTime = OffsetDateTime.of(2025, 2, 1, 4, 5, 6, 0, ZoneOffset.UTC).toString();

      byte[] data = builder.admin().reports().createReport(Facility.JOROINEN, "PACKED", fromTime, toTime, null);
      assertNotNull(data);

      try (Workbook workbook = builder.admin().reports().loadWorkbook(data)) {
        builder.admin().reports().assertCellValue("A - product", workbook, 0, 4, 0);
        builder.admin().reports().assertCellValue(80.0, workbook, 0, 4, 1);
      }
    }
  }

}