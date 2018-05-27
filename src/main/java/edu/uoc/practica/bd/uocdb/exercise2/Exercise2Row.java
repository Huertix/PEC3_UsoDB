package edu.uoc.practica.bd.uocdb.exercise2;

public class Exercise2Row {
	private int childId;
	private String childName = "";
	private String childCity = "";
	private int totalNumToys;
	private int totalNumLetters;
	private int maxNumToysInLetter;
	private int mostAskedToy;

	public Exercise2Row(int childId, String childName, String childCity, int totalNumToys, int totalNumLetters,
			int maxNumToysInLetter, int mostAskedToy) {
		this.childId = childId;
		this.childName = childName;
		this.childCity = childCity;
		this.totalNumToys = totalNumToys;
		this.totalNumLetters = totalNumLetters;
		this.maxNumToysInLetter = maxNumToysInLetter;
		this.mostAskedToy = mostAskedToy;
	}

	public int getChildId() {
		return childId;
	}

	public String getChildName() {
		return childName;
	}

	public String getChildCity() {
		return childCity;
	}

	public int getTotalNumToys() {
		return totalNumToys;
	}

	public int getTotalNumLetters() {
		return totalNumLetters;
	}

	public int getMaxNumToysInLetter() {
		return maxNumToysInLetter;
	}

	public int getMostAskedToy() {
		return mostAskedToy;
	}
}