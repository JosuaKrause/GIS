package gis.data.db;

import gis.data.db.config.ConfigurationException;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.IConfiguration;

public class Database {
	
	private IConfiguration config;
	
	public final BerlinLocationBackend berlinLocation;
	
	public Database() {
		try {
			config = FileConfiguration.read("config.txt");
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//initialize backends
		berlinLocation = new BerlinLocationBackend(config);
		//TODO
	}
}
