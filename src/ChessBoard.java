
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// https://stackoverflow.com/questions/18686199/fill-unicode-characters-in-labels/18686753#18686753
class ChessBoard {
    // unicodes for chess pieces
    static final String[] pieces = { "\u2654", "\u2655", "\u2656", "\u2657", "\u2658", "\u2659", "\u0000" };
    // mapping from piece to unicode
    static final int KING = 0, QUEEN = 1, ROOK = 2, BISHOP = 3, KNIGHT = 4, PAWN = 5, EMPTY = 6;

    // colors
    public final Color outlineColor = Color.DARK_GRAY;
    public final Color[] pieceColors = { new Color(203, 203, 197), new Color(192, 142, 60) };
    static final int WHITE = 0, BLACK = 1;

    // font
    static Font font = new Font("Sans-Serif", Font.PLAIN, 64);

    // gui
    JPanel gui;

    // state of the board - to enable fast movement instead of repainting gui
    ChessPiece[] state = new ChessPiece[64];

    private ArrayList<Shape> separateShapeIntoRegions(Shape shape) {
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

    private BufferedImage getImageForChessPiece(int piece, int side, boolean gradient) {
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

    // add item to JPanel "Array"
    private void addColoredUnicodeCharToContainer(int piece, int index, int side, Color bg, boolean gradient) {
        JLabel l = new JLabel(new ImageIcon(getImageForChessPiece(piece, side, gradient)), JLabel.CENTER);
        l.setBackground(bg);
        l.setOpaque(true);
        this.gui.add(l, index);
    }

    // mapping from piece to unicode
    private int determineNumber(String pieceName) {
        switch (pieceName) {
        case "KING":
            return 0;
        case "QUEEN":
            return 1;
        case "ROOK":
            return 2;
        case "BISHOP":
            return 3;
        case "KNIGHT":
            return 4;
        case "PAWN":
            return 5;
        case "EMPTY": // empty
            return 6;
        }
        return -1;
    }

    // determine the background on the board
    private Color determineBackground(int i, int j) {
        if (i % 2 != 0) {
            if (j % 2 == 0) {
                return Color.WHITE;
            } else {
                return Color.BLACK;
            }
        } else {
            if (j % 2 == 0) {
                return Color.BLACK;
            } else {
                return Color.WHITE;
            }
        }
    }

    public ChessBoard(ChessPiece[][] chessPieces) {
        JPanel gui = new JPanel(new GridLayout(0, 8, 0, 0));
        this.gui = gui;

        // enables program to run later - enables us to change gui dynamically at a
        // later point after initialization
        Runnable r = new Runnable() {
            @Override
            public void run() {
                gui.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY,
                        Color.GRAY.darker(), Color.GRAY));
                // map to the static indices defined in the class
                boolean gradientFill = true;

                for (int i = 0; i < chessPieces.length; i++) {
                    for (int j = 0; j < chessPieces[0].length; j++) {
                        ChessPiece piece = chessPieces[i][j];

                        // store it in the state variable for future tracking
                        int index = 8 * i + j;
                        state[index] = piece;
                        System.out.println(state[index]);
                        // determine name
                        String pieceName = piece.getMyType().toString();
                        int pieceNumber = determineNumber(pieceName);

                        // determine background color of board
                        Color bg = determineBackground(i, j);

                        // determine side of piece
                        char side = 'B';
                        if(piece.isWhite())
                            side = 'W';
                        int sideNumber = 0;

                        if (side == 'B') {
                            sideNumber = 1;
                        }

                        // draw
                        addColoredUnicodeCharToContainer(pieceNumber, index, sideNumber, bg, gradientFill);
                    }
                }

                JOptionPane.showOptionDialog(null, gui, "ChessBoard", JOptionPane.NO_OPTION, JOptionPane.NO_OPTION,
                        null, new Object[] {}, null);
            }
        };

        SwingUtilities.invokeLater(r);
    }

    // move a piece
    public void move(int[] from, int[] to) {
        // converting them to jpanel coordinates
        // to find its position in the JPanel "Array"
        int f = 8 * from[0] + from[1];
        int t = 8 * to[0] + to[1];

        // get the pieces from state
        ChessPiece fromPiece = state[f];
        ChessPiece toPiece = state[t];

        // determine name of piece we are moving
        // if there is a piece where we are moving
        // it wouldn't matter - we are going to capture it
        String pieceName = fromPiece.getMyType().toString();
        int pieceNumber = determineNumber(pieceName);

        // determine background color of board on both pieces
        Color from_bg = determineBackground(from[0], from[1]);
        Color to_bg = determineBackground(to[0], to[1]);

        // determine side of piece we are moving
        // if there is a piece where we are moving
        // it wouldn't matter - we are going to capture it
        char side = 'B';
        if(fromPiece.isWhite())
            side = 'W';

        int sideNumber = 0; //some of this can be cut down...

        if (side == 'B') {
            sideNumber = 1;
        }

        // replace from with empty box
        gui.remove(f);
        addColoredUnicodeCharToContainer(6, f, 0, from_bg, true);

        // replace to with from piece
        gui.remove(t);
        addColoredUnicodeCharToContainer(pieceNumber, t, sideNumber, to_bg, true);

        // change state to reflect these changes
        state[f] = new ChessPiece();
        state[t] = fromPiece;

        // repaint gui
        gui.revalidate();
        gui.repaint();
    }
}