package com.github.valfirst.slf4jtest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;

@ExtendWith(MockitoExtension.class)
class LevelAssertTest {

    @Mock private TestLogger logger;

    private LevelAssert assertions;

    @BeforeEach
    void setup() {
        assertions = new LevelAssert(logger, Level.INFO);
    }

    @Nested
    class HasLogCount {

        @Test
        void failsWhenDoesNotMatchExpected() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of());

            assertThatThrownBy(() -> assertions.hasNumberOfLogs(1))
                    .isInstanceOf(AssertionError.class)
                    .hasMessage("Expected level INFO to have 1 log messages available, but 0 were found");
        }

        @Test
        void passesWhenDoesMatch() {
            when(logger.getLoggingEvents())
                    .thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.warn("Ignore me"),
                                    LoggingEvent.info("Yay for me!"),
                                    LoggingEvent.info("With args {}", "argument")));

            assertThatCode(() -> assertions.hasNumberOfLogs(2)).doesNotThrowAnyException();
        }

        @Test
        void returnsSelfWhenPasses() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of(LoggingEvent.info("Event")));

            LevelAssert actual = assertions.hasNumberOfLogs(1);

            assertThat(actual).isNotNull();
        }
    }

    @Nested
    class HasMessageContaining {

        @Test
        void failsWhenDoesNotContainSubstring() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of());

            assertThatThrownBy(() -> assertions.hasMessageContaining("words"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessage(
                            "Expected level INFO to contain a log message containing `words`, but it did not");
        }

        @Test
        void passesWhenDoesMatch() {
            when(logger.getLoggingEvents())
                    .thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.warn("Ignore me"),
                                    LoggingEvent.info("Yay for me!"),
                                    LoggingEvent.info("With args {}", "argument")));

            assertThatCode(() -> assertions.hasMessageContaining("me")).doesNotThrowAnyException();
        }

        @Test
        void returnsSelfWhenPasses() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of(LoggingEvent.info("Event")));

            LevelAssert actual = assertions.hasMessageContaining("Event");

            assertThat(actual).isNotNull();
        }
    }

    @Nested
    class HasMessageMatching {

        @Test
        void failsWhenDoesNotMatchExpected() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of(LoggingEvent.info("")));

            assertThatThrownBy(() -> assertions.hasMessageMatching(".+"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessage(
                            "Expected level INFO to contain a log message matching regex `.+`, but it did not");
        }

        @Test
        void passesWhenDoesMatch() {
            when(logger.getLoggingEvents())
                    .thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.warn("Ignore me"),
                                    LoggingEvent.info("Yay for me!"),
                                    LoggingEvent.info("With args {}", "argument")));

            assertThatCode(() -> assertions.hasMessageMatching(".* .*")).doesNotThrowAnyException();
        }

        @Test
        void returnsSelfWhenPasses() {
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of(LoggingEvent.info("12340")));

            LevelAssert actual = assertions.hasMessageMatching("[0-9]+");

            assertThat(actual).isNotNull();
        }
    }
}
