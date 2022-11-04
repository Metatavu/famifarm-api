package fi.metatavu.famifarm.productionlines;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.CutPackingDAO;
import fi.metatavu.famifarm.persistence.dao.ProductionLineDAO;
import fi.metatavu.famifarm.persistence.model.CutPacking;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.rest.model.Facility;

@ApplicationScoped
public class ProductionLineController {
  
  @Inject
  private ProductionLineDAO productionLineDAO;

  @Inject
  private CutPackingDAO cutPackingDAO;

  /**
   * Creates new production line
   *
   * @param facility facility
   * @param lineNumber lineNumber
   * @param defaultGutterHoleCount default gutter hole count
   * @param userId userId
   * @return created production line
   */
  public ProductionLine createProductionLine(Facility facility, String lineNumber, Integer defaultGutterHoleCount, UUID userId) {
    return productionLineDAO.create(UUID.randomUUID(), facility, lineNumber, defaultGutterHoleCount, userId, userId);
  }

  /**
   * Finds production line by id
   * 
   * @param productionLineId production line id
   * @return production line
   */
  public ProductionLine findProductionLine(UUID productionLineId) {
    return productionLineDAO.findById(productionLineId);
  }

  /**
   * Lists production lines
   *
   * @param facility not null facility
   * @param firstResult first result
   * @param maxResults max results
   * @return list of production lines
   */
  public List<ProductionLine> listProductionLines(Facility facility, Integer firstResult, Integer maxResults) {
    return productionLineDAO.listSortByLineNumber(facility, firstResult, maxResults);
  }

  /**
   * Updates production line
   * 
   * @param productionLine productionLine
   * @param lineNumber lineNumber
   * @param defaultGutterHoleCount default gutter hole count
   * @param lastModifierId lastModifierId
   * @return updated production line
   */
  public ProductionLine updateProductionLine(ProductionLine productionLine, String lineNumber, Integer defaultGutterHoleCount, UUID lastModifierId) {
    productionLineDAO.updateLineNumber(productionLine, lineNumber, lastModifierId);
    productionLineDAO.updateDefaultGutterHoleCount(productionLine, defaultGutterHoleCount, lastModifierId);
    return productionLine;
  }

  /**
   * Deletes production line
   * 
   * @param productionLine productionLine to be deleted
   */
  public void deleteProductionLine(ProductionLine productionLine) {
    List<CutPacking> cutPackings = cutPackingDAO.list(null, null, null, null, productionLine, null, null);

    for (CutPacking cutPacking : cutPackings) {
      cutPackingDAO.delete(cutPacking);
    }

    productionLineDAO.delete(productionLine);
  }
}
