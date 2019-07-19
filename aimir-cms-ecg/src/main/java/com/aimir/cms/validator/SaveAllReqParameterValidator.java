package com.aimir.cms.validator;

import com.aimir.cms.constants.CMSConstants.ErrorType;
import com.aimir.cms.exception.CMSException;
import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.TariffEnt;

public class SaveAllReqParameterValidator {

    public static void validator( AuthCred authCred, CMSEnt cmsEnt, String saveAllType)
			 throws com.aimir.cms.exception.CMSException {   	
        if (saveAllType == null) {
            throw new CMSException(ErrorType.Error.getIntValue(), "SaveAllType is empty");
        }
        
    	if(cmsEnt == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "CMSEnt is empty");    		
    	}
    	
    	if(cmsEnt.getCustomer() == null && cmsEnt.getSerivcePoint() == null){
    		throw new CMSException(ErrorType.Error.getIntValue(), "DataItem is empty");   
    	}
    	
    	if(cmsEnt.getCustomer() != null){
    		if(cmsEnt.getCustomer().getCustomerId() == null || "".equals(cmsEnt.getCustomer().getCustomerId())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "CustomerId is empty");    	
        	}
    		/*
        	if(cmsEnt.getCustomer().getSurname() == null || "".equals(cmsEnt.getCustomer().getSurname())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Surname is empty");    	
        	}
        	if(cmsEnt.getCustomer().getOtherNames() == null || "".equals(cmsEnt.getCustomer().getOtherNames())){
        		throw new CMSException("Other_names is empty");    	
        	}
        	if(cmsEnt.getCustomer().getIdNo() == null || "".equals(cmsEnt.getCustomer().getIdNo())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Id_no is empty");    	
        	}    	
        	if(cmsEnt.getCustomer().getIdType() == null || "".equals(cmsEnt.getCustomer().getIdType())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Id_type is empty");    	
        	}    	
        	if(cmsEnt.getCustomer().getAddress1() == null || "".equals(cmsEnt.getCustomer().getAddress1())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Address1 is empty");    	
        	}
        	if(cmsEnt.getCustomer().getAddress2() == null || "".equals(cmsEnt.getCustomer().getAddress2())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Address2 is empty");    	
        	}
        	if(cmsEnt.getCustomer().getAddress3() == null || "".equals(cmsEnt.getCustomer().getAddress3())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Address3 is empty");    	
        	}    	
        	if(cmsEnt.getCustomer().getTelephone1() == null || "".equals(cmsEnt.getCustomer().getTelephone1())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Telephone_1 is empty");    	
        	}
        	if(cmsEnt.getCustomer().getTelephone2() == null || "".equals(cmsEnt.getCustomer().getTelephone2())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Telephone_2 is empty");    	
        	}
        	if(cmsEnt.getCustomer().getTelephone3() == null || "".equals(cmsEnt.getCustomer().getTelephone3())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Telephone_3 is empty");    	
        	}
        	if(cmsEnt.getCustomer().getFax() == null || "".equals(cmsEnt.getCustomer().getFax())){
        		throw new CMSException("Fax is empty");    	
        	}
        	if(cmsEnt.getCustomer().getEmail() == null || "".equals(cmsEnt.getCustomer().getEmail())){
        		throw new CMSException("email is empty");    	
        	} 
        	if(cmsEnt.getCustomer().getTaxRefNo() == null || "".equals(cmsEnt.getCustomer().getTaxRefNo())){
        		throw new CMSException(ErrorType.Error.getIntValue(), "Tax_Ref_No is empty");    	
        	}
        	*/
    		if(cmsEnt.getCustomer().getSurname() == null)
                cmsEnt.getCustomer().setSurname("");
            
            if(cmsEnt.getCustomer().getOtherNames() == null)
                cmsEnt.getCustomer().setOtherNames("");
            
            if (cmsEnt.getCustomer().getIdNo() == null)
                cmsEnt.getCustomer().setIdNo("");
            
            if (cmsEnt.getCustomer().getIdType() == null)
                cmsEnt.getCustomer().setIdType("");
            
            if (cmsEnt.getCustomer().getAddress1() == null)
                cmsEnt.getCustomer().setAddress1("");
            
            if (cmsEnt.getCustomer().getAddress2() == null)
                cmsEnt.getCustomer().setAddress2("");
            
            if (cmsEnt.getCustomer().getAddress3() == null)
                cmsEnt.getCustomer().setAddress3("");
            
            if (cmsEnt.getCustomer().getTelephone1() == null)
                cmsEnt.getCustomer().setTelephone1("");
            
            if (cmsEnt.getCustomer().getTelephone2() == null)
                cmsEnt.getCustomer().setTelephone2("");
            
            if (cmsEnt.getCustomer().getTelephone3() == null)
                cmsEnt.getCustomer().setTelephone3("");
            
            if (cmsEnt.getCustomer().getFax() == null)
                cmsEnt.getCustomer().setFax("");
            
            if (cmsEnt.getCustomer().getEmail() == null)
                cmsEnt.getCustomer().setEmail("");
            
            if (cmsEnt.getCustomer().getTaxRefNo() == null)
                cmsEnt.getCustomer().setTaxRefNo("");
            
