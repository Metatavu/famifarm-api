package fi.metatavu.famifarm.packagesizes;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackageSizeDAO;
import fi.metatavu.famifarm.persistence.model.PackageSize;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 *
 */
public class PackageSizeController {

  @Inject
  private PackageSizeDAO packageSizeDAO;

  /**
   * Creates new package size
   * 
   * @param name name
   * @param userId userId
   * @return created package size
   */
  public PackageSize createPackageSize(String name, UUID userId) {
    return packageSizeDAO.create(UUID.randomUUID(), name, userId, userId);
  }

  /**
   * Finds package size by id
   * 
   * @param packageSizeId packageSizeId
   * @return package size
   */
  public PackageSize findPackageSize(UUID packageSizeId) {
    return packageSizeDAO.findById(packageSizeId);
  }

  /**
   * Lists package sizes
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of package sizes
   */
  public List<PackageSize> listPackageSizes(Integer firstResult, Integer maxResults) {
    return packageSizeDAO.listAll(firstResult, maxResults);
  }

  /**
   * Updates package size
   * 
   * @param packageSize packageSize
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public PackageSize updatePackageSize(PackageSize packageSize, String name, UUID lastModifierId) {
    packageSizeDAO.updateName(packageSize, name, lastModifierId);
    return packageSize;
  }

  /**
   * Deletes package size
   * 
   * @param packageSize packageSize to be deleted
   */
  public void deletePackageSize(PackageSize packageSize) {
    packageSizeDAO.delete(packageSize);
  }

}
