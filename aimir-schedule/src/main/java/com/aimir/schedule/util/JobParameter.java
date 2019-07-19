package com.aimir.schedule.util;

public class JobParameter
{
    private String name;
    private String description;
    private boolean required;
    private String inputMask;

    public JobParameter()
    {
    }

    public JobParameter(String name, String description, String inputMask)
    {
        this.name = name;
        this.description = description;
        this.inputMask = inputMask;
    }

    public String getDescription()
    {
        return description;
    }

    public String getInputMask()
    {
        return inputMask;
    }

    public String getName()
    {
        return name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setInputMask(String inputMask)
    {
        this.inputMask = inputMask;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }
}