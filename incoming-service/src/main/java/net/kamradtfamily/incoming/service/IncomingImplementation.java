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
package net.kamradtfamily.incoming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.IncomingContract;
import net.kamradtfamily.incoming.contract.IncomingException;
import net.kamradtfamily.incoming.contract.Input;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

/**
 *
 * @author randalkamradt
 */
@Slf4j
@Component
public class IncomingImplementation implements IncomingContract {

    @Autowired
    private KafkaSender<String, String> kafkaKamradtSender;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Mono<String> incoming(final Mono<Input> input) throws IncomingException {
        return kafkaKamradtSender
                .send(input
                        .map(m -> transformToString(m))
                        .map(m -> new ProducerRecord<String, String>("kamradt", m))
                        .map(p -> SenderRecord.<String, String, Integer>create(p, 1)))
                .doOnError(e -> log.error("error sending to kafka", e))
                .doOnNext(m -> log.info("sending " + m))
                .map(m -> m.toString())
                .publishNext();
    }

    private String transformToString(Input input) {
        try {
            String string = mapper.writeValueAsString(input);
            log.info("incoming string " + string + " being send to message queue");
            return string;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("error parsing input", ex);
        }
    }    

    @Override
    public Flux<Input> alloutput() throws IncomingException {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }

    @Override
    public Mono<Input> output(String key) throws IncomingException {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }

}
