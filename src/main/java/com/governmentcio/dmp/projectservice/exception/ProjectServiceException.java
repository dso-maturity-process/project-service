package com.governmentcio.dmp.projectservice.exception;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support
 *
 */
public class ProjectServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProjectServiceException() {
	}

	public ProjectServiceException(String message) {
		super(message);
	}

	public ProjectServiceException(Throwable cause) {
		super(cause);
	}

	public ProjectServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectServiceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
