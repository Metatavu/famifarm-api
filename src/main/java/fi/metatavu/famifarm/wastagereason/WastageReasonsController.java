package fi.metatavu.famifarm.wastagereason;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.WastageReasonDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.WastageReason;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Controller class for wastage reasons
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class WastageReasonsController {

  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private WastageReasonDAO wastageReasonDAO;
  
  /**
   * Creates new wastage reason
   * 
   * @param reason reason
   * @param creatorId creatorId
   * @param facility facility
   * @return created wastage reason
   */
  public WastageReason createWastageReason(LocalizedEntry reason, UUID creatorId, Facility facility) {
    return wastageReasonDAO.create(UUID.randomUUID(), reason, creatorId, creatorId, facility);
  }

  /**
   * Finds wastage reason by id
   * 
   * @param wastageReasonId wastage reason id
   * @return WastageReason
   */
  public WastageReason findWastageReason(UUID wastageReasonId) {
    return wastageReasonDAO.findById(wastageReasonId);
  }

  /**
   * Lists wastage reasons
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @param facility facility
   * @return list of wastage reasons
   */
  public List<WastageReason> listWastageReasons(Integer firstResult, Integer maxResults, Facility facility) {
    return wastageReasonDAO.listAll(firstResult, maxResults, facility);
  }
  
  /**
   * Update wastage reason
   *
   * @param reason reason
   * @param lastModifierId lastModifierId
   * @return updated wastage reason
   */
  public WastageReason updateWastageReason(WastageReason wastageReason, LocalizedEntry reason, UUID lastModifierId) {
    wastageReasonDAO.updateReason(wastageReason, reason, lastModifierId);
    return wastageReason;
  }

  /**
   * Deletes a wastage reason
   * 
   * @param wastageReason wastage reason to be deleted
   */
  public void deleteWastageReason(WastageReason wastageReason) {
    LocalizedEntry reason = wastageReason.getReason();
    wastageReasonDAO.delete(wastageReason);
    localizedValueController.deleteEntry(reason);
  }
  
}
