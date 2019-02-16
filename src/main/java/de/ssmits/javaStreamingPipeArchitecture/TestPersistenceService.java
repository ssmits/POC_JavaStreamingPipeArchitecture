package de.ssmits.javaStreamingPipeArchitecture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence Service Implementation
 * 
 * @author ssmits
 */
@Service
@Transactional
public class TestPersistenceService {
	@Autowired
	private TestRepository repository;
	
	@Autowired
	private TestEtoMapper mapper;
	
	public TestPersistenceService() {
		
	}
	
	public TestPersistenceService(TestRepository repository, TestEtoMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}
	
	public List<TestEto> findAll_classic() {
		return repository
			.findAll_classic()
			.stream()
			.map(mapper::map)
			.collect(Collectors.toList());
	}
	
	public Stream<TestEto> findAll_streaming() {
		return repository
			.findAll_streaming()
			.map(mapper::map);
	}
}
