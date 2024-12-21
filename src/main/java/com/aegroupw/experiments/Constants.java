package com.aegroupw.experiments;

import java.nio.file.Paths;

public final class Constants {
  private Constants() {}

  public static final String experimentsDir =
    Paths.get("").toAbsolutePath().toString() + "/reports";
}
