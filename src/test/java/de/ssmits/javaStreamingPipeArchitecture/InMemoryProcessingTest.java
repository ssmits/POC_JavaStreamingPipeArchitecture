package de.ssmits.javaStreamingPipeArchitecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ssmits.javaStreamingPipeArchitecture.testUtil.TestObjectFactory;

/**
 * This benchmark test processes the 3 tier encapsulated architecture with both the streaming 
 * and the classic pattern. This test demonstrate the performance behavior of streaming pipe
 * processing compared to the classical processing way.
 * 
 * @author ssmits
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryProcessingTest implements HasLogger {
	private static final List<TestEntity> testEntities = new ArrayList<>();
    private static final List<TestDto> testDtos = new ArrayList<>();
	private static final List<ExecutionTime> executionTimes = new ArrayList<>();
	
	private class ExecutionTime {
		public ArchitectureType type;
		public int recordCount;
		private long duration;
		
		public ExecutionTime(ArchitectureType type, int recordCount, long duration) {
			this.type = type;
			this.recordCount = recordCount;
			this.duration = duration;
		}
	}
	
	// Test Setup vars
	private TestRepository mockRepository = mock(TestRepository.class);
	private static final TestEtoMapper persistenceMapper = new TestEtoMapperImpl();
	private static final TestDtoMapper presentationMapper = new TestDtoMapperImpl();
	private TestPersistenceService persistenceService = new TestPersistenceService(mockRepository, persistenceMapper);
	private TestBusinessService businessService = new TestBusinessService(persistenceService);
	private TestController controller = new TestController(presentationMapper, businessService);
	private MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
	
	// Test stage vars
    private int recordCount;
	
	@Parameters(name="#{index}: In-Memory Performance Benchmark [recordCount=''{0}'']")
    public static Collection<Object[]> data() {
    	HasLogger.getStaticLogger(InMemoryProcessingTest.class).info("Initializing parameterized im-memory processing test");
    	
    	return IntStream
    		.of(1, 10, 50, 100, 300, 500, 800, 1000, 1500, 2000, 3000, 5000, 8000, 10000, 15000)
    		.mapToObj(i -> new Object[] {i})
    		.collect(Collectors.toList());
    }
    
    public InMemoryProcessingTest(int recordCount) {
    	this.recordCount = recordCount;
    	
    	int objectsToAdd = testEntities.size() == 0 ? recordCount : recordCount - testEntities.size();
    	
    	if (objectsToAdd > 0) {
    		getLogger().debug("- Adding {} objects to the test bed. [initialSize='{}']", 
	    		objectsToAdd,
	    		testEntities.size()
	    	);
    		
	    	TestObjectFactory
	    		.generateMockTestEntityInstances(objectsToAdd)
	    		.forEach(entity -> {
	    			testEntities.add(entity);
	    			testDtos.add(presentationMapper.map(persistenceMapper.map(entity)));
	    		}
	    	);
    	}
    	
    	when(mockRepository.findAll_classic()).thenReturn(testEntities);
    	when(mockRepository.findAll_streaming()).thenReturn(testEntities.stream());
    }
    
	@Test
    public void testClassicPipe() throws Exception {
		profilingTest(ArchitectureType.CLASSIC, recordCount);
	}
	
	@Test
    public void testStreamingPipe() throws Exception {
		profilingTest(ArchitectureType.STREAMING, recordCount);
	}
	
	@Test
    public void testParallelStreamingPipe() throws Exception {
		profilingTest(ArchitectureType.PARALLEL_STREAMING, recordCount);
	}
	
	@Test
	public void testStreamingPipeWasFasterThanClassic() {
		final long execTimeStreaming = getExecutionTime(ArchitectureType.STREAMING, this.recordCount);
		final long execTimeClassic = getExecutionTime(ArchitectureType.CLASSIC, this.recordCount);
		
		assertThat(execTimeStreaming)
			.as(String.format(
				"Expecting streaming pipe to be faster than classic processing. " + 
				"[streaming='%s ms', classic='%s ms']", 
				execTimeStreaming,
				execTimeClassic))
			.isLessThanOrEqualTo(execTimeClassic);
	}
	
	@Test
	public void testParallelStreamingPipeWasFasterThanStreamingPipe() {
		final long execTimeStreaming = getExecutionTime(ArchitectureType.STREAMING, this.recordCount);
		final long execTimeParallelStreaming = getExecutionTime(ArchitectureType.PARALLEL_STREAMING, this.recordCount);
		
		assertThat(execTimeParallelStreaming)
			.as(String.format(
				"Expecting parallel streaming pipe to be faster than single threaded streaming pipe. " + 
				"[parallelStreaming='%s ms', streaming='%s ms']", 
				execTimeParallelStreaming,
				execTimeStreaming))
			.isLessThanOrEqualTo(execTimeStreaming);
	}
	
	private void profilingTest(ArchitectureType type, int recordCount) throws Exception {
		final long start = System.currentTimeMillis();
		
		architectureTest(type, recordCount);
		
		final ExecutionTime execTime = new ExecutionTime(type, recordCount, System.currentTimeMillis() - start);
		executionTimes.add(execTime);
		
		getLogger().info("Profing Test step finished. [architectureType='{}', recordCount='{}', duration='{}']", 
			execTime.type, 
			execTime.recordCount, 
			execTime.duration
		);
	}
	
	private static long getExecutionTime(ArchitectureType type, int recordCount) {
		return executionTimes
			.stream()
			.filter(e -> e.type.equals(type))
			.filter(e -> e.recordCount == recordCount)
			.map(e -> e.duration)
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}
	
	/**
     * Executes the architecture test.
     * 
     * @param The Architecture type to test
     * @param amount Amount of test records to process by all layers
     */
    private void architectureTest(ArchitectureType type, int recordCount) throws Exception {
    	mvc
            .perform(get("/testData?type={type}", type.name()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(recordCount)))
            .andExpect(jsonPath("$[0].id").value(testDtos.get(0).id))
            .andExpect(jsonPath("$[0].version").value(testDtos.get(0).version))
            .andExpect(jsonPath("$[0].uuid").value(testDtos.get(0).uuid))
            .andExpect(jsonPath("$[0].payload").value(testDtos.get(0).payload));
    }
    
    /**
     * Report the collected statistics into an xls spread sheet.
     * 
     * @throws IOException If the xls write operation fails
     */
    @AfterClass
    public static void generateReport() throws IOException {
    	StringBuilder xls = new StringBuilder();
    	xls.append(String.format("<table>%n"));
    	xls.append(String.format("\t<tr>%n"));
    	xls.append(String.format("\t\t<td><b>Record Count</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Exec. Time Classic (ms)</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Exec. Time Streaming (ms)</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Exec. Time Parallel-Streaming (ms)</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Deviation Streaming to Classic (ms)</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Deviation Parallel-Streaming to Streaming (ms)</b></td>%n"));
    	xls.append(String.format("\t\t<td><b>Winning Pipe</b></td>%n"));
    	xls.append(String.format("\t</tr>%n"));
    	
    	executionTimes
    		.stream()
    		.map(e -> e.recordCount)
    		.distinct()
    		.forEach(c -> {
    			final long execTimeClassic = getExecutionTime(ArchitectureType.CLASSIC, c);
    			final long execTimeStreaming = getExecutionTime(ArchitectureType.STREAMING, c);
    			final long execTimeParallelStreaming = getExecutionTime(ArchitectureType.PARALLEL_STREAMING, c);
    			
    			long deviationStreamingToClassic = 0;
    			long deviationParallelStreamingToStreaming = 0;
    			
    			if (execTimeClassic > execTimeStreaming) {
    				deviationStreamingToClassic = execTimeClassic - execTimeStreaming;
    			} else if (execTimeClassic < execTimeStreaming){
    				deviationStreamingToClassic = execTimeStreaming - execTimeClassic;
    			}
    			
    			if (execTimeStreaming > execTimeParallelStreaming) {
    				deviationParallelStreamingToStreaming = execTimeStreaming - execTimeParallelStreaming;
    			} else if (execTimeStreaming < execTimeParallelStreaming){
    				deviationParallelStreamingToStreaming = execTimeParallelStreaming - execTimeStreaming;
    			}
    			
    			ArchitectureType winningPipe = Stream
    				.of(Pair.of(execTimeClassic, ArchitectureType.CLASSIC),
    					Pair.of(execTimeStreaming, ArchitectureType.STREAMING),
    					Pair.of(execTimeParallelStreaming, ArchitectureType.PARALLEL_STREAMING))
    				.sorted((a, b) -> a.getFirst().compareTo(b.getFirst()))
    				.findFirst()
    				.map(Pair::getSecond)
    				.orElseThrow(IllegalStateException::new);
    				
    			xls.append(String.format("\t<tr>%n"));
            	xls.append(String.format("\t\t<td>%s</td>%n", c));
            	xls.append(String.format("\t\t<td align='right'>%s</td>%n", execTimeClassic));
            	xls.append(String.format("\t\t<td align='right'>%s</td>%n", execTimeStreaming));
            	xls.append(String.format("\t\t<td align='right'>%s</td>%n", execTimeParallelStreaming));
            	xls.append(String.format("\t\t<td align='right'>%s</td>%n", deviationStreamingToClassic));
            	xls.append(String.format("\t\t<td align='right'>%s</td>%n", deviationParallelStreamingToStreaming));
            	xls.append(String.format("\t\t<td align='center'>%s</td>%n", winningPipe));
            	xls.append(String.format("\t</tr>%n"));
    		});
    	
    	xls.append(String.format("</table>%n"));
    	
    	FileUtils.writeStringToFile(new File("target/BenchmarkStatistics.xls"), xls.toString(), Charset.forName("UTF-8"));
    }
}
