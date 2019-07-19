package com.aimir.schedule.util;

import java.util.Map;
import java.util.TreeMap;

public class DefinitionManager
{
    private Map definitionMap;

    public DefinitionManager()
    {
        definitionMap = new TreeMap();
    }

    public void addDefinition(String name, JobDefinition def)
    {
        definitionMap.put(name, def);
    }

    public void removeDefinition(String name)
    {
        if(definitionMap.containsKey(name))
            definitionMap.remove(name);
    }

    public JobDefinition getDefinition(String defName)
    {
        return (JobDefinition)definitionMap.get(defName);
    }

    public Map getDefinitions()
    {
        return definitionMap;
    }
}