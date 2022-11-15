package fi.metatavu.famifarm.rest.interceptors;

import fi.metatavu.famifarm.rest.model.ErrorResponse;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * JaxRs Interceptor for restricting access to non-authorized facility
 */
@Provider
public class FacilityInterceptor implements ContainerRequestFilter {

    private static final String FACILITY = "facility";

    @Context
    SecurityContext securityContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            UriInfo uriInfo = requestContext.getUriInfo();
            MultivaluedMap<String, String> pathParameters  = uriInfo.getPathParameters();
            String facility = pathParameters.get(FACILITY).stream().findFirst().orElseThrow();

            if (!securityContext.isUserInRole(facility.toLowerCase())) {
                requestContext.abortWith(createForbidden(String.format("Logged user doesn't have access to facility %s", facility)));
            }
        } catch (Exception e) {
            requestContext.abortWith(createBadRequest("Bad request"));
        }
    }

    /**
     * Constructs bad request response
     *
     * @param message message
     * @return response
     */
    protected Response createBadRequest(String message) {
        ErrorResponse entity = new ErrorResponse();
        entity.setMessage(message);
        Response.ResponseBuilder rb = Response.noContent();
        rb = rb.type(MediaType.APPLICATION_JSON);
        rb = rb.status(Response.Status.BAD_REQUEST);
        rb = rb.entity(entity);
        return rb.build();
    }

    /**
     * Constructs forbidden response
     *
     * @param message message
     * @return response
     */
    protected Response createForbidden(String message) {
        ErrorResponse entity = new ErrorResponse();
        entity.setMessage(message);
        Response.ResponseBuilder rb = Response.noContent();
        rb = rb.type(MediaType.APPLICATION_JSON);
        rb = rb.status(Response.Status.FORBIDDEN);
        rb = rb.entity(entity);
        return rb.build();
    }
}
