package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.process.BuildSQLProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelMockRoute extends RouteBuilder {

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    Environment environment;

    @Autowired
    BuildSQLProcessor buildSQLProcessor;


    @Override
    public void configure() throws Exception {

        DataFormat dataFormat = new BindyCsvDataFormat(Item.class);

        log.info("Starting the route....");

        from("{{startRoute}}")
            .log("Timer Invoked and body is "+ environment.getProperty("message"))
                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        .pollEnrich("{{fromRoute}}")
                        .log("Isn't a mock")
                    .otherwise()
                        .log("Is a mock").end()
                .to("{{toRoute1}}")
                .unmarshal(dataFormat)
                    .log("Body is: ${body}")
                .split(body())
                    .log("Body is: ${body}")
                    .to("{{toRoute2}}")
                    .log("Body is: ${body}")
                    .process(buildSQLProcessor)
                .end();

        log.info("Ending the route....");
    }
}
