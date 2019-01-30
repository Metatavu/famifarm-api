package fi.metatavu.famifarm.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.persistence.dao.SowingEventDAO;

@ApplicationScoped
public class SowingEventController {
  
  @Inject
  private SowingEventDAO sowingEventDAO;  

}
