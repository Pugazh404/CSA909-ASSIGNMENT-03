import java.io.IOException;
class ExamException extends Exception {
    ExamException(String m) { super(m); }
    ExamException(String m, Throwable cause) { super(m, cause); }
}
class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String m, Throwable cause) { super(m, cause); }
}
class RegistrationException extends ExamException {
    RegistrationException(String m) { super(m); }
}
class WindowClosedException extends ExamException {
    WindowClosedException(String m) { super(m); }
}
class Storage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) throw new IOException("Disk quota exceeded");
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // TODO 1 completed: retain the original IOException as the cause.
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}
class ExamService {
    private final Storage storage = new Storage();
    void submit(String file, long size, boolean registered, boolean open) throws ExamException {
        if (!open) throw new WindowClosedException("Exam window is closed");
        if (!registered) throw new RegistrationException("Student is not registered");
        storage.save(file, size);
    }
}
public class ExamPortalDemo {
    public static void main(String[] args) {
        ExamService service = new ExamService();
        try {
            service.submit("answer01.pdf", 120, true, true);
            service.submit("answer02.pdf", 800, true, true);
        } catch (ExamException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
            e.printStackTrace(System.out);
        }
    }
}