/**
 * 
 */
package org.opensixen.riena.client.proxy;

import org.opensixen.riena.interfaces.IServiceConnectionHandler;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class SimpleServiceConnectionHandler implements IServiceConnectionHandler {
	
	private ServiceConnection connection;
	
	public SimpleServiceConnectionHandler(String url)	{
		connection = new ServiceConnection();
		connection.setUrl(url);
	}
	

	/* (non-Javadoc)
	 * @see org.opensixen.riena.interfaces.IServiceConnectionHandler#getServiceConnection()
	 */
	@Override
	public ServiceConnection getServiceConnection() {
		return connection;
	}

}
