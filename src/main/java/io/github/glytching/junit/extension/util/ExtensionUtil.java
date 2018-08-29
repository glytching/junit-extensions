package io.github.glytching.junit.extension.util;

import org.junit.jupiter.api.extension.ExtensionContext;

public class ExtensionUtil {

  // this is a utility class - hide the public ctor
  private ExtensionUtil() {}

  /**
   * Creates a {@link ExtensionContext.Store} for a given {@code extensionContext}. A {@link
   * ExtensionContext.Store} is bound to an {@link ExtensionContext} so different test invocations
   * do not share the same store. For example a test invocation on {@code ClassA.testMethodA} will
   * have a different {@link ExtensionContext.Store} instance to that associated with a test
   * invocation on {@code ClassA.testMethodB} or test invocation on {@code ClassC.testMethodC}.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a {@link ExtensionContext.Store} for the given {@code extensionContext}
   */
  public static ExtensionContext.Store getStore(ExtensionContext extensionContext, Class clazz) {
    return extensionContext.getStore(namespace(extensionContext, clazz));
  }

  /**
   * Creates a {@link ExtensionContext.Namespace} in which extension state is stored on creation for
   * post execution destruction. Storing data in a custom namespace prevents accidental cross
   * pollination of data between extensions and between different invocations within the lifecycle
   * of a single extension.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a {@link ExtensionContext.Namespace} describing the scope for an extension
   */
  private static ExtensionContext.Namespace namespace(
      ExtensionContext extensionContext, Class clazz) {
    return ExtensionContext.Namespace.create(clazz, extensionContext);
  }
}
