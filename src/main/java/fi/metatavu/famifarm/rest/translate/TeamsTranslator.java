package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Team;

/**
 * Translator for teams
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class TeamsTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA team object into REST team object
   * 
   * @param team JPA team object
   * @return REST team
   */
  public Team translateTeam(fi.metatavu.famifarm.persistence.model.Team team) {
    if (team == null) {
      return null;
    }
    
    Team result = new Team();
    result.setId(team.getId());
    result.setName(translatelocalizedValue(team.getName()));

    return result;
  }
  
}
