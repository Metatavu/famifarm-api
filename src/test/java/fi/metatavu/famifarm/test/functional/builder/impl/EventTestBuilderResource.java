package fi.metatavu.famifarm.test.functional.builder.impl;

import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.EventsApi;
import fi.metatavu.famifarm.client.model.*;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;
import org.json.JSONException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EventTestBuilderResource  extends AbstractTestBuilderResource<Event, EventsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public EventTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new event
   * 
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param amount amount
   * @param productionLine production line id
   * @param seedBatches seed batch
   * @return created event
   */
  public Event createSowing(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, ProductionLine productionLine, List<SeedBatch> seedBatches, Facility facility) {
    SowingEventData data = createSowingEventData(amount, productionLine, seedBatches);
    
    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.SOWING);
    
    return addClosable(getApi().createEvent(event, facility));
  }
  
  /**
   * Creates new event
   * 
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param tableCount table count
   * @return created event
   */
  public Event createTableSpread(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer tableCount) {
    TableSpreadEventData data = createTableSpreadEventData(tableCount);
    
    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.TABLE_SPREAD);
    
    return addClosable(getApi().createEvent(event, Facility.JOROINEN));
  }

  /**
   * Creates new event
   * 
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param luminance luminance 
   * @param pests pests 
   * @param weight weight 
   * @param performedActions performedActions 
   * @return created event
   */
  public Event createCultivationObservation(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Double luminance, List<Pest> pests, Double weight, List<PerformedCultivationAction> performedActions) {
    CultivationObservationEventData data = createCultivationObservationEventData(luminance, pests, weight, performedActions);

    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.CULTIVATION_OBSERVATION);
    
    return addClosable(getApi().createEvent(event, Facility.JOROINEN));
  }

  /**
   * Creates new event
   * 
   * @param product product
   * @param amount amount
   * @param gutterHoleCount  gutter hole count
   * @param cuttingHeight cuttingHeight
   * @param startTime event start time
   * @param endTime event end time
   * @param productionLine production line
   * @param sowingDate sowingDate
   * @param type type
   * @param baskets baskets list
   * @return created event
   */
  public Event createHarvest(
    Product product,
    Integer amount,
    Integer gutterHoleCount,
    Integer cuttingHeight,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    ProductionLine productionLine,
    OffsetDateTime sowingDate,
    HarvestEventType type,
    List<HarvestBasket> baskets
  ) {
    HarvestEventData data = createHarvestEventData(productionLine, sowingDate, amount, gutterHoleCount, cuttingHeight, type, baskets);

    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.HARVEST);
    
    return addClosable(getApi().createEvent(event, Facility.JOROINEN));
  }

  /**
   * Creates new event
   *
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param productionLine production line
   * @param sowingDate sowingDate
   * @param type type
   * @param baskets baskets list
   * @return created event
   */
  public Event createHarvest(Product product, Integer amount, Integer gutterHoleCount, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, OffsetDateTime sowingDate, HarvestEventType type, List<HarvestBasket> baskets, Facility facility) {
    HarvestEventData data = createHarvestEventData(productionLine, sowingDate, amount, gutterHoleCount, type, baskets);

    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.HARVEST);

    return addClosable(getApi().createEvent(event, facility));
  }
  
  /**
   * Creates new event
   * 
   * @param product product
   * @param startTime start time
   * @param endTime end time
   * @param gutterCount gutter count
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param cellCount cell count
   * @param workerCount worker count
   * @return created event
   */
  public Event createPlanting(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer gutterCount, Integer gutterNumber, ProductionLine productionLine, OffsetDateTime sowingDate, Integer cellCount, Integer workerCount, Facility facility) {
    PlantingEventData data = createPlantingEventData(gutterCount, gutterNumber, productionLine, sowingDate, cellCount, workerCount);
    
    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.PLANTING);
    
    return addClosable(getApi().createEvent(event, facility));
  }

  /**
   * Creates new event
   * 
   * @param product product
   * @param startTime start time
   * @param endTime end time
   * @param amount amount
   * @param wastageReason wastage reason
   * @param additionalInformation additional information
   * @param phase phase
   * @return created event
   */
  public Event createWastage(Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, WastageReason wastageReason, String additionalInformation, EventType phase, UUID productionLineId) {
    
    WastageEventData data = createWastageEventData(amount, wastageReason, phase, productionLineId);
    
    Event event = new Event();
    event.setProductId(product != null ? product.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.WASTAGE);
    event.setAdditionalInformation(additionalInformation);
    
    return addClosable(getApi().createEvent(event, Facility.JOROINEN));
  }


  /**
   * Creates new event
   *
   * @param product product
   * @param startTime start time
   * @param endTime end time
   * @param amount amount
   * @param wastageReason wastage reason
   * @param additionalInformation additional information
   * @param phase phase
   * @return created event
   */
  public Event createWastage(UUID productId, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, WastageReason wastageReason, String additionalInformation, EventType phase, UUID productionLineId, Facility facility) {
    WastageEventData data = createWastageEventData(amount, wastageReason, phase, productionLineId);

    Event event = new Event();
    event.setProductId(productId);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.WASTAGE);
    event.setAdditionalInformation(additionalInformation);

    return addClosable(getApi().createEvent(event, facility));
  }
  
  /**
   * Finds an Event
   * 
   * @param eventId Event id
   * @return found Event
   */
  public Event findEvent(UUID eventId) {
    return getApi().findEvent(Facility.JOROINEN, eventId);
  }

  /**
   * Updates an Event into the API
   *
   * @param body body payload
   */
  public Event updateEvent(Event body) {
    return getApi().updateEvent(body, Facility.JOROINEN, body.getId());
  }
  
  /**
   * Deletes an Event from the API
   * 
   * @param event Event to be deleted
   */
  public void delete(Event event) {
    try {
      getApi().deleteEvent(Facility.JOROINEN, event.getId());
    } catch (FeignException e) {
      getApi().deleteEvent(Facility.JUVA, event.getId());
    }

    removeClosable(closable -> !closable.getId().equals(event.getId()));
  }

  /**
   * Asserts Event count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listEvents(Facility.JOROINEN, Collections.emptyMap()).size());
  }

  /**
   * Asserts Event count within the system
   *
   * @param productId batch id to filter list with
   * @param eventType event type
   * @param expected expected count
   */
  public void assertCount(UUID productId, Facility facility, EventType eventType, int expected) {
    assertEquals(expected, getApi().listEvents(facility, null, null, productId, null, null, eventType).size());
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param product product
   * @param startTime event start time
   * @param endTime event end time
   * @param amount amount
   * @param productionLine production line
   * @param seedBatches seed batch
   */
  public void assertCreateFailStatus(int expectedStatus, Facility facility, Product product, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, ProductionLine productionLine, List<SeedBatch> seedBatches) {
    try {
      SowingEventData data = createSowingEventData(amount, productionLine, seedBatches);
      
      Event event = new Event();
      event.setProductId(product != null ? product.getId() : null);
      event.setData(data);
      event.setEndTime(endTime);
      event.setStartTime(startTime);
      event.setType(EventType.SOWING);
      
      getApi().createEvent(event, facility);
      
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts find status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param eventId        event id
   * @param facility       facility
   */
  public void assertFindFailStatus(int expectedStatus, UUID eventId, Facility facility) {
    try {
      getApi().findEvent(facility, eventId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param event event
   */
  public void assertUpdateFailStatus(int expectedStatus, Event event) {
    try {
      getApi().updateEvent(event, Facility.JOROINEN, event.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param event event
   */
  public void assertDeleteFailStatus(int expectedStatus, Event event, Facility facility) {
    try {
      getApi().deleteEvent(facility, event.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts list status fails with given status code
   *
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listEvents(Facility.JOROINEN, Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus, Facility facility, UUID productId) {
    try {
      getApi().listEvents(facility, null, null, productId, null, null, null);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual event equals expected event when both are serialized into JSON
   * 
   * @param expected expected event
   * @param actual actual event
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertEventsEqual(Event expected, Event actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Event event) {
    try {
      getApi().deleteEvent(Facility.JOROINEN, event.getId());
    } catch (FeignException e) {
      getApi().deleteEvent(Facility.JUVA, event.getId());
    }
  }
  
  /**
   * Creates wastage event data object
   * 
   * @param amount amount
   * @param wastageReason wastage reason
   * @param phase phase
   * @return created wastage event data object
   */
  public WastageEventData createWastageEventData(Integer amount, WastageReason wastageReason, EventType phase, UUID productionLineId) {
    WastageEventData data = new WastageEventData();
    data.setAmount(amount);
    data.setReasonId(wastageReason.getId());
    data.setPhase(phase);
    data.setProductionLineId(productionLineId);
    
    return data;
  }

  /**
   * Parses harvest event data
   *
   * @param event harvest event
   * @return parsed harvest event data object
   */
  public HarvestEventData readHarvestEventData(Event event) throws IOException {
    return getObjectMapper().readValue(getObjectMapper().writeValueAsBytes(event.getData()), HarvestEventData.class);
  }

  /**
   * Creates event data object
   * 
   * @param amount amount
   * @param productionLine production line
   * @return
   */
  private SowingEventData createSowingEventData(Integer amount, ProductionLine productionLine, List<SeedBatch> seedBatches) {
    SowingEventData data = new SowingEventData();
    data.setAmount(amount);
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setSeedBatchIds(seedBatches.stream().map(SeedBatch::getId).collect(Collectors.toList()));
    return data;
  }

  /**
   * Creates table spread event data object
   * @param trayCount tray count
   * @return
   */
  private TableSpreadEventData createTableSpreadEventData(Integer trayCount) {
    TableSpreadEventData data = new TableSpreadEventData();
    // TODO: Remove location
    data.trayCount(trayCount);
    return data;
  }

  /**
   * Creates cultivation observation event data object
   * @param luminance 
   * @param pests 
   * @param weight 
   * @param performedActions 
   * 
   * @return created event data
   */
  private CultivationObservationEventData createCultivationObservationEventData(Double luminance, List<Pest> pests, Double weight, List<PerformedCultivationAction> performedActions) {
    CultivationObservationEventData data = new CultivationObservationEventData();
    data.setLuminance(luminance);
    data.setPerformedActionIds(performedActions.stream().map(PerformedCultivationAction::getId).collect(Collectors.toList()));
    data.setPestIds(pests.stream().map(Pest::getId).collect(Collectors.toList()));
    data.setWeight(weight);
    return data;
  }

  /**
   * Creates event data object
   * @param productionLine production line
   * @param gutterCount gutterCount
   * @param type type of event
   * @param gutterHoleCount gutter hole count
   * @param cuttingHeight cutting height
   * @param sowingDate sowing date
   * @param baskets number of baskets
   * @return harvest event data
   */
  private HarvestEventData createHarvestEventData(
    ProductionLine productionLine,
    OffsetDateTime sowingDate,
    Integer gutterCount,
    Integer gutterHoleCount,
    Integer cuttingHeight,
    HarvestEventType type,
    List<HarvestBasket> baskets
  ) {
    HarvestEventData data = new HarvestEventData();
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setGutterCount(gutterCount);
    data.setGutterHoleCount(gutterHoleCount);
    data.setSowingDate(sowingDate);
    data.setCuttingHeight(cuttingHeight);
    data.setType(type);
    data.setBaskets(baskets);
    return data;
  }
  
  /**
   * Creates event data object
   * 
   * @param gutterCount gutter count
   * @param gutterSize gutter size
   * @param productionLine production line
   * @param cellCount cell count
   * @param workerCount worker count
   * @return
   */
  private PlantingEventData createPlantingEventData(Integer gutterCount, Integer gutterSize, ProductionLine productionLine, OffsetDateTime sowingDate, Integer cellCount, Integer workerCount) {
    PlantingEventData data = new PlantingEventData();
    data.setGutterCount(gutterCount);
    data.setGutterHoleCount(gutterSize);
    data.setSowingDate(sowingDate);
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setTrayCount(cellCount);
    data.setWorkerCount(workerCount);
    return data;
  }

}
