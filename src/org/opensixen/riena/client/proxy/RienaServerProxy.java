/**
 * 
 */
package org.opensixen.riena.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.opensixen.riena.interfaces.IServiceConnectionHandler;

/**
 * Esta clase agrupa proxys conectados al mismo servidor
 * 
 * Maneja configuraciones globales 
 * asi como cambios en la configuracion
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class RienaServerProxy {
	
	/** Global connection handler	*/
	private static IServiceConnectionHandler serviceConnectionHandler;
	
	private static List<AbstractProxy<?>> services = new ArrayList<AbstractProxy<?>>();
	
	
	/**
	 * @return the serviceConnectionHandler
	 */
	public static IServiceConnectionHandler getServiceConnectionHandler() {
		return serviceConnectionHandler;
	}

	/**
	 * Set serviceConnectionHandler and reset connections
	 * @param serviceConnectionHandler the serviceConnectionHandler to set
	 */
	public static void setServiceConnectionHandler(
			IServiceConnectionHandler serviceConnectionHandler) {
		RienaServerProxy.serviceConnectionHandler = serviceConnectionHandler;
		
		// Update services
		restartConnection();
	}

	/**
	 * restartConnections
	 */
	public static void restartConnection()	{
		for (AbstractProxy<?> proxy:services)	{
			proxy.setServiceConnectionHandler(getServiceConnectionHandler());
			proxy.unregister();
			proxy.register();
		}

	}
	
	public static void addService(AbstractProxy<?> proxy)	{
		services.add(proxy);
	}
	
	public static void removeService(AbstractProxy<?> proxy)	{
		if (services.contains(proxy))	{
			services.remove(proxy);
		}
	}
}
