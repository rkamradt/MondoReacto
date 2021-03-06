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
package net.kamradtfamily.queue.puller;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

/**
 *
 * @author randalkamradt
 */
@Slf4j
@RestController
@RequestMapping("/queue")
public class QueuePullerController {

    @Autowired
    KafkaReceiver<String,String> kafkaKamradtTestReceiver;

    @Autowired
    KafkaReceiver<String,String> kafkaKamradtReceiver;

    @GetMapping(path="/kamradt/consumer", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<String> getFromKamradtConsumer() {
        return kafkaKamradtReceiver
                .receive()
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .map(ReceiverRecord::value)
                .publishNext();
    }

    @GetMapping(path="/kamradt/test/consumer", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<String> getFromKamradtTestConsumer() {
        return kafkaKamradtTestReceiver
                .receive()
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .map(ReceiverRecord::value)
                .publishNext();
    }
}
