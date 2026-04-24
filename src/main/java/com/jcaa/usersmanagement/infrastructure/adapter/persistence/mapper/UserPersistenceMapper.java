package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.UserPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserPersistenceMapper {

  UserPersistenceMapper INSTANCE = Mappers.getMapper(UserPersistenceMapper.class);

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "name", source = "name.value")
  @Mapping(target = "email", source = "email.value")
  @Mapping(target = "password", expression = "java(user.getPassword().value())")
  @Mapping(target = "role", expression = "java(user.getRole().name())")
  @Mapping(target = "status", expression = "java(user.getStatus().name())")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserPersistenceDto fromModelToDto(UserModel user);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "email", source = "email")
  @Mapping(target = "password", source = "password")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "status", source = "status")
  UserModel fromEntityToModel(UserEntity entity);

  default UserId mapId(String value) { return value != null ? new UserId(value) : null; }
  default UserName mapName(String value) { return value != null ? new UserName(value) : null; }
  default UserEmail mapEmail(String value) { return value != null ? new UserEmail(value) : null; }
  default UserPassword mapPassword(String value) { return value != null ? UserPassword.fromHash(value) : null; }
  default UserRole mapRole(String value) { return value != null ? UserRole.fromString(value) : null; }
  default UserStatus mapStatus(String value) { return value != null ? UserStatus.fromString(value) : null; }

  // ResultSet methods
  static UserEntity fromResultSetToEntity(final ResultSet resultSet) throws SQLException {
    return new UserEntity(
        resultSet.getString("id"),
        resultSet.getString("name"),
        resultSet.getString("email"),
        resultSet.getString("password"),
        resultSet.getString("role"),
        resultSet.getString("status"),
        resultSet.getString("created_at"),
        resultSet.getString("updated_at"));
  }

  static UserModel fromResultSetToModel(final ResultSet resultSet) throws SQLException {
    return INSTANCE.fromEntityToModel(fromResultSetToEntity(resultSet));
  }

  static List<UserModel> fromResultSetToModelList(final ResultSet resultSet) throws SQLException {
    final List<UserModel> users = new ArrayList<>();
    while (resultSet.next()) {
      users.add(fromResultSetToModel(resultSet));
    }
    return users;
  }
}