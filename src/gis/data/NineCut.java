package gis.data;

public enum NineCut {
  DISJOINT("%1$s and %2$s are disjoint", "[[F,F,T],[F,F,T],[T,T,T]]", "outside"),
  MEET("%1$s and %2$s meet", "[[T,F,T],[F,F,T],[T,T,T]]", "on the border"),
  COVERS("%1$s covers %2$s", "[[T,T,T],[T,T,T],[F,F,T]]", "inside"),
  OVERLAPS("%1$s and %2$s overlap", "[[T,T,T],[T,T,T],[T,T,T]]", "ERROR_OVERLAP"),
  CONTAINS("%1$s contains %2$s", "[[F,F,T],[T,T,T],[F,F,T]]", "inside"),
  EQUAL("%1$s and %2$s are equal", "[[T,F,F],[F,T,F],[F,F,T]]", "ERROR_EQUAL"),
  COVERED_BY("%1$s is covered by %2$s", "[[T,T,F],[T,T,F],[T,T,T]]", "inside"),
  INSIDE("%1$s is inside of %2$s", "[[F,T,F],[F,T,F],[T,T,T]]", "inside");

  private final String format;
  private String matrix;
  private final String pointPoly;

  private NineCut(final String format, final String matrix, final String pointPoly) {
    this.format = format;
    this.matrix = matrix;
    this.pointPoly = pointPoly;
  }

  public String getFormat() {
    return format;
  }

  public String getMatrix() {
    return matrix;
  }

  public String getPointPolyFormat() {
    return "%1$s and %2$s relation: " + pointPoly;
  }

  public static NineCut get(final boolean disjoint, final boolean meet,
      final boolean covers, final boolean overlaps,
      final boolean contains, final boolean equal, final boolean coveredBy,
      final boolean inside) {
    if(equal) return EQUAL;
    if(coveredBy) return COVERED_BY;
    if(contains) return CONTAINS;
    if(inside) return INSIDE;
    if(overlaps) return OVERLAPS;
    if(covers) return COVERS;
    if(meet) return MEET;
    if(disjoint) return DISJOINT;
    throw new AssertionError();
  }

}
