package com.openkm.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GenericException extends WebApplicationException {
	private static final long serialVersionUID = 1L;

	public GenericException(Exception e) {
		super(e, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getClass().getSimpleName() + ": " + e.getMessage()).build());
	}
}
