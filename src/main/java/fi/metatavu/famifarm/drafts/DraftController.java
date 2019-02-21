package fi.metatavu.famifarm.drafts;

import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.DraftDAO;
import fi.metatavu.famifarm.persistence.model.Draft;

/**
 * Controller for drafts
 * 
 * @author Antti Leppä
 */
public class DraftController {

  @Inject
  private DraftDAO draftDAO;
  
  public Draft createDraft(String type, String data, UUID creatorId) {
    return draftDAO.create(UUID.randomUUID(), type, data, creatorId, creatorId);
  }
  
  /**
   * Finds a draft by id
   * 
   * @param id id
   * @return found draft or null if not found
   */
  public Draft findDraftById(UUID id) {
    return draftDAO.findById(id);
  }
  
  /**
   * Finds a draft by creator id and type
   * 
   * @param creatorId creator id
   * @param type type
   * @return found draft or null if not found
   */
  public Draft findDraftByCreatorIdAndType(UUID creatorId, String type) {
    return draftDAO.findByCreatorIdAndType(creatorId, type);
  }
  
  /**
   * Deletes a draft
   * 
   * @param draft
   */
  public void deleteDraft(Draft draft) {
    draftDAO.delete(draft);
  }
  
}
