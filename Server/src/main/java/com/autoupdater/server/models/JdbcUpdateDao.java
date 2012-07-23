package com.autoupdater.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.autoupdater.server.interfaces.UpdateDao;

/**
 * Implementation of UpdateDao.
 */
public class JdbcUpdateDao implements UpdateDao {
	
	protected static Logger logger = Logger.getLogger(JdbcUpdateDao.class);
	
	/**
	 * DataSource object.
	 */
	private DataSource dataSource;
	
	/**
	 * Sets data source.
	 * 
	 * @param dataSource DataSource object
	 */
	public void setDataSource (DataSource dataSource) {
		this.dataSource = dataSource; 
	}
	
	/**
	 * Creates update in database from passed object.
	 * 
	 * @param update source of data
	 */
	public void create(Update update) {		
		java.util.Date now = new java.util.Date();
		java.sql.Date sqlNow = new java.sql.Date(now.getTime());
		
		String sql = "INSERT INTO updates (package_id, uploader_id, changelog, added, ver1, ver2, ver3, ver4, filedata, dev) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, update.getPackage_id());
			ps.setInt(2, update.getUploader_id());
			ps.setString(3, update.getChangelog());
			ps.setString(4, sqlNow.toString());
			ps.setInt(5, update.getVer1());
			ps.setInt(6, update.getVer2());
			ps.setInt(7, update.getVer3());
			ps.setInt(8, update.getVer4());
			ps.setBytes(9, update.getFiledataAsBytes());
			ps.setBoolean(10, update.isDev());

			ps.executeUpdate ();
			ps.close ();
			
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
		
	}

