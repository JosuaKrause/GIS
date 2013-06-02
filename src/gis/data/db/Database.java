package gis.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
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
	private Connection connection;
	
	public Database() {
		boolean quit = false;
		try {
			config = FileConfiguration.read("config.txt");
			connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
			((PGConnection)connection).addDataType("geometry", org.postgis.PGgeometry.class);//add support for Geometry types
		} catch (ConfigurationException e) {
			quit = true;
			e.printStackTrace();
		} catch (SQLException e) {
			quit = true;
			e.printStackTrace();
		}
		if (quit) {
			System.exit(1);
		}
	}
	
	public List<GeoMarker> getGeometry(Table table) {
		
		List<GeoMarker> markers = new ArrayList<GeoMarker>();
		
		try{
			Statement s = connection.createStatement(); //create query statement
			ResultSet r = s.executeQuery("SELECT gid, geom, " + table.infoColumnName +  " FROM " + table.name + " ORDER BY gid LIMIT 10000");//TODO
			while(r.next()) { //iterate while there are polygons to retrieve
				int gid = r.getInt(1);
				PGgeometry geom = (PGgeometry)r.getObject(2); //retrieve the object by index
				String info = r.getString(3);
				ElementId id = new ElementId(table, gid);
				GeoMarker m = GeometryConverter.convert(id, geom);
				m.info = info;
				markers.add(m);
			}
			s.close();//close statement when finished
		}catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
		
		return markers;
	}
	
	
	public List<ElementId> getByCoordinate(Coordinate c, List<Table> tables, double maxDistMeters) {
		List<ElementId> ids = new ArrayList<ElementId>();
		for (Table t : tables) {
			switch (t.geometryType) {
				case LINESTRING:
				
				case POINT:
					getPointsByCoordinate(c, t, maxDistMeters, ids);
					break;
				case POLYGON:
					getPolygonsByCoordinate(c, t, ids);
			}
		}
		return ids;
	}
	
	
	private void getPointsByCoordinate(Coordinate c, Table table, double maxDistMeters, List<ElementId> ids) {
		Statement stmt = null;
	    String query = "SELECT gid FROM " + table.name +
	    		" WHERE ST_DWithin(geom, ST_SetSRID(ST_Point(" +
	    		c.getLon() + "," + c.getLat() + "), 4326), " + maxDistMeters + ", true)";
	    try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        while (rs.next()) {
	        	int gid = rs.getInt(1);
	        	ElementId id = new ElementId(table, gid);
	        	ids.add(id);
	        }
	    } catch (SQLException e ) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) {
	        	try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	
	private void getPolygonsByCoordinate(Coordinate c, Table table, List<ElementId> ids) {
		Statement stmt = null;
	    String query = "SELECT gid FROM " + table.name +
	    		" WHERE ST_Contains(geom, ST_SetSRID(ST_Point(" +
	    		c.getLon() + ", " + c.getLat() + "), 4326))";
	    try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        while (rs.next()) {
	        	int gid = rs.getInt(1);
	        	ElementId id = new ElementId(table, gid);
	        	ids.add(id);
	        }
	    } catch (SQLException e ) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) {
	        	try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	
//	select gid, ST_Distance(geom, ST_SetSRID(ST_Point(13.388786,52.527084), 4326), true)
//	from berlin_poi where ST_DWithin(geom, ST_SetSRID(ST_Point(13.388786,52.527084), 4326), 100, true)
}
