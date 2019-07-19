package com.revolut.webapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperGeneric implements ExceptionMapper<Exception> {

    Logger logger = LoggerFactory.getLogger(ExceptionMapperGeneric.class);

    @Override
    public Response toResponse(Exception ex) {
        logger.error(ex.getMessage() + ex.getCause());
        return Response.status(500)
                .entity("Generic exception: ")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}