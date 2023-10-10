import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;




public class BinarySearchTree {
    private static boolean checkParametersCount(String[] args) {
        if (args[0].equals("help") || args[0].equals("h") || args.length != 4) {
            System.out.println("Usage: java BinarySearchTree <input file name> <output file name>");
            return false;
        }
        return true;
    }

    private static List<String[]> checkOpenFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            List<String[]> inputList = new ArrayList<String[]>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineArray = line.split(",");
                inputList.add(lineArray);
            }
            bufferedReader.close();
            return inputList;
        } catch (IOException e) {
            System.out.println("ERROR: could not open input file.");
            return null;
        }

    }

    private class Entry implements Comparable<Entry> {
        String name;
        Integer count;
        private Entry (String name, Integer count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public int compareTo(Entry o) {
            return this.name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "name='" + name + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    private static void createOutputFile(List<String> list, String outputFileName) {
        if (list == null || list.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (int i = 0; i < list.size(); i++) {
                String item = list.get(i);
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Check 1: check parameters count
        if (!checkParametersCount(args)) {
            return;
        }

        String fileName = args[0];
        int entriesCount = Integer.parseInt(args[1]);
        String mode = args[2];
        String outputFileName = args[3];

        // Check 2: check file can read
        List<String[]> rawList = checkOpenFile(fileName);
        if (rawList == null) {
            return;
        }

        List<String> outputList = new ArrayList<>();
        createOutputFile(outputList, outputFileName);

        return;
    }
}