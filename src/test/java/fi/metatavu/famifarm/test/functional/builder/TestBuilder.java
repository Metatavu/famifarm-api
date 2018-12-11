package fi.metatavu.famifarm.test.functional.builder;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.test.functional.builder.auth.DefaultAccessTokenProvider;
import fi.metatavu.famifarm.test.functional.builder.auth.TestBuilderAuthentication;

/**
 * Test builder class
 * 
 * @author Antti Leppä
 */
public class TestBuilder implements AutoCloseable {
  
  private static final String REALM = "test";
  private static final String CLIENT_ID = "ui";
  private static final String ADMIN_USER = "admin@example.com";
  private static final String ADMIN_PASSWORD = "test";
  
  private TestBuilderAuthentication admin;
  private List<AutoCloseable> closables = new ArrayList<>();
  
  /**
   * Returns admin authenticated authentication resource
   * 
   * @return admin authenticated authentication resource
   */
  public TestBuilderAuthentication admin() {
    if (admin != null) {
      return admin;
    }
    
    return admin = this.addClosable(new TestBuilderAuthentication(new DefaultAccessTokenProvider(REALM, CLIENT_ID, ADMIN_USER, ADMIN_PASSWORD, null)));
  }
  
  /**
   * Helper method for creating localized entries
   * 
   * @param en text in english
   * @return initialized localized entry
   */
  public LocalizedEntry createLocalizedEntry(String en) {
    return createLocalizedEntry(en, null);
  }

  /**
   * Helper method for creating localized entries
   * 
   * @param en text in English
   * @param fi text in Finnish
   * @return initialized localized entry
   */
  public LocalizedEntry createLocalizedEntry(String en, String fi) {
    LocalizedEntry result = new LocalizedEntry();

    LocalizedValue fiValue = createLocalizedValue("fi", fi);
    if (fiValue != null) {
      result.add(fiValue);
    }

    LocalizedValue enValue = createLocalizedValue("en", en);
    if (enValue != null) {
      result.add(enValue);
    }

    return result;
  }
  
  /**
   * Adds closable to clean queue
   * 
   * @param closable closable
   * @return given instance
   */
  private <T extends AutoCloseable> T addClosable(T closable) {
    closables.add(closable);
    return closable;
  }

  @Override
  public void close() throws Exception {
    for (int i = closables.size() - 1; i >= 0; i--) {
      closables.get(i).close();
    }
    
    admin = null;
  }

  /**
   * Creates localized value
   * 
   * @param language language
   * @param value value
   * @return localized value
   */
  private LocalizedValue createLocalizedValue(String language, String value) {
    LocalizedValue result = new LocalizedValue();
    result.setLanguage(language);
    result.setValue(value);
    return result;
  }

  
  
}
