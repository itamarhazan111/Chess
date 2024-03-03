package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import org.carrot2.shaded.guava.common.collect.ImmutableMap;


import java.util.HashMap;
import java.util.Map;

public abstract  class Tile {
    protected final int tileCoordinate;
    private static final Map<Integer,EmptyTile> EMPTY_TILES_CACHE =createPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createPossibleEmptyTiles() {
        final Map<Integer,EmptyTile> emptyTileMap= new HashMap<>();
        for(int i=0;i<BoardUtils.NUM_TILES;i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }
    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece !=null ? new DeccupiedTile(tileCoordinate,piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    private Tile(final int tileCoordinate){
         this.tileCoordinate=tileCoordinate;
     }

     public int getTileCoordinate(){
        return this.tileCoordinate;
     }
     public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile{

        private EmptyTile(final int tileCoordinate){
            super(tileCoordinate);
        }
        @Override
        public String toString(){
            return "-";
        }
        @Override
        public boolean isTileOccupied(){
            return false;
        }
        @Override
        public Piece getPiece(){
            return  null;
        }

    }
    public static final class DeccupiedTile extends Tile{

        private final Piece pieceOnTile;
        private DeccupiedTile(int tileCoordinate,final Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile=pieceOnTile;
        }
        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase():
                    getPiece().toString();
        }
        @Override
        public boolean isTileOccupied(){
            return true;
        }
        @Override
        public Piece getPiece(){
            return  pieceOnTile;
        }
    }

}
