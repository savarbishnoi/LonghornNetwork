package src;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a university-level student in the Longhorn Network simulation.
 * This class extends the abstract {@link Student} class by adding functionality
 * related to social interactions, roommate assignment, friendships, and chat history.
 *
 * <p>Instances of this class are also used to build graph connections
 * (via {@code StudentGraph}) and to participate in roommate matching
 * (via {@code GaleShapley}). Students can exchange friend requests and chat messages
 * through multithreaded components in the system.</p>
 */
public class UniversityStudent extends Student {

    /** The assigned roommate of this student, or {@code null} if none. */
    private UniversityStudent roommate;

    /** A thread-safe set of friends connected to this student. */
    private Set<UniversityStudent> friends;

    /** A thread-safe list maintaining chat messages received or sent by the student. */
    private List<String> chatHistory;

    /**
     * Constructs a fully defined {@code UniversityStudent} with academic information,
     * roommate preferences, and internship history.
     *
     * @param name the student's name
     * @param age the student's age
     * @param gender the student's gender
     * @param year the student's academic year
     * @param major the student's major
     * @param gpa the student's GPA
     * @param roommatePreferences a list of preferred roommate names
     * @param previousInternships a list of internship companies the student has worked at
     */
    public UniversityStudent(String name, int age, String gender, int year,
                             String major, double gpa,
                             List<String> roommatePreferences,
                             List<String> previousInternships) {

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;

        // Defensive copies to protect internal structure
        this.roommatePreferences = (roommatePreferences == null)
                ? new ArrayList<>()
                : new ArrayList<>(roommatePreferences);

        this.previousInternships = (previousInternships == null)
                ? new ArrayList<>()
                : new ArrayList<>(previousInternships);

        this.friends = Collections.synchronizedSet(new HashSet<>());
        this.chatHistory = new CopyOnWriteArrayList<>();
        this.roommate = null;
    }

    /**
     * Constructs a simplified student with only a name.
     * Primarily used for fallback or debugging scenarios.
     *
     * @param name the student's name
     */
    public UniversityStudent(String name) {
        this(name, 0, "", 0, "", 0.0, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Calculates the connection strength between this student and another student.
     * The score is based on:
     * <ul>
     *     <li>Roommate preference match (+4)</li>
     *     <li>Shared internships (+3 each)</li>
     *     <li>Same major (+2)</li>
     *     <li>Same age (+1)</li>
     * </ul>
     *
     * @param other the student being compared to this one
     * @return a positive integer representing connection strength,
     *         or 0 if the other student is not a {@code UniversityStudent}
     */
    @Override
    public int calculateConnectionStrength(Student other) {
        if (!(other instanceof UniversityStudent)) return 0;
        UniversityStudent o = (UniversityStudent) other;

        int score = 0;

        // Roommate preference match
        if (this.roommatePreferences != null && this.roommatePreferences.contains(o.name)) {
            score += 4;
        }

        // Shared internships
        if (this.previousInternships != null && o.previousInternships != null) {
            for (String c : this.previousInternships) {
                if (c == null || c.equalsIgnoreCase("None")) continue;
                for (String oc : o.previousInternships) {
                    if (oc == null || oc.equalsIgnoreCase("None")) continue;
                    if (c.equalsIgnoreCase(oc)) {
                        score += 3;
                    }
                }
            }
        }

        // Same major
        if (this.major != null && o.major != null
                && !this.major.isEmpty()
                && this.major.equalsIgnoreCase(o.major)) {
            score += 2;
        }

        // Same age
        if (this.age > 0 && o.age > 0 && this.age == o.age) {
            score += 1;
        }

        return score;
    }

    /**
     * Returns the student’s currently assigned roommate.
     *
     * @return the roommate, or {@code null} if none assigned
     */
    public UniversityStudent getRoommate() {
        return roommate;
    }

    /**
     * Assigns a roommate to this student.
     *
     * @param roommate the student to set as the roommate
     */
    public void setRoommate(UniversityStudent roommate) {
        this.roommate = roommate;
    }

    /**
     * Retrieves this student’s set of friends.
     * The set is synchronized for thread-safety.
     *
     * @return a thread-safe set of friends
     */
    public Set<UniversityStudent> getFriends() {
        return friends;
    }

    /**
     * Adds a friend to this student’s social network.
     *
     * @param s the student to add as a friend
     */
    public void addFriend(UniversityStudent s) {
        if (s == null || s == this) return;
        friends.add(s);
    }

    /**
     * Returns the chat history of this student.
     * This list is thread-safe for use in concurrent chat threads.
     *
     * @return a list of chat messages
     */
    public List<String> getChatHistory() {
        return chatHistory;
    }

    /**
     * Appends a chat message to this student's chat history.
     *
     * @param msg the message to append
     */
    public void addChatMessage(String msg) {
        chatHistory.add(msg);
    }

    /**
     * Returns the student's previous internship companies.
     *
     * @return a list of internship names
     */
    public List<String> getPreviousInternships() {
        return previousInternships;
    }

    /**
     * Returns a string representation of the student containing their profile data.
     *
     * @return formatted student information
     */
    @Override
    public String toString() {
        return String.format(
                "UniversityStudent{name='%s', age=%d, gender=%s, year=%d, major=%s, gpa=%.2f, prefs=%s, internships=%s}",
                name, age, gender, year, major, gpa, roommatePreferences, previousInternships
        );
    }

    /**
     * Determines equality between this student and another object.
     * Students are considered equal if their names match.
     *
     * @param o the object to compare
     * @return {@code true} if the students share the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniversityStudent that = (UniversityStudent) o;
        return Objects.equals(name, that.name);
    }

    /**
     * Computes a hash code for this student, based on their name.
     *
     * @return a hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

