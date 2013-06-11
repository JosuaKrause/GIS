package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.awt.Color;
import java.util.List;

public class ParksNearWaterQueryCheckBox extends QueryCheckBox {

  private static final long serialVersionUID = -7695687735225875092L;
  private static final double DEFAULT_DISTANCE_THRESHOLD_IN_METERS = 50;
  private final Query q;

  // -- Table: park
  //
  // -- DROP TABLE park;
  //
  // CREATE TABLE park
  // (
  // gid serial NOT NULL,
  // name character varying(48),
  // geom geometry(Point),
  // CONSTRAINT parks_pkey PRIMARY KEY (gid)
  // )
  // WITH (
  // OIDS=FALSE
  // );
  // ALTER TABLE park
  // OWNER TO postgres;
  //
  // -- Index: park_geom_gist
  //
  // -- DROP INDEX park_geom_gist;
  //
  // CREATE INDEX park_geom_gist
  // ON park
  // USING gist
  // (geom);

  // insert into park (name, water_dist, geom)
  // select p.name as name, min(st_distance(w.geom, p.geom, true)) as
  // water_dist, p.geom as geom
  // from berlin_water as w, (
  // select * from berlin_poi
  // where category = 'Leisure'
  // and (((name like 'Park%' or name like '%park%') and not name like
  // 'Parking%') or name like '%arten%')
  // and not name like '%layground%' and not name like 'Theme park%' and not
  // name like 'Theater%' and not name = 'Common:Gartenamt'
  // ) as p
  // group by p.gid, p.name, p.geom;

  public ParksNearWaterQueryCheckBox(final GisPanel gisPanel) {
    this(
        gisPanel,
        new Query(
            "select gid, name, water_dist, geom from park"
            , Table.PARK, "Parks near Water", "water_dist") {

          @Override
          protected void finishLoading(final List<GeoMarker> ms) {
            final double threshold = GisFrame.getInstance().getGisPanel().getThresholdDistanceInMeters();
            for(final GeoMarker m : ms) {
              if(m.getQueryValue() < threshold) {
                m.setColor(new Color(90, 180, 172));
              } else {
                m.setColor(new Color(216, 179, 101));
              }
              m.setFixedSize(true);
              m.setRadius(8);
              m.setOutlineColor(Color.BLACK);
              m.setAlphaNotSelected(0.9f);
              m.setAlphaSelected(0.6f);
            }
          }

        });
  }

  private ParksNearWaterQueryCheckBox(final GisPanel gisPanel, final Query q) {
    super(gisPanel, q);
    this.q = q;
  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    if(isSelected()) {
      GisFrame.getInstance().getGisPanel().openDistanceThresholdSelector(
          q, DEFAULT_DISTANCE_THRESHOLD_IN_METERS);
    } else {
      GisFrame.getInstance().getGisPanel().closeDistanceThresholdSelector();
    }
  }

}
