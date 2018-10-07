package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.alert.MailProcessor;
import com.learncamel.learncamelspringboot.process.HealCheckProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthRoute extends RouteBuilder {

    @Autowired
    HealCheckProcessor healCheckProcessor;

    @Autowired
    MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {
        from("{{healthRoute}}").routeId("healthRoutes")
            .pollEnrich("http://localhost:8081/health")
            .process(healCheckProcessor)
            .choice()
                .when(header("erro").isEqualTo(true))
                .process(mailProcessor)
            ;

    }
}
