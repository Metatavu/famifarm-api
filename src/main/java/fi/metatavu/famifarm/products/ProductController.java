package fi.metatavu.famifarm.products;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.CampaignProductDAO;
import fi.metatavu.famifarm.persistence.dao.CutPackingDAO;
import fi.metatavu.famifarm.persistence.dao.ProductDAO;
import fi.metatavu.famifarm.persistence.dao.ProductPackageSizeDAO;
import fi.metatavu.famifarm.persistence.model.*;

@ApplicationScoped
public class ProductController {
  @Inject
  private CampaignProductDAO campaignProductDAO;

  @Inject
  private ProductDAO productDAO;

  @Inject
  private CutPackingDAO cutPackingDAO;

  @Inject
  private ProductPackageSizeDAO productPackageSizeDAO;

  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSizes package sizes
   * @param isSubcontractorProduct is subcontractor product
   * @param creatorId creatorId
   * @return created product
   */
  public Product createProduct(LocalizedEntry name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, boolean active, UUID creatorId) {
    Product product = productDAO.create(UUID.randomUUID(), name, isSubcontractorProduct, active, creatorId, creatorId);
    packageSizes.forEach(packageSize -> productPackageSizeDAO.create(UUID.randomUUID(), product, packageSize));
    return product;
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
  public List<Product> listProducts(Integer firstResult, Integer maxResults, Boolean includeSubcontractorProducts, Boolean includeInActiveProducts) {
    return productDAO.list(firstResult, maxResults, includeSubcontractorProducts, includeInActiveProducts);
  }

  /**
   * Updates product
   * 
   * @param product product
   * @param name name
   * @param packageSizes new default package sizes
   * @param isSubcontractorProduct is subcontractor product
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public Product updateProduct(Product product, LocalizedEntry name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, Boolean isActive, UUID lastModifierId) {
    productDAO.updateName(product, name, lastModifierId);

    if (packageSizes != null && !packageSizes.isEmpty()) {
      List<ProductPackageSize> existingPackageSizes = productPackageSizeDAO.listByProduct(product);
      for (ProductPackageSize productPackageSize : existingPackageSizes) {
        productPackageSizeDAO.delete(productPackageSize);
      }

      for (PackageSize packageSize : packageSizes) {
        productPackageSizeDAO.create(UUID.randomUUID(), product, packageSize);
      }
    }

    productDAO.updateIsSubcontractorProduct(product, isSubcontractorProduct, lastModifierId);
    productDAO.updateIsActive(product, isActive, lastModifierId);
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

    List<ProductPackageSize> productPackageSizes = productPackageSizeDAO.listByProduct(product);

    for (ProductPackageSize productPackageSize: productPackageSizes) {
      productPackageSizeDAO.delete(productPackageSize);
    }

    productDAO.delete(product);
  }

  /**
   * Lists product package size ids by product
   *
   * @param product product
   * @return product package sizes ids
   */
  public List<UUID> listPackageSizesForProduct(Product product) {
    List<ProductPackageSize> productPackageSizes = productPackageSizeDAO.listByProduct(product);

    List<UUID> productPackageSizeIds = new ArrayList<>(productPackageSizes.size());

    for (ProductPackageSize productPackageSize : productPackageSizes) {
      productPackageSizeIds.add(productPackageSize.getId());
    }
    return productPackageSizeIds;
  }
}
