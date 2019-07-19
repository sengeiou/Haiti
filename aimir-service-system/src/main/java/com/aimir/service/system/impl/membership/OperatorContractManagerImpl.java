/**
 * OperatorContractManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.DashboardDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.dao.system.GadgetDao;
import com.aimir.dao.system.GadgetRoleDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.GadgetRole;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Role;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.membership.OperatorContractManager;

/**
 * OperatorContractManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 5.   v1.0       김상연         ContractNo 유효성 체크
 * 2011. 4. 12.  v1.0       김상연         OperatorContract 등록
 * 2011. 4. 13.  v1.0       김상연         OperatorContract 정보 조회 (Operator 조건)
 *
 */

@Service(value="operatorContractManager")
@Transactional(readOnly=false)
public class OperatorContractManagerImpl implements OperatorContractManager{
	
    @Autowired
    OperatorContractDao operatorContractDao;
    
    @Autowired
    CustomerDao customerDao;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    OperatorDao operatorDao;
    
    @Autowired
    DashboardDao dashboardDao;
    
    @Autowired
    GadgetDao gadgetDao;
    
    @Autowired
    DashboardGadgetDao dashboardGadgetDao;

    @Autowired
    RoleDao roleDao;
    
    @Autowired
    GadgetRoleDao gadgetRoleDao;
    
   
	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#checkOperatorContract(com.aimir.model.system.Operator)
	 */
	public boolean checkOperatorContract(Operator operator) {
		
		OperatorContract operatorContract = new OperatorContract();
		
		operatorContract.setOperator(operator);
		
		List<OperatorContract> operatorContracts = operatorContractDao.getOperatorContract(operatorContract);
		
		for (OperatorContract operatorContract2 : operatorContracts) {
			
			if (operatorContract2.getContract() != null) {
				
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#addOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	public OperatorContract addOperatorContract(OperatorContract operatorContract) {

		return operatorContractDao.add(operatorContract);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#getOperatorContractByOperator(com.aimir.model.system.Operator)
	 */
	public List<OperatorContract> getOperatorContractByOperator(Operator operator) {

		OperatorContract operatorContract = new OperatorContract();
		
		operatorContract.setOperator(operator);
		
		List<OperatorContract> operatorContracts = operatorContractDao.getOperatorContract(operatorContract);

		return operatorContracts;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#updateOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	public OperatorContract updateOperatorContract(OperatorContract operatorContract) {
		return operatorContractDao.update(operatorContract);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#deleteOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	public void deleteOperatorContract(OperatorContract operatorContract) {
		operatorContractDao.delete(operatorContract);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#getOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	public List<OperatorContract> getOperatorContract(OperatorContract operatorContract) {
		return operatorContractDao.getOperatorContract(operatorContract);
	}

	/**
	 * method name : getPrepaymentOperatorContractByOperator
	 * method Desc : 로긴 회원의 선불계약정보 리스트를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<OperatorContract> getPrepaymentOperatorContractByOperator(Map<String, Object> conditionMap) {

//	    OperatorContract operatorContract = new OperatorContract();
//
//	    operatorContract.setOperator(operator);

	    List<OperatorContract> operatorContracts = operatorContractDao.getPrepaymentOperatorContract(conditionMap);

	    return operatorContracts;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#saveDashboardGadgetInfo(int)
	 */
	public void saveDashboardGadgetInfo(int operatorID) {

		// 디폴트 대시보드명 정의
		final String DEFAULT_DASHBOARD_MYREPORT = "My Report";        // My Report : 전기,가스,수도 에너지 사용량 요약 대시보드
		final String DEFAULT_DASHBOARD_DASHBOARD = "Dashboard";       // Dashboard : 모든 가젯들을 취합해놓은 대시보드

		// 디폴트 가젯명 정의
		final String DEFAULT_GADGET_MYREPORT = "gadget.hems.myreport";                               // My Report
		final String DEFAULT_GADGET_ELECTCONSUMPTION = "gadget.hems.electconsumption";               // 전기 사용량 조회
		final String DEFAULT_GADGET_GASCONSUMPTION = "gadget.hems.gasconsumption";                   // 가스 사용량 조회
		final String DEFAULT_GADGET_WATERCONSUMPTION = "gadget.hems.waterconsumption";               // 수도 사용량 조회
		final String DEFAULT_GADGET_ELECTSAVINGSGOAL = "gadget.hems.electsavingsgoal";               // 전기 에너지 절감 목표 관리
		final String DEFAULT_GADGET_GASSAVINGSGOAL = "gadget.hems.gassavingsgoal";                   // 가스 에너지 절감 목표 관리
		final String DEFAULT_GADGET_WATERSAVINGSGOAL = "gadget.hems.watersavingsgoal";               // 수도 에너지 절감 목표 관리
		final String DEFAULT_GADGET_ELECTPREPAYMENTCUSTOMER = "gadget.hems.electprepaymentcustomer"; // 전기 선불 관리
		final String DEFAULT_GADGET_GASPREPAYMENTCUSTOMER = "gadget.hems.gasprepaymentcustomer";     // 가스 선불 관리
		final String DEFAULT_GADGET_WATERPREPAYMENTCUSTOMER = "gadget.hems.waterprepaymentcustomer"; // 수도 선불 관리
		final String DEFAULT_GADGET_HOMEDEVICEMGMT = "gadget.hems.homedevicemgmt";                   // 제품 관리
		final String DEFAULT_GADGET_DEMANDRESPONSEMGMT = "gadget.hems.demandresponsemgmt";           // DR 관리

		// 고객 정보 판단 플러그
		boolean isElectricity = false;
		boolean isGas = false;
		boolean isWater = false;
		boolean isDr = false;
		boolean isElectPrepayment = false;
		boolean isGasPrepayment = false;
		boolean isWaterPrepayment = false;

		int gridX = 0;  // 가젯의 배치 X좌표
		int gridY = 0;  // 가젯의 배치 Y좌표

		Gadget gadget = null;
		List<Gadget> list = null;
		List<Gadget> gadget_list = new ArrayList<Gadget>();
		Dashboard dashboard = null;
		List<Dashboard> dashboard_list = null;
		DashboardGadget dashboardGadget = null;

		// 사용자 정보를 취득한다.
		Operator operator = operatorDao.get(operatorID);
        List<OperatorContract> operatorContracts = this.getOperatorContractByOperator(operator);
        Contract contract;
        
        for (OperatorContract operatorContract : operatorContracts) {

        	contract = operatorContract.getContract();
  //      	contractNo.add(operatorContract.getContract().getContractNumber());
			// 에너지 코드 취득
			String code = contract.getServiceTypeCode().getCode();

			if((CommonConstants.MeterType.EnergyMeter.getServiceType()).equals(code)) {	    // 전기 고객일 경우(code="3.1")
				isElectricity = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isElectPrepayment = true;
				}
			} else if((CommonConstants.MeterType.GasMeter.getServiceType()).equals(code)) {	// 가스 고객일 경우(code="3.3")
				isGas = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isGasPrepayment = true;
				}
			} else if((CommonConstants.MeterType.WaterMeter.getServiceType()).equals(code)) {// 수도 고객일 경우(code="3.2")
				isWater = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isWaterPrepayment = true;
				}
			}

			// DR고객인지 판단
			if (contract.getCustomer().getDemandResponse() != null && contract.getCustomer().getDemandResponse()) {
				isDr = true;
			}
        	
        }
	        
/*
		// 추가할 가젯이 무엇인지 계약정보로 판단한다.
//		for (int i = 0; i < contractNo.size(); i++) {
		for(String contractNumber : contractNo) {
			List<Object> _list = contractDao.getContractIdByContractNo(contractNo[i]);
			Contract contract = (Contract)_list.get(0);

			// 에너지 코드 취득
			String code = contract.getServiceTypeCode().getCode();

			if((CommonConstants.MeterType.EnergyMeter.getServiceType()).equals(code)) {	    // 전기 고객일 경우(code="3.1")
				isElectricity = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isElectPrepayment = true;
				}
			} else if((CommonConstants.MeterType.GasMeter.getServiceType()).equals(code)) {	// 가스 고객일 경우(code="3.3")
				isGas = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isGasPrepayment = true;
				}
			} else if((CommonConstants.MeterType.WaterMeter.getServiceType()).equals(code)) {// 수도 고객일 경우(code="3.2")
				isWater = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isWaterPrepayment = true;
				}
			}

			// DR고객인지 판단
			if (contract.getCustomer().getDemandResponse() != null && contract.getCustomer().getDemandResponse()) {
				isDr = true;
			}
		}
*/
		/******************************************************************/
		// 전기 사용량 조회 -> 가스 사용량 조회 -> 수도 사용량 조회 
		// -> 전기 에너지 절감 목표 -> 가스 에너지 절감 목표 -> 수도 에너지 절감 목표
		// -> 전기 선불 관리 -> 가스 선불 관리 -> 수도 선불 관리 -> 제품 관리 -> DR관리
		// 순서로 가젯이 배치되도록 아래와 같은 순서로 리스트에 저장한다.
		/******************************************************************/
		
		////////////////////////////Block 1 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 전기 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}

			gadget_list.add(gadget);
			list.clear();
		}

