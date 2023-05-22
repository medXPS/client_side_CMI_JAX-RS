package com.eBankingApp.jabak_lah_backend;

import com.eBankingApp.jabak_lah_backend.webService.CMIJAXRSWebService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmiConfig {
    @Bean
    public ResourceConfig resourceConfig(){
        ResourceConfig jerseyServlet   = new ResourceConfig();
        jerseyServlet.register(CMIJAXRSWebService.class);
        return jerseyServlet ;

    }
}
