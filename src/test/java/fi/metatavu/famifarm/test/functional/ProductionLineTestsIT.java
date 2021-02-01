package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;

@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class ProductionLineTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      ProductionLine productionLine = builder.admin().productionLines().create("1", 100);
      assertNotNull(productionLine);
    }
  }

  @Test
  public void testFindProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().assertFindFailStatus(404, UUID.randomUUID());
      ProductionLine createdProductionLine = builder.admin().productionLines().create("1", 30);
      ProductionLine foundProductionLine = builder.admin().productionLines().findProductionLine(createdProductionLine.getId());
      assertEquals(createdProductionLine.getId(), foundProductionLine.getId());
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, foundProductionLine);
      builder.admin().productionLines().delete(foundProductionLine);
    }
  }

  @Test
  public void testListProductionLines() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().create("1", 7);
      builder.admin().productionLines().assertCount(1);
      builder.admin().productionLines().create("2",  7);
      builder.admin().productionLines().assertCount(2);
    }
  }

  @Test
  public void testUpdateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {

      ProductionLine createdProductionLine = builder.admin().productionLines().create("1",  7);
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));

      ProductionLine updatedProductionLine = new ProductionLine();
      updatedProductionLine.setId(createdProductionLine.getId());
      updatedProductionLine.setLineNumber("5c");

      builder.admin().productionLines().updateProductionLine(updatedProductionLine);
      builder.admin().productionLines().assertProductionLinesEqual(updatedProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));
    }
  }
  
  @Test
  public void testDeleteProductionLines() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      ProductionLine createdProductionLine = builder.admin().productionLines().create("1", 7);
      ProductionLine foundProductionLine = builder.admin().productionLines().findProductionLine(createdProductionLine.getId());
      assertEquals(createdProductionLine.getId(), foundProductionLine.getId());
      builder.admin().productionLines().delete(createdProductionLine);
      builder.admin().productionLines().assertFindFailStatus(404, createdProductionLine.getId());     
    }
  }
}
