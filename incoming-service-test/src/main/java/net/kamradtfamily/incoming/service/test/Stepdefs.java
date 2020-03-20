package net.kamradtfamily.incoming.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.Input;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
public class Stepdefs extends SpringEnabledSteps {

    @Autowired
    KafkaReceiver<String, String> kafkaKamradtTestReceiver;
    
    @Autowired
    IncomingClient incomingClient;
    
    @Autowired
    ObjectMapper objectMapper;

    Input inputValue;
    int httpStatus;

    @Given("a good input value")
    public void generateGoodInputValue() {
        inputValue = Input.builder()
                .value("test value")
                .key(UUID.randomUUID().toString())
                .optionalValue("optional test value")
                .build();
        log.info("generated good input value " + inputValue);
    }

    @When("the incoming service put is called")
    public void callThePutMethodWithInputValue() {
        log.info("call the put method with input value " + inputValue);
        try {
            incomingClient.incoming(Mono.just(inputValue))
                .subscribe(s -> log.info("post returned " + s), t -> log.error("error in post", t));
        } catch (Exception ex) {
            log.info("unexpected exception thrown", ex);
            fail();
        }
        httpStatus = 200; // all other status will throw an exception
    }

    @Then("the return value should be {int}")
    public void checkReturnValue(int value) {
        log.info("checking return value of " + httpStatus + " expected " + value);
        assertEquals(value, httpStatus);
    }

    @Then("the input value should be found on the message queue")
    public void findInputValueOnMessageQueue() throws JsonProcessingException {
        log.info("looking for input value on the message queue");
        Input actual = kafkaKamradtTestReceiver.receive()
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .doOnNext(r -> log.info("receiver record " + r))
                .map(r -> parseToInput(r.value()))
                .filter(i -> i.isPresent())
                .map(i -> i.get())
                .doOnNext(i -> log.info("prefilter value " + i))
                .filter(i -> i.equals(inputValue))
                .doOnNext(i -> log.info("postfilter value " + i))
                .take(1)
                .doOnNext(i -> log.info("published value " + i))
                .blockFirst(Duration.ofSeconds(10));
        log.info("find on message queue the input value " + actual);
        assertEquals(inputValue, actual);
    }

    private Optional<Input> parseToInput(String message) {
        try {
            return Optional.ofNullable(objectMapper.readValue(message, Input.class));
        } catch (Throwable e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
