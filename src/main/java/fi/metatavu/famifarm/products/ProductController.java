package fi.metatavu.famifarm.products;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.persistence.dao.CampaignProductDAO;
import fi.metatavu.famifarm.persistence.dao.CutPackingDAO;
import fi.metatavu.famifarm.persistence.dao.ProductDAO;
import fi.metatavu.famifarm.persistence.model.*;

@ApplicationScoped
public class ProductController {
  @Inject
  private CampaignProductDAO campaignProductDAO;

  @Inject
  private ProductDAO productDAO;

  @Inject
  private CutPackingDAO cutPackingDAO;

  /**
   * Creates new product
   * 
   * @param name name
   * @param defaultPackageSize defaultPackageSize
   * @param isSubcontractorProduct is subcontractor product
   * @param creatorId creatorId
   * @return created product
   */
  public Product createProduct(LocalizedEntry name, PackageSize defaultPackageSize, boolean isSubcontractorProduct, UUID creatorId) {
    return productDAO.create(UUID.randomUUID(), name, defaultPackageSize, isSubcontractorProduct, creatorId, creatorId);
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
   * @param includeSubcontractorProducts include subcontractor products
   * @return list of products
   */
  public List<Product> listProducts(Integer firstResult, Integer maxResults, Boolean includeSubcontractorProducts) {
    return productDAO.list(firstResult, maxResults, includeSubcontractorProducts);
  }

  /**
   * Updates product
   * 
   * @param product product
   * @param name name
   * @param packageSize new default package size
   * @param isSubcontractorProduct is subcontractor product
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public Product updateProduct(Product product, LocalizedEntry name, PackageSize packageSize, boolean isSubcontractorProduct, UUID lastModifierId) {
    productDAO.updateName(product, name, lastModifierId);
    productDAO.updateDefaultPackageSize(product, packageSize, lastModifierId);
    productDAO.updateIsSubcontractorProduct(product, isSubcontractorProduct, lastModifierId);
    return product;
  }

  /**
   * Deletes product
   * 
   * @param product product to be deleted
   */
  public void deleteProduct(Product product) {
    List<CampaignProduct> campaignProducts = campaignProductDAO.listByProduct(product);

    for (CampaignProduct campaignProduct : campaignProducts) {
      campaignProductDAO.delete(campaignProduct);
    }

    List<CutPacking> cutPackings = cutPackingDAO.list(null, null, product, null, null, null);

    for (CutPacking cutPacking : cutPackings) {
      cutPackingDAO.delete(cutPacking);
    }

    productDAO.delete(product);
  }
}
