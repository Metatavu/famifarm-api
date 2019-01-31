package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.PerformedCultivationAction;

/**
 * Translator for performed cultivation actions
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PerformedCultivationActionTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA performed cultivation action object into REST performed cultivation action object
   * 
   * @param performedCultivationAction JPA performed cultivation action object
   * @return REST PerformedCultivationAction
   */
  public PerformedCultivationAction translatePerformedCultivationAction(fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction) {
    if (performedCultivationAction == null) {
      return null;
    }
    
    PerformedCultivationAction result = new PerformedCultivationAction();
    result.setId(performedCultivationAction.getId());
    result.setName(translatelocalizedValue(performedCultivationAction.getName()));

    return result;
  }
  
}
