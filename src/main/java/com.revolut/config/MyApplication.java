package com.revolut.config;


import com.revolut.account.AccountService;
import com.revolut.account.savings.SavingsAccountService;
import com.revolut.webapi.ExceptionMapperGeneric;
import com.revolut.webapi.ExceptionMapperRevolut;
import com.revolut.webapi.MyResource;
import com.revolut.webapi.SavingsAccountResource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//@ApplicationPath("/webapi")
// extend javax.ws.rs.core.Application for config
public class MyApplication extends javax.ws.rs.core.Application {

    public MyApplication() {
        //packages("com.revolut.webapi");
        //register(SavingsAccountService.class);
        /*register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(SavingsAccountService.class).to(AccountService.class);
                //bind(new SavingsAccountService()).to(SavingsAccountService.class);
                //bindAsContract(SavingsAccountService.class);
            }
        });*/
    }

    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(SavingsAccountResource.class, MyResource.class,
                ExceptionMapperRevolut.class, ExceptionMapperGeneric.class));
    }

}

