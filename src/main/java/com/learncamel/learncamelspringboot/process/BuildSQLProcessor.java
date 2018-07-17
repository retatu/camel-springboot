package com.learncamel.learncamelspringboot.process;

import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item in process is: "+item);

        if(ObjectUtils.isEmpty(item.getSku())){
            throw new DataException("Sku null for: "+item.getItemDescription());
        }

        StringBuilder query = new StringBuilder();



        if(item.getTransactionType().equals("ADD")){
            query.append("INSERT INTO ITEMS (SKU, ITEM_DESCRIPTION, PRICE) VALUES ('");
            query.append(item.getSku()+"','");
            query.append(item.getItemDescription()+"',");
            query.append(item.getPrice()+")");
        }else if(item.getTransactionType().equals("UPDATE")){
            query.append("UPDATE ITEMS SET PRICE = ");
            query.append(item.getPrice());
            query.append(" WHERE SKU = ");
            query.append(item.getSku());
        }else if(item.getTransactionType().equals("DEL")){
            query.append("DELETE FROM ITEMS WHERE sku = ");
            query.append(item.getSku());
        }

        log.info("Final query is: "+query);
        exchange.getIn().setBody(query.toString());
    }
}
