package com.synergyapps.plugins.rest.service;

import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.synergyapps.plugins.rest.entities.HtmlPresentationHelper;
import com.synergyapps.plugins.util.DateUtil;
import com.synergyapps.plugins.util.WorklogUtil;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/addWorklog")
public class AddWorklogResource
{
    private static final Logger LOGGER = Logger.getLogger(AddWorklogResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addWorklog(
            @QueryParam("issueId") String issueId,
            @QueryParam("userName") String userName,
            @QueryParam("timeSpent") String timeSpent,
            @QueryParam("startDate") String startDate,
            @QueryParam("comment") String comment)
    {
        LOGGER.info("Add worklog for issue with id=" + issueId + ", user=" + userName + ": spent " + timeSpent + " at " + startDate + ". Commentary: " + comment);

        startDate += " 12:00 AM";
        try
        {
            Date date = DateUtil.parseDateForWorklog(startDate);
            Worklog worklog = WorklogUtil.createWorklog(userName, Long.parseLong(issueId), timeSpent, date, comment);
            if (worklog == null)
            {
                LOGGER.error("Worklog don't created");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Long worklogId = worklog.getId();
            LOGGER.info("Worklog saved with id=" + worklogId);

            return Response.ok(new HtmlPresentationHelper(Long.toString(worklogId))).build();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }
}
