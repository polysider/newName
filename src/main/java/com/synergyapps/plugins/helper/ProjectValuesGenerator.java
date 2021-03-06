package com.synergyapps.plugins.helper;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;


//Random comment number 2
//...aand number 4

public class ProjectValuesGenerator implements ValuesGenerator
{
    @Override
    public Map getValues(Map userParams)
    {
        Map<String, String> projectMap = new HashMap<String, String>();
        projectMap.put("", "All Projects");
        List<Project> allProjects = ComponentAccessor.getProjectManager().getProjectObjects();
        for (Project project : allProjects)
        {
            projectMap.put(project.getId().toString(), project.getName());
        }
        return projectMap;
    }
}
