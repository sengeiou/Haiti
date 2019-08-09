package com.aimir.mars.integration.metercontrol.util;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.integration.WSMeterConfigUserDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ErrorCode;
import com.aimir.mars.util.UserPasswordUtil;

@Component
public class CXFServerPasswordCallback implements CallbackHandler {
    private final Log _log = LogFactory.getLog(CXFServerPasswordCallback.class);

	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;
	
	@Autowired
	private WSMeterConfigUserDao wsMeterConfigUserDao;
	
    /**
     * Contains map of all users to be authenticated with their passwords to be
     * matched against.
     */
    private Map<String, String> authenticateUsers;

    /**
     * Contains map of possible server certificates with their key passwords.
     */
    private Map<String, String> certUsers;

    // Setters
    public void setAuthenticateUsers(Map<String, String> authenticateUsers) {
        this.authenticateUsers = authenticateUsers;
    }

    public void setCertUsers(Map<String, String> certUsers) {
        this.certUsers = certUsers;
    }

    //
    // These are all the possible callback usages:
    //
    // WSPasswordCallback.UNKNOWN; //0
    // WSPasswordCallback.DECRYPT; //1
    // WSPasswordCallback.USERNAME_TOKEN; //2
    // WSPasswordCallback.SIGNATURE; //3
    // WSPasswordCallback.KEY_NAME; //4
    // WSPasswordCallback.USERNAME_TOKEN_UNKNOWN; //5
    // WSPasswordCallback.SECURITY_CONTEXT_TOKEN; //6
    // WSPasswordCallback.CUSTOM_TOKEN; //7
    // WSPasswordCallback.ENCRYPTED_KEY_TOKEN; //8
    //

    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

        if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
    		TransactionStatus txStatus = null;
    		try {
    			txStatus = txManager.getTransaction(null);
	            // Validate UsernameToken
	        	String userId = pc.getIdentifier();
	        	String dbpassword = wsMeterConfigUserDao.getPassword(userId);
	            if (dbpassword == null) {
	                throw new IOException("usage: " + pc.getUsage()
	                        + " Invalid username: " + pc.getIdentifier());
	            }
	        	String decPassword = UserPasswordUtil.decrypt(dbpassword);
	            pc.setPassword(decPassword);
	            
				txManager.commit(txStatus);
			}
			catch (Exception e) {
				if (txStatus != null) {
					try {
						txManager.rollback(txStatus);
					}
					catch (Exception ee) {}
				}
				_log.error(e, e);
			}
            
        } else if (pc.getUsage() == WSPasswordCallback.DECRYPT) {
            // Set password for the private certificate key
            String password = certUsers.get(pc.getIdentifier());
            if (password == null) {
                throw new IOException("usage: " + pc.getUsage()
                        + " Invalid username: " + pc.getIdentifier());
            }
            pc.setPassword(password);
        } else {
            throw new IOException("Unsupported usage: " + pc.getUsage()
                    + " username/password: " + pc.getIdentifier() + " / "
                    + pc.getPassword());
        }

        _log.debug("Password for " + pc.getIdentifier() + " is "
                + pc.getPassword() + " with usage:" + pc.getUsage());
    }
}