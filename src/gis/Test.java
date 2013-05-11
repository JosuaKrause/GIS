package gis;

import gis.data.db.config.ConfigurationException;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.IConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

public class Test {
	
	public static void main(String[] args) {
		
		IConfiguration config = null;
		try {
			config = FileConfiguration.read("config.txt");
			System.out.println(config);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");//create postgresql driver
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
			
			((PGConnection)conn).addDataType("geometry", org.postgis.PGgeometry.class);//add support for Geometry types
			Statement s = conn.createStatement(); //create query statement
			ResultSet r = s.executeQuery("select gid, schluessel, lor, geom from berlin_administrative");
			while(r.next()) { //iterate while there are polygons to retrieve
				PGgeometry geom = (PGgeometry)r.getObject(4); //retrieve the object by index
				// do something with geometry ,i.e. store in the given data structure
				//TODO
			}
			s.close();//close statement when finished
		}catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		} finally {
			if (conn != null)
				try{
					conn.close(); //close connection when finished
				}catch(Exception ex) {}
		}
		
	}
	
}
