package com.revolut.webapi;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperGeneric implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        return Response.status(500)
                .entity("Generic exception: ")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}