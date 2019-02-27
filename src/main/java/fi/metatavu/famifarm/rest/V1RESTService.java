package fi.metatavu.famifarm.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.ejb3.annotation.SecurityDomain;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.famifarm.authentication.Roles;
import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.drafts.DraftController;
import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.events.PackingEventController;
import fi.metatavu.famifarm.events.PlantingEventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.events.TableSpreadEventController;
import fi.metatavu.famifarm.events.WastageEventController;
import fi.metatavu.famifarm.packagesizes.PackageSizeController;
import fi.metatavu.famifarm.performedcultivationactions.PerformedCultivationActionsController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PackingEvent;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.pests.PestsController;
import fi.metatavu.famifarm.productionlines.ProductionLineController;
import fi.metatavu.famifarm.products.ProductController;
import fi.metatavu.famifarm.reporting.Report;
import fi.metatavu.famifarm.reporting.ReportController;
import fi.metatavu.famifarm.reporting.ReportType;
import fi.metatavu.famifarm.rest.api.V1Api;
import fi.metatavu.famifarm.rest.model.Batch;
import fi.metatavu.famifarm.rest.model.CellType;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;
import fi.metatavu.famifarm.rest.model.Draft;
import fi.metatavu.famifarm.rest.model.Event;
import fi.metatavu.famifarm.rest.model.EventType;
import fi.metatavu.famifarm.rest.model.HarvestEventData;
import fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum;
import fi.metatavu.famifarm.rest.model.PackageSize;
import fi.metatavu.famifarm.rest.model.PackingEventData;
import fi.metatavu.famifarm.rest.model.PerformedCultivationAction;
import fi.metatavu.famifarm.rest.model.Pest;
import fi.metatavu.famifarm.rest.model.PlantingEventData;
import fi.metatavu.famifarm.rest.model.Product;
import fi.metatavu.famifarm.rest.model.ProductionLine;
import fi.metatavu.famifarm.rest.model.Seed;
import fi.metatavu.famifarm.rest.model.SeedBatch;
import fi.metatavu.famifarm.rest.model.SowingEventData;
import fi.metatavu.famifarm.rest.model.TableSpreadEventData;
import fi.metatavu.famifarm.rest.model.Team;
import fi.metatavu.famifarm.rest.model.WastageEventData;
import fi.metatavu.famifarm.rest.model.WastageReason;
import fi.metatavu.famifarm.rest.translate.BatchTranslator;
import fi.metatavu.famifarm.rest.translate.CultivationObservationEventTranslator;
import fi.metatavu.famifarm.rest.translate.DraftTranslator;
import fi.metatavu.famifarm.rest.translate.HarvestEventTranslator;
import fi.metatavu.famifarm.rest.translate.PackageSizeTranslator;
import fi.metatavu.famifarm.rest.translate.PackingEventTranslator;
import fi.metatavu.famifarm.rest.translate.PerformedCultivationActionTranslator;
import fi.metatavu.famifarm.rest.translate.PestsTranslator;
import fi.metatavu.famifarm.rest.translate.PlantingEventTranslator;
import fi.metatavu.famifarm.rest.translate.ProductionLineTranslator;
import fi.metatavu.famifarm.rest.translate.ProductsTranslator;
import fi.metatavu.famifarm.rest.translate.SeedBatchTranslator;
import fi.metatavu.famifarm.rest.translate.SeedsTranslator;
import fi.metatavu.famifarm.rest.translate.SowingEventTranslator;
import fi.metatavu.famifarm.rest.translate.TableSpreadEventTranslator;
import fi.metatavu.famifarm.rest.translate.TeamsTranslator;
import fi.metatavu.famifarm.rest.translate.WastageEventTranslator;
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
  
  private static final String FAILED_TO_READ_EVENT_DATA = "Failed to read event data";

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
  private PestsController pestsController;

  @Inject 
  private PestsTranslator pestsTranslator;
  
  @Inject 
  private ProductionLineTranslator productionLineTranslator;

  @Inject 
  private SowingEventTranslator sowingEventTranslator;

  @Inject 
  private SowingEventController sowingEventController;

  @Inject
  private TableSpreadEventController tableSpreadEventController;

  @Inject
  private TableSpreadEventTranslator tableSpreadEventTranslator;
  
  @Inject 
  private EventController eventController;

  @Inject
  private CultivationObservationEventController cultivationObservationEventController;
  
  @Inject 
  private CultivationObservationEventTranslator cultivationObservationEventTranslator;

  @Inject 
  private HarvestEventController harvestEventController;
  
  @Inject 
  private HarvestEventTranslator harvestEventTranslator;

  @Inject 
  private PlantingEventController plantingEventController;
  
  @Inject 
  private PlantingEventTranslator plantingEventTranslator;

  @Inject 
  private PackingEventController packingEventController;

  @Inject 
  private PackingEventTranslator packingEventTranslator;
  
  @Inject
  private WastageEventController wastageEventController;

  @Inject
  private WastageEventTranslator wastageEventTranslator;

  @Inject
  private ReportController reportController;

  @Inject
  private DraftController draftController;

  @Inject
  private DraftTranslator draftTranslator;
  
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
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response createEvent(Event body) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(body.getBatchId());
    if (batch == null) {
      return createBadRequest("Could not find specified batch");
    }
    
    OffsetDateTime startTime = body.getStartTime();
    OffsetDateTime endTime = body.getEndTime();
    String additionalInformation = body.getAdditionalInformation();
    
    switch (body.getType()) {
      case SOWING:
        return createSowingEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case TABLE_SPREAD:
        return createTableSpreadEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case CULTIVATION_OBSERVATION:
        return createCultivationObservationEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case HARVEST:
        return createHarvestEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case PLANTING:
        return createPlantingEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case WASTAGE:
        return createWastageEvent(batch, startTime, endTime, additionalInformation, body.getData());
      case PACKING:
        return createPackingEvent(batch, startTime, endTime, additionalInformation, body.getData());
      default:
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }
  
  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createPackageSize(PackageSize body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
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
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(body.getDefaultPackageSizeId());
    if (packageSize == null) {
      createNotFound("Package size not found");
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    
    return createOk(productsTranslator.translateProduct(productController.createProduct(name, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createProductionLine(ProductionLine body) {
    String lineNumber = body.getLineNumber();
    UUID defaultTeamId = body.getDefaultTeamId();
    fi.metatavu.famifarm.persistence.model.Team defaultTeam = null;
    
    if (defaultTeamId != null) {
      defaultTeam = teamsController.findTeam(defaultTeamId);
      if (defaultTeam == null) {
        return createBadRequest(String.format("Invalid default team id %s", defaultTeamId));
      }
    }
    
    return createOk(productionLineTranslator.translateProductionLine(productionLineController.createProductionLine(lineNumber, defaultTeam, getLoggerUserId())));
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
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deleteEvent(UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }
    
    switch (event.getType()) {
      case CULTIVATION_OBSERVATION:
        cultivationObservationEventController.deleteCultivationActionEvent((CultivationObservationEvent) event);
      break;
      case SOWING:
        sowingEventController.deleteSowingEvent((SowingEvent) event);
      break;
      case TABLE_SPREAD:
        tableSpreadEventController.deleteTableSpreadEvent((TableSpreadEvent) event);
      break;
      case HARVEST:
        harvestEventController.deleteHarvestEvent((HarvestEvent) event);
      break;
      case PLANTING:
        plantingEventController.deletePlantingEvent((PlantingEvent) event);
      break;
      case WASTAGE:
        wastageEventController.deleteWastageEvent((WastageEvent) event);
      break;
      case PACKING:
        packingEventController.deletePackingEvent((PackingEvent) event);
      break;
      default:
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }
    
    return createNoContent();
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
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findEvent(UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }
    
    return createOk(translateEvent(event));
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
  public Response listBatches(String statusParam, UUID productId, Integer firstResult, Integer maxResult, String createdBefore, String createdAfter) {
    BatchListStatus status = null;
    
    if (StringUtils.isNotEmpty(statusParam)) {
      status = EnumUtils.getEnum(BatchListStatus.class, statusParam);
      if (status == null) {
        return createBadRequest(String.format("Unsupported status %s", statusParam));
      }
    }
    
    fi.metatavu.famifarm.persistence.model.Product product = productId != null ? productController.findProduct(productId) : null;
    if (productId != null && product == null) {
      return createBadRequest("Invalid product id");
    }
    
    Integer remainingUnitsGreaterThan = null;
    Integer remainingUnitsLessThan = null;
    Integer remainingUnitsEqual = null;
    
    if (status != null) {
      switch (status) {
        case CLOSED:
          remainingUnitsEqual = 0;
        break;
        case NEGATIVE:
          remainingUnitsLessThan = 0;
        break;
        case OPEN:
          remainingUnitsGreaterThan = 0;
        break;
      }
    }
    
    List<fi.metatavu.famifarm.persistence.model.Batch> batches = batchController.listBatches(product, 
      remainingUnitsGreaterThan, 
      remainingUnitsLessThan, 
      remainingUnitsEqual, 
      parseTime(createdBefore), 
      parseTime(createdAfter), 
      firstResult, 
      maxResult);
    
    List<Batch> result = batches.stream()
        .map(batchTranslator::translateBatch)
        .collect(Collectors.toList());
      
    return createOk(result);
  }

  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listEvents(Integer firstResult, Integer maxResults, UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchId != null ? batchController.findBatch(batchId) : null;
    List<Event> result = eventController.listEvents(batch, firstResult, maxResults).stream()
      .map(this::translateEvent)
      .collect(Collectors.toList());
    
    return createOk(result);
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
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response updateEvent(Event body, UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }
    
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(body.getBatchId());
    if (batch == null) {
      return createBadRequest("Could not find specified batch");
    }

    OffsetDateTime startTime = body.getStartTime();
    OffsetDateTime endTime = body.getEndTime();
    String additionalInformation = body.getAdditionalInformation();
    
    switch (body.getType()) {
      case SOWING:
        return updateSowingEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case TABLE_SPREAD:
        return updateTableSpreadEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case CULTIVATION_OBSERVATION:
        return updateCultivationObservationEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case HARVEST:
        return updateHarvestEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case PLANTING:
        return updatePlantingEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case WASTAGE:
        return updateWastageEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      case PACKING:
        return updatePackingEvent(event, batch, startTime, endTime, additionalInformation, body.getData());
      default:
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updatePackageSize(PackageSize body, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(packageSizeId);
    if (packageSize == null) {
      return createNotFound("Package size not found");
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
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
    
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(body.getDefaultPackageSizeId());
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
    
    String lineNumber = body.getLineNumber();
    UUID defaultTeamId = body.getDefaultTeamId();
    fi.metatavu.famifarm.persistence.model.Team defaultTeam = null;
    
    if (defaultTeamId != null) {
      defaultTeam = teamsController.findTeam(defaultTeamId);
      if (defaultTeam == null) {
        return createBadRequest(String.format("Invalid default team id %s", defaultTeamId));
      }
    }
    
    return createOk(productionLineTranslator.translateProductionLine(productionLineController.updateProductionLine(productionLine, lineNumber, defaultTeam, getLoggerUserId())));
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
  
  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response getReport(String typeParam, String fromTime, String toTime) {
    ReportType reportType = EnumUtils.getEnum(ReportType.class, typeParam);
    
    if (reportType == null) {
      return createBadRequest(String.format("Invalid report type %s", typeParam));
    }
    
    Map<String, String> parameters = new HashMap<>();
    
    if (fromTime != null) {
      parameters.put("fromTime", fromTime);
    }
    
    if (toTime != null) {
      parameters.put("toTime", toTime);
    }
    
    Report report = reportController.getReport(reportType);
    if (report == null) {
      return createInternalServerError("Failed construct report");
    }
    
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      report.createReport(output, getLocale(), parameters);
      return streamResponse(output.toByteArray(), report.getContentType());
    } catch (IOException e) {
      return createInternalServerError("Failed to stream report");
    } catch (Exception e) {
      return createInternalServerError("Failed to create report");
    }
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response createDraft(Draft body) {
    return createOk(draftTranslator.translateDraft(draftController.createDraft(body.getType(), body.getData(), getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response deleteDraft(UUID draftId) {
    fi.metatavu.famifarm.persistence.model.Draft draft = draftController.findDraftById(draftId);
    if (draft == null) {
      return createNotFound("Not found");
    }
    
    if (!getLoggerUserId().equals(draft.getCreatorId())) {
      return createForbidden("Forbidden");
    }
    
    draftController.deleteDraft(draft);
    return createNoContent();
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER, Roles.WORKER})
  public Response listDrafts(UUID userId, String type) {
    fi.metatavu.famifarm.persistence.model.Draft draft = draftController.findDraftByCreatorIdAndType(userId, type);
    if (draft != null) {
      return createOk(Arrays.asList(draft));
    }
    
    return createOk(Collections.emptyList());
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response createPest(Pest body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(pestsTranslator.translatePest(pestsController.createPest(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response deletePest(UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    pestsController.deletePest(pest);
    
    return createNoContent();
  }
  
  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response findPest(UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(pestsTranslator.translatePest(pest));
  }
  
  @Override
  @RolesAllowed({Roles.WORKER, Roles.ADMIN, Roles.MANAGER})
  public Response listPests(Integer firstResult, Integer maxResults) {
    List<Pest> result = pestsController.listPests(firstResult, maxResults).stream()
        .map(pestsTranslator::translatePest)
        .collect(Collectors.toList());
      
      return createOk(result);
  }
  
  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  public Response updatePest(Pest body, UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(pestsTranslator.translatePest(pestsController.updatePest(pest, name, loggerUserId)));
  }

  /**
   * Parse time
   * 
   * @param timeString
   * @return time
   */
  private OffsetDateTime parseTime(String timeString) {
    if (StringUtils.isEmpty(timeString)) {
      return null;
    }
    
    return OffsetDateTime.parse(timeString);
  }
  
  private Event translateEvent(fi.metatavu.famifarm.persistence.model.Event event) {
    switch (event.getType()) {
      case SOWING:
        return sowingEventTranslator.translateEvent((SowingEvent) event);
      case TABLE_SPREAD:
        return tableSpreadEventTranslator.translateEvent((TableSpreadEvent) event);
      case CULTIVATION_OBSERVATION:
        return cultivationObservationEventTranslator.translateEvent((CultivationObservationEvent) event);
      case HARVEST:
        return harvestEventTranslator.translateEvent((HarvestEvent) event);
      case PLANTING:
        return plantingEventTranslator.translateEvent((PlantingEvent) event);
      case PACKING:
        return packingEventTranslator.translateEvent((PackingEvent) event);
      case WASTAGE:
        return wastageEventTranslator.translateEvent((WastageEvent) event);
      default:
      break;
    }
  
    return null;
  }

  /**
   * Creates new sowing event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createSowingEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    SowingEventData eventData;
    
    try {
      eventData = readEventData(SowingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(eventData.getSeedBatchId());
    CellType cellType = eventData.getCellType();
    Integer amount = eventData.getAmount();
    
    SowingEvent event = sowingEventController.createSowingEvent(batch, startTime, endTime, productionLine, seedBatch, cellType, amount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    
    return createOk(sowingEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates sowing event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateSowingEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    SowingEventData eventData;
    
    try {
      eventData = readEventData(SowingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(eventData.getSeedBatchId());
    CellType cellType = eventData.getCellType();
    Integer amount = eventData.getAmount();    
    SowingEvent updatedEvent = sowingEventController.updateSowingEvent((SowingEvent) event, batch, startTime, endTime, productionLine, seedBatch, cellType, amount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    
    return createOk(sowingEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new tableSpread event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createTableSpreadEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    TableSpreadEventData eventData;
    
    try {
      eventData = readEventData(TableSpreadEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }
    
    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    String location = eventData.getLocation();
    Integer tableCount = eventData.getTableCount();
    TableSpreadEvent event = tableSpreadEventController.createTableSpreadEvent(batch, startTime, endTime, tableCount, location, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(tableSpreadEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }
  
  /**
   * Updates table spread event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateTableSpreadEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    TableSpreadEventData eventData;
    
    try {
      eventData = readEventData(TableSpreadEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    String location = eventData.getLocation();
    Integer tableCount = eventData.getTableCount();
    
    TableSpreadEvent updatedEvent = tableSpreadEventController.updateTableSpreadEvent((TableSpreadEvent) event, batch, startTime, endTime, tableCount, location, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(tableSpreadEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new harvest event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createCultivationObservationEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    CultivationObservationEventData eventData;
    
    try {
      eventData = readEventData(CultivationObservationEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }
    
    UUID creatorId = getLoggerUserId();
    Double weight = eventData.getWeight();
    Double luminance = eventData.getLuminance();
    
    List<fi.metatavu.famifarm.persistence.model.PerformedCultivationAction> actions = new ArrayList<>();
    Response actionsResponse = translatePerformedCultivationActions(eventData.getPerformedActionIds(), actions);
    if (actionsResponse != null) {
      return actionsResponse;
    }
    
    List<fi.metatavu.famifarm.persistence.model.Pest> pests = new ArrayList<>();
    Response pestsResponse = translatePests(eventData.getPestIds(), pests);
    if (pestsResponse != null) {
      return pestsResponse;
    }
    
    CultivationObservationEvent event = cultivationObservationEventController.createCultivationActionEvent(batch, startTime, endTime, weight, luminance, pests, actions, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
 
    return createOk(cultivationObservationEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }
  
  /**
   * Updates cultivationObservation event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateCultivationObservationEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    CultivationObservationEventData eventData;
    
    try {
      eventData = readEventData(CultivationObservationEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    Double weight = eventData.getWeight();
    Double luminance = eventData.getLuminance();
    
    List<fi.metatavu.famifarm.persistence.model.PerformedCultivationAction> actions = new ArrayList<>();
    Response actionsResponse = translatePerformedCultivationActions(eventData.getPerformedActionIds(), actions);
    if (actionsResponse != null) {
      return actionsResponse;
    }
    
    List<fi.metatavu.famifarm.persistence.model.Pest> pests = new ArrayList<>();
    Response pestsResponse = translatePests(eventData.getPestIds(), pests);
    if (pestsResponse != null) {
      return pestsResponse;
    }
 
    CultivationObservationEvent updatedEvent = cultivationObservationEventController.updateCultivationActionEvent((CultivationObservationEvent) event, batch, startTime, endTime, weight, luminance, pests, actions, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(cultivationObservationEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new harvest event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createHarvestEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    HarvestEventData eventData;
    
    try {
      eventData = readEventData(HarvestEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }
    
    UUID creatorId = getLoggerUserId();
    
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = null;
    if (eventData.getProductionLineId() != null) {
      productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
      if (productionLine == null) {
        return createBadRequest("Invalid production line");
      }
    }
    
    fi.metatavu.famifarm.persistence.model.Team team = null;
    if (eventData.getTeamId() != null) {
      team = teamsController.findTeam(eventData.getTeamId());
      if (team == null) {
        return createBadRequest("Invalid team");        
      }
    }
    
    TypeEnum harvestType = eventData.getType();
    HarvestEvent event = harvestEventController.createHarvestEvent(batch, startTime, endTime, team, harvestType, productionLine, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(harvestEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }
  
  /**
   * Updates harvest event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateHarvestEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    HarvestEventData eventData;
    
    try {
      eventData = readEventData(HarvestEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = null;
    if (eventData.getProductionLineId() != null) {
      productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
      if (productionLine == null) {
        return createBadRequest("Invalid production line");
      }
    }
    
    fi.metatavu.famifarm.persistence.model.Team team = null;
    if (eventData.getTeamId() != null) {
      team = teamsController.findTeam(eventData.getTeamId());
      if (team == null) {
        return createBadRequest("Invalid team");        
      }
    }
    
    TypeEnum harvestType = eventData.getType();
    HarvestEvent updatedEvent = harvestEventController.updateHarvestEvent((HarvestEvent) event, batch, startTime, endTime, team, harvestType, productionLine, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(harvestEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new planting event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createPlantingEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    PlantingEventData eventData;
    
    try {
      eventData = readEventData(PlantingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
    Integer gutterSize = eventData.getGutterSize();
    Integer gutterCount = eventData.getGutterCount();
    Integer cellCount = eventData.getCellCount();
    Integer workerCount = eventData.getWorkerCount();
    
    PlantingEvent event = plantingEventController.createPlantingEvent(batch, startTime, endTime, productionLine, gutterSize, gutterCount, cellCount, workerCount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    
    return createOk(plantingEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }
  
  /**
   * Updates planting event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updatePlantingEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    PlantingEventData eventData;
    
    try {
      eventData = readEventData(PlantingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
    Integer gutterSize = eventData.getGutterSize();
    Integer gutterCount = eventData.getGutterCount();
    Integer cellCount = eventData.getCellCount();
    Integer workerCount = eventData.getWorkerCount();
    
    PlantingEvent updatedEvent = plantingEventController.updatePlantingEvent((PlantingEvent) event, batch, startTime, endTime, productionLine, gutterSize, gutterCount, cellCount, workerCount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(plantingEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new packing event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createPackingEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    PackingEventData eventData;
    
    try {
      eventData = readEventData(PackingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = eventData.getPackageSizeId() != null ? packageSizeController.findPackageSize(eventData.getPackageSizeId()) : null;
    Integer packedAmount = eventData.getPackedAmount();

    PackingEvent event = packingEventController.createPackingEvent(batch, startTime, endTime, packageSize, packedAmount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    
    return createOk(packingEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }
  
  /**
   * Updates packing event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updatePackingEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    PackingEventData eventData;
    
    try {
      eventData = readEventData(PackingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = eventData.getPackageSizeId() != null ? packageSizeController.findPackageSize(eventData.getPackageSizeId()) : null;
    Integer packedAmount = eventData.getPackedAmount();
    
    PackingEvent updatedEvent = packingEventController.updatePackingEvent((PackingEvent) event, batch, startTime, endTime, packageSize, packedAmount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(packingEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }
  
  /**
   * Translates list of performed cultivation action ids into entities
   * 
   * @param performedActionIds ids
   * @param result result list
   * @return error response or null if translation succeeds
   */
  private Response translatePerformedCultivationActions(List<UUID> performedActionIds, List<fi.metatavu.famifarm.persistence.model.PerformedCultivationAction> result) {
    if (performedActionIds == null) {
      return null;
    }
    
    for (UUID performedActionId : performedActionIds) {
      fi.metatavu.famifarm.persistence.model.PerformedCultivationAction cultivationAction = performedCultivationActionsController.findPerformedCultivationAction(performedActionId);
      if (cultivationAction == null) {
        return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid performted action id %s", performedActionId)).build();
      }
      
      result.add(cultivationAction);
    }
    
    return null;
  }
  
  /**
   * Translates list of pests ids into entities
   * 
   * @param pestIds ids
   * @param result result list
   * @return error response or null if translation succeeds
   */
  private Response translatePests(List<UUID> pestIds, List<fi.metatavu.famifarm.persistence.model.Pest> result) {
    if (pestIds == null) {
      return null;
    }
    
    for (UUID pestId : pestIds) {
      fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
      if (pest == null) {
        return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid pest id %s", pestId)).build();
      }
      
      result.add(pest);
    }
    
    return null;
  }

  /**
   * Creates new wastage event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventData event data
   * @return response
   */
  private Response createWastageEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    WastageEventData eventData;

    try {
      eventData = readEventData(WastageEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID creatorId = getLoggerUserId();
    Integer amount = eventData.getAmount();
    EventType phase = eventData.getPhase();
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController.findWastageReason(eventData.getReasonId());
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = eventData.getProductionLineId() != null ? productionLineController.findProductionLine(eventData.getProductionLineId()) : null;
    
    if (eventData.getProductionLineId() != null && productionLine == null) {
      return createBadRequest("Invalid production line");
    }
    
    WastageEvent event = wastageEventController.createWastageEvent(batch, startTime, endTime, amount, wastageReason, phase, additionalInformation, productionLine, creatorId);
    batchController.updateRemainingUnits(batch);
    
    return createOk(wastageEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates wastage event
   * 
   * @param event event
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateWastageEvent(fi.metatavu.famifarm.persistence.model.Event event, fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    WastageEventData eventData;

    try {
      eventData = readEventData(WastageEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }

    UUID lastModifierId = getLoggerUserId();
    Integer amount = eventData.getAmount();
    EventType phase = eventData.getPhase();
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController.findWastageReason(eventData.getReasonId());

    WastageEvent updatedEvent = wastageEventController.updateWastageEvent((WastageEvent) event, batch, startTime, endTime, amount, wastageReason, phase, additionalInformation, lastModifierId);
    batchController.updateRemainingUnits(batch);

    return createOk(wastageEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }
  
  /**
   * Updates batche's active event
   * 
   * @param event event that triggered the change
   * @return same event
   */
  private <E extends fi.metatavu.famifarm.persistence.model.Event> E updateBatchActiveEvent(E event) {
    fi.metatavu.famifarm.persistence.model.Event activeEvent = eventController.findLastEventByBatch(event.getBatch());
    batchController.updateBatchActiveEvent(event.getBatch(), activeEvent);
    return event;
  }
  
  private <D> D readEventData(Class<D> targetClass, Object object) throws IOException {
    if (object == null) {
      return null;
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(objectMapper.writeValueAsBytes(object), targetClass);
  }
  
}