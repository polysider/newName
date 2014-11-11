package com.synergyapps.plugins.reports;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.synergyapps.plugins.util.DateUtil;
import com.synergyapps.plugins.util.TextUtil;
import com.synergyapps.plugins.util.WorklogUtil;
import com.synergyapps.plugins.worklog.WorklogContext;
import org.apache.log4j.Logger;
import webwork.action.ActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractCustomReport extends AbstractReport
{
    private static final Logger LOGGER = Logger.getLogger(AbstractCustomReport.class);

    protected final WorklogManager worklogManager;

    public AbstractCustomReport()
    {
        worklogManager = ComponentAccessor.getWorklogManager();
    }

    @Override
    public String generateReportHtml(ProjectActionSupport action, Map map) throws Exception
    {
        Map<String, Object> velocityParams = prepareVelocityParams(map);
        velocityParams.put("textUtil", TextUtil.INSTANCE);
        velocityParams.put("forExcel", "false");

        return descriptor.getHtml("view", velocityParams);
    }

    @Override
    public String generateReportExcel(ProjectActionSupport action, Map map) throws Exception
    {
        final StringBuilder contentDispositionValue = new StringBuilder(50);
        contentDispositionValue.append("attachment;filename=\"");
        contentDispositionValue.append(getDescriptor().getName()).append(".xls\";");
        final HttpServletResponse response = ActionContext.getResponse();
        response.addHeader("content-disposition", contentDispositionValue.toString());

        Map<String, Object> velocityParams = prepareVelocityParams(map);
        velocityParams.put("textUtil", TextUtil.INSTANCE);
        velocityParams.put("forExcel", "true");

        return descriptor.getHtml("excel", velocityParams);
    }

    @Override
    public boolean isExcelViewSupported()
    {
        return true;
    }

    abstract protected Map<String, Object> prepareVelocityParams(Map map);

	//TO DO OR NOT TO DO?
	
    @Override
    public void validate(ProjectActionSupport action, Map params)
    {
        Date startDate = parseDate(action, params, "startDateId");
        Date endDate = parseDate(action, params, "endDateId");

        if (!action.getHasErrors() && !startDate.before(endDate))
        {
            action.addError("startDateId", "Start date must be before end date!");
            action.addError("endDateId", "Start date must be before end date!");
        }

        String jqlQuery = (String) params.get("jqlQueryId");
        if (!WorklogUtil.isJqlQueryValid(jqlQuery))
        {
            action.addError("jqlQueryId", "Invalid jqlQuery!");
        }

        super.validate(action, params);
    }

    Date parseDate(ProjectActionSupport action, Map params, String dateFieldId)
    {
        String dateValue = (String) params.get(dateFieldId);
        try
        {
            return DateUtil.parseDateFromUi(dateValue);
        }
        catch (IllegalArgumentException ex)
        {
            action.addError(dateFieldId, "Both dates must have valid format!");
            LOGGER.error("Invalid date format: " + dateValue);

            return null;
        }
    }

    public void putWorklogToVelocityTemplate(WorklogContext worklogContext, Map<String, Object> velocityParams)
    {
        List<Worklog> worklogs = new ArrayList<Worklog>();
        for (Issue issue : worklogContext.getIssues())
        {
            worklogs.addAll(worklogManager.getByIssue(issue));
        }
        velocityParams.put("worklogs", worklogs);
    }
}
