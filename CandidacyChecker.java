import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

public class CandidacyChecker {

	public static final String INPUT_FILE_NAME = "candidates.txt";
	public static final String ENCODE = "UTF-8";
	public static final int MAX_CANDIDATES = 4;
	
	public static PriorityQueue<Candidate> candidates;
	
	public static void main(String[] args) {
		// I'm expecting department name is always first in command line.
		String departmentName = args[0];
		
		try {
			setCandidates();
//			printCandidates();
			ArrayList<Candidate> selected = selectCandidates(departmentName, MAX_CANDIDATES);
//			printSelected(selected);
			writeToFile(departmentName + ".txt", selected);
		} catch (FileNotFoundException fnfe) {
			System.out.printf("%s is not found in this directory\n", INPUT_FILE_NAME);
		} catch (UnsupportedEncodingException uee) {
			System.out.printf("Encode: %s is not supported\n", ENCODE);
		} catch (Exception e) {
			System.out.println("Something went wrong");
			System.out.println(e);
		}
	}

	public static void setCandidates() throws FileNotFoundException {
		candidates = new PriorityQueue<Candidate> ();
		
		File f = new File(INPUT_FILE_NAME);
		Scanner in = new Scanner(f);
		
		while (in.hasNextLine()) {
			String s = in.nextLine();
			String[] sarray = s.split(";");
			Candidate c = new Candidate(sarray[0], Integer.parseInt(sarray[1]), Integer.parseInt(sarray[2]), sarray[3], Integer.parseInt(sarray[4]));
			candidates.add(c);
		}
		
		in.close();
	}
	
	public static ArrayList<Candidate> selectCandidates(String departmentName, int max) {
		int counter = 0;
		ArrayList<Candidate> selected = new ArrayList<Candidate>();
		
		while (counter < max && candidates.size() > 0) {
			Candidate c = candidates.poll();
			if (c.departmentName.equals(departmentName) && !selected.contains(c)) {
				selected.add(c);
				counter++;
			}
		}
		
		return selected;
	}
	
	public static void writeToFile(String fileName, ArrayList<Candidate> selected) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, ENCODE);
		
		for (int i = 0; i < selected.size(); i++) {
			writer.println(selected.get(i).name);
		}
		
		writer.close();
	}
	
	public static void printCandidates() {
		while (candidates.size() > 0) {
			System.out.println(candidates.poll());
		}
	}
	
	public static void printSelected(Candidate[] selected) {
		for (int i = 0; i < MAX_CANDIDATES; i++) {
			System.out.println(selected[i]);
		}
	}
}

class Candidate implements Comparable<Candidate> {

	String name;
	int writtenExamScore;
	int programmingExamScore;
	String departmentName;
	double totalScore;
	int preference;
	
	private final double writtenExamScoreRate = 0.4;
	private final double programmingExamScoreRate = 0.6;
	
	public Candidate(String name, int writtenExamScore, int programmingExamScore, String departmentName, int preference) {
		super();
		
		this.name = name;
		this.writtenExamScore = writtenExamScore;
		this.programmingExamScore = programmingExamScore;
		this.departmentName = departmentName;
		this.preference = preference;
		
		// Set total score
		setTotalScore();
	}

	/**
	 * Assumes writtenExamScore and programmingExamScore are already defined.
	 */
	private void setTotalScore() {
		this.totalScore = this.writtenExamScore * writtenExamScoreRate  + this.programmingExamScore * programmingExamScoreRate; 
	}
	
	public String toString() {
		return this.totalScore + ";" + this.name + ";" + this.writtenExamScore + ";" + this.programmingExamScore + ";" + this.departmentName + ";" + this.preference + ";";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Candidate)) {
			return false;
		}
		
		Candidate c = (Candidate) obj;
		return this.name.equals(c.name);
	}

	@Override
	public int compareTo(Candidate o) {
		// If total score is less than or equal to 1, use preference
		if (Math.abs(this.totalScore - o.totalScore) <= 1) {
			return this.preference - o.preference;
		}
		
		// If diff of total score is greater than 1, use total score.
		double diff = this.totalScore - o.totalScore;
		
		// If this.totalScore is greater than o.totalScore, that must comes before o, so return 1
		if (diff > 0) {
			return -1;
		} else {
			return 1;
		}
	}
	
}

