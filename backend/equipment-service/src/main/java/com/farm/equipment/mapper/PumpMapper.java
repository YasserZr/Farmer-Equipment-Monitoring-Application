package com.farm.equipment.mapper;

import com.farm.equipment.dto.request.CreatePumpRequest;
import com.farm.equipment.dto.request.UpdatePumpRequest;
import com.farm.equipment.dto.response.PumpDTO;
import com.farm.equipment.model.ConnectedPump;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for ConnectedPump entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PumpMapper {
    
    @Mapping(target = "formattedMaxFlow", expression = "java(pump.getFormattedMaxFlow())")
    @Mapping(target = "operational", expression = "java(pump.isOperational())")
    @Mapping(target = "maintenanceOverdue", expression = "java(pump.isMaintenanceOverdue())")
    PumpDTO toDTO(ConnectedPump pump);
    
    List<PumpDTO> toDTOList(List<ConnectedPump> pumps);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastMaintenanceDate", ignore = true)
    @Mapping(target = "nextMaintenanceDate", ignore = true)
    @Mapping(target = "maintenanceNotes", ignore = true)
    ConnectedPump toEntity(CreatePumpRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "installationDate", ignore = true)
    @Mapping(target = "lastMaintenanceDate", ignore = true)
    @Mapping(target = "nextMaintenanceDate", ignore = true)
    @Mapping(target = "maintenanceNotes", ignore = true)
    void updateEntityFromRequest(UpdatePumpRequest request, @MappingTarget ConnectedPump pump);
}
