package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Product;

/**
 * DAO class for package sizes
 * 
 * @author Ville Koivukangas
 */
public class ProductDAO extends AbstractDAO<Product> {

  /**
   * Creates new product
   *
   * @param id id
   * @param name name
   * @param defaultPackageSize defaultPackageSize
   * @return created seed
   * @param lastModifier modifier
   */
  public Product create(UUID id, LocalizedEntry name, PackageSize defaultPackageSize, UUID creatorId, UUID lastModifierId) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setDefaultPackageSize(defaultPackageSize);
    product.setCreatorId(creatorId);
    product.setLastModifierId(lastModifierId);
    return persist(product);
  }

  /**
   * Updates name
   *
   * @param product product
   * @param name name
   * @param lastModifier modifier
   * @return updated product
   */
  public Product updateName(Product product, LocalizedEntry name, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setName(name);
    return persist(product);
  }

  /**
   * Updates default package size
   *
   * @param product product
   * @param packageSize packageSize
   * @param lastModifier modifier
   * @return updated product
   */
  public Product updateDefaultPackageSize(Product product, PackageSize packageSize, UUID lastModifierId) {
    product.setLastModifierId(lastModifierId);
    product.setDefaultPackageSize(packageSize);
    return persist(product);
  }

}
