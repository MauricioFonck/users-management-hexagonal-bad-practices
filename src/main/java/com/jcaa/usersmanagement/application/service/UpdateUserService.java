package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.mapper.UserApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Set;

@Log
@RequiredArgsConstructor
public final class UpdateUserService implements UpdateUserUseCase {

  private final UpdateUserPort updateUserPort;
  private final GetUserByIdPort getUserByIdPort;
  private final GetUserByEmailPort getUserByEmailPort;
  private final EmailNotificationService emailNotificationService;
  private final Validator validator;

  @Override
  public void execute(final UpdateUserCommand command) {
    validateCommand(command);

    log.info("Actualizando usuario id=" + command.id() + ", email=" + command.email() + ", nombre=" + command.name());

    final UserId userId = new UserId(command.id());
    final UserModel current = findExistingUserOrFail(userId);
    final UserEmail newEmail = new UserEmail(command.email());

    ensureEmailIsNotTakenByAnotherUser(newEmail, userId);

    final UserModel userToUpdate =
        UserApplicationMapper.INSTANCE.fromUpdateCommandToModel(command, current.getPassword());
    
    final UserModel updatedUser = updateUserPort.update(userToUpdate);

    notifyUserUpdated(updatedUser);
  }

  private void notifyUserUpdated(final UserModel user) {
    emailNotificationService.notifyUserUpdated(user);
  }

  private void logSilentUpdate(final UserModel user) {
    log.info("Actualización silenciosa para usuario: " + user.getId().value());
  }

  // Clean Code - Regla 6: método con dos modos de operar según el boolean — viola la regla.
  // Clean Code - Regla 7: efecto secundario oculto — el nombre "notifyIfRequired" no indica
  // que también hace logging cuando notify=false. El nombre es engañoso sobre sus efectos.

  private void validateCommand(final UpdateUserCommand command) {
    final Set<ConstraintViolation<UpdateUserCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private UserModel findExistingUserOrFail(final UserId userId) {
    return getUserByIdPort
        .getById(userId)
        .orElseThrow(() -> UserNotFoundException.becauseIdWasNotFound(userId.value()));
  }

  private void ensureEmailIsNotTakenByAnotherUser(final UserEmail newEmail, final UserId ownerId) {
    if (isEmailTakenByAnotherUser(newEmail, ownerId)) {
      throw UserAlreadyExistsException.becauseEmailAlreadyExists(newEmail.value());
    }
  }

  private boolean isEmailTakenByAnotherUser(final UserEmail email, final UserId ownerId) {
    return getUserByEmailPort.getByEmail(email)
        .map(user -> !user.getId().equals(ownerId))
        .orElse(false);
  }
}
