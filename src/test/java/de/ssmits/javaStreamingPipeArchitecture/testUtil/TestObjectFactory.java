package de.ssmits.javaStreamingPipeArchitecture.testUtil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.ssmits.javaStreamingPipeArchitecture.TestDto;
import de.ssmits.javaStreamingPipeArchitecture.TestEntity;

public class TestObjectFactory {
	private static final Random random = new Random();
	
	public static Stream<TestDto> generateTestDtoInstances(int amount) {
		return IntStream
			.range(0, amount)
			.parallel()
			.mapToObj(i -> generateTestDtoInstance());
	}
	
	private static TestDto generateTestDtoInstance() {
		TestDto dto = new TestDto();
		dto.id = random.nextLong();
		dto.version = random.nextLong();
		dto.uuid = UUID.randomUUID().toString();
		dto.payload = "Some Payload Content";
		
		return dto;
	}
	
	public static Stream<TestEntity> generateTestEntityInstances(int amount) {
		return IntStream
			.range(0, amount)
			.mapToObj(i -> generateTestEntityInstance());
	}
	
	private static TestEntity generateTestEntityInstance() {
		TestEntity entity = new TestEntity();
		entity.setPayload(UUID.randomUUID().toString());
		
		return entity;
	}
	
	public static Stream<TestEntity> generateMockTestEntityInstances(int amount) {
		return IntStream
			.range(0, amount)
			.mapToObj(i -> generateMockTestEntityInstance());
	}
	
	private static TestEntity generateMockTestEntityInstance() {
		UUID uuid = UUID.randomUUID();
		String payload = String.format("%s-%s", uuid.toString().replace("-", ""), UUID.randomUUID().toString());
		
		TestEntity entity = mock(TestEntity.class);
		when(entity.getId()).thenReturn(Long.MAX_VALUE);
		when(entity.getVersion()).thenReturn(Long.MAX_VALUE);
		when(entity.getUuid()).thenReturn(uuid);
		when(entity.getPayload()).thenReturn(payload);
		
		return entity;
	}
}
