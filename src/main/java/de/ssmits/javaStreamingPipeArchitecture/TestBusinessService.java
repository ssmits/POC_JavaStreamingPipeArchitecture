package de.ssmits.javaStreamingPipeArchitecture;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business Service Implementation
 * 
 * @author ssmits
 */
@Service
@Transactional
public class TestBusinessService {
	@Autowired
	private TestPersistenceService persistenceService;
	
	public TestBusinessService() {
		
	}
	
	public TestBusinessService(TestPersistenceService service) {
		this.persistenceService = service;
	}
	
	public List<TestEto> findAll_classic() {
		return persistenceService.findAll_classic();
	}
	
	public Stream<TestEto> findAll_streaming() {
		return persistenceService.findAll_streaming();
	}
	
	public Stream<TestEto> findAll_parallelStreaming() {
		return persistenceService.findAll_streaming().parallel();
	}
}
