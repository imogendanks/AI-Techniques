/* Imogen Danks - C1431388
 * 
 * Used Resources
 * 
 * currentTimeMillis 
 * 			- https://www.tutorialspoint.com/java/lang/system_currenttimemillis.htm
 * 			- http://stackoverflow.com/questions/351565/system-currenttimemillis-vs-system-nanotime
 *				(top comment here specifically)
 *
 * discussed general ideas/problem solving with PhD student 
 */
package players;

import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import quoridor.GameState2P;
import quoridor.Quoridor;
import moves.*;

public class RandomSimulationPlayer extends QuoridorPlayer {
	
	public RandomSimulationPlayer(GameState2P state, int index, Quoridor game) {
        super(state, index, game);
    }
	
	public Move chooseMove(){
		//move variable to be returned
		Move choice = null;
		//find legal moves for current played state
		List<Move> legalMoves = GameState2P.getLegalMoves(state, index);
		int length = legalMoves.size();
		//create 2D array to store wins/number of plays counter
		int[][] wins = new int[length][2];
		//get current time in milliseconds
		long time = System.currentTimeMillis();
		//current time - previous current time = time elapsed
		//do while time elapsed is less than 5 seconds
		while (System.currentTimeMillis() - time < 5000){
				//another check for time - in case it entered the while loop close to 5 seconds - break if greater that 5 secs.
				if (System.currentTimeMillis() - time > 5000){
					break;
				}
				Random i = new Random();
				int n = i.nextInt(length);
				//increase legal move play counter
				wins[n][1] += 1;
				//run simulation for the chosen move/resulting state
				if (simulate(legalMoves.get(n).doMove(state)) == true){
					//if the method returns a 'win' (true boolean)
					//increase win for legal move chosen previously
					wins[n][0] += 1;
				}		
		}
		//varibles used to calculate 'best' move to play
		float topScore = 0;
		float tempScore = 0;
		int best = 0;
		
		//iterate through each legal move index
		for (int x = 0; x < length; x++){
				//calculate its percentage chance of a win result
				tempScore = (float)wins[x][0]/wins[x][1];
				//if the current index percentage score is greater than the previous largest, 
				//then the make it be the current topScore
				if (tempScore > topScore){
					topScore = tempScore;
					//System.out.println(topScore);
					//make the best move be the move index
					best = x;
				}
		}
		
		//make the returnable move be the best from the legal move array.
		choice = legalMoves.get(best);
		//return best move
		return choice;
	}

	public void doMove() {
			//simulate and return best move
			Move m = chooseMove();
			//play move and update state
			GameState2P newState = m.doMove(state);
			game.doMove(index, newState);
		
	}
	
	public boolean simulate (GameState2P currentState) {
		//chosen random move
		Move randomMove = null;
		
		//current player
		int playerIndex = index;
		//all legal moves for current simulated state
		List<Move> legalMoves;
		Random random = new Random();
		
		//while the game hasn't reached a game over state
		while (!currentState.isGameOver()){
			//get the legal moves for the current simulated state for each player (changes each loop)
			legalMoves = GameState2P.getLegalMoves(currentState, playerIndex);
			//choose a random move
			randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
			// update the simulated state to the state after the randomly chosen move
			currentState = randomMove.doMove(currentState);
			//if the current simualted state is not a game over state then 'flip' the player index for the next iteration of the loop
			if (!currentState.isGameOver()){
				playerIndex = (playerIndex + 1)% 2;
			}
		}
		//else the winner is the current index - if it is the same as the actual current player then return a win
		return (playerIndex == index);
	}
}



