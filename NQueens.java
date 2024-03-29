import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class NQueens {
	// Main Method
	public static void main(String[] args) {
		try {

			// Create a chessboard object
			QueenGraph queenGraph = new QueenGraph(Integer.parseInt(args[1]), args[2]);
			// If user has provided FOR as his first argument, call Backtracking with Forward Checking
			if (args[0].equalsIgnoreCase("FOR")) {
				NQueenSolverBTFOR solver = new NQueenSolverBTFOR(args[3]);
				solver.BackTrackSearchWithForwardChecking(queenGraph);
				// Else If user has provided MAC as his first argument, call Backtracking with Maintaining Arc Consistency
			} else if (args[0].equalsIgnoreCase("MAC")) {
				NQueenSolverBTMAC solver = new NQueenSolverBTMAC(args[3]);
				solver.BackTrackSearchWithMAC(queenGraph);

			} else
				System.out.println("Invalid argument provided for Algorithm, please provide either of FOR or MAC\n your input was :" + args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("Please provide 4 arguments, namely ALG, N, CFile, RFile");
		} catch (NumberFormatException n) {
			System.out.println("Invalid value given for N, only give a positive integer\n your input was : "+args[1]);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

//The NQueenBoard class, which is a representation of the chess board.
class QueenGraph {
	// stores the size (N) of the board
	int size;
	//variables stores our variables and their assignments, variable.get(i) returns value of variable Q(i+1)
	ArrayList<Integer> variables = new ArrayList<Integer>();
	//domain is a list of list of all possible values for each of the the variables
	//domains.get(i) returns the domains of variable Q(i+1)
	ArrayList<ArrayList<Integer>> domains = new ArrayList<ArrayList<Integer>>();
	// CFile name is stored in this string
	String fileName;
	
	
	// The following constructor initializes the Chess board and also writes to the CFile Output
	public QueenGraph(int size, String fileName) throws Exception {
		this.size = size;
		// Set all variable assignments to -1
		for (int i = 0; i < size; i++) {
				variables.add(-1);
		}
		
		// Store full domains for all variables in an Arraylist of Arraylist of integers called domains. 
		//domains.get(i) returns the domains of variable Q(i+1)
		
		for (int i = 0; i < size; i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < size; j++) {
						a.add(j);
			}
			domains.add(a);
		}
		// Open CFile to Write to
		FileWriter fw = new FileWriter("./" + fileName);
		BufferedWriter cFile = new BufferedWriter(fw);
		// Write all the variables
		cFile.write("Variables : \n");
		for (int i = 1; i <= size; i++) {
			cFile.write("Q" + i + '\n');
		}
		// Write all Domains
		cFile.write("\nDomains for the Variables: \n");
		for (int i = 1; i <= size; i++) {
			cFile.write("Q" + i + ": [1");
			for (int j = 2; j <= size; j++) {
				cFile.write("," + j);
			}
			cFile.write("]\n");
		}
		// Write all constraints
		cFile.write("\nConstraints : \n");
		int assignArray[][] = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j) {
					assignArray[i][j] = 1;
				} else {
					if (assignArray[i][j] != 1) {
						cFile.write("Q" + (i + 1) + " != Q" + (j + 1) + "\n");
						cFile.write("Q" + (i + 1) + " - Q" + (j + 1) + " != " + Math.abs(i - j) + "\n");
						cFile.write("Q" + (j + 1) + " - Q" + (i + 1) + " != " + Math.abs(j - i) + "\n");
						assignArray[i][j] = 1;
						assignArray[j][i] = 1;
					}
				}
			}
		}
		// Close CFile
		cFile.close();
		fw.close();

	}

}

//The NQueenSolutionList Class is used to store Solution States obtained after performing Forward checking or Maintaining Arc Consistency
class NQueenSolutionList {
	// Stores the time when code started executing
	long startTime;
	// Represents number of backtracking steps taken
	int countBackTrack;
	// A list of Solution states are stored in the following array list
	ArrayList<ArrayList<Integer>> sol;
	// Used to store the RFile name to which output is written
	String rFileName;

	// Constructor
	NQueenSolutionList(String fileName) {
		rFileName = fileName;
		countBackTrack = 0;
		sol = new ArrayList<ArrayList<Integer>>();
	}

