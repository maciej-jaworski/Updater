package com.autoupdater.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.autoupdater.server.interfaces.ProgramDao;

public class JdbcProgramDao implements ProgramDao{

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
	
	@Override
	public void create(Program prog) {
		String sql = "INSERT INTO programs (name) VALUES (?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, prog.getName());
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

	@Override
	public void update(Program prog) {
		String sql = "UPDATE programs SET name = ? WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, prog.getName());
			ps.setString (2, Integer.toString(prog.getId()));
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

	@Override
	public void delete(int id) {
		String sql = "DELETE FROM programs WHERE id = ?";
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

	@Override
	public Program findById(int id) {
		String sql = "SELECT * FROM programs WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, Integer.toString(id));
			
			Program prog = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				prog = new Program (rs.getInt("id"),rs.getString ("name"));
			rs.close ();
			ps.close ();
			return prog;
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
	public Program findByName(String name) {
		String sql = "SELECT * FROM programs WHERE name = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, name);
			
			Program prog = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				prog = new Program (rs.getInt("id"),rs.getString ("name"));
			rs.close ();
			ps.close ();
			return prog;
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
	public List<Program> findAll() {
		String sql = "SELECT * FROM programs";
		List<Program> programs = new ArrayList<Program>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ()) {
				Program prog = new Program (rs.getInt("id"), rs.getString ("name"));
				programs.add(prog);
			}

			rs.close ();
			ps.close ();
			return programs;
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
	public List<String> findAllNames() {
		String sql = "SELECT * FROM programs";
		List<String> programs = new ArrayList<String>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				String pack = rs.getString ("name");
				programs.add(pack);
			}

			rs.close ();
			ps.close ();
			return programs;
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
	public int getIdFromName(String name) {
		String sql = "SELECT * FROM programs WHERE name = ?";
		Connection conn = null;
		int id = -1;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, name);
			
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				id = rs.getInt("id");
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

}
