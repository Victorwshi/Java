package tablut;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.*;

import static tablut.Board.SIZE;
import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Victor Shi
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** A Hashmap for testing. */
    private HashMap<Move, Integer> testing;
    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }
    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }
    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move myMove = findMove();
        _controller.reportMove(myMove);
        return myMove.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** A list that stores moves. */
    private ArrayList moves;
    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        testing = new HashMap<>();
        moves = new ArrayList();
        if (_myPiece == BLACK) {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        }
        if (_lastFoundMove == null) {
            b.setWinner(b.turn().opponent());
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        if (sense == 1) {
            List<Move> whiteMoves = board.legalMoves(WHITE);
            if (whiteMoves == null) {
                return WINNING_VALUE;
            }
            int maxResult = -INFTY;
            for (Move temp : whiteMoves) {
                board.makeMove(temp);
                int test = findMove(board, depth - 1,
                        false, -1, alpha, beta);
                testing.put(temp, test);
                board.undo();
                if (test > alpha && saveMove) {
                    _lastFoundMove = temp;
                }
                maxResult = max(maxResult, test);
                alpha = max(alpha, maxResult);
                if (alpha >= beta) {
                    break;
                }
            }
            return maxResult;
        } else {
            List<Move> blackMoves = board.legalMoves(BLACK);
            if (blackMoves == null) {
                return -WINNING_VALUE;
            }
            int minResult = INFTY;
            for (Move temp2 : blackMoves) {
                board.makeMove(temp2);
                int test2 = findMove(board, depth - 1,
                        false, 1, alpha, beta);
                board.undo();
                minResult = min(minResult, test2);
                if (test2 < beta && saveMove) {
                    _lastFoundMove = temp2;
                }
                beta = min(beta, minResult);
                if (alpha >= beta) {
                    break;
                }
            }
            return minResult;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        int x = board.moveCount();
        return 4;
    }
    /** TO return check if there is a forced win for white based on
     * characteristics of BOARD. */
    private boolean whiteWillWin(Board board) {
        Square king = board.kingPosition();
        if (board.turn() == WHITE) {
            if (king != null && board.isUnblockedMove(king,
                    sq(king.col(), SIZE - 1))
                    && board.whiteWinCol(king, SIZE - 1)) {
                return true;
            } else if (king != null && board.isUnblockedMove(king,
                    sq(king.col(), 0))
                    && board.whiteWinCol(king, 0)) {
                return true;
            } else if (king != null && board.isUnblockedMove(king,
                    sq(SIZE - 1, king.row()))
                    && board.whiteWinRow(king, SIZE - 1)) {
                return true;
            } else if (king != null && king.row() == 1
                    && board.isUnblockedMove(king,
                    sq(0, king.row()))
                    && board.whiteWinRow(king, 0)) {
                return true;
            }
        }
        return false;
    }
    /** To return check if there is a forced win for black based on
     * characteristics of BOARD. */
    private boolean blackWillWin(Board board) {
        return false;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        List<Move> whiteMoves;
        List<Move> blackMoves;
        int finalScore = 0;
        if (board.winner() == WHITE) {
            return WINNING_VALUE - board.moveCount();
        } else if (board.winner() == BLACK) {
            return -WINNING_VALUE + board.moveCount();
        }
        if (board.turn() == WHITE) {
            whiteMoves = board.legalMoves(WHITE);
            board.turnChanger(BLACK);
            blackMoves = board.legalMoves(BLACK);
            board.turnChanger(WHITE);
        } else {
            blackMoves = board.legalMoves(BLACK);
            board.turnChanger(WHITE);
            whiteMoves = board.legalMoves(WHITE);
            board.turnChanger(BLACK);
        }
        HashSet<Square> whitePiece = board.getPieceLocation(WHITE);
        HashSet<Square> blackPiece = board.getPieceLocation(BLACK);
        finalScore += whiteMoves.size() - blackMoves.size();
        finalScore += whitePiece.size() - blackPiece.size();

        return finalScore;
    }


}
