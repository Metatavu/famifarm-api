package fi.metatavu.famifarm.reporting.xlsx.workhours;

import fi.metatavu.famifarm.reporting.xlsx.AbstractXlsxReport;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractWorkHoursReport extends AbstractXlsxReport {

  protected String formatDuration(Duration duration) {
    return String.format("%d:%02d",
      duration.toHours(),
      duration.toMinutesPart()
    );
  }

  /**
   * Formats a given offset
   *
   * @param date Date time
   *
   * @return formatted date string
   */
  protected String formatOffsetDateTime(OffsetDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return date.format(formatter);
  }
}
