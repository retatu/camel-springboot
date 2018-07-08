package com.learncamel.learncamelspringboot.process;

import com.learncamel.learncamelspringboot.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item in process is: "+item);

        StringBuilder query = new StringBuilder();

        if(item.getTransactionType().equals("ADD")){
            query.append("INSERT INTO ITEMS (SKU, ITEM_DESCRIPTION, PRICE) VALUES ('");
            query.append(item.getSku()+"','");
            query.append(item.getItemDescription()+"','");
            query.append(item.getPrice()+"')");
        }

        log.info("Final query is: "+query);

        exchange.getIn().setBody(query.toString());
    }
}