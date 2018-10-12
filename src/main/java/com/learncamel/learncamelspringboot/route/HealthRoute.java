package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.alert.MailProcessor;
import com.learncamel.learncamelspringboot.process.HealCheckProcessor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthRoute extends RouteBuilder {

    @Autowired
    HealCheckProcessor healCheckProcessor;

    @Autowired
    MailProcessor mailProcessor;

    Predicate isNotMock = header("env").isNotEqualTo("mock");

    @Override
    public void configure() throws Exception {
        from("{{healthRoute}}").routeId("healthRoute")
                .choice().when(isNotMock)
                    .pollEnrich("http://localhost:8081/health")
                .end()
                .process(healCheckProcessor)
                .choice()
                    .when(header("erro").isEqualTo(true))
                    .choice().when(isNotMock)
                        .process(mailProcessor)
                    .end()
                .end()
            ;

    }
}
