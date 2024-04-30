package fi.metatavu.famifarm.products;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.*;
import fi.metatavu.famifarm.rest.model.Facility;
import fi.metatavu.famifarm.rest.model.HarvestEventType;
import fi.metatavu.famifarm.persistence.model.*;

@ApplicationScoped
public class ProductController {

  @Inject
  CampaignProductDAO campaignProductDAO;

  @Inject
  ProductDAO productDAO;

  @Inject
  CutPackingDAO cutPackingDAO;

  @Inject
  ProductPackageSizeDAO productPackageSizeDAO;

  @Inject
  ProductAllowedHarvestTypeDAO productAllowedHarvestTypeDAO;

  @Inject
  PackingBasketDAO packingBasketDAO;

  /**
   * Creates new product
   * 
   * @param name name
   * @param packageSizes package sizes
   * @param isSubcontractorProduct is subcontractor product
   * @param isEndProduct is end product
   * @param isRawMaterial is raw material
   * @param salesWeight sales weight
   * @param facility facility
   * @param creatorId creatorId
   * @return created product
   */
  public Product createProduct(LocalizedEntry name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, boolean active, boolean isEndProduct, boolean isRawMaterial, Double salesWeight, Facility facility, UUID creatorId) {
    Product product = productDAO.create(UUID.randomUUID(), name, isSubcontractorProduct, active, isEndProduct, isRawMaterial, salesWeight, facility, creatorId, creatorId);
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
   * @param facility facility
   * @param firstResult first result
   * @param maxResults max results
   * @param includeSubcontractorProducts include subcontractor products
   * @param filterIsEndProduct filter by end products
   * @param filterIsRawMaterial filter by raw materials
   * @return list of products
   */
  public List<Product> listProducts(Facility facility, Integer firstResult, Integer maxResults, Boolean includeSubcontractorProducts, Boolean includeInActiveProducts, Boolean filterIsEndProduct, Boolean filterIsRawMaterial) {
    return productDAO.list(facility, firstResult, maxResults, includeSubcontractorProducts, includeInActiveProducts, filterIsEndProduct, filterIsRawMaterial);
  }

  /**
   * Updates product
   * 
   * @param product product
   * @param name name
   * @param packageSizes new default package sizes
   * @param isSubcontractorProduct is subcontractor product
   * @param isEndProduct is end product
   * @param isRawMaterial is raw material
   * @param salesWeight sales weight
   * @param lastModifierId lastModifierId
   * @return updated package size
   */
  public Product updateProduct(Product product, LocalizedEntry name, List<PackageSize> packageSizes, boolean isSubcontractorProduct, Boolean isActive, Boolean isEndProduct, Boolean isRawMaterial, Double salesWeight, UUID lastModifierId) {
    productDAO.updateName(product, name, lastModifierId);

    if (packageSizes != null) {
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
    productDAO.updateIsEndProduct(product, isEndProduct, lastModifierId);
    productDAO.updateIsRawMaterial(product, isRawMaterial, lastModifierId);
    productDAO.updateSalesWeight(product, salesWeight, lastModifierId);
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

    List<CutPacking> cutPackings = cutPackingDAO.list(null, null, null, product, null, null, null);

    for (CutPacking cutPacking : cutPackings) {
      cutPackingDAO.delete(cutPacking);
    }

    List<PackingBasket> packingBaskets = packingBasketDAO.listByProduct(product);
    for (PackingBasket packingBasket : packingBaskets) {
      packingBasketDAO.delete(packingBasket);
    }

    List<ProductPackageSize> productPackageSizes = productPackageSizeDAO.listByProduct(product);

    for (ProductPackageSize productPackageSize: productPackageSizes) {
      productPackageSizeDAO.delete(productPackageSize);
    }

    productAllowedHarvestTypeDAO
      .listByProduct(product)
      .forEach(productAllowedHarvestTypeDAO::delete);

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
      productPackageSizeIds.add(productPackageSize.getPackageSize().getId());
    }
    return productPackageSizeIds;
  }

  /**
   * Lists allowed harvest types for product
   * 
   * @param product product
   * @return List of allowed harvest types
   */
  public List<ProductAllowedHarvestType> listAllowedHarvestTypes(Product product) {
    return productAllowedHarvestTypeDAO.listByProduct(product);
  }

  /**
   * Creates new allowed harvest type for product
   * 
   * @param type type
   * @param product product
   * @return created allowed harvest type
   */
  public ProductAllowedHarvestType createAllowedHarvestType(HarvestEventType type, Product product) {
    return productAllowedHarvestTypeDAO.create(UUID.randomUUID(), type, product);
  }


  /**
   * Deletes allowed harvest type for product
   * 
   * @param type allowed harvest type to be deleted
   */
  public void deleteProductAllowedHarvestType(ProductAllowedHarvestType type) {
    productAllowedHarvestTypeDAO.delete(type);
  }
}
