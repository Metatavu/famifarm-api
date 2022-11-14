package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Date;

import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.reporting.ReportFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.ReportsApi;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for reports
 * 
 * @author Antti Leppä
 */
public class ReportTestBuilderResource extends AbstractTestBuilderResource<Object, ReportsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public ReportTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates report with given type
   * 
   * @param type type
   * @return report data
   * @throws IOException thrown when request fails
   */
  public byte[] createReport(Facility facility, String type, String fromTime, String toTime, ReportFormat reportFormat) throws IOException {
    ApiClient apiClient = getApiClient();
    return getBinaryData(apiClient, new URL(String.format("%s/v1/%s/reports/%s?fromTime=%s&toTime=%s&format=%s", apiClient.getBasePath(), facility, type, fromTime, toTime, reportFormat)));
  }

  /**
   * Asserts that cell's value is expected
   * 
   * @param data data
   */
  public Workbook loadWorkbook(byte[] data) throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
      return WorkbookFactory.create(inputStream);  
    }
  }
  
  /**
   * Asserts that cell's value is expected
   * 
   * @param expected expected
   * @param workbook workbook
   * @param sheetIndex sheet index
   * @param rowIndex row index
   * @param cellIndex cell index
   */
  public void assertCellValue(OffsetDateTime expected, Workbook workbook, int sheetIndex, int rowIndex, int cellIndex) {
    Cell cell = getCell(workbook, sheetIndex, rowIndex, cellIndex); 
    assertEquals(Date.from(expected.toInstant()), cell.getDateCellValue());
  }
  
  /**
   * Asserts that cell's value is expected
   * 
   * @param expected expected
   * @param workbook workbook
   * @param sheetIndex sheet index
   * @param rowIndex row index
   * @param cellIndex cell index
   */
  public void assertCellValue(String expected, Workbook workbook, int sheetIndex, int rowIndex, int cellIndex) {
    Cell cell = getCell(workbook, sheetIndex, rowIndex, cellIndex);  
    assertEquals(expected, cell.getStringCellValue());
  }
  
  /**
   * Asserts that cell's value is expected
   * 
   * @param expected expected
   * @param workbook workbook
   * @param sheetIndex sheet index
   * @param rowIndex row index
   * @param cellIndex cell index
   */
  public void assertCellValue(Date expected, Workbook workbook, int sheetIndex, int rowIndex, int cellIndex) {
    Cell cell = getCell(workbook, sheetIndex, rowIndex, cellIndex);    
    assertEquals(expected, cell.getDateCellValue());
  }
  
  /**
   * Asserts that cell's value is expected
   * 
   * @param expected expected
   * @param workbook workbook
   * @param sheetIndex sheet index
   * @param rowIndex row index
   * @param cellIndex cell index
   */
  public void assertCellValue(double expected, Workbook workbook, int sheetIndex, int rowIndex, int cellIndex) {
    Cell cell = getCell(workbook, sheetIndex, rowIndex, cellIndex);    
    assertEquals(expected, cell.getNumericCellValue(), 0d);
  }

  /**
   * Finds a cell
   * 
   * @param workbook workbook
   * @param sheetIndex sheet index
   * @param rowIndex row index
   * @param cellIndex cell index
   * @return cell
   */
  private Cell getCell(Workbook workbook, int sheetIndex, int rowIndex, int cellIndex) {
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    assertNotNull(sheet);
    
    Row row = sheet.getRow(rowIndex);
    assertNotNull(row);
    
    Cell cell = row.getCell(cellIndex);
    assertNotNull(cell);

    return cell;
  }

  @Override
  public void clean(Object t) {
    // Nothing to clean 
  }
  
}
