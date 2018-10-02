import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(tags = "~@wip", monochrome = false, format = {"json:target/cucumber.json", "html:target/reports", "pretty"})
public class RunTest {
}
