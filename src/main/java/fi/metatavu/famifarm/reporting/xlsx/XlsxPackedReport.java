package fi.metatavu.famifarm.reporting.xlsx;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.EventCountController;
import fi.metatavu.famifarm.reporting.ReportException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.util.*;

/**
 * Report for packings
 */
@ApplicationScoped
public class XlsxPackedReport extends AbstractXlsxReport {
    
    @Inject
    private LocalesController localesController;
    
    @Inject
    private PackingController packingController;
    
    @Inject
    private LocalizedValueController localizedValueController;

    @Inject
    private EventCountController eventCountController;

    protected String getTitle(Locale locale) {
        return localesController.getString(locale, "reports.packed.title");
    }

    @Override
    public void createReport(OutputStream output, Locale locale, Map<String, String> parameters) throws ReportException {
        try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
            String sheetId = xlsxBuilder.createSheet(getTitle(locale));

            int productIndex = 0;
            int countIndex = 1;

            Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
            Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());

            xlsxBuilder.setCellValue(sheetId, 0, 0, getTitle(locale));
            xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));

            List<Packing> packings = packingController.listPackings(null, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));

            Map<UUID, ReportRow> rowLookup = new HashMap<>();
            packings.stream().forEach(packing -> {
                Product product = packing.getProduct();
                if (product != null && !rowLookup.containsKey(product.getId())) {
                    rowLookup.put(
                            product.getId(),
                            new ReportRow(localizedValueController.getValue(product.getName(), locale),
                                    eventCountController.countPackedUnitsByProduct(packings, product)
                    ));
                }
            });

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
