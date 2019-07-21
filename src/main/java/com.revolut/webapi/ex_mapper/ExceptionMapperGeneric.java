package com.revolut.webapi.ex_mapper;


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
        logger.error("Unknown error: ",ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Unknown Error")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}