	// This method prints the Time taken, backtracking step count, number of solutions and up to 2(N) solutions to RFile
	public void printToRFileAndExit() throws Exception {
		// Store time when FOR or MAC Code exited
		long timeExit = System.nanoTime();
		// Calculate time taken by subtracting start itme from finish time
		long tot = timeExit - startTime;
		// Open RFile to write to
		FileWriter fw = new FileWriter("./" + rFileName);
		BufferedWriter rFile = new BufferedWriter(fw);
		rFile.write("Total Number of Solutions found :" + sol.size() + "\n");
		rFile.write("Real Time Taken :" + tot + " nanoseconds\n");
		rFile.write("Number of BackTracking Steps : " + countBackTrack + "\n");
		rFile.write("The Solutions are as follows\n");
		for (int i = 0; i < sol.size(); i++) {
			rFile.write("Solution :" + (i + 1) + " ");
			rFile.write("[");
			for (int j = 0; j < sol.get(i).size(); j++) {
				rFile.write("Q" + (j + 1) + " = " + (sol.get(i).get(j) + 1));
				if (j < sol.get(i).size() - 1) {
					rFile.write(" , ");
				}

			}
			rFile.write("]\n");
		}
		// Close RFile
		rFile.close();
		fw.close();
		System.exit(0);
	}
}

//Arc class is used to Store an arc between two neighboring variables
class Arc {
	// Variable 1 of the arc
	int var1;
	// Variable 2 of the arc
	int var2;

	Arc(int var1, int var2) {
		this.var1 = var1;
		this.var2 = var2;
	}
}

//This class is used to solve the NQueens problem using Backtracking With forward checking 
class NQueenSolverBTFOR {

	// Constructor, initializes a NQueenSolutionList object to store solutions and also sets RFile Name as provided by user argument
	public NQueenSolverBTFOR(String rFileName) {
		this.rFileName = rFileName;
		queenSolutionList = new NQueenSolutionList(rFileName);
	}

	String rFileName;
	NQueenSolutionList queenSolutionList;
	
	
	// This method starts off our backtracking with forward checking method
	public void BackTrackSearchWithForwardChecking(QueenGraph queenGraph) throws Exception {

		// Initialize backtracking steps to 0
		queenSolutionList.countBackTrack = 0;
		
		// Make note of time when Backtracking Algorithm starts
		queenSolutionList.startTime = System.nanoTime();
		// Call the backtracking algorithm that used Forward Checking
		BTFOR(queenGraph);
		// This method gets called when the number of solutions is less than 2*N and Backtracking algorithm terminates
		queenSolutionList.printToRFileAndExit();
	}

	// The following method performs backtracking with forward checking
	// It takes the chessboard state as input
	public void BTFOR(QueenGraph queenGraph) throws Exception {
		// Check if varibale assignment is complete, that is if all variables have been assigned a value
		if (isCompleteAssign(queenGraph.variables)) {
			// If number of solutions found is less than 2*N, add the new solution to the QueenGraph class
			if (queenSolutionList.sol.size() < (2 * queenGraph.size)) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				for (int i = 0; i < queenGraph.variables.size(); i++) {
					a.add(queenGraph.variables.get(i));
				}
				queenSolutionList.sol.add(a);

				// If after adding the new solution, total count becomes 2*N, print the RFile
				// and Exit
				if (queenSolutionList.sol.size() == (2 * queenGraph.size)) {
					queenSolutionList.printToRFileAndExit();
				}
			}

			return;
		}

