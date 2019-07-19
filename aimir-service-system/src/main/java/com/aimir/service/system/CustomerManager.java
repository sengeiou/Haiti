package com.aimir.service.system;

import com.aimir.model.system.Customer;
import com.aimir.util.Condition;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

@WSDLDocumentation(value="Energy Consumer(Customer) Information Management Service", placement=WSDLDocumentation.Placement.TOP)
@WebService(name="CustomerService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface CustomerManager {
	
    public List<Customer> getCustomers();
    
    public void update(Customer customer);
	
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get the customer information by customer ID", placement=WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value="customer object", placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
	@WebMethod
	@WebResult(name="CustomerInstance")
    public Customer getCustomer(
    		@WebParam(name="customerId") Integer customerId);
	
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Adding customer information", placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
	@WebMethod
    public void addCustomer(
    		@WebParam(name="customer") Customer customer);
	
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Updating customer information", placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
	@WebMethod
    public void updateCustomer(
    		@WebParam(name="customer") Customer customer);
	
	@WSDLDocumentation("Deleting customer information")
	@WebMethod
	public void deleteCustomer(
			@WebParam(name="customer") Customer customer);
	
	@WSDLDocumentation("Get a customer information to the login id")
	@WebMethod
	@WebResult(name="CustomersByLoginId")
	public Customer getCustomersByLoginId(
			@WebParam(name="loginId") String loginId);
	
    @WebMethod
    @WebResult(name="loginIdOverlapCheck")
    public int loginIdOverlapCheck(
            @WebParam(name="loginId") String loginId,
            @WebParam(name="customerNo") String customerNo
            );
	
	@WSDLDocumentation("Check duplicate customer number - Return Count, If you can not duplicate returns 0")
	@WebMethod
	@WebResult(name="idOverlapCheck")
	public int idOverlapCheck(
			@WebParam(name="customerNo") String customerNo);

	/**
	 * method name : checkCustomerNoLoginMapping
     * method Desc : 입력받은 CustomerNumber가 다른 customer의 Login아이디와 매핑되어있는지 체크한다.
     * 
	 * @param customerNo
	 * @return
	 */
	@WebMethod
    @WebResult(name="checkCustomerNoLoginMapping")
    public Map<String, String> checkCustomerNoLoginMapping(@WebParam(name="customerNo") String customerNo);
	
	@WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get a list of customers from customer number or customer name and range(first page, max count)", placement=WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value="Customer No", placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value="Customer Name", placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value="First Name", placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value="Max", placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT)
            })
	@WebMethod
	@WebResult(name="CustomerList")
	public List<Customer> customerSearchList(
			@WebParam(name="customerNo") String customerNo, 
			@WebParam(name="name") String name, 
			@WebParam(name="firstname") String first, 
			@WebParam(name="max") String max);
	
	@WSDLDocumentation("Get a list of customers from customer number or customer name")
	@WebMethod
	@WebResult(name="CustomerListCount")
	public Map<String,String> customerSearchListCount(
			@WebParam(name="customerNo") String customerNo, 
			@WebParam(name="name") String name);
	
	
	public Map<String, Object> getCustomerContractInfo(Map<String,Object> params);
	
	@WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value="Get a list of customers and contracts",placement=WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters Map \r\n"
                        + " ($key=supplierId, $valueType=String, @description=Supplier.id ) \r\n"
                        + " ($key=yyyyMMdd, $valueType=String, @description=yyyyMMdd ) \r\n"
                        + " ($key=serviceType, $valueType=String, @description=Contract.serviceTypeCode.code )",
                               placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),                    
                @WSDLDocumentation(value = "Return Map \r\n"
                        + " ($key=chart, $valueType=Map<String, Object>, @description=Today's customers the supply of health statistics) \r\n"
                        + " chart data value \r\n"
                        + " ($key=today, $valueType=String, @description=yyyyMMdd) \r\n"
                        + " ($key=totalCount, $valueType=String, @description=number of total customers) \r\n"
                        + " ($key=normal, $valueType=int, @description=Customer's supply is normal number of customers) \r\n"
                        + " ($key=pause, $valueType=int, @description=Customer's supply is pause number of customers) \r\n"
                        + " ($key=stop, $valueType=int, @description=Customer's supply is stop number of customers) \r\n"
                        + " ($key=cancel, $valueType=int, @description=Customer's supply is cancel number of customers) \r\n"
                        + " ($key=todayNormal, $valueType=int, @description=Today's supply is normal number of customers) \r\n"
                        + " ($key=todayPause, $valueType=int, @description=Today's supply is pause number of customers) \r\n"
                        + " ($key=todayStop, $valueType=int, @description=Today's supply is stop number of customers) \r\n"
                        + " ($key=todayCancel, $valueType=int, @description=Today's supply is cancel number of customers) \r\n"
                        + " ($key=grid, $valueType=List<Object>, @description=Object (tariffType, tariffCount)",
                                placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )   
    @WebMethod
    @WebResult(name="CustomerContractMap")
	public Map<String, Object> getCustomerContractInfoByparam(
            @WebParam(name="Supplier.id") String supplierId,
            @WebParam(name="yyyyMMdd")  String yyyymmdd,
            @WebParam(name="Contract.serviceTypeCode.code") String serviceType);
	
	@WSDLDocumentation("Delete customer information by customer ID")
	@WebMethod
	public void customerDelete(
			@WebParam(name="id") Integer id);

	public Integer getTotalCustomer(Map<String, Object> conditionMap);
	
	@WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value="Get the total number of customers ", placement=WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters Map \r\n"
                        + " ($key=customerName, $valueType=String, @description=Customer.name ) \r\n"
                        + " ($key=location, $valueType=String, @description=Location.id) \r\n"
                        + " ($key=mdsId, $valueType=String, @description=Meter.mdsId) \r\n"
                        + " ($key=address, $valueType=String, @description=Customer.address) \r\n"
                        + " ($key=serviceType, $valueType=String, @description=Contract.serviceType.id) \r\n"
                        + " ($key=supplierId, $valueType=String, @description=Supplier.id)",
                               placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Returns the total number of customer",
                               placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )   
    @WebMethod
    @WebResult(name="TotalCustomerCount")
	public Integer getTotalCustomerByParam(
	        @WebParam(name="Customer.name")String customerName,
	        @WebParam(name="Location.id")String location,
	        @WebParam(name="Meter.mdsId")String mdsId,
	        @WebParam(name="Customer.address")String address,
	        @WebParam(name="Contract.serviceType.id")String serviceType,
	        @WebParam(name="Supplier.id")String supplierId);
	
	@WebMethod
	@WebResult(name="TitleName")
	public String getTitleName(
			@WebParam(name="excel") String excel, 
			@WebParam(name="ext") String ext);
	
	@WebMethod
	@WebResult(name="readExcelXLS")
	public Map<String,Object> readExcelXLS(
			@WebParam(name="excel") String excel);
	
	@WebMethod
	@WebResult(name="saveExcelXLS")
	public Map<String,Object> saveExcelXLS(
			@WebParam(name="excel") String excel,
			@WebParam(name="supplierId") int supplierId);
	
	@WebMethod
	@WebResult(name="readExcelXLSX")
	public Map<String,Object> readExcelXLSX(
			@WebParam(name="excel") String excel);
	
	@WebMethod
	@WebResult(name="saveExcelXLSX")
	public Map<String,Object> saveExcelXLSX(
			@WebParam(name="excel") String excel,
			@WebParam(name="supplierId") int supplierId);
	
	@WSDLDocumentation("Adding customer information")
	@WebMethod
	@WebResult(name="insertCustomer")
	public Customer insertCustomer(
			@WebParam(name="customer") Customer customer);
	
	@WSDLDocumentation("Create new customerNumber using next customer.id")
	@WebMethod
	@WebResult(name="createNewCustomerNumber")
	public String createNewCustomerNumber();

	@WebMethod
	@WebResult(name="customerSearchListFindSet")
	public List<Customer> customerSearchListFindSet(Set<Condition> set);
	
    /**
     * method name : getCustomerListByRole<b/>
     * method Desc : User Management 가젯에서 Operator(Customer) List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="customerListByRole")
    public List<Map<String, Object>> getCustomerListByRole(Map<String, Object> conditionMap);
    
    /**
     * method name : getCustomerListByRoleTotalCount<b/>
     * method Desc : User Management 가젯에서 Operator(Customer) List Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="customerListByRoleTotal")
    public Integer getCustomerListByRoleTotalCount(Map<String, Object> conditionMap);
    
	@WebMethod
	@WebResult(name="customerForUser")
	public Customer getCustomerForUser(Integer userId);

}
