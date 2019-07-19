package com.aimir.mars.util;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.common.ext.WSPasswordCallback;

public class CXFServerPasswordCallback implements CallbackHandler {
    private final Log _log = LogFactory.getLog(CXFServerPasswordCallback.class);

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
            // Validate UsernameToken
            String password = authenticateUsers.get(pc.getIdentifier());
            if (password == null) {
                throw new IOException("usage: " + pc.getUsage()
                        + " Invalid username: " + pc.getIdentifier());
            }
            pc.setPassword(password);
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