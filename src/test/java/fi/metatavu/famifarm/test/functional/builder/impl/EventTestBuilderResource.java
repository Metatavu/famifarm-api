package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.EventsApi;
import fi.metatavu.famifarm.client.model.Batch;
import fi.metatavu.famifarm.client.model.CellType;
import fi.metatavu.famifarm.client.model.Event;
import fi.metatavu.famifarm.client.model.Event.TypeEnum;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.SeedBatch;
import fi.metatavu.famifarm.client.model.SowingEventData;
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
  public void assertCreateFailStatus(int expectedStatus, Batch batch, OffsetDateTime startTime, OffsetDateTime endTime, Double amount, CellType cellType, Integer gutterNumber, ProductionLine productionLine, SeedBatch seedBatch) 
  {
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
   * Creates sowing event data object
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

}
