package com.synergyapps.plugins.rest.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.velocity.VelocityManager;
import com.synergyapps.plugins.rest.entities.HtmlPresentationHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.synergyapps.plugins.rest.service.VelocityParamsGadgetHelper.prepareVelocityParams;

@Path("/timesheet-report")
public class TimesheetReportResource
{
    private static final Logger LOGGER = Logger.getLogger(TimesheetReportResource.class);

    private static final String[] TIMESHEET_REPORT_TEMPLATE = { "templates/gadgets/timesheet-gadget/", "view.vm" };

    @GET
    @AnonymousAllowed
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getTimesheetReportData(
            @QueryParam("timeInterval") String timeInterval,
            @QueryParam("startDayOfTheWeek") String startDayOfTheWeek,
            @QueryParam("jqlQuery") String jqlQuery,
            @QueryParam("baseUrl") String baseUrl)
    {
        LOGGER.info("Call of REST service /timesheet-report with next parameters: " +
                "{timeInterval,startDayOfTheWeek,jqlQuery} = {" + timeInterval + "," + startDayOfTheWeek + "," + jqlQuery + "}"
        );

        return prepareResponse(
                TIMESHEET_REPORT_TEMPLATE[0],
                TIMESHEET_REPORT_TEMPLATE[1],
                timeInterval,
                startDayOfTheWeek,
                jqlQuery,
                null,
                baseUrl
        );
    }

    Response prepareResponse(String templatePath, String templateFileName, String timeInterval, String startDayOfTheWeek, String jqlQuery, String projectId, String baseUrl)
    {
        VelocityManager vm = ComponentAccessor.getVelocityManager();
        try
        {
            int timeIntervalInteger = Integer.parseInt(timeInterval);
            int startDayOfTheWeekInteger = Integer.parseInt(startDayOfTheWeek);
            jqlQuery = jqlQuery.replace("&#34;", "\"");
            return Response.ok(new HtmlPresentationHelper(
                    vm.getBody(templatePath, templateFileName, prepareVelocityParams(timeIntervalInteger, startDayOfTheWeekInteger, jqlQuery, projectId, "false", baseUrl))
            )).build();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }
}
