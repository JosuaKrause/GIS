package gis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Flickr {

  public static void main(final String[] args) {
    final String path = new String("flickrData.csv");
    final Flickr flickrParser = new Flickr();
    flickrParser.connect("jdbc:postgresql://134.34.225.25/joschi_gis_db",
        "postgres", "admin");
    flickrParser.process("flickr", path, Runtime.getRuntime().availableProcessors(),
        flickrParser.getBrandenburgerTor());
    flickrParser.disconnect();
  }

  private PGgeometry getBrandenburgerTor() {
    try {
      final Statement stat = m_conn.createStatement();
      final ResultSet r = stat
          .executeQuery("select geom from buildings where name = 'Brandenburger Tor'");
      while(r.next()) {
        final PGgeometry geom = (PGgeometry) r.getObject("geom");
        if(geom != null) return geom;
      }
    } catch(final SQLException e) {
      System.err.print("error occurred: " + e.getMessage() + " \n");
    }
    return null;
  }

  BufferedReader m_br;
  Queue<String[]> m_stringQueue;
  Queue<FutureTask<Long>> m_processedQueue;
  ExecutorService m_threadPool;
  Connection m_conn = null;

  public void connect(final String url, final String user, final String password) {
    try {
      m_conn = DriverManager.getConnection(url, user, password);
      ((PGConnection) m_conn).addDataType("geometry",
          org.postgis.PGgeometry.class);
    } catch(final SQLException e) {
      System.err.print("error occurred: " + e.getMessage() + " \n");
    }
  }

  public void disconnect() {
    try {
      m_conn.close();
    } catch(final SQLException e) {
      System.err.print("error occurred: " + e.getMessage() + " \n");
    }
  }

  public void process(final String tablename, final String filename,
      final int numThreads, final PGgeometry distObject) {
    try {
      System.out.print("reading file.. ");
      m_stringQueue = new LinkedList<>();
      {
        final CSVReader reader = new CSVReader(
            new FileReader(filename), ';');
        String[] nextLine;
        while((nextLine = reader.readNext()) != null) {
          m_stringQueue.add(nextLine);
        }
      }
      {
        final CSVReader reader = new CSVReader(
            new FileReader(filename), ',');
        String[] nextLine;
        while((nextLine = reader.readNext()) != null) {
          m_stringQueue.add(nextLine);
        }
      }
      System.out.print("done\n");
    } catch(final IOException e) {// Catch exception if any
      System.err.print("error occurred: " + e.getMessage() + " \n");
      return;
    }
    System.out.println("Num rows: " + m_stringQueue.size());

    try {
      System.out.print("create table.. ");
      final Statement stat = m_conn.createStatement();
      stat.execute("DROP TABLE IF EXISTS " + tablename
          + "; CREATE TABLE " + tablename + " (" + "photoID bigint," + // 7923488532
          "photoTitle text," + // Quattro in the Sky
          "photoUrl text," + // http://www.flickr.com/photos/micha_u/7923488532/
          "photoAccur integer," + // 16
          "photoTags text," + // micha_u - Froschkönig Photos -
          // Canon EOS 60D - Canon EFS 10-22 -
          // hdr - Berlin - Brandenburger Tor
          // - Himmel - Sky - Wolken - Clouds
          // - 2012 -
          "photoTagsC integer," + // 12
          "photoComme integer," + // 13
          "photoDateP text," + // 4 Sep 2012 14:45:34 GMT
          "photoDateT text," + // 1 Sep 2012 12:17:26 GMT
          "photoDescr text," + // ""
          "photoNotes text," + // ""
          "photoNot_1 integer," + // 0
          "photoMedia text," + // NA
          "photoMed_1 text," + // NA
          "photoLicen integer," + // 0
          "photoIsFam bool," + // FALSE
          "photoIsFri bool," + // FALSE
          "photoIsPri text," + // NA
          "photoLongi double precision," + // 13,377721
          "photoLatit double precision," + // 52,51626
          "userID text," + // 56205589@N00
          "userName text," + // Froschkönig Photos
          "userRealNa text," + // ""
          "userPhotoC integer," + // 1742
          "userPhotoF text," + // 22 Jan 2007 16:34:47 GMT
          "userPhot_1 text," + // 29 Jul 2004 18:16:33 GMT
          "userLocati text," + // ""
          "userBuddyI text," + // http://farm1.static.flickr.com/129/buddyicons/56205589@N00.jpg
          "userIsPro bool," + // TRUE
          "userIsAdmi bool," + // FALSE
          "userContac integer," + // 248
          "userPhotoS integer," + // 96
          "distanceToBT double precision " +
          ");");
      stat.execute("SELECT AddGeometryColumn('','" + tablename
          + "','poly_geom','-1','POINT',2);");
      System.out.print("done\n");
    } catch(final SQLException e) {
      System.err.print("error occurred: " + e.getMessage() + " \n");
      return;
    }

    System.out.print("starting parser.. ");
    final CSVWriter writer;
    try {
      writer = new CSVWriter(new FileWriter("error.csv"), ';');
      m_threadPool = Executors.newFixedThreadPool(numThreads);
      m_processedQueue = new LinkedList<>();
      int id = 0;
      while(!m_stringQueue.isEmpty()) {
        final FutureTask<Long> task = new FutureTask<>(
            new FlickerThread(++id, m_stringQueue.poll(), m_conn,
                tablename, distObject, writer));
        m_processedQueue.add(task);
        m_threadPool.execute(task);
      }

      System.out.print("done\n");

    } catch(final IOException e) {
      System.err.print("error occurred: " + e.getMessage() + " \n");
      return;
    }

    System.out.print("please wait! saving data.. ");
    long max = Long.MIN_VALUE;
    long min = Long.MAX_VALUE;
    long sum = 0;
    long num = 0;
    try {
      while(!m_processedQueue.isEmpty()) {
        final long t = m_processedQueue.poll().get();
        sum += t;
        ++num;
        min = Math.min(min, t);
        max = Math.max(max, t);
      }
      System.out.print("done\n");
      System.out.println("Average time: " + sum / num + " ms");
      System.out.println("Min time: " + min + " ms");
      System.out.println("Max time: " + max + " ms");
      writer.close();
    } catch(final Exception e) {
      System.err.println("error occurred: " + e.getMessage() + " \n");
    } finally {
      m_threadPool.shutdown();
    }

    try (final Statement stat = m_conn.createStatement()) {
      stat.execute("UPDATE " + tablename
          + " set poly_geom = ST_SetSRID(poly_geom, 4326);");
    } catch(final SQLException e) {
      e.printStackTrace();
    }
  }

  private static class FlickerThread implements Callable<Long> {
    String[] str;
    Connection con;
    String tablename;
    int id;
    PGgeometry distanceObject;
    final CSVWriter writer;

    public FlickerThread(final int id, final String[] str,
        final Connection con, final String tablename,
        final PGgeometry distanceObject, final CSVWriter writer) {
      this.str = str;
      this.con = con;
      this.tablename = tablename;
      this.id = id;
      this.distanceObject = distanceObject;
      this.writer = writer;
    }

    @Override
    public Long call() {
      final long start = System.nanoTime();

      try {
        if(str.length == 33 && exists(str[3])) {
          final String imagepath = getImageUrl(str[3]).replace(
              "_m.jpg", "_c.jpg");
          if(exists(imagepath)) {
            str[3] = "img:" + imagepath;
            // getImage(imagepath, str[3]);
          } else {
            str[3] = "url:" + str[3];
          }
          str[19] = str[19].replace(",", ".");
          str[20] = str[20].replace(",", ".");
          final Statement stat = con.createStatement();
          String query = "INSERT INTO " + tablename + " VALUES (";
          for(int i = 1; i < str.length; ++i) {
            query += "'" + str[i] + "', ";
          }
          query += "ST_Distance(ST_GeomFromText('"
              + distanceObject.toString()
              + "',-1), ST_GeomFromText('POINT(" + str[19] + " "
              + str[20] + ")', -1),TRUE),";
          query += "ST_GeomFromText('POINT(" + str[19] + " "
              + str[20] + ")')";
          query += ");";
          stat.execute(query);
        }
      } catch(final Exception e) {
        System.err.print("error occurred: " + e.getMessage() + " \n");
        synchronized(writer) {
          writer.writeNext(str);
          try {
            writer.flush();
          } catch(final IOException e1) {
            System.out.println("..Cannot write");
          }
        }
      }
      return System.nanoTime() - start;
    }

    public boolean exists(final String url)
        throws MalformedURLException, IOException {
      HttpURLConnection.setFollowRedirects(false);
      final HttpURLConnection con = (HttpURLConnection) new URL(url)
          .openConnection();
      con.setRequestMethod("HEAD");
      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    }

    public String getImageUrl(final String siteurl) throws IOException {
      final URL url = new URL(siteurl);
      final InputStreamReader in = new InputStreamReader(url.openStream());
      final BufferedReader br = new BufferedReader(in);
      final Pattern p = Pattern
          .compile(".*<link rel=\"image_src\" href=\"(http:.*)\" id=\"image-src\">.*");

      String line;
      while((line = br.readLine()) != null) {
        final Matcher m = p.matcher(line);
        if(m.find()) return m.group(1);
      }

      in.close();
      return null;
    }

    public void getImage(final String imageurl, final String imagepath)
        throws IOException {
      final URL url = new URL(imageurl);
      final InputStream in = new BufferedInputStream(url.openStream());
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final byte[] buf = new byte[1024];
      int n = 0;
      while((n = in.read(buf)) != -1) {
        out.write(buf, 0, n);
      }
      out.close();
      in.close();
      final FileOutputStream fos = new FileOutputStream(imagepath);
      fos.write(out.toByteArray());
      fos.close();
    }

  } // FlickerThread

}
