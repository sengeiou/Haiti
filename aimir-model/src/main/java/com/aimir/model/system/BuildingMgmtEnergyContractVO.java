package com.aimir.model.system;

import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

public class BuildingMgmtEnergyContractVO implements JSONString {

	SupplyType supplyType = null;
	Supplier supplier = null;
    String contract ="";
    String contractDate ="";
    
	ContractCapacity contractCapacity= null;
	String supplyLocationStr = "";
	String code="";

	public BuildingMgmtEnergyContractVO(ContractCapacity contractCapacity){		
		this.contractCapacity = contractCapacity;
		this.code = contractCapacity.getContractTypeCode().getName();		
		
		Set<SupplyTypeLocation> supplyLoc = contractCapacity.getSupplyTypeLocations();
		SupplyTypeLocation supplylocation = null;
		if(supplyLoc != null){
		Iterator<SupplyTypeLocation> supplyLocIterator=supplyLoc.iterator();
			int i=0;
			while(supplyLocIterator.hasNext()){
				supplylocation = supplyLocIterator.next();
				if(i!=0)
				supplyLocationStr = supplyLocationStr+","+supplylocation.getLocation().getName();	
				else
					supplyLocationStr = supplyLocationStr+supplylocation.getLocation().getName();	
				
	    		i++;
			}
		}
		this.contract = contractCapacity.getContractNumber();
		this.contractDate = contractCapacity.getContractDate();
		this.supplyType = supplylocation.getSupplyType();
		this.supplier = this.supplyType.getSupplier();	
		System.out.println(toString());
	}
	
	
	public BuildingMgmtEnergyContractVO(SupplyType supplyType){		
		this.supplyType = supplyType;
		this.supplier = supplyType.getSupplier();	
		this.code = supplyType.getTypeCode().getName();		
		
		Set<SupplyTypeLocation> supplyLocation = supplyType.getSupplyTypeLocations();	
				
		Iterator<SupplyTypeLocation> supplyLocatinIterator=supplyLocation.iterator();
		
		while(supplyLocatinIterator.hasNext()){
			SupplyTypeLocation location = supplyLocatinIterator.next();
			contractCapacity = location.getContractCapacity();
			break;
		}
		if(contractCapacity != null){
			
			Set<SupplyTypeLocation> supplyLoc = contractCapacity.getSupplyTypeLocations();
	
			if(supplyLoc != null){
			Iterator<SupplyTypeLocation> supplyLocIterator=supplyLoc.iterator();
				int i=0;
				while(supplyLocIterator.hasNext()){
					SupplyTypeLocation location = supplyLocIterator.next();
					if(i!=0)
					supplyLocationStr = supplyLocationStr+","+location.getLocation().getName();	
					else
						supplyLocationStr = supplyLocationStr+location.getLocation().getName();	
					
		    		i++;
				}
			}
		}
		System.out.println(toString());
	}
	@Override
	public String toString()
	{
	    return "BuildingMgmtEnergyContract "+toJSONString();
	}

	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("contract").value(contract)
    				   .key("supplier").value(this.supplier.getName())
    				   .key("supplierId").value(this.supplier.getId())
    				   .key("contractCapacityId").value((this.contractCapacity == null)? "":this.contractCapacity.getId())
    				   .key("contractCapacity").value((this.contractCapacity == null)? "":this.contractCapacity.getCapacity())
    				   .key("contractDay").value(this.contractDate)
    				   .key("code").value((this.code == null)? "":this.code)
    				   .key("codeId").value((this.contractCapacity == null)? "":contractCapacity.getContractTypeCode().getId())
    				   .key("supplyLocation").value((this.supplyLocationStr.length() == 0)? "":supplyLocationStr).endObject();
    				  
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
	}

}
