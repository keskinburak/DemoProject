package org.example.mapper;

import org.example.model.CreateRoleInput;
import org.example.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    Role createRoleInputToRole(CreateRoleInput createRoleInput);
}