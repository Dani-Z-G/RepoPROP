package edu.upc.epsevg.prop.amazons;

import edu.upc.epsevg.prop.amazons.players.HumanPlayer;
import edu.upc.epsevg.prop.amazons.players.CarlinhosPlayer;
import edu.upc.epsevg.prop.amazons.players.PolloTruco;
import edu.upc.epsevg.prop.amazons.players.RandomPlayer;
import javax.swing.SwingUtilities;

/**
 *
 * @author bernat
 */
public class Amazons {
        /**
     * @param args
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                //IPlayer player1 = new HumanPlayer("Snail");
                IPlayer player2 = new CarlinhosPlayer();
                //IPlayer player1 = new PolloTruco("1");
                //IPlayer player1 = new RandomPlayer("");
                
                IPlayer player1 = new PolloTruco("Pollo");
                
                new AmazonsBoard(player1 , player2, 10, Level.QUARTERBOARD);
                //new AmazonsBoard(player1 , player2, 10, Level.FULL_BOARD);
                //new AmazonsBoard(player1 , player2, 10, Level.HALF_BOARD);
            }
        });
    }
}
