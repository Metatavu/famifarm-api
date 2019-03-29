package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Team;
import fi.metatavu.famifarm.rest.model.ProductionLine;

/**
 * Translator for package size
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductionLineTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA production line object into REST production line object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public ProductionLine translateProductionLine(fi.metatavu.famifarm.persistence.model.ProductionLine productionLine) {
    if (productionLine == null) {
      return null;
    }

    Team defaultTeam = productionLine.getDefaultTeam();
    
    ProductionLine result = new ProductionLine();
    result.setId(productionLine.getId());
    result.setDefaultTeamId(defaultTeam != null ? defaultTeam.getId() : null);
    result.setLineNumber(productionLine.getLineNumber());
    result.setDefaultGutterHoleCount(productionLine.getDefaultGutterHoleCount());

    return result;
  }
  
}
