package com.synergyapps.plugins.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class WorklogCollectionHelper
{
    @XmlElement
    private Collection<WorklogHelper> worklogHelpers = new ArrayList<WorklogHelper>();

    public WorklogCollectionHelper(Collection<WorklogHelper> worklogHelpers)
    {
        this.worklogHelpers.addAll(worklogHelpers);
    }

    public WorklogCollectionHelper(WorklogHelper worklogHelper)
    {
        this.worklogHelpers.add(worklogHelper);
    }
}
