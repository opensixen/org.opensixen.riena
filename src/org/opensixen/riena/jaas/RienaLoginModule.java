package org.opensixen.riena.jaas;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.eclipse.riena.security.common.authentication.AuthenticationTicket;
import org.eclipse.riena.security.common.authentication.RemoteLoginProxy;



public class RienaLoginModule implements LoginModule {

	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, ?> sharedState;
	private String username;
	private char[] password;
	private Map<String, ?> options;
	
	private Set<Principal> principal;
	private AuthenticationTicket ticket;

	public RienaLoginModule() {
		
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

	}

	@Override
	public boolean login() throws LoginException {
		Callback[] callbacks = getCallbacks();		
		RemoteLoginProxy proxy = new RemoteLoginProxy("omvc", subject);
		return proxy.login(callbacks);		
	}

	@Override
	public boolean commit() throws LoginException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout() throws LoginException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Get the username and password. This method does not return any value.
	 * Instead, it sets global name and password variables.
	 * 
	 * <p>
	 * Also note that this method will set the username and password values in
	 * the shared state in case subsequent LoginModules want to use them via
	 * use/tryFirstPass.
	 * 
	 * @param getPasswdFromSharedState
	 *            boolean that tells this method whether to retrieve the
	 *            password from the sharedState.
	 * @exception LoginException
	 *                if the username/password cannot be acquired.
	 */
	private Callback[] getCallbacks() throws LoginException {

		// prompt for a username and password
		if (callbackHandler == null)
			throw new LoginException("No CallbackHandler available " + "to acquire authentication information from the user");

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("usuario");
		callbacks[1] = new PasswordCallback("passwd", false);

		try {
			callbackHandler.handle(callbacks);			
			return callbacks;

		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());

		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: " + uce.getCallback().toString()
					+ " not available to acquire authentication information"
					+ " from the user");
		}
	}

}
