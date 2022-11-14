package fi.metatavu.famifarm.reporting.xlsx;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.model.Campaign;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.PackingType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Report for campaing packings
 */
@ApplicationScoped
public class XlsxPackedCampaingsReport extends AbstractXlsxReport {
    
    @Inject
    private LocalesController localesController;
    
    @Inject
    private PackingController packingController;

    protected String getTitle(Locale locale) {
        return localesController.getString(locale, "reports.packedCampaing.title");
    }

    @Override
    public void createReport(OutputStream output, Facility facility, Locale locale, Map<String, String> parameters) throws ReportException {
        try (XlsxBuilder xlsxBuilder = new XlsxBuilder()) {
            String sheetId = xlsxBuilder.createSheet(getTitle(locale));

            int campaingNameIndex = 0;
            int countIndex = 1;

            Date fromTime = Date.from(parseDate(parameters.get("fromTime")).toInstant());
            Date toTime = Date.from(parseDate(parameters.get("toTime")).toInstant());

            xlsxBuilder.setCellValue(sheetId, 0, 0, getTitle(locale));
            xlsxBuilder.setCellValue(sheetId, 1, 0, localesController.getString(locale, "reports.common.dateBetween", fromTime, toTime));

            List<Packing> allPackings = packingController.listPackings(null, null, facility, null, null, null, parseDate(parameters.get("toTime")), parseDate(parameters.get("fromTime")));
            List<Packing> packings = allPackings
              .stream()
              .filter(p -> p.getType().equals(PackingType.CAMPAIGN))
              .collect(Collectors.toList());

            Map<UUID, ReportRow> rowLookup = new HashMap<>();
            packings.stream().forEach(packing -> {
                Campaign campaign = packing.getCampaign();
                if (campaign != null && !rowLookup.containsKey(campaign.getId())) {
                    long campaingCont = packings
                      .stream()
                      .filter(p -> p.getCampaign() != null && p.getCampaign().getId().equals(campaign.getId()))
                      .count();
                    rowLookup.put(
                      campaign.getId(),
                      new ReportRow(campaign.getName(), campaingCont)
                    );
                }
            });

            int rowIndex = 4;
            List<ReportRow> rows = new ArrayList<>(rowLookup.values());
            Collections.sort(rows);

            for (ReportRow row : rows) {
                xlsxBuilder.setCellValue(sheetId, rowIndex, campaingNameIndex, row.getName());
                xlsxBuilder.setCellValue(sheetId, rowIndex, countIndex, row.getCount());
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

        public ReportRow(String name, long count) {
            this.name = name;
            this.count = count;
        }

        private long count;

        private String name;

        public String getName() {
          return name;
        }

        public long getCount() {
          return count;
        }

        @Override
        public int compareTo(ReportRow other) {
            return this.name.compareToIgnoreCase(other.getName());
        }
    }
}
