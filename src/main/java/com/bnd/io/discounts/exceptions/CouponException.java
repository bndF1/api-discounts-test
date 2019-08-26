package com.bnd.io.discounts.exceptions;

public class CouponException extends RuntimeException {
  private static final long serialVersionUID = -4401436013663869885L;

  public CouponException(final ApiExceptions message) {
    super(String.valueOf(message));
  }
}
