package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserApplicationMapper {

  UserApplicationMapper INSTANCE = Mappers.getMapper(UserApplicationMapper.class);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "email", source = "email")
  @Mapping(target = "password", source = "password")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "status", constant = "PENDING")
  UserModel fromCreateCommandToModel(CreateUserCommand command);

  @Mapping(target = "id", source = "command.id")
  @Mapping(target = "name", source = "command.name")
  @Mapping(target = "email", source = "command.email")
  @Mapping(target = "password", expression = "java(resolvePassword(command.password(), currentPassword))")
  @Mapping(target = "role", source = "command.role")
  @Mapping(target = "status", source = "command.status")
  UserModel fromUpdateCommandToModel(UpdateUserCommand command, UserPassword currentPassword);

  default UserId fromGetUserByIdQueryToUserId(GetUserByIdQuery query) {
    if (query == null) {
      throw new IllegalArgumentException("Query cannot be null");
    }
    return new UserId(query.id());
  }

  default UserId fromDeleteCommandToUserId(DeleteUserCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("Command cannot be null");
    }
    return new UserId(command.id());
  }

  default UserId mapId(String value) { return value != null ? new UserId(value) : null; }
  default UserName mapName(String value) { return value != null ? new UserName(value) : null; }
  default UserEmail mapEmail(String value) { return value != null ? new UserEmail(value) : null; }
  default UserPassword mapPassword(String value) { return value != null ? UserPassword.fromPlainText(value) : null; }
  default UserRole mapRole(String value) { return value != null ? UserRole.fromString(value) : null; }
  default UserStatus mapStatus(String value) { return value != null ? UserStatus.fromString(value) : null; }

  default UserPassword resolvePassword(String newPassword, UserPassword currentPassword) {
    if (newPassword == null || newPassword.isBlank()) {
      return currentPassword;
    }
    return UserPassword.fromPlainText(newPassword);
  }

  // Clean Code - Regla 21: No se usan códigos especiales de error como -1 o null.
  // La lógica de roleToCode fue eliminada y reemplazada por mapeos seguros o excepciones.
}
