package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.ProductionLine;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

public class ProductionLineTestsIT extends AbstractFunctionalTest {
  @Test
  public void testCreateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      ProductionLine productionLine = builder.admin().productionLines().create("1", null);
      assertNotNull(productionLine);
    }
  }

  @Test
  public void testFindProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().assertFindFailStatus(404, UUID.randomUUID());
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Team", "Tiimi"));
      ProductionLine createdProductionLine = builder.admin().productionLines().create("1", team);
      ProductionLine foundProductionLine = builder.admin().productionLines().findProductionLine(createdProductionLine.getId());
      assertEquals(createdProductionLine.getId(), foundProductionLine.getId());
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, foundProductionLine);
      builder.admin().productionLines().delete(foundProductionLine);
    }
  }

  @Test
  public void testListProductionLines() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().productionLines().create("1", null);
      builder.admin().productionLines().assertCount(1);
      builder.admin().productionLines().create("2", null);
      builder.admin().productionLines().assertCount(2);
    }
  }

  @Test
  public void testUpdateProductionLine() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Team", "Tiimi"));

      ProductionLine createdProductionLine = builder.admin().productionLines().create("1", null);
      builder.admin().productionLines().assertProductionLinesEqual(createdProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));

      ProductionLine updatedProductionLine = new ProductionLine();
      updatedProductionLine.setId(createdProductionLine.getId());
      updatedProductionLine.setLineNumber("5c");
      updatedProductionLine.setDefaultTeamId(team.getId());

      builder.admin().productionLines().updateProductionLine(updatedProductionLine);
      builder.admin().productionLines().assertProductionLinesEqual(updatedProductionLine, builder.admin().productionLines().findProductionLine(createdProductionLine.getId()));
    }
  }
  
  @Test
  public void testDeleteProductionLines() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Team", "Tiimi"));
      
      ProductionLine createdProductionLine = builder.admin().productionLines().create("1", team);
      ProductionLine foundProductionLine = builder.admin().productionLines().findProductionLine(createdProductionLine.getId());
      assertEquals(createdProductionLine.getId(), foundProductionLine.getId());
      builder.admin().productionLines().delete(createdProductionLine);
      builder.admin().productionLines().assertFindFailStatus(404, createdProductionLine.getId());     
    }
  }
}
