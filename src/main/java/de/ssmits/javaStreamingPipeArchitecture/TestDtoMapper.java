package de.ssmits.javaStreamingPipeArchitecture;

import org.mapstruct.Mapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Mapper(config = MapperConfig.class)
@Transactional(propagation = Propagation.SUPPORTS)
public interface TestDtoMapper {
	public TestDto map(TestEto eto);
}
