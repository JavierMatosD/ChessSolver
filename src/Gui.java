import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// https://stackoverflow.com/questions/18686199/fill-unicode-characters-in-labels/18686753#18686753
class Gui extends JFrame {
    // unicodes for chess pieces
    static final String[] pieces = { "\u2654", "\u2655", "\u2656", "\u2657", "\u2658", "\u2659", "\u0000" };
    // mapping from piece to unicode
    public Map<String, Integer> pieceMapping = new HashMap<>();

    // colors
    public final Color outlineColor = Color.DARK_GRAY;
    public final Color[] pieceColors = { new Color(203, 203, 197), new Color(192, 142, 60) };
    static final int WHITE = 0, BLACK = 1;

    // font
    static Font font = new Font("Sans-Serif", Font.PLAIN, 64);

    // boardPanel
    JPanel boardPanel;
    // keeps all the moves made
    DefaultListModel<String> movesModel;

    // state of the board - to enable fast movement instead of repainting gui
    ChessPiece[] state = new ChessPiece[72];

    // turn
    String turn;

    // labels
    String[] COLS = "ABCDEFGH".split("");

    // LinkedList of moves
    LinkedList<Move> moves = new LinkedList<>();
    int currentMove = 0;

    // next turn button
    JButton next = new JButton("NEXT MOVE");

