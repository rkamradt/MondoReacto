package net.kamradtfamily.incoming.persist.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.kamradtfamily.incoming.contract.Input;
import net.kamradtfamily.incoming.datamodel.MondoData;
import org.apache.kafka.clients.producer.ProducerRecord;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
public class Stepdefs extends SpringEnabledSteps {

    @Autowired
    KafkaSender kafkaSender;

    @Autowired
    ReactiveMongoTemplate template;

    @Autowired
    ObjectMapper objectMapper;

    Input inputValue;

    @Given("a good input value")
    public void generateGoodInputValue() {
        inputValue = Input.builder()
                .value("value")
                .key("key")
                .optionalValue(Optional.empty())
                .build();
        log.info("generated good input value " + inputValue);
    }

    @When("the input value is handed to the message queue")
    public void putTheInputValueOnTheMessageQueue() {
        final Mono<SenderRecord> message = Mono.just(inputValue).map(i -> {
            try {
                String string = objectMapper.writeValueAsString(i);
                log.info("incoming string " + string + " being send to message queue");
                return string;
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("error parsing input", ex);
            }
        })
                .map(m -> SenderRecord.create(new ProducerRecord<>("kamradt", "key", m), m));
        kafkaSender.<String>send(message)
                .doOnError(e -> log.error("Send failed", e))
                .then();
    }

    @Then("the input value should be found in the database")
    public void findInputValueOnMessageQueue() throws JsonProcessingException {
        template.findById(inputValue.getKey(), MondoData.class)
                .subscribe(s -> compare(s));

    }

    private Disposable compare(MondoData actual) {
        assertEquals(inputValue.getKey(), actual.getId());
        assertEquals(inputValue.getOptionalValue(), actual.getName());
        assertEquals(inputValue.getValue(), actual.getDescription());
        return Disposables.disposed();
    }
}
