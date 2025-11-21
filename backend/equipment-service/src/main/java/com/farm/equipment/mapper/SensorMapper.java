package com.farm.equipment.mapper;

import com.farm.equipment.dto.request.CreateSensorRequest;
import com.farm.equipment.dto.request.UpdateSensorRequest;
import com.farm.equipment.dto.response.SensorDTO;
import com.farm.equipment.model.ConnectedSensor;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for ConnectedSensor entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SensorMapper {
    
    @Mapping(target = "batteryStatus", expression = "java(sensor.getBatteryStatus())")
    @Mapping(target = "batteryLow", expression = "java(sensor.isBatteryLow())")
    @Mapping(target = "batteryCritical", expression = "java(sensor.isBatteryCritical())")
    @Mapping(target = "online", expression = "java(sensor.isOnline())")
    @Mapping(target = "unit", expression = "java(sensor.getUnit())")
    SensorDTO toDTO(ConnectedSensor sensor);
    
    List<SensorDTO> toDTOList(List<ConnectedSensor> sensors);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastCommunication", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "alertThreshold", defaultValue = "20")
    ConnectedSensor toEntity(CreateSensorRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "farmId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastCommunication", ignore = true)
    @Mapping(target = "installationDate", ignore = true)
    void updateEntityFromRequest(UpdateSensorRequest request, @MappingTarget ConnectedSensor sensor);
}
