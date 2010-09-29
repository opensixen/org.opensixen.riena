/**
 * 
 */
package org.opensixen.riena.client.proxy;

import java.lang.reflect.ParameterizedType;

import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.factory.Register;

import org.opensixen.riena.Activator;
import org.opensixen.riena.exceptions.ServiceRegistrationException;
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
public abstract class AbstractProxy<T extends IRienaService> {
		
	private Class<T> clazz;

	private static IServiceConnectionHandler serviceConnectionHandler;
	
	private boolean registered = false;
	
	private static IRemoteServiceRegistration serviceRegistration;
	
	private static BundleContext context = Activator.getContext();
	
	private ServiceReference serviceReference;

	
	
	protected AbstractProxy()	{		
		this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		register();
	}
	
	
	/**
	 * @return the serviceConnectionHandler
	 */
	public static IServiceConnectionHandler getServiceConnectionHandler() {
		return serviceConnectionHandler;
	}

	/**
	 * @param serviceConnectionHandler the serviceConnectionHandler to set
	 */
	public static void setServiceConnectionHandler(
			IServiceConnectionHandler serviceConnectionHandler) {
		AbstractProxy.serviceConnectionHandler = serviceConnectionHandler;
	}

	public boolean register()	{
		if (registered)	{
			return true;			
		}
		
		register(clazz);
		if (serviceReference != null)	{
			return true;
		}
		return false;
		
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
	
	
	public T getService()	{
		T service = (T) context.getService(serviceReference);
		if (service.testService())	{
			return service;
		}
		throw new ServiceRegistrationException("No se puede registrar el servidor");
	}
	
	/**
	 * Generate an url with host and webService
	 * @param host
	 * @param webService
	 * @return
	 */
	public static String getURL(String host, String webService)	{
		if (host == null)	{
			return null;
		}
		
		if (host.endsWith("/"))	{
			host = host.substring(0,host.lastIndexOf("/"));
		}
		
		StringBuffer buff = new StringBuffer(host);
		buff.append("/hessian/");
		
		if (webService.startsWith("/"))	{
			webService = webService.substring(1);
		}
		buff.append(webService);
		return buff.toString();
	}	
	
	
}
