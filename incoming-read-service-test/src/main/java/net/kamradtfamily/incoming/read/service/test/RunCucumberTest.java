package net.kamradtfamily.incoming.read.service.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"},
		features = { "classpath:features" })
public class RunCucumberTest {
}
