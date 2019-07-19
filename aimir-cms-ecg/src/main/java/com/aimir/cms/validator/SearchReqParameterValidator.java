package com.aimir.cms.validator;

import com.aimir.cms.constants.CMSConstants.SearchType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;

public class SearchReqParameterValidator {

    public static void validator( AuthCred authCred, String searchType, CMSEnt cmsEnt)
			 throws com.aimir.cms.exception.CMSException {   	

    	if(searchType == null || "".equals(searchType)){
    		throw new CMSException("SearchType is null");
    	}

    	if(!SearchType.getSearchType(searchType).equals(SearchType.EXACT) && !SearchType.getSearchType(searchType).equals(SearchType.LIKE)){
    		throw new CMSException("SearchType Invalid");
    	}
    	
    	if(cmsEnt == null){
    		throw new CMSException("CMSEnt is null");    		
    	}
    	
    	if(cmsEnt.getCustomer() != null){
    		//"Add Customer"
        	if(cmsEnt.getCustomer().getCustomerId() == null 
        			|| "".equals(cmsEnt.getCustomer().getCustomerId())){
        		throw new CMSException("Customer Id is empty");    		
        	}
    	}
    	
    	if(cmsEnt.getSerivcePoint() != null){
    		//"Add ServicePoint"
        	if(cmsEnt.getSerivcePoint().getServPointId() == null 
        			|| "".equals(cmsEnt.getSerivcePoint().getServPointId())){
        		throw new CMSException("ServicePoint Id is empty");    		
        	}
    	}


    	
    	
    	
    }
	
}
