package com.revolut.webapi.exception_mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperNotFound implements ExceptionMapper<NotFoundException> {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionMapperNotFound.class);

    @Override
    public Response toResponse(NotFoundException ex) {
        logger.error("Resource not found", ex);
        return Response.status(Response.Status.NOT_FOUND)
                .entity("resource not found")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}