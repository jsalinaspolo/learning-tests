package com.jspcore.ratpack

import com.logcapture.junit.LogCaptureTrait
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import ratpack.exec.Blocking
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.RequestId
import ratpack.handling.RequestLogger
import ratpack.logging.MDCInterceptor
import spock.lang.Specification

import static com.logcapture.assertion.ExpectedLoggingMessage.aLog
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.Matchers.matchesPattern
import static org.hamcrest.Matchers.not

class ApplicationLoggingShould extends Specification implements LogCaptureTrait {

  public static final String CORRELATION_ID = "correlation_id"
  def UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12})"

  def "use ndca to log request id"() {
    expect:
    GroovyEmbeddedApp.of {
      handlers {
        all(RequestLogger.ncsa())
        get("", new HandlerWithoutMDC())
      }
    } test { httpClient -> httpClient.get() }

    logged(aLog().info().withMessage(containsString(""""GET / HTTP/1.1" 200 0 id=""")))
  }

  def "use mdc to log correlation id for async blocks"() {
    expect:
    GroovyEmbeddedApp.of {
      handlers {
        all(RequestLogger.ncsa())
        get("", new HandlerWithMDC())
      }
    } test { httpClient -> httpClient.get() }

    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("about to block")))
    logged(aLog().info().withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("back from blocking")))
  }

  def "use mdc interceptor to carry across non async blocks"() {
    expect:
    GroovyEmbeddedApp.of {
      registryOf { add(MDCInterceptor.instance()) }
      handlers {
        all(RequestLogger.ncsa())
        get("", new HandlerWithMDC())
      }
    } test { httpClient -> httpClient.get() }

    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("about to block")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("back from blocking")))
  }

  def "use mdc with initialise to add correlation id from requestId"() {
    expect:
    GroovyEmbeddedApp.of {
      registryOf {
        add(MDCInterceptor.withInit { e ->
          e.maybeGet(RequestId.class).ifPresent { requestId ->
            MDC.put(CORRELATION_ID, requestId.toString())
          }
        })
      }
      handlers {
        all(RequestLogger.ncsa())
        get("", new HandlerWithoutMDC())
      }
    } test { httpClient -> httpClient.get() }

    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("about to block")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("back from blocking")))

  }

  def "use header from request to initialise requestId and correlation id"() {
    given:
    def correlationId = UUID.randomUUID().toString()
    expect:
    GroovyEmbeddedApp.of {
      handlers {
        all(new HeaderHandler())
        all(RequestLogger.ncsa())
        get("", new HandlerWithoutMDC())
      }
    } test { httpClient ->
      httpClient.request() { requestSpec ->
        requestSpec.headers({ header -> header.set("X-Correlation-Id", correlationId) })
          .get()
      }
    }

    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("about to block")))
    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("back from blocking")))
  }

  def "add correlation id when no header from request to initialise requestId and correlation id"() {
    expect:
    GroovyEmbeddedApp.of {
      registryOf {
        add(MDCInterceptor.withInit { e ->
          e.maybeGet(RequestId.class).ifPresent { requestId ->
            MDC.put(CORRELATION_ID, requestId.toString())
          }
        })
      }
      handlers {
        all(new HeaderHandler())
        all(RequestLogger.ncsa())
        get("", new HandlerWithoutMDC())
      }
    } test { httpClient -> httpClient.get() }

    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("about to block")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, matchesPattern(UUID_REGEX)).withMessage(containsString("back from blocking")))
  }

  def "use header from request overwrites ratpack mdc interceptor init"() {
    given:
    def correlationId = UUID.randomUUID().toString()
    expect:
    GroovyEmbeddedApp.of {
      registryOf {
        add(MDCInterceptor.withInit { e ->
          e.maybeGet(RequestId.class).ifPresent { requestId ->
            MDC.put(CORRELATION_ID, requestId.toString())
          }
        })
      }
      handlers {
        all(new HeaderHandler())
        all(RequestLogger.ncsa())
        get("", new HandlerWithoutMDC())
      }
    } test { httpClient ->
      httpClient.request() { requestSpec ->
        requestSpec.headers({ header -> header.set("X-Correlation-Id", correlationId) })
          .get()
      }
    }
    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("about to block")))
    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("blocking")))
    logged(aLog().info().withMdc(CORRELATION_ID, containsString(correlationId)).withMessage(containsString("back from blocking")))
  }

  class HeaderHandler implements Handler {

    @Override
    void handle(Context ctx) throws Exception {
      ctx.header("X-Correlation-Id").ifPresent { correlationId ->
        ctx.request.add(RequestId.of(correlationId))
        MDC.put(CORRELATION_ID, correlationId.toString())
      }
      ctx.next()
    }
  }

  class HandlerWithoutMDC implements Handler {
    def log = LoggerFactory.getLogger(getClass())

    @Override
    void handle(Context ctx) throws Exception {
      log.info("about to block")
      Blocking.get {
        log.info("blocking")
        "something"
      } then { str ->
        // And back again
        log.info("back from blocking")
        ctx.render("")
      }
    }
  }

  class HandlerWithMDC implements Handler {
    def log = LoggerFactory.getLogger(getClass())

    @Override
    void handle(Context ctx) throws Exception {
      MDC.put(CORRELATION_ID, ctx.request.get(RequestId.class).toString())
      log.info("about to block")
      Blocking.get {
        log.info("blocking")
        "something"
      } then { str ->
        // And back again
        log.info("back from blocking")
        ctx.render("")
      }
    }
  }
}
