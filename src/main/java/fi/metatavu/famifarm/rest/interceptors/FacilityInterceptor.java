package fi.metatavu.famifarm.rest.interceptors;

import fi.metatavu.famifarm.rest.model.ErrorResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * JaxRs Interceptor for restricting access to non-authorized facility
 */
@Provider
public class FacilityInterceptor implements ContainerRequestFilter {


    private static final String FACILITY = "facility";
    private static final String[] WHITELISTED_PATHS = { "/v1/system/ping" };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        MultivaluedMap<String, String> pathParameters  = uriInfo.getPathParameters();
        List<String> facilityParams = pathParameters.get(FACILITY);
        CollectionUtils.filter(facilityParams, PredicateUtils.notNullPredicate());

        if (facilityParams == null) {
            return;
        }

        Optional<String> facility = facilityParams.stream().findFirst();
        SecurityContext securityContext = requestContext.getSecurityContext();
        Principal userPrincipal = securityContext.getUserPrincipal();

        if (!Arrays.asList(WHITELISTED_PATHS).contains(path) && userPrincipal == null) {
            requestContext.abortWith(createErrorResponse("Unauthorized", Response.Status.UNAUTHORIZED));
            return;
        }

        if (!securityContext.isUserInRole(facility.get().toLowerCase())) {
            requestContext.abortWith(createErrorResponse(String.format("Logged user doesn't have access to facility %s", facility), Response.Status.FORBIDDEN));
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
