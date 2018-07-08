package com.learncamel.learncamelspringboot.route;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class SimpleCamelRouteTest {

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment environment;

    @BeforeClass
    public static void startCleanUp() throws IOException {
    //    FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
    }

    @AfterClass
    public static void startCleanUp2() throws IOException {
    //    FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
    }

    @Test
    public void testModeFile() throws InterruptedException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,iPhone 6s,387\n" +
                "ADD,100,Samsung Galaxy s6,320";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"), message, Exchange.FILE_NAME, "test.txt");

        Thread.sleep(5000);

        File file = new File("data/output/test.txt");

        assertTrue(file.exists());
    }

    @Test
    public void test_ADD() throws InterruptedException, IOException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,iPhone 6s,387\n" +
                "ADD,100,Samsung Galaxy s6,320";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, "test.txt");

        Thread.sleep(5000);

        File file = new File("data/output/test.txt");

        assertTrue(file.exists());

        String expectedOutput = "Data updated SuccessFully";
        String output = new String(Files.readAllBytes(Paths.get("data/output/success.txt")));

        assertEquals(expectedOutput, output);
    }

}
