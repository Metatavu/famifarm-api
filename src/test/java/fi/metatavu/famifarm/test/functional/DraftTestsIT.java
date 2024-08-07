package fi.metatavu.famifarm.test.functional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import fi.metatavu.famifarm.client.model.Facility;
import org.junit.jupiter.api.Test;

import fi.metatavu.famifarm.client.model.Draft;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

import io.quarkus.test.junit.QuarkusTest;
import fi.metatavu.famifarm.test.functional.resources.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;

/**
 * Tests for drafts
 * 
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class DraftTestsIT extends AbstractFunctionalTest {
  
  private static final String TEST_JSON = "{\"test\": \"test data\"}";

  @Test
  public void testCreateDraft() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().drafts().create("test", TEST_JSON, Facility.JOROINEN));
      assertNotNull(builder.admin().drafts().create("test", TEST_JSON, Facility.JUVA));

      // Test that user cannot access facility they don't have access to
      builder.workerJuva().drafts().assertCreateFailStatus(403, "test", TEST_JSON, Facility.JOROINEN);
      builder.workerJoroinen().drafts().assertCreateFailStatus(403, "test", TEST_JSON, Facility.JUVA);
    }
  }
  
  @Test
  public void testListDrafts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Draft draft = builder.admin().drafts().create("test", TEST_JSON, Facility.JOROINEN);
      builder.admin().drafts().assertCount(1, draft.getUserId(), draft.getType(), Facility.JOROINEN);
      builder.admin().drafts().assertCount(0, draft.getUserId(), draft.getType(), Facility.JUVA);
      builder.admin().drafts().assertCount(0, UUID.randomUUID(), draft.getType(), Facility.JOROINEN);
      builder.admin().drafts().assertCount(0, draft.getUserId(), "something", Facility.JOROINEN);
      builder.admin().drafts().assertCount(0, UUID.randomUUID(), "something", Facility.JOROINEN);
      builder.admin().drafts().delete(draft, Facility.JOROINEN);
      builder.admin().drafts().assertCount(0, draft.getUserId(), draft.getType(), Facility.JOROINEN);

      // Test that user cannot access facility they don't have access to
      builder.workerJuva().drafts().assertListFailStatus(403, Facility.JOROINEN);
      builder.workerJoroinen().drafts().assertListFailStatus(403, Facility.JUVA);
    }
  }
  
  @Test
  public void testDeleteDraftPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Draft draft = builder.admin().drafts().create("test", TEST_JSON, Facility.JOROINEN);
      builder.admin().drafts().assertDeleteFailStatus(404, draft, Facility.JUVA);
      builder.workerJoroinen().drafts().assertDeleteFailStatus(403, draft, Facility.JOROINEN);
      builder.anonymous().drafts().assertDeleteFailStatus(401, draft, Facility.JOROINEN);
      builder.invalid().drafts().assertDeleteFailStatus(401, draft, Facility.JOROINEN);

      // Test that user cannot access facility they don't have access to
      builder.workerJoroinen().drafts().assertDeleteFailStatus(403, draft, Facility.JUVA);
      builder.workerJuva().drafts().assertDeleteFailStatus(403, draft, Facility.JOROINEN);
    }
  }
  
}