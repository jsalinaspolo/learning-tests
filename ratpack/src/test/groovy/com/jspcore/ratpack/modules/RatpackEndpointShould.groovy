package com.jspcore.ratpack.modules

import ratpack.func.Action
import ratpack.groovy.test.handling.GroovyRequestFixture
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.test.handling.HandlingResult
import spock.lang.Specification

class RatpackEndpointShould extends Specification {

  def "some test"() {
    when:
    HandlingResult result = GroovyRequestFixture.handle(new MyHandler(), {
      header("input-value", "foo").uri("some/path")
    })

    then:
    result.rendered(String) == "received: some/path"
    result.headers.get("output-value") == "foo:bar"
  }

  static class MyHandler implements Handler {
    void handle(Context ctx) throws Exception {
      String outputHeaderValue = ctx.getRequest().getHeaders().get("input-value") + ":bar";
      ctx.getResponse().getHeaders().set("output-value", outputHeaderValue);
      ctx.render("received: " + ctx.getRequest().getPath());
    }
  }

  static class PostHandler implements Handler {
    void handle(Context ctx) throws Exception {
      ctx.parse(SimpleObject).
        then(ctx.&render)
    }
  }

  static class SimpleObject {
    String name
    Integer age
  }

  static class PostEndpoint implements Action<Chain> {
    @Override
    void execute(Chain chain) throws Exception {
      chain.post("path", new PostHandler())
    }
  }
}
