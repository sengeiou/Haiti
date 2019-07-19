package com.aimir.bo;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class ExcludeFilter implements TypeFilter
{
    private static Log log = LogFactory.getLog(ExcludeFilter.class);

    @Override
    public boolean match(MetadataReader arg0, MetadataReaderFactory arg1)
            throws IOException {
        String name = arg0.getAnnotationMetadata().getClassName();
        if (name.contains("com.aimir.fep")) {
            log.debug("Exclude Class[" + name + "]");
            return true;
        }
        return false;
    }

}

