import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Loads an output file with JFileChooser and displays its five-column statistics in a JTable. */
public final class BenchmarkReport {
    private BenchmarkReport() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Path input;
                if (args.length > 0 && !args[0].equals("--screenshot")) input = Paths.get(args[0]);
                else {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Choose sorting benchmark data file");
                    if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
                    input = chooser.getSelectedFile().toPath();
                }
                List<Object[]> statistics = readStatistics(input);
                JFrame frame = createFrame(input, statistics);
                frame.setVisible(true);
                // Optional deterministic capture of the actual running JTable window.
                if (args.length >= 3 && "--screenshot".equals(args[1])) {
                    Path screenshot = Paths.get(args[2]);
                    Timer timer = new Timer(1200, e -> {
                        try {
                            Rectangle bounds = frame.getBounds();
                            ImageIO.write(new Robot().createScreenCapture(bounds), "png", screenshot.toFile());
                            System.out.println("Screenshot saved: " + screenshot);
                        } catch (Exception ex) { ex.printStackTrace(); }
                        frame.dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static JFrame createFrame(Path input, List<Object[]> rows) {
        String label = input.getFileName().toString().replace('_', ' ');
        JFrame frame = new JFrame("CMSC 451 Benchmark Report - " + label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] columns = {"Size", "Avg Count", "Coef Count", "Avg Time (ns)", "Coef Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Object[] row : rows) model.addRow(row);
        JTable table = new JTable(model);
        table.setRowHeight(27);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel content = new JPanel(new BorderLayout());
        content.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(content);
        frame.setPreferredSize(new Dimension(770, 440));
        frame.pack();
        frame.setLocation(65, 65);
        return frame;
    }

    private static List<Object[]> readStatistics(Path input) throws IOException {
        List<Object[]> tableRows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(input)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length != 81) {
                    throw new IOException("Expected 1 size and 40 count/time pairs; got " + tokens.length);
                }
                double[] counts = new double[40], times = new double[40];
                for (int i = 0; i < 40; i++) {
                    counts[i] = Long.parseLong(tokens[1 + 2 * i]);
                    times[i] = Long.parseLong(tokens[2 + 2 * i]);
                }
                tableRows.add(new Object[]{Integer.parseInt(tokens[0]),
                        format(mean(counts)), format(coefficientOfVariation(counts)) + "%",
                        format(mean(times)), format(coefficientOfVariation(times)) + "%"});
            }
        }
        return tableRows;
    }

    private static double mean(double[] values) {
        double total = 0;
        for (double value : values) total += value;
        return total / values.length;
    }

    // Sample standard deviation divided by the mean, expressed in percent.
    private static double coefficientOfVariation(double[] values) {
        double average = mean(values);
        if (average == 0) return 0;
        double squares = 0;
        for (double value : values) squares += (value - average) * (value - average);
        return 100.0 * Math.sqrt(squares / (values.length - 1)) / average;
    }

    private static String format(double value) { return String.format(Locale.US, "%.2f", value); }
}
