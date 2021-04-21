import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// https://stackoverflow.com/questions/18686199/fill-unicode-characters-in-labels/18686753#18686753
class ChessBoard {

    /**
     * Unicodes for chess pieces.
     */
    static final String[] pieces = { "\u2654", "\u2655", "\u2656", "\u2657", "\u2658", "\u2659", "\u0000" };
    static final int KING = 0, QUEEN = 1, CASTLE = 2, BISHOP = 3, KNIGHT = 4, PAWN = 5, EMPTY = 6;

    // will take in enum of chess pieces at some point
    public static final int[] order = new int[] { CASTLE, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, CASTLE };

    /*
     * Colors..
     */
    public static final Color outlineColor = Color.DARK_GRAY;
    public static final Color[] pieceColors = { new Color(203, 203, 197), new Color(192, 142, 60) };
    static final int WHITE = 0, BLACK = 1;

    /*
     * Font. The images use the font sizeXsize.
     */
    static Font font = new Font("Sans-Serif", Font.PLAIN, 64);

    public static ArrayList<Shape> separateShapeIntoRegions(Shape shape) {
        ArrayList<Shape> regions = new ArrayList<Shape>();

        PathIterator pi = shape.getPathIterator(null);
        int ii = 0;
        GeneralPath gp = new GeneralPath();
        while (!pi.isDone()) {
            double[] coords = new double[6];
            int pathSegmentType = pi.currentSegment(coords);
            int windingRule = pi.getWindingRule();
            gp.setWindingRule(windingRule);
            if (pathSegmentType == PathIterator.SEG_MOVETO) {
                gp = new GeneralPath();
                gp.setWindingRule(windingRule);
                gp.moveTo(coords[0], coords[1]);
            } else if (pathSegmentType == PathIterator.SEG_LINETO) {
                gp.lineTo(coords[0], coords[1]);
            } else if (pathSegmentType == PathIterator.SEG_QUADTO) {
                gp.quadTo(coords[0], coords[1], coords[2], coords[3]);
            } else if (pathSegmentType == PathIterator.SEG_CUBICTO) {
                gp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
            } else if (pathSegmentType == PathIterator.SEG_CLOSE) {
                gp.closePath();
                regions.add(new Area(gp));
            } else {
                System.err.println("Unexpected value! " + pathSegmentType);
            }

            pi.next();
        }

        return regions;
    }

    public static BufferedImage getImageForChessPiece(int piece, int side, boolean gradient) {
        int sz = font.getSize();
        BufferedImage bi = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, pieces[piece]);
        Rectangle2D box1 = gv.getVisualBounds();

        Shape shape1 = gv.getOutline();
        Rectangle r = shape1.getBounds();
        int spaceX = sz - r.width;
        int spaceY = sz - r.height;
        AffineTransform trans = AffineTransform.getTranslateInstance(-r.x + (spaceX / 2), -r.y + (spaceY / 2));

        Shape shapeCentered = trans.createTransformedShape(shape1);

        Shape imageShape = new Rectangle2D.Double(0, 0, sz, sz);
        Area imageShapeArea = new Area(imageShape);
        Area shapeArea = new Area(shapeCentered);
        imageShapeArea.subtract(shapeArea);
        ArrayList<Shape> regions = separateShapeIntoRegions(imageShapeArea);
        g.setStroke(new BasicStroke(1));
        g.setColor(pieceColors[side]);
        Color baseColor = pieceColors[side];
        if (gradient) {
            Color c1 = baseColor.brighter();
            Color c2 = baseColor;
            GradientPaint gp = new GradientPaint(sz / 2 - (r.width / 4), sz / 2 - (r.height / 4), c1,
                    sz / 2 + (r.width / 4), sz / 2 + (r.height / 4), c2, false);
            g.setPaint(gp);
        } else {
            g.setColor(baseColor);
        }

        for (Shape region : regions) {
            Rectangle r1 = region.getBounds();
            if (r1.getX() < 0.001 && r1.getY() < 0.001) {
            } else {
                g.fill(region);
            }
        }
        g.setColor(outlineColor);
        g.fill(shapeArea);
        g.dispose();

