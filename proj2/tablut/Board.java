package tablut;

import java.util.HashSet;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Formatter;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Victor Shi
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        for (int i = 0; i < this._board.length; i += 1) {
            for (int j = 0; j < this._board[0].length; j += 1) {
                this._board[i][j] = model._board[i][j];
            }
        }
        this._moveCount = model.moveCount();
        this._turn = model._turn;
        this._winner = model.winner();
    }

    /** Clears the board to the initial position. */
    void init() {
        positionCount = new HashSet<String>();
        pastPositions = new Stack<MyPair>();
        _winner = null;
        _turn = BLACK;
        _board = new Piece[SIZE][SIZE];
        _board[THRONE.row()][THRONE.col()] = KING;
        _theKing = _board[THRONE.row()][THRONE.col()];
        _moveCount = 0;
        _repeated = false;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                boolean checker = true;
                for (Square x : INITIAL_ATTACKERS) {
                    if (x.row() == i && x.col() == j) {
                        _board[i][j] = BLACK;
                        checker = false;
                        break;
                    }
                }
                if (checker) {
                    for (Square y : INITIAL_DEFENDERS) {
                        if (y.row() == i && y.col() == j) {
                            _board[i][j] = WHITE;
                            checker = false;
                            break;
                        }
                    }
                }
                if (checker && _board[i][j] != KING) {
                    _board[i][j] = EMPTY;
                }
            }
        }
    }

    /** Set the move limit to N.  It is an error if 2*N <= moveCount(). */
    void setMoveLimit(int n) {
        _moveLim = n;
        if (2 * _moveLim <= moveCount()) {
            throw new Error("Wrong limit");
        }
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (positionCount.contains(encodedBoard())) {
            _repeated = true;
            if (_turn == BLACK) {
                _winner = WHITE;
            } else if (_turn == WHITE) {
                _winner = BLACK;
            }
        } else {
            positionCount.add(encodedBoard());
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int x = 0; x < _board.length; x++) {
            for (int y = 0; y < _board[0].length; y++) {
                if (_board[x][y] == _theKing) {
                    return sq(y, x);
                }
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[row][col];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(row - '1', col - 'a');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.row()][s.col()] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        Piece p0 = _board[s.row()][s.col()];
        _board[s.row()][s.col()] = p;
        MyPair newPair = new MyPair(p, s, p0);
        pastPositions.push(newPair);
    }


    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (from.row() != to.row() && from.col() != to.col()) {
            return false;
        } else if (from.row() == to.row()) {
            for (int i = 1; i <= Math.abs(from.col() - to.col()); i++) {
                int bigger;
                if (from.col() < to.col()) {
                    bigger = from.col() + i;
                } else {
                    bigger = from.col() - i;
                }
                if (_board[from.row()][bigger] != EMPTY) {
                    return false;
                }
            }
            return true;
        } else if (from.col() == to.col()) {
            for (int i = 1; i <= Math.abs(from.row() - to.row()); i++) {
                int smaller;
                if (from.row() < to.row()) {
                    smaller = from.row() + i;
                } else {
                    smaller = from.row() - i;
                }
                if (_board[smaller][from.col()] != EMPTY) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        if (get(from) == KING && _turn == WHITE) {
            return true;
        }
        return get(from) == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (from == to) {
            return false;
        }
        if (!isUnblockedMove(from, to)
                || !isLegal(from) || get(to) != EMPTY) {
            return false;
        }
        if (to == THRONE && get(from) != KING) {
            return false;
        }
        if (from.row() != to.row() && from.col() != to.col()) {
            return false;
        }
        return true;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        Piece enemy = _turn.opponent();
        if (!hasMove(_turn)) {
            _winner = enemy;
        }
        if (isLegal(from) && isLegal(from, to)) {
            revPut(_board[from.row()][from.col()], to);
            revPut(EMPTY, from);
        }
        if (_turn == WHITE) {
            captureHelpWhite(to);
        } else if (_turn == BLACK) {
            captureHelpBlack(to);
        }
        if (_turn == BLACK) {
            if (get(THRONE) == KING
                    || get(THRONE.col() + 1, THRONE.row()) == KING
                    || get(THRONE.col() - 1, THRONE.row()) == KING
                    || get(THRONE.col(), THRONE.row() + 1) == KING
                    || get(THRONE.col(), THRONE.row() - 1) == KING) {
                kingCapture();
                throneCapture();
            } else {
                kingCaptureHelp(to);
            }
        }
        throneSideCap();
        checkRepeated();
        if (repeatedPosition()) {
            _winner = enemy;
        } else if (_turn == WHITE && kingChecker()) {
            _winner = WHITE;
        }

        if (legalMoves(_turn) == null) {
            _winner = enemy;
        }
        _moveCount++;
        _turn = enemy;

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }
    /** Return checks if the King reaches the edge. */
    boolean kingChecker() {
        if (kingPosition() == null) {
            return false;
        }
        return kingPosition().col() == 0 || kingPosition().row() == 0
                || kingPosition().col() == SIZE - 1
                || kingPosition().row() == SIZE - 1;
    }
    /** Capture all potential pieces when piece is moved to TO
     * assuming the turn is white. */
    void captureHelpWhite(Square to) {
        if (to.col() + 2 < SIZE
                && (_board[to.row()][to.col() + 2] == WHITE
                || _board[to.row()][to.col() + 2] == KING)
                && _board[to.row()][to.col() + 1] == BLACK) {
            capture(to, sq(to.col() + 2, to.row()));
        }
        if (to.col() - 2 >= 0
                && (_board[to.row()][to.col() - 2] == WHITE
                || _board[to.row()][to.col() - 2] == KING)
                && _board[to.row()][to.col() - 1] == BLACK) {
            capture(to, sq(to.col() - 2, to.row()));
        }
        if (to.row() + 2 < SIZE
                && (_board[to.row() + 2][to.col()] == _turn
                || _board[to.row() + 2][to.col()] == KING)
                && _board[to.row() + 1][to.col()] == BLACK) {
            capture(to, sq(to.col(), to.row() + 2));
        }
        if (to.row() - 2 >= 0
                && (_board[to.row() - 2][to.col()] == _turn
                || _board[to.row() - 2][to.col()] == KING)
                && _board[to.row() - 1][to.col()] == BLACK) {
            capture(to, sq(to.col(), to.row() - 2));
        }
    }

    /** Capture all potential pieces when piece is moved to TO
     * assuming the turn is black. */
    void captureHelpBlack(Square to) {
        Piece enemy = _turn.opponent();
        if (to.col() + 2 < SIZE
                && _board[to.row()][to.col() + 2] == BLACK
                && _board[to.row()][to.col() + 1] == WHITE) {
            capture(to, sq(to.col() + 2, to.row()));
        }
        if (to.col() - 2 >= 0
                && _board[to.row()][to.col() - 2] == BLACK
                && _board[to.row()][to.col() - 1] == WHITE) {
            capture(to, sq(to.col() - 2, to.row()));
        }
        if (to.row() + 2 < SIZE
                && _board[to.row() + 2][to.col()] == BLACK
                && _board[to.row() + 1][to.col()] == WHITE) {
            capture(to, sq(to.col(), to.row() + 2));
        }
        if (to.row() - 2 >= 0
                && _board[to.row() - 2][to.col()] == BLACK
                && _board[to.row() - 1][to.col()] == WHITE) {
            capture(to, sq(to.col(), to.row() - 2));
        }
    }

    /** Capture the king when piece is moved to TO assuming
     * it's black turn. */
    void kingCaptureHelp(Square to) {
        if (to.col() + 2 < SIZE
                && _board[to.row()][to.col() + 2] == BLACK
                && _board[to.row()][to.col() + 1] == KING) {
            capture(to, sq(to.col() + 2, to.row()));
            _winner = BLACK;
        }
        if (to.col() - 2 >= 0
                && _board[to.row()][to.col() - 2] == BLACK
                && _board[to.row()][to.col() - 1] == KING) {
            capture(to, sq(to.col() - 2, to.row()));
            _winner = BLACK;
        }
        if (to.row() + 2 < SIZE
                && _board[to.row() + 2][to.col()] == BLACK
                && _board[to.row() + 1][to.col()] == KING) {
            capture(to, sq(to.col(), to.row() + 2));
            _winner = BLACK;
        }
        if (to.row() - 2 >= 0
                && _board[to.row() - 2][to.col()] == BLACK
                && _board[to.row() - 1][to.col()] == KING) {
            capture(to, sq(to.col(), to.row() - 2));
            _winner = BLACK;
        }
    }

    /** Capture all potential pieces when throne is empty. */
    void throneSideCap() {
        if (get(THRONE) == EMPTY
                && get(THRONE.col(), THRONE.row() + 1) != EMPTY
                && get(THRONE.col(), THRONE.row() + 2) != EMPTY
                && get(THRONE.col(), THRONE.row() + 1) != KING) {
            if (get(THRONE.col(), THRONE.row() + 1).opponent()
                    == get(THRONE.col(), THRONE.row() + 2)) {
                capture(THRONE, sq(THRONE.col(), THRONE.row() + 1));
            }
        }
        if (get(THRONE) == EMPTY
                && get(THRONE.col(), THRONE.row() - 1) != EMPTY
                && get(THRONE.col(), THRONE.row() - 2) != EMPTY
                && get(THRONE.col(), THRONE.row() - 1) != KING) {
            if (get(THRONE.col(), THRONE.row() - 1).opponent()
                    == get(THRONE.col(), THRONE.row() - 2)) {
                capture(THRONE, sq(THRONE.col(), THRONE.row() - 1));
            }
        }
        if (get(THRONE) == EMPTY
                && get(THRONE.col() + 1, THRONE.row()) != EMPTY
                && get(THRONE.col() + 2, THRONE.row()) != EMPTY
                && get(THRONE.col() + 1, THRONE.row()) != KING) {
            if (get(THRONE.col() + 1, THRONE.row()).opponent()
                    == get(THRONE.col() + 2, THRONE.row())) {
                capture(THRONE, sq(THRONE.col() + 1, THRONE.row()));
            }
        }
        if (get(THRONE) == EMPTY
                && get(THRONE.col() - 1, THRONE.row()) != EMPTY
                && get(THRONE.col() - 2, THRONE.row()) != EMPTY
                && get(THRONE.col() + 1, THRONE.row()) != KING) {
            if (get(THRONE.col() - 1, THRONE.row()).opponent()
                    == get(THRONE.col() - 2, THRONE.row())) {
                capture(THRONE, sq(THRONE.col() - 1, THRONE.row()));
            }
        }
    }
    /** Capture the king when the king is in throne. */
    void kingCapture() {
        if (get(THRONE) == KING
                && get(THRONE.col() + 1, THRONE.row()) == BLACK
                && get(THRONE.col() - 1, THRONE.row()) == BLACK
                && get(THRONE.col(), THRONE.row() + 1) == BLACK
                && get(THRONE.col(), THRONE.row() - 1) == BLACK) {
            revPut(EMPTY, kingPosition());
            _winner = BLACK;
        }
    }

    /** Capture the king when he is surrounded by three
     * black pieces and the throne. */
    void throneCapture() {
        Square king = kingPosition();
        int tRow = THRONE.row();
        int tCol = THRONE.col();
        if (sq(tCol, tRow + 1) == kingPosition()
                && get(tCol, tRow + 2) == BLACK
                && get(tCol - 1, tRow + 1) == BLACK
                && get(tCol + 1, tRow + 1) == BLACK) {
            revPut(EMPTY, kingPosition());
            _winner = BLACK;
        }
        if (sq(tCol, tRow - 1) == kingPosition()
                && get(tCol, tRow - 2) == BLACK
                && get(tCol - 1, tRow - 1) == BLACK
                && get(tCol + 1, tRow - 1) == BLACK) {
            revPut(EMPTY, kingPosition());
            _winner = BLACK;
        }
        if (sq(tCol + 1, tRow) == kingPosition()
                && get(tCol + 2, tRow) == BLACK
                && get(tCol + 1, tRow + 1) == BLACK
                && get(tCol + 1, tRow - 1) == BLACK) {
            revPut(EMPTY, kingPosition());
            _winner = BLACK;
        }
        if (sq(tCol - 1, tRow) == kingPosition()
                && get(tCol - 2, tRow) == BLACK
                && get(tCol - 1, tRow + 1) == BLACK
                && get(tCol - 1, tRow - 1) == BLACK) {
            revPut(EMPTY, kingPosition());
            _winner = BLACK;
        }
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        if (sq0.row() == sq2.row()) {
            int minCol = Math.min(sq0.col(), sq2.col()) + 1;
            revPut(EMPTY, sq(minCol, sq0.row()));
        } else if (sq0.col() == sq2.col()) {
            int minRow = Math.min(sq0.row(), sq2.row()) + 1;
            revPut(EMPTY, sq(sq0.col(), minRow));
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            while (pastPositions.peek().getPiece1() == EMPTY) {
                MyPair temp = pastPositions.pop();
                put(temp.getPiece0(), temp.getSq());
            }
            MyPair move = pastPositions.pop();
            put(move.getPiece0(), move.getSq());
        }
        _winner = null;
        _moveCount--;
        _turn = _turn.opponent();
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        _repeated = false;
        if (!positionCount.contains(encodedBoard())) {
            positionCount.remove(encodedBoard());
        }
    }


    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        pastPositions.empty();
        positionCount.clear();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> result = new ArrayList<Move>();
        for (int x = 0; x < _board.length; x++) {
            for (int y = 0; y < _board[0].length; y++) {
                if (_board[x][y] == KING && side == WHITE) {
                    for (int i = 0; i < SIZE; i++) {
                        if (x != i && isLegal(mv(sq(y, x), sq(y, i)))) {
                            result.add(0, mv(sq(y, x), sq(y, i)));
                        }
                        if (y != i && isLegal(mv(sq(y, x), sq(i, x)))) {
                            result.add(0, mv(sq(y, x), sq(i, x)));
                        }
                    }
                }
                if (_board[x][y] == side) {
                    for (int i = 0; i < SIZE; i++) {
                        if (x != i && isLegal(mv(sq(y, x), sq(y, i)))) {
                            result.add(mv(sq(y, x), sq(y, i)));
                        }
                        if (y != i && isLegal(mv(sq(y, x), sq(i, x)))) {
                            result.add(mv(sq(y, x), sq(i, x)));
                        }
                    }
                }

            }
        }
        return result;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> result = new HashSet<Square>();
        for (int x = 0; x < _board.length; x++) {
            for (int y = 0; y < _board[0].length; y++) {
                if (_board[x][y] == KING && side == WHITE) {
                    result.add(sq(y, x));
                }
                if (_board[x][y] == side) {
                    result.add(sq(y, x));
                }
            }
        }
        return result;
    }
    /** To return the HashSet of the locations of all pieces on SIDE. */
    HashSet<Square> getPieceLocation(Piece side) {
        return pieceLocations(side);
    }
    /** To return how close the KINGPOSITION is to the edge. */
    int kingWin(Square kingPosition) {
        int kingColMin = Math.min(kingPosition.col(),
                (SIZE - 1 - kingPosition.col()));
        int kingRowMin = Math.min(kingPosition.row(),
                (SIZE - 1 - kingPosition.row()));
        int result = Math.min(kingColMin, kingRowMin);
        return 4 - result;
    }
    /** To return how close the KINGPOSITION is to the edge. */
    int kingSurround(Square kingPosition) {
        kingPosition = kingPosition();
        int kCol = kingPosition.col();
        int kRow = kingPosition.row();
        int checker = 0;
        if (kCol + 1 < SIZE && get(kCol + 1,
                kRow) == BLACK) {
            checker++;
        }
        if (kCol - 1 >= 0 && get(kCol - 1,
                kRow) == BLACK) {
            checker++;
        }
        if (kRow + 1 < SIZE && get(kCol,
                kRow + 1) == BLACK) {
            checker++;
        }
        if (kRow - 1 >= 0 && get(kCol,
                kRow - 1) == BLACK) {
            checker++;
        }
        if (checker == 3) {
            checker = 4;
        }
        return checker;

    }
    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** To change the TURN of the board. */
    void turnChanger(Piece turn) {
        _turn = turn;
    }

    /** Return checks if KING can have a forced win assuming
     * its path to EDGE is unblocked. */
    boolean whiteWinCol(Square king, int edge) {
        if (edge == SIZE) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = king.row(); j < SIZE; j++) {
                    if (get(i, j) == BLACK && isUnblockedMove(sq(i, j),
                            sq(king.col(), j))) {
                        return false;
                    }
                }
            }
        } else if (edge == 0) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = king.row(); j >= 0; j--) {
                    if (get(i, j) == BLACK && isUnblockedMove(sq(i, j),
                            sq(king.col(), j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** To return check if KING can have a forced win assuming
     * its path to EDGE is unblocked. */
    boolean whiteWinRow(Square king, int edge) {
        if (edge == SIZE) {
            for (int i = king.col(); i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (get(i, j) == BLACK && isUnblockedMove(sq(i, j),
                            sq(i, king.row()))) {
                        return false;
                    }
                }
            }
        } else if (edge == 0) {
            for (int i = king.col(); i >= 0; i--) {
                for (int j = 0; j < SIZE; j++) {
                    if (get(i, j) == BLACK && isUnblockedMove(sq(i, j),
                            sq(i, king.row()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /** To return location if black can have a forced win . */
    Square blackMightWin() {
        Square king = kingPosition();
        int checker = 0;
        Square result = sq(0, 0);
        if (get(king.col() + 1, king.row()) == BLACK) {
            checker++;
            result = sq(king.col() + 1, king.row());
        } else if (get(king.col() - 1, king.row()) == BLACK) {
            checker++;
            result = sq(king.col() - 1, king.row());
        } else if (get(king.col(), king.row() + 1) == BLACK) {
            checker++;
            result = sq(king.col(), king.row() + 1);
        } else if (get(king.col(), king.row() - 1) == BLACK) {
            checker++;
            result = sq(king.col(), king.row() - 1);
        }
        if (get(result) == WHITE) {
            return null;
        }
        if (checker >= 3) {
            return result;
        } else {
            return null;
        }
    }
    /** To return check if black will have a forced win assuming the king is
     * surrounded by three black pieces except ONE. */
    boolean blackWillWinCol(Square one) {
        if (sq(one.col() - 1, one.row()) == kingPosition()) {
            for (int i = 0; i < SIZE; i++) {
                if (i == one.row()) {
                    break;
                }
                if (get(sq(one.col() - 1, i)) == BLACK
                        && isUnblockedMove(sq(one.col() - 1, i), one)) {
                    if (one.row() > i) {
                        for (int j = i; j <= one.row(); j++) {
                            for (int x = 0; x < SIZE; x++) {
                                if (get(x, j) == WHITE
                                        && isUnblockedMove(sq(x, j), one)) {
                                    return false;
                                }
                            }
                        }
                    } else if (one.row() < i) {
                        for (int j = one.row(); j <= i; j++) {
                            for (int x = 0; x < SIZE; x++) {
                                if (get(x, j) == WHITE
                                        && isUnblockedMove(sq(x, j), one)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    /** To return check if black will have a forced win assuming the king is
     * surrounded by three black pieces except ONE. */
    boolean blackWillWinRow(Square one) {
        for (int i = 0; i < SIZE; i++) {
            if (i == one.row()) {
                break;
            }
            if (get(sq(one.col() - 1, i)) == BLACK
                    && isUnblockedMove(sq(one.col() - 1, i), one)) {
                if (one.row() > i) {
                    for (int j = i; j <= one.row(); j++) {
                        for (int x = 0; x < SIZE; x++) {
                            if (get(x, j) == WHITE
                                    && isUnblockedMove(sq(x, j), one)) {
                                return false;
                            }
                        }
                    }
                } else if (one.row() < i) {
                    for (int j = one.row(); j <= i; j++) {
                        for (int x = 0; x < SIZE; x++) {
                            if (get(x, j) == WHITE
                                    && isUnblockedMove(sq(x, j), one)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /** Set the winner of the board to SIDE. */
    void setWinner(Piece side) {
        _winner = side;

    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** 2D array that represents the board. */
    private Piece[][] _board;
    /** The piece representing the King. */
    private Piece _theKing;
    /** The movement number limit. */
    private int _moveLim;
    /** The Hash Set that stores board positions. */
    private HashSet<String> positionCount;
    /** The that stores past positions. */
    private Stack<MyPair> pastPositions;

}
