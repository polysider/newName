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

@Path("/updateWorklog")
public class UpdateWorklogResource
{
    private static final Logger LOGGER = Logger.getLogger(UpdateWorklogResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateWorklog(
            @QueryParam("worklogId") String worklogId,
            @QueryParam("comment") String comment,
            @QueryParam("timeSpent") String timeSpent)
    {
        LOGGER.info("Update worklog with id=" + worklogId + ", new comment: " + comment + ", new time spent: " + timeSpent);

        try
        {
            Long id = Long.parseLong(worklogId);
            if (WorklogUtil.updateWorklog(id, comment, timeSpent) != null)
            {
                LOGGER.info("Worklog updated successfully");
                return Response.status(Response.Status.OK).build();
            }
            else
            {
                LOGGER.error("Error occurs during error update");
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
