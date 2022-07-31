import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PuzzleCombinations {
	//The 8 puzzle throughout this program is considered in the form of a matrix and is referred to as a matrix
	//The original state of the matrix and the current state of the matrix
	private static char [][] matrix_org = new char[3][3];
	private static char [][] matrix_cur = new char[3][3];
	//Co-ordinate of the blank tile
	private static Integer x = 0;
	private static Integer y = 0;
	//ArrayList of moves leading to duplicate states
	private static ArrayList<String> illegal_moves = new ArrayList<String>();
	//Stack of moves leading from the original state to the current state
	private static ArrayList<String> move_stack = new ArrayList<String>(Arrays.asList("start"));
	//ArrayList of all unique matrices created moves
	private static ArrayList<String> matrices_made = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		//Makes sure file is empty for program
		clearMatrices();
		
		//Obtains the user inputs for the first state
		System.out.println("Enter values for the 8 puzzle (State 1)");
		Scanner inputScanner = new Scanner(System.in);
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				System.out.println("Value for position ("+Integer.toString(i)+","+Integer.toString(j)+"):");
				char ans = inputScanner.nextLine().charAt(0);
				matrix_org[i][j] = ans;
				matrix_cur[i][j] = ans;
			}
		}
		
		//Adds start to original and matrices made
		storeMatrix1();
		matrices_made.add(ArrayToString(Arrays.copyOf(matrix_cur, 3)));
		
		//Condition for while loop
		boolean finished = false;
		String direction = "";
		System.out.println("Average time taken is 12mins per state");
		System.out.println("Please do not enter any values while calculating");
		System.out.println("Calculating...");
		while (!(finished)) {
			boolean duplicate = false;
			//creates list of moves and removes unavailable moves after getting the position of blank
			ArrayList<String> moves = new ArrayList<String>(Arrays.asList("up","down","left","right"));
			findBlank();
			moves = getMoves(moves);
			if (!(moves.isEmpty())) {
				//obtains direction blank moved in and stores if its a duplicate
				direction = checkMove(moves);
				duplicate = checkMatrix();
				if (duplicate) {
					/*
					if matrix made is a duplicate the move is stored as an illegal move
					and matrix is returned to previous state with the mover removed from the stack
					*/
					findBlank();
					illegal_moves.add(direction);
					returnMatrix(direction);
					move_stack.remove((move_stack.size()-1));
				} else {
					//if matrix made is unique then illegal moves are cleared and the new matrix is stored
					illegal_moves.clear();
					storeMatrix1();
					matrices_made.add(ArrayToString(Arrays.copyOf(matrix_cur, 3)));
				}
			} else if ((equals()) && (moves.isEmpty())) {
				/*
				if there are no available moves and the current state is equal to the original state
				there are no more possible combinations left to be made
				 */
				finished = true;
			} else {
				/*
				no available moves but the current state is not the 
				original matrix than use the stack to return to a previous state
				 */
				findBlank();
				returnMatrix(move_stack.get((move_stack.size()-1)));
				move_stack.remove((move_stack.size()-1));
				illegal_moves.clear();
			}
		}
		
		//Size of matrices made is equal to |R(S1)|
		int Rs1 = matrices_made.size();
		
		//Clears all attributes to run code for second matrix
		illegal_moves.clear();
		move_stack.clear();
		matrices_made.clear();
		move_stack.add("start");
		
		//Lines 100 to 147 are the same as 33 to 92 but with respect to the second user inputed state
		System.out.println("Enter values for the 8 puzzle (State 2)");

		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				System.out.println("Value for position ("+Integer.toString(i)+","+Integer.toString(j)+"):");
				char ans = inputScanner.nextLine().charAt(0);
				matrix_org[i][j] = ans;
				matrix_cur[i][j] = ans;
			}
		}
		inputScanner.close();
		storeMatrix2();
		matrices_made.add(ArrayToString(Arrays.copyOf(matrix_cur, 3)));
		
		finished = false;
		direction = "";
		System.out.println("Average time taken is 12mins per state");
		System.out.println("Calculating...");
		while (!(finished)) {
			boolean duplicate = false;
			ArrayList<String> moves = new ArrayList<String>(Arrays.asList("up","down","left","right"));
			findBlank();
			moves = getMoves(moves);
			if (!(moves.isEmpty())) {
				direction = checkMove(moves);
				duplicate = checkMatrix();
				if (duplicate) {
					findBlank();
					illegal_moves.add(direction);
					returnMatrix(direction);
					move_stack.remove((move_stack.size()-1));
				} else {
					illegal_moves.clear();
					storeMatrix2();
					matrices_made.add(ArrayToString(Arrays.copyOf(matrix_cur, 3)));
				}
			} else if ((equals()) && (moves.isEmpty())) {
				finished = true;
			} else {
				findBlank();
				returnMatrix(move_stack.get((move_stack.size()-1)));
				move_stack.remove((move_stack.size()-1));
				illegal_moves.clear();
			}
		}
		
		int Rs2 = matrices_made.size();
		
		//Displays values for |R(S1)| and |R(S2)| and states how to obtain the reachable states from S1 and S2
		System.out.println("|R(S1)| (Not including S1) = "+Integer.toString(Rs1-1));
		System.out.println("For R(S1) open the 'matrices1.txt' file to view all possible states. (Each array of length 3 on a line represents a row in the puzzle starting from the top row to the bottom row)");
		System.out.println("|R(S2)| (Not including S2) = "+Integer.toString(Rs2-1));
		System.out.println("For R(S2) open the 'matrices2.txt' file to view all possible states.");
		
		String text = new String(Files.readAllBytes(Paths.get("matrices1.txt")), StandardCharsets.UTF_8);
		String S2 = Arrays.toString(matrix_org[0]) + Arrays.toString(matrix_org[1]) + Arrays.toString(matrix_org[2]);
		
		//Checks if S2 is in the file containing all reachable solutions from S1
		if (text.contains(S2)) {
			//If S2 is reachable then all nodes created by S1 can also be created by S2
			System.out.println("|R(S1 n S2)| (Not including S1 or S2) = "+Integer.toString(Rs1-2));
			System.out.println("For R(S1 n S2) open either TextFile mentioned above as all reachable states are included.");
		} else {
			//If S2 is not reachable then S1 and S2 have no common nodes
			System.out.println("|R(S1 n S2)| (Not including S1 or S2) = 0");
			System.out.println("For R(S1 n S2) there are no common states.");
		}
	}
	
	//Turns the matrix into a string
	public static String ArrayToString (char[][] array) {
		String mat = Arrays.deepToString(array);
		return mat;
	}
	
	//Removes the impossible moves (such as UP on the top row) and moves that produce duplicate states (moves present in illegal moves or the last move performed)
	public static ArrayList<String> getMoves(ArrayList<String> moves){
		String last_move = move_stack.get((move_stack.size()-1));
		if ((y.equals(0)) || (last_move.equals("down"))) {
			moves.remove("up");
		} 
		if ((y.equals(2)) || (last_move.equals("up")))  {
			moves.remove("down");
		} 
		
		if ((x.equals(0)) || (last_move.equals("right"))) {
			moves.remove("left");
		}
		if ((x.equals(2)) || (last_move.equals("left"))){
			moves.remove("right");
		}
		for (int n=0; n<illegal_moves.size();n++) {
			moves.remove(illegal_moves.get(n));
		}
		return moves;
	}
	
	/*
	Stores the location of the blank tile
	For loops slowed the program down so an if statement was used 
	 */
	public static void findBlank() {
		if (matrix_cur[0][0] == ' ') {
			x = 0;
			y = 0;
		} else if (matrix_cur[0][1] == ' ') {
			x = 1;
			y = 0;
		} else if (matrix_cur[0][2] == ' ') {
			x = 2;
			y = 0;
		} else if (matrix_cur[1][0] == ' ') {
			x = 0;
			y = 1;
		} else if (matrix_cur[1][1] == ' ') {
			x = 1;
			y = 1;
		} else if (matrix_cur[1][2] == ' ') {
			x = 2;
			y = 1;
		} else if (matrix_cur[2][0] == ' ') {
			x = 0;
			y = 2;
		} else if (matrix_cur[2][1] == ' ') {
			x = 1;
			y = 2;
		} else if (matrix_cur[2][2] == ' ') {
			x = 2;
			y = 2;
		}		
	}
	
	//Checks if the matrix was a duplicate by checking matrices made
	public static boolean checkMatrix() {
		if (matrices_made.contains(Arrays.deepToString(matrix_cur))) {
			return true;
		}
		return false;
	}
	
	//Returns matrix to previous state if a duplicate is formed
	public static void returnMatrix(String move) {
		if (move.equals("up")) {
			moveDown();
		} else if (move.equals("down")) {
			moveUp();
		} else if (move.equals("left")) {
			moveRight();
		} else if (move.equals("right")) {
			moveLeft();
		}
	}
	
	//Performs move based on available moves and adds to the stack
	public static String checkMove(ArrayList<String> moves) {
		String move = moves.get(0);
		if (move.equals("up")) {
			moveUp();
			move_stack.add("up");
			return "up";
		} else if (move.equals("down")) {
			moveDown();
			move_stack.add("down");
			return "down";
		} else if (move.equals("left")) {
			moveLeft();
			move_stack.add("left");
			return "left";
		} else if (move.equals("right")) {
			moveRight();
			move_stack.add("right");
			return "right";
		} else {
			return null;
		}
	}
	
	//Next 4 functions move blank tile in the given direction
	public static void moveUp() {
		char temp = matrix_cur [y-1][x];
		matrix_cur[y][x] = temp;
		matrix_cur[y-1][x] = ' ';
	}
	
	public static void moveDown() {
		char temp = matrix_cur [y+1][x];
		matrix_cur[y][x] = temp;
		matrix_cur[y+1][x] = ' ';
	}
	
	public static void moveLeft() {
		char temp = matrix_cur [y][x-1];
		matrix_cur[y][x] = temp;
		matrix_cur[y][x-1] = ' ';
	}
	
	public static void moveRight() {
		char temp = matrix_cur [y][x+1];
		matrix_cur[y][x] = temp;
		matrix_cur[y][x+1] = ' ';
	}
	
	//Stores the possible states of first matrix in the first file
	public static void storeMatrix1() {
		try {
		    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("matrices1.txt", true)));
			writer.print(Arrays.toString(matrix_cur[0]));
			writer.print(Arrays.toString(matrix_cur[1]));
			writer.print(Arrays.toString(matrix_cur[2]));
			writer.println();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//Stores the possible states of second matrix in the second file
	public static void storeMatrix2() {
		try {
		    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("matrices2.txt", true)));
			writer.print(Arrays.toString(matrix_cur[0]));
			writer.print(Arrays.toString(matrix_cur[1]));
			writer.print(Arrays.toString(matrix_cur[2]));
			writer.println();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//Clears both matrix files
	public static void clearMatrices() throws FileNotFoundException {
		PrintWriter remover;
		remover = new PrintWriter("matrices1.txt");
		remover.print("");
		remover.close();
		remover = new PrintWriter("matrices2.txt");
		remover.print("");
		remover.close();
	}
	
	//Checks if the original matrix and the current matrix are equal
	public static boolean equals() {
	    if (!(matrix_cur[0][0] == matrix_org[0][0])) {
	    	return false;
	    } else if (!(matrix_cur[0][1] == matrix_org[0][1])) {
	    	return false;
	    } else if (!(matrix_cur[0][2] == matrix_org[0][2])) {
	    	return false;
	    } else if (!(matrix_cur[1][0] == matrix_org[1][0])) {
	    	return false;
	    } else if (!(matrix_cur[1][1] == matrix_org[1][1])) {
	    	return false;
	    } else if (!(matrix_cur[1][2] == matrix_org[1][2])) {
	    	return false;
	    } else if (!(matrix_cur[2][0] == matrix_org[2][0])) {
	    	return false;
	    } else if (!(matrix_cur[2][1] == matrix_org[2][1])) {
	    	return false;
	    } else if (!(matrix_cur[2][2] == matrix_org[2][2])) {
	    	return false;
	    }
	    return true;
	 }
	
}
