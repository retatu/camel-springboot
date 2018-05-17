package com.learncamel.learncamelspringboot.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleCamelMockRoute extends RouteBuilder {

    @Autowired
    Environment environment;
    @Override
    public void configure() throws Exception {
        from("{{startRoute}}")
            .log("Timer Invoked and body is "+ environment.getProperty("message"))
                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        .pollEnrich("{{fromRoute}}")
                        .log("Isn't a mock")
                    .otherwise()
                        .log("Is a mock").end()
                .to("{{toRoute1}}");
    }
}
