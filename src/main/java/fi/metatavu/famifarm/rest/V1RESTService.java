package fi.metatavu.famifarm.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.metatavu.famifarm.authentication.Roles;
import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.discards.StorageDiscardController;
import fi.metatavu.famifarm.drafts.DraftController;
import fi.metatavu.famifarm.events.*;
import fi.metatavu.famifarm.filmbatches.PackagingFilmBatchController;
import fi.metatavu.famifarm.packagesizes.PackageSizeController;
import fi.metatavu.famifarm.packings.CutPackingController;
import fi.metatavu.famifarm.packings.CutPackingInvalidParametersException;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.performedcultivationactions.PerformedCultivationActionsController;
import fi.metatavu.famifarm.persistence.model.*;
import fi.metatavu.famifarm.pests.PestsController;
import fi.metatavu.famifarm.printing.PrintingController;
import fi.metatavu.famifarm.productionlines.ProductionLineController;
import fi.metatavu.famifarm.products.ProductController;
import fi.metatavu.famifarm.reporting.*;
import fi.metatavu.famifarm.rest.api.V1Api;
import fi.metatavu.famifarm.rest.model.*;
import fi.metatavu.famifarm.rest.model.Campaign;
import fi.metatavu.famifarm.rest.model.CampaignProduct;
import fi.metatavu.famifarm.rest.model.CutPacking;
import fi.metatavu.famifarm.rest.model.Draft;
import fi.metatavu.famifarm.rest.model.Event;
import fi.metatavu.famifarm.rest.model.HarvestBasket;
import fi.metatavu.famifarm.rest.model.PackageSize;
import fi.metatavu.famifarm.rest.model.PackagingFilmBatch;
import fi.metatavu.famifarm.rest.model.Packing;
import fi.metatavu.famifarm.rest.model.PerformedCultivationAction;
import fi.metatavu.famifarm.rest.model.Pest;
import fi.metatavu.famifarm.rest.model.Product;
import fi.metatavu.famifarm.rest.model.ProductionLine;
import fi.metatavu.famifarm.rest.model.Seed;
import fi.metatavu.famifarm.rest.model.SeedBatch;
import fi.metatavu.famifarm.rest.model.StorageDiscard;
import fi.metatavu.famifarm.rest.model.WastageReason;
import fi.metatavu.famifarm.rest.translate.*;
import fi.metatavu.famifarm.seedbatches.SeedBatchesController;
import fi.metatavu.famifarm.seeds.SeedsController;
import fi.metatavu.famifarm.wastagereason.WastageReasonsController;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

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
  CampaignTranslator campaignTranslator;

  @Inject
  CampaignController campaignController;

  @Inject
  Logger logger;

  @Inject
  SeedsController seedsController;

  @Inject
  SeedsTranslator seedsTranslator;

  @Inject
  WastageReasonsController wastageReasonsController;

  @Inject
  WastageReasonsTranslator wastageReasonsTranslator;

  @Inject
  SeedBatchesController seedBatchController;

  @Inject
  SeedBatchTranslator seedBatchesTranslator;

  @Inject
  PackageSizeTranslator packageSizeTranslator;

  @Inject
  PackageSizeController packageSizeController;

  @Inject
  ProductController productController;

  @Inject
  ProductsTranslator productsTranslator;

  @Inject
  ProductionLineController productionLineController;

  @Inject
  PerformedCultivationActionTranslator performedCultivationActionTranslator;

  @Inject
  PerformedCultivationActionsController performedCultivationActionsController;

  @Inject
  PestsController pestsController;

  @Inject
  PestsTranslator pestsTranslator;

  @Inject
  ProductionLineTranslator productionLineTranslator;

  @Inject
  SowingEventTranslator sowingEventTranslator;

  @Inject
  SowingEventController sowingEventController;

  @Inject
  TableSpreadEventController tableSpreadEventController;

  @Inject
  TableSpreadEventTranslator tableSpreadEventTranslator;

  @Inject
  EventController eventController;

  @Inject
  CultivationObservationEventController cultivationObservationEventController;

  @Inject
  CultivationObservationEventTranslator cultivationObservationEventTranslator;

  @Inject
  HarvestEventController harvestEventController;

  @Inject
  HarvestEventTranslator harvestEventTranslator;

  @Inject
  PlantingEventController plantingEventController;

  @Inject
  PlantingEventTranslator plantingEventTranslator;

  @Inject
  WastageEventController wastageEventController;

  @Inject
  WastageEventTranslator wastageEventTranslator;

  @Inject
  ReportController reportController;

  @Inject
  DraftController draftController;

  @Inject
  DraftTranslator draftTranslator;
  
  @Inject
  PackingController packingController;
  
  @Inject
  PackingTranslator packingTranslator;

  @Inject
  PrintingController printingController;

  @Inject
  CutPackingController cutPackingController;

  @Inject
  CutPackingTranslator cutPackingTranslator;

  @Inject
  StorageDiscardController storageDiscardController;

  @Inject
  StorageDiscardTranslator storageDiscardTranslator;

  @Inject
  PackagingFilmBatchController packagingFilmBatchController;

  @Inject
  PackagingFilmBatchTranslator packagingFilmBatchTranslator;

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response createPacking(Packing body, Facility facility) {
    PackingType packingType = body.getType();

    UUID productId = body.getProductId();
    fi.metatavu.famifarm.persistence.model.Product product = null;
    if (productId != null) {
      product = productController.findProduct(body.getProductId());

      if (product == null || product.getFacility() != facility) {
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
      if (campaign == null || campaign.getFacility() != facility) {
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
      if (packageSize == null || packageSize.getFacility() != facility) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    // Verify packing baskets product references and create a list of valid packing baskets to be reused for creating the objects
    List<PackingBasket> validPackingBaskets = new ArrayList<>();
    if (body.getBasketsUsed() != null) {
      for (PackingUsedBasket basket : body.getBasketsUsed()) {
        UUID basketProductId = basket.getProductId();
        fi.metatavu.famifarm.persistence.model.Product basketProduct = productController.findProduct(basketProductId);
        if (basketProduct == null || basketProduct.getFacility() != facility) {
          return createNotFound(NOT_FOUND_MESSAGE);
        }
        PackingBasket packingBasket = new PackingBasket();
        packingBasket.setId(UUID.randomUUID());
        packingBasket.setProduct(basketProduct);
        packingBasket.setCount(basket.getBasketCount());
        validPackingBaskets.add(packingBasket);
      }
    }

    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch packagingFilmBatch = null;
    if (body.getPackagingFilmBatchId() != null) {
      packagingFilmBatch = packagingFilmBatchController.findById(body.getPackagingFilmBatchId());
      if (packagingFilmBatch == null || packagingFilmBatch.getFacility() != facility) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    return createOk(
      packingTranslator.translate(
        packingController.create(
          getLoggerUserId(),
          facility,
          product,
          packageSize,
          body,
          packagingFilmBatch,
          validPackingBaskets,
          campaign,
          packingType
        )
      )
    );
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response deletePacking(Facility facility, UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);
    
    if (packing == null || packing.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    packingController.deletePacking(packing);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findPacking(Facility facility, UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);
    
    if (packing == null || packing.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(packingTranslator.translate(packing));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listPackings(Facility facility, Integer firstResult, Integer maxResults, UUID productId, UUID campaingId, PackingState status,
      String createdBefore, String createdAfter) {
    fi.metatavu.famifarm.persistence.model.Product product = null;
    fi.metatavu.famifarm.persistence.model.Campaign campaign = null;
    if (productId != null) {
      product = productController.findProduct(productId);
      
      if (product == null || product.getFacility() != facility) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    if (campaingId != null) {
      campaign = campaignController.find(campaingId);
      if (campaign == null || campaign.getFacility() != facility) {
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

    return createOk(packingController.listPackings(firstResult, maxResults, facility, product, campaign, status, createdBeforeTime, createdAfterTime).stream().map(packing -> packingTranslator.translate(packing)).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response updatePacking(Packing body, Facility facility, UUID packingId) {
    fi.metatavu.famifarm.persistence.model.Packing packing = packingController.findById(packingId);

    if (packing == null || packing.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    PackingType packingType = body.getType();
    UUID productId = body.getProductId();
    fi.metatavu.famifarm.persistence.model.Product product = null;
    if (productId != null) {
      product = productController.findProduct(body.getProductId());

      if (product == null || product.getFacility() != facility) {
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
      if (campaign == null || campaign.getFacility() != facility) {
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
      if (packageSize == null || packageSize.getFacility() != facility) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    // Verify packing baskets and create a list of valid packing baskets to be reused for creating the objects
    List<PackingBasket> validPackingBaskets = new ArrayList<>();
    if (body.getBasketsUsed() != null) {
      for (PackingUsedBasket basket : body.getBasketsUsed()) {
        UUID basketProductId = basket.getProductId();
        fi.metatavu.famifarm.persistence.model.Product basketProduct = productController.findProduct(basketProductId);
        if (basketProduct == null || basketProduct.getFacility() != facility) {
          return createNotFound(NOT_FOUND_MESSAGE);
        }
        PackingBasket packingBasket = new PackingBasket();
        packingBasket.setId(UUID.randomUUID());
        packingBasket.setPacking(packing);
        packingBasket.setProduct(basketProduct);
        packingBasket.setCount(basket.getBasketCount());
        validPackingBaskets.add(packingBasket);
      }
    }

    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch packagingFilmBatch = null;
    if (body.getPackagingFilmBatchId() != null) {
      packagingFilmBatch = packagingFilmBatchController.findById(body.getPackagingFilmBatchId());
      if (packagingFilmBatch == null || packagingFilmBatch.getFacility() != facility) {
        return createNotFound(NOT_FOUND_MESSAGE);
      }
    }

    return createOk(
      packingTranslator.translate(
        packingController.updatePacking(
          packing,
          packageSize,
          body,
          packagingFilmBatch,
          validPackingBaskets,
          product,
          campaign,
          getLoggerUserId())
      )
    );
  }
  
  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createSeed(Seed body, Facility facility) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(seedsTranslator.translateSeed(seedsController.createSeed(facility, name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteSeed(Facility facility, UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);

    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (seed.getFacility() != facility) {
      return createBadRequest(String.format("Seed with id %s doesn't belong to facility %s", seedId, facility));
    }

    seedsController.deleteSeed(seed);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findSeed(Facility facility, UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null || seed.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(seedsTranslator.translateSeed(seed));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listSeeds(Facility facility, Integer firstResult, Integer maxResults) {
    List<Seed> result = seedsController.listSeeds(facility, firstResult, maxResults).stream().map(seedsTranslator::translateSeed)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listStorageDiscards(Facility facility, Integer firstResult, Integer maxResults, String fromTime, String toTime, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productId != null ? productController.findProduct(productId) : null;

    if (product != null && product.getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", productId, facility));
    }

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

    List<StorageDiscard> collect = storageDiscardController.listStorageDiscards(firstResult, maxResults, createdBeforeTime, createdAfterTime, product, facility).stream().map(storageDiscardTranslator::translateStorageDiscard)
        .collect(Collectors.toList());

    return createOk(collect);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateSeed(Seed body, Facility facility, UUID seedId) {
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed == null || seed.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(seedsTranslator.translateSeed(seedsController.updateSeed(seed, name, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createCampaign(@Valid Campaign campaign, Facility facility) {
    HashMap<fi.metatavu.famifarm.persistence.model.Product, Integer> campaignProductsToCreate = new HashMap<>();
    List<CampaignProduct> restCampaignProducts = campaign.getProducts();

    for (CampaignProduct campaignProduct : restCampaignProducts) {
      fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(campaignProduct.getProductId());
      if (product == null) {
        return createNotFound(String.format("Campaign product %s not found!", campaignProduct.getProductId()));
      }

      if (product.getFacility() != facility) {
        return createBadRequest(String.format("Campaign product with id %s doesn't belong to facility %s", product.getId(), facility));
      }

      campaignProductsToCreate.put(product, campaignProduct.getCount());
    }

    fi.metatavu.famifarm.persistence.model.Campaign createdCampaign = campaignController.create(campaign.getName(), campaignProductsToCreate, facility, getLoggerUserId());
    return createOk(campaignTranslator.translate(createdCampaign));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createCutPacking(@Valid CutPacking cutPacking, Facility facility) {
    try {
      fi.metatavu.famifarm.persistence.model.CutPacking createdCutPacking = cutPackingController.create(
              facility,
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
  public Response createEvent(Event body, Facility facility) {

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null || product.getFacility() != facility) {
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
  public Response createPackageSize(PackageSize body, Facility facility) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    Integer size = body.getSize();
    return createOk(packageSizeTranslator
        .translatePackageSize(packageSizeController.createPackageSize(name, size, facility, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({Roles.ADMIN, Roles.MANAGER})
  @Transactional
  public Response createPackagingFilmBatch(PackagingFilmBatch packagingFilmBatch, Facility facility) {
    return createOk(packagingFilmBatchTranslator.translatePackagingFilmBatch(
        packagingFilmBatchController.create(
            facility,
            packagingFilmBatch,
            getLoggerUserId()
        )
    ));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createPerformedCultivationAction(PerformedCultivationAction body, Facility facility) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(performedCultivationActionTranslator.translatePerformedCultivationAction(
        performedCultivationActionsController.createPerformedCultivationAction(name, facility, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createProduct(Product body, Facility facility) {
    List<fi.metatavu.famifarm.persistence.model.PackageSize> packageSizes = new ArrayList<>();

    if (body.getDefaultPackageSizeIds() != null) {
      body.getDefaultPackageSizeIds().forEach(uuid -> {
        fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(uuid);
        if (packageSize != null && packageSize.getFacility() == facility)
          packageSizes.add(packageSize);
      });
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    fi.metatavu.famifarm.persistence.model.Product productEntity = productController.createProduct(
      name,
      packageSizes,
      body.getIsSubcontractorProduct(),
      body.getActive(),
      body.getIsEndProduct(),
      body.getIsRawMaterial(),
      body.getSalesWeight(),
      facility,
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
  public Response createProductionLine(ProductionLine body, Facility facility) {
    String lineNumber = body.getLineNumber();
    Integer defaultGutterHoleCount = body.getDefaultGutterHoleCount();

    return createOk(productionLineTranslator.translateProductionLine(productionLineController
        .createProductionLine(facility, lineNumber, defaultGutterHoleCount, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createSeedBatch(SeedBatch body, Facility facility) {
    String code = body.getCode();
    UUID seedId = body.getSeedId();
    OffsetDateTime time = body.getTime();

    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);

    if (seed == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (seed.getFacility() != facility) {
      return createBadRequest(String.format("Seed with id %s doesn't belong to facility %s", seedId, facility));
    }

    return createOk(seedBatchesTranslator
        .translateSeedBatch(seedBatchController.createSeedBatch(code, seed, time, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createStorageDiscard(StorageDiscard payload, Facility facility) {
    UUID loggerUserId = getLoggerUserId();
    fi.metatavu.famifarm.persistence.model.Product foundProduct = productController.findProduct(payload.getProductId());

    if (foundProduct == null) {
      return createNotFound(String.format("Product with id %s not found", payload.getId()));
    }

    if (foundProduct.getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", foundProduct.getId(), facility));
    }

    fi.metatavu.famifarm.persistence.model.PackageSize foundpackageSize = packageSizeController.findPackageSize(payload.getPackageSizeId());

    if (foundpackageSize == null) {
      return createNotFound(String.format("PackageSize with id %s not found", payload.getPackageSizeId()));
    }

    if (foundpackageSize.getFacility() != facility) {
      return createNotFound(String.format("PackageSize with id %s doesn't belong to facility %s", payload.getPackageSizeId(), facility));
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
  public Response createWastageReason(WastageReason body, Facility facility) {
    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();

    return createOk(wastageReasonsTranslator
        .translateWastageReason(wastageReasonsController.createWastageReason(reason, loggerUserId, facility)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteCampaign(Facility facility, UUID campaignId) {
    fi.metatavu.famifarm.persistence.model.Campaign campaign = campaignController.find(campaignId);
    if (campaign == null || campaign.getFacility() != facility) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    campaignController.delete(campaign);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response deleteCutPacking(Facility facility, UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking existingCutPacking = cutPackingController.find(cutPackingId);

    if (existingCutPacking == null) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    if (existingCutPacking.getProduct().getFacility() != facility) {
      return createBadRequest(String.format("Cut packing with id %s doesn't belong to facility %s", cutPackingId, facility));
    }

    cutPackingController.delete(existingCutPacking);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response deleteEvent(Facility facility, UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null || event.getProduct().getFacility() != facility) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    eventController.deleteEvent(event);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deletePackageSize(Facility facility, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);

    if (packageSize == null) {
      return createNotFound("Package size not found");
    }

    if (packageSize.getFacility() != facility) {
      return createBadRequest(String.format("Package size with id %s doesn't belong to facility %s", packageSizeId, facility));
    }

    packageSizeController.deletePackageSize(packageSize);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deletePackagingFilmBatch(Facility facility, UUID packagingFilmBatchId) {
    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch packagingFilmBatch = packagingFilmBatchController.findById(packagingFilmBatchId);
    if (packagingFilmBatch == null) {
      return createNotFound(String.format("Packaging film batch with id %s not found!", packagingFilmBatchId));
    }

    if (packagingFilmBatch.getFacility() != facility) {
      return createBadRequest(String.format("Packaging film batch with id %s doesn't belong to facility %s", packagingFilmBatchId, facility));
    }

    packagingFilmBatchController.delete(packagingFilmBatch);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deletePerformedCultivationAction(Facility facility, UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);

    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (performedCultivationAction.getFacility() != facility) {
      return createBadRequest(String.format("Performed cultivation action with id %s doesn't belong to facility %s", performedCultivationActionId, facility));
    }

    performedCultivationActionsController.deletePerformedCultivationAction(performedCultivationAction);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteProduct(Facility facility, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null || product.getFacility() != facility) {
      return createNotFound("Product not found");
    }
    
    for (fi.metatavu.famifarm.persistence.model.Packing packing : packingController.listPackings(null, null, facility, null, null, null, null, null)) {
      if (packing.getProduct() != null && packing.getProduct().getId() == productId) {
        return createBadRequest("Product can not be deleted, because it is linked to packings");
      }
    }
    
    productController.deleteProduct(product);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteProductionLine(Facility facility, UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);

    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (productionLine.getFacility() != facility) {
      return createBadRequest(String.format("Production line with id %s doesn't belong to facility %s", productionLineId, facility));
    }

    productionLineController.deleteProductionLine(productionLine);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteSeedBatch(Facility facility, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);

    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (seedBatch.getSeed().getFacility() != facility) {
      return createBadRequest(String.format("Seed with id %s doesn't belong to facility %s", seedBatch.getSeed().getId(), facility));
    }

    seedBatchController.deleteSeedBatch(seedBatch);

    return createNoContent();
  }

  @Override
  @Transactional
  public Response deleteStorageDiscard(Facility facility, UUID storageDiscardId) {
    fi.metatavu.famifarm.persistence.model.StorageDiscard storageDiscard = storageDiscardController.findById(storageDiscardId);

    if (storageDiscard == null) {
      return createNotFound(String.format("Storage discard with id %s not found!", storageDiscardId));
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(storageDiscard.getProduct().getId());

    if (product.getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", product.getId(), facility));
    }

    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(storageDiscard.getPackageSize().getId());

    if (packageSize.getFacility() != facility) {
      return createBadRequest(String.format("Package size with id %s doesn't belong to facility %s", packageSize.getFacility(), facility));
    }

    storageDiscardController.deleteStorageDiscard(storageDiscard);
    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deleteWastageReason(Facility facility, UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);

    if (wastageReason == null) {
      return createNotFound(String.format("Wastage reason with id %s not found!", wastageReasonId));
    }

    if (wastageReason.getFacility() != facility) {
      return createBadRequest(String.format("Wastage reason iwth id %s doesn't belong to facility %s", wastageReasonId, facility));
    }

    wastageReasonsController.deleteWastageReason(wastageReason);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response findCampaign(Facility facility, UUID campaignId) {
    fi.metatavu.famifarm.persistence.model.Campaign campaign = campaignController.find(campaignId);

    if (campaign == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    if (campaign.getFacility() != facility) {
      return createBadRequest(String.format("Campaign %s doesn't belong to facility %s", campaignId, facility));
    }

    return createOk(campaignTranslator.translate(campaign));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findCutPacking(Facility facility, UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking foundCutPacking = cutPackingController.find(cutPackingId);

    if (foundCutPacking == null) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    if (foundCutPacking.getProduct().getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", foundCutPacking.getProduct().getId(), facility));
    }

    CutPacking translatedCutPacking = cutPackingTranslator.translate(foundCutPacking);
    return createOk(translatedCutPacking);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findEvent(Facility facility, UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null || event.getProduct().getFacility() != facility) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    return createOk(translateEvent(event));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPackageSize(Facility facility, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);

    if (packageSize == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (facility != packageSize.getFacility()) {
      return createBadRequest(String.format("Package size with id %s doesn't belong to facility %s", packageSizeId, facility));
    }

    return createOk(packageSizeTranslator.translatePackageSize(packageSize));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPackagingFilmBatch(Facility facility, UUID packagingFilmBatchId) {
    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch packagingFilmBatch = packagingFilmBatchController.findById(packagingFilmBatchId);
    if (packagingFilmBatch == null) {
      return createNotFound(String.format("Packaging film batch with id %s not found!", packagingFilmBatchId));
    }

    if (packagingFilmBatch.getFacility() != facility) {
      return createBadRequest(String.format("Packaging film batch with id %s doesn't belong to facility %s", packagingFilmBatchId, facility));
    }

    return createOk(packagingFilmBatchTranslator.translatePackagingFilmBatch(packagingFilmBatch));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPerformedCultivationAction(Facility facility, UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);
    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (performedCultivationAction.getFacility() != facility) {
      return createBadRequest(String.format("Performed cultivation action with id %s doesn't belong to facility %s", performedCultivationActionId, facility));
    }

    return createOk(
        performedCultivationActionTranslator.translatePerformedCultivationAction(performedCultivationAction));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findProduct(Facility facility, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null || product.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(productsTranslator.translateProduct(product));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findProductionLine(Facility facility, UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);

    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (productionLine.getFacility() != facility) {
      return createBadRequest(String.format("Production line with id %s doesn't belong to facility %s", productionLineId, facility));
    }

    return createOk(productionLineTranslator.translateProductionLine(productionLine));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findSeedBatch(Facility facility, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);

    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (seedBatch.getSeed().getFacility() != facility) {
      return createBadRequest(String.format("Seed batch with id %s doesn't belong to facility %s", seedBatchId, facility));
    }

    return createOk(seedBatchesTranslator.translateSeedBatch(seedBatchController.findSeedBatch(seedBatchId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response findWastageReason(Facility facility, UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);

    if (wastageReason == null) {
      return createNotFound(String.format("Wastage reason with id %s not found!", wastageReasonId));
    }

    if (wastageReason.getFacility() != facility) {
      return createBadRequest(String.format("Wastage reason with id %s doesn't belong to facility %s", wastageReasonId, facility));
    }

    return createOk(wastageReasonsTranslator.translateWastageReason(wastageReason));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listCampaigns(Facility facility) {
    List<Campaign> translatedCampaigns = campaignController.list(facility).stream().map(campaignTranslator::translate).collect(Collectors.toList());
    return createOk(translatedCampaigns);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listCutPackings(Facility facility, Integer firstResult, Integer maxResults, UUID productId, String createdBeforeString, String createdAfterString) {
    fi.metatavu.famifarm.persistence.model.Product productToFilterBy = null;

    if (productId != null) {
      fi.metatavu.famifarm.persistence.model.Product existingProduct = productController.findProduct(productId);

      if (existingProduct == null) {
        return createNotFound(String.format("Product with id %s not found!", productId));
      }

      if (existingProduct.getFacility() != facility) {
        return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", productId, facility));
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

    List<fi.metatavu.famifarm.persistence.model.CutPacking> cutPackings = cutPackingController.list(facility, firstResult, maxResults, productToFilterBy, null, createdBefore, createdAfter);
    List<CutPacking> translatedCutPackings = cutPackings.stream().map(cutPackingTranslator::translate).collect(Collectors.toList());
    return createOk(translatedCutPackings);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listEvents(Facility facility, Integer firstResult, Integer maxResults, UUID productId, String createdAfter,
  String createdBefore, EventType eventType) {
    fi.metatavu.famifarm.persistence.model.Product product = productId != null ? productController.findProduct(productId) : null;
    if (product != null && product.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    OffsetDateTime createdBeforeTime = createdBefore != null ? OffsetDateTime.parse(createdBefore) : null;
    
    OffsetDateTime createdAfterTime = createdAfter != null ? OffsetDateTime.parse(createdAfter) : null;

    List<Event> result = eventController.listEventsRest(facility, product, createdAfterTime, createdBeforeTime, firstResult, eventType, maxResults).stream().map(this::translateEvent)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPackageSizes(Facility facility, Integer firstResult, Integer maxResults) {
    List<PackageSize> result = packageSizeController.listPackageSizes(facility, firstResult, maxResults).stream()
        .map(packageSizeTranslator::translatePackageSize).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPackagingFilmBatches(Facility facility, Integer firstResult, Integer maxResults, Boolean includePassive) {
    List<PackagingFilmBatch> result = packagingFilmBatchController.list(firstResult, maxResults, facility, includePassive).stream()
        .map(packagingFilmBatchTranslator::translatePackagingFilmBatch).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPerformedCultivationActions(Facility facility, Integer firstResult, Integer maxResults) {
    List<PerformedCultivationAction> result = performedCultivationActionsController
        .listPerformedCultivationActions(facility, firstResult, maxResults).stream()
        .map(performedCultivationActionTranslator::translatePerformedCultivationAction).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listProductionLines(Facility facility, Integer firstResult, Integer maxResults) {
    List<ProductionLine> result = productionLineController.listProductionLines(facility, firstResult, maxResults).stream()
        .map(productionLineTranslator::translateProductionLine).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listProducts(Facility facility, Integer firstResult, Integer maxResults, Boolean includeInActiveProducts, Boolean includeSubcontractorProducts, Boolean filterByEndProduct, Boolean filterByRawMaterials) {
    List<Product> result = productController.listProducts(facility, firstResult, maxResults, includeSubcontractorProducts, includeInActiveProducts, filterByEndProduct, filterByRawMaterials).stream()
        .map(productsTranslator::translateProduct).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listSeedBatches(Facility facility, Integer firstResult, Integer maxResults, Boolean isPassive) {
    Boolean active = null;
    if (isPassive != null) {
      active = !isPassive;
    }
    List<SeedBatch> result = seedBatchController.listSeedBatches(facility, firstResult, maxResults, active).stream()
        .map(seedBatchesTranslator::translateSeedBatch).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  public Response listWastageReasons(Facility facility, Integer firstResult, Integer maxResults) {
    List<WastageReason> result = wastageReasonsController.listWastageReasons(firstResult, maxResults, facility).stream()
        .map(wastageReasonsTranslator::translateWastageReason).collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response print(@Valid PrintData printData, Facility facility, String printerId) {
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
      this.logger.error("Failed to print qr code", e);
      return createInternalServerError(e.getMessage());
    }
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateCampaign(@Valid Campaign campaign, Facility facility, UUID campaignId) {
    HashMap<fi.metatavu.famifarm.persistence.model.Product, Integer> campaignProductsToCreate = new HashMap<>();
    List<CampaignProduct> restCampaignProducts = campaign.getProducts();

    fi.metatavu.famifarm.persistence.model.Campaign campaignToUpdate = campaignController.find(campaignId);
    if (campaignToUpdate == null) {
      return createNotFound(String.format("Campaign %s not found!", campaignId));
    }

    if (campaignToUpdate.getFacility() != facility) {
      return createBadRequest(String.format("Campaign %s doesn't belong to facility %s", campaignId, facility));
    }

    for (CampaignProduct campaignProduct : restCampaignProducts) {
      fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(campaignProduct.getProductId());
      if (product == null) {
        return createNotFound(String.format("Campaign product %s not found!", campaignProduct.getProductId()));
      }

      campaignProductsToCreate.put(product, campaignProduct.getCount());
    }

    fi.metatavu.famifarm.persistence.model.Campaign updatedCampaign = campaignController.update(campaignToUpdate, campaign.getName(), campaignProductsToCreate, getLoggerUserId());
    return createOk(campaignTranslator.translate(updatedCampaign));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response updateCutPacking(@Valid CutPacking cutPacking, Facility facility, UUID cutPackingId) {
    fi.metatavu.famifarm.persistence.model.CutPacking existingCutPacking = cutPackingController.find(cutPackingId);

    if (existingCutPacking == null || existingCutPacking.getProduct().getFacility() != facility) {
      return createNotFound(String.format("Cut packing with id %s not found!", cutPackingId));
    }

    try {
      fi.metatavu.famifarm.persistence.model.CutPacking updatedCutPacking = cutPackingController.update(
              facility,
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
  public Response updateEvent(Event body, Facility facility, UUID eventId) {
    fi.metatavu.famifarm.persistence.model.Event event = eventController.findEventById(eventId);
    if (event == null || event.getProduct().getFacility() != facility) {
      return createNotFound(String.format("Could not find event %s", eventId));
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(body.getProductId());
    if (product == null || product.getFacility() != facility) {
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
  public Response updatePackageSize(PackageSize body, Facility facility, UUID packageSizeId) {
    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController
        .findPackageSize(packageSizeId);
    if (packageSize == null || packageSize.getFacility() != facility) {
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
  public Response updatePackagingFilmBatch(PackagingFilmBatch packagingFilmBatch, Facility facility, UUID packagingFilmBatchId) {
    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch existingPackagingFilmBatch = packagingFilmBatchController.findById(packagingFilmBatchId);
    if (existingPackagingFilmBatch == null) {
      return createNotFound(String.format("Packaging film batch with id %s not found!", packagingFilmBatchId));
    }

    if (existingPackagingFilmBatch.getFacility() != facility) {
      return createBadRequest(String.format("Packaging film batch with id %s doesn't belong to facility %s", packagingFilmBatchId, facility));
    }

    fi.metatavu.famifarm.persistence.model.PackagingFilmBatch updatedPackagingFilmBatch = packagingFilmBatchController.update(existingPackagingFilmBatch, packagingFilmBatch, getLoggerUserId());
    return createOk(packagingFilmBatchTranslator.translatePackagingFilmBatch(updatedPackagingFilmBatch));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updatePerformedCultivationAction(PerformedCultivationAction body, Facility facility, UUID performedCultivationActionId) {
    fi.metatavu.famifarm.persistence.model.PerformedCultivationAction performedCultivationAction = performedCultivationActionsController
        .findPerformedCultivationAction(performedCultivationActionId);

    if (performedCultivationAction == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (performedCultivationAction.getFacility() != facility) {
      return createBadRequest(String.format("Performed cultivation action with id %s doesn't belong to facility %s", performedCultivationActionId, facility));
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
  public Response updateProduct(Product body, Facility facility, UUID productId) {
    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(productId);
    if (product == null || product.getFacility() != facility) {
      return createNotFound("Product not found");
    }

    List<fi.metatavu.famifarm.persistence.model.PackageSize> packageSizeList = new ArrayList<>();
    if (body.getDefaultPackageSizeIds() != null && !body.getDefaultPackageSizeIds().isEmpty()) {
      packageSizeList = body.getDefaultPackageSizeIds().stream().map(id -> packageSizeController.findPackageSize(id)).filter(packageSize -> packageSize.getFacility() == facility).collect(Collectors.toList());
    }

    LocalizedEntry name = createLocalizedEntry(body.getName());
    fi.metatavu.famifarm.persistence.model.Product productEntity = productController.updateProduct(
      product,
      name,
      packageSizeList,
      body.getIsSubcontractorProduct(),
      body.getActive(),
      body.getIsEndProduct(),
      body.getIsRawMaterial(),
      body.getSalesWeight(),
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
  public Response updateProductionLine(ProductionLine body, Facility facility, UUID productionLineId) {
    fi.metatavu.famifarm.persistence.model.ProductionLine productionLine = productionLineController
        .findProductionLine(productionLineId);

    if (productionLine == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (productionLine.getFacility() != facility) {
      return createBadRequest(String.format("Production line with id %s doesn't belong to facility %s", productionLineId, facility));
    }

    Integer defaultGutterHoleCount = body.getDefaultGutterHoleCount();
    String lineNumber = body.getLineNumber();

    return createOk(productionLineTranslator.translateProductionLine(productionLineController
        .updateProductionLine(productionLine, lineNumber, defaultGutterHoleCount, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateSeedBatch(SeedBatch body, Facility facility, UUID seedBatchId) {
    fi.metatavu.famifarm.persistence.model.SeedBatch seedBatch = seedBatchController.findSeedBatch(seedBatchId);

    if (seedBatch == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (seedBatch.getSeed().getFacility() != facility) {
      return createBadRequest(String.format("Seed batch with id %s doesn't belong to facility %s", seedBatchId, facility));
    }

    String code = body.getCode();
    OffsetDateTime time = body.getTime();
    UUID seedId = body.getSeedId();
    fi.metatavu.famifarm.persistence.model.Seed seed = seedsController.findSeed(seedId);
    if (seed.getFacility() != facility) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    boolean active = body.getActive() != null ? body.getActive() : Boolean.FALSE;

    return createOk(seedBatchController.updateSeedBatch(seedBatch, code, seed, time, active, getLoggerUserId()));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateStorageDiscard(StorageDiscard payload, Facility facility, UUID storageDiscardId) {
    fi.metatavu.famifarm.persistence.model.StorageDiscard foundStorageDiscard = storageDiscardController.findById(storageDiscardId);

    if (foundStorageDiscard == null) {
      return createNotFound(String.format("Storage discard with id %s not found!", storageDiscardId));
    }

    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(payload.getPackageSizeId());
    if (packageSize == null) {
      return createNotFound("Package size not found");
    }

    if (packageSize.getFacility() != facility) {
      return createBadRequest(String.format("Package size with id %s doesn't belong to facility %s", packageSize.getId(), facility));
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(payload.getProductId());
    if (product == null) {
      return createNotFound("Product not found");
    }

    if (product.getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", product.getId(), facility));
    }

    return createOk(storageDiscardTranslator.translateStorageDiscard(storageDiscardController.
        updateStorageDiscard(foundStorageDiscard, payload.getDiscardAmount(), payload.getDiscardDate(), product, packageSize, getLoggerUserId())));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updateWastageReason(WastageReason body, Facility facility, UUID wastageReasonId) {
    fi.metatavu.famifarm.persistence.model.WastageReason wastageReason = wastageReasonsController
        .findWastageReason(wastageReasonId);

    if (wastageReason == null) {
      return createNotFound(String.format("Wastage reason with id %s not found!", wastageReasonId));
    }

    if (wastageReason.getFacility() != facility) {
      return createBadRequest(String.format("Wastage reason with id %s doesn't belong to facility %s", wastageReasonId, facility));
    }

    LocalizedEntry reason = createLocalizedEntry(body.getReason());
    UUID loggerUserId = getLoggerUserId();

    return createOk(wastageReasonsTranslator
        .translateWastageReason(wastageReasonsController.updateWastageReason(wastageReason, reason, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  public Response getReport(Facility facility, String typeParam, String fromTime, String toTime, String reportFormat) {
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
      report.createReport(output, facility, getLocale(), parameters);
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
  public Response getStorageDiscard(Facility facility, UUID storageDiscardId) {

    fi.metatavu.famifarm.persistence.model.StorageDiscard foundStorageDiscard = storageDiscardController.findById(storageDiscardId);
    if (foundStorageDiscard == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.famifarm.persistence.model.Product product = productController.findProduct(foundStorageDiscard.getProduct().getId());
    if (product.getFacility() != facility) {
      return createBadRequest(String.format("Product with id %s doesn't belong to facility %s", product.getId(), facility));
    }

    fi.metatavu.famifarm.persistence.model.PackageSize packageSize = packageSizeController.findPackageSize(foundStorageDiscard.getPackageSize().getId());
    if (packageSize.getFacility() != facility) {
      return createBadRequest(String.format("Package size with id %s doesn't belong to facility %s", packageSize.getId(), facility));
    }

    return createOk(storageDiscardTranslator.translateStorageDiscard(foundStorageDiscard));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response createDraft(Draft body, Facility facility) {
    UUID creatorId = getLoggerUserId();

    draftController.deleteDraftsByCreatorIdAndType(creatorId, body.getType(), facility);
    return createOk(
        draftTranslator.translateDraft(draftController.createDraft(body.getType(), body.getData(), facility, creatorId))
    );
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER, Roles.WORKER })
  @Transactional
  public Response deleteDraft(Facility facility, UUID draftId) {
    fi.metatavu.famifarm.persistence.model.Draft draft = draftController.findDraftById(draftId);
    if (draft == null || draft.getFacility() != facility) {
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
  public Response listDrafts(Facility facility, UUID userId, String type) {
    fi.metatavu.famifarm.persistence.model.Draft draft = draftController.findDraftByCreatorIdAndType(userId, type, facility);
    if (draft != null) {
      return createOk(List.of(draft));
    }

    return createOk(Collections.emptyList());
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response createPest(Pest body, Facility facility) {
    LocalizedEntry name = createLocalizedEntry(body.getName());
    UUID loggerUserId = getLoggerUserId();

    return createOk(pestsTranslator.translatePest(pestsController.createPest(name, facility, loggerUserId)));
  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response deletePest(Facility facility, UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);

    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (pest.getFacility() != facility) {
      return createBadRequest(String.format("Pest with id %s doesn't belong to facility %s", pestId, facility));
    }

    pestsController.deletePest(pest);

    return createNoContent();
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response findPest(Facility facility, UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);

    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (pest.getFacility() != facility) {
      return createBadRequest(String.format("Pest with id %s doesn't belong to facility %s", pestId, facility));
    }

    return createOk(pestsTranslator.translatePest(pest));
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPests(Facility facility, Integer firstResult, Integer maxResults) {
    List<Pest> result = pestsController.listPests(facility, firstResult, maxResults).stream().map(pestsTranslator::translatePest)
        .collect(Collectors.toList());

    return createOk(result);
  }

  @Override
  @RolesAllowed({ Roles.WORKER, Roles.ADMIN, Roles.MANAGER })
  public Response listPrinters(Facility facility) {
    try {
      return createOk(printingController.getPrinters());
    } catch (Exception e) {
      return createInternalServerError(e.getMessage());
    }

  }

  @Override
  @RolesAllowed({ Roles.ADMIN, Roles.MANAGER })
  @Transactional
  public Response updatePest(Pest body, Facility facility, UUID pestId) {
    fi.metatavu.famifarm.persistence.model.Pest pest = pestsController.findPest(pestId);

    if (pest == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (pest.getFacility() != facility) {
      return createBadRequest(String.format("Pest with id %s doesn't belong to facility %s", pestId, facility));
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
   * @param product     product
   * @param startTime start time
   * @param endTime   end time
   * @param additionalInformation additional information
   * @param eventDataObject event data
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
   * @param product         product
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
   * @param product   product
   * @param startTime start time
   * @param endTime   end time
   * @param additionalInformation additional information
   * @param eventDataObject event data object
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
   * @param product         product
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
   * @param product   product
   * @param startTime start time
   * @param endTime   end time
   * @param eventDataObject event data object
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
   * @param product         product
   * @param startTime       start time
   * @param endTime         end time
   * @param additionalInformation additional information
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
   * @param product   product
   * @param startTime start time
   * @param endTime   end time
   * @param additionalInformation additional information
   * @param eventDataObject event data object
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
    List<HarvestBasket> baskets = eventData.getBaskets();
    OffsetDateTime sowingTime = eventData.getSowingDate();
    Integer cuttingHeight = eventData.getCuttingHeight();

    HarvestEventType harvestType = eventData.getType();
    HarvestEvent event = harvestEventController.createHarvestEvent(product, startTime, endTime, harvestType,
      productionLine, sowingTime, additionalInformation, amount, gutterHoleCount, cuttingHeight, baskets, creatorId);

    return createOk(harvestEventTranslator.translateEvent(event));
  }

  /**
   * Updates harvest event
   * 
   * @param event           event
   * @param product         product
   * @param startTime       start time
   * @param endTime         end time
   * @param additionalInformation additionalInformation
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
      endTime, harvestType, productionLine, sowingTime, eventData.getGutterCount(), eventData.getGutterHoleCount(),
      additionalInformation, eventData.getCuttingHeight(), eventData.getBaskets(), creatorId);

    return createOk(harvestEventTranslator.translateEvent(updatedEvent));
  }

  /**
   * Creates new planting event
   * 
   * @param product   product
   * @param startTime start time
   * @param endTime   end time
   * @param additionalInformation additional information
   * @param eventDataObject event data object
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
   * @param product         product
   * @param startTime       start time
   * @param endTime         end time
   * @param additionalInformation additional information
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
   * @param product   product
   * @param startTime start time
   * @param endTime   end time
   * @param additionalInformation additional information
   * @param eventDataObject event data object
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
   * @param product         product
   * @param startTime       start time
   * @param endTime         end time
   * @param additionalInformation additional information
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
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper.readValue(objectMapper.writeValueAsBytes(object), targetClass);
  }

}
