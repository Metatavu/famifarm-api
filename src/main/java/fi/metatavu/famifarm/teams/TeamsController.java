package fi.metatavu.famifarm.teams;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.persistence.dao.TeamDAO;
import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Team;

/**
 * Controller class for teams
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class TeamsController {

  @Inject
  private LocalizedValueController localizedValueController;
  
  @Inject
  private TeamDAO teamDAO;
  
  /**
   * Creates new team
   * 
   * @param name name
   * @param creatorId creatorId
   * @return created team
   */
  public Team createTeam(LocalizedEntry name, UUID creatorId) {
    return teamDAO.create(UUID.randomUUID(), name, creatorId, creatorId);
  }

  /**
   * Finds team by id
   * 
   * @param teamId team id
   * @return team
   */
  public Team findTeam(UUID teamId) {
    return teamDAO.findById(teamId);
  }

  /**
   * Lists teams
   * 
   * @param firstResult first result
   * @param maxResults max results
   * @return list of teams
   */
  public List<Team> listTeams(Integer firstResult, Integer maxResults) {
    return teamDAO.listAll(firstResult, maxResults);
  }
  
  /**
   * Update team
   *
   * @param name name
   * @param lastModifierId lastModifierId
   * @return updated team
   */
  public Team updateTeam(Team team, LocalizedEntry name, UUID lastModifierId) {
    teamDAO.updateName(team, name, lastModifierId);
    return team;
  }

  /**
   * Deletes a team
   * 
   * @param team team to be deleted
   */
  public void deleteTeam(Team team) {
    LocalizedEntry name = team.getName();
    teamDAO.delete(team);
    localizedValueController.deleteEntry(name);
  }
  
}
