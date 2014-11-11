package com.synergyapps.plugins.reports;

import java.util.Map;

public class IssuesReport extends AbstractCustomReport
{
    @Override
    protected Map<String, Object> prepareVelocityParams(Map map)
    {
        return VelocityParamsReportHelper.prepareVelocityParams(map, false);
    }
}
