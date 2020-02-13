package fi.metatavu.famifarm.reporting.xlsx;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalesController;
import fi.metatavu.famifarm.rest.model.EventType;

/**
 * Sowed report
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class XlsxSowedReport extends XlsxEventCountReport {

  @Inject
  private LocalesController localesController;

  @Override
  protected String getTitle(Locale locale) {
    return localesController.getString(locale, "reports.sowed.title");
  }

  @Override
  protected EventType getEventType() {
    return EventType.SOWING;
  }

}
