package de.ssmits.javaStreamingPipeArchitecture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testData")
@Transactional
public class TestController {
	@Autowired
	private TestDtoMapper mapper;
	
	@Autowired
	private TestBusinessService service;
	
	public TestController() {
		
	}
	
	public TestController(TestDtoMapper mapper, TestBusinessService service) {
		this.mapper = mapper;
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<TestDto[]> findAll(@RequestParam(name="type", required=true) ArchitectureType type) {
		switch (type) {
			case CLASSIC:
				return ResponseEntity.ok(service
					.findAll_classic()
					.stream()
					.map(mapper::map)
					.toArray(TestDto[]::new)
				);
			case PARALLEL_STREAMING:
				return ResponseEntity.ok(service
					.findAll_parallelStreaming()
					.map(mapper::map)
					.toArray(TestDto[]::new)
				);
			case STREAMING:
				return ResponseEntity.ok(service
					.findAll_streaming()
					.map(mapper::map)
					.toArray(TestDto[]::new)
				);
			default:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
