 /******* BEGIN LICENSE BLOCK *****
 * Versión: GPL 2.0/CDDL 1.0/EPL 1.0
 *
 * Los contenidos de este fichero están sujetos a la Licencia
 * Pública General de GNU versión 2.0 (la "Licencia"); no podrá
 * usar este fichero, excepto bajo las condiciones que otorga dicha 
 * Licencia y siempre de acuerdo con el contenido de la presente. 
 * Una copia completa de las condiciones de de dicha licencia,
 * traducida en castellano, deberá estar incluida con el presente
 * programa.
 * 
 * Adicionalmente, puede obtener una copia de la licencia en
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Este fichero es parte del programa opensiXen.
 *
 * OpensiXen es software libre: se puede usar, redistribuir, o
 * modificar; pero siempre bajo los términos de la Licencia 
 * Pública General de GNU, tal y como es publicada por la Free 
 * Software Foundation en su versión 2.0, o a su elección, en 
 * cualquier versión posterior.
 *
 * Este programa se distribuye con la esperanza de que sea útil,
 * pero SIN GARANTÍA ALGUNA; ni siquiera la garantía implícita 
 * MERCANTIL o de APTITUD PARA UN PROPÓSITO DETERMINADO. Consulte 
 * los detalles de la Licencia Pública General GNU para obtener una
 * información más detallada. 
 *
 * TODO EL CÓDIGO PUBLICADO JUNTO CON ESTE FICHERO FORMA PARTE DEL 
 * PROYECTO OPENSIXEN, PUDIENDO O NO ESTAR GOBERNADO POR ESTE MISMO
 * TIPO DE LICENCIA O UNA VARIANTE DE LA MISMA.
 *
 * El desarrollador/es inicial/es del código es
 *  FUNDESLE (Fundación para el desarrollo del Software Libre Empresarial).
 *  Indeos Consultoria S.L. - http://www.indeos.es
 *
 * Contribuyente(s):
 *  Eloy Gómez García <eloy@opensixen.org> 
 *
 * Alternativamente, y a elección del usuario, los contenidos de este
 * fichero podrán ser usados bajo los términos de la Licencia Común del
 * Desarrollo y la Distribución (CDDL) versión 1.0 o posterior; o bajo
 * los términos de la Licencia Pública Eclipse (EPL) versión 1.0. Una 
 * copia completa de las condiciones de dichas licencias, traducida en 
 * castellano, deberán de estar incluidas con el presente programa.
 * Adicionalmente, es posible obtener una copia original de dichas 
 * licencias en su versión original en
 *  http://www.opensource.org/licenses/cddl1.php  y en  
 *  http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * Si el usuario desea el uso de SU versión modificada de este fichero 
 * sólo bajo los términos de una o más de las licencias, y no bajo los 
 * de las otra/s, puede indicar su decisión borrando las menciones a la/s
 * licencia/s sobrantes o no utilizadas por SU versión modificada.
 *
 * Si la presente licencia triple se mantiene íntegra, cualquier usuario 
 * puede utilizar este fichero bajo cualquiera de las tres licencias que 
 * lo gobiernan,  GPL 2.0/CDDL 1.0/EPL 1.0.
 *
 * ***** END LICENSE BLOCK ***** */
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
		if (needAuth())	{
			
			if (getJAASConfigFile() == null || getJAASConfigurationName() == null)	{
				throw new RuntimeException("If your module need Auth, JAASConfigFile and JAASConfigurationName must be implemented and can't be null.");
			}
			
			// Do auth
			// Setup login context
			loginContext = LoginContextFactory.createContext(getJAASConfigurationName(), getJAASConfigFile());

		}
		
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
