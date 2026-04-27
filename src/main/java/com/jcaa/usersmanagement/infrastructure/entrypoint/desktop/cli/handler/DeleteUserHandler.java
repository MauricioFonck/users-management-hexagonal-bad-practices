package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DeleteUserHandler implements OperationHandler {

  private final UserController userController;
  private final ConsoleIO console;

  @Override
  public void handle() {
    final String id = console.readRequired("User ID to delete: ");
    try {
      userController.deleteUser(new UserId(id));
      console.println("  User deleted successfully.");
    } catch (final UserNotFoundException exception) {
      console.println("  Not found: " + exception.getMessage());
    }
  }
}