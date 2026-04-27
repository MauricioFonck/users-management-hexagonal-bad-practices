package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.LoginRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserDesktopMapper {

  UserDesktopMapper INSTANCE = Mappers.getMapper(UserDesktopMapper.class);

  CreateUserCommand toCreateCommand(CreateUserRequest request);

  UpdateUserCommand toUpdateCommand(UpdateUserRequest request);

  default DeleteUserCommand toDeleteCommand(UserId userId) {
    if (userId == null) {
      throw new IllegalArgumentException("ID inválido");
    }
    return new DeleteUserCommand(userId.value());
  }

  default GetUserByIdQuery toGetByIdQuery(UserId userId) {
    return new GetUserByIdQuery(userId.value());
  }

  LoginCommand toLoginCommand(LoginRequest request);

  @Mapping(target = "id", source = "idValue")
  @Mapping(target = "name", source = "nameValue")
  @Mapping(target = "email", source = "emailValue")
  @Mapping(target = "role", source = "roleName")
  @Mapping(target = "status", source = "statusName")
  UserResponse toResponse(UserModel user);

  List<UserResponse> toResponseList(List<UserModel> users);
}
