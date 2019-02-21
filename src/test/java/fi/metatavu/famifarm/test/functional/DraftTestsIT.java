package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Draft;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for drafts
 * 
 * @author Antti Leppä
 */
public class DraftTestsIT extends AbstractFunctionalTest {
  
  private static String TEST_JSON = "{\"test\": \"test data\"}";

  @Test
  public void testCreateDraft() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().drafts().create("test", TEST_JSON));
    }
  }
  
  @Test
  public void testListDrafts() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Draft draft = builder.admin().drafts().create("test", TEST_JSON);
      builder.admin().drafts().assertCount(1, draft.getUserId(), draft.getType());
      builder.admin().drafts().assertCount(0, UUID.randomUUID(), draft.getType());
      builder.admin().drafts().assertCount(0, draft.getUserId(), "something");
      builder.admin().drafts().assertCount(0, UUID.randomUUID(), "something");
      builder.admin().drafts().delete(draft);
      builder.admin().drafts().assertCount(0, draft.getUserId(), draft.getType());
    }
  }
  
  @Test
  public void testDeleteDraftPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Draft draft = builder.admin().drafts().create("test", TEST_JSON);
      builder.worker1().drafts().assertDeleteFailStatus(403, draft);
      builder.anonymous().drafts().assertDeleteFailStatus(401, draft);
      builder.invalid().drafts().assertDeleteFailStatus(401, draft);
    }
  }
  
}