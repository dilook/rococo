package guru.qa.rococo.service;

import javax.annotation.Nonnull;

public interface GhClient {
  @Nonnull
  String issueState(String issueNumber);
}
