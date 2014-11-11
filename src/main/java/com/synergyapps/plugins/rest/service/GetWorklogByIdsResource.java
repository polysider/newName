package com.synergyapps.plugins.rest.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.synergyapps.plugins.rest.entities.WorklogCollectionHelper;
import com.synergyapps.plugins.rest.entities.WorklogHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/getWorklogByIds")
public class GetWorklogByIdsResource
{
    private static final Logger LOGGER = Logger.getLogger(GetWorklogByIdsResource.class);

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getWorklogByIds(@QueryParam("ids") String ids)
    {
        LOGGER.info("Get worklog by list of ids: " + ids);

        try
        {
            List<WorklogHelper> worklogHelpers = new ArrayList<WorklogHelper>();
            String[] idsList = ids.split(",");
            for (String id : idsList)
            {
                Worklog worklog = ComponentAccessor.getWorklogManager().getById(Long.parseLong(id));
                String comment = worklog.getComment();
                String timeSpent = worklog.getTimeSpent().toString();
                WorklogHelper worklogHelper = new WorklogHelper(id, comment, timeSpent);
                worklogHelpers.add(worklogHelper);
            }
            return Response.ok(new WorklogCollectionHelper(worklogHelpers)).build();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }
}
