/**
 * 
 */
package org.opensixen.riena.client.proxy;

import java.util.ArrayList;

import org.opensixen.riena.interfaces.IConnectionChangeListener;
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
	
	private ArrayList<IConnectionChangeListener> connectionChangeListeners = new ArrayList<IConnectionChangeListener>();
	
	/**
	 * Simple Constructor
	 * @param url
	 */
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
	
	public void setServiceConnection(ServiceConnection connection)	{
		if (connection != null && connection.equals(this.connection))	{
			this.connection = connection;
			
		}
	}


	/* (non-Javadoc)
	 * @see org.opensixen.riena.interfaces.IServiceConnectionHandler#addConnectionChangeListener(org.opensixen.riena.interfaces.IConnectionChangeListener)
	 */
	@Override
	public void addConnectionChangeListener(IConnectionChangeListener listener) {
		connectionChangeListeners.add(listener);
		
	}

	/* (non-Javadoc)
	 * @see org.opensixen.riena.interfaces.IServiceConnectionHandler#removeConnectionChangeListener(org.opensixen.riena.interfaces.IConnectionChangeListener)
	 */
	@Override
	public void removeConnectionChangeListener(IConnectionChangeListener listener) {
		if (connectionChangeListeners.contains(listener))	{
			connectionChangeListeners.remove(listener);
		}		
	}	
	
	private void fireConnectionChange()	{
		for (IConnectionChangeListener listener : connectionChangeListeners)	{
			listener.fireConnectionChange();
		}
	}
	
}
