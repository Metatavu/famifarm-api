package fi.metatavu.famifarm.test.functional;

import fi.metatavu.famifarm.client.model.Facility;
import fi.metatavu.famifarm.client.model.PerformedCultivationAction;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import fi.metatavu.famifarm.test.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for performedCultivationActions
 *
 * @author Ville Koivukangas
 */
@QuarkusTest
@QuarkusTestResource(MysqlResource.class)
@QuarkusTestResource(KeycloakResource.class)
public class PerformedCultivationActionTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePerformedCultivationAction() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN));
    }
  }

  @Test
  public void testCreatePerformedCultivationActionPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().performedCultivationActions().assertCreateFailStatus(403, builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"));
      builder.anonymous().performedCultivationActions().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"));
      builder.invalid().performedCultivationActions().assertCreateFailStatus(401, builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"));
    }
  }

  @Test
  public void testFindPerformedCultivationAction() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions().assertFindFailStatus(404, UUID.randomUUID(), Facility.JUVA);
      PerformedCultivationAction createdPerformedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      PerformedCultivationAction foundPerformedCultivationAction = builder.admin().performedCultivationActions().findPerformedCultivationAction(createdPerformedCultivationAction.getId());
      builder.admin().performedCultivationActions().assertFindFailStatus(404, createdPerformedCultivationAction.getId(), Facility.JUVA);
      assertEquals(createdPerformedCultivationAction.getId(), foundPerformedCultivationAction.getId());
      builder.admin().performedCultivationActions().assertPerformedCultivationActionsEqual(createdPerformedCultivationAction, foundPerformedCultivationAction);
    }
  }

  @Test
  public void testFindPerformedCultivationActionPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PerformedCultivationAction performedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      assertNotNull(builder.admin().performedCultivationActions().findPerformedCultivationAction(performedCultivationAction.getId()));
      assertNotNull(builder.manager().performedCultivationActions().findPerformedCultivationAction(performedCultivationAction.getId()));
      assertNotNull(builder.worker1().performedCultivationActions().findPerformedCultivationAction(performedCultivationAction.getId()));
      builder.invalid().performedCultivationActions().assertFindFailStatus(401, performedCultivationAction.getId(), Facility.JOROINEN);
      builder.anonymous().performedCultivationActions().assertFindFailStatus(401, performedCultivationAction.getId(), Facility.JOROINEN);
    }
  }

  @Test
  public void testListperformedCultivationActions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {

      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction 2", "Testi viljely 2"), Facility.JUVA);
      builder.admin().performedCultivationActions().assertCount(1, Facility.JOROINEN);
      builder.admin().performedCultivationActions().assertCount(1, Facility.JUVA);
    }
  }

  @Test
  public void testListPerformedCultivationActionPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      builder.worker1().performedCultivationActions().assertCount(1, Facility.JOROINEN);
      builder.manager().performedCultivationActions().assertCount(1, Facility.JOROINEN);
      builder.admin().performedCultivationActions().assertCount(1, Facility.JOROINEN);
      builder.invalid().performedCultivationActions().assertListFailStatus(401, Facility.JOROINEN);
      builder.anonymous().performedCultivationActions().assertListFailStatus(401, Facility.JOROINEN);
    }
  }

  @Test
  public void testUpdatePerformedCultivationAction() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PerformedCultivationAction createdPerformedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      builder.admin().performedCultivationActions().assertPerformedCultivationActionsEqual(createdPerformedCultivationAction, builder.admin().performedCultivationActions().findPerformedCultivationAction(createdPerformedCultivationAction.getId()));

      PerformedCultivationAction updatePerformedCultivationAction = new PerformedCultivationAction();
      updatePerformedCultivationAction.setId(createdPerformedCultivationAction.getId());
      updatePerformedCultivationAction.setName(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"));

      builder.admin().performedCultivationActions().assertUpdateFailStatus(404, updatePerformedCultivationAction, Facility.JUVA);
      builder.admin().performedCultivationActions().updatePerformedCultivationAction(updatePerformedCultivationAction);
      builder.admin().performedCultivationActions().assertPerformedCultivationActionsEqual(updatePerformedCultivationAction, builder.admin().performedCultivationActions().findPerformedCultivationAction(createdPerformedCultivationAction.getId()));
    }
  }

  @Test
  public void testUpdatePerformedCultivationActionPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PerformedCultivationAction performedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      builder.worker1().performedCultivationActions().assertUpdateFailStatus(403, performedCultivationAction, Facility.JOROINEN);
      builder.anonymous().performedCultivationActions().assertUpdateFailStatus(401, performedCultivationAction, Facility.JOROINEN);
      builder.invalid().performedCultivationActions().assertUpdateFailStatus(401, performedCultivationAction, Facility.JOROINEN);
    }
  }

  @Test
  public void testDeletePerformedCultivationActions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PerformedCultivationAction createdPerformedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      PerformedCultivationAction foundPerformedCultivationAction = builder.admin().performedCultivationActions().findPerformedCultivationAction(createdPerformedCultivationAction.getId());
      assertEquals(createdPerformedCultivationAction.getId(), foundPerformedCultivationAction.getId());
      builder.admin().performedCultivationActions().assertDeleteFailStatus(404, createdPerformedCultivationAction, Facility.JUVA);
      builder.admin().performedCultivationActions().delete(createdPerformedCultivationAction, Facility.JOROINEN);
      builder.admin().performedCultivationActions().assertFindFailStatus(404, createdPerformedCultivationAction.getId(), Facility.JUVA);
    }
  }

  @Test
  public void testDeletePerformedCultivationActionPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      PerformedCultivationAction performedCultivationAction = builder.admin().performedCultivationActions().create(builder.createLocalizedEntry("Test PerformedCultivationAction", "Testi viljely"), Facility.JOROINEN);
      builder.worker1().performedCultivationActions().assertDeleteFailStatus(403, performedCultivationAction, Facility.JOROINEN);
      builder.anonymous().performedCultivationActions().assertDeleteFailStatus(401, performedCultivationAction, Facility.JOROINEN);
      builder.invalid().performedCultivationActions().assertDeleteFailStatus(401, performedCultivationAction, Facility.JOROINEN);
    }
  }

}