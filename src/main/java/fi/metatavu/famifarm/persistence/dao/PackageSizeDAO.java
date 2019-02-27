package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackageSize;

/**
 * DAO class for package sizes
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PackageSizeDAO extends AbstractDAO<PackageSize> {

  /**
   * Creates new package size
   *
   * @param id id
   * @param name name
   * @return created seed
   * @param lastModifier modifier
   */
  public PackageSize create(UUID id, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    PackageSize packageSize = new PackageSize();
    packageSize.setId(id);
    packageSize.setName(name);
    packageSize.setCreatorId(creatorId);
    packageSize.setLastModifierId(lastModifierId);
    return persist(packageSize);
  }

  /**
   * Updates name
   *
   * @param packageSize packageSize
   * @param name name
   * @param lastModifier modifier
   * @return updated packageSize
   */
  public PackageSize updateName(PackageSize packageSize, LocalizedEntry name, UUID lastModifierId) {
    packageSize.setLastModifierId(lastModifierId);
    packageSize.setName(name);
    return persist(packageSize);
  }

}
