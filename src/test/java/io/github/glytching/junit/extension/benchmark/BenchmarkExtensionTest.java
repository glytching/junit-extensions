package io.github.glytching.junit.extension.benchmark;

import io.github.glytching.junit.extension.util.ExecutionEvent;
import io.github.glytching.junit.extension.util.RecordingExecutionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class BenchmarkExtensionTest {

  private JupiterTestEngine engine;
  private RecordingExecutionListener listener;

  @BeforeEach
  public void setUp() {
    engine = new JupiterTestEngine();
    listener = new RecordingExecutionListener();
  }

  @Test
  void willPublishBenchmarkResultsWithDefaultTimeUnit() {
    // when executing a test case
    execute(DefaultTimeUnitBenchmarkTest.class);

    // then the benchmark report event(s) are published
    List<ExecutionEvent> publishedEvents = getReportEntries();

    assertThat(publishedEvents.size(), equalTo(1));

    assertThat(
        toReportEntryKey(publishedEvents.get(0)),
        equalTo(format("Elapsed time in %s for canBenchmark()", MILLISECONDS.name())));
  }

  @Test
  void willPublishBenchmarkResultsWithChosenTimeUnit() {
    // when executing a test case
    execute(CustomTimeUnitBenchmarkTest.class);

    // then the benchmark report event(s) are published
    List<ExecutionEvent> publishedEvents = getReportEntries();

    assertThat(publishedEvents.size(), equalTo(1));

    assertThat(
        toReportEntryKey(publishedEvents.get(0)),
        equalTo(format("Elapsed time in %s for canBenchmark()", MICROSECONDS.name())));
  }

  private void execute(Class<?> clazz) {
    LauncherDiscoveryRequest request = getRequest(clazz);
    engine.execute(
        new ExecutionRequest(
            getTestDescriptor(request), listener, request.getConfigurationParameters()));
  }

  private LauncherDiscoveryRequest getRequest(Class<?> clazz) {
    return request().selectors(selectClass(clazz)).build();
  }

  private TestDescriptor getTestDescriptor(LauncherDiscoveryRequest request) {
    return engine.discover(request, UniqueId.forEngine(engine.getId()));
  }

  private List<ExecutionEvent> getReportEntries() {
    return listener
        .getEventsByType(ExecutionEvent.Type.REPORTING_ENTRY_PUBLISHED)
        .collect(Collectors.toList());
  }

  private String toReportEntryKey(ExecutionEvent event) {
    return event.getPayload(ReportEntry.class).get().getKeyValuePairs().keySet().iterator().next()
        + "()";
  }

  @ExtendWith(BenchmarkExtension.class)
  static class DefaultTimeUnitBenchmarkTest {

    @Test
    public void canBenchmark() throws InterruptedException {
      // note: the actual assertion - verifying publication of report events - is performed in the
      // containing class
      Thread.sleep(5);
    }
  }

  static class CustomTimeUnitBenchmarkTest {

    @SuppressWarnings("unused")
    @RegisterExtension
    static BenchmarkExtension benchmarkExtension = new BenchmarkExtension(TimeUnit.MICROSECONDS);

    @Test
    public void canBenchmark() throws InterruptedException {
      // note: the actual assertion - verifying publication of report events - is performed in the
      // containing class
      Thread.sleep(5);
    }
  }
}
