package de.ssmits.javaStreamingPipeArchitecture;

import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.MapperConfig(
	componentModel = "spring", 
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, 
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
	unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MapperConfig {

}
