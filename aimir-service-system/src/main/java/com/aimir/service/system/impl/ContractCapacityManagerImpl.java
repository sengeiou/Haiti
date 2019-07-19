package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ContractCapacityDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.util.Condition;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.Condition.Restriction;

@WebService(endpointInterface = "com.aimir.service.system.ContractCapacityManager")
@Service(value = "contractCapacityManager")
@Transactional
public class ContractCapacityManagerImpl implements ContractCapacityManager {

	Log logger = LogFactory.getLog(ContractCapacityManagerImpl.class);

	@Autowired
	ContractCapacityDao dao;

	@Autowired
	SupplyTypeDao supplytypeDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	TariffTypeDao tariffTypeDao;
	
	
	@Autowired
	SupplierDao supplierDao;

	public void add(ContractCapacity contractCapacity) {
		dao.add(contractCapacity);
	}

	public void delete(int contractCapacityId) {
		dao.deleteById(contractCapacityId);

	}

	public ContractCapacity getContractCapacity(int contractCapacityId) {
		return dao.get(contractCapacityId);

	}

	public void update(ContractCapacity contractCapacity) {
		dao.update(contractCapacity);

	}

	public List<ContractCapacity> getContractCapacityList() {
		
		return dao.getContractCapacityList();
	}

	public List<ContractCapacity> getContractCapacityList(int page, int count) {
		return dao.getContractCapacityList(page, count);
	}

	public Map<String, Object> getLocationSupplierContract(Map<String, Object> params) {
		
		Integer discrete = (Integer)params.get("discrete");
		Integer supplierId = (Integer)params.get("supplierId");
		Map<String, Object> retMap = new HashMap<String, Object>();
     
		if (discrete == 1) {
			List<SupplyType> supplyTypeList = supplytypeDao
					.getSupplyTypeBySupplierId(supplierId);
			List<Object> contractTypelist = new ArrayList<Object>();
			
			for (SupplyType supplyType : supplyTypeList) {
				
				List<TariffType> codeList = tariffTypeDao.getTariffTypeBySupplier(supplyType
						.getTypeCode().getCode(), supplierId);
				for (TariffType code : codeList) {
					Map<String, Object> codeMap = new HashMap<String, Object>();
					codeMap.put("name", code.getName());
					codeMap.put("id", code.getId()+"");
					contractTypelist.add(codeMap);
				}
			}
			 
			retMap.put("contractTypelist", contractTypelist);
			return retMap;

		}
		List<Object> locationArray = new ArrayList<Object>();

		List<Location> root = locationDao.getParentsBySupplierId(supplierId);

		
		
		if (root.size() > 0) {
//			locationArray.add(root.get(0));
			Map<String, Object> locMap = new HashMap<String, Object>();
			locMap.put("name", root.get(0).getName());
			locMap.put("id", root.get(0).getId()+"");
			locationArray.add(locMap);
			Set<Location> child = root.get(0).getChildren();
			Iterator<Location> childIterator = child.iterator();

			int i = 0;
			while (childIterator.hasNext()) {
				Location loc = childIterator.next();
				Map<String, Object> locMap1 = new HashMap<String, Object>();
				locMap1.put("name", loc.getName());
				locMap1.put("id", loc.getId()+"");
				locationArray.add(locMap1);
				i++;
			}

		}

		if (discrete == 0) {
			retMap.put("locationList", locationArray);
			return retMap;
		}
		
		Set<Condition> condition = new HashSet<Condition>(0);
		condition.add(new Condition("typeCode",
				new Object[] { }, null, Restriction.NOTNULL));
	
		List<SupplyType> supplyTypeList =supplytypeDao.findByConditions(condition);
		List<Object> supplierList = new ArrayList<Object>();
		
		HashMap supplierHash = new HashMap();
		for (SupplyType supplyType : supplyTypeList) {
			if (!supplierHash.containsKey(supplyType.getSupplier().getId())) {
				supplierHash.put(supplyType.getSupplier().getId(), supplyType
						.getSupplier());
				Map<String, Object> supplier = new HashMap<String, Object>();
				supplier.put("name", supplyType.getSupplier().getName());
				supplier.put("id", supplyType.getSupplier().getId()+"");
				
				supplierList.add(supplier);
			}
		}

		List<Object> contractList = new ArrayList<Object>();

		List<ContractCapacity> contractCapacityList = getContractCapacityList();
		
		for (ContractCapacity contractCapacity : contractCapacityList) {
			
			Map<String, Object> contract = new HashMap<String, Object>();
			String supplyLocationStr = "";

			Set<SupplyTypeLocation> supplyLoc = contractCapacity
					.getSupplyTypeLocations();
			SupplyTypeLocation supplylocation = null;
			if (supplyLoc != null) {
				Iterator<SupplyTypeLocation> supplyLocIterator = supplyLoc
						.iterator();
				int i = 0;
				while (supplyLocIterator.hasNext()) {
					supplylocation = supplyLocIterator.next();
					if (i != 0)
						supplyLocationStr = supplyLocationStr + ","
								+ supplylocation.getLocation().getName();
					else
						supplyLocationStr = supplyLocationStr
								+ supplylocation.getLocation().getName();

					i++;
				}
			}
			contract.put("codeId", contractCapacity.getContractTypeCode()
					.getId());
			contract.put("code", contractCapacity.getContractTypeCode()
					.getName());
			contract.put("contract", contractCapacity.getContractNumber());
			
			Supplier localeSupplier = supplierDao.get(supplierId);
			
			contract.put("contractDay", TimeLocaleUtil.getLocaleDate(contractCapacity.getContractDate(), localeSupplier.getLang().getCode_2letter(), localeSupplier.getCountry().getCode_2letter()));

			SupplyType supplyType = supplylocation.getSupplyType();
			contract.put("supplierId", supplyType.getSupplier().getId());
			contract.put("supplier", supplyType.getSupplier().getName());
			contract.put("contractCapacityId", contractCapacity.getId());
			contract.put("contractCapacity", contractCapacity.getCapacity()+"");
			contract.put("supplyLocation", supplyLocationStr);
			
			contractList.add(contract);

		}

		retMap.put("contractList", contractList);
		retMap.put("supplierList", supplierList);
		retMap.put("locationList", locationArray);
		
		return retMap;
	}

	public boolean contractEnergyExistCheck(Integer serviceTypeId,Integer locationId) {
		List<Object> exist = dao.contractEnergyExistCheck(serviceTypeId,locationId);
		
	//	System.out.println("exist:"+exist);
		if(exist.size() >0){
			return true;
		}else{
			return false;
		}
	}

}
