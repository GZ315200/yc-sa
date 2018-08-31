package com.unistacks.tamboo.kafkaclient.sasl;

import com.unistacks.tamboo.kafkaclient.pojo.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.Map;

public class ClientPlainLoginModule implements LoginModule {
    private static  Logger logger = LoggerFactory.getLogger(ClientPlainLoginModule.class);

    private static  String USERNAME_CONFIG = "username";
    private static  String PASSWORD_CONFIG = "password";

    private static Credential credential = new Credential();

    public static void setCredential(String publicCredential, String privateCredential) {
        if (credential.isSet()) {
            logger.info("Client has been logined with PublicCredential: " + credential.getPublicCredential());
            return;
        }
        credential.setPublicCredential(publicCredential);
        credential.setPrivateCredential(privateCredential);
    }

    public static String getPublicCredential() {
        return credential.getPublicCredential();
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        String publicCredential = credential.getPublicCredential();
        String privateCredential = credential.getPrivateCredential();
        if (publicCredential != null) {
            subject.getPublicCredentials().add(publicCredential);
        }
        if (privateCredential != null) {
            subject.getPrivateCredentials().add(privateCredential);
        }
        logger.info("Login... publicCredential = " + publicCredential);
    }

    public boolean login() throws LoginException {
        return true;
    }

    public boolean logout() throws LoginException {
        return true;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public boolean abort() throws LoginException {
        return false;
    }
}
