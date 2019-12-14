package net.kamradtfamily.incoming.service.test;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incomingcontract.Input;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
public class Stepdefs extends SpringEnabledSteps {

    @Autowired
    KafkaReceiver kafkaReceiver;
    
    @Autowired
    IncomingClient incomingClient;

    Input inputValue;
    int httpStatus;

    @Given("a good input value")
    public void generateGoodInputValue() {
        inputValue = Input.builder()
                .value("value")
                .key("key")
                .build();
        log.info("generated good input value " + inputValue);
    }

    @When("the incoming service put is called")
    public void callThePutMethodWithInputValue() {
        log.info("call the put method with input value " + inputValue);
        try {
            incomingClient.incoming(Mono.just(inputValue));
        } catch (Exception ex) {
            log.info("unexpected exception thrown", ex);
            fail();
        }
        httpStatus = 200; // all other status will throw an exception
    }

    @Then("the return value should be {int}")
    public void checkReturnValue(int value) {
        assertEquals(200, httpStatus);
    }

    @Then("the input value should be found on the message queue")
    public void findInputValueOnMessageQueue() {
        Flux<ReceiverRecord<String, String>> kafkaFlux = kafkaReceiver.receive();

        String message = kafkaFlux
                .log()
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .blockFirst()
                .value();
        log.info("find on message queue the input value " + inputValue);
    }
}
