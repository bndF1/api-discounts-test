package com.bnd.io.discounts.exceptions;

public enum ApiExceptions {
  DEACTIVATED_COUPON("COUPON IS NOT ACTIVE"),
  COUPON_NOT_FOUND("COUPON NOT FOUND");

  ApiExceptions(final String exception) {}
}
