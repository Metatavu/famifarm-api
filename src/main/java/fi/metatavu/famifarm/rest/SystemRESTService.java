package fi.metatavu.famifarm.rest;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * System REST Services
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@Path ("/v1/system")
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class SystemRESTService {
  
  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  /**
   * Returns pong
   * 
   * @return pong in plain text
   */
  @GET
  @Path ("/ping")
  @Produces (MediaType.TEXT_PLAIN)
  @PermitAll
  public Response getPing() {
    return Response.ok("pong").build();
  }

  /**
   * Flushes JPA entity cache
   * 
   * @return "ok"
   */
  @GET
  @Path ("/jpa/cache/flush")
  @Produces (MediaType.TEXT_PLAIN)
  public Response flushCaches() {
    if ("TEST".equals(System.getProperty("runmode"))) {
      entityManagerFactory.getCache().evictAll();
      return Response.ok("ok").build();
    }
    
    return Response.status(Status.FORBIDDEN).build();
  }
}
