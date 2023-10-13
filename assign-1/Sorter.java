import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;




public class Sorter {
    private static boolean checkParametersCount(String[] args) {
        if (args.length != 4 || args[0].equals("help") ) {
            System.out.println("Usage: java Sorter <input file> <number of entries> <mode> <output file>");
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

    private static boolean checkEntriesCount(int entriesCount) {
        if (entriesCount <= 0) {
            System.out.println("ERROR: number of entries must be positive.");
            return false;
        }
        return true;
    }

    private static boolean checkValidMode(String mode) {
        String[] validModes = new String[] {"name", "count", "nameThenCount", "countThenName"};

        boolean isValidMode = false;

        for (String validMode : validModes) {
            if (validMode.equals(mode)) {
                isValidMode = true;
                break;
            }
        }

        if (!isValidMode) {
            System.out.println("ERROR: mode must be name, count, nameThenCount, or countThenName.");
            return false;
        }

        return true;
    }

    private static int compareName(String[] itemA, String[] itemB) {
        return itemA[0].compareTo(itemB[0]);
    }

    private static int compareCount(String[] itemA, String[] itemB) {
        if (Integer.parseInt(itemA[1]) < Integer.parseInt(itemB[1])) {
            return -1;
        } else if (Integer.parseInt(itemA[1]) == Integer.parseInt(itemB[1])) {
            return 0;
        } else {
            return 1;
        }
    }

    private static int compareNameThenCount(String[] itemA, String[] itemB) {
        if (compareName(itemA, itemB) < 0) {
            return -1;
        } else if (compareName(itemA, itemB) == 0) {
            return compareCount(itemA, itemB);
        } else {
            return 1;
        }
    }

    private static int compareCountThenName(String[] itemA, String[] itemB) {
        if (compareCount(itemA, itemB) < 0) {
            return -1;
        } else if (compareCount(itemA, itemB) == 0) {
            return compareName(itemA, itemB);
        } else {
            return 1;
        }
    }

    private static List<String[]> mergeSort(List<String[]> left, List<String[]> right, String mode) {
        if (left == null || left.size() == 0) {
            return right;
        } else if (right == null || right.size() == 0) {
            return left;
        }

        int leftLen = left.size();
        int preEnd = leftLen >> 1;

        List<String[]> newLeft = mergeSort(left.subList(0, preEnd), left.subList(preEnd, leftLen), mode);

        int rightLen = right.size();
        preEnd = rightLen >> 1;
        List<String[]> newRight = mergeSort(right.subList(0, preEnd), right.subList(preEnd, rightLen), mode);

        List<String[]> ans = new ArrayList<String[]>();

        int l = 0, r = 0;
        while (l < leftLen && r < rightLen) {
            if (mode.equals("name")) {
                if (compareName(newLeft.get(l), newRight.get(r)) < 0) {
                    ans.add(newLeft.get(l));
                    l++;
                } else {
                    ans.add(newRight.get(r));
                    r++;
                }
            } else if (mode.equals("count")) {
                if (compareCount(newLeft.get(l), newRight.get(r)) < 0) {
                    ans.add(newLeft.get(l));
                    l++;
                } else {
                    ans.add(newRight.get(r));
                    r++;
                }
            } else if (mode.equals("nameThenCount")) {
                if (compareNameThenCount(newLeft.get(l), newRight.get(r)) < 0) {
                    ans.add(newLeft.get(l));
                    l++;
                } else {
                    ans.add(newRight.get(r));
                    r++;
                }
            } else if (mode.equals("countThenName")) {
                if (compareCountThenName(newLeft.get(l), newRight.get(r)) < 0) {
                    ans.add(newLeft.get(l));
                    l++;
                } else {
                    ans.add(newRight.get(r));
                    r++;
                }
            }
        }

        if (l < leftLen) {
            ans.addAll(newLeft.subList(l, leftLen));
        }

        if (r < rightLen) {
            ans.addAll(newRight.subList(r, rightLen));
        }

        return ans;
    }

    private static void createOutputFile(List<String[]> list, int entries, String outputFileName) {
        if (list == null || list.isEmpty() || entries <= 0) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (int i = 0; i < Math.min(entries, list.size()); i++) {
                String[] item = list.get(i);
                writer.write(item[0] + ',' + item[1]);
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

        // Check 3: check number of entries
        if (!checkEntriesCount(entriesCount)) {
            return;
        }

        // Check 4: If the mode parameter is invalid
        if (!checkValidMode(mode)) {
            return;
        }


        int listLen = rawList.size();
        int preEnd = listLen >> 1;

        List<String[]> sortedList = mergeSort(rawList.subList(0, preEnd), rawList.subList(preEnd + 1, listLen), mode);

        createOutputFile(sortedList, entriesCount, outputFileName);

        return;
    }
}