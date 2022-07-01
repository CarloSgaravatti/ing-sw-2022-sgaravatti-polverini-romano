package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The bag class represent the Bag during a game. It contains a list of students, which is
 * used to pick a random student that will be used during the game.
 */
public class Bag {
	private final List<Student> students;

	/**
	 * Construct an empty bag, with no students in it
	 */
	public Bag() {
		students = new ArrayList<>();
	}

	/**
	 * Method pickStudent returns a random Student that is present in the Bag
	 * @return a random student from the student list
	 * @throws EmptyBagException if the bag is empty
	 */
	public Student pickStudent() throws EmptyBagException {
		if (isEmpty()) throw new EmptyBagException();
		Random rnd = new Random();
		int indexToPick = rnd.nextInt(students.size());
		Student res = students.get(indexToPick);
		students.remove(indexToPick);
		return res;
	}

	/**
	 * Returns the students that are present in the bag
	 *
	 * @return the students that are present in the bag
	 */
	public List<Student> getStudent(){
		return this.students;
	}

	/**
	 * Method isEmpty verify if the bag contains or not a student
	 * @return true, if the bag does not contain any student, otherwise false
	 */
	public boolean isEmpty() {
		return students.isEmpty();
	}

	/**
	 * Method insertStudent insert a student in the bag's students list
	 * @param student the student to insert in the bag
	 */
	public void insertStudent(Student student) {
		students.add(student);
	}
}
