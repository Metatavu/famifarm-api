package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

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
    
    ProductionLine result = new ProductionLine();
    result.setId(productionLine.getId());
    result.setLineNumber(productionLine.getLineNumber());

    return result;
  }
  
}
