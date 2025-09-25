package fi.metatavu.famifarm.filmbatches;

import fi.metatavu.famifarm.persistence.dao.PackagingFilmBatchDAO;
import fi.metatavu.famifarm.persistence.dao.PackingDAO;
import fi.metatavu.famifarm.persistence.model.PackagingFilmBatch;
import fi.metatavu.famifarm.rest.model.Facility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Controller for packaging film batches
 *
 * @author Katja Danilova
 */
@ApplicationScoped
public class PackagingFilmBatchController {

  @Inject
  public PackagingFilmBatchDAO packagingFilmBatchDAO;

  @Inject
  public PackingDAO packingDAO;

  /**
   * Creates new packaging film batch
   *
   * @param facility facility
   * @param packagingFilmBatch packaging film batch
   * @param loggerUserId logger user id
   * @return created packaging film batch
   */
  public PackagingFilmBatch create(Facility facility, fi.metatavu.famifarm.rest.model.PackagingFilmBatch packagingFilmBatch, UUID loggerUserId) {
    return packagingFilmBatchDAO.create(
      UUID.randomUUID(),
      facility,
      packagingFilmBatch.getName(),
      packagingFilmBatch.getActive(),
      packagingFilmBatch.getTime(),
      loggerUserId,
      loggerUserId
    );
  }

  /**
   * Finds packaging film batch by id
   *
   * @param packagingFilmBatchId packaging film batch id
   * @return found packaging film batch or null if not found
   */
  public PackagingFilmBatch findById(UUID packagingFilmBatchId) {
    return packagingFilmBatchDAO.findById(packagingFilmBatchId);
  }

  /**
   * Deletes packaging film batch
   *
   * @param packagingFilmBatch packaging film batch to delete
   */
  public void delete(PackagingFilmBatch packagingFilmBatch) {
    var packings = packingDAO.listByPackagingFilmBatch(packagingFilmBatch);
    for (var packing : packings) {
      packingDAO.updatePackagingFilmBatch(packing, null, packing.getLastModifierId());
    }
    packagingFilmBatchDAO.delete(packagingFilmBatch);
  }

  /**
   * Lists packaging film batches
   *
   * @param firstResult index of the first result
   * @param maxResults maximum number of results
   * @param facility facility
   * @param includePassive if true, passive batches are included
   * @return list of packaging film batches
   */
  public List<PackagingFilmBatch> list(Integer firstResult, Integer maxResults, Facility facility, Boolean includePassive) {
    Boolean activeFilter = (includePassive == null || !includePassive) ? true : null;
    return packagingFilmBatchDAO.list(firstResult, maxResults, facility, activeFilter);
  }

  /**
   * Updates existing packaging film batch
   *
   * @param existingPackagingFilmBatch existing packaging film batch
   * @param packagingFilmBatch new packaging film batch data
   * @param loggerUserId logger user id
   * @return updated packaging film batch
   */
  public PackagingFilmBatch update(PackagingFilmBatch existingPackagingFilmBatch, fi.metatavu.famifarm.rest.model.PackagingFilmBatch packagingFilmBatch, UUID loggerUserId) {
    PackagingFilmBatch updatedPackagingFilmBatch = packagingFilmBatchDAO.updateName(existingPackagingFilmBatch, packagingFilmBatch.getName(), loggerUserId);
    updatedPackagingFilmBatch = packagingFilmBatchDAO.updateIsActive(updatedPackagingFilmBatch, packagingFilmBatch.getActive(), loggerUserId);
    updatedPackagingFilmBatch = packagingFilmBatchDAO.updateArrivalTime(updatedPackagingFilmBatch, packagingFilmBatch.getTime(), loggerUserId);
    return updatedPackagingFilmBatch;
  }
}
