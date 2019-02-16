package de.ssmits.javaStreamingPipeArchitecture;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * HasLogger is a feature interface that provides Logging capability for anyone
 * implementing it where logger needs to operate in {@link Serializable} environment
 * without being static.
 * 
 * @author Sascha.Smits@ush-network.de
 */
public interface HasLogger {
	/**
	 * Returns a default logger instance dedicated to
	 * the implementing class.
	 * 
	 * @return Logger instance
	 * @see org.slf4j.Logger
	 */
	default Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}
	
	static Logger getStaticLogger(Class<?> type) {
		return LoggerFactory.getLogger(type);
	}
	
	/**
	 * Returns a default logger instance dedicated to
	 * the specified type class.
	 * 
	 * @param type The type class the logger should be dedicated to
	 * @return Logger instance
	 * @see org.slf4j.Logger
	 */
	default Logger getLogger(Class<?> type) {
		return LoggerFactory.getLogger(type);
	}
}