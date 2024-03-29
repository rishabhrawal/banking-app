package com.revolut;


import com.revolut.config.MyApplicationConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Optional;

public class App {

    public static final Optional<String> port = Optional.ofNullable(System.getenv("BANKING_APP_PORT"));

    public static void main(String[] args) throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(Integer.valueOf(port.orElse("8080")));
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        /*jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                SavingsAccountResource.class.getCanonicalName());*/

        jerseyServlet.setInitParameter("javax.ws.rs.Application",
                MyApplicationConfig.class.getCanonicalName());

        /*jerseyServlet.setInitParameter("javax.ws.rs.Application",
                AppInjector.class.getCanonicalName());*/

        jerseyServlet.setInitParameter("io.swagger.jaxrs.listing",
                "com.revolut.webapi");


        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}