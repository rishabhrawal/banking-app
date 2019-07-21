package com.revolut.config;

import com.revolut.webapi.SavingsAccountResource;
import com.revolut.webapi.TransactionResource;
import com.revolut.webapi.exception_mapper.ExceptionMapperGeneric;
import com.revolut.webapi.exception_mapper.ExceptionMapperNotFound;
import com.revolut.webapi.exception_mapper.ExceptionMapperRevolut;
import org.glassfish.jersey.server.ResourceConfig;

//@ApplicationPath("/webapi/v1/")
// extend javax.ws.rs.core.Application
public class MyApplicationConfig extends ResourceConfig {

    public MyApplicationConfig() {
        register(SavingsAccountResource.class);
        register(TransactionResource.class);
        register(ExceptionMapperRevolut.class);
        register(ExceptionMapperNotFound.class);
        register(ExceptionMapperGeneric.class);
        register(new MyApplicationBinder());
    }

}

