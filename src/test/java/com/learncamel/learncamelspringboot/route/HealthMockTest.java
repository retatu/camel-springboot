package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.process.HealCheckProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HealthMockTest extends CamelTestSupport {

    @Autowired
    CamelContext context;

    @Autowired
    Environment environment;

    @Autowired
    protected CamelContext createCamelContext(){
        return context;
    }

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    HealCheckProcessor healCheckProcessor;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new HealthRoute();
    }

    @Before
    public void setUp(){

    }

    @Test
    public void healthRouteTest(){
        String input = "{\"status\":\"DOWN\",\"camel\":{\"status\":\"UP\",\"name\":\"camel-1\",\"version\":\"2.20.1\",\"contextStatus\":\"Started\"},\"camel-health-checks\":{\"status\":\"UP\",\"route:healthRoutes\":\"UP\",\"route:mainRoute\":\"UP\"},\"mail\":{\"status\":\"UP\",\"location\":\"smtp.gmail.com:587\"},\"diskSpace\":{\"status\":\"UP\",\"total\":395248136192,\"free\":189344976896,\"threshold\":10485760},\"db\":{\"status\":\"DOWN\",\"error\":\"org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is org.postgresql.util.PSQLException: FATAL: password authentication failed for user \\\"postgres\\\"\"}}";

        String response = (String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthRoute"), input, "env", environment.getProperty("spring.profiles.active"));

        String expectedOutPut = "Component: status in the route is down \n" +
                "Component: db in the route is down \n";

        assertEquals(expectedOutPut, response);
    }

}
