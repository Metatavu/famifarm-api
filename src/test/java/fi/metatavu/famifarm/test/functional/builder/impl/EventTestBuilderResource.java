package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.EventsApi;
import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.EventType;
import fi.metatavu.famifarm.client.model.HarvestEventData;
import fi.metatavu.famifarm.client.model.PackageSize;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.client.model.Pest;
import fi.metatavu.famifarm.client.model.PlantingEventData;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.SowingEventData;
import fi.metatavu.famifarm.client.model.TableSpreadEventData;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.client.model.WastageEventData;
import fi.metatavu.famifarm.client.model.WastageReason;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;
import fi.metatavu.famifarm.rest.model.PackingEventData;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

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
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param amount amount
   * @param cellType cell type
   * @param productionLine production line id
   * @param seedBatch seed batch
   * @return created event
   */
  public Event createSowing(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, CellType cellType, ProductionLine productionLine, SeedBatch seedBatch) {
    SowingEventData data = createSowingEventData(amount, cellType, productionLine, seedBatch);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.SOWING);
    
    return addClosable(getApi().createEvent(event));
  }
  
  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param location 
   * @param tableCount 
   * @return created event
   */
  public Event createTableSpread(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, String location, Integer tableCount) {
    TableSpreadEventData data = createTableSpreadEventData(location, tableCount);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.TABLE_SPREAD);
    
    return addClosable(getApi().createEvent(event));
  }

  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param luminance luminance 
   * @param pests pests 
   * @param weight weight 
   * @param performedActions performedActions 
   * @return created event
   */
  public Event createCultivationObservation(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double luminance, List<Pest> pests, Double weight, List<PerformedCultivationAction> performedActions) {
    CultivationObservationEventData data = createCultivationObservationEventData(luminance, pests, weight, performedActions);

    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.CULTIVATION_OBSERVATION);
    
    return addClosable(getApi().createEvent(event));
  }

  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param productionLine production line
   * @param team team
   * @param type type
   * @return created event
   */
  public Event createHarvest(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, ProductionLine productionLine, Team team, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum type) {
    HarvestEventData data = createHarvestEventData(productionLine, team, type);

    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.HARVEST);
    
    return addClosable(getApi().createEvent(event));
  }
  
  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param gutterCount gutter count
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param cellCount cell count
   * @param workerCount worker count
   * @return created event
   */
  public Event createPlanting(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer gutterCount, Integer gutterNumber, ProductionLine productionLine, Integer cellCount, Integer workerCount) {
    PlantingEventData data = createPlantingEventData(gutterCount, gutterNumber, productionLine, cellCount, workerCount);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.PLANTING);
    
    return addClosable(getApi().createEvent(event));
  }

  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param amount amount
   * @param wastageReason wastege reason
   * @param additionalInformation additional information
   * @param phase phase
   * @return created event
   */
  public Event createWastage(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, WastageReason wastageReason, String additionalInformation, EventType phase, UUID productionLineId) {
    
    WastageEventData data = createWastageEventData(amount, wastageReason, phase, productionLineId);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.WASTAGE);
    event.setAdditionalInformation(additionalInformation);
    
    return addClosable(getApi().createEvent(event));
  }

  /**
   * Creates new event
   * 
   * @param batch batch
   * @param startTime start time
   * @param endTime end time
   * @param packageSize package size
   * @param packedAmount packed amount
   * @return created event
   */
  public Event createPacking(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, PackageSize packageSize, Integer packedAmount) {
    PackingEventData data = createPackingEventData(packageSize, packedAmount);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(EventType.PACKING);
    
    return addClosable(getApi().createEvent(event));
  }
  
  /**
   * Finds an Event
   * 
   * @param eventId Event id
   * @return found Event
   */
  public Event findEvent(UUID eventId) {
    return getApi().findEvent(eventId);
  }

  /**
   * Updates an Event into the API
   * 
   * @param body body payload
   */
  public Event updateEvent(Event body) {
    return getApi().updateEvent(body, body.getId());
  }
  
  /**
   * Deletes an Event from the API
   * 
   * @param event Event to be deleted
   */
  public void delete(Event event) {
    getApi().deleteEvent(event.getId());  
    removeClosable(closable -> !closable.getId().equals(event.getId()));
  }
  
  /**
   * Asserts Event count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listEvents(Collections.emptyMap()).size());
  }

  /**
   * Asserts Event count within the system
   *
   * @param batchId batch id to filter list with
   * @param expected expected count
   */
  public void assertCount(UUID batchId, int expected) {
    assertEquals(expected, getApi().listEvents(null, null, batchId).size());
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param amount amount
   * @param cellType cell type
   * @param productionLine production line
   * @param seedBatch seed batch
   */
  public void assertCreateFailStatus(int expectedStatus, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer amount, CellType cellType, ProductionLine productionLine, SeedBatch seedBatch) {
    try {
      SowingEventData data = createSowingEventData(amount, cellType, productionLine, seedBatch);
      
      Event event = new Event();
      event.setBatchId(batch != null ? batch.getId() : null);
      event.setData(data);
      event.setEndTime(endTime);
      event.setStartTime(startTime);
      event.setType(EventType.SOWING);
      
      getApi().createEvent(event);
      
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param eventId event id
   */
  public void assertFindFailStatus(int expectedStatus, UUID eventId) {
    try {
      getApi().findEvent(eventId);
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
      getApi().updateEvent(event, event.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, Event event) {
    try {
      getApi().deleteEvent(event.getId());
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
      getApi().listEvents(Collections.emptyMap());
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
    getApi().deleteEvent(event.getId());  
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
   * Creates event data object
   * 
   * @param amount amount
   * @param cellType cell type
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param seedBatch seed batch
   * @return
   */
  private SowingEventData createSowingEventData(Integer amount, CellType cellType, ProductionLine productionLine, SeedBatch seedBatch) {
    SowingEventData data = new SowingEventData();
    data.setAmount(amount);
    data.setCellType(cellType);
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setSeedBatchId(seedBatch != null ? seedBatch.getId() : null);
    return data;
  }

  /**
   * Creates table spread event data object
   * @param location location
   * @param tableCount table count
   * @return
   */
  private TableSpreadEventData createTableSpreadEventData(String location, Integer tableCount) {
    TableSpreadEventData data = new TableSpreadEventData();
    data.setLocation(location);
    data.setTableCount(tableCount);
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
   * @param team team
   * @param type 
   * @return harvest event data
   */
  private HarvestEventData createHarvestEventData(ProductionLine productionLine, Team team, fi.metatavu.famifarm.client.model.HarvestEventData.TypeEnum type) {
    HarvestEventData data = new HarvestEventData();
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setTeamId(team != null ? team.getId() : null);
    data.setType(type);
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
  private PlantingEventData createPlantingEventData(Integer gutterCount, Integer gutterSize, ProductionLine productionLine, Integer cellCount, Integer workerCount) {
    PlantingEventData data = new PlantingEventData();
    data.setGutterCount(gutterCount);
    data.setGutterSize(gutterSize);
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setCellCount(cellCount);
    data.setWorkerCount(workerCount);
    return data;
  }
  
  /**
   * Creates event data object
   *
   * @param packageSize 
   * @param packedAmount 
   * @return event data
   */
  private PackingEventData createPackingEventData(PackageSize packageSize, Integer packedAmount) {
    PackingEventData data = new PackingEventData();
    data.setPackageSizeId(packageSize != null ? packageSize.getId() : null);
    data.setPackedAmount(packedAmount);
    return data;
  }

}
