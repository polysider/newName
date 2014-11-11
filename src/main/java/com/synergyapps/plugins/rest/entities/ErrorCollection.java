package com.synergyapps.plugins.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class ErrorCollection
{
    /**
     * Errors specific to a certain field.
     */
    @XmlElement
    private Collection<ValidationError> errors = new ArrayList<ValidationError>();

    public ErrorCollection(Collection<ValidationError> errors)
    {
        this.errors.addAll(errors);
    }

    public ErrorCollection(ValidationError error)
    {
        this.errors.add(error);
    }
}
