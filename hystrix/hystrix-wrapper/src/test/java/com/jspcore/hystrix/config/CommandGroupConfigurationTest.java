package com.jspcore.hystrix.config;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class CommandGroupConfigurationTest {

  @Test
  public void checkValidCommand() throws Exception {
    // given
    TestCommandGroupConfiguration testCommandGroupConfiguration = new TestCommandGroupConfiguration();

    // when
    testCommandGroupConfiguration.checkValidCommand(testCommandGroupConfiguration.getTest1Command());
  }

  @Test
  public void checkValidCommandWithCommandConfiguration() throws Exception {
    // given
    TestCommandGroupConfiguration originCommandConfig = new TestCommandGroupConfiguration();
    TestCommandGroupConfiguration newCommandConfig = new TestCommandGroupConfiguration();
    Whitebox.setInternalState(newCommandConfig.getTest1Command().getCircuitBreaker(), "enabled", !newCommandConfig.getTest1Command().getCircuitBreaker().getEnabled());

    // when
    originCommandConfig.checkValidCommand(newCommandConfig.getTest1Command());
  }

  @Test
  public void checkValidCommandThrowsExceptionWithUnknownCommand() throws Exception {
    // given
    TestCommandGroupConfiguration originCommandConfig = new TestCommandGroupConfiguration();
    CommandConfiguration unknownCommand = new CommandConfiguration("Blah") {
    };

    try {
      // when
      originCommandConfig.checkValidCommand(unknownCommand);
      fail("Should have got IllegalStateException");
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo(String.format("Command %s not part of group %s", unknownCommand.getClass(), originCommandConfig.getClass()));
    }
  }
}
