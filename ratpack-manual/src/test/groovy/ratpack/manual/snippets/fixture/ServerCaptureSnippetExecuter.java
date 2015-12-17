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

package ratpack.manual.snippets.fixture;

import ratpack.manual.snippets.TestCodeSnippet;
import ratpack.manual.snippets.executer.SnippetExecuter;
import ratpack.override.ForcePortOverride;
import ratpack.override.Overrides;
import ratpack.server.RatpackServer;
import ratpack.server.internal.ServerCapturer;

public abstract class ServerCaptureSnippetExecuter implements SnippetExecuter {

  private final SnippetExecuter executer;

  public ServerCaptureSnippetExecuter(SnippetExecuter executer) {
    this.executer = executer;
  }

  @Override
  public SnippetFixture getFixture() {
    return executer.getFixture();
  }

  @Override
  public void execute(TestCodeSnippet snippet) throws Exception {
    withServer(ServerCapturer.capture(Overrides.of(ForcePortOverride.ephemeral()), () -> executer.execute(snippet)));
  }

  protected abstract void withServer(RatpackServer server) throws Exception;

}
