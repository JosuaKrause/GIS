package gis.data.db.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileReader {
	
	/**
	 * Returns the content of a file as a String.
	 * @param filePath the path to the file
	 * @return file content or <code>null</code> if there was an error.
	 */
	public static String read(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || !file.canRead()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			StringBuilder sb = new StringBuilder();
			byte[] buffer = new byte[4096];
			int bytesRead;
			while (true) {
				bytesRead = fis.read(buffer, 0, buffer.length);
				if (bytesRead <= 0) {
					break;
				}
				sb.append(new String(buffer, 0, bytesRead));
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
}
