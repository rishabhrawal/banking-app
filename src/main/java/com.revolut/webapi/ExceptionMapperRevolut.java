package com.revolut.webapi;

import com.revolut.exception.RevolutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperRevolut implements ExceptionMapper<RevolutException> {

    Logger logger = LoggerFactory.getLogger(ExceptionMapperRevolut.class);

    @Override
    public Response toResponse(RevolutException ex) {
        logger.error(ex.getMessage() +" " + ex.getCause());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Application error " + ex.getMessage())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}