package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Draft;

/**
 * Translator for drafts
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DraftTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA draft object into REST draft object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public Draft translateDraft(fi.metatavu.famifarm.persistence.model.Draft draft) {
    if (draft == null) {
      return null;
    }
    
    Draft result = new Draft();
    result.setId(draft.getId());
    result.setCreatedAt(draft.getCreatedAt());
    result.setData(draft.getData());
    result.setType(draft.getType());
    result.setUserId(draft.getCreatorId());
    
    return result;
  }
  
}