		// Make an exact same copy of the domains of queenGraph
		ArrayList<ArrayList<Integer>> reducedDomainCopy = getReducedDomainCopy(queenGraph.domains);
		// Select a variable that is not assigned yet and store it in var
		//var represents Q(var+1) variable
		int var = selectMinUnassigned(queenGraph.variables);
		// Explore the ordered domain of the Q(var+1) variable
		for (int i = 0; i < queenGraph.domains.get(var).size(); i++) {
			// Value is the smallest unexplored element of variable Q(var+1)'s domain
			int value = queenGraph.domains.get(var).get(i);
			// Perform forward checking on this assignment of Q(var+1), value and return a reduced domain based on the forward checking
			queenGraph.domains = forwardChecking(var, value, queenGraph);
			// if reduced domain is not null, it implies the domain of no variable was
			// reduced to empty list
			if (queenGraph.domains != null) {

				// Call BackTracking with Forward checking recursively to move on to the next variable's assignment
				BTFOR(queenGraph);
				/*
				 * Once our algorithm returns from the above recursive call, it implies that it
				 * is backtracking to the previous varibale's assignment and therefore we also
				 * need to backtrack to the previous domains and remove this assignment
				 */
				// Therefore we set our reducedDomain to the previously set copy
				queenGraph.domains = reducedDomainCopy;
				// Remove the assignment
				queenGraph.variables.set(var, -1);
				/* If reduced domain is null, it implies the domain of at least one variable was
				 reduced to empty list
				 Even in this case, we need to return to the previous domain values
				 */
			} else {
				/*If any of the varibale domains were reduced to empty sets, copy back the previous domains 
				in order to check for the next assignment */ 
				queenGraph.domains = reducedDomainCopy;
			}
			/*
			 * We increment backtrack count as we would reach this code only when we either
			 * backtrack from a recursive call or we run out of viable domain values for our
			 * current variable , where too, we backtrack to the previous variable
			 * assignment
			 */
			queenSolutionList.countBackTrack++;
		}
	}

	// This method picks the smallest unassigned variable number
	public int selectMinUnassigned(ArrayList<Integer> assignements) {
		for (int i = 0; i < assignements.size(); i++) {
			if (assignements.get(i) == -1) {
				return i;
			}
		}
		return assignements.size();
	}

	// This method performs forward checking on our current variable assignment and reduces the domains of the appropriate variables accordingly
	public ArrayList<ArrayList<Integer>> forwardChecking(int var, int value, QueenGraph queenGraph) {

		ArrayList<ArrayList<Integer>> reducedDomainsCopy = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> a;

		// The code snippet below reduces our domain based on the assignmentQ(var+1)=value and stores it in a varibale called reducedDomainCopy
		for (int i = 0; i < queenGraph.domains.size(); i++) {
			a = new ArrayList<Integer>();
			// if a variable is unassigned and not a part of our current assignment
			if (queenGraph.variables.get(i) == -1 && i != var) {
				for (int j = 0; j < queenGraph.domains.get(i).size(); j++) {
					// If a value in another variable's domain conflicts with our current assignment, remove that value from reduced domain
					if (!(queenGraph.domains.get(i).get(j) == value)
							&& !(Math.abs(i - var) == Math.abs(value - queenGraph.domains.get(i).get(j)))) {
						a.add(queenGraph.domains.get(i).get(j));
					}
				}

				// If a variable is assigned or a part of our current assignment, copy it's domain as it is
			} else {
				for (int j = 0; j < queenGraph.domains.get(i).size(); j++) {
					a.add(queenGraph.domains.get(i).get(j));

				}
			}
			reducedDomainsCopy.add(a);
		}

		// Check if if any domain i our reducedDomainCopy was empty, if yes ,return null.
		for (int i = 0; i < reducedDomainsCopy.size(); i++) {
			if (reducedDomainsCopy.get(i).size() == 0) {
				return null;
			}
		}
		// If none of the reducedDomains were reduced to empty sets, add the current Assign value to variable Q(var+1)
		queenGraph.variables.set(var, value);
		// return the new reducedDomainCopy as our new reducedDomain
		return reducedDomainsCopy;
	}

	// This method makes a copy of the reducedDomain Object
	public ArrayList<ArrayList<Integer>> getReducedDomainCopy(ArrayList<ArrayList<Integer>> reducedDomain) {
		ArrayList<ArrayList<Integer>> redArrayList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> a;
		for (int i = 0; i < reducedDomain.size(); i++) {
			a = new ArrayList<Integer>();
			for (int j = 0; j < reducedDomain.get(i).size(); j++) {
				a.add(reducedDomain.get(i).get(j));
			}
			redArrayList.add(a);
		}

		return redArrayList;
	}

	// This method check if all the variables have been assigned a value
	// If yes, it returns true otherwise it returns false
	public boolean isCompleteAssign(ArrayList<Integer> assignments) {
		for (int i = 0; i < assignments.size(); i++) {
			if (assignments.get(i) == -1) {
				return false;
			}

		}
		return true;
	}

}

//This class is used to solve the NQueens problem using Backtracking With Maintaining Arc Consistency
class NQueenSolverBTMAC {

	// Constructor, initializes a QueenGraph object to store solutions and also sets RFile Name as provided by user argument
	public NQueenSolverBTMAC(String rfileName) {
		// TODO Auto-generated constructor stub
		this.rfileName = rfileName;
		nQueenSolutionList= new NQueenSolutionList(rfileName);
	}

