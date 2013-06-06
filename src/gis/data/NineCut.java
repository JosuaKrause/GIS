package gis.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

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
  private final String matrix;
  private final boolean[] booleanMatrix;
  private final String pointPoly;

  private NineCut(final String format, final String matrix, final String pointPoly) {
    this.format = format;
    this.matrix = matrix;
    this.pointPoly = pointPoly;
    booleanMatrix = convertStringMatrix(matrix);
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

  private static boolean[] convertStringMatrix(final String matrix) {
    final boolean[] m = new boolean[9];
    int mIndex = 0;
    for(int i = 0; i < matrix.length(); ++i) {
      final char c = matrix.charAt(i);
      if(c == 'T') {
        m[mIndex++] = true;
      } else if(c == 'F') {
        m[mIndex++] = false;
      }
    }
    if(mIndex != 9) throw new AssertionError();
    return m;
  }

  private static final int CELL_WIDTH = 4;
  private static final int CELL_HEIGHT = 3;
  private static final int CELL_PADDING = 1;
  private static final Dimension DIMENSION = new Dimension(3 * CELL_WIDTH + 2
      * CELL_PADDING,
      3 * CELL_HEIGHT + 2 * CELL_PADDING);

  public void paint(final Graphics2D g, final int x, final int y) {
    for(int i = 0; i < 3; ++i) {
      for(int j = 0; j < 3; ++j) {
        final boolean b = booleanMatrix[i * 3 + j];
        final int cellX = x + i * (CELL_WIDTH + CELL_PADDING);
        final int cellY = y + j * (CELL_HEIGHT + CELL_PADDING);
        if(b) {
          g.setColor(Color.WHITE);
        } else {
          g.setColor(Color.BLACK);
        }
        g.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
      }
    }
  }

  public static final Dimension getDimension() {
    return DIMENSION;
  }
}
