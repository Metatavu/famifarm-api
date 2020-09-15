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
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.printing.PrintingController;
import fi.metatavu.famifarm.rest.model.*;
import fi.metatavu.famifarm.rest.translate.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.famifarm.authentication.Roles;
import fi.metatavu.famifarm.batches.BatchController;
import fi.metatavu.famifarm.drafts.DraftController;
import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.events.PlantingEventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.events.TableSpreadEventController;
import fi.metatavu.famifarm.events.WastageEventController;
import fi.metatavu.famifarm.packagesizes.PackageSizeController;
import fi.metatavu.famifarm.packing.PackingController;
import fi.metatavu.famifarm.performedcultivationactions.PerformedCultivationActionsController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.persistence.model.TableSpreadEvent;
import fi.metatavu.famifarm.persistence.model.WastageEvent;
import fi.metatavu.famifarm.pests.PestsController;
import fi.metatavu.famifarm.productionlines.ProductionLineController;
import fi.metatavu.famifarm.products.ProductController;
import fi.metatavu.famifarm.reporting.Report;
import fi.metatavu.famifarm.reporting.ReportController;
import fi.metatavu.famifarm.reporting.ReportException;
import fi.metatavu.famifarm.reporting.ReportType;
import fi.metatavu.famifarm.rest.api.V1Api;
import fi.metatavu.famifarm.rest.model.HarvestEventData.TypeEnum;
import fi.metatavu.famifarm.seedbatches.SeedBatchesController;
import fi.metatavu.famifarm.seeds.SeedsController;
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
  private CampaignTranslator campaignTranslator;

  @Inject
  private CampaignController campaignController;

  @Inject
  private Logger logger;

  @Inject
  private SeedsController seedsController;

  @Inject
  private SeedsTranslator seedsTranslator;

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
  private WastageEventController wastageEventController;

  @Inject
  private WastageEventTranslator wastageEventTranslator;

  @Inject
  private ReportController reportController;

  @Inject
  private DraftController draftController;

  @Inject
  private DraftTranslator draftTranslator;
  
  @Inject
  private PackingController packingController;
  
  @Inject
  private PackingTranslator packingTranslator;

  @Inject
  private PrintingController printingController;

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response createPacking(Packing body) {
    PackingType packingType = body.getType();

    UUID productId = body.getProductId();
    fi.metatavu.famifarm.persistence.model.Product product = null;
    if (productId != null) {
      product = productController.findProduct(body.getProductId());

      if (product == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    } else {
      if (packingType == PackingType.BASIC) {
        return createBadRequest("When type is BASIC, a productId is required.");
      }
    }

    UUID campaignId = body.getCampaignId();
    fi.metatavu.famifarm.persistence.model.Campaign campaign = null;
    if (campaignId != null) {
      campaign = campaignController.find(campaignId);
      if (campaign == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    } else {
      if (packingType == PackingType.CAMPAIGN) {
        return createBadRequest("When type is CAMPAIGN, a campaignId is required.");
      }
    }

    UUID packageSizeId = body.getPackageSizeId();
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = null;
    if (packageSizeId != null) {
      packageSize = packageSizeController.findPackageSize(packageSizeId);
    }

    return createOk(packingTranslator.translate(packingController.create(getLoggerUserId(), product, packageSize, body.getPackedCount(), body.getState(), body.getTime(), campaign, packingType)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response deletePacking(UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);
    
    if (packing == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    packingController.deletePacking(packing);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findPacking(UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);
    
    if (packing == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(packingTranslator.translate(packing));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listPackings(Integer firstResult, Integer maxResults, UUID productId, PackingState status,
      String createdBefore, String createdAfter) {
    fi.metatavu.famifarm.persistence.model.Product product = null;
    if (productId != null) {
      product = productController.findProduct(productId);
      
      if (product == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }
    
    OffsetDateTime createdBeforeTime = null;
    if (createdBefore != null) {
      createdBeforeTime = OffsetDateTime.parse(createdBefore);
    }
    
    OffsetDateTime createdAfterTime = null;
    if (createdAfter != null) {
      createdAfterTime = OffsetDateTime.parse(createdAfter);
    }

    return createOk(packingController.listPackings(firstResult, maxResults, product, status, createdBeforeTime, createdAfterTime).stream().map(packing -> packingTranslator.translate(packing)).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response updatePacking(Packing body, UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);

    if (packing == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    PackingType packingType = body.getType();
    UUID productId = body.getProductId();
    fi.metatavu.famifarm.persistence.model.Product product = null;
    if (productId != null) {
      product = productController.findProduct(body.getProductId());

      if (product == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    } else {
      if (packingType == PackingType.BASIC) {
        return createBadRequest("When type is BASIC, a productId is required.");
      }
    }

    UUID campaignId = body.getCampaignId();
    fi.metatavu.famifarm.persistence.model.Campaign campaign = null;
    if (campaignId != null) {
      campaign = campaignController.find(campaignId);
      if (campaign == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    } else {
      if (packingType == PackingType.CAMPAIGN) {
        return createBadRequest("When type is CAMPAIGN, a campaignId is required.");
      }
    }

    UUID packageSizeId = body.getPackageSizeId();
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = null;
    if (packageSizeId != null) {
      packageSize = packageSizeController.findPackageSize(packageSizeId);
    }
    return createOk(packingTranslator.translate(packingController.updatePacking(packing, packageSize, body.getState(), body.getPackedCount(), product, body.getTime(), campaign, packingType, getLoggerUserId())));
  }
  
  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createSeed(Seed body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(seedsTranslator.translateSeed(seedsController.createSeed(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    seedsController.deleteSeed(seed);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findSeed(UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedsTranslator.translateSeed(seed));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listSeeds(Integer firstResult, Integer maxResults) {
    List<Seed> result = seedsController.listSeeds(firstResult, maxResults).stream().map(seedsTranslator::translateSeed)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response createBatch(Batch body) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(batchTranslator.translateBatch(batchController.createBatch(product, body.getPhase(), getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createCampaign(@Valid Campaign campaign) {
    HashMap<fi.metatavu.famifarm.persistence.model.Product, Integer> campaignProductsToCreate = new HashMap<>();
    List<CampaignProducts> restCampaignProducts = campaign.getProducts();

    for (CampaignProducts campaignProduct : restCampaignProducts) {
      fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(campaignProduct.getProductId());
      if (product == null) {
        return createNotFound(String.format("Campaign product %s not found!", campaignProduct.getProductId()));
      }

      campaignProductsToCreate.put(product, campaignProduct.getCount());
    }

    fi.metatavu.famifarm.persistence.model.Campaign createdCampaign = campaignController.create(campaign.getName(), campaignProductsToCreate, getLoggerUserId());
    return createOk(campaignTranslator.translate(createdCampaign));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
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
    default:
      return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createPackageSize(PackageSize body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    Integer size = body.getSize();
    return createOk(packageSizeTranslator
        .translatePackageSize(packageSizeController.createPackageSize(name, size, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createPerformedCultivationAction(PerformedCultivationAction body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(
        performedCultivationActionsController.createPerformedCultivationAction(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createProduct(Product body) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(body.getDefaultPackageSizeId());
    if (packageSize == null) {
      createNotFound("Package size not found");
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());

    return createOk(
        productsTranslator.translateProduct(productController.createProduct(name, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createProductionLine(ProductionLine body) {
    String lineNumber = body.getLineNumber();
    Integer defaultGutterHoleCount = body.getDefaultGutterHoleCount();

    return createOk(productionLineTranslator.translateProductionLine(productionLineController
        .createProductionLine(lineNumber, defaultGutterHoleCount, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createSeedBatch(SeedBatch body) {
    String code = body.getCode();
    UUID seedId = body.getSeedId();
    OffsetDateTime time = body.getTime();

    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedBatchesTranslator
        .translateSeedBatch(seedBatchController.createSeedBatch(code, seed, time, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createWastageReason(WastageReason body) {
    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();

    return createOk(wastageReasonsTranslator
        .translateWastageReason(wastageReasonsController.createWastageReason(reason, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteBatch(UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    batchController.deleteBatch(batch);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteCampaign(UUID campaignId) {
    fi.metatavu.famifarm.persistence.model.Campaign campaign = campaignController.find(campaignId);
    if (campaign == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    campaignController.delete(campaign);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response deleteEvent(UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    fi.metatavu.famifarm.persistence.model.Batch batch = event.getBatch();
    eventController.deleteEvent(event);
    if (event.getType().equals(EventType.SOWING)) {
      batchController.refreshCreationDate(batch);
    }
    updateBatchActiveEvent(batch);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deletePackageSize(UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);
    if (packageSize == null) {
      createNotFound("Package size not found");
    }

    packageSizeController.deletePackageSize(packageSize);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deletePerformedCultivationAction(UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    performedCultivationActionsController.deletePerformedCultivationAction(performedCultivationAction);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteProduct(UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }
    
    for (fi.metatavu.famifarm.persistence.model.Packing packing : packingController.listPackings(null, null, null, null, null, null)) {
      if (packing.getProduct().getId() == productId) {
        return createBadRequest("Product can not be deleted, because it is linked to packings");
      }
    }
    
    productController.deleteProduct(product);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteProductionLine(UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    productionLineController.deleteProductionLine(productionLine);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteSeedBatch(UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    seedBatchController.deleteSeedBatch(seedBatch);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deleteWastageReason(UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    wastageReasonsController.deleteWastageReason(wastageReason);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findBatch(UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(batchTranslator.translateBatch(batch));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response findCampaign(UUID campaignId) {
    fi.metatavu.famifarm.persistence.model.Campaign campaign = campaignController.find(campaignId);

    if (campaign == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    return createOk(campaignTranslator.translate(campaign));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findEvent(UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    return createOk(translateEvent(event));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPackageSize(UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);
    if (packageSize == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(packageSizeTranslator.translatePackageSize(packageSize));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPerformedCultivationAction(UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(
        performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationAction));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findProduct(UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(productsTranslator.translateProduct(product));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findProductionLine(UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(productionLineTranslator.translateProductionLine(productionLine));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findSeedBatch(UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedBatchesTranslator.translateSeedBatch(seedBatchController.findSeedBatch(seedBatchId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findWastageReason(UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(wastageReasonsTranslator.translateWastageReason(wastageReason));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listBatches(String statusParam, BatchPhase phase, UUID productId, Integer firstResult, Integer maxResult,
      String createdBefore, String createdAfter) {
    BatchListStatus status = null;

    if (StringUtils.isNotEmpty(statusParam)) {
      status = EnumUtils.getEnum(BatchListStatus.class, statusParam);
      if (status == null) {
        return createBadRequest(String.format("Unsupported status %s", statusParam));
      }
    }

    fi.metatavu.famifarm.persistence.model.Product product = productId != null
        ? productController.findProduct(productId)
        : null;
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

    List<fi.metatavu.famifarm.persistence.model.Batch> batches = batchController.listBatches(product, phase,
        remainingUnitsGreaterThan, remainingUnitsLessThan, remainingUnitsEqual, parseTime(createdBefore),
        parseTime(createdAfter), firstResult, maxResult);

    List<Batch> result = batches.stream().map(batchTranslator::translateBatch).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listCampaigns() {
    List<Campaign> translatedCampaigns = campaignController.list().stream().map(campaignTranslator::translate).collect(Collectors.toList());
    return createOk(translatedCampaigns);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listEvents(Integer firstResult, Integer maxResults, UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchId != null ? batchController.findBatch(batchId) : null;
    List<Event> result = eventController.listEvents(batch, firstResult, maxResults).stream().map(this::translateEvent)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPackageSizes(Integer firstResult, Integer maxResults) {
    List<PackageSize> result = packageSizeController.listPackageSizes(firstResult, maxResults).stream()
        .map(packageSizeTranslator::translatePackageSize).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPerformedCultivationActions(Integer firstResult, Integer maxResults) {
    List<PerformedCultivationAction> result = performedCultivationActionsController
        .listPerformedCultivationActions(firstResult, maxResults).stream()
        .map(performedCultivationActionTranslator::translatePerformedCultivationAction).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listProductionLines(Integer firstResult, Integer maxResults) {
    List<ProductionLine> result = productionLineController.listProductionLines(firstResult, maxResults).stream()
        .map(productionLineTranslator::translateProductionLine).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listProducts(Integer firstResult, Integer maxResults) {
    List<Product> result = productController.listProducts(firstResult, maxResults).stream()
        .map(productsTranslator::translateProduct).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listSeedBatches(Integer firstResult, Integer maxResults, Boolean isPassive) {
    Boolean active = null;
    if (isPassive != null) {
      active = !isPassive;
    }
    List<SeedBatch> result = seedBatchController.listSeedBatches(firstResult, maxResults, active).stream()
        .map(seedBatchesTranslator::translateSeedBatch).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listWastageReasons(Integer firstResult, Integer maxResults) {
    List<WastageReason> result = wastageReasonsController.listWastageReasons(firstResult, maxResults).stream()
        .map(wastageReasonsTranslator::translateWastageReason).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response print(@Valid PrintData printData, String printerId) {
    UUID packingId = printData.getPackingId();
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);
    if (packing == null) {
      return createBadRequest("Packing not found!");
    }

    if (packing.getType() == PackingType.CAMPAIGN) {
      return createBadRequest("Printing campaign packings not supported currently");
    }

    try {
      List<Printer> printers = printingController.getPrinters();
      Printer correctPrinter = null;
      for (Printer printer : printers) {
        if (printer.getId().equals(printerId)) {
          correctPrinter = printer;
        }
      }

      if (correctPrinter == null) {
        return createNotFound("Printer not found!");
      }

      int status = printingController.printQrCode(printerId, packing, getLocale());
      if (status > 299) {
        return Response.status(status).build();
      } else {
        return Response.status(200).build();
      }
    } catch (Exception e) {
      return createInternalServerError(e.getMessage());
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response updateBatch(Batch body, UUID batchId) {
    fi.metatavu.famifarm.persistence.model.Batch batch = batchController.findBatch(batchId);
    if (batch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    fi.metatavu.famifarm.persistence.model.Batch updatedBatch = batchController.updateBatch(batch, product, body.getPhase(), getLoggerUserId());
    return createOk(batchTranslator.translateBatch(updatedBatch));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updateCampaign(@Valid Campaign campaign, UUID campaignId) {
    HashMap<fi.metatavu.famifarm.persistence.model.Product, Integer> campaignProductsToCreate = new HashMap<>();
    List<CampaignProducts> restCampaignProducts = campaign.getProducts();

    for (CampaignProducts campaignProduct : restCampaignProducts) {
      fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(campaignProduct.getProductId());
      if (product == null) {
        return createNotFound(String.format("Campaign product %s not found!", campaignProduct.getProductId()));
      }

      campaignProductsToCreate.put(product, campaignProduct.getCount());
    }

    fi.metatavu.famifarm.persistence.model.Campaign campaignToUpdate = campaignController.find(campaignId);
    if (campaignToUpdate == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    fi.metatavu.famifarm.persistence.model.Campaign updatedCampaign = campaignController.update(campaignToUpdate, campaign.getName(), campaignProductsToCreate, getLoggerUserId());
    return createOk(campaignTranslator.translate(updatedCampaign));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
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
    default:
      return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updatePackageSize(PackageSize body, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);
    if (packageSize == null) {
      return createNotFound("Package size not found");
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    Integer size = body.getSize();
    return createOk(packageSizeTranslator
        .translatePackageSize(packageSizeController.updatePackageSize(packageSize, name, size, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updatePerformedCultivationAction(PerformedCultivationAction body, UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(
        performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationActionsController
            .updatePerformedCultivationAction(performedCultivationAction, name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updateProduct(Product body, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }

    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(body.getDefaultPackageSizeId());
    if (packageSize == null) {
      createNotFound("Package size not found");
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());

    return createOk(productsTranslator
        .translateProduct(productController.updateProduct(product, name, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updateProductionLine(ProductionLine body, UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);
    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    Integer defaultGutterHoleCount = body.getDefaultGutterHoleCount();
    String lineNumber = body.getLineNumber();

    return createOk(productionLineTranslator.translateProductionLine(productionLineController
        .updateProductionLine(productionLine, lineNumber, defaultGutterHoleCount, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updateSeedBatch(SeedBatch body, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      createNotFound(NOT_FOUND_MESSAGE);
    }

    String code = body.getCode();
    OffsetDateTime time = body.getTime();
    UUID seedId = body.getSeedId();
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    boolean active = body.isisActive() != null ? body.isisActive() : Boolean.FALSE;

    return createOk(seedBatchController.updateSeedBatch(seedBatch, code, seed, time, active, getLoggerUserId()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response updateWastageReason(WastageReason body, UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);
    if (wastageReason == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();

    return createOk(wastageReasonsTranslator
        .translateWastageReason(wastageReasonsController.updateWastageReason(wastageReason, reason, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
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
    } catch (ReportException e) {
      this.logger.error("Failed to create report", e);
      return createInternalServerError("Failed create report");
    } catch (IOException e) {
      this.logger.error("Failed to stream report", e);
      return createInternalServerError("Failed to stream report");
    } catch (Exception e) {
      this.logger.error("Failed to output report", e);
      return createInternalServerError("Failed to output report");
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response createDraft(Draft body) {
    UUID creatorId = getLoggerUserId();

    //TODO: Add unique index to database to prevent duplicates created by ui errors
    draftController.deleteDraftsByCreatorIdAndType(creatorId, body.getType());
    return createOk(
        draftTranslator.translateDraft(draftController.createDraft(body.getType(), body.getData(), creatorId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listDrafts(UUID userId, String type) {
    fi.metatavu.famifarm.persistence.model.Draft draft = draftController.findDraftByCreatorIdAndType(userId, type);
    if (draft != null) {
      return createOk(Arrays.asList(draft));
    }

    return createOk(Collections.emptyList());
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response createPest(Pest body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(pestsTranslator.translatePest(pestsController.createPest(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response deletePest(UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    pestsController.deletePest(pest);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPest(UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(pestsTranslator.translatePest(pest));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPests(Integer firstResult, Integer maxResults) {
    List<Pest> result = pestsController.listPests(firstResult, maxResults).stream().map(pestsTranslator::translatePest)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPrinters() {
    try {
      return createOk(printingController.getPrinters());
    } catch (Exception e) {
      return createInternalServerError(e.getMessage());
    }

  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
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
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createSowingEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime,
      OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
    SowingEventData eventData;

    try {
      eventData = readEventData(SowingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }
    
    List<UUID> seedBatchIds = eventData.getSeedBatchIds();
    if (seedBatchIds == null) {
      seedBatchIds = Collections.emptyList();
    }
    
    List<fi.metatavu.famifarm.persistence.model.SeedBatch> seedBatches = new ArrayList<>(seedBatchIds.size());
    for (UUID seedBatchId : seedBatchIds) {
      fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
      if (seedBatch == null) {
        return createBadRequest(String.format("Invalid seed batch id %s", seedBatchId));
      }
      
      seedBatches.add(seedBatch);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController.findProductionLine(eventData.getProductionLineId());
    PotType potType = eventData.getPotType();
    Integer amount = eventData.getAmount();

    SowingEvent event = sowingEventController.createSowingEvent(batch, startTime, endTime, productionLine, seedBatches,
        potType, amount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    batchController.refreshCreationDate(batch);

    return createOk(sowingEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates sowing event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateSowingEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
    SowingEventData eventData;

    try {
      eventData = readEventData(SowingEventData.class, eventDataObject);
    } catch (IOException e) {
      return createInternalServerError(e.getMessage());
    }

    if (eventData == null) {
      return createInternalServerError(FAILED_TO_READ_EVENT_DATA);
    }
    
    List<UUID> seedBatchIds = eventData.getSeedBatchIds();
    if (seedBatchIds == null) {
      seedBatchIds = Collections.emptyList();
    }
    
    List<fi.metatavu.famifarm.persistence.model.SeedBatch> seedBatches = new ArrayList<>(seedBatchIds.size());
    for (UUID seedBatchId : seedBatchIds) {
      fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
      if (seedBatch == null) {
        return createBadRequest(String.format("Invalid seed batch id %s", seedBatchId));
      }
      
      seedBatches.add(seedBatch);
    }

    UUID creatorId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(eventData.getProductionLineId());
    PotType potType = eventData.getPotType();
    Integer amount = eventData.getAmount();
    SowingEvent updatedEvent = sowingEventController.updateSowingEvent((SowingEvent) event, batch, startTime, endTime,
        productionLine, seedBatches, potType, amount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);
    batchController.refreshCreationDate(batch);

    return createOk(sowingEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new tableSpread event
   * 
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createTableSpreadEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime,
      OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
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
    Integer trayCount = eventData.getTrayCount();
    TableSpreadEvent event = tableSpreadEventController.createTableSpreadEvent(batch, startTime, endTime, trayCount,
        additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(tableSpreadEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates table spread event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateTableSpreadEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
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
    Integer trayCount = eventData.getTrayCount();

    TableSpreadEvent updatedEvent = tableSpreadEventController.updateTableSpreadEvent((TableSpreadEvent) event, batch,
        startTime, endTime, trayCount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(tableSpreadEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new harvest event
   * 
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createCultivationObservationEvent(fi.metatavu.famifarm.persistence.model.Batch batch,
      OffsetDateTime startTime, OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
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

    CultivationObservationEvent event = cultivationObservationEventController.createCultivationActionEvent(batch,
        startTime, endTime, weight, luminance, pests, actions, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(cultivationObservationEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates cultivationObservation event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateCultivationObservationEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
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

    CultivationObservationEvent updatedEvent = cultivationObservationEventController.updateCultivationActionEvent(
        (CultivationObservationEvent) event, batch, startTime, endTime, weight, luminance, pests, actions,
        additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(cultivationObservationEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new harvest event
   * 
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createHarvestEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime,
      OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
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

    Integer amount = eventData.getGutterCount();

    TypeEnum harvestType = eventData.getType();
    HarvestEvent event = harvestEventController.createHarvestEvent(batch, startTime, endTime, harvestType,
        productionLine, additionalInformation, amount, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(harvestEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates harvest event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateHarvestEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
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

    TypeEnum harvestType = eventData.getType();
    HarvestEvent updatedEvent = harvestEventController.updateHarvestEvent((HarvestEvent) event, batch, startTime,
        endTime, harvestType, productionLine, eventData.getGutterCount(), additionalInformation, creatorId);

    batchController.updateRemainingUnits(batch);

    return createOk(harvestEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Creates new planting event
   * 
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createPlantingEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime,
      OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
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
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(eventData.getProductionLineId());
    Integer gutterHoleCount = eventData.getGutterHoleCount();
    Integer gutterCount = eventData.getGutterCount();
    Integer trayCount = eventData.getTrayCount();
    Integer workerCount = eventData.getWorkerCount();

    PlantingEvent event = plantingEventController.createPlantingEvent(batch, startTime, endTime, productionLine,
        gutterHoleCount, gutterCount, trayCount, workerCount, additionalInformation, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(plantingEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates planting event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updatePlantingEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
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
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(eventData.getProductionLineId());
    Integer gutterHoleCount = eventData.getGutterHoleCount();
    Integer gutterCount = eventData.getGutterCount();
    Integer trayCount = eventData.getTrayCount();
    Integer workerCount = eventData.getWorkerCount();

    PlantingEvent updatedEvent = plantingEventController.updatePlantingEvent((PlantingEvent) event, batch, startTime,
        endTime, productionLine, gutterHoleCount, gutterCount, trayCount, workerCount, additionalInformation,
        creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(plantingEventTranslator.translateEvent(updateBatchActiveEvent(updatedEvent)));
  }

  /**
   * Translates list of performed cultivation action ids into entities
   * 
   * @param performedActionIds ids
   * @param result             result list
   * @return error response or null if translation succeeds
   */
  private Response translatePerformedCultivationActions(List<UUID> performedActionIds,
      List<fi.metatavu.famifarm.persistence.model.PerformedCultivationAction> result) {
    if (performedActionIds == null) {
      return null;
    }

    for (UUID performedActionId : performedActionIds) {
      fi.metatavu.famifarm.persistence.model.PerformedCultivationAction cultivationAction = performedCultivationActionsController
          .findPerformedCultivationAction(performedActionId);
      if (cultivationAction == null) {
        return Response.status(Status.BAD_REQUEST)
            .entity(String.format("Invalid performted action id %s", performedActionId)).build();
      }

      result.add(cultivationAction);
    }

    return null;
  }

  /**
   * Translates list of pests ids into entities
   * 
   * @param pestIds ids
   * @param result  result list
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
   * @param batch     batch
   * @param startTime start time
   * @param endTime   end time
   * @param eventData event data
   * @return response
   */
  private Response createWastageEvent(fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime,
      OffsetDateTime endTime, String additionalInformation, Object eventDataObject) {
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
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(eventData.getReasonId());
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = eventData.getProductionLineId() != null
        ? productionLineController.findProductionLine(eventData.getProductionLineId())
        : null;

    if (eventData.getProductionLineId() != null && productionLine == null) {
      return createBadRequest("Invalid production line");
    }

    WastageEvent event = wastageEventController.createWastageEvent(batch, startTime, endTime, amount, wastageReason,
        phase, additionalInformation, productionLine, creatorId);
    batchController.updateRemainingUnits(batch);

    return createOk(wastageEventTranslator.translateEvent(updateBatchActiveEvent(event)));
  }

  /**
   * Updates wastage event
   * 
   * @param event           event
   * @param batch           batch
   * @param startTime       start time
   * @param endTime         end time
   * @param eventDataObject event data object
   * @return response
   */
  private Response updateWastageEvent(fi.metatavu.famifarm.persistence.model.Event event,
      fi.metatavu.famifarm.persistence.model.Batch batch, OffsetDateTime startTime, OffsetDateTime endTime,
      String additionalInformation, Object eventDataObject) {
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
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(eventData.getReasonId());
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = eventData.getProductionLineId() != null
            ? productionLineController.findProductionLine(eventData.getProductionLineId())
            : null;

    if (eventData.getProductionLineId() != null && productionLine == null) {
      return createBadRequest("Invalid production line");
    }

    WastageEvent updatedEvent = wastageEventController.updateWastageEvent((WastageEvent) event, batch, startTime,
        endTime, amount, wastageReason, phase, additionalInformation, productionLine, lastModifierId);
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
    updateBatchActiveEvent(event.getBatch());
    return event;
  }

  /**
   * Updates batche's active event
   * 
   * @param batch batch
   * @return same event
   */
  private void updateBatchActiveEvent(fi.metatavu.famifarm.persistence.model.Batch batch) {
    fi.metatavu.famifarm.persistence.model.Event activeEvent = eventController.findLastEventByBatch(batch);
    batchController.updateBatchActiveEvent(batch, activeEvent);
  }

  private <D> D readEventData(Class<D> targetClass, Object object) throws IOException {
    if (object == null) {
      return null;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(objectMapper.writeValueAsBytes(object), targetClass);
  }

}