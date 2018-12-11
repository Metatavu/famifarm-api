package fi.metatavu.famifarm.test.functional;

import org.junit.Test;

import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for seeds
 * 
 * @author Antti Leppä
 */
public class SeedTestsIT {
  
  @Test
  public void testListSeeds() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().seeds().assertCount(0);
      builder.admin().seeds().create(builder.createLocalizedEntry("Rocket", "Rucola"));
      builder.admin().seeds().assertCount(1);
      builder.admin().seeds().create(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"));
      builder.admin().seeds().assertCount(2);
    }
  }
  

}