	/**
	 * Updates update in database from passed object.
	 * 
	 * @param update source of data
	 */
	public void update(Update update) {
		String sql = "UPDATE updates SET package_id = ?," +
				" uploader_id = ?," +
				" changelog = ?," +
				" added = ?," +
				" ver1 = ?," +
				" ver2 = ?," +
				" ver3 = ?," +
				" ver4 = ?," +
				" dev = ? " +
				" WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, update.getPackage_id());
			ps.setInt(2, update.getUploader_id());
			ps.setString(3, update.getChangelog());
			ps.setString(4, update.getData().toString());
			ps.setInt(5, update.getVer1());
			ps.setInt(6, update.getVer2());
			ps.setInt(7, update.getVer3());
			ps.setInt(8, update.getVer4());
			ps.setBoolean(9, update.isDev());
			ps.setInt(10, update.getId());
			ps.executeUpdate ();
			ps.close ();
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	/**
	 * Deletes update in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete(int id) {
		String sql = "DELETE FROM updates WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, Integer.toString(id));
			ps.executeUpdate ();
			ps.close ();
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
		
	}

	/**
	 * Returns update for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   update
	 */
	public Update findById (int id) {
		String sql = "SELECT * FROM updates WHERE id = ?";
		Update update = new Update();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				update = new Update (rs.getInt("id"), rs.getInt("package_id"), rs.getInt("uploader_id"), rs.getString ("changelog"),
						rs.getDate("added"), rs.getInt("ver1"), rs.getInt("ver2"), rs.getInt("ver3"), rs.getInt("ver4"));
				update.setDev(rs.getBoolean("dev"));
				
				String sql2 = "SELECT name as fn FROM packages WHERE id = ?";
				PreparedStatement ps2 = conn.prepareStatement (sql2);
				ps2.setInt(1, update.getPackage_id());
				ResultSet rs2 = ps2.executeQuery();
				
				while (rs2.next ()) {
					update.setPackage_name(rs2.getString("fn"));
				}
				
				rs2.close();
			}

			rs.close ();
			ps.close ();
			return update;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	/**
	 * Returns all updates.
	 * 
	 * @return collection of updates
	 */
	public List<Update> findAll() {	
		String sql = "SELECT id AS a," +
				"package_id AS b," +
				"uploader_id AS c," +
				"changelog AS e," +
				"added AS f," +
				"ver1 AS g," +
				"ver2 AS h," +
				"ver3 AS i," +
				"ver4 AS j," +
				"dev AS k" +
				" FROM updates";
		List<Update> updates = new ArrayList<Update>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				Update up = new Update();
				up = new Update (rs.getInt("a"), rs.getInt("b"), rs.getInt("c"), rs.getString ("e"),
						rs.getDate("f"), rs.getInt("g"), rs.getInt("h"), rs.getInt("i"), rs.getInt("j"));
				up.setDev(rs.getBoolean("k"));
				
				String sql2 = "SELECT * FROM packages WHERE id = ?";
				PreparedStatement ps2 = conn.prepareStatement (sql2);
				ps2.setString(1, rs.getString("b"));
				ResultSet rs2 = ps2.executeQuery();
				
				while (rs2.next ())
				{
					up.setPackage_name(rs2.getString("name"));
				}
				
				rs2.close();
				
				updates.add(up);
			}

			rs.close ();
			ps.close ();
			return updates;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	/**
	 * Returns newest update for passed package ID.
	 * 
	 * @param id package's ID
	 * @return   update
	 */
	public Update findNewestByPackageId(int id) {
		
	String sql = "SELECT * FROM updates WHERE package_id = ? ORDER BY ver1 DESC, ver2 DESC, ver3 DESC, ver4 DESC LIMIT 1";
		
		
		Update update = new Update();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
					
			//logger.error("DEBUG: " + ps.toString());
			
			while (rs.next ())
			{
				update = new Update (rs.getInt("id"), rs.getInt("package_id"), rs.getInt("uploader_id"), rs.getString ("changelog"),
						rs.getDate("added"), rs.getInt("ver1"), rs.getInt("ver2"), rs.getInt("ver3"), rs.getInt("ver4"));
				
				update.setDev(rs.getBoolean("dev"));
				
				String sqlp = "SELECT name FROM packages WHERE id = ?";
				PreparedStatement ps2 = conn.prepareStatement (sqlp);
				ps2.setInt(1, id);
				ResultSet rs2 = ps2.executeQuery();
				
				while (rs2.next ())
				{
					update.setPackage_name(rs2.getString("name"));
				}
				
				rs2.close();
			}
			
			rs.close ();
			ps.close ();
						
			return update;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	/**
	 * Returns bytes of file's content.
	 * 
	 * @return file's content as byte array
	 */
	public byte[] getBytesById(int id) {
		String sql = "SELECT filedata as f FROM updates WHERE id = ?";
		byte[] filedata = null;
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				filedata = rs.getBytes("f");
			}

			rs.close ();
			ps.close ();
			return filedata;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	@Override
	public List<Update> findAllByPackageId(int id) {
		String sql = "SELECT id AS a," +
				"package_id AS b," +
				"uploader_id AS c," +
				"changelog AS e," +
				"added AS f," +
				"ver1 AS g," +
				"ver2 AS h," +
				"ver3 AS i," +
				"ver4 AS j," +
				"dev as k" +
				" FROM updates WHERE package_id = ?";
		List<Update> updates = new ArrayList<Update>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				Update up = new Update();
				up = new Update (rs.getInt("a"), rs.getInt("b"), rs.getInt("c"), rs.getString ("e"),
						rs.getDate("f"), rs.getInt("g"), rs.getInt("h"), rs.getInt("i"), rs.getInt("j"));
				up.setDev(rs.getBoolean("k"));
				
				String sql2 = "SELECT * FROM packages WHERE id = ?";
				PreparedStatement ps2 = conn.prepareStatement (sql2);
				ps2.setString(1, rs.getString("b"));
				ResultSet rs2 = ps2.executeQuery();
				
				while (rs2.next ())
				{
					up.setPackage_name(rs2.getString("name"));
				}
				
				rs2.close();
				
				updates.add(up);
			}

			rs.close ();
			ps.close ();
			return updates;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	@Override
	public List<ChangelogVer> findPackageChangelogs(int packageId) {
		
		String sql = "SELECT changelog AS cl, ver1 AS v1, ver2 AS v2, ver3 AS v3, ver4 AS v4 " +
				"FROM updates WHERE package_id = ? ORDER BY ver1 DESC, ver2 DESC, ver3 DESC, ver4 DESC";
		
		List<ChangelogVer> changelogs = new ArrayList<ChangelogVer>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, packageId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
				{				
					String ver = rs.getString("v1")+"."+rs.getString("v2")+"."+rs.getString("v3")+"."+rs.getString("v4");
					changelogs.add(new ChangelogVer(ver, rs.getString("cl")));
				}
				
			rs.close ();
			ps.close ();
						
			return changelogs;
		} catch (SQLException e) {
			throw new RuntimeException (e);
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}
}
