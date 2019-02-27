package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction;

/**
 * DAO class for performed cultivation action
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PerformedCultivationActionDAO extends AbstractDAO<PerformedCultivationAction> {

  /**
   * Creates new performed cultivation action
   *
   * @param name name
   * @param lastModifier modifier
   * @return created performed cultivation action
   */
  public PerformedCultivationAction create(UUID id, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    PerformedCultivationAction performedCultivationAction = new PerformedCultivationAction();
    performedCultivationAction.setName(name);
    performedCultivationAction.setId(id);
    performedCultivationAction.setCreatorId(creatorId);
    performedCultivationAction.setLastModifierId(lastModifierId);
    return persist(performedCultivationAction);
  }

  /**
   * Updates name
   *
   * @param performedCultivationAction performed cultivation action
   * @param name name
   * @param lastModifier modifier
   * @return updated performed cultivation action
   */
  public PerformedCultivationAction updateName(PerformedCultivationAction performedCultivationAction, LocalizedEntry name, UUID lastModifierId) {
    performedCultivationAction.setLastModifierId(lastModifierId);
    performedCultivationAction.setName(name);
    return persist(performedCultivationAction);
  }

}
