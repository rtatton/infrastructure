package org.cirrus.infrastructure.handler.api;

import java.util.Set;

public final class HttpStatus {

  public static final int OK = 200;
  public static final int CREATED = 201;
  public static final int ACCEPTED = 202;
  public static final int NO_CONTENT = 204;

  public static final int BAD_REQUEST = 400;
  public static final int UNAUTHORIZED = 401;
  public static final int FORBIDDEN = 403;
  public static final int NOT_FOUND = 404;

  public static final int INTERNAL_SERVER_ERROR = 500;

  private static final Set<Integer> STATUSES =
      Set.of(
          OK,
          CREATED,
          ACCEPTED,
          NO_CONTENT,
          BAD_REQUEST,
          UNAUTHORIZED,
          FORBIDDEN,
          NOT_FOUND,
          INTERNAL_SERVER_ERROR);

  private HttpStatus() {
    // no-op
  }

  public static boolean contains(int status) {
    return STATUSES.contains(status);
  }
}
