package org.opensixen.riena.exceptions;

public class ServiceRegistrationException extends RuntimeException {

	public ServiceRegistrationException(String msg) {
		super (msg);
	}

	public ServiceRegistrationException(String msg, Exception e) {
		super (msg,e);
	}

	private static final long serialVersionUID = 1L;

}
