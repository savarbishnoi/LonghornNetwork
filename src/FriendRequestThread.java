package src;

import java.util.concurrent.Semaphore;

/**
 * A runnable task that simulates sending a friend request between two
 * {@link UniversityStudent} objects. This class uses a static semaphore to
 * serialize access to the critical section so that friend relationships
 * are updated in a thread-safe manner during concurrent execution.
 *
 * <p>The purpose of this class is to demonstrate concurrency control
 * within the Longhorn Network simulation.</p>
 */
public class FriendRequestThread implements Runnable {

    /**
     * A static semaphore shared across all friend request threads.
     * Ensures that only one thread at a time can update social relationships.
     */
    private static final Semaphore sem = new Semaphore(1);

    /** The student sending the friend request. */
    private final UniversityStudent from;

    /** The student receiving the friend request. */
    private final UniversityStudent to;

    /**
     * Constructs a new {@code FriendRequestThread}.
     *
     * @param from the initiating student of the friend request
     * @param to the student who will receive and accept the friend request
     */
    public FriendRequestThread(UniversityStudent from, UniversityStudent to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Executes the friend request operation. The method:
     * <ol>
     *     <li>Acquires the semaphore to enter the critical section.</li>
     *     <li>Simulates a short processing delay.</li>
     *     <li>Adds each student to the other's friend list, ensuring reciprocity.</li>
     *     <li>Releases the semaphore upon completion.</li>
     * </ol>
     *
     * <p>If the thread is interrupted during execution, it restores the interrupted status
     * and continues to release the semaphore safely.</p>
     */
    @Override
    public void run() {
        try {
            sem.acquire();
            // Simulate processing delay
            Thread.sleep(50);

            // Update both students' friend lists
            from.addFriend(to);
            to.addFriend(from);

            System.out.println("FriendRequest: " + from.name + " and " + to.name + " are now friends.");
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt(); // Preserve interrupt flag
        } finally {
            sem.release();
        }
    }
}


