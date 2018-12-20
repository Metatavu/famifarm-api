package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.WastageReason;

/**
 * DAO class for wastage reason
 * 
 * @author Ville Koivukangas
 */
public class WastageReasonDAO extends AbstractDAO<WastageReason> {

  /**
   * Creates new wastage reason
   *
   * @param reason reason
   * @param lastModifier modifier
   * @return created wastage reason
   */
  public WastageReason create(UUID id, LocalizedEntry reason, UUID creatorId, UUID lastModifierId) {
    WastageReason wastageReason = new WastageReason();
    wastageReason.setReason(reason);
    wastageReason.setId(id);
    wastageReason.setCreatorId(creatorId);
    wastageReason.setLastModifierId(lastModifierId);
    return persist(wastageReason);
  }

  /**
   * Updates reason
   *
   * @param wastageReason wastage reason
   * @param reason reason
   * @param lastModifier modifier
   * @return updated wastage reason
   */
  public WastageReason updateName(WastageReason wastageReason, LocalizedEntry reason, UUID lastModifierId) {
    wastageReason.setLastModifierId(lastModifierId);
    wastageReason.setReason(reason);
    return persist(wastageReason);
  }

}
