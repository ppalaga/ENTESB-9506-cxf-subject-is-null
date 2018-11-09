package com.example.services.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.cxf.common.security.SecurityToken;
import org.apache.cxf.common.security.UsernameToken;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.JAASLoginInterceptor;
import org.apache.cxf.interceptor.security.NamePasswordCallbackHandler;
import org.apache.cxf.interceptor.security.callback.CallbackHandlerProvider;
import org.apache.cxf.interceptor.security.callback.CallbackHandlerProviderAuthPol;
import org.apache.cxf.interceptor.security.callback.CallbackHandlerProviderUsernameToken;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

/**
 * Interceptor that utilize the JBoss Security Domain Authentication for cxf web services.
 *
 */
public class OdsJAASLoginInterceptor extends JAASLoginInterceptor {

    Logger logger = Logger.getLogger(OdsJAASLoginInterceptor.class.getSimpleName());
    List<CallbackHandlerProvider>  myCallbackHandlerProviders;
    private Configuration loginConfig;
    public OdsJAASLoginInterceptor(final String phase) {
        super(phase);
        logger.info("OdsJAASLoginInterceptor() ..phase:" + phase);
    }

    public OdsJAASLoginInterceptor() {
        super();
        logger.info("OdsJAASLoginInterceptor() ..");
        this.myCallbackHandlerProviders = new ArrayList<CallbackHandlerProvider>();
        myCallbackHandlerProviders.add(new CallbackHandlerProviderAuthPol());
        myCallbackHandlerProviders.add(new CallbackHandlerProviderUsernameToken());
        setCallbackHandlerProviders(myCallbackHandlerProviders);
        logger.info("myCallbackHandlerProviders added ..size::" + getCallbackHandlerProviders().size());
        
        

    }

    @Override
    public void handleMessage(final Message message) throws Fault {
       /* logger.info("inside handleMessage()..");

        for (final CallbackHandlerProvider cbp : getCallbackHandlerProviders()) {
            logger.info("cbp : " + cbp.getClass().getName());
            final CallbackHandler cbh = cbp.create(message);
            logger.info("cbh : " + cbh);
        }

        final SecurityToken token = message.get(SecurityToken.class);
        logger.info("token : " + token);
        if (!(token instanceof UsernameToken)) {
            logger.info("token is wrong");
        } else {
            logger.info("token is correct");
        }
*/
    	CallbackHandler handler = getFirstCallbackHandler(message);
    	LoginContext ctx;
		try {
			ctx = new LoginContext(getContextName(), null, handler, getLoginConfig());
			ctx.login();
			Subject subject = ctx.getSubject();
			System.out.println("Principal: " +subject.getPrincipals());
			String name = getUsername(handler);
			System.out.println("Name: "+name);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        
        
        super.handleMessage(message);

    }
    private String getUsername(CallbackHandler handler) {
        if (handler == null) {
            return null;
        }
        try {
            NameCallback usernameCallBack = new NameCallback("user");
            handler.handle(new Callback[]{usernameCallBack });
            return usernameCallBack.getName();
        } catch (Exception e) {
            return null;
        }
    }
    private CallbackHandler getFirstCallbackHandler(Message message) {
        for (CallbackHandlerProvider cbp : myCallbackHandlerProviders) {
            CallbackHandler cbh = cbp.create(message);
            if (cbh != null) {
                return cbh;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.cxf.interceptor.security.JAASLoginInterceptor#getCallbackHandler(java.lang.String, java.lang.String)
     */
    @Override
    protected CallbackHandler getCallbackHandler(final String name, final String password) {
        // return new NamePasswordCallbackHandler(name, password, "setCredential");
        logger.info("inside OdsJAASLoginInterceptor.getCallbackHandler() ..");
        return new NamePasswordCallbackHandler(name, password);
    }

    @Override
    public List<CallbackHandlerProvider> getCallbackHandlerProviders() {
        logger.info("inside getCallbackHandlerProviders()");
        final List<CallbackHandlerProvider> cbhp = super.getCallbackHandlerProviders();
        logger.info("chhp:" + cbhp != null ? cbhp.size() + "" : "null");
        return cbhp;
    }

    @Override
    protected SecurityContext createSecurityContext(final String name, final Subject subject) {
        logger.info("inside createSecurityContext: " + name + " : " + subject);
        return super.createSecurityContext(name, subject);
    }

    @Override
    public String getContextName() {
        logger.info("getContextName getCallbackHandlerProviders().size()::" + getCallbackHandlerProviders().size());
        logger.info("super.getContextName(): " + super.getContextName());
        return super.getContextName();
    }

}
