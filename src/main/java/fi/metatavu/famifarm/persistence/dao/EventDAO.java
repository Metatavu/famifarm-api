package fi.metatavu.famifarm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.persistence.model.Event;

/**
 * Generic DAO class for events
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class EventDAO extends AbstractEventDAO<Event> {

}
