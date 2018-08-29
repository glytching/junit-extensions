package io.github.glytching.junit.extension.testname;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

/**
 * The test name extension makes the current test name available inside each test method.
 *
 * <p>Usage example:
 *
 * <p>Injecting random values as fields:
 *
 * <pre>
 * &#064;ExtendWith(TestNameExtension.class)
 * public class MyTest {
 *
 *     &#064;TestName
 *     private String testName;
 *
 *     &#064;Test
 *     public void testUsingRandomString() {
 *         // use the populated testName
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @since 1.1.0
 */
public class TestNameExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    setTestNameFieldValue(
        getTestNameField(extensionContext),
        extensionContext.getRequiredTestInstance(),
        extensionContext.getRequiredTestMethod().getName());
  }

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    setTestNameFieldValue(
        getTestNameField(extensionContext), extensionContext.getRequiredTestInstance(), null);
  }

  private Optional<Field> getTestNameField(ExtensionContext extensionContext) {
    for (Field field : extensionContext.getRequiredTestClass().getDeclaredFields()) {
      if (isAnnotated(field, TestName.class)) {
        return Optional.of(field);
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private void setTestNameFieldValue(
      Optional<Field> testNameField, Object testInstance, String value) throws IllegalAccessException {
    if (testNameField.isPresent()) {
      testNameField.get().setAccessible(true);
      testNameField.get().set(testInstance, value);
    }
  }
}
