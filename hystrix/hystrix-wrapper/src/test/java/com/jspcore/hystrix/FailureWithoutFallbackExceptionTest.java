package com.jspcore.hystrix;

import com.google.common.base.Optional;
import org.assertj.guava.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FailureWithoutFallbackExceptionTest {

  @Test
  public void causeReturnsAbsentIfNotFound() throws Exception {
    // given
    FailureWithoutFallbackException exception = new FailureWithoutFallbackException(
      new RuntimeException(
        new RuntimeException()));

    // when
    Optional<?> cause = exception.causeByType(NullPointerException.class);

    // then
    Assertions.assertThat(cause).isAbsent();
  }

  @Test
  public void causeReturnsOptionOfCauseWhenMatchingCauseFound() throws Exception {
    // given
    IllegalArgumentException expectedCause = new IllegalArgumentException();
    FailureWithoutFallbackException exception = new FailureWithoutFallbackException(
      new RuntimeException(
        new RuntimeException(
          expectedCause)));

    // when
    Optional<?> cause = exception.causeByType(IllegalArgumentException.class);

    // then
    Assertions.assertThat(cause).contains(expectedCause);
  }

  @Test
  public void causeReturnsAbsentIfLookingForSelf() throws Exception {
    // given
    FailureWithoutFallbackException exception = new FailureWithoutFallbackException(
      new RuntimeException());

    // when
    Optional<?> cause = exception.causeByType(FailureWithoutFallbackException.class);

    // then
    Assertions.assertThat(cause).isAbsent();
  }

  @Test
  public void hasCauseReturnsTrueWhenCauseFound() throws Exception {
    // given
    FailureWithoutFallbackException exception = new FailureWithoutFallbackException(
      new RuntimeException(
        new RuntimeException(
          new IllegalArgumentException())));

    // then
    assertThat(exception.hasCauseByType(IllegalArgumentException.class)).isTrue();
  }

  @Test
  public void hasCauseReturnsFalseWhenCauseNotFound() throws Exception {
    // given
    FailureWithoutFallbackException exception = new FailureWithoutFallbackException(
      new RuntimeException(
        new RuntimeException(
          new IllegalArgumentException())));

    // then
    assertThat(exception.hasCauseByType(NullPointerException.class)).isFalse();
  }
}
