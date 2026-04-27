package com.jcaa.usersmanagement.domain.valueobject;

import java.util.Objects;
import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;

public record UserName(String value) {

  private static final int MIN_NAME_LENGTH = 3;

  public UserName {
    if (Objects.isNull(value)) {
      throw new NullPointerException("UserName cannot be null");
    }
    final String normalizedValue = value.trim();
    validateNotEmpty(normalizedValue);
    validateMinimumLength(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidUserNameException.becauseValueIsEmpty();
    }
  }

  private static void validateMinimumLength(final String normalizedValue) {
    if (normalizedValue.length() < MIN_NAME_LENGTH) {
      throw InvalidUserNameException.becauseLengthIsTooShort(MIN_NAME_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
