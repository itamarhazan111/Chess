package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveAttack;
import com.chess.engine.board.Move.MoveMajor;
import com.chess.engine.board.Tile;
import org.carrot2.shaded.guava.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;
public class Bishop extends Piece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES ={-9,-7,7,9};

    public Bishop(final Alliance pieceAlliance,final int piecePosition) {
        super(PieceType.BISHOP,piecePosition, pieceAlliance,true);
    }
    public Bishop(final Alliance pieceAlliance,final int piecePosition,final boolean isFirstMove) {
        super(PieceType.BISHOP,piecePosition, pieceAlliance,isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATES){
            int candidateDestinationCoordinate=this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(candidateDestinationCoordinate,currentCandidateOffset)||
                        isEighthColumnExclusion(candidateDestinationCoordinate,currentCandidateOffset)){
                    break;
                }
                candidateDestinationCoordinate+=currentCandidateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile CandidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!CandidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MoveMajor(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAeDestination = CandidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAeDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAeDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition]&&((candidateOffset==-9)||(candidateOffset==7));
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition]&&((candidateOffset==-7)||(candidateOffset==9));
    }
}
