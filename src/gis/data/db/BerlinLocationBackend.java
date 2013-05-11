package gis.data.db;


import gis.data.datatypes.Table;
import gis.data.db.config.IConfiguration;

public class BerlinLocationBackend extends TableBackend {

	public BerlinLocationBackend(IConfiguration config) {
		super(Table.BERLIN_LOCATION, config);
	}
	
}