		// 가스 고객
		if(isGas) {
			// 가스 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 고객
		if(isWater) {
			// 수도 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 1 End///////////////////////////////////////
		
		////////////////////////////Block 2 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 전기 사용량 절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 가스 고객
		if(isGas) {
			// 가스 절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 고객
		if(isWater) {
			// 수도  절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 2 End///////////////////////////////////////
		
		////////////////////////////Block 3 Start/////////////////////////////////////
		// 전기 선불 고객
		if(isElectPrepayment) {
			// 전기 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 가스 선불 고객
		if(isGasPrepayment) {
			// 가스 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 선불 고객
		if(isWaterPrepayment) {
			// 수도 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		////////////////////////////Block 3 End///////////////////////////////////////
		
		////////////////////////////Block 4 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 제품 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_HOMEDEVICEMGMT);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		
		// DemandResponse 고객
		if(isDr) {
			// DR관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_DEMANDRESPONSEMGMT);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 4 End///////////////////////////////////////

        //Role role = roleDao.getRoleByName("customer");
		// 고객 정보와 상관없이 My Report대시보드와 가젯은 디폴트로 등록한다.
		dashboard_list = dashboardDao.getDashboardByName(DEFAULT_DASHBOARD_MYREPORT);
		if(dashboard_list.size() != 0) {
			dashboard = new Dashboard();
			dashboard.setMaxGridX(dashboard_list.get(0).getMaxGridX());
			dashboard.setDescr(dashboard_list.get(0).getDescr());
			dashboard.setMaxGridY(dashboard_list.get(0).getMaxGridY());
			dashboard.setName(dashboard_list.get(0).getName());
			dashboard.setOperator(operator);// 현재 로그인한 고객의 Operator정보를 설정한다.
			dashboard.setOrderNo(dashboard_list.get(0).getOrderNo());
			//dashboard.setRole(role); -> 대시보드 취득하는 로직에서 operator와 상관없이 role에 해당하는 정보를 가져오는 부분이 있기때문에 role설정은 null로 한다.
		}
		// 대시보드 정보를 등록한다.
		dashboard = dashboardDao.add(dashboard);

		// 대시보드 가젯 정보 등록
		dashboardGadget = new DashboardGadget();
		dashboardGadget.setCollapsible(true);
		dashboardGadget.setDashboard(dashboard);
		dashboardGadget.setGadget(gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_MYREPORT).get(0));
		dashboardGadget.setGridX(0);
		dashboardGadget.setGridY(0);
		dashboardGadget.setLayout("fit");
		dashboardGadgetDao.add(dashboardGadget);

		// 상기에서 판단한 디폴트 가젯 정보를 DASHBOARD에 등록한다.
		dashboard_list.clear();
		dashboard_list = dashboardDao.getDashboardByName(DEFAULT_DASHBOARD_DASHBOARD);
		if(dashboard_list.size() != 0) {
			dashboard = new Dashboard();
			dashboard.setMaxGridX(dashboard_list.get(0).getMaxGridX());
			dashboard.setDescr(dashboard_list.get(0).getDescr());
			dashboard.setMaxGridY(dashboard_list.get(0).getMaxGridY());
			dashboard.setName(dashboard_list.get(0).getName());
			dashboard.setOperator(operator);// 현재 로그인한 고객의 Operator정보를 설정한다.
			dashboard.setOrderNo(dashboard_list.get(0).getOrderNo());
//			dashboard.setRole(role); -> 대시보드 취득하는 로직에서 operator와 상관없이 role에 해당하는 정보를 가져오는 부분이 있기때문에 role설정은 null로 한다.
		}
		dashboard = dashboardDao.add(dashboard);		

	    for(int i=0; i<gadget_list.size(); i++) {
			// 가젯 권한 정보 등록
			dashboardGadget = new DashboardGadget();
			dashboardGadget.setCollapsible(true);
			dashboardGadget.setDashboard(dashboard);
			dashboardGadget.setGadget(gadget_list.get(i));
			if(i%3 == 0) {
				gridX = 0;
				gridY = gridY + 1;
			}
			dashboardGadget.setGridX(gridX);
			dashboardGadget.setGridY(gridY);
			dashboardGadget.setLayout("fit");
			gridX++;
			dashboardGadgetDao.add(dashboardGadget);
		}
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.membership.OperatorContractManager#modifyDashboardGadgetInfo(int)
	 */
	public void modifyDashboardGadgetInfo(int operatorId) {

		// 디폴트 대시보드명 정의
//		final String DEFAULT_DASHBOARD_MYREPORT = "My Report";        // My Report : 전기,가스,수도 에너지 사용량 요약 대시보드
		final String DEFAULT_DASHBOARD_DASHBOARD = "Dashboard";       // Dashboard : 모든 가젯들을 취합해놓은 대시보드

		// 디폴트 가젯명 정의
//		final String DEFAULT_GADGET_MYREPORT = "gadget.hems.myreport";                               // My Report
		final String DEFAULT_GADGET_ELECTCONSUMPTION = "gadget.hems.electconsumption";               // 전기 사용량 조회
		final String DEFAULT_GADGET_GASCONSUMPTION = "gadget.hems.gasconsumption";                   // 가스 사용량 조회
		final String DEFAULT_GADGET_WATERCONSUMPTION = "gadget.hems.waterconsumption";               // 수도 사용량 조회
		final String DEFAULT_GADGET_ELECTSAVINGSGOAL = "gadget.hems.electsavingsgoal";               // 전기 에너지 절감 목표 관리
		final String DEFAULT_GADGET_GASSAVINGSGOAL = "gadget.hems.gassavingsgoal";                   // 가스 에너지 절감 목표 관리
		final String DEFAULT_GADGET_WATERSAVINGSGOAL = "gadget.hems.watersavingsgoal";               // 수도 에너지 절감 목표 관리
		final String DEFAULT_GADGET_ELECTPREPAYMENTCUSTOMER = "gadget.hems.electprepaymentcustomer"; // 전기 선불 관리
		final String DEFAULT_GADGET_GASPREPAYMENTCUSTOMER = "gadget.hems.gasprepaymentcustomer";     // 가스 선불 관리
		final String DEFAULT_GADGET_WATERPREPAYMENTCUSTOMER = "gadget.hems.waterprepaymentcustomer"; // 수도 선불 관리
		final String DEFAULT_GADGET_HOMEDEVICEMGMT = "gadget.hems.homedevicemgmt";                   // 제품 관리
		final String DEFAULT_GADGET_DEMANDRESPONSEMGMT = "gadget.hems.demandresponsemgmt";           // DR 관리

		// 고객 정보 판단 플러그
		boolean isElectricity = false;
		boolean isGas = false;
		boolean isWater = false;
		boolean isDr = false;
		boolean isElectPrepayment = false;
		boolean isGasPrepayment = false;
		boolean isWaterPrepayment = false;

		int gridX = 0;  // 가젯의 배치 X좌표
		int gridY = 0;  // 가젯의 배치 Y좌표

		Gadget gadget = null;
		List<Gadget> list = null;
		List<Gadget> gadget_list = new ArrayList<Gadget>();
		Dashboard dashboard = null;
		List<Dashboard> dashboard_list = null;
		DashboardGadget dashboardGadget = null;

		// 사용자 정보를 취득한다.
		Operator operator = operatorDao.get(operatorId);
        List<OperatorContract> operatorContracts = this.getOperatorContractByOperator(operator);
        Contract contract;
        
        for (OperatorContract operatorContract : operatorContracts) {

        	contract = operatorContract.getContract();
  //      	contractNo.add(operatorContract.getContract().getContractNumber());
			// 에너지 코드 취득
			String code = contract.getServiceTypeCode().getCode();

			if((CommonConstants.MeterType.EnergyMeter.getServiceType()).equals(code)) {	    // 전기 고객일 경우(code="3.1")
				isElectricity = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isElectPrepayment = true;
				}
			} else if((CommonConstants.MeterType.GasMeter.getServiceType()).equals(code)) {	// 가스 고객일 경우(code="3.3")
				isGas = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isGasPrepayment = true;
				}
			} else if((CommonConstants.MeterType.WaterMeter.getServiceType()).equals(code)) {// 수도 고객일 경우(code="3.2")
				isWater = true;
				// 선불 고객인지 판단
				if(Code.PREPAYMENT.equals(contract.getCreditType().getCode()) || Code.EMERGENCY_CREDIT.equals(contract.getCreditType().getCode())) {
					isWaterPrepayment = true;
				}
			}

			// DR고객인지 판단
			if (contract.getCustomer().getDemandResponse() != null && contract.getCustomer().getDemandResponse()) {
				isDr = true;
			}
        }
		
		/******************************************************************/
		// 전기 사용량 조회 -> 가스 사용량 조회 -> 수도 사용량 조회 
		// -> 전기 에너지 절감 목표 -> 가스 에너지 절감 목표 -> 수도 에너지 절감 목표
		// -> 전기 선불 관리 -> 가스 선불 관리 -> 수도 선불 관리 -> 제품 관리 -> DR관리
		// 순서로 가젯이 배치되도록 아래와 같은 순서로 리스트에 저장한다.
		/******************************************************************/
		
		////////////////////////////Block 1 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 전기 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}

			gadget_list.add(gadget);
			list.clear();
		}

		// 가스 고객
		if(isGas) {
			// 가스 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 고객
		if(isWater) {
			// 수도 사용량 조회 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERCONSUMPTION);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 1 End///////////////////////////////////////
		
		////////////////////////////Block 2 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 전기 사용량 절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 가스 고객
		if(isGas) {
			// 가스 절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 고객
		if(isWater) {
			// 수도  절감 목표 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERSAVINGSGOAL);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 2 End///////////////////////////////////////

		////////////////////////////Block 3 Start/////////////////////////////////////
		// 전기 선불 고객
		if(isElectPrepayment) {
			// 전기 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_ELECTPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}



		// 가스 선불 고객
		if(isGasPrepayment) {
			// 가스 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_GASPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// 수도 선불 고객
		if(isWaterPrepayment) {
			// 수도 선불 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_WATERPREPAYMENTCUSTOMER);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		////////////////////////////Block 3 End///////////////////////////////////////

		////////////////////////////Block 4 Start/////////////////////////////////////
		// 전기 고객
		if(isElectricity) {
			// 제품 관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_HOMEDEVICEMGMT);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}

		// DemandResponse 고객
		if(isDr) {
			// DR관리 가젯
			list = gadgetDao.getGadgetByGadgetCode(DEFAULT_GADGET_DEMANDRESPONSEMGMT);
			if(list.size() != 0) {
				gadget = list.get(0);
			}
			gadget_list.add(gadget);
			list.clear();
		}
		////////////////////////////Block 4 End///////////////////////////////////////

		// 기존에 등록한 로그인 유저의 디폴트 대시보드 정보를 취득한다.
		dashboard_list = dashboardDao.getDashboardByNameOpeatorId(DEFAULT_DASHBOARD_DASHBOARD, operatorId);

		int dashboardId = dashboard_list.get(0).getId();

		// 대시보드 가젯정보를  등록&업데이트 한다.
	    for(int i=0; i<gadget_list.size(); i++) {

	    	int gadgetId = gadget_list.get(i).getId();

	    	List<DashboardGadget> dashboardGadget_list = dashboardGadgetDao.getDashboardGadgetByDashboardIdGadgetId(dashboardId, gadgetId);

	    	if(dashboardGadget_list.size() != 0){
	    		for(DashboardGadget rst : dashboardGadget_list) {
	    			dashboardGadgetDao.saveOrUpdate(rst);
	    			if(i%3 == 0) {
	    				gridX = 0;
	    				gridY = gridY + 1;
	    			}
	    		}
	    	} else {
    			if(i%3 == 0) {
    				gridX = 0;
    				gridY = gridY + 1;
    			}
	    		dashboardGadget = new DashboardGadget();
	    		dashboardGadget.setCollapsible(true);
				dashboardGadget.setDashboard(dashboard_list.get(0));
				dashboardGadget.setGadget(gadget_list.get(i));
				dashboardGadget.setGridX(gridX);
				dashboardGadget.setGridY(gridY);
				dashboardGadget.setLayout("fit");
				dashboardGadgetDao.add(dashboardGadget);
	    	}
	    	gridX++;
		}
	}
}
