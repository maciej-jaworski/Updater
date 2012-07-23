package com.autoupdater.server.singletons;

import org.apache.log4j.Logger;

import com.autoupdater.server.external.*;
import com.autoupdater.server.interfaces.UpdateDao;

/**
 * Stores cache for server.
 */
public class CacheStorage {
	/**
	 * Cache for files.
	 */
	private Cache<Integer, byte[]> FileCache;
	
	/**
	 * DataSource.
	 */
	private UpdateDao ud;
	
	/**
	 * Class logger.
	 */
	protected static Logger logger = Logger.getLogger (CacheStorage.class);
	
	/**
	 * Creates cacheStorage.
	 */
	public CacheStorage () {
		FileCache = new Cache <Integer, byte[]> (new FileCacheSource (), false).setMaxKeep (10).setResetCounter (10);
	}
	
	/**
	 * Runs cleaning procedure.
	 */
	public void runCleanup () {
		FileCache.runCleanup ();
	}
	
	/**
	 * Sets update dao.
	 * 
	 * @param UpdateDao UpdateDao object
	 */
	public void setUpdateDao (UpdateDao ud) {
		this.ud = ud; 
	}
	
	/**
	 * Sets maximal amount of cached object kept after cleaning.
	 * 
	 * @param maxKeep maximal amount of cached object kept after cleaning
	 */
	public void setMaxKeep (int maxKeep) {
		FileCache.setMaxKeep (maxKeep);
	}
	
	/**
	 * Sets maximal amount of memory in bytes above which runCleanup() is called.
	 * 
	 * @param maxMemory maximal amount of memory in bytes above which runCleanup() is called
	 */
	public void setMaxMemory (long maxMemory) {
		FileCache.setMaxMemory (maxMemory);
	}
	
	/**
	 * Sets number of cleaning cycles after which request list is reset.
	 * 
	 * @param resetCounter number of cycles after which list is reset
	 */
	public void setResetCounter (int resetCounter) {
		FileCache.setResetCounter (resetCounter);
	}
	
	/**
	 * Obtain file by it's name.
	 * 
	 * @param id file's ID
	 * @return file's content
	 */
	public synchronized byte[] getFile (int id) {
		return FileCache.getDataFor (id);
	}
	
	/**
	 * Removes file from cache.
	 * 
	 * @param id file's ID
	 */
	public synchronized void resetFile (int id) {
		FileCache.forceReload (id);
	}
	
	/**
	 * Implements CacheSource for files.
	 */
	private class FileCacheSource implements CacheSource <Integer, byte[]> {
		/**
		 * Obtain file by it's name.
		 */
		public byte[] getElement (Integer input) {
			return ud.getBytesById (input);
		}
	}
}
