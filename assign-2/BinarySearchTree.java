import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;




public class BinarySearchTree {

    class TreeNode {
        String name;
        String count;
        TreeNode parent;
        TreeNode left;
        TreeNode right;
        TreeNode(String name, String count, TreeNode parent, TreeNode left, TreeNode right) {
            this.name = name;
            this.count = count;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        // Constructor with default values for depth, left, and right
        TreeNode(String name, String count) {
            this(name, count, null, null, null);
        }
    }

    // Constants
    public static final String ADD = "ADD";
    public static final String SEARCH = "SEARCH";
    public static final String DELETE = "DELETE";

    TreeNode root; // tree root
    String inputFileName;
    String outputFileName;
    List<String> outputList;

    public BinarySearchTree(String inputFileName, String outputFileName) {
        this.root = null;
    }

    private static boolean checkParametersCount(String[] args) {
        if (args.length == 1 && (args[0].equals("help") || args[0].equals("h")) || args.length != 2) {
            System.out.println("Usage: java BinarySearchtTree <input file name> <output file name>");
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
            System.out.println("ERROR: could not open input file.");
            return null;
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

    // add new Node into bst
    public Integer add(String name, String count) {
        TreeNode newNode = new TreeNode(name, count);
        int depth = 1;
        if (root == null) {
            root = newNode;
            return depth;
        }
        TreeNode current = root;
        TreeNode parent = null;
        while (true) {
            depth++;
            parent = current;
            if (name.compareTo(current.name) < 0) { // node name smaller than current node name, go left
                current = current.left;
                if (current == null) {
                    parent.left = newNode;
                    newNode.parent = parent;
                    return depth;
                }
            } else { // node name larger than current node name, go right
                current = current.right;
                if (current == null) {
                    parent.right = newNode;
                    newNode.parent = parent;
                    return depth;
                }
            }
        }
    }

    public String[] search(String name) {
        TreeNode current = root;
        int depth = 1;
        while (current != null && !current.name.equals(name)) {
            depth++;
            if (name.compareTo(current.name) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current == null) {
            return new String[] { "-1", null, null };
        } else {
            return new String[] { "" + depth, current.name, current.count };
        }
    }

    private TreeNode getSuccessor(TreeNode deleteNode) {
        TreeNode successorParent = null;
        TreeNode successor = deleteNode;
        TreeNode current = deleteNode.right;

        // find the smallest node in the right subtree
        while (current != null) {
            successorParent = successor;
            successor = current;
            current = current.left;
        }

        // move connections to successor if successor is not the right child of node to be deleted
        if (successor != deleteNode.right) {
            successorParent.left = successor.right;
            successor.right = deleteNode.right;
        }

        return successor;
    }

    public TreeNode delete(String name) {
        if (root == null) {
            return null;
        }
        TreeNode current = root;
        boolean isLeftChild = false;
        // step 1: try to find the node with the name and its parent
        while (!current.name.equals(name)) {
            if (name.compareTo(current.name) < 0) {
                isLeftChild = true;
                current = current.left;
            } else {
                isLeftChild = false;
                current = current.right;
            }
            if (current == null) {
                return null;
            }
        }
        // step 2: delete the target Node, build new Tree
        // case 1: Node to be deleted has no children
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            }
            if (isLeftChild) {
                current.parent.left = null;
            } else {
                current.parent.right = null;
            }
        }
        // case 2: Node to be deleted has one child
        else if (current.right == null) {
            if (current == root) {
                root = current.left;
                current.left.parent = null;
            } else if (isLeftChild) {
                current.parent.left = current.left;
                current.left.parent = current.parent;
            } else {
                current.parent.right = current.right;
                current.right.parent = current.parent;
            }
        } else if (current.left == null) {
            if (current == root) {
                root = current.right;
                current.right.parent = null;
            } else if (isLeftChild) {
                current.parent.right = current.right;
                current.right.parent = current.parent;
            } else {
                current.parent.right = current.right;
                current.right.parent = current.parent;
            }
        }
        // case 3: Node to be deleted has two children
        else {
            TreeNode successor = getSuccessor(current);

            // Connect parent of current to successor
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                current.parent.left = successor;
            } else {
                current.parent.right = successor;
            }

            // Connect successor to current's left child
            successor.left = current.left;
            successor.parent = current.parent;
        }
        return current;
    }

    public static void main(String[] args) {
        // Check 1: check parameters count
        if (!checkParametersCount(args)) {
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        // Check 2: check file can read
        List<String> inputList = checkAndOpenFile(inputFileName);
        if (inputList == null) {
            return;
        }

        List<String> outputList = new ArrayList<>();
        BinarySearchTree bsTree = new BinarySearchTree(inputFileName, outputFileName);

        for (String line: inputList) {
            String[] splittedLine = line.split(" ");
            String action = splittedLine[0];
            String[] content = splittedLine[1].split(",");
            String name = content[0];
            String count = content.length > 1 ? content[1] : null;
            String outputLine = "";
            if (action.equals(ADD)) {
                int addDepth = bsTree.add(name, count);
                outputLine = "Added (" + name + "," + count + ") at depth " + addDepth + ".";
                outputList.add(outputLine);
                // System.out.println(outputLine);
            } else if (action.equals(SEARCH)) {
                String[] outputStrs = bsTree.search(name);
                if (outputStrs[0].equals("-1")) {
                    outputLine = "Not found: " + name + ".";
                } else {
                    outputLine = "Found (" + outputStrs[1] + "," + outputStrs[2] + ") at depth " + outputStrs[0] + ".";
                }
                outputList.add(outputLine);
            } else if (action.equals(DELETE)) {
                TreeNode deletedNode = bsTree.delete(name);

                if (deletedNode == null) {
                    outputLine = "Cannot delete: " + name + ".";
                } else {
                    outputLine = "Deleted (" + deletedNode.name + "," + deletedNode.count + ").";
                }
                outputList.add(outputLine);
            }
        }

        createOutputFile(outputList, outputFileName);

        return;
    }
}