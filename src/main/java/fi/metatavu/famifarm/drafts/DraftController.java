package fi.metatavu.famifarm.drafts;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.DraftDAO;
import fi.metatavu.famifarm.persistence.model.Draft;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Controller for drafts
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DraftController {

  @Inject
  private DraftDAO draftDAO;
  
  public Draft createDraft(String type, String data, Facility facility, UUID creatorId) {
    return draftDAO.create(UUID.randomUUID(), type, data, facility, creatorId, creatorId);
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
   * @param type      type
   * @param facility  facility
   * @return found draft or null if not found
   */
  public Draft findDraftByCreatorIdAndType(UUID creatorId, String type, Facility facility) {
    List<Draft> drafts = draftDAO.listByCreatorIdAndType(creatorId, type, facility);
    return drafts.isEmpty() ? null : drafts.get(drafts.size() - 1);
  }

  /**
   * deletes drafts by creator id and type
   * 
   * @param creatorId creator id
   * @param type type
   */
  public void deleteDraftsByCreatorIdAndType(UUID creatorId, String type, Facility facility) {
    draftDAO.listByCreatorIdAndType(creatorId, type, facility)
      .stream()
      .forEach(draftDAO::delete);
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
