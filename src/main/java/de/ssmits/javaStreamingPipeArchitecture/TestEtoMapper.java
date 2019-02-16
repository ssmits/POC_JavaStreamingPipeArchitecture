package de.ssmits.javaStreamingPipeArchitecture;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Mapper(config = MapperConfig.class)
@Transactional(propagation = Propagation.SUPPORTS)
public interface TestEtoMapper {
	@Mapping(target="uuid", expression="java(entity.getUuid().toString())")
	public TestEto map(TestEntity entity);
}
