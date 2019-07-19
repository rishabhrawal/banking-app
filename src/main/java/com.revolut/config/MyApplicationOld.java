package com.revolut.config;


import com.revolut.account.AccountService;
import com.revolut.account.savings.SavingsAccountService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

//@ApplicationPath("/webapi")
// extend javax.ws.rs.core.Application for config
public class MyApplicationOld extends ResourceConfig {

    public MyApplicationOld() {
        //packages("com.revolut.webapi");
        //register(SavingsAccountService.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(SavingsAccountService.class).to(AccountService.class);
                //bind(new SavingsAccountService()).to(SavingsAccountService.class);
                //bindAsContract(SavingsAccountService.class);
            }
        });


    }
}
