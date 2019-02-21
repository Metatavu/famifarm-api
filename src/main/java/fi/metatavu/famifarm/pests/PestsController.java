package fi.metatavu.famifarm.pests;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.PestDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Pest;

/**
 * Controller class for pests
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PestsController {

  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private PestDAO pestDAO;
  
  /**
   * Creates new pest
   * 
   * @param name name
   * @param creatorId creatorId
   * @return created pest
   */
  public Pest createPest(LocalizedEntry name, UUID creatorId) {
    return pestDAO.create(UUID.randomUUID(), name, creatorId, creatorId);
  }

  /**
   * Finds pest by id
   * 
   * @param pestId pest id
   * @return Pest
   */
  public Pest findPest(UUID pestId) {
    return pestDAO.findById(pestId);
  }

  /**
   * Lists pests
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of pests
   */
  public List<Pest> listPests(Integer firstResult, Integer maxResults) {
    return pestDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Update pest
   *
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated pest
   */
  public Pest updatePest(Pest pest, LocalizedEntry name, UUID lastModifierId) {
    pestDAO.updateName(pest, name, lastModifierId);
    return pest;
  }

  /**
   * Deletes a pest
   * 
   * @param pest pest to be deleted
   */
  public void deletePest(Pest pest) {
    LocalizedEntry name = pest.getName();
    pestDAO.delete(pest);
    localizedValueController.deleteEntry(name);
  }
  
}
