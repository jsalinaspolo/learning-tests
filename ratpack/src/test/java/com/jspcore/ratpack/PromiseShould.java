package com.jspcore.ratpack;

import org.junit.Test;
import ratpack.exec.ExecResult;
import ratpack.exec.Promise;
import ratpack.test.exec.ExecHarness;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PromiseShould {

  @Test
  public void guarantee_order_of_execution_model() throws Exception {
    try (ExecHarness harness = ExecHarness.harness()) {
      ExecResult<List<String>> result = harness.yield(execution -> {
        List<String> elements = new ArrayList<>();
        elements.add("1");

        Promise<List<String>> p = Promise.async(downstream -> {
          elements.add("3");
          downstream.success(elements);
        });
        elements.add("2");
        return p;
      });

      assertThat(result.getValue()).containsExactly("1", "2", "3");
    }
  }

  @Test
  public void guarantee_order_of_execution_model_waiting_promise() throws Exception {
    try (ExecHarness harness = ExecHarness.harness()) {
      harness.yield(execution -> {
        List<String> elements = new ArrayList<>();

        elements.add("1");
        Promise.<String>async(f -> {
          Thread.sleep(30);
          f.success("3");
        }).then(elements::add);

        Promise.<String>async(f -> {
          Thread.sleep(20);
          f.success("4");
        }).then(elements::add);

        Promise.<String>async(f -> {
          Thread.sleep(10);
          f.success("5");
        }).then(r -> {
          elements.add(r);

          assertThat(elements).containsExactly("1", "2", "3", "4", "5");
          System.out.println(elements);
        });

        elements.add("2");
        return null;
      });
    }
  }

}
