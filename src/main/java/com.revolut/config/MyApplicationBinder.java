package com.revolut.config;

import com.revolut.account.savings.SavingsAccountService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class MyApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SavingsAccountService.class).to(SavingsAccountService.class);
        //bind(ExampleResourcesAdapter.class).to(ExampleResourcesAdapter.class);
    }

}