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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.IncomingContract;
import net.kamradtfamily.incoming.contract.IncomingException;
import net.kamradtfamily.incoming.contract.Input;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 *
 * @author randalkamradt
 */
@Slf4j
@RestController
@RequestMapping("/incoming")
public class IncomingController {

    private final IncomingContract incomingImplementation;
    private final ObjectMapper objectMapper;
    
    public IncomingController(final ObjectMapper objectMapper, final IncomingContract incomingImplementation) {
        this.objectMapper = objectMapper;
        this.incomingImplementation = incomingImplementation;
    }

    @PostMapping()
    private Mono<String> incomingInput(@RequestBody Input payload)
            throws IncomingException {
        log.info("incoming payload " + payload);
        return incomingImplementation.incoming(Mono.just(payload));
    }

}
