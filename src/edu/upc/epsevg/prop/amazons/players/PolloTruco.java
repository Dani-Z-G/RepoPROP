/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.amazons.players;

import edu.upc.epsevg.prop.amazons.*;
import static edu.upc.epsevg.prop.amazons.CellType.opposite;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class PolloTruco implements IPlayer, IAuto {
    
    private boolean inTime;
    
    CellType jugadr;
    
    private String name;
    private int nodesExplorats;
   
    @Override
    public Move move(GameStatus s){
        
        CellType color = s.getCurrentPlayer();
        int profMax = 100, valor = -1000000, heu=0, prof;
        Float alfa = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;
        Point queenFrom = new Point(0,0), queenTo = new Point(0,0), arrowTo = new Point(0,0);
        
        nodesExplorats=0;
        inTime=true;
        jugadr = color;

        // Profunditats de menys a més, ha de ser major a 0
        for (prof=1; prof <= profMax && inTime; prof++){
            // Todas las fichas del color
            for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
               Point pos = s.getAmazon(color, num);
               ArrayList<Point> arr = s.getAmazonMoves(pos, false);
               // Movimientos posibles
               for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                   GameStatus mov_queen = new GameStatus(s);
                   mov_queen.moveAmazon(pos, arr.get(i));
                   // Lista de posiciones vacias
                   for (int x = 0; x < s.getSize(); x++){
                       for (int y = 0; y < s.getSize(); y++){
                           if (mov_queen.getPos(x, y)==CellType.EMPTY){
                                GameStatus mov_arrow = new GameStatus(mov_queen);
                                if (mov_arrow.getPos(x, y)==CellType.EMPTY){
                                    mov_arrow.placeArrow(new Point(x, y));
                                    if(inTime) heu = MinValor(mov_arrow, prof-1, alfa, beta);
                                    if (valor < heu) {
                                        System.out.print("Nova heu: "+prof+"\n");
                                        valor=heu;
                                        queenFrom=pos;
                                        queenTo=arr.get(i);
                                        arrowTo = new Point(x,y);
                                    }
                                }
                            }
                        }                    
                    }
                }
            }
        }
        return new Move(queenFrom, queenTo, arrowTo, nodesExplorats, prof-1, SearchType.MINIMAX);
    }

    private int MinValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        int valor = 1000000; 
        if (profunditat == 0 || s.isGameOver()) {
            return heuristica(s, profunditat);
        }
       
        for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
            Point pos = s.getAmazon(color, num);
            ArrayList<Point> arr = s.getAmazonMoves(pos, false);
            for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                GameStatus mov_queen = new GameStatus(s);
                mov_queen.moveAmazon(pos, arr.get(i));
                // Lista de posiciones vacias
                for (int x = 0; x < s.getSize(); x++){
                    for (int y = 0; y < s.getSize(); y++){
                        if (mov_queen.getPos(x, y)==CellType.EMPTY && inTime){
                            GameStatus mov_arrow = new GameStatus(mov_queen);
                            mov_arrow.placeArrow(new Point(x, y));
                           
                            valor = Math.min(valor, MaxValor(mov_arrow, profunditat-1, alfa, beta));
                            
                            //beta=Math.min(valor,beta);
                            //if(beta<=alfa) return valor;
                        }
                    }
                }
            }
        }
        return valor;
    }

    private int MaxValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        int valor = -1000000;
        if (profunditat == 0 || s.isGameOver()) {
            return heuristica(s, profunditat);
        }
       
        for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
            Point pos = s.getAmazon(color, num);
            ArrayList<Point> arr = s.getAmazonMoves(pos, false);
            for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                GameStatus mov_queen = new GameStatus(s);
                mov_queen.moveAmazon(pos, arr.get(i));
                // Lista de posiciones vacias
                for (int x = 0; x < s.getSize(); x++){
                    for (int y = 0; y < s.getSize(); y++){
                        if (mov_queen.getPos(x, y)==CellType.EMPTY && inTime){
                            GameStatus mov_arrow = new GameStatus(mov_queen);
                            mov_arrow.placeArrow(new Point(x, y));
                
                            valor = Math.max(valor, MinValor(mov_arrow, profunditat-1, alfa, beta));

                            alfa=Math.max(valor,alfa);
                            if(beta<=alfa) return valor;
                        }
                    }
                }
            }
        }
        return valor;
    }

   public int heuristica(GameStatus s, int profunditat){
        nodesExplorats++;
        int heurPL1=0, heurPL2=0; 
        
        CellType colCurr = jugadr;
        CellType colOpp = opposite(jugadr);
             
        if (s.isGameOver()){
           if (s.GetWinner() == colCurr){
               return 1000000;
           }else{
               return -1000000;
           }
        }
       
        //PLAYER Inicial
       for(int i=0; i<=3;i++){
           Point p = s.getAmazon(colCurr,i);
           int moves = s.getAmazonMoves(p, true).size();
           heurPL1 += moves*6 + s.getAmazonMoves(p, false).size()*1;
           // Penalitzem si tenim alguna fitxa sense moviments
           if (moves == 0){
               heurPL1 += -100;
           }
       }

       //PLAYER Contrari
       for(int i=0; i<=3;i++){
           Point p = s.getAmazon(colOpp,i);
           int moves = s.getAmazonMoves(p, true).size();
           heurPL2 += moves*6 + s.getAmazonMoves(p, false).size()*1;
           // Penalitzem si tenim alguna fitxa sense moviments
           if (moves == 0){
               heurPL2 += -80;
           }
       }
        
        // Retornem el valor segons si beneficia al jugador o no
        int heur=heurPL1-heurPL2;
        return heur;
    } 
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void timeout() {
        System.out.print("Timeout\n");
        inTime=false;
    }
    
    public PolloTruco (String name) {
        this.name = name;
    }
}