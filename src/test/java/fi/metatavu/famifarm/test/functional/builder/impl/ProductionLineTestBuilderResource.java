package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.ProductionLinesApi;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for production lines
 * 
 * @author Ville Koivukangas
 */
public class ProductionLineTestBuilderResource extends AbstractTestBuilderResource<ProductionLine, ProductionLinesApi> {
  
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
   * @param name name
   * @return created production line
   */
  public ProductionLine create(Integer lineNumber) {
    ProductionLine productionLine = new ProductionLine();
    productionLine.setLineNumber(lineNumber);
    return addClosable(getApi().createProductionLine(productionLine));
  }

  /**
   * Finds a production line
   * 
   * @param productionLineId production line id
   * @return found production line
   */
  public ProductionLine findProductionLine(UUID productionLineId) {
    return getApi().findProductionLine(productionLineId);
  }

  /**
   * Updates a production line into the API
   * 
   * @param body body payload
   */
  public ProductionLine updateProductionLine(ProductionLine body) {
    return getApi().updateProductionLine(body, body.getId());
  }
  
  /**
   * Deletes a production line from the API
   * 
   * @param productionLine production line to be deleted
   */
  public void delete(ProductionLine productionLine) {
    getApi().deleteProductionLine(productionLine.getId());  
    removeClosable(closable -> !closable.getId().equals(productionLine.getId()));
  }
  
  /**
   * Asserts seed count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listProductionLines(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID productionLineId) {
    try {
      getApi().findProductionLine(productionLineId);
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
  public void assertUpdateFailStatus(int expectedStatus, ProductionLine productionLine) {
    try {
      getApi().updateProductionLine(productionLine, productionLine.getId());
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
  public void assertDeleteFailStatus(int expectedStatus, ProductionLine productionLine) {
    try {
      getApi().deleteProductionLine(productionLine.getId());
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
      getApi().listProductionLines(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual production line equals expected seed when both are serialized into JSON
   * 
   * @param expectedStatus expected status code
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertProductionLinesEqual(ProductionLine expected, ProductionLine actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(ProductionLine productionLine) {
    getApi().deleteProductionLine(productionLine.getId());  
  }

}
