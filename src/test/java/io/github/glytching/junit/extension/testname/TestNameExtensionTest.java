package io.github.glytching.junit.extension.testname;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(TestNameExtension.class)
public class TestNameExtensionTest {

  @TestName private String testName;

  @BeforeEach
  public void testNameIsOnlyPopulatedOnTestInvocation() {
    assertThat(testName, nullValue());
  }

  @AfterEach
  public void testNameIsDiscardedBetweenTests() {
    assertThat(testName, nullValue());
  }

  @Test
  public void canSetTestName() {
    assertThat(testName, is("canSetTestName"));
  }

  @Test
  public void canSetADifferentTestName() {
    assertThat(testName, is("canSetADifferentTestName"));
  }
}
