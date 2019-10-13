import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class NQueens {

	public static void main(String[] args) throws Exception {

		NQueenBoard nQueenBoard = new NQueenBoard(Integer.parseInt(args[1]), args[2]);
		if (args[0].equalsIgnoreCase("FOR")) {
			NQueenSolverBTFOR solver = new NQueenSolverBTFOR(args[3]);
			solver.BackTrackSearchWithForwardChecking(nQueenBoard);
		} else if (args[0].equalsIgnoreCase("MAC")) {
			NQueenSolverBTMAC solver = new NQueenSolverBTMAC(args[3]);
			solver.BackTrackSearchWithMAC(nQueenBoard);

		}
	}
}

class NQueenBoard {
	int size;
	String fileName;

	public NQueenBoard(int size, String fileName) throws Exception {
		this.size = size;
		FileWriter fw = new FileWriter("./" + fileName);
		BufferedWriter cFile = new BufferedWriter(fw);
		cFile.write("Variables : \n");
		for (int i = 1; i <= size; i++) {
			cFile.write("Q" + i + '\n');
		}
		cFile.write("\nDomains for the Variables: \n");
		for (int i = 1; i <= size; i++) {
			cFile.write("Q" + i + ": [1");
			for (int j = 2; j <= size; j++) {
				cFile.write("," + j);
			}
			cFile.write("]\n");
		}
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
						cFile.write("Q" + (j + 1) + " - Q" + (1 + 1) + " != " + Math.abs(j - i) + "\n");
						assignArray[i][j] = 1;
						assignArray[j][i] = 1;
					}
				}
			}
		}
		cFile.close();
		fw.close();

	}

}

class QueenGraph {
	long timeElapsed;
	int countBackTrack;
	ArrayList<ArrayList<Integer>> sol;
	String rFileName;

	QueenGraph(String fileName) {
		rFileName = fileName;
		countBackTrack = 0;
		sol = new ArrayList<ArrayList<Integer>>();
	}

	public void printToRFileAndExit() throws Exception {
		long timeExit = System.nanoTime();
		long tot = timeExit - timeElapsed;
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
		rFile.close();
		fw.close();
		System.exit(0);
	}
}

class Arc {
	int var1;
	int var2;

	Arc(int var1, int var2) {
		this.var1 = var1;
		this.var2 = var2;
	}
}

class NQueenSolverBTFOR {

	public NQueenSolverBTFOR(String rFileName) {
		this.rFileName = rFileName;
		queenGraph = new QueenGraph(rFileName);
	}

	String rFileName;
	QueenGraph queenGraph; 
	ArrayList<Integer> assignment = new ArrayList<Integer>();

	public void BackTrackSearchWithForwardChecking(NQueenBoard nBoard) throws Exception {

		queenGraph.countBackTrack = 0;
		for (int i = 0; i < nBoard.size; i++) {
			assignment.add(-1);
		}
		ArrayList<ArrayList<Integer>> reducedDomains = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < nBoard.size; i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < nBoard.size; j++) {
				a.add(j);
			}
			reducedDomains.add(a);
		}
		queenGraph.timeElapsed = System.nanoTime();
		BTFOR(nBoard, reducedDomains);
		queenGraph.printToRFileAndExit();
	}

	public void BTFOR(NQueenBoard nBoard, ArrayList<ArrayList<Integer>> reducedDomains) throws Exception {
		if (isCompleteAssign(assignment)) {
			if (queenGraph.sol.size() < (2 * nBoard.size)) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				for (int i = 0; i < assignment.size(); i++) {
					a.add(assignment.get(i));
				}
				queenGraph.sol.add(a);

				if (queenGraph.sol.size() == (2 * nBoard.size)) {
					queenGraph.printToRFileAndExit();
				}
			}

			return;
		}

		ArrayList<ArrayList<Integer>> reducedDomainCopy = getReducedDomainCopy(reducedDomains);
		int var = selectMinUnassigned(assignment);
		for (int i = 0; i < reducedDomains.get(var).size(); i++) {
			int value = reducedDomains.get(var).get(i);
			reducedDomains = forwardChecking(var, value, assignment, reducedDomains);
			if (reducedDomains != null) {

				BTFOR(nBoard, reducedDomains);

				reducedDomains = reducedDomainCopy;
				assignment.set(var, -1);

			} else {

				reducedDomains = reducedDomainCopy;
			}
			queenGraph.countBackTrack++;
		}
	}

	public boolean isAssignmentComplete(ArrayList<Integer> assignment) {
		for (int i = 0; i < assignment.size(); i++) {
			if (assignment.get(i) == -1) {
				return false;
			}
		}
		return true;
	}

	public int selectMinUnassigned(ArrayList<Integer> assignements) {
		for (int i = 0; i < assignements.size(); i++) {
			if (assignements.get(i) == -1) {
				return i;
			}
		}
		return assignements.size();
	}

	public ArrayList<ArrayList<Integer>> forwardChecking(int var, int value, ArrayList<Integer> assignments,
			ArrayList<ArrayList<Integer>> reducedDomains) {
		ArrayList<ArrayList<Integer>> reducedDomainsCopy = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> a;
		for (int i = 0; i < reducedDomains.size(); i++) {
			a = new ArrayList<Integer>();
			if (assignments.get(i) == -1 && i != var) {
				for (int j = 0; j < reducedDomains.get(i).size(); j++) {
					if (!(reducedDomains.get(i).get(j) == value)
							&& !(Math.abs(i - var) == Math.abs(value - reducedDomains.get(i).get(j)))) {
						a.add(reducedDomains.get(i).get(j));
					}
				}

			} else {
				for (int j = 0; j < reducedDomains.get(i).size(); j++) {
					a.add(reducedDomains.get(i).get(j));

				}
			}
			reducedDomainsCopy.add(a);
		}

		for (int i = 0; i < reducedDomainsCopy.size(); i++) {
			if (reducedDomainsCopy.get(i).size() == 0) {
				return null;
			}
		}
		assignments.set(var, value);
		return reducedDomainsCopy;
	}

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

	public boolean isCompleteAssign(ArrayList<Integer> assignments) {
		for (int i = 0; i < assignments.size(); i++) {
			if (assignments.get(i) == -1) {
				return false;
			}

		}
		return true;
	}

}

