package fi.metatavu.famifarm.reporting.xlsx.summaryreports;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.dao.PackingBasketDAO;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;
import fi.metatavu.famifarm.reporting.xlsx.XlsxBuilder;
import fi.metatavu.famifarm.reporting.xlsx.listreports.data.PackingData;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class XlsxPackingSummaryReport extends AbstractXlsxReport {

  @Inject
  private PackingController packingController;

  @Inject
  private LocalesController localesController;

  @Inject
  private LocalizedValueController localizedValueController;

  @Inject
  private PackingBasketDAO packingBasketDAO;

  final int productIndex = 0;
  final int bagAmountIndex = 1;
  final int boxAmountIndex = 2;

  @Override
  public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
    try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
      String title = localesController.getString(locale, "reports.packing_summary.title");
      String sheetId = xlsxBuilder.createSheet(title);
      OffsetDateTime toTime = parseDate(parameters.get("toTime"));
      OffsetDateTime fromTime = parseDate(parameters.get("fromTime"));

      List<PackingData> events = getEntities(facility, fromTime, toTime);

      int rowIndex = 0;
      int columnIndex = 0;

      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, title);
      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, columnIndex, localesController.getString(locale, "reports.common.dateBetween", Date.from(fromTime.toInstant()), Date.from(toTime.toInstant())));
      rowIndex++;
      rowIndex++;

      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.common.productHeader"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagAmountIndex, localesController.getString(locale, "reports.packing_summary.bag_count"));
      xlsxBuilder.setCellValue(sheetId, rowIndex, bagAmountIndex, localesController.getString(locale, "reports.packing_summary.box_count"));

      Map<Product, Pair<Integer, Integer>> products = new HashMap<>();

      for (PackingData event : events) {
        Product product = event.getPacking().getProduct();
        int boxAmount = event.getPacking().getPackedCount();
        int bagAmount = boxAmount * event.getPacking().getPackageSize().getSize();


        if (products.containsKey(product)) {
          bagAmount += products.get(product).first;
          boxAmount += products.get(product).second;
        }

        products.put(product, new Pair<>(bagAmount, boxAmount));
      }

      for (Map.Entry<Product, Pair<Integer, Integer>> entry : products.entrySet()) {
        rowIndex++;
        xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(entry.getKey().getName(), locale));
        xlsxBuilder.setCellValue(sheetId, rowIndex, bagAmountIndex, entry.getValue().first);
        xlsxBuilder.setCellValue(sheetId, rowIndex, boxAmountIndex, entry.getValue().second);
      }

      rowIndex++;
      xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localesController.getString(locale, "reports.sowing_summary.total"));

      int bagSum = products.values().stream().map(Pair::getFirst).reduce(Integer::sum).orElse(0);
      int boxSum = products.values().stream().map(Pair::getSecond).reduce(Integer::sum).orElse(0);

      xlsxBuilder.setCellValue(sheetId, rowIndex, bagAmountIndex, bagSum);
      xlsxBuilder.setCellValue(sheetId, rowIndex, boxAmountIndex, boxSum);

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

  static class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
      this.first = first;
      this.second = second;
    }

    public A getFirst() {
      return first;
    }

    public B getSecond() {
      return second;
    }
  }

}
