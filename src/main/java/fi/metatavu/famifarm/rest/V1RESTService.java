package fi.metatavu.famifarm.rest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import fi.metatavu.famifarm.authentication.Roles;
import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.packagesizes.PackageSizeController;
import fi.metatavu.famifarm.performedcultivationactions.PerformedCultivationActionsController;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.productionlines.ProductionLineController;
import fi.metatavu.famifarm.products.ProductController;
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
import fi.metatavu.famifarm.rest.translate.BatchTranslator;
import fi.metatavu.famifarm.rest.translate.PackageSizeTranslator;
import fi.metatavu.famifarm.rest.translate.PerformedCultivationActionTranslator;
import fi.metatavu.famifarm.rest.translate.ProductionLineTranslator;
import fi.metatavu.famifarm.rest.translate.ProductsTranslator;
import fi.metatavu.famifarm.rest.translate.SeedBatchTranslator;
import fi.metatavu.famifarm.rest.translate.SeedsTranslator;
import fi.metatavu.famifarm.rest.translate.TeamsTranslator;
import fi.metatavu.famifarm.rest.translate.WastageReasonsTranslator;
import fi.metatavu.famifarm.seedbatches.SeedBatchesController;
import fi.metatavu.famifarm.seeds.SeedsController;
import fi.metatavu.famifarm.teams.TeamsController;
import fi.metatavu.famifarm.wastagereason.WastageReasonsController;

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
@SecurityDomain("keycloak")
public class V1RESTService extends AbstractApi implements V1Api {
  
  @Inject
  private SeedsController seedsController;

  @Inject
  private SeedsTranslator seedsTranslator;
  
  @Inject
  private TeamsController teamsController;

  @Inject
  private TeamsTranslator teamsTranslator;
  
  @Inject
  private WastageReasonsController wastageReasonsController;

  @Inject
  private WastageReasonsTranslator wastageReasonsTranslator;
  
  @Inject
  private SeedBatchesController seedBatchController;
  
  @Inject 
  private SeedBatchTranslator seedBatchesTranslator;
  
  @Inject 
  private PackageSizeTranslator packageSizeTranslator;
  
  @Inject 
  private PackageSizeController packageSizeController;
  
  @Inject 
  private ProductController productController;
  
  @Inject 
  private ProductsTranslator productsTranslator;
  
  @Inject 
  private BatchTranslator batchTranslator;
  
  @Inject 
  private BatchController batchController;
  
  @Inject 
  private ProductionLineController productionLineController;
  
  @Inject 
  private PerformedCultivationActionTranslator performedCultivationActionTranslator;
  
  @Inject 
  private PerformedCultivationActionsController performedCultivationActionsController;
  
