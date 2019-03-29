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
   * @param id   id
   * @param name name
   * @param size size
   * @return created seed
   * @param lastModifier modifier
   */
  public PackageSize create(UUID id, LocalizedEntry name, Integer size, UUID creatorId, UUID lastModifierId) {
    PackageSize packageSize = new PackageSize();
    packageSize.setId(id);
    packageSize.setName(name);
    packageSize.setSize(size);
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


  /**
   * Updates size
   *
   * @param packageSize packageSize
   * @param size size
   * @param lastModifier modifier
   * @return updated packageSize
   */
  public PackageSize updateSize(PackageSize packageSize, Integer size, UUID lastModifierId) {
    packageSize.setLastModifierId(lastModifierId);
    packageSize.setSize(size);
    return persist(packageSize);
  }

}
