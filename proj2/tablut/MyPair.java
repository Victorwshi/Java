package tablut;

/** A pair to record past actions.
 *  @author Victor Shi
 */
public class MyPair {
    /** The piece that records history. */
    private final Piece _piece0;
    /** The piece that records history. */
    private final Piece _piece1;
    /** The square that records history. */
    private final Square sq;

    /** A pair structure that stores PIECE1, SQ0, PIECE0
     * to record history for undoing. */
    public MyPair(Piece piece1, Square sq0, Piece piece0) {
        _piece0 = piece0;
        _piece1 = piece1;
        sq = sq0;
    }
    /** To return piece0. */
    public Piece getPiece0() {
        return _piece0;
    }
    /** To return piece1. */
    public Piece getPiece1() {
        return _piece1;
    }
    /** To return sq. */
    public Square getSq() {
        return sq;
    }
}
