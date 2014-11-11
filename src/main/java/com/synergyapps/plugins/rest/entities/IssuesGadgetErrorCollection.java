package com.synergyapps.plugins.rest.entities;

public class IssuesGadgetErrorCollection extends ErrorCollection
{
    public IssuesGadgetErrorCollection()
    {
        super(new ValidationError("jqlQuery", "Wrong JQL query"));
    }
}
