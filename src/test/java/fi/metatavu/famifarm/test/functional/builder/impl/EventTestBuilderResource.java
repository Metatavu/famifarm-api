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
import fi.metatavu.famifarm.client.model.Event.TypeEnum;
import fi.metatavu.famifarm.client.model.HarvestEventData;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.client.model.PlantingEventData;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.SowingEventData;
import fi.metatavu.famifarm.client.model.TableSpreadEventData;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.rest.model.CultivationObservationEventData;
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
   * @param gutterNumber gutter number
   * @param productionLine production line id
   * @param seedBatch seed batch
   * @return created event
   */
  public Event createSowing(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double amount, CellType cellType, Integer gutterNumber, ProductionLine productionLine, SeedBatch seedBatch) {
    SowingEventData data = createSowingEventData(amount, cellType, gutterNumber, productionLine, seedBatch);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(TypeEnum.SOWING);
    
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
    event.setType(TypeEnum.TABLE_SPREAD);
    
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
  public Event createCultivationObservation(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double luminance, String pests, Double weight, List<PerformedCultivationAction> performedActions) {
    CultivationObservationEventData data = createCultivationObservationEventData(luminance, pests, weight, performedActions);

    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(TypeEnum.CULTIVATION_OBSERVATION);
    
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
    event.setType(TypeEnum.HARVEST);
    
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
   * @param trayCount tray count
   * @param workerCount worker count
   * @return created event
   */
  public Event createPlanting(Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Integer gutterCount, Integer gutterNumber, ProductionLine productionLine, Integer trayCount, Integer workerCount) {
    PlantingEventData data = createPlantingEventData(gutterCount, gutterNumber, productionLine, trayCount, workerCount);
    
    Event event = new Event();
    event.setBatchId(batch != null ? batch.getId() : null);
    event.setData(data);
    event.setEndTime(endTime);
    event.setStartTime(startTime);
    event.setType(TypeEnum.PLANTING);
    
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
   * Asserts create status fails with given status code
   * 
   * @param batch batch
   * @param startTime event start time
   * @param endTime event end time
   * @param amount amount
   * @param cellType cell type
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param seedBatch seed batch
   */
  public void assertCreateFailStatus(int expectedStatus, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double amount, CellType cellType, Integer gutterNumber, ProductionLine productionLine, SeedBatch seedBatch) {
    try {
      SowingEventData data = createSowingEventData(amount, cellType, gutterNumber, productionLine, seedBatch);
      
      Event event = new Event();
      event.setBatchId(batch != null ? batch.getId() : null);
      event.setData(data);
      event.setEndTime(endTime);
      event.setStartTime(startTime);
      event.setType(TypeEnum.SOWING);
      
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
   * Creates event data object
   * 
   * @param amount amount
   * @param cellType cell type
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param seedBatch seed batch
   * @return
   */
  private SowingEventData createSowingEventData(Double amount, CellType cellType, Integer gutterNumber, ProductionLine productionLine, SeedBatch seedBatch) {
    SowingEventData data = new SowingEventData();
    data.setAmount(amount);
    data.setCellType(cellType);
    data.setGutterNumber(gutterNumber);
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
  private CultivationObservationEventData createCultivationObservationEventData(Double luminance, String pests, Double weight, List<PerformedCultivationAction> performedActions) {
    CultivationObservationEventData data = new CultivationObservationEventData();
    data.setLuminance(luminance);
    data.setPerformedActionIds(performedActions.stream().map(PerformedCultivationAction::getId).collect(Collectors.toList()));
    data.setPests(pests);
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
   * @param gutterNumber gutter number
   * @param productionLine production line
   * @param trayCount tray count
   * @param workerCount worker count
   * @return
   */
  private PlantingEventData createPlantingEventData(Integer gutterCount, Integer gutterNumber, ProductionLine productionLine, Integer trayCount, Integer workerCount) {
    PlantingEventData data = new PlantingEventData();
    data.setGutterCount(gutterCount);
    data.setGutterNumber(gutterNumber);
    data.setProductionLineId(productionLine != null ? productionLine.getId() : null);
    data.setTrayCount(trayCount);
    data.setWorkerCount(workerCount);
    return data;
  }
}
