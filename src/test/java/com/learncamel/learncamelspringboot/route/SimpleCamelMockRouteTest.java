package com.learncamel.learncamelspringboot.route;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
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
public class SimpleCamelMockRouteTest extends CamelTestSupport {

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


    @Test
    public void testMoveFile(){
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,101,iPhone 6s,387\n" +
                "ADD,102,Samsung Galaxy s6,320";

        MockEndpoint mockEndpoint = getMockEndpoint(environment.getProperty("toRoute1"));
        mockEndpoint.expectedBodiesReceived(message);
        mockEndpoint.expectedMessageCount(1);

        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"), message,
                "env", environment.getProperty("spring.profiles.active"));

        try {
            assertMockEndpointsSatisfied();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testMoveFileMockDB(){
        String message =
        "type,sku#,itemdescription,price\n" +
                "ADD,101,iPhone 6s,387\n" +
                "ADD,102,Samsung Galaxy s6,320";
        String outMessage = "Data updated SuccessFully";

        MockEndpoint mockEndpoint = getMockEndpoint(environment.getProperty("toRoute1"));
        mockEndpoint.expectedBodiesReceived(message);
        mockEndpoint.expectedMessageCount(1);

        MockEndpoint mockEndpoint1 = getMockEndpoint(environment.getProperty("toRoute3"));
        mockEndpoint1.expectedBodiesReceived(outMessage);
        mockEndpoint1.expectedMessageCount(1);

        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"), message,
                "env", environment.getProperty("spring.profiles.active"));

        try {
            assertMockEndpointsSatisfied();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
