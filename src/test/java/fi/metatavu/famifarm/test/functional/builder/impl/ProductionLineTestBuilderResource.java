package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.json.JSONException;
import feign.FeignException;
import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.ProductionLinesApi;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for production lines
 * 
 * @author Ville Koivukangas
 */
public class ProductionLineTestBuilderResource extends AbstractTestBuilderResource<ProductionLine, ProductionLinesApi> {

  private final HashMap<UUID, Facility> lineFacilityMap = new HashMap<>();

  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public ProductionLineTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new production line
   *
   * @return created production line
   */
  public ProductionLine create(String lineNumber, Integer defaultGutterHoleCount, Facility facility) {
    ProductionLine productionLine = new ProductionLine();
    productionLine.setLineNumber(lineNumber);
    productionLine.setDefaultGutterHoleCount(defaultGutterHoleCount);
    ProductionLine created = getApi().createProductionLine(productionLine, facility);
    lineFacilityMap.put(created.getId(), facility);
    return addClosable(created);
  }

  /**
   * Finds a production line
   * 
   * @param productionLineId production line id
   * @return found production line
   */
  public ProductionLine findProductionLine(UUID productionLineId, Facility facility) {
    return getApi().findProductionLine(facility, productionLineId);
  }

  /**
   * Lists production lines
   *
   * @return production lines
   */
  public List<ProductionLine> listProductionLines(Facility facility) {
    return getApi().listProductionLines(facility, Collections.emptyMap());
  }

  /**
   * Updates a production line into the API
   * 
   * @param body body payload
   */
  public ProductionLine updateProductionLine(ProductionLine body, Facility facility) {
    return getApi().updateProductionLine(body, facility, body.getId());
  }
  
  /**
   * Deletes a production line from the API
   * 
   * @param productionLine production line to be deleted
   */
  public void delete(ProductionLine productionLine, Facility facility) {
    getApi().deleteProductionLine(facility, productionLine.getId());
    removeClosable(closable -> !closable.getId().equals(productionLine.getId()));
  }
  
  /**
   * Asserts seed count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected, Facility facility) {
    assertEquals(expected, getApi().listProductionLines(facility, Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID productionLineId, Facility facility) {
    try {
      getApi().findProductionLine(facility, productionLineId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param productionLine productionLine
   */
  public void assertUpdateFailStatus(int expectedStatus, ProductionLine productionLine, Facility facility) {
    try {
      getApi().updateProductionLine(productionLine, facility, productionLine.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param productionLine
   */
  public void assertDeleteFailStatus(int expectedStatus, ProductionLine productionLine, Facility facility) {
    try {
      getApi().deleteProductionLine(facility, productionLine.getId());
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
  public void assertListFailStatus(int expectedStatus, Facility facility) {
    try {
      getApi().listProductionLines(facility, Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual production line equals expected seed when both are serialized into JSON
   * 
   * @param expected expected status code
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertProductionLinesEqual(ProductionLine expected, ProductionLine actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(ProductionLine productionLine) {
    getApi().deleteProductionLine(lineFacilityMap.get(productionLine.getId()), productionLine.getId());
  }

}
