package src;

import java.util.concurrent.Semaphore;

/**
 * A runnable task that simulates sending a chat message between two
 * {@link UniversityStudent} objects. This class uses a shared static semaphore
 * to prevent interleaving of chat history updates and console output when
 * multiple chat threads run concurrently.
 *
 * <p>This serves as part of the concurrency demonstration in the Longhorn
 * Network simulation, ensuring thread-safe interaction between students.</p>
 */
public class ChatThread implements Runnable {

    /**
     * A static semaphore shared by all chat threads to synchronize
     * message creation, chat history updates, and printing.
     */
    private static final Semaphore sem = new Semaphore(1);

    /** The student sending the message. */
    private final UniversityStudent from;

    /** The student receiving the message. */
    private final UniversityStudent to;

    /** The text content of the chat message. */
    private final String message;

    /**
     * Constructs a new {@code ChatThread} representing a chat message
     * from one student to another.
     *
     * @param from the sender of the message
     * @param to the receiver of the message
     * @param message the textual message being sent
     */
    public ChatThread(UniversityStudent from, UniversityStudent to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    /**
     * Executes the chat operation. The method:
     * <ol>
     *     <li>Acquires the semaphore to ensure exclusive access.</li>
     *     <li>Creates a formatted message record.</li>
     *     <li>Appends the message to both students' chat histories.</li>
     *     <li>Simulates network delay using {@code Thread.sleep()}.</li>
     *     <li>Prints the message to the console.</li>
     *     <li>Releases the semaphore.</li>
     * </ol>
     *
     * <p>Interrupted threads will restore the interrupt flag and still
     * release the semaphore safely.</p>
     */
    @Override
    public void run() {
        try {
            sem.acquire();

            String record = String.format("%s -> %s: %s", from.name, to.name, message);

            // Add message to both chat histories
            from.addChatMessage(record);
            to.addChatMessage(record);

            // Simulate delay
            Thread.sleep(30);

            System.out.println("Chat sent: " + record);

        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt(); // Preserve interrupt state
        } finally {
            sem.release();
        }
    }
}

