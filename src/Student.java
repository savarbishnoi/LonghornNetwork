package src;

import java.util.*;

/**
 * The {@code Student} class serves as an abstract base class for all student types
 * used in the Longhorn Network simulation. It stores core academic and personal
 * attributes such as name, age, major, GPA, roommate preferences, and internship history.
 *
 * <p>This class provides the structure for calculating connection strengths
 * between students, which is implemented by subclasses such as
 * {@link UniversityStudent}. The connection strength is used for
 * graph construction, roommate matching, and referral path finding.
 */
public abstract class Student {

    /** The name of the student. */
    protected String name;

    /** The age of the student. */
    protected int age;

    /** The gender of the student. */
    protected String gender;

    /** The academic year of the student (e.g., 1 = freshman). */
    protected int year;

    /** The major field of study of the student. */
    protected String major;

    /** The grade point average of the student. */
    protected double gpa;

    /**
     * A list of preferred roommate names, in priority order.
     * Names must correspond to other students in the dataset.
     */
    protected List<String> roommatePreferences;

    /**
     * A list of company names where the student previously completed internships.
     * Used by the referral path finder to locate connections to target companies.
     */
    protected List<String> previousInternships;

    /**
     * Calculates the connection strength between this student and another student.
     * The exact scoring rules are determined by subclasses, but typically include
     * factors such as:
     * <ul>
     *   <li>Roommate preference match</li>
     *   <li>Shared internships</li>
     *   <li>Same major</li>
     *   <li>Same age</li>
     * </ul>
     *
     * @param other the {@code Student} to compare against
     * @return an integer representing the strength of the connection,
     *         where larger values indicate stronger relationships
     */
    public abstract int calculateConnectionStrength(Student other);
}

