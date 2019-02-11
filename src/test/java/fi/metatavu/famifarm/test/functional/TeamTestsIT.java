package fi.metatavu.famifarm.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.test.functional.builder.TestBuilder;

/**
 * Tests for teams
 * 
 * @author Ville Koivukangas
 */
public class TeamTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateTeam() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi")));
    }
  }

  @Test
  public void testCreateTeamPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.worker1().teams().assertCreateFailStatus(403, builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.anonymous().teams().assertCreateFailStatus(401, builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.invalid().teams().assertCreateFailStatus(401, builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
    }
  }
  
  @Test
  public void testFindTeam() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().teams().assertFindFailStatus(404, UUID.randomUUID());
      Team createdTeam = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      Team foundTeam = builder.admin().teams().findTeam(createdTeam.getId());
      assertEquals(createdTeam.getId(), foundTeam.getId());
      builder.admin().teams().assertTeamsEqual(createdTeam, foundTeam);
    }
  }
  
  @Test
  public void testFindTeamPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      assertNotNull(builder.admin().teams().findTeam(team.getId()));
      assertNotNull(builder.manager().teams().findTeam(team.getId()));
      assertNotNull(builder.worker1().teams().findTeam(team.getId()));
      builder.invalid().teams().assertFindFailStatus(401, team.getId());
      builder.anonymous().teams().assertFindFailStatus(401, team.getId());
    }
  }
  
  @Test
  public void testListteams() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      
      builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.admin().teams().assertCount(1);
      builder.admin().teams().create(builder.createLocalizedEntry("lettuce", "Lehtisalaatti"));
      builder.admin().teams().assertCount(2);
    }
  }
  
  @Test
  public void testListTeamPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.worker1().teams().assertCount(1);
      builder.manager().teams().assertCount(1);
      builder.admin().teams().assertCount(1);
      builder.invalid().teams().assertFindFailStatus(401, team.getId());
      builder.anonymous().teams().assertFindFailStatus(401, team.getId());
    }
  }
  
  @Test
  public void testUpdateTeam() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team createdTeam = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.admin().teams().assertTeamsEqual(createdTeam, builder.admin().teams().findTeam(createdTeam.getId()));
      
      Team updateTeam = new Team(); 
      updateTeam.setId(createdTeam.getId());
      updateTeam.setName(builder.createLocalizedEntry("Updated Team", "Päivitetty tiimi"));
     
      builder.admin().teams().updateTeam(updateTeam);
      builder.admin().teams().assertTeamsEqual(updateTeam, builder.admin().teams().findTeam(createdTeam.getId()));
    }
  }
  
  @Test
  public void testUpdateTeamPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.worker1().teams().assertUpdateFailStatus(403, team);
      builder.anonymous().teams().assertUpdateFailStatus(401, team);
      builder.invalid().teams().assertUpdateFailStatus(401, team);
    }
  }
  
  @Test
  public void testDeleteteams() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team createdTeam = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      Team foundTeam = builder.admin().teams().findTeam(createdTeam.getId());
      assertEquals(createdTeam.getId(), foundTeam.getId());
      builder.admin().teams().delete(createdTeam);
      builder.admin().teams().assertFindFailStatus(404, createdTeam.getId());     
    }
  }

  @Test
  public void testDeleteTeamPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Team team = builder.admin().teams().create(builder.createLocalizedEntry("Test Team", "Testi Tiimi"));
      builder.worker1().teams().assertDeleteFailStatus(403, team);
      builder.anonymous().teams().assertDeleteFailStatus(401, team);
      builder.invalid().teams().assertDeleteFailStatus(401, team);
    }
  }
  
}