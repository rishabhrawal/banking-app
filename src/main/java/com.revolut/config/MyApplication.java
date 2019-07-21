package com.revolut.config;

import com.revolut.webapi.ex_mapper.ExceptionMapperGeneric;
import com.revolut.webapi.ex_mapper.ExceptionMapperNotFound;
import com.revolut.webapi.ex_mapper.ExceptionMapperRevolut;
import org.glassfish.jersey.server.ResourceConfig;

//@ApplicationPath("/webapi/v1/")
// extend javax.ws.rs.core.Application
public class MyApplication extends ResourceConfig {

    public MyApplication() {

        //packages("com.revolut.webapi");
        register(ExceptionMapperRevolut.class);
        register(ExceptionMapperNotFound.class);
        register(ExceptionMapperGeneric.class);
        register(new MyApplicationBinder());
    }

    /*public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(SavingsAccountResource.class, MyResource.class,
                ExceptionMapperRevolut.class, ExceptionMapperNotFound.class, ExceptionMapperGeneric.class));
    }*/

}

