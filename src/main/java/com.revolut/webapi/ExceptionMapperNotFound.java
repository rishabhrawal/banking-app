package com.revolut.webapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperNotFound implements ExceptionMapper<NotFoundException> {

    Logger logger = LoggerFactory.getLogger(ExceptionMapperNotFound.class);

    @Override
    public Response toResponse(NotFoundException ex) {
        logger.error(ex.getMessage() + ex.getCause());
        return Response.status(Response.Status.NOT_FOUND)
                .entity("resource not found")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}