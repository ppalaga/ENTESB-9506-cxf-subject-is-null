/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.services.interceptor;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.apache.cxf.common.security.SecurityToken;
import org.apache.cxf.common.security.TokenType;
import org.apache.cxf.common.security.UsernameToken;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.LoginSecurityContext;
import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SecurityContextFactory;

/**
 * An {@link Interceptor} that sets the {@link Subject} to JBoss {@link org.jboss.security.SecurityContext} so that
 * {@code PolicyContext.getContext(SecurityConstants.SUBJECT_CONTEXT_KEY)} works when called from an WS endpoint handler
 * bean.
 * <p>
 * Do not use unless you understand what you do!
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class SubjectSettingInterceptor extends AbstractPhaseInterceptor<Message> {

    private static SecurityContext createSecurityContext(final String domain) {
        return AccessController.doPrivileged(new PrivilegedAction<SecurityContext>() {

            @Override
            public SecurityContext run() {
                try {
                    return SecurityContextFactory.createSecurityContext(domain);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void setSecurityContextOnAssociation(final SecurityContext sc) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                SecurityContextAssociation.setSecurityContext(sc);
                return null;
            }
        });
    }

    private String securityDomain;

    public SubjectSettingInterceptor() {
        this(Phase.POST_UNMARSHAL);
    }

    public SubjectSettingInterceptor(String phase) {
        super(phase);
    }

    public String getSecurityDomain() {
        return securityDomain;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        LoginSecurityContext cxfSecurityContext = (LoginSecurityContext) message
                .get(org.apache.cxf.security.SecurityContext.class);
        Subject subject = cxfSecurityContext.getSubject();

        SecurityToken token = message.get(SecurityToken.class);
        if (token != null) {
            // Try authenticating using SecurityToken info
            if (token.getTokenType() != TokenType.UsernameToken) {
                throw new SecurityException(String.format("Unsupported token type: %s", token.getTokenType()));
            }
            final UsernameToken ut = (UsernameToken) token;
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    SecurityContext jbossSecurityContext = SecurityContextAssociation.getSecurityContext();
                    if (jbossSecurityContext == null) {
                        jbossSecurityContext = createSecurityContext(securityDomain);
                        setSecurityContextOnAssociation(jbossSecurityContext);
                    }
                    jbossSecurityContext.getUtil().createSubjectInfo(cxfSecurityContext.getUserPrincipal(), ut.getPassword(),
                            subject);
                    return null;
                }
            });
        } else {
            throw new SecurityException("SecurityToken not set");
        }

    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

}
