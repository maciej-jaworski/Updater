package com.autoupdater.server.models;

import java.sql.Connection; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;
 
import javax.sql.DataSource;

import com.autoupdater.server.interfaces.PackageDao;

/**
 * Implementation of PackageDao.
 */
public class JdbcPackageDao implements PackageDao {
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
	 * Creates package in database from passed object.
	 * 
	 * @param pack source of data
	 */
	public void create (Package pack) {
		String sql = "INSERT INTO packages (name, program_id) VALUES (?, ?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, pack.getName());
			ps.setInt(2, pack.getProgramId());
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
	 * Updates package in database from passed object.
	 * 
	 * @param pack source of data
	 */
	public void update (Package pack) {
		String sql = "UPDATE packages SET name = ?, program_id = ? WHERE id = ?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, pack.getName());
			ps.setInt(2, pack.getProgramId());
			ps.setInt(3, pack.getId());
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
	 * Deletes package in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id) {
		String sql = "DELETE FROM packages WHERE id = ?";
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
	 * Returns package for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   package
	 */
	public Package findById (int id) {
		String sql = "SELECT * FROM packages WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, Integer.toString(id));
			
			Package pack = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				pack = new Package (rs.getInt("id"),rs.getString ("name"), rs.getInt("program_id"));
			rs.close ();
			ps.close ();
			return pack;
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
	 * Returns all packages.
	 * 
	 * @return collection of packages
	 */
	public List<Package> findAll() {

		String sql = "SELECT * FROM packages";
		List<Package> packages = new ArrayList<Package>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ()) {
				Package pack = new Package (rs.getInt("id"),rs.getString ("name"), rs.getInt("program_id"));
				packages.add(pack);
			}

			rs.close ();
			ps.close ();
			return packages;
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
	 * Returns all package names.
	 * 
	 * @return collection of names
	 */
	public List<String> findAllNames() {

		String sql = "SELECT * FROM packages";
		List<String> packages = new ArrayList<String>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				String pack = rs.getString ("name");
				packages.add(pack);
			}

			rs.close ();
			ps.close ();
			return packages;
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
	public List<Package> findByProgramId(int programId) {
		String sql = "SELECT * FROM packages WHERE program_id = ?";
		List<Package> packages = new ArrayList<Package>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, programId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ()) {
				Package pack = new Package (rs.getInt("id"),rs.getString ("name"), rs.getInt("program_id"));
				packages.add(pack);
			}

			rs.close ();
			ps.close ();
			return packages;
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
	public int getProgramIdFromPackageId(int packageId) {
			String sql = "SELECT * FROM packages WHERE id = ?";
			Connection conn = null;
			int id = -1;
			try {
				conn = dataSource.getConnection ();
				PreparedStatement ps = conn.prepareStatement (sql);
				ps.setInt (1, packageId);
				
				ResultSet rs = ps.executeQuery ();
				if (rs.next ())
					id = rs.getInt("program_id");
				rs.close ();
				ps.close ();
				return id;
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
	public List<String> findAllNames(int programId) {
		String sql = "SELECT * FROM packages WHERE program_id = ?";
		List<String> packages = new ArrayList<String>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setInt(1, programId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				String pack = rs.getString ("name");
				packages.add(pack);
			}

			rs.close ();
			ps.close ();
			return packages;
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
