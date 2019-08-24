package com.bnd.io.discounts.exceptions;

public class DeactivatedCouponException extends RuntimeException {
  private static final long serialVersionUID = -4401436013663869885L;

  public DeactivatedCouponException(final ApiExceptions message) {
    super(String.valueOf(message));
  }
}
