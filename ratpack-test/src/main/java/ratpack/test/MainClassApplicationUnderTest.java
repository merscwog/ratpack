/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.test;

import com.google.common.collect.Lists;
import ratpack.override.*;
import ratpack.override.Override;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.server.internal.ServerCapturer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class MainClassApplicationUnderTest extends ServerBackedApplicationUnderTest {

  private final Class<?> mainClass;

  public MainClassApplicationUnderTest(Class<?> mainClass) {
    this.mainClass = mainClass;
  }

  protected Registry createOverrides(Registry serverRegistry) throws Exception {
    return Registry.empty();
  }

  protected void addOverrides(List<Override> overrides) {

  }

  @java.lang.Override
  protected RatpackServer createServer() throws Exception {
    List<Override> overrides = Lists.newArrayList(
      ForcePortOverride.ephemeral(),
      ForceDevelopmentOverride.of(true),
      UserRegistryOverrides.of(this::createOverrides)
    );

    addOverrides(overrides);

    RatpackServer ratpackServer = ServerCapturer.capture(
      Overrides.of(overrides),
      () -> {
        Method method;
        try {
          method = mainClass.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
          throw new IllegalStateException("Class" + mainClass.getName() + " does not have a main(String...) class");
        }

        if (!Modifier.isStatic(method.getModifiers())) {
          throw new IllegalStateException(mainClass.getName() + ".main() must be static");
        }

        try {
          method.invoke(null, new Object[]{new String[]{}});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new IllegalStateException("Could not invoke " + mainClass.getName() + ".main()", e);
        }

      });

    if (ratpackServer == null) {
      throw new IllegalStateException(mainClass.getName() + ".main() did not start a Ratpack server");
    } else {
      return ratpackServer;
    }
  }
}
