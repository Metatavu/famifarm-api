package fi.metatavu.famifarm.packagesizes;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.PackageSizeDAO;
import fi.metatavu.famifarm.persistence.dao.ProductPackageSizeDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.rest.model.Facility;

/**
 * Controller for seed batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PackageSizeController {

  @Inject
  private ProductPackageSizeDAO productPackageSizeDAO;

  @Inject
  private PackageSizeDAO packageSizeDAO;

  /**
   * Creates new package size
   * 
   * @param name name
   * @param size size
   * @param facility facility
   * @param userId userId
   * @return created package size
   */
  public PackageSize createPackageSize(LocalizedEntry name, Integer size, Facility facility, UUID userId) {
    return packageSizeDAO.create(UUID.randomUUID(), name, size, facility, userId, userId);
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
   * @param facility facility
   * @param firstResult first result
   * @param maxResults  max results
   * @return list of package sizes
   */
  public List<PackageSize> listPackageSizes(Facility facility, Integer firstResult, Integer maxResults) {
    return packageSizeDAO.list(facility, firstResult, maxResults);
  }

  /**
   * Updates package size
   * 
   * @param packageSize packageSize
   * @param name name
   * @param size size
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public PackageSize updatePackageSize(PackageSize packageSize, LocalizedEntry name, Integer size, UUID lastModifierId) {
    packageSizeDAO.updateName(packageSize, name, lastModifierId);
    packageSizeDAO.updateSize(packageSize, size, lastModifierId);
    return packageSize;
  }

  /**
   * Deletes package size
   * 
   * @param packageSize packageSize to be deleted
   */
  public void deletePackageSize(PackageSize packageSize) {
    productPackageSizeDAO.listByPackageSize(packageSize).forEach(
            productPackageSize -> productPackageSizeDAO.delete(productPackageSize)
    );
    packageSizeDAO.delete(packageSize);
  }

}
