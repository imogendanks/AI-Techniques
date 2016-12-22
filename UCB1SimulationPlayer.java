/* Imogen Danks - C1431388
 * 
 * Used Resources
 * 
 * currentTimeMillis 
 * 			- https://www.tutorialspoint.com/java/lang/system_currenttimemillis.htm
 * 			- http://stackoverflow.com/questions/351565/system-currenttimemillis-vs-system-nanotime
 *				(top comment here specifically)
 *
 * Percentage calculation - http://stackoverflow.com/questions/11552158/percentage-chance-of-saying-something
 * 
 * discussed general ideas/problem solving with PhD student 
 */

package players;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.Math;
import javax.swing.JOptionPane;

import quoridor.GameState2P;
import quoridor.Quoridor;
import moves.*;

public class UCB1SimulationPlayer extends QuoridorPlayer {
	
	public UCB1SimulationPlayer(GameState2P state, int index, Quoridor game) {
        super(state, index, game);
    }
	
	public Move chooseMove(){
		
		//move variable to be returned
		Move choice = null;
		boolean hasPlayed = false;
		double totalSimulations = 0;
		
		//find legal moves for current played state
		List<Move> legalMoves = GameState2P.getLegalMoves(state, index);
		int length = legalMoves.size();
		//create 2D array to store wins/number of plays counter
		float[][] wins = new float[length][3];
		//get current time in milliseconds
		long time = System.currentTimeMillis();
		float maxUCB = 0;
		int bestUCB = 0;
		//int counter = 0;
		
		//current time - previous current time = time elapsed
		//do while time elapsed is less than 5 seconds
		while (System.currentTimeMillis() - time < 5000){
			//counter++;
			//System.out.println(counter);
			//count through each legal move index
			//if its the first iteration
			if (hasPlayed == false){
				//System.out.println("first set of sims");
				for (int i = 0; i < length; i++){
					//another check for time - in case it entered the while loop close to 5 seconds - break if greater that 5 secs.
					if (System.currentTimeMillis() - time > 5000){
						break;
					}
					//increase legal move play counter
					wins[i][1] += 1;
					totalSimulations += 1;
					//run simulation for the chosen move/resulting state
					if (simulate(legalMoves.get(i).doMove(state)) == true){
						//if the method returns a 'win' (true boolean)
						//increase win for legal move chosen previously
						wins[i][0] += 1;
					}
					//System.out.println(Arrays.deepToString(wins));
				}
			}
			//for all possible legal moves
			maxUCB = 0;
			bestUCB = 0;
			for(int a = 0; a < length; a++){
				if (System.currentTimeMillis() - time > 5000){
					break;
				}
				//calculate the UCB1 for their 1st simulation

				wins[a][2] = ((float)wins[a][0]/wins[a][1])+((float)Math.sqrt((2*Math.log(totalSimulations))/wins[a][1]));
				//store the best UCB/best UCB move 
				if (wins[a][2] > maxUCB){
					maxUCB = wins[a][2];
					bestUCB = a;
				}
			}
			wins[bestUCB][1] += 1;
			totalSimulations += 1;
			//run simulation for the best UCB move
			if (simulate(legalMoves.get(bestUCB).doMove(state)) == true){
				//if the method returns a 'win' (true boolean)
				//increase win for legal move chosen previously
				wins[bestUCB][0] += 1;
			}
			//indicate the first iteration has been completed
			hasPlayed = true;
		}
		//System.out.println(counter);
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
		Move alphaMove = null;
		
		//current player
		int playerIndex = index;
		//all legal moves for current simulated state
		List<Move> legalMoves;
		Random random = new Random();
		
		//while the game hasn't reached a game over state
		while (!currentState.isGameOver()){
			//get the legal moves for the current simulated state for each player (changes each loop)
			legalMoves = GameState2P.getLegalMoves(currentState, playerIndex);
			//choose random number from 0 - 1, times by 100 to give an integer no. between 0 and 100, 
			//if its between 0 and 90 (90% chance) then do alphabeta
			//else (10%) do random simulation.
			if (Math.random() * 100 < 90) {
				//System.out.println("alphaBeta");
				AlphaBetaPlayerFixed alphaBeta = new AlphaBetaPlayerFixed(currentState, playerIndex, game);
				//set depth of AB
				alphaBeta.setMaxDepth(2);
				alphaMove = alphaBeta.chooseMove();
				currentState = alphaMove.doMove(currentState);
			}
			else {
				//System.out.println("Random");
				//choose a random move
				randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
				// update the simulated state to the state after the randomly chosen move
				currentState = randomMove.doMove(currentState);
			}
			//if the current simualted state is not a game over state then 'flip' the player index for the next iteration of the loop
			if (!currentState.isGameOver()){
				playerIndex = (playerIndex + 1)% 2;
			}
		}
		//else the winner is the current index - if it is the same as the actual current player then return a win
		return (playerIndex == index);
	}
}
