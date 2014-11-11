package com.synergyapps.plugins.rest.service;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.synergyapps.plugins.util.WorklogUtil;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/deleteWorklog")
public class DeleteWorklogResource
{
    private static final Logger LOGGER = Logger.getLogger(DeleteWorklogResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response deleteWorklog(@QueryParam("worklogId") String worklogId)
    {
        LOGGER.info("Delete worklog with id=" + worklogId);

        try
        {
            Long id = Long.parseLong(worklogId);
            boolean result = WorklogUtil.deleteWorklog(id);
            if (result)
            {
                LOGGER.info("Deletion performed successfully");
                return Response.status(Response.Status.OK).build();
            }
            else
            {
                LOGGER.error("Error occurs during deletion");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }
}
