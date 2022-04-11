package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloudTest extends TestCase {
    Cloud cloud;
    @BeforeEach
    void setup(){
        int numStudents = 3;
        cloud = new Cloud(numStudents);
    }

    @Test
    void pickStudentsTest() {
        Student[] students = new Student[3];
        for(int i = 0; i< 3; i++){
            students[i]=new Student(RealmType.RED_DRAGONS);
        }
        try {
            cloud.insertStudents(students);
            Assertions.assertEquals(students.length, cloud.getStudentsNumber());
        }catch (Exception StudentsNumberInCloudException){
            Assertions.fail();
        }
        try {
            Assertions.assertSame(students,cloud.pickStudents());
        }catch (Exception StudentsNumberInCloudException){
            Assertions.fail();
        }
    }

}