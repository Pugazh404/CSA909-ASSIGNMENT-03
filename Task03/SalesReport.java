import java.io.*;
import java.nio.file.*;
import java.util.*;
public class SalesReport {
    static void generate(Path input, Path output) throws IOException {
        double total = 0;
        int processed = 0;
        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("DAILY SALES SUMMARY"); writer.newLine();
            try (var paths = Files.list(input)) {
                for (Path file : paths.filter(Files::isRegularFile).sorted().toList()) {
                    double storeTotal = 0;
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null)
                            if (!line.isBlank()) storeTotal += Double.parseDouble(line.trim());
                    } catch (IOException | NumberFormatException e) {
                        writer.write("ERROR " + file.getFileName() + ": " + e.getMessage());
                        writer.newLine();
                        System.err.println("Failed file " + file + ": " + e.getMessage());
                        continue;
                    }
                    writer.write(file.getFileName() + " = " + storeTotal); writer.newLine();
                    total += storeTotal; processed++;
                }
            }
            writer.write("Stores processed = " + processed); writer.newLine();
            writer.write("Grand total = " + total); writer.newLine();
        }
    }
    public static void main(String[] args) throws IOException {
        Path input = Paths.get(args.length > 0 ? args[0] : "stores");
        Path output = Paths.get(args.length > 1 ? args[1] : "sales-summary.txt");
        if (!Files.exists(input)) {
            Files.createDirectories(input);
            Files.writeString(input.resolve("store01.txt"), "1200.50\n800\n");
            Files.writeString(input.resolve("store02.txt"), "950\n400.25\n");
            Files.writeString(input.resolve("store03.txt"), "700\n");
        }
        generate(input, output);
        System.out.println("Report generated: " + output.toAbsolutePath());
        System.out.println(Files.readString(output));
    }
}