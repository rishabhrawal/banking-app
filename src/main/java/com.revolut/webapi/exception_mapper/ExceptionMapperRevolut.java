package com.revolut.webapi.exception_mapper;

import com.revolut.exception.RevolutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperRevolut implements ExceptionMapper<RevolutException> {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionMapperRevolut.class);

    @Override
    public Response toResponse(RevolutException ex) {
        logger.error("Revolut Exception",ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Business Error: " + ex.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}