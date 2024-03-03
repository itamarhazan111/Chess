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

public class King extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES ={-9,-8,-7,-1,1,7,8,9};
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    public King(final Alliance pieceAlliance,final int piecePosition,final boolean kingSideCastleCapable,final boolean queenSideCastleCapable) {
        super(PieceType.KING,piecePosition, pieceAlliance,true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    public King(final Alliance pieceAlliance,final int piecePosition,final boolean isFirstMove,final boolean isCastled,final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING,piecePosition, pieceAlliance,isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    public boolean isCastled() {
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset:CANDIDATE_MOVE_COORDINATES){
            final int candidateDestinationCoordinate=this.piecePosition+currentCandidateOffset;
            if (isFirstColumnExclusion(this.piecePosition,currentCandidateOffset)
                    ||isEighthColumnExclusion(this.piecePosition,currentCandidateOffset)){
                continue;
            }
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile CandidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!CandidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MoveMajor(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAeDestination = CandidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAeDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAeDestination));
                    }
                }
            }
        }
                return ImmutableList.copyOf(legalMoves);
    }
    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate(),false,move.isCastlingMove(),false,false);
    }
    @Override
    public String toString(){
        return PieceType.KING.toString();
    }
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition]&&((candidateOffset==-1)||(candidateOffset==-9)||(candidateOffset==7));
    }
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition]&&((candidateOffset==-7)||(candidateOffset==1)||(candidateOffset==9));
    }
}
