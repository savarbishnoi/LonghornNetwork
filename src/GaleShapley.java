package src;

import java.util.*;

/**
 * Implements a simplified version of the Gale–Shapley algorithm to assign
 * stable roommate pairs based on students' roommate preference lists.
 *
 * <p>This implementation works as a proposer–receiver model:</p>
 * <ul>
 *     <li>Each student with a non-empty preference list becomes a proposer.</li>
 *     <li>Students propose to their preferred roommates in order.</li>
 *     <li>A candidate student accepts the best proposal according to their
 *         own preference list, potentially breaking an existing match.</li>
 *     <li>Students with empty preference lists remain unpaired.</li>
 * </ul>
 *
 * <p>When two students are matched, the algorithm sets their roommate fields
 * reciprocally via {@link UniversityStudent#setRoommate(UniversityStudent)}.</p>
 *
 * <p>At the end, a sanity check ensures all roommate assignments are mutual.</p>
 */
public class GaleShapley {

    /**
     * Performs the stable roommate assignment using a Gale–Shapley-style process.
     *
     * <p>Behavior summary:</p>
     * <ul>
     *     <li>Students with no preferences are excluded from the proposal queue.</li>
     *     <li>Each proposer moves down their preference list until matched or exhausted.</li>
     *     <li>If a candidate prefers a new proposer over their current match,
     *         they "break up" with their current roommate.</li>
     *     <li>All roommate assignments are enforced to be reciprocal at the end.</li>
     * </ul>
     *
     * @param students the list of students participating in the roommate matching phase
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        if (students == null) return;

        // Map names to student objects for quick lookups
        Map<String, UniversityStudent> byName = new HashMap<>();
        for (UniversityStudent s : students) {
            byName.put(s.name, s);
        }

        // Tracks next preference index for each proposer
        Map<UniversityStudent, Integer> nextIndex = new HashMap<>();

        // Queue of students who still need to propose
        Queue<UniversityStudent> free = new ArrayDeque<>();

        // Initialize data structures
        for (UniversityStudent s : students) {
            nextIndex.put(s, 0);
            s.setRoommate(null); // clear previous roommate

            if (s.roommatePreferences != null && !s.roommatePreferences.isEmpty()) {
                free.add(s); // only students with preferences propose
            }
        }

        // Main Gale–Shapley loop
        while (!free.isEmpty()) {
            UniversityStudent proposer = free.poll();
            List<String> prefs = proposer.roommatePreferences;

            if (prefs == null || nextIndex.get(proposer) >= prefs.size()) {
                // No remaining options
                continue;
            }

            // Get next candidate name and increment index
            String candidateName = prefs.get(nextIndex.get(proposer));
            nextIndex.put(proposer, nextIndex.get(proposer) + 1);

            // Look up candidate student
            UniversityStudent candidate = byName.get(candidateName);
            if (candidate == null) {
                // If name not found, move to next preference
                if (nextIndex.get(proposer) < prefs.size()) {
                    free.add(proposer);
                }
                continue;
            }

            UniversityStudent current = candidate.getRoommate();

            if (current == null) {
                // Candidate is free → match them
                proposer.setRoommate(candidate);
                candidate.setRoommate(proposer);
            } else {
                // Candidate compares proposer and current roommate
                int idxProposer = indexOfPreference(candidate, proposer.name);
                int idxCurrent = indexOfPreference(candidate, current.name);

                if (idxProposer < idxCurrent) {
                    // Candidate prefers proposer → switch roommates
                    current.setRoommate(null);

                    proposer.setRoommate(candidate);
                    candidate.setRoommate(proposer);

                    // The old partner goes back into the free queue if they still have options
                    if (nextIndex.get(current) <
                        (current.roommatePreferences == null ? 0 : current.roommatePreferences.size())) {
                        free.add(current);
                    }

                } else {
                    // Candidate rejects proposer → proposer tries next option
                    if (nextIndex.get(proposer) < prefs.size()) {
                        free.add(proposer);
                    }
                }
            }
        }

        // Final mutual-consistency check
        for (UniversityStudent s : students) {
            UniversityStudent r = s.getRoommate();
            if (r != null && r.getRoommate() != s) {
                s.setRoommate(null);
            }
        }
    }

    /**
     * Returns the index of a name in a student's preference list.
     * If the name is not found, returns {@code Integer.MAX_VALUE}.
     *
     * @param student the student whose preferences are being searched
     * @param name the name to locate
     * @return the index of the name in the preference list, or
     *         {@code Integer.MAX_VALUE} if not present
     */
    private static int indexOfPreference(UniversityStudent student, String name) {
        if (student.roommatePreferences == null) return Integer.MAX_VALUE;

        for (int i = 0; i < student.roommatePreferences.size(); i++) {
            if (student.roommatePreferences.get(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }
}

