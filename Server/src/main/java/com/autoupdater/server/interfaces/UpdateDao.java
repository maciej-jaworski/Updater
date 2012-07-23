package com.autoupdater.server.interfaces;

import java.util.List;

import com.autoupdater.server.models.ChangelogVer;
import com.autoupdater.server.models.Update;

/**
 * Interface for Update DAO.
 * 
 * Which implementation is returned is set in WEB-INF/classes/beans.xml file.
 */
public interface UpdateDao {
	/**
	 * Creates update in database from passed object.
	 * 
	 * @param update source of data
	 */
	public void create (Update update);
	
	/**
	 * Updates update in database from passed object.
	 * 
	 * @param update source of data
	 */
	public void update (Update update);
	
	/**
	 * Deletes update in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id);
	
	/**
	 * Returns update for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   update
	 */
	public Update findById (int id);
	
	/**
	 * Returns newest update for passed package ID.
	 * 
	 * @param id package's ID
	 * @return   update
	 */
	public Update findNewestByPackageId (int id);
	
	/**
	 * Returns all updates.
	 * 
	 * @return collection of updates
	 */
	public List<Update> findAll ();
	
	/**
	 * Returns updates for package identified by provided id
	 * 
	 * @return collection of updates
	 */
	public List<Update> findAllByPackageId (int id);
	
	/**
	 * Returns bytes of file's content.
	 * 
	 * @return file's content as byte array
	 */
	public byte[] getBytesById (int id);
	
	/**
	 * Returns changelogs for a package by its ID.
	 * 
	 * @param packageId package ID
	 * @return          list of changes
	 */
	public List<ChangelogVer> findPackageChangelogs(int packageId);
}