class NQueenSolverBTMAC {

	public NQueenSolverBTMAC(String rfileName) {
		// TODO Auto-generated constructor stub
		this.rfileName = rfileName;
		 queenGraph= new QueenGraph(rfileName);
	}

	String rfileName;
	ArrayList<Arc> arcs = new ArrayList<Arc>();
	QueenGraph queenGraph;
	ArrayList<Integer> assignment = new ArrayList<Integer>();

	public void BackTrackSearchWithMAC(NQueenBoard nBoard) throws Exception {

		queenGraph.countBackTrack = 0;
		for (int i = 0; i < nBoard.size; i++) {
			assignment.add(-1);
		}
		ArrayList<ArrayList<Integer>> reducedDomains = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < nBoard.size; i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < nBoard.size; j++) {
				a.add(j);
			}
			reducedDomains.add(a);
		}
		queenGraph.timeElapsed = System.nanoTime();
		BTMAC(nBoard, reducedDomains);
		queenGraph.printToRFileAndExit();
	}

	public void BTMAC(NQueenBoard nBoard, ArrayList<ArrayList<Integer>> reducedDomains) throws Exception {
		if (isCompleteAssign(assignment)) {
			if (queenGraph.sol.size() < (2 * nBoard.size)) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				for (int i = 0; i < assignment.size(); i++) {
					a.add(assignment.get(i));
				}
				queenGraph.sol.add(a);

				if (queenGraph.sol.size() == (2 * nBoard.size)) {
					queenGraph.printToRFileAndExit();
				}
			}

			return;
		}

		ArrayList<ArrayList<Integer>> reducedDomainCopy = getReducedDomainCopy(reducedDomains);
		int var = selectMinUnassigned(assignment);
		for (int i = 0; i < reducedDomains.get(var).size(); i++) {
			int value = reducedDomains.get(var).get(i);
			reducedDomains = maintainArcConsistency(var, value, assignment, reducedDomains);
			if (reducedDomains != null) {

				BTMAC(nBoard, reducedDomains);

				reducedDomains = reducedDomainCopy;
				assignment.set(var, -1);

			} else {

				reducedDomains = reducedDomainCopy;
			}
			queenGraph.countBackTrack++;
		}
	}

	public boolean isAssignmentComplete(ArrayList<Integer> assignment) {
		for (int i = 0; i < assignment.size(); i++) {
			if (assignment.get(i) == -1) {
				return false;
			}
		}
		return true;
	}

	public int selectMinUnassigned(ArrayList<Integer> assignements) {
		for (int i = 0; i < assignements.size(); i++) {
			if (assignements.get(i) == -1) {
				return i;
			}
		}
		return assignements.size();
	}

	public ArrayList<ArrayList<Integer>> maintainArcConsistency(int var, int value, ArrayList<Integer> assignments,
			ArrayList<ArrayList<Integer>> reducedDomains) {

		ArrayList<Integer> assignmentsCopy = new ArrayList<Integer>();
		for (int i = 0; i < assignments.size(); i++) {
			assignmentsCopy.add(assignments.get(i));
		}
		assignmentsCopy.set(var, value);
		ArrayList<ArrayList<Integer>> reducedDomainsCopy = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < reducedDomains.size(); i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			if (assignmentsCopy.get(i) != -1) {
				a.add(assignmentsCopy.get(i));
			} else {
				for (int j = 0; j < reducedDomains.get(i).size(); j++) {
					a.add(reducedDomains.get(i).get(j));
				}
			}
			reducedDomainsCopy.add(a);
		}

		for (int i = 0; i < assignmentsCopy.size(); i++) {
			if (assignmentsCopy.get(i) == -1) {
				arcs.add(new Arc(i, var));
			}
		}

		while (arcs.size() != 0) {
			Arc current = arcs.remove(0);
			if (revisedDomain(current, reducedDomainsCopy)) {
				if (reducedDomainsCopy.get(current.var1).size() == 0) {
					arcs.clear();
					return null;
				} else {
					for (int i = 0; i < assignmentsCopy.size(); i++) {
						if (i != current.var2 && i != current.var1) {
							arcs.add(new Arc(i, current.var1));

						}
					}
				}
			}
		}

		assignment = assignmentsCopy;
		return reducedDomainsCopy;
	}

	public boolean revisedDomain(Arc arc, ArrayList<ArrayList<Integer>> reducedDomainCopy) {
		boolean revised = false;
		int count = 0;
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
		return revised;

	}

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

	public boolean isCompleteAssign(ArrayList<Integer> assignments) {
		for (int i = 0; i < assignments.size(); i++) {
			if (assignments.get(i) == -1) {
				return false;
			}

		}
		return true;
	}
}