  @Inject 
  private ProductionLineTranslator productionLineTranslator;

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createSeed(Seed body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(seedsTranslator.translateSeed(seedsController.createSeed(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    seedsController.deleteSeed(seed);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response findSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(seedsTranslator.translateSeed(seed));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response listSeeds(Integer firstResult, Integer maxResults) {
    List<Seed> result = seedsController.listSeeds(firstResult, maxResults).stream()
      .map(seedsTranslator::translateSeed)
      .collect(Collectors.toList());
    
    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
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
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createBatch(Batch body) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(batchTranslator.translateBatch(batchController.createBatch(product, getLoggerUserId())));
  }

  @Override
  public Response createEvent(Event body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createPackageSize(PackageSize body) {
    String name = body.getName();
    return createOk(packageSizeTranslator.translatePackageSize(packageSizeController.createPackageSize(name, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createPerformedCultivationAction(PerformedCultivationAction body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationActionsController.createPerformedCultivationAction(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createProduct(Product body) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(body.getDefaultPackageSize());
    if (packageSize == null) {
      createNotFound("Package size not found");
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    
    return createOk(productsTranslator.translateProduct(productController.createProduct(name, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createProductionLine(ProductionLine body) {
    Integer lineNumber = body.getLineNumber();
    
    return createOk(productionLineTranslator.translateProductionLine(productionLineController.createProductionLine(lineNumber, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createSeedBatch(SeedBatch body) {
    String code = body.getCode();
    UUID seedId = body.getSeedId();
    OffsetDateTime time = body.getTime();

    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedBatchesTranslator.translateSeedBatch(seedBatchController.createSeedBatch(code, seed, time, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createTeam(Team body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(teamsTranslator.translateTeam(teamsController.createTeam(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createWastageReason(WastageReason body) {
    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(wastageReasonsTranslator.translateWastageReason(wastageReasonsController.createWastageReason(reason, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteBatch(UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    batchController.deleteBatch(batch);
    
    return createNoContent();
  }

  @Override
  public Response deleteEvent(UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deletePackageSize(UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(packageSizeId);
    if (packageSize == null) {
      createNotFound("Package size not found");
    }
    
    packageSizeController.deletePackageSize(packageSize);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deletePerformedCultivationAction(UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController.findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    performedCultivationActionsController.deletePerformedCultivationAction(performedCultivationAction);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteProduct(UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }
    
    productController.deleteProduct(product);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteProductionLine(UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    productionLineController.deleteProductionLine(productionLine);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteSeedBatch(UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    seedBatchController.deleteSeedBatch(seedBatch);

    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteTeam(UUID teamId) {
    fi.metatavu.famifarm.persistence.model.Team team = teamsController.findTeam(teamId);
    if (team == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    teamsController.deleteTeam(team);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteWastageReason(UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController.findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    wastageReasonsController.deleteWastageReason(wastageReason);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findBatch(UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(batchTranslator.translateBatch(batch));
  }

  @Override
  public Response findEvent(UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findPackageSize(UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(packageSizeId);
    if (packageSize == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(packageSizeTranslator.translatePackageSize(packageSize));
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findPerformedCultivationAction(UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController.findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationAction));
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findProduct(UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(productsTranslator.translateProduct(product));
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findProductionLine(UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(productionLineTranslator.translateProductionLine(productionLine));
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findSeedBatch(UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedBatchesTranslator.translateSeedBatch(seedBatchController.findSeedBatch(seedBatchId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response findTeam(UUID teamId) {
    fi.metatavu.famifarm.persistence.model.Team team = teamsController.findTeam(teamId);
    if (team == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(teamsTranslator.translateTeam(team));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response findWastageReason(UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController.findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(wastageReasonsTranslator.translateWastageReason(wastageReason));
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listBatches(Integer firstResult, Integer maxResult) {
    List<Batch> result = batchController.listBatches(firstResult, maxResult).stream()
        .map(batchTranslator::translateBatch)
        .collect(Collectors.toList());
      
    return createOk(result);
  }

  @Override
  public Response listEvents(Integer firstResult, Integer maxResults) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listPackageSizes(Integer firstResult, Integer maxResults) {
    List<PackageSize> result = packageSizeController.listPackageSizes(firstResult, maxResults).stream()
        .map(packageSizeTranslator::translatePackageSize)
        .collect(Collectors.toList());
      
    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listPerformedCultivationActions(Integer firstResult, Integer maxResults) {
    List<PerformedCultivationAction> result = performedCultivationActionsController.listPerformedCultivationActions(firstResult, maxResults).stream()
        .map(performedCultivationActionTranslator::translatePerformedCultivationAction)
        .collect(Collectors.toList());
      
      return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listProductionLines(Integer firstResult, Integer maxResults) {
    List<ProductionLine> result = productionLineController.listProductionLines(firstResult, maxResults).stream()
        .map(productionLineTranslator::translateProductionLine)
        .collect(Collectors.toList());
      
    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listProducts(Integer firstResult, Integer maxResults) {
    List<Product> result = productController.listProducts(firstResult, maxResults).stream()
        .map(productsTranslator::translateProduct)
        .collect(Collectors.toList());
      
    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response listSeedBatches(Integer firstResult, Integer maxResults) {
    List<SeedBatch> result = seedBatchController.listSeedBatches(firstResult, maxResults).stream()
        .map(seedBatchesTranslator::translateSeedBatch).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response listTeams(Integer firstResult, Integer maxResults) {
    List<Team> result = teamsController.listTeams(firstResult, maxResults).stream()
        .map(teamsTranslator::translateTeam)
        .collect(Collectors.toList());
      
      return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response listWastageReasons(Integer firstResult, Integer maxResults) {
    List<WastageReason> result = wastageReasonsController.listWastageReasons(firstResult, maxResults).stream()
        .map(wastageReasonsTranslator::translateWastageReason)
        .collect(Collectors.toList());
      
      return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateBatch(Batch body, UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(batchController.updateBatch(batch, product, getLoggerUserId()));
  }

  @Override
  public Response updateEvent(Event body, UUID eventId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updatePackageSize(PackageSize body, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(packageSizeId);
    if (packageSize == null) {
      return createNotFound("Package size not found");
    }
    
    String name = body.getName();
    
    return createOk(packageSizeTranslator.translatePackageSize(packageSizeController.updatePackageSize(packageSize, name, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updatePerformedCultivationAction(PerformedCultivationAction body, UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController.findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationActionsController.updatePerformedCultivationAction(performedCultivationAction, name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateProduct(Product body, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }
    
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(body.getDefaultPackageSize());
    if (packageSize == null) {
      createNotFound("Package size not found");
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    
    return createOk(productsTranslator.translateProduct(productController.updateProduct(product, name, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateProductionLine(ProductionLine body, UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    Integer lineNumber = body.getLineNumber();
    
    return createOk(productionLineTranslator.translateProductionLine(productionLineController.updateProductionLine(productionLine, lineNumber, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateSeedBatch(SeedBatch body, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      createNotFound(NOT_FOUND_MESSAGE);
    }

    String code = body.getCode();
    OffsetDateTime time = body.getTime();
    UUID seedId = body.getSeedId();
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);

    return createOk(seedBatchController.updateSeedBatch(seedBatch, code, seed, time, getLoggerUserId()));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateTeam(Team body, UUID teamId) {
    fi.metatavu.famifarm.persistence.model.Team team = teamsController.findTeam(teamId);
    if (team == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(teamsTranslator.translateTeam(teamsController.updateTeam(team, name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updateWastageReason(WastageReason body, UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController.findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(wastageReasonsTranslator.translateWastageReason(wastageReasonsController.updateWastageReason(wastageReason, reason, loggerUserId)));
  }
  
}