            if (cmsEnt.getCustomer().isExist() == null)
                cmsEnt.getCustomer().setExist(false);
    	}
    	
    	if(cmsEnt.getSerivcePoint() != null){
    		if(cmsEnt.getSerivcePoint().getServPointId() == null || "".equals(cmsEnt.getSerivcePoint().getServPointId())){
    			throw new CMSException(ErrorType.Error.getIntValue(), "servPointID is empty"); 
    		}
    		/*
    		if(cmsEnt.getSerivcePoint().getAddress1() == null || "".equals(cmsEnt.getSerivcePoint().getAddress1())){
    			throw new CMSException(ErrorType.Error.getIntValue(), "Address_1 is empty"); 
    		}
    		if(cmsEnt.getSerivcePoint().getAddress1() == null || "".equals(cmsEnt.getSerivcePoint().getAddress2())){
    			throw new CMSException(ErrorType.Error.getIntValue(), "Address_2 is empty"); 
    		}
    		if(cmsEnt.getSerivcePoint().getAddress1() == null || "".equals(cmsEnt.getSerivcePoint().getAddress3())){
    			throw new CMSException(ErrorType.Error.getIntValue(), "Address_3 is empty"); 
    		}    		
    		if(cmsEnt.getSerivcePoint().getGeoCode() == null || "".equals(cmsEnt.getSerivcePoint().getGeoCode())){
    			throw new CMSException(ErrorType.Error.getIntValue(), "Geo_code is empty"); 
    		}
    		
    		if(cmsEnt.getSerivcePoint().getTariff() != null){
    			if(cmsEnt.getSerivcePoint().getTariff().getTariffCode() == null || "".equals(cmsEnt.getSerivcePoint().getTariff().getTariffCode())){
        			throw new CMSException(ErrorType.Error.getIntValue(), "Tariff_code is empty"); 
        		}
    			if(cmsEnt.getSerivcePoint().getTariff().getTariffGroup() == null){
        			throw new CMSException(ErrorType.Error.getIntValue(), "Tariff_group is empty"); 
        		}
    		}
    		
    		if(cmsEnt.getSerivcePoint().getBlockFlag() == null) {
    		    throw new CMSException(ErrorType.Error.getIntValue(), "blockFlag is empty");
    		}
    		else {
    		    if (cmsEnt.getSerivcePoint().getBlockReason() == null) {
    		        throw new CMSException(ErrorType.Error.getIntValue(), "blockReason is empty");
    		    }
    		}
    		*/
    		if (cmsEnt.getSerivcePoint().getAddress1() == null)
                cmsEnt.getSerivcePoint().setAddress1("");
            
            if (cmsEnt.getSerivcePoint().getAddress2() == null)
                cmsEnt.getSerivcePoint().setAddress2("");
            
            if (cmsEnt.getSerivcePoint().getAddress3() == null)
                cmsEnt.getSerivcePoint().setAddress3("");
            
            if (cmsEnt.getSerivcePoint().getBlockReason() == null)
                cmsEnt.getSerivcePoint().setBlockReason("");
            
            if (cmsEnt.getSerivcePoint().getBlockFlag() == null)
                cmsEnt.getSerivcePoint().setBlockFlag(false);
            
            if (cmsEnt.getSerivcePoint().getGeoCode() == null)
                cmsEnt.getSerivcePoint().setGeoCode("");
            
            if (cmsEnt.getSerivcePoint().isExist() == null)
                cmsEnt.getSerivcePoint().setExist(false);
    	}
    	
    	if (saveAllType.equals("f_add_serv_point") || saveAllType.equals("f_mod_serv_point")) {
    	    TariffEnt tariff = cmsEnt.getSerivcePoint().getTariff();
    	    
    	    if (tariff != null && !"".equals(tariff.getTariffCode())) {
    	        if (tariff.getTariffCode().equals("E11")) {
    	            if (tariff.getTariffGroup() == 1);
    	            else if (tariff.getTariffGroup() == 2);
    	            else {
    	                throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
    	            }
    	        }
    	        else if (tariff.getTariffCode().equals("E12")) {
    	            if (tariff.getTariffGroup() == 1);
    	            else if (tariff.getTariffGroup() == 2);
    	            else if (tariff.getTariffGroup() == 3);
    	            else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
    	        }
                else if(tariff.getTariffCode().equals("PRE-RESIDENTIAL")) {//for iraq
                	if (tariff.getTariffGroup() == 1);
    	            else if (tariff.getTariffGroup() == 2);
    	            else if (tariff.getTariffGroup() == 3);
    	            else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
                }                	
                else if(tariff.getTariffCode().equals("PRE-INDUSTRIAL")){//for iraq
                	if (tariff.getTariffGroup() == 1);
                	else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
                }                	
                else if(tariff.getTariffCode().equals("PRE-COMMERCIAL")){//for iraq
                	if (tariff.getTariffGroup() == 1);
                	else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
                }                	
                else if(tariff.getTariffCode().equals("PRE-GOVERNMENT")){//for iraq
                	if (tariff.getTariffGroup() == 1);
                	else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
                }                	
                else if(tariff.getTariffCode().equals("PRE-AGRICULTURE")){//for iraq
                	if (tariff.getTariffGroup() == 1);
                	else {
                        throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Group"); 
                    }
                }                	
    	        else {
                    throw new CMSException(ErrorType.Error.getIntValue(), "Invalid Tariff Code"); 
                }
    	    }
    	}
    	
    }
	
}
