package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.products.ProductController;
import fi.metatavu.famifarm.rest.model.Product;

/**
 * Translator for products
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductsTranslator extends AbstractTranslator {

  @Inject
  private ProductController productController;

  /**
   * Translates JPA product object into REST product object
   * 
   * @param product JPA product object
   * @return REST WastageReason
   */
  public Product translateProduct(fi.metatavu.famifarm.persistence.model.Product product) {
    if (product == null) {
      return null;
    }
    
    Product result = new Product();
    result.setId(product.getId());
    result.setName(translatelocalizedValue(product.getName()));
    result.setDefaultPackageSizeIds(productController.listPackageSizesForProduct(product));
    result.setIsSubcontractorProduct(product.isSubcontractorProduct());
    result.setActive(product.isActive());

    return result;
  }
  
}