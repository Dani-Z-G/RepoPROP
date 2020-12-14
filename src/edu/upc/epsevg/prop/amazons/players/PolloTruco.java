/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.amazons.players;

import edu.upc.epsevg.prop.amazons.*;
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
    //private GameStatus s;
    
    public PolloTruco (String name) {
        this.name = name;
    }
    //setNumerOfNodesExplored​(long numerOfNodesExplored);

    @Override
    public void timeout() {
        System.out.print("Timeout\n");
        inTime=false;
    } 
   
    @Override
    public Move move(GameStatus s){
        inTime=true;
        CellType color = s.getCurrentPlayer();
        jugadr = color;
        int profMax = 2;
        int valor = -999999999, heu;
        Float alfa = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;
        Point queenFrom = new Point(0,0), queenTo = new Point(0,0), arrowTo = new Point(0,0);

        // Profunditats de menys a més, ha de ser major a 0
        for (int prof=1; prof <= profMax; prof++){
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
                           //
                           if (mov_queen.getPos(x, y)==CellType.EMPTY){
                                GameStatus mov_arrow = new GameStatus(mov_queen);
                                //System.out.print("ENTRA\n");
                                //
                                if (mov_arrow.getPos(x, y)==CellType.EMPTY){
                                    mov_arrow.placeArrow(new Point(x, y));
                                    //System.out.print("BUSCA NOVA HURISTICA\n");
                                    heu = MinValor(mov_arrow, prof-1, alfa, beta);
                                    if (valor < heu) {
                                        //System.out.print("NOVA HURISTICA\n");
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
           //System.out.print("Buscant en profunditat: "+prof+" \n");
        }/*
        //System.out.print("Amazonas: "+s.getNumberOfAmazonsForEachColor()+" \n");
        System.out.print("Color Amazonas: "+color+" \n");
        System.out.print("Lista Amazonas: "+s.getAmazon(color, 0)+" \n");
        System.out.print("block: "+queenFrom+" "+queenTo+" "+arrowTo+" \n");
        */
        return new Move(queenFrom, queenTo, arrowTo, 0, 0, SearchType.MINIMAX);
    }

    private int MinValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        if (profunditat == 0 || s.isGameOver()) {
            if (s.isGameOver()){
                return 999999999;
            }
            // get winner
            return heuristica(s, profunditat);
        }
        int valor = -999999999;
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
                            
                            beta=Math.min(valor,beta);
                            if(beta<=alfa) return valor;
                        }
                    }
                }
            }
        }
        return valor;
    }

    private int MaxValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        if (profunditat == 0 || s.isGameOver()) {
            if (s.isGameOver()){
                return -999999999;
            }
            return heuristica(s, profunditat);
        }
        int valor = 999999999;
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
        int heurPL1=0;
        int heurPL2=0;
        int heur=0;
        int aux=0;
        int aux1=0;
    
        //PLAYER1
        for(int i=0; i<=3;i++){
                   Point p = s.getAmazon(CellType.PLAYER1,i);
                   ArrayList<Point> arr = s.getAmazonMoves(p, true);
                   ArrayList<Point> arr1 = s.getAmazonMoves(p, false);
                   aux=arr.size()*4;
                   aux1=(arr1.size())*1;
                   heurPL1 = heurPL1 + aux + aux1;
                   
        }
        //PLAYER2
        for(int i=0; i<=3;i++){
                   Point p = s.getAmazon(CellType.PLAYER2,i);
                   ArrayList<Point> arr = s.getAmazonMoves(p, true);
                   ArrayList<Point> arr1 = s.getAmazonMoves(p, false);
                   aux=arr.size()*4;
                   aux1=(arr1.size())*1;
                   heurPL2 = heurPL2 + aux + aux1;
        }  
        /*
        if(s.getCurrentPlayer()==CellType.PLAYER1){
            heur=heurPL1-heurPL2; 
            System.out.println("Entra222222");
            return heur;
        }
        else if(s.getCurrentPlayer()==CellType.PLAYER2){
            heur=heurPL2-heurPL1;
            System.out.println("Entra");
            return heur;
        }
        */
        if (jugadr==CellType.PLAYER2){
            heur=heurPL2-heurPL1;
        }else{
            heur=heurPL1-heurPL2;
        }
        //System.out.println(heur);
        return heur;
    }

    
    @Override
    public String getName() {
        return name;
    }
}
 