	String rfileName;
	// Arcs is a queue which stores objects of type Arc
	ArrayList<Arc> arcs = new ArrayList<Arc>();
	NQueenSolutionList nQueenSolutionList;

	// This method starts off our backtracking with MAC, it initializes all assignments to -1 and sets the initial domains for all variables
	public void BackTrackSearchWithMAC(QueenGraph queenGraph) throws Exception {
		// Initialize backtracking steps to 0
		nQueenSolutionList.countBackTrack = 0;
		
		// Make note of time when Backtracking Algorithm starts
		nQueenSolutionList.startTime = System.nanoTime();
		// Call the backtracking algorithm that used Forward Checking
		BTMAC(queenGraph);
		// This method gets called when the number of solutions is less than 2*N and Backtracking algorithm terminates
		nQueenSolutionList.printToRFileAndExit();
	}

	// The following method performs backtracking with forward checking
	// It takes the chessboard state as input
	public void BTMAC(QueenGraph queenGraph) throws Exception {
		// Check is assignment is complete, that is if all variables have been assigned a value
		if (isCompleteAssign(queenGraph.variables)) {
			// If number of solutions found is less than 2*N, add the new solution to the
			// QueenGraph class
			if (nQueenSolutionList.sol.size() < (2 * queenGraph.size)) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				for (int i = 0; i < queenGraph.variables.size(); i++) {
					a.add(queenGraph.variables.get(i));
				}
				nQueenSolutionList.sol.add(a);
				// If after adding the new solution, total count becomes 2*N, print the RFile
				// and Exit
				if (nQueenSolutionList.sol.size() == (2 * queenGraph.size)) {
					nQueenSolutionList.printToRFileAndExit();
				}
			}

			return;
		}
		// Make an exact same copy of the domains into reducedDomainCopy Variable
		ArrayList<ArrayList<Integer>> reducedDomainCopy = getReducedDomainCopy(queenGraph.domains);
		// Select a variable that is not assigned yet and store it in var
		//var represents variable Q(var+1)
		int var = selectMinUnassigned(queenGraph.variables);
		// Explore the ordered domain of the var variable
		for (int i = 0; i < queenGraph.domains.get(var).size(); i++) {
			// Value is the smallest unexplored element of variable Q(var+1)'s domain
			int value = queenGraph.domains.get(var).get(i);
			// Perform Maintaining Arc consistency on this assignment of var, value and return a reduced domain
			queenGraph.domains = maintainArcConsistency(var, value, queenGraph);
			// if reduced domain is not null, it implies the domain of no variable was reduced to empty list
			if (queenGraph.domains != null) {
				// Call BackTracking with Maintaining Arc Consistency recursively to move on to the next varible's assignment
				BTMAC(queenGraph);
				/*
				 * Once our algorithm returns from the above recursive call, it implies that it
				 * is backtracking to the previous variable's assignment and therefore we also
				 * need to backtrack to the previous domains and remove this assignment
				 */
				// Therefore we set our reducedDomain to the previously set copy
				queenGraph.domains = reducedDomainCopy;
				// Remove the assignment
				queenGraph.variables.set(var, -1);
				// if reduced domain is null, it implies the domain of at least one variable was reduced to empty list
				// Even in this case, we need to return to the previous domain values
			} else {
				
				queenGraph.domains = reducedDomainCopy;
			}
			/*
			 * We increment backtrack count as we would reach this code only when we either
			 * backtrack from a recursive call or we run out of viable domain values for our
			 * current variable , where too, we backtrack to the previous variable
			 * assignment
			 */
			nQueenSolutionList.countBackTrack++;
		}
	}

	// This method picks the smallest unassigned variable number
	public int selectMinUnassigned(ArrayList<Integer> assignements) {
		for (int i = 0; i < assignements.size(); i++) {
			if (assignements.get(i) == -1) {
				return i;
			}
		}
		return assignements.size();
	}

	// This method performs Maintaining Arc Consistency on our current variable assignment and reduces the domains of the appropriate variables accordingly
	public ArrayList<ArrayList<Integer>> maintainArcConsistency(int var, int value, QueenGraph queenGraph) {
		// Copy current assignment values into a variable assignmentCopy
		ArrayList<Integer> assignmentsCopy = new ArrayList<Integer>();
		for (int i = 0; i < queenGraph.variables.size(); i++) {
			assignmentsCopy.add(queenGraph.variables.get(i));
		}
		// Add var, value to assignment
		assignmentsCopy.set(var, value);

		// Copy current reducedDomain values into reducedDomainCopy except for the ones
		// already assigned
		ArrayList<ArrayList<Integer>> reducedDomainsCopy = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < queenGraph.domains.size(); i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			// If a variable is assigned a value, add that value into reducedDomainsCopy for
			// that variable
			if (assignmentsCopy.get(i) != -1) {
				a.add(assignmentsCopy.get(i));
				// For unassigned variables, copy all values into reducedDomainsCopy
			} else {
				for (int j = 0; j < queenGraph.domains.get(i).size(); j++) {
					a.add(queenGraph.domains.get(i).get(j));
				}
			}
			reducedDomainsCopy.add(a);
		}

		// Add arcs for all unassigned variables that are connected to our current
		// variable to the queue
		for (int i = 0; i < assignmentsCopy.size(); i++) {
			if (assignmentsCopy.get(i) == -1) {
				arcs.add(new Arc(i, var));
			}
		}

		// While Queue is not empty
		while (arcs.size() != 0) {
			// Remove the arc at the beginning of the queue
			Arc current = arcs.remove(0);
			// Check if Domain of variable x1 was revised for an arc(x1,x2)
			if (revisedDomain(current, reducedDomainsCopy)) {
				// If the domain of x1 in arc(x1,x2) was reduced to empty list, return null
				if (reducedDomainsCopy.get(current.var1).size() == 0) {
					// Clear up the queue and return null
					arcs.clear();
					return null;
					// If the domain of x1 in arc(x1,x2) was not reduced to empty list, then add arcs with neighboring elements of x1
				} else {
					// if x3 is a neighbor of x1 in arc(x1,x2), add arc(x3,x1) to the queue such that x3 is not the same variable as x2
					for (int i = 0; i < assignmentsCopy.size(); i++) {
						if (i != current.var2 && i != current.var1) {
							arcs.add(new Arc(i, current.var1));

						}
					}
				}
			}
		}

		// Add var, value to assignment as they are consistent with the other assignments
		queenGraph.variables = assignmentsCopy;
		// return the new reducedDomain
		return reducedDomainsCopy;
	}

	// This method check if for an arc(a,b), whether the domain of "a" was revised or not
	public boolean revisedDomain(Arc arc, ArrayList<ArrayList<Integer>> reducedDomainCopy) {
		boolean revised = false;
		int count = 0;
		/*
		 * The following code checks if for a value D in domain of variable "a" in an
		 * arc(a,b), is there a value E in domain of b for which D is consistent. If
		 * there is no such value, delete D from a's domain and set revised as true
		 */
		for (int i = 0; i < reducedDomainCopy.get(arc.var1).size(); i++) {
			count = 0;
			for (int j = 0; j < reducedDomainCopy.get(arc.var2).size(); j++) {
				int a = reducedDomainCopy.get(arc.var1).get(i);
				int b = reducedDomainCopy.get(arc.var2).get(j);
				if ((a == b) || (Math.abs(a - b) == Math.abs(arc.var1 - arc.var2))) {
					count++;
				}
			}
			if (count == reducedDomainCopy.get(arc.var2).size()) {
				reducedDomainCopy.get(arc.var1).remove(i);
				i = i - 1;
				revised = true;
			}

		}
		// Return revised
		return revised;

	}

	// This method makes a copy of the reducedDomain list
	public ArrayList<ArrayList<Integer>> getReducedDomainCopy(ArrayList<ArrayList<Integer>> reducedDomain) {
		ArrayList<ArrayList<Integer>> redArrayList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> a;
		for (int i = 0; i < reducedDomain.size(); i++) {
			a = new ArrayList<Integer>();
			for (int j = 0; j < reducedDomain.get(i).size(); j++) {
				a.add(reducedDomain.get(i).get(j));
			}
			redArrayList.add(a);
		}

		return redArrayList;
	}

	// The following method checks whether all variables have been assigned a value or not
	public boolean isCompleteAssign(ArrayList<Integer> assignments) {
		for (int i = 0; i < assignments.size(); i++) {
			if (assignments.get(i) == -1) {
				return false;
			}

		}
		return true;
	}
}
