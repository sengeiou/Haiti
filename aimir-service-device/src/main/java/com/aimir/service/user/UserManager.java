package com.aimir.service.user;

import com.aimir.model.user.User;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name="UserService", targetNamespace="http://aimir.com/services")
public interface UserManager {
    @SuppressWarnings("unchecked")
    
    @WebMethod
    @WebResult(name="getsList")
    public List<User> gets();
    
    @WebMethod
    @WebResult(name="getUser")
    public User get(@WebParam(name="userId") String userId);
	
    @WebMethod
    public void add(@WebParam(name="user") User user);
	
    @WebMethod
    public void update(@WebParam(name="user") User user);
	
    @WebMethod
    public void delete(@WebParam(name="userId") String userId);
}
