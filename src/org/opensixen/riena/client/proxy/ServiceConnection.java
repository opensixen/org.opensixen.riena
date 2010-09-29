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
	
	private int port;
	
	private String url;
	
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
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
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
		
		if (port > 0)	{
			buff.append(":").append(port).append("/");
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
