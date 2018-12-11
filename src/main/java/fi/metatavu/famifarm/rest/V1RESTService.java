package fi.metatavu.famifarm.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.rest.api.V1Api;
import fi.metatavu.famifarm.rest.model.Batch;
import fi.metatavu.famifarm.rest.model.Event;
import fi.metatavu.famifarm.rest.model.PackageSize;
import fi.metatavu.famifarm.rest.model.PerformedCultivationAction;
import fi.metatavu.famifarm.rest.model.Product;
import fi.metatavu.famifarm.rest.model.ProductionLine;
import fi.metatavu.famifarm.rest.model.Seed;
import fi.metatavu.famifarm.rest.model.SeedBatch;
import fi.metatavu.famifarm.rest.model.Team;
import fi.metatavu.famifarm.rest.model.WastageReason;
import fi.metatavu.famifarm.rest.translate.SeedsTranslator;
import fi.metatavu.famifarm.seeds.SeedsController;

/**
 * V1 REST Services
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class V1RESTService extends AbstractApi implements V1Api {
  
  @Inject
  private SeedsController seedsController;

  @Inject
  private SeedsTranslator seedsTranslator;

  @Override
  public Response createSeed(Seed body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(seedsTranslator.translateSeed(seedsController.createSeed(name, loggerUserId)));
  }

  @Override
  public Response deleteSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    seedsController.deleteSeed(seed);
    
    return createNoContent();
  }

  @Override
  public Response findSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(seedsTranslator.translateSeed(seed));
  }

  @Override
  public Response listSeeds(Integer firstResult, Integer maxResults) {
    List<Seed> result = seedsController.listSeeds(firstResult, maxResults).stream()
      .map(seedsTranslator::translateSeed)
      .collect(Collectors.toList());
    
    return createOk(result);
  }

  @Override
  public Response updateSeed(Seed body, UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(seedsTranslator.translateSeed(seedsController.updateSeed(seed, name, loggerUserId)));
  }

  @Override
  public Response createBatch(Batch body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createEvent(Event body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createPackageSize(PackageSize body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createPerformedCultivationAction(PerformedCultivationAction body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createProduct(Product body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createProductionLine(ProductionLine body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createSeedBatch(SeedBatch body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createTeam(Team body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createWastageReason(WastageReason body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteBatch(UUID batchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteEvent(UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deletePackageSize(UUID packageSizeId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deletePerformedCultivationAction(UUID performedCultivationActionId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteProduct(UUID productId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteProductionLine(UUID productionLineId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteSeedBatch(UUID seedBatchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteTeam(UUID teamId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteWastageReason(UUID wastageReasonId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findBatch(UUID batchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findEvent(UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findPackageSize(UUID packageSizeId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findPerformedCultivationAction(UUID performedCultivationActionId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findProduct(UUID productId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findProductionLine(UUID productionLineId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findSeedBatch(UUID seedBatchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findTeam(UUID teamId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findWastageReason(UUID wastageReasonId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listBatches(Integer maxResult) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listEvents(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listPackageSizes(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listPerformedCultivationActions(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listProductionLines(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listProducts(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listSeedBatches(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listTeams(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listWastageReasons(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateBatch(Batch body, UUID batchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateEvent(Event body, UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updatePackageSize(PackageSize body, UUID packageSizeId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updatePerformedCultivationAction(PerformedCultivationAction body, UUID performedCultivationActionId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateProduct(Product body, UUID productId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateProductionLine(ProductionLine body, UUID productionLineId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateSeedBatch(SeedBatch body, UUID seedBatchId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateTeam(Team body, UUID teamId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateWastageReason(WastageReason body, UUID wastageReasonId) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
