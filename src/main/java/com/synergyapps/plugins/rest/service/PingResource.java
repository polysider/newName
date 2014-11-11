package com.synergyapps.plugins.rest.service;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource
{
    private static final Logger LOGGER = Logger.getLogger(PingResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response ping(@QueryParam("param") String param)
    {
        LOGGER.info("Ping with param: " + param);

        return Response.status(Response.Status.OK).build();
    }
}
