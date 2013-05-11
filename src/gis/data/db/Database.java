package gis.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

import gis.data.GeometryConverter;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.config.ConfigurationException;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.IConfiguration;

public class Database {
	
	private IConfiguration config;
	
	public Database() {
		try {
			config = FileConfiguration.read("config.txt");
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
public List<GeoMarker> getGeometry(Table table) {
		
		List<GeoMarker> markers = new ArrayList<GeoMarker>();
		
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");//create postgresql driver
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
			
			((PGConnection)conn).addDataType("geometry", org.postgis.PGgeometry.class);//add support for Geometry types
			Statement s = conn.createStatement(); //create query statement
			ResultSet r = s.executeQuery("SELECT gid, geom FROM " + table.name + " LIMIT 10000");//TODO
			while(r.next()) { //iterate while there are polygons to retrieve
				int gid = r.getInt(1);
				PGgeometry geom = (PGgeometry)r.getObject(2); //retrieve the object by index
				ElementId id = new ElementId(table, gid);
				GeoMarker m = GeometryConverter.convert(id, geom);
				markers.add(m);
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
		
		return markers;
	}
}
