package com.farm.farmers.mapper;

import com.farm.farmers.dto.request.CreateFarmerRequest;
import com.farm.farmers.dto.request.UpdateFarmerRequest;
import com.farm.farmers.dto.response.FarmerDTO;
import com.farm.farmers.model.Farmer;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MapStruct mapper for Farmer entity and DTOs.
 * Handles conversions between entity and various DTO representations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmerMapper {
    
    /**
     * Convert Farmer entity to FarmerDTO
     * @param farmer the entity
     * @return the DTO
     */
    @Mapping(target = "farmCount", expression = "java(farmer.getFarmCount())")
    FarmerDTO toDTO(Farmer farmer);
    
    /**
     * Convert list of Farmer entities to list of FarmerDTOs
     * @param farmers the entities
     * @return the DTOs
     */
    List<FarmerDTO> toDTOList(List<Farmer> farmers);
    
    /**
     * Convert CreateFarmerRequest to Farmer entity
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farms", ignore = true)
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Farmer toEntity(CreateFarmerRequest request);
    
    /**
     * Update existing Farmer entity from UpdateFarmerRequest
     * Only updates non-null fields from the request
     * @param request the update request
     * @param farmer the existing entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farms", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateFarmerRequest request, @MappingTarget Farmer farmer);
}
