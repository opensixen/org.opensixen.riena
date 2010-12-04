/**
 * 
 */
package org.opensixen.riena.interfaces;

import org.opensixen.riena.client.proxy.ServiceConnection;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public interface IServiceConnectionHandler {
	
	public ServiceConnection getServiceConnection();
	
	
	public void addConnectionChangeListener(IConnectionChangeListener listener);
	
	public void removeConnectionChangeListener(IConnectionChangeListener listener);

}
