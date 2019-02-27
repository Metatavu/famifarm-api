package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.LocalizedEntry;
import fi.metatavu.famifarm.persistence.model.Team;

/**
 * DAO class for teams
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class TeamDAO extends AbstractDAO<Team> {

  /**
   * Creates new team
   *
   * @param name name
   * @param lastModifier modifier
   * @return created team
   */
  public Team create(UUID id, LocalizedEntry name, UUID creatorId, UUID lastModifierId) {
    Team team = new Team();
    team.setName(name);
    team.setId(id);
    team.setCreatorId(creatorId);
    team.setLastModifierId(lastModifierId);
    return persist(team);
  }

  /**
   * Updates name
   *
   * @param team team
   * @param name name
   * @param lastModifier modifier
   * @return updated team
   */
  public Team updateName(Team team, LocalizedEntry name, UUID lastModifierId) {
    team.setLastModifierId(lastModifierId);
    team.setName(name);
    return persist(team);
  }

}
