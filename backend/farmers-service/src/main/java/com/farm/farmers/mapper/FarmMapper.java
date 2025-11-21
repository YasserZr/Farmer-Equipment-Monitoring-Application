package com.farm.farmers.mapper;

import com.farm.farmers.dto.request.CreateFarmRequest;
import com.farm.farmers.dto.request.UpdateFarmRequest;
import com.farm.farmers.dto.response.FarmDTO;
import com.farm.farmers.model.Farm;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Farm entity and DTOs.
 * Handles conversions between entity and various DTO representations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmMapper {
    
    /**
     * Convert Farm entity to FarmDTO
     * @param farm the entity
     * @return the DTO
     */
    @Mapping(target = "farmerName", source = "farmer.name")
    @Mapping(target = "formattedArea", expression = "java(farm.getFormattedArea())")
    @Mapping(target = "largeFarm", expression = "java(farm.isLargeFarm())")
    FarmDTO toDTO(Farm farm);
    
    /**
     * Convert list of Farm entities to list of FarmDTOs
     * @param farms the entities
     * @return the DTOs
     */
    List<FarmDTO> toDTOList(List<Farm> farms);
    
    /**
     * Convert CreateFarmRequest to Farm entity
     * @param request the request DTO
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Farm toEntity(CreateFarmRequest request);
    
    /**
     * Update existing Farm entity from UpdateFarmRequest
     * Only updates non-null fields from the request
     * @param request the update request
     * @param farm the existing entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmerId", ignore = true)
    @Mapping(target = "farmer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateFarmRequest request, @MappingTarget Farm farm);
}
