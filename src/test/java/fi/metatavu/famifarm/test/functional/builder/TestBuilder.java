package fi.metatavu.famifarm.test.functional.builder;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.client.model.LocalizedValue;
import fi.metatavu.famifarm.test.functional.builder.auth.DefaultAccessTokenProvider;
import fi.metatavu.famifarm.test.functional.builder.auth.InvalidAccessTokenProvider;
import fi.metatavu.famifarm.test.functional.builder.auth.NullAccessTokenProvider;
import fi.metatavu.famifarm.test.functional.builder.auth.TestBuilderAuthentication;

/**
 * Test builder class
 * 
 * @author Antti Leppä
 */
public class TestBuilder implements AutoCloseable {
  
  private static final String REALM = "test";
  private static final String CLIENT_ID = "ui";
  private static final String ADMIN_USER = "admin";
  private static final String ADMIN_PASSWORD = "test";
  private static final String MANAGER_JOROINEN_USER = "manager@example.com";
  private static final String MANAGER_JOROINEN_PASSWORD = "test";
  private static final String WORKER_JOROINEN_USER = "worker1@example.com";
  private static final String WORKER_JOROINEN_PASSWORD = "test";
  private static final String MANAGER_JUVA_USER = "manager-juva";
  private static final String MANAGER_JUVA_PASSWORD = "password";
  private static final String WORKER_JUVA_USER = "worker-juva";
  private static final String WORKER_JUVA_PASSWORD = "password";
  
  private TestBuilderAuthentication admin;
  private TestBuilderAuthentication invalid;
  private TestBuilderAuthentication anonymous;
  private TestBuilderAuthentication workerJoroinen;
  private TestBuilderAuthentication workerJuva;
  private TestBuilderAuthentication managerJuva;
  private TestBuilderAuthentication managerJoroinen;
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
   * Returns manager authenticated authentication resource
   * 
   * @return manager authenticated authentication resource
   */
  public TestBuilderAuthentication managerJoroinen() {
    if (managerJoroinen != null) {
      return managerJoroinen;
    }
    
    return managerJoroinen = this.addClosable(new TestBuilderAuthentication(new DefaultAccessTokenProvider(REALM, CLIENT_ID, MANAGER_JOROINEN_USER, MANAGER_JOROINEN_PASSWORD, null)));
  }

  /**
   * Returns manager-juva authenticated authentication resource
   *
   * @return manager-juva authenticated authentication resource
   */
  public TestBuilderAuthentication managerJuva() {
    if (managerJuva != null) {
      return managerJuva;
    }

    return managerJuva = this.addClosable((new TestBuilderAuthentication(new DefaultAccessTokenProvider(REALM, CLIENT_ID, MANAGER_JUVA_USER, MANAGER_JUVA_PASSWORD, null))));
  }

  /**
   * Returns authentication resource with invalid token
   * 
   * @return authentication resource with invalid token
   */
  public TestBuilderAuthentication invalid() {
    if (invalid != null) {
      return invalid;
    }
    
    return invalid = this.addClosable(new TestBuilderAuthentication(new InvalidAccessTokenProvider()));
  }

  /**
   * Returns authentication resource without token
   * 
   * @return authentication resource without token
   */
  public TestBuilderAuthentication anonymous() {
    if (anonymous != null) {
      return anonymous;
    }
    
    return anonymous = this.addClosable(new TestBuilderAuthentication(new NullAccessTokenProvider()));
  }

  /**
   * Returns worker1 authenticated authentication resource
   * 
   * @return worker1 authenticated authentication resource
   */
  public TestBuilderAuthentication workerJoroinen() {
    if (workerJoroinen != null) {
      return workerJoroinen;
    }
    
    return workerJoroinen = this.addClosable(new TestBuilderAuthentication(new DefaultAccessTokenProvider(REALM, CLIENT_ID, WORKER_JOROINEN_USER, WORKER_JOROINEN_PASSWORD, null)));
  }

  /**
   * Returns worker-juva authenticated authentication resource
   *
   * @return worker-juva authenticated authentication resource
   */
  public TestBuilderAuthentication workerJuva() {
    if (workerJuva != null) {
      return workerJuva;
    }

    return workerJuva = this.addClosable((new TestBuilderAuthentication(new DefaultAccessTokenProvider(REALM, CLIENT_ID, WORKER_JUVA_USER, WORKER_JUVA_PASSWORD, null))));
  }
  
  /**
   * Helper method for creating localized entries
   * 
   * @param en text in english
   * @return initialized localized entry
   */
  public List<LocalizedValue> createLocalizedEntry(String en) {
    return createLocalizedEntry(en, null);
  }

  /**
   * Helper method for creating localized entries
   * 
   * @param en text in English
   * @param fi text in Finnish
   * @return initialized localized entry
   */
  public List<LocalizedValue> createLocalizedEntry(String en, String fi) {
    List<LocalizedValue> result = new ArrayList<>();

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
    if (value != null) {
      LocalizedValue result = new LocalizedValue();
      result.setLanguage(language);
      result.setValue(value);
      return result;
    }
    
    return null;
  }
  
  
}
