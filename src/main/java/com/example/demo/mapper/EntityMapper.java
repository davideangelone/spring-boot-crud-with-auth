package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.example.demo.entity.UserEntity;
import com.example.demo.model.UserModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    UserModel toUserModel(UserEntity userEntity);
}
