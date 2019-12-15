/*
 * The MIT License
 *
 * Copyright 2019 randalkamradt.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.kamradtfamily.incoming.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.Input;
import net.kamradtfamily.incoming.datamodel.MondoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

/**
 *
 * @author randalkamradt
 */
@Slf4j
@Service
public class IncomingPersistBean {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired 
    ReactiveMongoTemplate template;
    @Autowired
    KafkaReceiver<String, String> kafkaReceiver;

    public void run() {
        kafkaReceiver
                .receive()
                .log()
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .map(r -> mapToInput(r.value()))
                .map(r -> mapToMondoData(r))
                .subscribe(r -> template.save(r), e -> log.error("Error in subscription", e));
                
    }

    private MondoData mapToMondoData(Input value) {
        return MondoData.builder()
                .id(value.getKey())
                .name(value.getValue())
                .description(value.getOptionalValue())
                .build();
    }

    private Input mapToInput(String value) {
        try {
            return objectMapper.readValue(value, Input.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("error parsing input string", ex);
        }
    }
    
}
