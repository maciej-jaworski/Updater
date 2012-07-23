package com.autoupdater.server.models;

import java.sql.Connection; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource; 

import org.apache.commons.lang.RandomStringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.autoupdater.server.external.*;
import com.autoupdater.server.interfaces.UserDao;

/**
 * Implementation of UserDao.
 */
public class JdbcUserDao implements UserDao {
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
	 * Creates user in database from passed object.
	 * 
	 * @param user source of data
	 */
	public void create (User user) {
		String sql = "INSERT INTO users (username, pw, full_name, admin, package_admin) VALUES (?, ?, ?, ?, ?)";
		Connection conn = null;	
		boolean tAdmin;
		boolean tPackageAdmin;
		
		if (user.getUser_type().equals("Package Admin"))
			{
				tAdmin = false;
				tPackageAdmin = true;
			}
		else if (user.getUser_type().equals("System Admin"))
			{
				tAdmin = true;
				tPackageAdmin = true;
			}
		else
			{
				tAdmin = false;
				tPackageAdmin = false;
			}
		
		
		
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, user.getUsername ());
			ps.setString (2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			ps.setString (3, user.getName ());
			ps.setBoolean(4, tAdmin);
			ps.setBoolean(5, tPackageAdmin);
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
	 * Updates user in database from passed object.
	 * 
	 * @param user source of data
	 */
	public void update (User user) {
		String sql = "UPDATE users SET username = ?, full_name = ?, admin = ?, package_admin = ? WHERE id = ?";
		Connection conn = null;
		boolean tAdmin;
		boolean tPackageAdmin;
		
		if (user.getUser_type().equals("Package Admin"))
			{
				tAdmin = false;
				tPackageAdmin = true;
			}
		else if (user.getUser_type().equals("System Admin"))
			{
				tAdmin = true;
				tPackageAdmin = true;
			}
		else
			{
				tAdmin = false;
				tPackageAdmin = false;
			}
		
		
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, user.getUsername ());
			ps.setString (2, user.getName ());
			ps.setBoolean(3, tAdmin);
			ps.setBoolean(4, tPackageAdmin);
			ps.setString (5, Integer.toString(user.getId()));
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
	 * Deletes user in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id) {
		String sql = "DELETE FROM users WHERE id = ?";
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
	 * Returns user for passed login.
	 * 
	 * @param filename user's login
	 * @return         user
	 */
	public User findByUsername (String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, username);
			User user = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				user = new User (rs.getInt("id"), rs.getString ("username"), rs.getString ("pw"), rs.getString ("full_name"),
						rs.getBoolean("admin"),rs.getBoolean("package_admin"));
			rs.close ();
			ps.close ();
			return user;
		} catch (SQLException e) {
			throw new RuntimeException ();
		} finally {
			if (conn != null)
				try {
					conn.close ();
				} catch (SQLException e) {}
		}
	}

	/**
	 * Returns user for passed name.
	 * 
	 * @param id user's name
	 * @return   user
	 */
	public User findByName (String name) {
		String sql = "SELECT * FROM users WHERE full_name = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, name);
			
			User user = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				user = new User (rs.getInt("id"), rs.getString ("username"), rs.getString ("pw"), rs.getString ("full_name"),
						rs.getBoolean("admin"),rs.getBoolean("package_admin"));
			rs.close ();
			ps.close ();
			return user;
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
	 * Returns all users.
	 * 
	 * @return collection of users
	 */
	public List<User> findAll() {	
		String sql = "SELECT * FROM users";
		List<User> users = new ArrayList<User>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				User user = new User();
				user = new User (rs.getInt("id"), rs.getString ("username"), rs.getString ("pw"), rs.getString ("full_name"),
						rs.getBoolean("admin"),rs.getBoolean("package_admin"));	
				users.add(user);
			}

			rs.close ();
			ps.close ();
			return users;
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
	 * Returns user for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   user
	 */
	public User findById (int id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, Integer.toString(id));
			
			User user = null;
			ResultSet rs = ps.executeQuery ();
			if (rs.next ())
				user = new User (rs.getInt("id"), rs.getString ("username"), rs.getString ("pw"), rs.getString ("full_name"),
						rs.getBoolean("admin"),rs.getBoolean("package_admin"));
			rs.close ();
			ps.close ();
			return user;
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
	 * Creates new password for user.
	 * 
	 * @param id entry's ID
	 * @return   new password
	 */
	public String resetPwById (int id) {
		String newPass = RandomStringUtils.randomAlphanumeric(20);
		
		String sql = "UPDATE users SET pw = ? WHERE id = ?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString (1, BCrypt.hashpw(newPass, BCrypt.gensalt(31)));
			ps.setString (2, Integer.toString(id));
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
		
		return newPass;		
	}

	@Override
	public List<String> findAllUsernames() {
		String sql = "SELECT username as u FROM users";
		List<String> users = new ArrayList<String>();
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next ())
			{
				users.add(rs.getString("u"));
			}

			rs.close ();
			ps.close ();
			return users;
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
	public void changePasswordForId(PasswordEdit pe) {
		String sql = "UPDATE users SET pw = ? where id = ?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection ();
			PreparedStatement ps = conn.prepareStatement (sql);
			ps.setString(1, BCrypt.hashpw(pe.getNewPw(), BCrypt.gensalt()));
			ps.setInt(2, pe.getUserId());
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
}
