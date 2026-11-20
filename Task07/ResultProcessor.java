import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

class ProcessingResult {
    final String file; final boolean success; final String message;
    ProcessingResult(String f, boolean s, String m) { file=f; success=s; message=m; }
}
class ScoreTask implements Callable<ProcessingResult> {
    private final Path file;
    ScoreTask(Path file) { this.file=file; }
    public ProcessingResult call() {
        try {
            int mark = Integer.parseInt(Files.readString(file).trim());
            if (mark < 0 || mark > 100) throw new IllegalArgumentException("Mark outside 0-100");
            String grade = mark>=90 ? "A" : mark>=75 ? "B" : mark>=60 ? "C" : mark>=50 ? "D" : "F";
            return new ProcessingResult(file.getFileName().toString(), true, "Mark="+mark+", Grade="+grade);
        } catch (IOException | IllegalArgumentException e) {
            return new ProcessingResult(file.getFileName().toString(), false, e.getMessage());
        }
    }
}
public class ResultProcessor {
    static void createSamples(Path dir) throws IOException {
        Files.createDirectories(dir);
        try (var stream = Files.list(dir)) { if (stream.findAny().isPresent()) return; }
        Files.writeString(dir.resolve("student01.txt"), "95");
        Files.writeString(dir.resolve("student02.txt"), "82");
        Files.writeString(dir.resolve("student03.txt"), "67");
        Files.writeString(dir.resolve("student04.txt"), "45");
        Files.writeString(dir.resolve("student05.txt"), "corrupt");
    }
    public static void main(String[] args) throws Exception {
        Path dir = Paths.get(args.length>0 ? args[0] : "score-files");
        int pool = args.length>1 ? Integer.parseInt(args[1]) : 4;
        if (pool!=1 && pool!=2 && pool!=4 && pool!=8)
            throw new IllegalArgumentException("Pool size must be 1, 2, 4 or 8");
        createSamples(dir);
        List<Path> files = new ArrayList<>();
        try (var stream=Files.list(dir)) { stream.filter(Files::isRegularFile).sorted().forEach(files::add); }
        ExecutorService executor = Executors.newFixedThreadPool(pool);
        List<Future<ProcessingResult>> futures = new ArrayList<>();
        ConcurrentLinkedQueue<ProcessingResult> results = new ConcurrentLinkedQueue<>();
        long start = System.nanoTime();
        try {
            for (Path file:files) futures.add(executor.submit(new ScoreTask(file)));
            for (Future<ProcessingResult> future:futures) {
                try { results.add(future.get()); }
                catch (ExecutionException e) { results.add(new ProcessingResult("unknown",false,e.getCause().toString())); }
            }
        } finally { executor.shutdown(); }
        long elapsed=(System.nanoTime()-start)/1_000_000;
        int ok=0, failed=0;
        for (ProcessingResult r:results) {
            System.out.println((r.success ? "SUCCESS " : "FAILURE ")+r.file+": "+r.message);
            if (r.success) ok++; else failed++;
        }
        System.out.println("Pool size: "+pool);
        System.out.println("Successful files: "+ok);
        System.out.println("Failed files: "+failed);
        System.out.println("Total files processed: "+(ok+failed));
        System.out.println("Elapsed time (ms): "+elapsed);
    }
}