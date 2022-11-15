package fi.metatavu.famifarm.rest.interceptors;

import fi.metatavu.famifarm.rest.model.ErrorResponse;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * JaxRs Interceptor for restricting access to non-authorized facility
 */
@Provider
public class FacilityInterceptor implements ContainerRequestFilter {

    private static final String FACILITY = "facility";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            UriInfo uriInfo = requestContext.getUriInfo();
            MultivaluedMap<String, String> pathParameters  = uriInfo.getPathParameters();
            String facility = pathParameters.get(FACILITY).stream().findFirst().orElseThrow();
            Principal principal = requestContext.getSecurityContext().getUserPrincipal();
            SecurityContext securityContext = requestContext.getSecurityContext();

            if (principal != null && !securityContext.isUserInRole(facility.toLowerCase())) {
                requestContext.abortWith(createErrorResponse(String.format("Logged user doesn't have access to facility %s", facility), Response.Status.FORBIDDEN));
            }
        } catch (Exception e) {
            requestContext.abortWith(createErrorResponse("Bad request", Response.Status.BAD_REQUEST));
        }
    }

    /**
     * Constructs error response
     *
     * @param message message
     * @param status status
     * @return response
     */
    protected Response createErrorResponse(String message, Response.Status status) {
        ErrorResponse entity = new ErrorResponse();
        entity.setMessage(message);
        Response.ResponseBuilder rb = Response.noContent();
        rb = rb.type(MediaType.APPLICATION_JSON);
        rb = rb.status(status);
        rb = rb.entity(entity);
        return rb.build();
    }
}
