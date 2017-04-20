package com.androidweardocs.wearablemessage;

import junit.framework.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by michael on 4/19/17.
 */

public class StringData {
    public String STRING_DATA;

    public StringData () {
        STRING_DATA = "Computer Science 256/L - Fact sheet\n"+
            "Fall 2016\n" +
            "Instructor:\n" +
            "Richard Lorentz\n" +
            "e-mail: lorentz@csun.edu\n" +
            "http://www.csun.edu/~lorentz/\n" +
            "Office:\n" +
            "Jacaranda Hall 4403.\n" +
            "phone: 677-3388\n" +
            "Office Hours:\n" +
            "Mondays, 1:00 - 2:00.\n" +
            "Tuesdays, 5:00 - 6:00.\n" +
            "Other times by appointment.\n" +
            "Texts:\n" +
            "Discrete Mathematics, by Kenneth A. Ross and Charles R. B. Wright., Prentice Hall,\n" +
            "ISBN: 0-13-065247-4. The plan is to cover Chapters 1, 3, 4, 5, and 6 in great detail and\n" +
            "bits and pieces of the remainder of the book as time allows.\n" +
            "Course Objectives:\n" +
            "The purpose of this class is to teach you many of the mathematical structures that are\n" +
            "commonly used in Computer Science and how to deal with these structures formally. You\n" +
            "will learn how to be rigorous and precise when dealing with these topics. The official\n" +
            "departmental learning objectives for this class state that a successful student will: (1) Be\n" +
            "able to demonstrate knowledge of basic terms and operations associated with sets,\n" +
            "functions, and relations such as the basic algebra of sets and logic and basic properties of\n" +
            "arbitrary functions. (2) Study various proof techniques and problems appropriate to them;\n" +
            "(3) Study how the ideas of mathematical induction relate to recursion and recursively\n" +
            "defined structures; (4) Be able to solve counting problems involving combinatorics and\n" +
            "permutations; (5) Learn the basic definitions, theorems, and algorithms of graph theory,\n" +
            "and be able to apply them to specific graphs; (6) Learn basic algorithms for traversing\n" +
            "trees and be able to apply them to specific trees.\n" +
            "The lab will provide the opportunity to practice some of the abstract ideas in a more\n" +
            "concrete setting, e.g., in the form of programs or problem assignments. The official\n" +
            "departmental learning objectives for the lab state that a successful student will: (1) Learn\n" +
            "how to relate the basic properties of functions and relations to the functional paradigm;\n" +
            "(2) Be able to demonstrate the use of various proof techniques and problems appropriate\n" +
            "to them, including relating mathematical induction to recursion and recursively defined\n" +
            "structures; (3) Learn to model problems in Computer Science using combinatorics and\n" +
            "permutations; (4) Be able to model problems in Computer Science using graphs, trees,\n" +
            "and traversal methods. In practice, a large part of the lab time will be spent either going\n" +
            "over problem assignments that come mostly from the textbook or taking and going over\n" +
            "quizzes. Some simple programming assignments might also be worked on in the lab.\n" +
            "Grading:\n" +
            "Even though there are technically two different classes corresponding to the lecture and\n" +
            "the lab, you will receive the same grade for both portions. This means whatever grade you earn will be recorded for both the lecture and the lab classes.\n" +
            "Grading will be done using the \"plus/minus\" system. 30% of your grade will be based on\n" +
            "lab activities such as problem sets, quizzes, and programming assignments. There will be\n" +
            "two midterm exams, each worth 20% of your final grade for this class. The final exam\n" +
            "will be worth 30% of your class grade. Although it is natural to discuss homework and\n" +
            "programming assignments with your fellow students, you must still be sure that the work\n" +
            "you turn in is your own. For example, seeing two programming assignments that have\n" +
            "components that are identical except for the names of some variables indicates that some\n" +
            "cheating has occurred. There should be no sharing of code. Do not accept or provide\n" +
            "code, whether it be an entire program, a single method, or even a few helpful lines. Do\n" +
            "not share homework problem solutions. There are many problems in the book that you\n" +
            "can work on with your fellow students without having to work together on assigned\n" +
            "problems. Do not copy code or problem answers from the Internet. If you get ideas for\n" +
            "programs or problem solutions from the Internet, be sure to properly cite the source.\n" +
            "Doing otherwise is plagiarism. Cheating of any kind on homework, programming\n" +
            "assignments, or exams will not be tolerated. Any incident of cheating will result in an\n" +
            "automatic F in the class and will be reported to the Dean of Students, the Associate Dean\n" +
            "of CECS, and the chair of the Computer Science Department.\n" +
            "Exams:\n" +
            "The two midterm exams and the final will be closed book and notes. The first exam will\n" +
            "be given around the sixth week of classes, most likely on Thursday, October 6 and the\n" +
            "second will be given around the eleventh week, most likely on Thursday, November 10.\n" +
            "The final is scheduled for Thursday, December 15, 3:00 - 5:00 p.m. in our regular\n" +
            "classroom, JD 3520.";

    }

    public String compress() throws FileNotFoundException, IOException, ClassNotFoundException {
        // even number of characters
        //Huffman.compress(STRING_DATA);
        //Assert.assertEquals("some", Huffman.expand());

        return STRING_DATA;//Huffman.STROMG;
    }
}