        return bi;
    }

    public static void addColoredUnicodeCharToContainer(Container c, int piece, int side, Color bg, boolean gradient) {
        JLabel l = new JLabel(new ImageIcon(getImageForChessPiece(piece, side, gradient)), JLabel.CENTER);
        l.setBackground(bg);
        l.setOpaque(true);
        c.add(l);
    }

    public static void addPiecesToContainer(Container c, int intialSquareColor, int side, int[] pieces,
            boolean gradient) {

        for (int piece : pieces) {
            addColoredUnicodeCharToContainer(c, piece, side,
                    intialSquareColor++ % 2 == BLACK ? Color.BLACK : Color.WHITE, gradient);
        }
    }

    public static void addPiecesToContainer(Container c, Color bg, int side, int[] pieces, boolean gradient) {
        for (int piece : pieces) {
            addColoredUnicodeCharToContainer(c, piece, side, bg, gradient);
        }
    }

    // testing this class
    public void swap(JPanel gui) {
        final int[] pawnRow = new int[] { PAWN, PAWN, PAWN, PAWN, EMPTY, PAWN, PAWN, PAWN };
        final int[] pawnRow2 = new int[] { EMPTY, EMPTY, EMPTY, EMPTY, PAWN, EMPTY, EMPTY, EMPTY };
        final int[] emptyRow = new int[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY };
        boolean gradientFill = true;

        gui.removeAll();
        addPiecesToContainer(gui, BLACK, WHITE, order, gradientFill);
        addPiecesToContainer(gui, WHITE, WHITE, pawnRow, gradientFill);
        addPiecesToContainer(gui, BLACK, WHITE, pawnRow2, gradientFill);
        addPiecesToContainer(gui, WHITE, BLACK, emptyRow, gradientFill);
        addPiecesToContainer(gui, BLACK, BLACK, emptyRow, gradientFill);
        addPiecesToContainer(gui, WHITE, BLACK, pawnRow2, gradientFill);
        addPiecesToContainer(gui, BLACK, BLACK, pawnRow, gradientFill);
        addPiecesToContainer(gui, WHITE, BLACK, order, gradientFill);
        gui.revalidate();
        gui.repaint();

    }

    public ChessBoard(JPanel gui) {
        final int[] pawnRow = new int[] { PAWN, PAWN, PAWN, PAWN, EMPTY, PAWN, PAWN, PAWN };
        final int[] pawnRow2 = new int[] { EMPTY, EMPTY, EMPTY, EMPTY, PAWN, EMPTY, EMPTY, EMPTY };
        final int[] emptyRow = new int[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY };

        boolean gradientFill = true;

        // enables program to run later - enables us to change gui dynamically at a
        // later point after initialization
        Runnable r = new Runnable() {
            @Override
            public void run() {
                gui.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY,
                        Color.GRAY.darker(), Color.GRAY));
                addPiecesToContainer(gui, WHITE, BLACK, order, gradientFill);
                addPiecesToContainer(gui, BLACK, BLACK, pawnRow, gradientFill);
                addPiecesToContainer(gui, WHITE, BLACK, pawnRow2, gradientFill);
                addPiecesToContainer(gui, BLACK, BLACK, emptyRow, gradientFill);
                addPiecesToContainer(gui, WHITE, BLACK, emptyRow, gradientFill);
                addPiecesToContainer(gui, BLACK, WHITE, pawnRow2, gradientFill);
                addPiecesToContainer(gui, WHITE, WHITE, pawnRow, gradientFill);
                addPiecesToContainer(gui, BLACK, WHITE, order, gradientFill);
                JOptionPane.showOptionDialog(null, gui, "ChessBoard", JOptionPane.NO_OPTION, JOptionPane.NO_OPTION,
                        null, new Object[] {}, null);
            }
        };

        SwingUtilities.invokeLater(r);
    }
}