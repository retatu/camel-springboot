package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.alert.MailProcessor;
import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import com.learncamel.learncamelspringboot.process.BuildSQLProcessor;
import com.learncamel.learncamelspringboot.process.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class MainRoute extends RouteBuilder {

    @Autowired
    Environment environment;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    BuildSQLProcessor buildSQLProcessor;

    @Autowired
    SuccessProcessor successProcessor;

    @Autowired
    MailProcessor mailProcessor;


    @Override
    public void configure() throws Exception {

        //errorHandler(deadLetterChannel("log:errorLog?level=ERROR&showProperties=true")
        //    .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR));

        onException(DataException.class).log(LoggingLevel.ERROR, "DataException in the route ${body}");

        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}")
                .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR)
                ;


        DataFormat dataFormat = new BindyCsvDataFormat(Item.class);

        log.info("Starting the route....");

        from("{{startRoute}}").routeId("mainRoute")
            .log("Timer Invoked and body is "+ environment.getProperty("message"))
                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        .log("Isn't a mock")
                        .pollEnrich("{{fromRoute}}")
                        .log("Isn't a mock")
                    .otherwise()
                        .log("Is a mock").end()
                .to("{{toRoute1}}")
                .unmarshal(dataFormat)
                    .log("Body is: ${body}")
                .split(body())
                    .log("Body is: ${body}")
                    .process(buildSQLProcessor)
                    .to("{{toRoute2}}")
                .end()
            .process(successProcessor)
        .to("{{toRoute3}}");

        log.info("Ending the route....");
    }
}
