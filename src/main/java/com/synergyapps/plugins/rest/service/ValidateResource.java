package com.synergyapps.plugins.rest.service;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.synergyapps.plugins.rest.entities.IssuesGadgetErrorCollection;
import com.synergyapps.plugins.util.WorklogUtil;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/validate")
public class ValidateResource
{
    private static final Logger LOGGER = Logger.getLogger(ValidateResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response peformValidation(@QueryParam("jqlQuery") String jqlQuery)
    {
        LOGGER.info("Perform validation for jqlQuery=" + jqlQuery);

        try
        {
            jqlQuery = jqlQuery.replace("&#34;", "\"");
            if (WorklogUtil.isJqlQueryValid(jqlQuery))
            {
                LOGGER.info("jqlQuery validation was passed");
                return Response.status(Response.Status.OK).build();
            }
            LOGGER.error("Error occurs during jqlQuery validation");
            return Response.status(Response.Status.BAD_REQUEST).entity(new IssuesGadgetErrorCollection()).build();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }
}