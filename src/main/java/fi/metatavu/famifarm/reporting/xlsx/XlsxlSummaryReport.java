package fi.metatavu.famifarm.reporting.xlsx;

import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.model.Event;
import fi.metatavu.famifarm.persistence.model.Product;
import fi.metatavu.famifarm.reporting.EventCountController;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Summary report
 */
@ApplicationScoped
public class XlsxlSummaryReport extends AbstractXlsxReport {

    @Inject
    private LocalesController localesController;

    @Inject
    private EventController eventController;

    @Inject
    private LocalizedValueController localizedValueController;

    @Inject
    private EventCountController eventCountController;

    @Override
    public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
        try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
            String sheetId = xlsxBuilder.createSheet(localesController.getString(locale, "reports.summary.title"));

            int productIndex = 0;
            int cartSowedIndex = 1;
            int cartPlantedIndex = 2;
            int tableHarvestedIndex = 3;
            int basketsHarvestedIndex = 4;

            int headerRow1 = 3;
            int headerRow2 = 4;

            Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
            Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());

            // Headers
            xlsxBuilder.setCellValue(sheetId, 0, 0, localesController.getString(locale, "reports.summary.title"));
            xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));

            xlsxBuilder.setCellValue(sheetId, headerRow1, cartSowedIndex, localesController.getString(locale, "reports.summary.cartSowed1"));
            xlsxBuilder.setCellValue(sheetId, headerRow1, cartPlantedIndex, localesController.getString(locale, "reports.summary.cartPlanted1"));
            xlsxBuilder.setCellValue(sheetId, headerRow1, tableHarvestedIndex, localesController.getString(locale, "reports.summary.tableHarvested1"));
            xlsxBuilder.setCellValue(sheetId, headerRow1, basketsHarvestedIndex, localesController.getString(locale, "reports.summary.basketsHarvested1"));

            xlsxBuilder.setCellValue(sheetId, headerRow2, cartSowedIndex, localesController.getString(locale, "reports.summary.cartSowed2"));
            xlsxBuilder.setCellValue(sheetId, headerRow2, cartPlantedIndex, localesController.getString(locale, "reports.summary.cartPlanted2"));
            xlsxBuilder.setCellValue(sheetId, headerRow2, tableHarvestedIndex, localesController.getString(locale, "reports.summary.tableHarvested2"));
            xlsxBuilder.setCellValue(sheetId, headerRow2, basketsHarvestedIndex, localesController.getString(locale, "reports.summary.basketsHarvested2"));

            // Values
            int rowIndex = headerRow2 + 1;
            List<Event> allEvents = eventController.listByFacilityAndStartTimeAfterAndStartTimeBefore(facility, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));

            Map<Product, List<Event>> collectedEvents = allEvents.stream().collect(groupingBy(Event::getProduct));

            int cartCells = 32;
            // Rows = number of products
            for (Map.Entry<Product, List<Event>> productEntry : collectedEvents.entrySet()) {
                Product product = productEntry.getKey();
                List<Event> events = productEntry.getValue();
                Double sowedCarts = eventCountController.countSowedUnits(events) / cartCells;
                Double plantedCarts = eventCountController.countPlantedUnits(events) / cartCells;
                Double harvestedTables = eventCountController.countHarvestedUnits(events);
                Double harvestedBaskets = eventCountController.countHarvestedBaskets(events);

                xlsxBuilder.setCellValue(sheetId, rowIndex, productIndex, localizedValueController.getValue(product.getName(), locale));
                xlsxBuilder.setCellValue(sheetId, rowIndex, cartSowedIndex, sowedCarts);
                xlsxBuilder.setCellValue(sheetId, rowIndex, cartPlantedIndex, plantedCarts);
                xlsxBuilder.setCellValue(sheetId, rowIndex, tableHarvestedIndex, harvestedTables);
                xlsxBuilder.setCellValue(sheetId, rowIndex, basketsHarvestedIndex, harvestedBaskets);

                rowIndex++;
            }
            xlsxBuilder.write(output);
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }
}
