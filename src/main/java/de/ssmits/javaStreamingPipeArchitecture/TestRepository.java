package de.ssmits.javaStreamingPipeArchitecture;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
	/**
	 * Fetches all test entities from the underlying persistence unit in the classic way. 
	 * Materialize to list happens within the repository.
	 * 
	 * @return List of persisted test entity instances
	 * 
	 * @see de.ssmits.javaStreamingPipeArchitecture.TestEntity
	 * @see java.util.List
	 */
	@Query("SELECT E FROM #{#entityName} E")
	List<TestEntity> findAll_classic();
	
	/**
	 * Fetches all test entities from the underlying persistence unit in the streaming way. 
	 * The returning stream is backed by a rolling result set which lazy loads data from the DB. The lazy
	 * loading chunk size can be configured via hibernate.
	 * 
	 * @return List of persisted test entity instances
	 * 
	 * @see de.ssmits.javaStreamingPipeArchitecture.TestEntity
	 * @see java.util.Stream
	 */
	@Query("SELECT E FROM #{#entityName} E")
	Stream<TestEntity> findAll_streaming();
}
