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
package net.kamradtfamily.incoming.read.service;

import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.IncomingContract;
import net.kamradtfamily.incoming.contract.IncomingException;
import net.kamradtfamily.incoming.contract.Input;
import net.kamradtfamily.incoming.datamodel.MondoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author randalkamradt
 */
@Slf4j
@Component
public class IncomingReadImplementation implements IncomingContract {
    @Autowired
    ReactiveMongoTemplate template;

    
    @Override
    public Mono<String> incoming(final Mono<Input> input) throws IncomingException {
       throw new UnsupportedOperationException("Not supported by this implementation");
     }

    @Override
    public Flux<Input> alloutput() throws IncomingException {
        return template
                .findAll(MondoData.class)
                .map(m -> mapToInput(m));
    }

    @Override
    public Mono<Input> output(String key) throws IncomingException {
        return template
                .findById(key, MondoData.class)
                .map(m -> mapToInput(m));
    }

    private Input mapToInput(MondoData m) {
        return Input.builder()
                .key(m.getId())
                .value(m.getName())
                .optionalValue(m.getDescription())
                .build();
    }

}
