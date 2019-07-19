package com.aimir.mars.util;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.common.ext.WSPasswordCallback;

public class CXFClientPasswordCallback implements CallbackHandler {
    private final Log _log = LogFactory.getLog(CXFClientPasswordCallback.class);

    /**
     * Contains map of all possible client users and their passwords.
     */
    private Map<String, String> clientUsers;

    /**
     * Contains map of all possible certificate users and their key passwords.
     */
    private Map<String, String> certUsers;

    // Setters
    public void setClientUsers(Map<String, String> clientUsers) {
        this.clientUsers = clientUsers;
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

    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

        Map<String, String> mapUsers;
        if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
            // Set UsernameToken password for outgoing messages
            mapUsers = clientUsers;
        } else if (pc.getUsage() == WSPasswordCallback.SIGNATURE) {
            // Set the password to the private certificate key
            mapUsers = certUsers;
        } else {
            throw new IOException("Unsupported usage: " + pc.getUsage());
        }

        String password = mapUsers.get(pc.getIdentifier());
        if (password == null) {
            throw new IOException("Invalid username: " + pc.getIdentifier());
        }
        pc.setPassword(password);

        _log.debug("Password for " + pc.getIdentifier() + " is "
                + pc.getPassword() + " with usage:" + pc.getUsage());
    }
}