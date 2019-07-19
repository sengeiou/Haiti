package com.aimir.util;

import java.util.Map;

import javax.persistence.Query;

public class CommonUtils {
    /**
     * @desc 쿼리에 페이징 기능을 추가하는 method
     * @param query : 결과값 가져오기 전에 쿼리 
     * @param conditionMap :page, pageSize 값이 들어있는 conditionMap
     * @return Query
     */
    public static Query addPagingForQuery(Query query, Map<String, String> conditionMap)
    {
        String strPage = conditionMap.get("page");
        String strPageSize = conditionMap.get("pageSize");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;
        
        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);
        
        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;
        
        query.setFirstResult(firstResult);      
        query.setMaxResults(pageSize);
        
        
        return query;
        
    }
}
