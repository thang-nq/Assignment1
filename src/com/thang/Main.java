package com.thang;

import Classes.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static int menu() {
        System.out.println("MAIN MENU");
        System.out.println("1. Add a new enrollment");
        System.out.println("2. Edit an enrollment");
        System.out.println("3. View report");
        System.out.println("4. Exit");

        return Console.validateInt("Type in your choice (1-4): ",1,4);

    }

    public static void addEnrollmentMenu(String fileName) throws IOException {
        System.out.println("----------------------------------------");
        Console.displayStudentList();
        int studentIndex = Console.validateID("Type in student ID: ",EnrolmentList.getInstance().getStudentList());
        Console.displayCourseList();
        int courseIndex = Console.validateCourseID("Type in course ID: ",EnrolmentList.getInstance().getCourseList());
        String semester = Console.semesterInput();
        StudentEnrollment newEnrollment = new StudentEnrollment(EnrolmentList.getInstance().getStudentList().get(studentIndex),
                EnrolmentList.getInstance().getCourseList().get(courseIndex),
                semester);
        EnrolmentList.getInstance().add(newEnrollment);
        FileManager.writeEnrollmentToCSV(EnrolmentList.getInstance().getAll(), fileName);
        System.out.println("Enrollment successfully!");
    }

    public static void updateEnrollmentMenu(String fileName) throws IOException {
        System.out.println("---------------------");
        Console.displayStudentList();
        int studentIndex = Console.validateID("Type in a student ID to update their enrollment",EnrolmentList.getInstance().getStudentList());
        System.out.println("All enrollment of this student: ");
        List<StudentEnrollment> enrollments = EnrolmentList.getInstance().getAllEnrollmentOfStudent(studentIndex);
        Console.displayObjectByIndex(enrollments);
        int enrollmentIndex = Console.validateInt("Your selection: ",0,enrollments.size()-1);
        String studentID = EnrolmentList.getInstance().getStudentList().get(studentIndex).getId();
        String courseID = enrollments.get(enrollmentIndex).getCourse().getId();
        String semester = enrollments.get(enrollmentIndex).getSemester();
        int enrollmentListIndex = Console.getEnrollmentIndex(studentID,courseID,semester);

        System.out.println("1. Update");
        System.out.println("2. Delete");
        int choice = Console.validateInt("Select: ");
        if (choice == 1) {
            EnrolmentList.getInstance().update(enrollmentListIndex);
        } else {
            EnrolmentList.getInstance().delete(enrollmentListIndex);
        }

        FileManager.writeEnrollmentToCSV(EnrolmentList.getInstance().getAll(), fileName);
        System.out.println("Update successfully!");
    }

    public static void reportMenu() throws IOException {
        List<String[]> data = new ArrayList<>();
        String semester;
        System.out.println("1. Report of a student");
        System.out.println("2. Report of students in a course");
        System.out.println("3. All course offered in a semester");
        int choice = Console.validateInt("Your selection (1-3): ",1,3);
        switch (choice) {
            case 1 -> {
                Console.displayStudentList();
                int studentIndex = Console.validateID("Type in student ID: ", EnrolmentList.getInstance().getStudentList());
                System.out.println("Enrolled semester:");
                List<String> semesters = EnrolmentList.getInstance().getStudentSemester(studentIndex);
                Console.displayObjectByIndex(semesters);
                int semesterIndex = Console.validateInt("Select: ", 0, semesters.size() - 1);
                semester = semesters.get(semesterIndex);
                List<Course> enrolledCourse = EnrolmentList.getInstance().getEnrolledCourse(studentIndex, semester);
                for (Course course : enrolledCourse) {
                    data.add(course.objectToString());
                }
                Console.displayObjectByIndex(enrolledCourse);
            }
            case 2 -> {
                Console.displayCourseList();
                int courseIndex = Console.validateCourseID("Type in course ID: ", EnrolmentList.getInstance().getCourseList());
                System.out.println("Semester: ");
                List<String> semesters = EnrolmentList.getInstance().getCourseSemester(courseIndex);
                int semesterIndex = Console.validateInt("Selection: ", 0, semesters.size() - 1);
                semester = semesters.get(semesterIndex);
                List<Student> students = EnrolmentList.getInstance().getEnrolledStudent(courseIndex, semester);
                for (Student student : students) {
                    data.add(student.objectToString());
                }
                Console.displayObjectByIndex(students);
            }
            default -> {
                semester = Console.semesterInput();
                List<Course> courseList = EnrolmentList.getInstance().getEnrolledCourse(semester);

                Console.displayObjectByIndex(courseList);
                for (Course course : courseList) {
                    data.add(course.objectToString());
                }
            }
        }
        if (data.isEmpty()) {
            System.out.println("Nothing to export...");
        } else {
            System.out.println("Save as csv file?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            choice = Console.validateInt("Your selection: ");
            if (choice == 1) {
                FileManager.saveAsCSVFile(data, semester);
                System.out.println("File save as " + semester + ".csv");
            } else {
                System.out.println("Returning to main menu....");
            }
        }

    }

    public static void main(String[] args) throws IOException {
	// write your code here
        //Load data from csv files
        String courseFile = "courses.csv";
        String studentFile = "students.csv";
        String enrollmentFile = "default.csv";
        List<String[]> courseData = FileManager.readFileCSV(courseFile);
        List<String[]> studentData = FileManager.readFileCSV(studentFile);
        EnrolmentList.getInstance().setCourseList(FileManager.stringToCourseObj(courseData));
        EnrolmentList.getInstance().setStudentList(FileManager.stringToStudentObj(studentData));



        System.out.println("Do you want to load any enrollment data?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int choice = Console.validateInt("Your choice: ");
        if (choice == 1) {
            boolean valid;
            do {
                enrollmentFile = Console.input("Type in your file name: ").toLowerCase();
                File temp = new File(enrollmentFile);
                valid = temp.exists();
            } while (!enrollmentFile.matches("^.*\\.(CSV)$") && !valid);

        }
        List<String[]> enrollmentData = FileManager.readFileCSV(enrollmentFile);
        EnrolmentList.getInstance().setStudentEnrollmentList(FileManager.stringToEnrollmentObj(enrollmentData));
        while (true) {
            Console.displayEnrollmentList();

            choice = menu();
            switch (choice) {
                case 1 -> addEnrollmentMenu(enrollmentFile);
                case 2 -> updateEnrollmentMenu(enrollmentFile);
                case 3 -> reportMenu();
                default -> {
                    System.out.println("shutting down...");
                    System.exit(0);
                }
            }
        }




//
//        String testFile = "test2.csv";
//        List<String[]> courseData = FileManager.readFileCSV(courseFile);
//        List<String[]> studentData = FileManager.readFileCSV(studentFile);
//        List<String[]> enrollmentData = FileManager.readFileCSV(enrollmentFile);
//
//        EnrolmentList listManage = EnrolmentList.getInstance();
//        listManage.setCourseList(FileManager.stringToCourseObj(courseData));
//        listManage.setStudentList(FileManager.stringToStudentObj(studentData));
//        listManage.setStudentEnrollmentList(FileManager.stringToEnrollmentObj(enrollmentData));
//
//        FileManager.writeEnrollmentToCSV(listManage.getAll(),testFile);
//        System.out.println("Save student list as new file?");
//        System.out.println("1. Yes");
//        System.out.println("2. No");
//        choice = Console.validateInt("Your choice (1/2): ",1,2);
//        if (choice == 1) {
//            FileManager.saveAsCSVFile(studentData,listManage.getStudentList().get(0).getId());
//        }



//
//        Console.displayEnrollmentList();
//        int index = Console.validateID("Type in id: ",listManage.getStudentList());
//        Console.displayCourseList();
//        int cindex = Console.validateCourseID("Course ID: ",listManage.getCourseList());
//        String a = Console.semesterInput();
//
//
//
//        //add new enrollment
//        StudentEnrollment enroll = new StudentEnrollment(listManage.getStudentList().get(index),listManage.getCourseList().get(cindex),a);
//        listManage.add(enroll);
//        System.out.println("Enrollment successful!");
//
//        Console.displayEnrollmentList();
//
//
//
//        //view enrolled courses
//        index = Console.validateID("Type in id: ",listManage.getStudentList());
//
//        String sem = Console.semesterInput();
//
//
//        List<Course> enrolledCourse = listManage.getEnrolledCourse(index,sem);
//
//        System.out.println("Enrolled courses in sem " + sem);
//        Console.displayObjectByIndex(enrolledCourse);
//
//        int courseInt = Console.validateInt("Choose course to update: ",0,enrolledCourse.size()-1);
//        String selectedStudentId = EnrolmentList.getInstance().getStudentList().get(index).getId();
//        String selectedCourseID = enrolledCourse.get(courseInt).getId();
//
//        //update a student enrollment
//        int enrollIndex = Console.getEnrollmentIndex(selectedStudentId,selectedCourseID,sem);
//        EnrolmentList.getInstance().update(enrollIndex);
//
//        Console.displayEnrollmentList();
//
//        index = Console.validateID("Type in id: ",listManage.getStudentList());
//        sem = Console.semesterInput();
//        enrolledCourse = listManage.getEnrolledCourse(index,sem);
//        System.out.println("Enrolled courses in sem " + sem);
//        Console.displayObjectByIndex(enrolledCourse);
//
//
//        courseInt = Console.validateInt("Choose course to delete: ",0,enrolledCourse.size()-1);
//        selectedStudentId = EnrolmentList.getInstance().getStudentList().get(index).getId();
//        selectedCourseID = enrolledCourse.get(courseInt).getId();
//        enrollIndex = Console.getEnrollmentIndex(selectedStudentId,selectedCourseID,sem);
//        EnrolmentList.getInstance().delete(enrollIndex);
//
//        Console.displayEnrollmentList();








    }
}
