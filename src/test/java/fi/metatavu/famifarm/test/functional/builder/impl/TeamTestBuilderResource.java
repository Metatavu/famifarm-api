package fi.metatavu.famifarm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.client.TeamsApi;
import fi.metatavu.famifarm.client.model.LocalizedEntry;
import fi.metatavu.famifarm.client.model.Team;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

/**
 * Test builder resource for teams
 * 
 * @author Ville Koivukangas
 */
public class TeamTestBuilderResource extends AbstractTestBuilderResource<Team, TeamsApi> {
  
  /**
   * Constructor
   * 
   * @param apiClient initialized API client
   */
  public TeamTestBuilderResource(ApiClient apiClient) {
    super(apiClient);
  }
  
  /**
   * Creates new team
   * 
   * @param name name
   * @return created team
   */
  public Team create(LocalizedEntry name) {
    Team team = new Team();
    team.setName(name);
    return addClosable(getApi().createTeam(team));
  }

  /**
   * Finds a team
   * 
   * @param teamId team id
   * @return found team
   */
  public Team findTeam(UUID teamId) {
    return getApi().findTeam(teamId);
  }

  /**
   * Updates a team into the API
   * 
   * @param body body payload
   */
  public Team updateTeam(Team body) {
    return getApi().updateTeam(body, body.getId());
  }
  
  /**
   * Deletes a team from the API
   * 
   * @param team team to be deleted
   */
  public void delete(Team team) {
    getApi().deleteTeam(team.getId());  
    removeClosable(closable -> !closable.getId().equals(team.getId()));
  }
  
  /**
   * Asserts team count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listTeams(Collections.emptyMap()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertFindFailStatus(int expectedStatus, UUID teamId) {
    try {
      getApi().findTeam(teamId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertCreateFailStatus(int expectedStatus, LocalizedEntry name) {
    try {
      Team team = new Team();
      team.setName(name);
      getApi().createTeam(team);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertUpdateFailStatus(int expectedStatus, Team team) {
    try {
      getApi().updateTeam(team, team.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertDeleteFailStatus(int expectedStatus, Team team) {
    try {
      getApi().deleteTeam(team.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listTeams(Collections.emptyMap());
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual teams equals expected seed when both are serialized into JSON
   * 
   * @param expected expected team
   * @param actual actual team
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertTeamsEqual(Team expected, Team actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Team team) {
    getApi().deleteTeam(team.getId());  
  }

}
