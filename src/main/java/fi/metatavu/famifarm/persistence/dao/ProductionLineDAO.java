package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.Team;

/**
 * DAO class for seed batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductionLineDAO extends AbstractDAO<ProductionLine> {

  /**
   * Creates new seed production line
   *
   * @param id id
   * @param lineNumber lineNumber
   * @param defaultTeam default team
   * @param defaultGutterHoleCount default gutter hole count
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created production line
   */
  public ProductionLine create(UUID id, String lineNumber, Team defaultTeam, Integer defaultGutterHoleCount, UUID creatorId, UUID lastModifierId) {
    ProductionLine productionLine = new ProductionLine();
    productionLine.setId(id);
    productionLine.setLineNumber(lineNumber);
    productionLine.setDefaultTeam(defaultTeam);
    productionLine.setDefaultGutterHoleCount(defaultGutterHoleCount);
    productionLine.setCreatorId(creatorId);
    productionLine.setLastModifierId(lastModifierId);
    return persist(productionLine);
  }

  /**
   * Updates line number
   *
   * @param productionLine productionLine
   * @param lineNumber lineNumber
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateLineNumber(ProductionLine productionLine, String lineNumber, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setLineNumber(lineNumber);
    return persist(productionLine);
  }

  /**
   * Updates default team
   *
   * @param productionLine productionLine
   * @param defaultTeam team
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateDefaultTeam(ProductionLine productionLine, Team defaultTeam, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setDefaultTeam(defaultTeam);
    return persist(productionLine);
  }

  /**
   * Updates defaultGutterHoleCount
   *
   * @param productionLine productionLine
   * @param defaultGutterHoleCount defaultGutterHoleCount
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateDefaultGutterHoleCount(ProductionLine productionLine, Integer defaultGutterHoleCount, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setDefaultGutterHoleCount(defaultGutterHoleCount);
    return persist(productionLine);
  }
}
