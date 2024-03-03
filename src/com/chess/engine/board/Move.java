package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE=new NullMove();

    private Move(final Board board,final Piece movedPiece, final int destinationCoordinate) {
        this.board=board;
        this.movedPiece=movedPiece;
        this.destinationCoordinate=destinationCoordinate;
        this.isFirstMove=movedPiece.isFirstMove();
    }
    private Move(final Board board, final int destinationCoordinate) {
        this.board=board;
        this.destinationCoordinate=destinationCoordinate;
        this.movedPiece=null;
        this.isFirstMove=false;
    }
    @Override
    public int hashCode(){
        final int prime=31;
        int result=1;
        result=prime*result+this.destinationCoordinate;
        result=prime*result+this.movedPiece.hashCode();
        result=prime*result+this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if(this==other){
            return true;
        }
        if (!(other instanceof Move)){
            return  false;
        }
        final Move move=(Move) other;
        return getCurrentCoordinate()==((Move) other).getCurrentCoordinate()&&
                getDestinationCoordinate()== move.getDestinationCoordinate()&&
                getMovedPiece().equals(move.getMovedPiece());
    }

    public int getCurrentCoordinate(){
        return this.getMovedPiece().getPiecePosition();
    }
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }
    public Board getBoard(){return this.board; }
    public boolean isAttack(){
        return false;
    }
    public boolean isCastlingMove(){
        return false;
    }
    public Piece getAttackPiece(){
        return null;
    }

    public Board execute() {
        final Builder builder=new Builder();
        for (final Piece piece:this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }
    public static final class MajorAttackMove extends MoveAttack{
        public MajorAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate,attackPiece);
        }
        @Override
        public boolean equals(final Object other) {
            return this==other|| other instanceof MajorAttackMove && super.equals(other);
        }
        @Override
        public String toString(){
            return movedPiece.getPieceType()+BoardUtils.getPositionAtCoordinate((this.destinationCoordinate));
        }
    }

    public static final class MoveMajor extends Move{

        public MoveMajor(final Board board,final Piece movedPiece,final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this==other|| other instanceof MoveMajor&& super.equals(other);
        }
        @Override
        public String toString(){
            return movedPiece.getPieceType().toString()+BoardUtils.getPositionAtCoordinate((this.destinationCoordinate));
        }
    }
    public static class MoveAttack extends Move{
        final Piece attackPiece;
        public MoveAttack(final Board board,final Piece movedPiece,final int destinationCoordinate,final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackPiece=attackPiece;
        }
        @Override
        public int hashCode(){
            return this.attackPiece.hashCode()+super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this==other){
                return true;
            }
            if (!(other instanceof MoveAttack)){
                return  false;
            }
            final  MoveAttack move=(MoveAttack) other;
            return super.equals(move)&&getAttackPiece().equals(move.getAttackPiece());
        }
        @Override
        public boolean isAttack(){
            return true;
        }
        @Override
        public Piece getAttackPiece(){
            return this.attackPiece;
        }

    }
    public static final class PawnMove extends Move{

        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnMove && super.equals(other);

        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }
    public static class PawnAttackMove extends MoveAttack{

        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate,attackPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnAttackMove && super.equals(other);

        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1)+"x"+
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate,attackPiece);
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnEnPassantAttackMove && super.equals(other);

        }
        @Override
        public Board execute(){
            final Builder builder=new Builder();
            for (final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    public static class PawnPromotion extends Move{
        final Move decorateMove;
        final Pawn pawnPromoted;

        public PawnPromotion(final Move decorateMove) {
            super(decorateMove.getBoard(), decorateMove.getMovedPiece(), decorateMove.getDestinationCoordinate());
            this.decorateMove=decorateMove;
            this.pawnPromoted=(Pawn) decorateMove.getMovedPiece();
        }
        @Override
        public int hashCode(){
            return decorateMove.hashCode()+(31*pawnPromoted.hashCode());
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnPromotion && super.equals(other);

        }
        @Override
        public Board execute(){
            final Board pawnMovedBoard=this.decorateMove.execute();
            final Board.Builder builder=new Builder();
            for (final Piece piece:pawnMovedBoard.currentPlayer().getActivePieces()){
                if (!this.pawnPromoted.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece:pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.pawnPromoted.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }
        @Override
        public boolean isAttack(){
            return this.decorateMove.isAttack();
        }
        @Override
        public Piece getAttackPiece(){
            return this.decorateMove.getAttackPiece();
        }
        @Override
        public String toString(){
            return "";
        }
    }


    public static final class PawnJump extends Move{

        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public Board execute() {
            final Builder builder=new Builder();
            for (final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn=(Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }
    static abstract class CastleMove extends Move{
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(final Board board,final Piece movedPiece,final int destinationCoordinate,
                          final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook=castleRook;
            this.castleRookStart=castleRookStart;
            this.castleRookDestination=castleRookDestination;
        }
        public Rook getCastleRook(){
            return this.castleRook;
        }
        @Override
        public boolean isCastlingMove(){
            return true;
        }
        @Override
        public Board execute() {
            final Builder builder=new Builder();
            for (final Piece piece:this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)&&!this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(),this.castleRookDestination,false));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override
        public int hashCode(){
            final int prime=31;
            int result=super.hashCode();
            result=prime*result+this.castleRook.hashCode();
            result=prime*result+this.castleRookDestination;
            return result;
        }
        @Override
        public boolean equals(final Object other) {
            if(this==other){
                return true;
            }
            if (!(other instanceof MoveAttack)){
                return  false;
            }
            final  CastleMove otherCastleMove=(CastleMove) other;
            return super.equals(otherCastleMove)&&this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }
    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof KingSideCastleMove && super.equals(other);

        }
        @Override
        public String toString(){
            return "O-O";
        }

    }
    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof QueenSideCastleMove && super.equals(other);

        }
        @Override
        public String toString(){
            return "O-O-O";
        }
    }
    public static final class NullMove extends Move {
        public NullMove() {
            super(null,65);
        }
        @Override
        public Board execute(){
            throw new RuntimeException("cannot execute null move");
        }
        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }
    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("not instantiable");
        }
        public static Move createMove(final Board board,final int currentCoordinate,final int destinationCoordinate){
            for (final Move move:board.getAllLegalMoves()){
                if(move.getCurrentCoordinate()==currentCoordinate&&
                move.getDestinationCoordinate()==destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
