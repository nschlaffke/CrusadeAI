/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.player;

import put.ai.games.crusade.CrusadeBoard;
import put.ai.games.game.Board;
import put.ai.games.game.Move;

import java.util.List;

public class SmartPlayer extends put.ai.games.game.Player
{
    private static final double RETREAT_VALUE = 300;
    private class MoveValue
    {

        MoveValue(Integer value)
        {
            this.value = value;
            this.move = null;
        }

        private Move move;

        void setValue(int value)
        {
            this.value = value;
        }

        int getValue()
        {
            return value;
        }

        void setMove(Move move)
        {
            this.move = move;
        }

        Move getMove()
        {
            return move;
        }

        private Integer value;
    }

    @Override
    public String getName()
    {
        return "Norbert Schlaffke 127201 Piotr Janiszewski 127246";
    }

    @Override
    public Move nextMove(Board b)
    {
        return MinMax((CrusadeBoard) b);
    }

    private Move MinMax(CrusadeBoard board)
    {
        return maxMove(board, Integer.MIN_VALUE, Integer.MAX_VALUE,  getTime(),0).getMove();
    }
    private boolean retreat(CrusadeBoard board, long timeLeft, long depth)
    {
        return (timeLeft - depth * RETREAT_VALUE * board.getSize()/10 <= 0);
    }
    private MoveValue maxMove(final CrusadeBoard board, Integer alpha, Integer beta, long timeLeft, long depth)
    {
        System.out.println(timeLeft);
        long start = System.currentTimeMillis();
        Color color = getColor();
        if (gameEnded(board) || retreat(board, timeLeft, depth))
        {
            System.out.println("Retreat");
            return new MoveValue(board.countStones(color));
        }
        List<Move> availableMoves = board.getMovesFor(color);
        MoveValue bestMove = new MoveValue(Integer.MIN_VALUE);
        for (Move move : availableMoves)
        {
            long elapsedTime = System.currentTimeMillis() - start;
            int score = minMove(applyMove(board, move), beta, alpha, timeLeft - elapsedTime, depth + 1).getValue();
            if (score > bestMove.getValue())
            {
                bestMove.setMove(move);
                bestMove.setValue(score);
                alpha = score;
            }
            if(alpha > beta)
            {
                System.out.println("Cutoff");
                return bestMove;
            }
        }
        return bestMove;
    }
    private MoveValue minMove(final CrusadeBoard board, Integer beta, Integer alpha, long timeLeft, long depth)
    {
        System.out.println(timeLeft);
        long start = System.currentTimeMillis();
        Color color = getOpponent(getColor());
        if (gameEnded(board) || retreat(board, timeLeft, depth))
        {
            System.out.println("Retreat");
            return new MoveValue(board.countStones(getColor()));
        }
        List<Move> availableMoves = board.getMovesFor(color);
        MoveValue bestMove = new MoveValue(Integer.MAX_VALUE);
        for (Move move : availableMoves)
        {
            long elapsedTime = System.currentTimeMillis() - start;
            int score = maxMove(applyMove(board, move), alpha, beta, timeLeft - elapsedTime, depth + 1).getValue();
            if (score < bestMove.getValue())
            {
                bestMove.setMove(move);
                bestMove.setValue(score);
                beta = score;
            }
            if(alpha > beta)
            {
                System.out.println("Cutoff");
                return bestMove;
            }
        }
        return bestMove;
    }
    private CrusadeBoard applyMove(final CrusadeBoard board, Move move)
    {
        CrusadeBoard copy = new CrusadeBoard(board);
        copy.doMove(move);
        return copy;
    }

    private boolean gameEnded(CrusadeBoard board)
    {
        Color max = getColor();
        Color min = getOpponent(max);
        return (board.getWinner(max) != null) || (board.getWinner(getOpponent(min)) != null);
    }
}
