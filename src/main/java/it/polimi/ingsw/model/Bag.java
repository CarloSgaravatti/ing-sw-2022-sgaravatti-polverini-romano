package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
	private final List<Student> students;

	public Bag() {
		students = new ArrayList<>();
	}

	public Student pickStudent() throws EmptyBagException {
		if (isEmpty()) throw new EmptyBagException();
		Random rnd = new Random();
		int indexToPick = rnd.nextInt(students.size());
		Student res = students.get(indexToPick);
		students.remove(indexToPick);
		return res;
	}

	//just for tests
	public List<Student> getStudent(){
		return this.students;
	}

	public boolean isEmpty() {
		return students.isEmpty();
	}

	public void insertStudent(Student s) {
		students.add(s);
	}
}
