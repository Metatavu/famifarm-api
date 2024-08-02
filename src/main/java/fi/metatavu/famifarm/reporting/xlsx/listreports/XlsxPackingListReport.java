package fi.metatavu.famifarm.reporting.xlsx.listreports;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.dao.PackingVerificationWeightingDAO;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PackingBasket;
import fi.metatavu.famifarm.persistence.model.PackingVerificationWeighting;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.reporting.xlsx.listreports.data.PackingData;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class XlsxPackingListReport extends AbstractXlsxReport {

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private PackingController packingController;

  @Inject
  private PackingBasketDAO packingBasketDAO;

  @Inject
  private PackingVerificationWeightingDAO packingVerificationWeightingDAO;

  final int productIndex = 0;
  final int packingDateIndex = 1;
  final int bagCountIndex = 2;
  final int boxCountIndex = 3;
  final int dynamicUsedMaterialsIndex = 4;

  // After dynamic columns
  final int totalUsedMaterialsIndex = 5;
  final int weightingResultIndex = 6;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.packing_report.title"));

      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<PackingData> entities = getEntities(facility, fromTime, toTime);

      int columnIndex = 0;
      int rowIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.packing_report.title"));
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      // Static columns
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.packing_report.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, packingDateIndex, localesController.getString(locale, "reports.packing_report.packingDate"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagCountIndex, localesController.getString(locale, "reports.packing_report.bagCount"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxCountIndex, localesController.getString(locale, "reports.packing_report.boxCount"));

      // Dynamic column headers
      HashMap<Product, Integer> dynamicColumns = new HashMap<>();
      for (PackingData entity : entities) {
        List<PackingBasket> baskets = entity.getPackingBaskets().stream().distinct().collect(Collectors.toList());
        for (PackingBasket basket : baskets) {
          if (!dynamicColumns.containsKey(basket.getProduct()) && basket.getProduct().isRawMaterial()) {
            dynamicColumns.put(basket.getProduct(), dynamicColumns.size());
            xlsxBuilder.setCellValue(sheetId, rowIndex, dynamicUsedMaterialsIndex + dynamicColumns.size(), localizedValueController.getValue(basket.getProduct().getName(), locale));
          }
        }
      }

      xlsxBuilder.setCellValue(sheetId, rowIndex, totalUsedMaterialsIndex + dynamicColumns.size(), localesController.getString(locale, "reports.packing_report.totalUsedMaterials"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, weightingResultIndex + dynamicColumns.size(), localesController.getString(locale, "reports.packing_report.weightingResult"));

      for (PackingData packingData : entities) {
        rowIndex++;

        Packing packing = packingData.getPacking();
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(packing.getProduct().getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, packingDateIndex, packing.getStartTime());
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagCountIndex, packing.getPackedCount() * packing.getPackageSize().getSize()); // One of these is wrong
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxCountIndex, packing.getPackedCount());

        HashMap<Product, Integer> columnDynamicValues = new HashMap<>();
        for (PackingBasket basket : packingData.getPackingBaskets()) {
          if (!basket.getProduct().isRawMaterial()) {
            continue;
          }

          if (!columnDynamicValues.containsKey(basket.getProduct())) {
            columnDynamicValues.put(basket.getProduct(), 0);
          }

          int columnIndexForProduct = dynamicUsedMaterialsIndex + dynamicColumns.get(basket.getProduct());
          int value = columnDynamicValues.get(basket.getProduct());
          columnDynamicValues.put(basket.getProduct(), value + basket.getCount());
          xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndexForProduct, value + basket.getCount());
        }

        int totalUsedMaterials = columnDynamicValues.values().stream().mapToInt(Integer::intValue).sum();
        xlsxBuilder.setCellValue(sheetId, rowIndex, totalUsedMaterialsIndex + dynamicColumns.size(), totalUsedMaterials);

        float weight = packingVerificationWeightingDAO.listByPacking(packingData.getPacking()).stream().map(PackingVerificationWeighting::getWeight).reduce(0f, Float::sum);
        xlsxBuilder.setCellValue(sheetId, rowIndex, weightingResultIndex + dynamicColumns.size(), Float.toString(weight));
      }

      xlsxBuilder.write(output);
    } catch (Exception e) {
      throw new ReportException(e);
    }
  }

  private List<PackingData> getEntities(Facility facility, OffsetDateTime fromTime, OffsetDateTime toTime) {
    List<Packing> packings = packingController.listPackings(
      null,
      null,
      facility,
      null,
      null,
      null,
      toTime,
      fromTime
    );
    return packings.stream().map(
      packing -> new PackingData(
        packing,
        packingBasketDAO.listByPacking(packing)
      )
    ).collect(Collectors.toList());
  }
}
