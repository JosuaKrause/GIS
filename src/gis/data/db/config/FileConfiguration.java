package gis.data.db.config;

import java.util.HashMap;
import java.util.Map;

public class FileConfiguration implements IConfiguration {
	
	private final String url;
	private final String user;
	private final String password; 
	
	private FileConfiguration(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public static FileConfiguration read(String filePath) throws ConfigurationException {
		//read file
		String content = FileReader.read(filePath);
		if (content == null) {
			throw new ConfigurationException("unable to read from configuration file at " + filePath);
		}
		//extract and store key-value pairs
		Map<String, String> map = new HashMap<String, String>();
		String[] pairs = content.split(System.getProperty("line.separator"));
		//System.out.println(java.util.Arrays.toString(pairs));//TODO
		for (String pair : pairs) {
			String[] kv = pair.split("=");
			if (kv.length != 2) {
				throw new ConfigurationException("malformed line");
			}
			map.put(kv[0], kv[1]);
		}
		//make sure that all required keys are present
		ConfigKey[] keys = ConfigKey.values();
		for (ConfigKey key : keys) {
			String value = map.get(key.getKey());
			if (value == null) {
				throw new ConfigurationException("key " + key.getKey() + " missing in configuration file");
			}
		}
		//use values to create and return FileConfiguration object
		String url = "jdbc:postgresql://" + map.get(ConfigKey.HOSTNAME.getKey()) + ":" +
				map.get(ConfigKey.PORT.getKey()) + "/" + map.get(ConfigKey.DBNAME.getKey());
		String user = map.get(ConfigKey.USER.getKey());
		String password = map.get(ConfigKey.PASSWORD.getKey());
		return new FileConfiguration(url, user, password);
	}
	
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "url=" + url + "\nuser=" + user + "\npassword=" + password + "\n"; 
	}
	
	private enum ConfigKey {
		HOSTNAME("hostname"),
		PORT("port"),
		DBNAME("dbname"),
		USER("user"),
		PASSWORD("password");
		
		private final String key;
		
		private ConfigKey(String key) {
			this.key = key;
		}
		
		String getKey() {
			return key;
		}
	}
}
