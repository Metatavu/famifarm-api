package fi.metatavu.famifarm.performedcultivationactions;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.PerformedCultivationActionDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PerformedCultivationAction;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Controller class for performed cultivation actions
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PerformedCultivationActionsController {

  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private PerformedCultivationActionDAO performedCultivationActionDAO;
  
  /**
   * Creates new performed cultivation action
   * 
   * @param name name
   * @param facility facility
   * @param creatorId creatorId
   * @return created performed cultivation action
   */
  public PerformedCultivationAction createPerformedCultivationAction(LocalizedEntry name, Facility facility, UUID creatorId) {
    return performedCultivationActionDAO.create(UUID.randomUUID(), name, facility, creatorId, creatorId);
  }

  /**
   * Finds performed cultivation action by id
   * 
   * @param performedCultivationActionId performed cultivation action id
   * @return PerformedCultivationAction
   */
  public PerformedCultivationAction findPerformedCultivationAction(UUID performedCultivationActionId) {
    return performedCultivationActionDAO.findById(performedCultivationActionId);
  }

  /**
   * Lists performed cultivation actions
   *
   * @param facility
   * @param firstResult first result
   * @param maxResults  max results
   * @return list of performed cultivation actions
   */
  public List<PerformedCultivationAction> listPerformedCultivationActions(Facility facility, Integer firstResult, Integer maxResults) {
    return performedCultivationActionDAO.list(facility, firstResult, maxResults);
  }
  
  /**
   * Update performed cultivation action
   *
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated performed cultivation action
   */
  public PerformedCultivationAction updatePerformedCultivationAction(PerformedCultivationAction performedCultivationAction, LocalizedEntry name, UUID lastModifierId) {
    performedCultivationActionDAO.updateName(performedCultivationAction, name, lastModifierId);
    return performedCultivationAction;
  }

  /**
   * Deletes a performed cultivation action
   * 
   * @param performedCultivationAction performed cultivation action to be deleted
   */
  public void deletePerformedCultivationAction(PerformedCultivationAction performedCultivationAction) {
    LocalizedEntry name = performedCultivationAction.getName();
    performedCultivationActionDAO.delete(performedCultivationAction);
    localizedValueController.deleteEntry(name);
  }
  
}
