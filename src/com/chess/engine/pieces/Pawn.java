package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import org.carrot2.shaded.guava.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES ={8,16,7,9};

    public Pawn(final Alliance pieceAlliance,final int piecePosition) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,true);
    }
    public Pawn(final Alliance pieceAlliance,final int piecePosition,final boolean isFirstMove) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset:CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection()*currentCandidateOffset);
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }
            if (currentCandidateOffset==8&&!board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                if (pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                }else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset ==16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition]&&this.pieceAlliance.isBlack())||
                    (BoardUtils.SECOND_RANK[this.piecePosition]&&this.pieceAlliance.isWhite()))) {
                final int behindCandidateDestinationCoordinate=this.piecePosition+(this.pieceAlliance.getDirection()*8);
                if(!board.getTile(candidateDestinationCoordinate).isTileOccupied()&&
                        !board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                   }
                } else if (currentCandidateOffset==7
                &&!((BoardUtils.EIGHTH_COLUMN[this.piecePosition]&&this.pieceAlliance.isWhite()||
                        (BoardUtils.FIRST_COLUMN[this.piecePosition]&&this.pieceAlliance.isBlack())))) {
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate=board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()) {
                            if (pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            } else {
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                            }
                        }
                    } else if (board.getEnPassantPawn()!=null) {
                        if (board.getEnPassantPawn().getPiecePosition()==(this.getPiecePosition()+(this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate=board.getEnPassantPawn();
                            if (this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }

            } else if (currentCandidateOffset==9
                        &&!((BoardUtils.EIGHTH_COLUMN[this.piecePosition]&&this.pieceAlliance.isBlack()||
                        (BoardUtils.FIRST_COLUMN[this.piecePosition]&&this.pieceAlliance.isWhite())))) {
                    if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate=board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                            if (pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate)));
                            }else{
                                legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }else if (board.getEnPassantPawn()!=null) {
                        if (board.getEnPassantPawn().getPiecePosition()==(this.getPiecePosition()-(this.pieceAlliance.getOppositeDirection()))){
                            final Piece pieceOnCandidate=board.getEnPassantPawn();
                            if (this.pieceAlliance!=pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate,pieceOnCandidate));
                            }
                        }
                    }
                }
            }

        return ImmutableList.copyOf(legalMoves);
    }
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
    }
    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
    public Piece getPromotionPiece(){
        return new Queen(this.pieceAlliance,this.piecePosition,false);
    }
}
