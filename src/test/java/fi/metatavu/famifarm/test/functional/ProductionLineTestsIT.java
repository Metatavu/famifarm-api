package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Seed;
import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

public class ProductionLineTestsIT {
  @Test
  public void testCreateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      ProductionLine productionLine = builder.admin().productionLines().create(1);
      assertNotNull(productionLine);
    }
  }

  @Test
  public void testFindProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().assertFindFailStatus(404, UUID.randomUUID());
      ProductionLine createdProductionLine = builder.admin().productionLines().create(1);
      ProductionLine foundProductionLine = builder.admin().productionLines().findProductionLine(createdProductionLine.getId());
      assertEquals(createdProductionLine.getId(), foundProductionLine.getId());
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, foundProductionLine);
      builder.admin().productionLines().delete(foundProductionLine);
    }
  }

  @Test
  public void testListProductionLines() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().create(1);
      builder.admin().productionLines().assertCount(1);
      builder.admin().productionLines().create(2);
      builder.admin().productionLines().assertCount(2);
    }
  }

  @Test
  public void testUpdateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      ProductionLine createdProductionLine = builder.admin().productionLines().create(1);
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));

      ProductionLine updatedProductionLine = new ProductionLine();
      updatedProductionLine.setId(createdProductionLine.getId());
      updatedProductionLine.setLineNumber(5);

      builder.admin().productionLines().updateProductionLine(updatedProductionLine);
      builder.admin().productionLines().assertProductionLinesEqual(updatedProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));
    }
  }
}
