package com.learncamel.learncamelspringboot.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleCamelMockRoute extends RouteBuilder {

    @Autowired
    Environment environment;
    @Override
    public void configure() throws Exception {

        log.info("Starting the route....");

        from("{{startRoute}}")
            .log("Timer Invoked and body is "+ environment.getProperty("message"))
                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        .pollEnrich("{{fromRoute}}")
                        .log("Isn't a mock")
                    .otherwise()
                        .log("Is a mock").end()
                .to("{{toRoute1}}");

        log.info("Ending the route....");
    }
}
