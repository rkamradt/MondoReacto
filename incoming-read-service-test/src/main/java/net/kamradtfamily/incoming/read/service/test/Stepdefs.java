package net.kamradtfamily.incoming.read.service.test;

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

import net.kamradtfamily.incoming.datamodel.MondoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

@Slf4j
public class Stepdefs extends SpringEnabledSteps {

    @Autowired
    IncomingClient incomingClient;
    @Autowired
    ReactiveMongoTemplate template;
    @Autowired
    ObjectMapper objectMapper;

    Input inputValue;
    Input actualValue;
    MondoData saved;
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

    @Given("the input value is inserted in mongo")
    public void insertIntoMongo() {
        saved = template.save(mapToMondoData(inputValue))
                .block(Duration.ofSeconds(10));
        log.info("saved " + saved);
    }

    @When("the incoming service get is called with the key value")
    public void callTheGetMethodWithKeyValue() {
        log.info("call the get method with key value " + inputValue.getKey());
        try {
            actualValue = incomingClient.output(inputValue.getKey())
                .block(Duration.ofSeconds(10));
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

    @Then("the return value equal to the input value")
    public void findInputValueOnMessageQueue() throws JsonProcessingException {
        assertEquals(inputValue, actualValue);
    }

    private MondoData mapToMondoData(Input value) {
        return MondoData.builder()
                .id(value.getKey())
                .name(value.getValue())
                .description(value.getOptionalValue())
                .build();
    }

}
