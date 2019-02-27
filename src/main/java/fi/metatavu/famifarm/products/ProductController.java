package fi.metatavu.famifarm.products;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.ProductDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackageSize;
import fi.metatavu.famifarm.persistence.model.Product;

@ApplicationScoped
public class ProductController {
  
  @Inject
  private ProductDAO productDAO;

  /**
   * Creates new product
   * 
   * @param name name
   * @param defaultPackageSize defaultPackageSize
   * @param creatorId creatorId
   * @return created product
   */
  public Product createProduct(LocalizedEntry name, PackageSize defaultPackageSize, UUID creatorId) {
    return productDAO.create(UUID.randomUUID(), name, defaultPackageSize, creatorId, creatorId);
  }

  /**
   * Finds product by id
   * 
   * @param productId productId
   * @return product
   */
  public Product findProduct(UUID productId) {
    return productDAO.findById(productId);
  }

  /**
   * Lists products
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of products
   */
  public List<Product> listProducts(Integer firstResult, Integer maxResults) {
    return productDAO.listAll(firstResult, maxResults);
  }

  /**
   * Updates product
   * 
   * @param product product
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public Product updateProduct(Product product, LocalizedEntry name, PackageSize packageSize, UUID lastModifierId) {
    productDAO.updateName(product, name, lastModifierId);
    productDAO.updateDefaultPackageSize(product, packageSize, lastModifierId);
    return product;
  }

  /**
   * Deletes product
   * 
   * @param product product to be deleted
   */
  public void deleteProduct(Product product) {
    productDAO.delete(product);
  }
}
