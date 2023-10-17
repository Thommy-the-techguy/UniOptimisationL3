package com.tomomoto.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainForm extends JFrame {
    private JTable matrixTable;
    private JScrollPane scrollPane;
    private JButton readFromFileButton;
    private JButton findPathButton;
    private JComboBox<String> comboBox;
    private final Path pathToMatrixFile = Path.of("src", "main", "resources", "matrix.txt");
    private int[][] matrix = new int[11][11];

    public MainForm() {
        super("Раскраска графа");
        setSize(550, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        matrix = new int[getMatrixSize()][getMatrixSize()];
        initializeComponents();
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainForm();
    }

    private void initializeComponents() {
        initializeMatrixTable();
        initializeScrollPane();
        initializeReadFromFileButton();
        initializeFindPathButton();
        initializeOrientationComboBox();
    }

    private void initializeOrientationComboBox() {
        comboBox = new JComboBox<>(new String[]{"Ориентированный", "Не ориентированный"});
        comboBox.setSize(150, 30);
        comboBox.setLocation(400, 30);
        this.add(comboBox);
    }

    private int getMatrixSize() {
        try {
            return readConnectionsFromMatrixFile().size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> getMapOfVertexes() {
        Map<String, List<String>> mapOfVertexes = new LinkedHashMap<>();
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            String key = String.format("v%d", i + 1);
            List<String> values = new ArrayList<>();
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                if (matrixTable.getValueAt(i, j).equals(1)) {
                    values.add(String.format("v%d", j));
                }
            }
            mapOfVertexes.put(key, values);
        }
        return mapOfVertexes;
    }

    private void initializeReadFromFileButton() {
        readFromFileButton = new JButton("Считать");
        readFromFileButton.setSize(100, 30);
        readFromFileButton.setLocation(400, 0);
        readFromFileButton.addActionListener(event -> {
            try {
                List<String> matrixLinesList = readConnectionsFromMatrixFile();
                System.out.println(matrixLinesList);
                transferMatrixValuesToArray(convertStringValuesToInt(matrixLinesList));
                fillMatrixTableWithArrayMatrix();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.add(readFromFileButton);
    }

    private void initializeFindPathButton() {
        findPathButton = new JButton("Найти путь");
        findPathButton.setSize(100, 30);
        findPathButton.setLocation(200, 230);
        findPathButton.addActionListener((event) -> new Graph(this, getMapOfVertexes()));
        this.add(findPathButton);
    }

    private void initializeMatrixTable() {
        TableModel tableModel = new DefaultTableModel(getColumnNamesVector(), matrix.length);
        matrixTable = new JTable(tableModel);
        matrixTable.setGridColor(Color.BLACK);
        fillLeftTableNumbers();
        fillTableWithZeroes();
    }

    private void fillLeftTableNumbers() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            matrixTable.setValueAt(String.valueOf(i + 1), i, 0);
        }
    }

    private void fillTableWithZeroes() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                matrixTable.setValueAt(0, i, j);
            }
        }
    }
    
    private void fillMatrixTableWithArrayMatrix() {
        for (int i = 0; i < matrixTable.getRowCount(); i++) {
            for (int j = 1; j < matrixTable.getColumnCount(); j++) {
                matrixTable.setValueAt(matrix[i][j - 1], i, j);
            }
        }
    }

    private Vector<String> getColumnNamesVector() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        for (int i = 0; i < matrix.length; i++) {
            columnNames.add(String.valueOf(i + 1));
        }
        return columnNames;
    }

    private void initializeScrollPane() {
        scrollPane = new JScrollPane(matrixTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(400, 200);
        scrollPane.setLocation(0, 0);
        this.add(scrollPane);
    }

    private List<String> readConnectionsFromMatrixFile() throws IOException {
        return List.of(Files.readString(pathToMatrixFile).split("_")[0].trim().split("\n"));
    }

    private void transferMatrixValuesToArray(List<Integer> matrixIntegerValuesList) {
        int size = getMatrixSize(matrixIntegerValuesList.size());
        matrix = new int[size][size];
        for (int i = 0, k = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = matrixIntegerValuesList.get(k);
                k++;
            }
        }
    }

    private int getMatrixSize(int matrixSize) {
        return (int) Math.sqrt(matrixSize);
    }

    private List<Integer> convertStringValuesToInt(List<String> matrixLinesList) {
        List<Integer> integers = new ArrayList<>();
        matrixLinesList.forEach(
                item -> Arrays.stream(item.split(" "))
                        .forEach(stringValue -> integers.add(Integer.valueOf(stringValue)))
        );
        return integers;
    }
}