    private LinkedList<Shape> separateShapeIntoRegions(Shape shape) {
        LinkedList<Shape> regions = new LinkedList<Shape>();

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
        LinkedList<Shape> regions = separateShapeIntoRegions(imageShapeArea);
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
        // this.gui.add(l);
        this.boardPanel.add(l, index);
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

    public Gui(ChessPiece[][] chessPieces, String turn) {
        // initialize mapping from piece to unicode
        pieceMapping.put("KING", 0);
        pieceMapping.put("QUEEN", 1);
        pieceMapping.put("ROOK", 2);
        pieceMapping.put("BISHOP", 3);
        pieceMapping.put("KNIGHT", 4);
        pieceMapping.put("PAWN", 5);
        pieceMapping.put("EMPTY", 6);

        // GUI will hold two panels
        // 1. Board 2. Moves
        JPanel gui = new JPanel();
        gui.setLayout(new GridLayout(1, 2));

        // 1. boardPanel
        JPanel boardPanel = new JPanel(new GridLayout(10, 9));
        this.boardPanel = boardPanel;
        this.turn = turn;

        // 2. movesPanel
        // keep track of moves
        JScrollPane movesPane = new JScrollPane();
        // dynamic list
        this.movesModel = new DefaultListModel<String>();
        JList<String> movesList = new JList<String>(this.movesModel);
        // add the moves panel
        // Initialize the list with items
        movesModel.addElement("MOVES");
        movesModel.addElement(" ");
        movesPane.setViewportView(movesList);

        // enables program to run later - enables us to change gui dynamically at a
        // later point after initialization
        Runnable r = new Runnable() {
            @Override
            public void run() {
                boardPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY,
                        Color.GRAY.darker(), Color.GRAY));
                // map to the static indices defined in the class
                boolean gradientFill = true;

                for (int i = 0; i < 9; i++) {
                    if (i == 8) {
                        // add the letters row
                        for (int ii = 0; ii < 8; ii++) {
                            boardPanel.add(new JLabel(COLS[ii], SwingConstants.CENTER));
                        }
                        continue;
                    }

                    for (int j = 0; j < 9; j++) {
                        if (j == 8) {
                            // add the numbers column
                            boardPanel.add(new JLabel(String.valueOf(9 - (i + 1)), SwingConstants.CENTER));
                            continue;
                        }

                        ChessPiece piece = chessPieces[i][j];

                        // store it in the state variable for future tracking
                        int index = 9 * i + j;
                        state[index] = piece;
                        // System.out.println(state[index]);
                        // determine name
                        String pieceName = piece.getMyType().toString();
                        int pieceNumber = pieceMapping.get(pieceName);

                        // determine background color of board
                        Color bg = determineBackground(i, j);

                        // determine side of piece
                        int sideNumber = 0;

                        if (!piece.isWhite())
                            sideNumber = 1;

                        // draw
                        addColoredUnicodeCharToContainer(pieceNumber, index, sideNumber, bg, gradientFill);
                    }
                }

                // some padding and button
                boardPanel.add(new JLabel(" ", SwingConstants.CENTER));
                boardPanel.add(new JLabel(" ", SwingConstants.CENTER));

                next.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // execute the next move on the board
                        executeNextMove();
                    }
                });
                next.setMargin(new Insets(0, 0, 0, 0));
                boardPanel.add(next);
                boardPanel.add(new JLabel(" ", SwingConstants.CENTER));

                JLabel turnLabel = new JLabel("TURN: ", SwingConstants.CENTER);
                turnLabel.setFont(new Font("Serif", Font.BOLD, 16));
                boardPanel.add(turnLabel);

                JLabel label = new JLabel(turn, SwingConstants.CENTER);
                label.setFont(new Font("Serif", Font.BOLD, 16));
                boardPanel.add(label, 85);

                // add the two componenets to the GUI
                gui.add(boardPanel);
                gui.add(movesPane);

                JOptionPane.showOptionDialog(null, gui, "ChessBoard", JOptionPane.NO_OPTION, JOptionPane.NO_OPTION,
                        null, new Object[] {}, null);
            }
        };

        SwingUtilities.invokeLater(r);
    }

    public void executeNextMove() {
        if (currentMove == moves.size()) {
            if (currentMove > 0) {
                // checkmate since moves are done
                this.movesModel.addElement("CHECKMATE");
                next.setText("OVER");
                next.setEnabled(false);
                next.paintImmediately(next.getVisibleRect());
                System.out.println("No more moves available");
                currentMove += 1;
                return;
            } else {
                // we are still calculating moves
                next.setText("WAITING...");
                next.setEnabled(false);
                next.paintImmediately(next.getVisibleRect());

                while (moves.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                next.setText("NEXT MOVE");
                next.setEnabled(true);
                next.paintImmediately(next.getVisibleRect());
            }
        }

        if (currentMove > moves.size()) {
            System.out.println("No more moves available");
            return;
        }

        Move move = this.moves.get(currentMove);
        int[] from = new int[] { move.x_start, move.y_start };
        int[] to = new int[] { move.x_end, move.y_end };
        move(from, to);

        currentMove += 1;
    }

    // move a piece
    public void move(int[] from, int[] to) {
        // converting them to jpanel coordinates
        // to find its position in the JPanel "Array"
        int f = 9 * from[0] + from[1];
        int t = 9 * to[0] + to[1];

        // get the pieces from state
        ChessPiece fromPiece = state[f];

        // determine name of piece we are moving
        // if there is a piece where we are moving
        // it wouldn't matter - we are going to capture it
        String pieceName = fromPiece.getMyType().toString();
        int pieceNumber = pieceMapping.get(pieceName);

        // determine background color of board on both pieces
        Color from_bg = determineBackground(from[0], from[1]);
        Color to_bg = determineBackground(to[0], to[1]);

        // determine side of piece we are moving
        // if there is a piece where we are moving
        // it wouldn't matter - we are going to capture it
        int sideNumber = 0;

        if (!fromPiece.isWhite())
            sideNumber = 1;

        // replace from with empty box
        boardPanel.remove(f);
        addColoredUnicodeCharToContainer(6, f, 0, from_bg, true);

        // replace to with from piece
        boardPanel.remove(t);
        addColoredUnicodeCharToContainer(pieceNumber, t, sideNumber, to_bg, true);

        // change state to reflect these changes
        state[f] = new ChessPiece();
        state[t] = fromPiece;

        // add to movesList
        this.movesModel.addElement(
                turn + " " + pieceName + " -> " + COLS[from[1]] + (8 - from[0]) + " : " + COLS[to[1]] + (8 - to[0]));

        // change turn - test
        turn = turn == "WHITE" ? "BLACK" : "WHITE";
        boardPanel.remove(85);
        JLabel label = new JLabel(turn, SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 16));
        boardPanel.add(label, 85);

        // repaint boardPanel only
        boardPanel.revalidate();
        boardPanel.repaint();
    }
}