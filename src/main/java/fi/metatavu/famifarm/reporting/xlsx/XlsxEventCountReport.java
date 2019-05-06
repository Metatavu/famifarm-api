package fi.metatavu.famifarm.reporting.xlsx;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Batch;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;

/**
 * Abstract Report for event counts
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public abstract class XlsxEventCountReport extends AbstractXlsxReport {

  @Inject
  private LocalesController localesController;
  
  @Inject
  private EventController eventController;
  
  @Inject
  private BatchController batchController;
  
  @Inject
  private LocalizedValueController localizedValueController;

  /**
   * Counts units from single batch
   * 
   * @param events events from single batch
   * @return count of units
   */
  protected abstract Double countUnits(List<Event> events);

  /**
   * Gets the localized report title
   * 
   * @param locale locale
   * @return localized report title
   */
  protected abstract String getTitle(Locale locale);

  @Override
  public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(getTitle(locale));
      
      int productIndex = 0;
      int countIndex = 1;
      
      Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
      Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());
      
      // Headers 
      
      xlsxBuilder.setCellValue(sheetId, 0, 0, getTitle(locale)); 
      xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime)); 
      
      // Values
      
      List<Batch> batches = batchController.listBatches(null, null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")), null, null);
      Map<UUID, ReportRow> rowLookup = new HashMap<>();
      
      for (Batch batch : batches) {
        List<Event> events = eventController.listEvents(batch, null, null);
        
        Product product = batch.getProduct();
        Double count = countUnits(events);
        if (rowLookup.containsKey(product.getId())) {
          rowLookup.get(product.getId()).addCount(count);
        } else {
          rowLookup.put(product.getId(), new ReportRow(localizedValueController.getValue(product.getName(), locale), count));
        }
      }

      int rowIndex = 4;
      List<ReportRow> rows = new ArrayList<>(rowLookup.values());
      Collections.sort(rows);

      for (ReportRow row : rows) {
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, row.getProductName());
        xlsxBuilder.setCellValue(sheetId, rowIndex, countIndex, Double.toString(row.getCount()));
        rowIndex++;
      } 

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Inner class representing single row in report
   */
  private class ReportRow implements Comparable<ReportRow>{

    public ReportRow(String productName, Double count) {
      this.productName = productName;
      this.count = count;
    }
    
    private Double count;

    private String productName;

    /**
     * Increases count
     * 
     * @param toAdd amount to increase
     */
    public void addCount(Double toAdd) {
      count += toAdd;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
      return productName;
    }

    /**
     * @return the count
     */
    public Double getCount() {
      return count;
    }

    @Override
    public int compareTo(ReportRow other) {
      return this.productName.compareToIgnoreCase(other.getProductName());
    }
  }

}
