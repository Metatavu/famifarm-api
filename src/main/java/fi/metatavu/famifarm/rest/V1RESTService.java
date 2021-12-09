package fi.metatavu.famifarm.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.discards.StorageDiscardController;
import fi.metatavu.famifarm.packings.CutPackingController;
import fi.metatavu.famifarm.packings.CutPackingInvalidParametersException;
import fi.metatavu.famifarm.printing.PrintingController;
import fi.metatavu.famifarm.reporting.ReportFormat;
import fi.metatavu.famifarm.rest.model.*;
import fi.metatavu.famifarm.rest.translate.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.metatavu.famifarm.authentication.Roles;
import fi.metatavu.famifarm.drafts.DraftController;
import fi.metatavu.famifarm.events.CultivationObservationEventController;
import fi.metatavu.famifarm.events.EventController;
import fi.metatavu.famifarm.events.HarvestEventController;
import fi.metatavu.famifarm.events.PlantingEventController;
import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.events.TableSpreadEventController;
import fi.metatavu.famifarm.events.WastageEventController;
import fi.metatavu.famifarm.packagesizes.PackageSizeController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.performedcultivationactions.PerformedCultivationActionsController;
import fi.metatavu.famifarm.persistence.model.CultivationObservationEvent;
import fi.metatavu.famifarm.persistence.model.HarvestEvent;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.PlantingEvent;
import fi.metatavu.famifarm.persistence.model.ProductAllowedHarvestType;
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
@Consumes({ "application/json" })
@Produces({ "application/json" })
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

  @Inject
  private CutPackingController cutPackingController;

  @Inject
  private CutPackingTranslator cutPackingTranslator;

  @Inject
  private StorageDiscardController storageDiscardController;

  @Inject
  private StorageDiscardTranslator storageDiscardTranslator;

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
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
  @Transactional
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
  public Response listPackings(Integer firstResult, Integer maxResults, UUID productId, UUID campaingId, PackingState status,
      String createdBefore, String createdAfter) {
    fi.metatavu.famifarm.persistence.model.Product product = null;
    fi.metatavu.famifarm.persistence.model.Campaign campaign = null;
    if (productId != null) {
      product = productController.findProduct(productId);
      
      if (product == null) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    if (campaingId != null) {
      campaign = campaignController.find(campaingId);
      if (campaign == null) {
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

    return createOk(packingController.listPackings(firstResult, maxResults, product, campaign, status, createdBeforeTime, createdAfterTime).stream().map(packing -> packingTranslator.translate(packing)).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
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
  @Transactional
  public Response createSeed(Seed body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(seedsTranslator.translateSeed(seedsController.createSeed(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listStorageDiscards(Integer firstResult, Integer maxResults, String fromTime, String toTime, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productId != null ? productController.findProduct(productId) : null;

    OffsetDateTime createdAfterTime;
    OffsetDateTime createdBeforeTime;

    try {
      createdBeforeTime = toTime != null ? OffsetDateTime.parse(toTime) : null;
    } catch (DateTimeParseException ex) {
      return createBadRequest("Invalid created before date");
    }

    try {
      createdAfterTime = fromTime != null ? OffsetDateTime.parse(fromTime) : null;
    } catch (DateTimeParseException ex) {
      return createBadRequest("Invalid created after date");
    }

    List<StorageDiscard> collect = storageDiscardController.listStorageDiscards(firstResult, maxResults, createdBeforeTime, createdAfterTime, product).stream().map(storageDiscardTranslator::translateStorageDiscard)
        .collect(Collectors.toList());

    return createOk(collect);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
  public Response createCutPacking(@Valid CutPacking cutPacking) {
    try {
      fi.metatavu.famifarm.persistence.model.CutPacking createdCutPacking = cutPackingController.create(
              cutPacking.getProductId(),
              cutPacking.getProductionLineId(),
              cutPacking.getWeight(),
              cutPacking.getSowingDay(),
              cutPacking.getCuttingDay(),
              cutPacking.getProducer(),
              cutPacking.getContactInformation(),
              cutPacking.getGutterCount(),
              cutPacking.getGutterHoleCount(),
              cutPacking.getStorageCondition(),
              getLoggerUserId()
      );

      CutPacking translatedCutPacking = cutPackingTranslator.translate(createdCutPacking);

      return createOk(translatedCutPacking);
    } catch (CutPackingInvalidParametersException exception) {
      return createBadRequest(exception.getMessage());
    }
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createEvent(Event body) {

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createBadRequest("Could not find specified product");
    }

    OffsetDateTime startTime = body.getStartTime();
    OffsetDateTime endTime = body.getEndTime();
    String additionalInformation = body.getAdditionalInformation();

    switch (body.getType()) {
    case SOWING:
      return createSowingEvent(product, startTime, endTime, additionalInformation, body.getData());
    case TABLE_SPREAD:
      return createTableSpreadEvent(product, startTime, endTime, additionalInformation, body.getData());
    case CULTIVATION_OBSERVATION:
      return createCultivationObservationEvent(product, startTime, endTime, additionalInformation, body.getData());
    case HARVEST:
      return createHarvestEvent(product, startTime, endTime, additionalInformation, body.getData());
    case PLANTING:
      return createPlantingEvent(product, startTime, endTime, additionalInformation, body.getData());
    case WASTAGE:
      return createWastageEvent(product, startTime, endTime, additionalInformation, body.getData());
    default:
      return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createPackageSize(PackageSize body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    Integer size = body.getSize();
    return createOk(packageSizeTranslator
        .translatePackageSize(packageSizeController.createPackageSize(name, size, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createPerformedCultivationAction(PerformedCultivationAction body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(
        performedCultivationActionsController.createPerformedCultivationAction(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createProduct(Product body) {
    List<fi.metatavu.famifarm.persistence.model.PackageSize> packageSizes = new ArrayList<>();

    if (body.getDefaultPackageSizeIds() != null) {
      body.getDefaultPackageSizeIds().forEach(uuid -> {
        fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(uuid);
        if (packageSize != null)
          packageSizes.add(packageSize);
      });
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    fi.metatavu.famifarm.persistence.model.Product productEntity = productController.createProduct(
      name,
      packageSizes,
      body.getIsSubcontractorProduct(),
      body.getActive(),
      getLoggerUserId()
    );
    if (body.getAllowedHarvestTypes() != null) {
      body.getAllowedHarvestTypes().forEach(allowedHarvestType -> {
        productController.createAllowedHarvestType(allowedHarvestType, productEntity);
      });
    }

    return createOk(productsTranslator.translateProduct(productEntity));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createProductionLine(ProductionLine body) {
    String lineNumber = body.getLineNumber();
    Integer defaultGutterHoleCount = body.getDefaultGutterHoleCount();

    return createOk(productionLineTranslator.translateProductionLine(productionLineController
        .createProductionLine(lineNumber, defaultGutterHoleCount, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createStorageDiscard(StorageDiscard payload) {
    UUID loggerUserId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.Product foundProduct = productController.findProduct(payload.getProductId());

    if (foundProduct == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.famifarm.persistence.model.PackageSize foundpackageSize = packageSizeController.findPackageSize(payload.getPackageSizeId());

    if (foundpackageSize == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (productController.listPackageSizesForProduct(foundProduct).stream().filter(packSize -> packSize == foundpackageSize.getId()).findAny().isEmpty()) {
      return createBadRequest("Package size doesn't belong to the product");
    }

    fi.metatavu.famifarm.persistence.model.StorageDiscard storageDiscard = storageDiscardController.create(foundProduct, foundpackageSize, payload.getDiscardAmount(), payload.getDiscardDate(), loggerUserId);
    return createOk(storageDiscardTranslator.translateStorageDiscard(storageDiscard));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createWastageReason(WastageReason body) {
    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();

    return createOk(wastageReasonsTranslator
        .translateWastageReason(wastageReasonsController.createWastageReason(reason, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
  public Response deleteCutPacking(UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking existingCutPacking = cutPackingController.find(cutPackingId);

    if (existingCutPacking == null) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    cutPackingController.delete(existingCutPacking);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response deleteEvent(UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    eventController.deleteEvent(event);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
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
  @Transactional
  public Response deleteProduct(UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }
    
    for (fi.metatavu.famifarm.persistence.model.Packing packing : packingController.listPackings(null, null, null, null, null, null, null)) {
      if (packing.getProduct().getId() == productId) {
        return createBadRequest("Product can not be deleted, because it is linked to packings");
      }
    }
    
    productController.deleteProduct(product);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
  public Response deleteSeedBatch(UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    seedBatchController.deleteSeedBatch(seedBatch);

    return createNoContent();
  }

  @Override
  @Transactional
  public Response deleteStorageDiscard(UUID storageDiscardId) {
    fi.metatavu.famifarm.persistence.model.StorageDiscard storageDiscard = storageDiscardController.findById(storageDiscardId);

    if (storageDiscard == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    storageDiscardController.deleteStorageDiscard(storageDiscard);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response findCampaign(UUID campaignId) {
    fi.metatavu.famifarm.persistence.model.Campaign campaign = campaignController.find(campaignId);

    if (campaign == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    return createOk(campaignTranslator.translate(campaign));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findCutPacking(UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking foundCutPacking = cutPackingController.find(cutPackingId);

    if (foundCutPacking == null) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    CutPacking translatedCutPacking = cutPackingTranslator.translate(foundCutPacking);
    return createOk(translatedCutPacking);
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listCampaigns() {
    List<Campaign> translatedCampaigns = campaignController.list().stream().map(campaignTranslator::translate).collect(Collectors.toList());
    return createOk(translatedCampaigns);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listCutPackings(Integer firstResult, Integer maxResults, UUID productId, String createdBeforeString, String createdAfterString) {
    fi.metatavu.famifarm.persistence.model.Product productToFilterBy = null;

    if (productId != null) {
      fi.metatavu.famifarm.persistence.model.Product existingProduct = productController.findProduct(productId);

      if (existingProduct == null) {
        return createBadRequest(String.format("Product with id %s not found!", productId));
      }

      productToFilterBy = existingProduct;
    }

    OffsetDateTime createdBefore = null;
    if (createdBeforeString != null) {
      createdBefore = OffsetDateTime.parse(createdBeforeString);
    }

    OffsetDateTime createdAfter = null;
    if (createdAfterString != null) {
      createdAfter = OffsetDateTime.parse(createdAfterString);
    }

    List<fi.metatavu.famifarm.persistence.model.CutPacking> cutPackings = cutPackingController.list(firstResult, maxResults, productToFilterBy, null, createdBefore, createdAfter);
    List<CutPacking> translatedCutPackings = cutPackings.stream().map(cutPackingTranslator::translate).collect(Collectors.toList());
    return createOk(translatedCutPackings);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listEvents(Integer firstResult, Integer maxResults, UUID productId, String createdAfter,
  String createdBefore, EventType eventType) {
    fi.metatavu.famifarm.persistence.model.Product product = productId != null ? productController.findProduct(productId) : null;

    OffsetDateTime createdBeforeTime = createdBefore != null ? OffsetDateTime.parse(createdBefore) : null;
    
    OffsetDateTime createdAfterTime = createdAfter != null ? OffsetDateTime.parse(createdAfter) : null;

    List<Event> result = eventController.listEventsRest(product, createdAfterTime, createdBeforeTime, firstResult, eventType, maxResults).stream().map(this::translateEvent)
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
  public Response listProducts(Integer firstResult, Integer maxResults, Boolean includeInActiveProducts, Boolean includeSubcontractorProducts) {
    List<Product> result = productController.listProducts(firstResult, maxResults, includeSubcontractorProducts, includeInActiveProducts).stream()
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
    fi.metatavu.famifarm.persistence.model.CutPacking cutPacking = cutPackingController.find(packingId);

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

      Integer status = null;

      if (packing == null && cutPacking == null) {
        return createBadRequest("Packing not found!");
      }

      if (packing != null) {
        status = printingController.printQrCode(printerId, packing, getLocale());
      }

      if (cutPacking != null) {
        status = printingController.printQrCode(printerId, cutPacking, getLocale());
      }

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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response updateCutPacking(@Valid CutPacking cutPacking, UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking existingCutPacking = cutPackingController.find(cutPackingId);

    if (existingCutPacking == null) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    try {
      fi.metatavu.famifarm.persistence.model.CutPacking updatedCutPacking = cutPackingController.update(
              existingCutPacking,
              cutPacking.getProductId(),
              cutPacking.getProductionLineId(),
              cutPacking.getWeight(),
              cutPacking.getSowingDay(),
              cutPacking.getCuttingDay(),
              cutPacking.getProducer(),
              cutPacking.getContactInformation(),
              cutPacking.getGutterCount(),
              cutPacking.getGutterHoleCount(),
              cutPacking.getStorageCondition(),
              getLoggerUserId()
      );

      CutPacking translatedCutPacking = cutPackingTranslator.translate(updatedCutPacking);

      return createOk(translatedCutPacking);
    } catch (CutPackingInvalidParametersException exception) {
      return createBadRequest(exception.getMessage());
    }
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateEvent(Event body, UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null) {
      return createBadRequest("Could not find specified batch");
    }

    OffsetDateTime startTime = body.getStartTime();
    OffsetDateTime endTime = body.getEndTime();
    String additionalInformation = body.getAdditionalInformation();

    switch (body.getType()) {
    case SOWING:
      return updateSowingEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    case TABLE_SPREAD:
      return updateTableSpreadEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    case CULTIVATION_OBSERVATION:
      return updateCultivationObservationEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    case HARVEST:
      return updateHarvestEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    case PLANTING:
      return updatePlantingEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    case WASTAGE:
      return updateWastageEvent(event, product, startTime, endTime, additionalInformation, body.getData());
    default:
      return Response.status(Status.NOT_IMPLEMENTED).build();
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
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
  @Transactional
  public Response updateProduct(Product body, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null) {
      return createNotFound("Product not found");
    }

    List<fi.metatavu.famifarm.persistence.model.PackageSize> packageSizeList = new ArrayList<>();
    if (body.getDefaultPackageSizeIds() != null && !body.getDefaultPackageSizeIds().isEmpty()) {
      packageSizeList = body.getDefaultPackageSizeIds().stream().map(id -> packageSizeController.findPackageSize(id)).collect(Collectors.toList());
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    fi.metatavu.famifarm.persistence.model.Product productEntity = productController.updateProduct(
      product,
      name,
      packageSizeList,
      body.getIsSubcontractorProduct(),
      body.getActive(),
      getLoggerUserId()
    );
    List<ProductAllowedHarvestType> allowedHarvestTypes = productController.listAllowedHarvestTypes(productEntity);
    List<HarvestEventType> updatedHarvestTypes = body.getAllowedHarvestTypes() != null ? body.getAllowedHarvestTypes() : new ArrayList<HarvestEventType>();
    allowedHarvestTypes.forEach(allowedHarvestType -> {
      if (updatedHarvestTypes.contains(allowedHarvestType.getHarvestType())) {
        updatedHarvestTypes.remove(allowedHarvestType.getHarvestType());
      } else {
        productController.deleteProductAllowedHarvestType(allowedHarvestType);
      }
    });
    updatedHarvestTypes.forEach(newHarvestType -> {
      productController.createAllowedHarvestType(newHarvestType, productEntity);
    });

    return createOk(productsTranslator
        .translateProduct(productEntity));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
  public Response updateSeedBatch(SeedBatch body, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);
    if (seedBatch == null) {
      createNotFound(NOT_FOUND_MESSAGE);
    }

    String code = body.getCode();
    OffsetDateTime time = body.getTime();
    UUID seedId = body.getSeedId();
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    boolean active = body.getActive() != null ? body.getActive() : Boolean.FALSE;

    return createOk(seedBatchController.updateSeedBatch(seedBatch, code, seed, time, active, getLoggerUserId()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateStorageDiscard(StorageDiscard payload, UUID storageDiscardId) {
    fi.metatavu.famifarm.persistence.model.StorageDiscard foundStorageDiscard = storageDiscardController.findById(storageDiscardId);

    if (foundStorageDiscard == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(payload.getPackageSizeId());
    if (packageSize == null) {
      return createNotFound("Package size not found");
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(payload.getProductId());
    if (product == null) {
      return createNotFound("Product not found");
    }

    return createOk(storageDiscardTranslator.translateStorageDiscard(storageDiscardController.
        updateStorageDiscard(foundStorageDiscard, payload.getDiscardAmount(), payload.getDiscardDate(), product, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  public Response getReport(String typeParam, String fromTime, String toTime, String reportFormat) {
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

    ReportFormat format = EnumUtils.getEnum(ReportFormat.class, reportFormat);
    if (format ==  null) {
      format = fi.metatavu.famifarm.reporting.ReportFormat.XLS;
    }

    Report report = reportController.getReport(reportType, format);

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

  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Override
  public Response getStorageDiscard(UUID storageDiscardId) {

    fi.metatavu.famifarm.persistence.model.StorageDiscard foundStorageDiscard = storageDiscardController.findById(storageDiscardId);
    if (foundStorageDiscard == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(storageDiscardTranslator.translateStorageDiscard(foundStorageDiscard));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response createDraft(Draft body) {
    UUID creatorId = getLoggerUserId();

    //TODO: Add unique index to database to prevent duplicates created by ui errors
    draftController.deleteDraftsByCreatorIdAndType(creatorId, body.getType());
    return createOk(
        draftTranslator.translateDraft(draftController.createDraft(body.getType(), body.getData(), creatorId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
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
  @Transactional
  public Response createPest(Pest body) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(pestsTranslator.translatePest(pestsController.createPest(name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
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
  @Transactional
  public Response updatePest(Pest body, UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);
    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(pestsTranslator.translatePest(pestsController.updatePest(pest, name, loggerUserId)));
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
  private Response createSowingEvent(fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime,
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
    PotType potType = PotType.PAPER;
    Integer amount = eventData.getAmount();

    SowingEvent event = sowingEventController.createSowingEvent(product, startTime, endTime, productionLine, seedBatches,
        potType, amount, additionalInformation, creatorId);

    return createOk(sowingEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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
    PotType potType = ((SowingEvent) event).getPotType();
    Integer amount = eventData.getAmount();
    SowingEvent updatedEvent = sowingEventController.updateSowingEvent((SowingEvent) event, product, startTime, endTime,
        productionLine, seedBatches, potType, amount, additionalInformation, creatorId);

    return createOk(sowingEventTranslator.translateEvent(updatedEvent));
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
  private Response createTableSpreadEvent(fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime,
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
    TableSpreadEvent event = tableSpreadEventController.createTableSpreadEvent(product, startTime, endTime, trayCount,
        additionalInformation, creatorId);

    return createOk(tableSpreadEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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

    TableSpreadEvent updatedEvent = tableSpreadEventController.updateTableSpreadEvent((TableSpreadEvent) event, product,
        startTime, endTime, trayCount, additionalInformation, creatorId);

    return createOk(tableSpreadEventTranslator.translateEvent(updatedEvent));
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
  private Response createCultivationObservationEvent(fi.metatavu.famifarm.persistence.model.Product product,
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

    CultivationObservationEvent event = cultivationObservationEventController.createCultivationActionEvent(product,
        startTime, endTime, weight, luminance, pests, actions, additionalInformation, creatorId);

    return createOk(cultivationObservationEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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
        (CultivationObservationEvent) event, product, startTime, endTime, weight, luminance, pests, actions,
        additionalInformation, creatorId);

    return createOk(cultivationObservationEventTranslator.translateEvent(updatedEvent));
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
  private Response createHarvestEvent(fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime,
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
    Integer gutterHoleCount = eventData.getGutterHoleCount();
    OffsetDateTime sowingTime = eventData.getSowingDate();

    HarvestEventType harvestType = eventData.getType();
    HarvestEvent event = harvestEventController.createHarvestEvent(product, startTime, endTime, harvestType,
        productionLine, sowingTime, additionalInformation, amount, gutterHoleCount, creatorId);

    return createOk(harvestEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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

    HarvestEventType harvestType = eventData.getType();
    OffsetDateTime sowingTime = eventData.getSowingDate();
    HarvestEvent updatedEvent = harvestEventController.updateHarvestEvent((HarvestEvent) event, product, startTime,
        endTime, harvestType, productionLine, sowingTime, eventData.getGutterCount(), eventData.getGutterHoleCount(), additionalInformation, creatorId);


    return createOk(harvestEventTranslator.translateEvent(updatedEvent));
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
  private Response createPlantingEvent(fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime,
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
    OffsetDateTime sowingTime = eventData.getSowingDate();

    PlantingEvent event = plantingEventController.createPlantingEvent(product, startTime, endTime, productionLine, sowingTime,
        gutterHoleCount, gutterCount, trayCount, workerCount, additionalInformation, creatorId);

    return createOk(plantingEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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
    OffsetDateTime sowingTime = eventData.getSowingDate();

    PlantingEvent updatedEvent = plantingEventController.updatePlantingEvent((PlantingEvent) event, product, startTime,
        endTime, productionLine, sowingTime, gutterHoleCount, gutterCount, trayCount, workerCount, additionalInformation,
        creatorId);

    return createOk(plantingEventTranslator.translateEvent(updatedEvent));
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
  private Response createWastageEvent(fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime,
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

    WastageEvent event = wastageEventController.createWastageEvent(product, startTime, endTime, amount, wastageReason,
        phase, additionalInformation, productionLine, creatorId);

    return createOk(wastageEventTranslator.translateEvent(event));
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
      fi.metatavu.famifarm.persistence.model.Product product, OffsetDateTime startTime, OffsetDateTime endTime,
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

    WastageEvent updatedEvent = wastageEventController.updateWastageEvent((WastageEvent) event, product, startTime,
        endTime, amount, wastageReason, phase, additionalInformation, productionLine, lastModifierId);

    return createOk(wastageEventTranslator.translateEvent(updatedEvent));
  }


  private <D> D readEventData(Class<D> targetClass, Object object) throws IOException {
    if (object == null) {
      return null;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper.readValue(objectMapper.writeValueAsBytes(object), targetClass);
  }

}