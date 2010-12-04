/**
 * 
 */
package org.opensixen.riena.client.proxy;

import java.lang.reflect.ParameterizedType;
import java.net.URL;

import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;
import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.factory.Register;

import org.opensixen.riena.Activator;
import org.opensixen.riena.exceptions.ServiceRegistrationException;
import org.opensixen.riena.interfaces.IConnectionChangeListener;
import org.opensixen.riena.interfaces.IRienaService;
import org.opensixen.riena.interfaces.IServiceConnectionHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 * @param <T>
 *
 */
public abstract class AbstractProxy<T extends IRienaService> implements IConnectionChangeListener {
			
	private Class<T> clazz;

	private IServiceConnectionHandler serviceConnectionHandler;
	
	private boolean registered = false;
	
	private IRemoteServiceRegistration serviceRegistration;
	
	private static BundleContext context = Activator.getContext();
	
	private ServiceReference serviceReference;

	private ILoginContext loginContext;

	
	
	protected AbstractProxy()	{		
		this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	
	/**
	 * @return the serviceConnectionHandler
	 */
	public IServiceConnectionHandler getServiceConnectionHandler() {
		return serviceConnectionHandler;
	}

	/**
	 * @param serviceConnectionHandler the serviceConnectionHandler to set
	 */
	public void setServiceConnectionHandler(
			IServiceConnectionHandler aServiceConnectionHandler) {
		serviceConnectionHandler = aServiceConnectionHandler;
		serviceConnectionHandler.addConnectionChangeListener(this);
		if (registered)	{
			unregister();
		}
		register();
	}

	/**
	 * Register service proxy and do login
	 * @return
	 */
	public boolean register()	{
		if (registered)	{
			return true;			
		}
		
		register(clazz);
		if (serviceReference == null)	{
			return false;
		}
		
		registered = true;
		
		// If don't need auth, return true now
		if (!needAuth())	{
			return true;
		}
		
		if (getJAASConfigFile() == null || getJAASConfigurationName() == null)	{
			throw new RuntimeException("If your module need Auth, JAASConfigFile and JAASConfigurationName must be implemented and can't be null.");
		}
		
		// Do auth
		// Setup login context
		loginContext = LoginContextFactory.createContext(getJAASConfigurationName(), getJAASConfigFile());
		
		// Do something after register
		afterRegister();
		
		return true;		
		
	}
	
	/**
	 * Do something after register
	 * 
	 * This method must be override
	 */
	protected void afterRegister()	{
	}
	
	
	public void unregister()	{
		if (!registered)	{
			return;
		}
		
		//TODO
		serviceRegistration.unregister();
	}
	
	
	
	
	/**
	 * Register service and return serviceReference
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws ServiceRegistrationException
	 */
	private void register(Class<T> clazz) throws ServiceRegistrationException	{
		if (serviceConnectionHandler == null)	{
			throw new ServiceRegistrationException("serviceConnectionHandler is null.");
		}
		
		ServiceConnection connection = serviceConnectionHandler.getServiceConnection();
		String url = getURL(connection.getUrl(), getServicePath()); 
		
		try {
			serviceRegistration = Register.remoteProxy(clazz).usingUrl(url).withProtocol("hessian").andStart(context);			
			serviceReference = Activator.getContext().getServiceReference(clazz.getName());			
		}
		catch (Exception e)	{
			throw new ServiceRegistrationException("No se puede conectar con el servidor en la URL: " + url, e);
		}
	}

	/**
	 * Return the service path in server
	 * @return
	 */
	public abstract String getServicePath();
	
	/**
	 * Return true if service need JAAS auth
	 */
	public abstract boolean needAuth();
	
	/**
	 * Return JAAS configuration Name
	 * if needAuth = true, this method must be implemented
	 * @return
	 */
	public String getJAASConfigurationName() {
		return null;
	}
	
	/**
	 * Return JAAS config file path
	 * if needAuth = true, this method must be implemented
	 * @return
	 */
	public URL getJAASConfigFile() {
		return null;
	}
	
	
	
	
	public T getService()	{
		T service = (T) context.getService(serviceReference);
		try {
			if (service.testService())	{
				return service;
			}
		}
		catch (Exception e)	{
			// Unregister incorrect service
			unregister();
			throw new ServiceRegistrationException("No se puede registrar el servidor", e);
		}
		
		throw new ServiceRegistrationException("No se puede registrar el servidor");
		
	}
	
	/**
	 * Generate an url with host and webService
	 * @param serverURL the URL returned by the IServiceConnectionHandler
	 * @param webService
	 * @return
	 */
	public static String getURL(String serverURL, String webService)	{
		if (serverURL == null)	{
			return null;
		}
		
		if (serverURL.endsWith("/"))	{
			serverURL = serverURL.substring(0,serverURL.lastIndexOf("/"));
		}
		
		StringBuffer buff = new StringBuffer(serverURL);
		buff.append("/hessian/");
		
		if (webService.startsWith("/"))	{
			webService = webService.substring(1);
		}
		buff.append(webService);
		return buff.toString();
	}	
	
	
	public ILoginContext getLoginContext()	{
		return loginContext;
	}


	/* (non-Javadoc)
	 * @see org.opensixen.riena.interfaces.IConnectionChangeListener#fireConnectionChange()
	 */
	@Override
	public boolean fireConnectionChange() {
		unregister();
		return register();
	}
	
	
	
}
