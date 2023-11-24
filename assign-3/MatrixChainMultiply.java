import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MatrixChainMultiply {

    public MatrixChainMultiply() {

    }

    private static boolean checkParametersCount(String[] args) {
        if (args.length != 1 || args[0].equals("--help") || args[0].equals("-h")) {
            System.out.println("Usage: java MatrixChainMultiply <input file>");
            return false;
        }
        return true;
    }

    private static List<String> checkAndOpenFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            List<String> inputList = new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                inputList.add(line);
            }
            bufferedReader.close();
            return inputList;
        } catch (IOException e) {
            System.out.println("Failed to open input file.");
            return null;
        }

    }

    private static int[] checkMultiplicationValid(List<String> inputList) {
        try {
            int n = inputList.size();
            if (n <= 1) throw new Exception("input size invalid");

            // matrix A[i] has dimension p[i] * p[i + 1] for i = 0 ... n - 1
            int[] p = new int[n + 1];
            int[][] inputDimension = new int[n][2];
            int preColCount = Integer.MIN_VALUE;
            for (int k = 0; k < n; k++) {
                String[] inputLine = inputList.get(k).split(" ");
                if (inputLine.length != 3) throw new Exception("invalid matrix value");
                inputDimension[k][0] = Integer.parseInt(inputLine[1]);
                inputDimension[k][1] = Integer.parseInt(inputLine[2]);

                if (preColCount != Integer.MIN_VALUE && preColCount != inputDimension[k][0]) {
                    throw new Exception("cannot be multiplied.");
                }
                preColCount = inputDimension[k][1];
            }
            p[0] = inputDimension[0][0];
            for (int t = 0; t < n; t++) {
                p[t + 1] = inputDimension[t][1];
            }
            return p;
        } catch(Exception e) {
            System.out.println("The matrices cannot be multiplied.");
            return null;
        }
    }

    public static void main(String[] args) {
        // Check 1: check parameters count
        if (!checkParametersCount(args)) {
            return;
        }

        // Check 2: check file can read
        String inputFileName = args[0];
        List<String> inputList = checkAndOpenFile(inputFileName);
        if (inputList == null) return;

        // Check 3: check the Multiplication valid, if not valid, end the program, else get the dimension array
        int[] p = checkMultiplicationValid(inputList);
        if (p == null) return;

        int pLen = p.length;
        int n = pLen - 1;
        // dp[i][j] means the chain from A[i] to A[j], the minimum Multiplication number
        int[][] dp = new int[n][n];
        String[][] memo = new String[n][n];
        for (int i = 0; i < n; i++) {
            memo[i][i] = "A_" + (i + 1);
        }

        // calculated chain length(from 2 to s)
        for (int l = 2; l <= n; l++) {
            for (int i = 0; i <= n - l; i++) {
                int j = i + l - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    int q = dp[i][k] + dp[k + 1][j] + p[i] * p[k + 1] * p[j + 1];
                    if (q < dp[i][j]) {
                        dp[i][j] = q;
                        memo[i][j] = "(" + memo[i][k] + " " + memo[k + 1][j] + ")";
                    }
                }
            }
        }

        System.out.println(dp[0][n - 1]);
        System.out.println(memo[0][n - 1]);
    }
}
