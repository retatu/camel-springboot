package com.learncamel.learncamelspringboot.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class HealCheckProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String healthCheckResult = exchange.getIn().getBody(String.class);

        log.info("Health Check String of the APP is: "+healthCheckResult);

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> map = objectMapper.readValue(healthCheckResult, new TypeReference<Map<String,Object>>(){});

        log.info("Map read is: "+map);

        StringBuilder erro = null;
        for (String key : map.keySet()){
            if(map.get(key).toString().contains("DOWN")){
                if(erro == null){
                    erro = new StringBuilder();
                }
                erro.append("Component: "+key+" in the route is down \n");
            }
        }

        if(erro != null){
            log.info("Exception msg: "+erro.toString());
            exchange.getIn().setHeader("Erro", true);
            exchange.getIn().setBody(erro.toString());
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, erro.toString());
        }
    }
}
