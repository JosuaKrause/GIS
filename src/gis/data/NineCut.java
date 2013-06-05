package gis.data;

public enum NineCut {
  DISJOINT("%1$s and %2$s are disjoint"),
  MEET("%1$s and %2$s meet"),
  COVERS("%1$s covers %2$s"),
  OVERLAPS("%1$s and %2$s overlap"),
  CONTAINS("%1$s contains %2$s"),
  EQUAL("%1$s and %2$s are equal"),
  COVERED_BY("%1$s is covered by %2$s"),
  INSIDE("%1$s is inside of %2$s");

  private final String format;
  private boolean[] matrix;// TODO

  private NineCut(final String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public static NineCut get(final boolean disjoint, final boolean meet,
      final boolean covers, final boolean overlaps,
      final boolean contains, final boolean equal, final boolean coveredBy,
      final boolean inside) {
    if(inside) return INSIDE;
    if(coveredBy) return COVERED_BY;
    if(equal) return EQUAL;
    if(contains) return CONTAINS;
    if(overlaps) return OVERLAPS;
    if(covers) return COVERS;
    if(meet) return MEET;
    if(disjoint) return DISJOINT;
    throw new AssertionError();
  }

  public boolean get(final int i, final int j) {
    final int index = (i - 1) * 3 + (j - 1);
    return matrix[index];
  }

}
