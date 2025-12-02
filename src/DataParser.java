package src;

import java.io.*;
import java.util.*;

/**
 * Utility class for parsing student data from a CSV-like text file.
 *
 * <p>The expected format is:</p>
 * <pre>
 * name,age,gender,year,major,gpa,roommatePrefs,internships
 * </pre>
 *
 * <p>Roommate preferences and internships are separated by semicolons within a field.</p>
 *
 * <p>Example:</p>
 * <pre>
 * Alice,20,Female,2,Computer Science,3.5,Bob;Charlie;Frank,Google
 * </pre>
 *
 * <p>Lines starting with {@code #} are ignored. A header row is allowed and skipped automatically.</p>
 *
 * <p>This class creates {@link UniversityStudent} objects and returns them
 * as a list for use in graph construction, roommate matching, referral search,
 * and multithreaded interactions.</p>
 */
public class DataParser {

    /**
     * Reads a CSV-like file and converts each line into a {@link UniversityStudent}.
     *
     * <p>Behavior details:</p>
     * <ul>
     *     <li>Lines beginning with {@code #} are skipped.</li>
     *     <li>A header row is automatically detected and skipped.</li>
     *     <li>Missing fields are padded with empty values.</li>
     *     <li>Roommate preferences and internships are split on semicolons.</li>
     * </ul>
     *
     * @param filename the path to the input file
     * @return a list of {@link UniversityStudent} objects parsed from the file
     * @throws IOException if the file cannot be read
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Detect and skip header line
                if (firstLine &&
                    line.toLowerCase().contains("name") &&
                    line.toLowerCase().contains("age")) {
                    firstLine = false;
                    continue;
                }

                firstLine = false;

                // Split CSV fields (simple split, assumes no quoted commas)
                String[] parts = line.split(",", -1);

                if (parts.length < 8) {
                    // Pad with empty fields if line has fewer than 8 columns
                    String[] padded = new String[8];
                    Arrays.fill(padded, "");
                    System.arraycopy(parts, 0, padded, 0, parts.length);
                    parts = padded;
                }

                String name = parts[0].trim();

                int age = parseInteger(parts[1]);
                String gender = parts[2].trim();
                int year = parseInteger(parts[3]);
                String major = parts[4].trim();
                double gpa = parseDouble(parts[5]);

                List<String> prefs = parseListField(parts[6]);
                List<String> internships = parseListField(parts[7]);

                UniversityStudent s = new UniversityStudent(
                        name, age, gender, year, major, gpa, prefs, internships
                );

                students.add(s);
            }
        }

        return students;
    }

    /**
     * Parses a semicolon-separated field (e.g. roommate preferences or internships)
     * into a list of strings.
     *
     * @param raw the raw field value from the CSV
     * @return a list of trimmed, non-empty tokens
     */
    private static List<String> parseListField(String raw) {
        raw = (raw == null) ? "" : raw.trim();
        if (raw.isEmpty()) return new ArrayList<>();

        String[] tokens = raw.split(";");
        List<String> output = new ArrayList<>();

        for (String t : tokens) {
            String cleaned = t.trim();
            if (!cleaned.isEmpty()) {
                output.add(cleaned);
            }
        }
        return output;
    }

    /**
     * Safely parses an integer from a string.
     * If parsing fails, returns 0.
     *
     * @param s the string to parse
     * @return the parsed integer or 0 if invalid
     */
    private static int parseInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Safely parses a double from a string.
     * If parsing fails, returns 0.0.
     *
     * @param s the string to parse
     * @return the parsed double or 0.0 if invalid
     */
    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}

