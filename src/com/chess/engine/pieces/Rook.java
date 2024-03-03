package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import org.carrot2.shaded.guava.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.chess.engine.board.Move.*;
public class Rook extends Piece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES ={-8,-1,1,8};

    public Rook(final Alliance pieceAlliance,final int piecePosition) {
        super(PieceType.ROOK,piecePosition, pieceAlliance,true);
    }
    public Rook(final Alliance pieceAlliance,final int piecePosition,final boolean isFirstMove) {
        super(PieceType.ROOK,piecePosition, pieceAlliance,isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
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
                        legalMoves.add(new Move.MoveMajor(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = CandidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }
    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
    }
    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition]&&((candidateOffset==-1));
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition]&&((candidateOffset==1));
    }
}
