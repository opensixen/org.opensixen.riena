/**
 * 
 */
package org.opensixen.riena.client.proxy;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class ServiceConnection {
	
	private String host;
	
	private String port;
	
	private String url;
	
	private String service;
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		if (url != null)	{
			return url;
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append("http://");
		buff.append(host);
		
		if (port != null)	{
			buff.append(":").append(port);
		}
		
		if (getService() != null && getService().length() > 0)	{
			buff.append("/").append(getService());
		}
		
		return buff.toString();
		
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
