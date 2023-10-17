package com.tomomoto.gui;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.GreedyColoring;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import com.tomomoto.util.MyWeightedEdge;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.*;

public class Graph extends JFrame {
    private JGraphXAdapter<String, ?> jgxAdapter;
    private final Map<String, List<String>> vertexMap;
    private final AbstractBaseGraph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
    private final Map<Integer, String> colorsMap = new LinkedHashMap<>();
    private Map<String, Integer> vertexesAndColors;

    public Graph(JFrame parentWindow, Map<String, List<String>> vertexMap) {
        super("Graph");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });
        setVisible(true);

        this.vertexMap = vertexMap;

        fillColorsMapWithKeysAndColors();
        buildGraph();
    }

    private void buildGraph() {
        addVertexes();
        addEdges();

        jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.STYLE_ENDARROW);
        getContentPane().add(graphComponent);

        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());


        vertexesAndColors = new GreedyColoring<>(graph).getColoring().getColors();
        System.out.println(vertexesAndColors);
        colorVertexes(cells);
    }

    private void colorVertexes(Collection<Object> cells) {
        Object[] cellsArray = cells.toArray();
//        mxCell mxCell = (com.mxgraph.model.mxCell) cellsArray[12];
//        jgxAdapter.getModel().beginUpdate();
//        mxCell.setStyle("color=red");
//        jgxAdapter.getModel().endUpdate();
        System.out.println(cells);
        for (int i = 0; i < cells.size(); i++) {
            if (vertexesAndColors.containsKey(((com.mxgraph.model.mxCell) cellsArray[i]).getValue()) && ((com.mxgraph.model.mxCell) cellsArray[i]).isVertex()) {
                jgxAdapter.getModel().beginUpdate();
                jgxAdapter.setCellStyles(mxConstants.STYLE_FILLCOLOR, colorsMap.get(vertexesAndColors.get(((com.mxgraph.model.mxCell) cellsArray[i]).getValue())), new Object[]{cellsArray[i]});
                jgxAdapter.getModel().endUpdate();
            }
        }
    }

    private void fillColorsMapWithKeysAndColors() {
        List<String> colorsList = new ArrayList<>(List.of(
           "green",
                "blue",
                "red",
                "yellow",
                "pink",
                "purple",
                "lime",
                "brown",
                "cyan",
                "silver",
                "coral",
                "orange",
                "beige"
        ));
        for (int i = 0; i < vertexMap.size() + 1; i++) {
            colorsMap.put(i, colorsList.get(i));
        }
    }

    private void addVertexes() {
        vertexMap.forEach((key, value) -> graph.addVertex(key));
    }

    private void addEdges() {
        vertexMap.forEach((sourceVertex, value) -> value.forEach((targetVertex) -> graph.addEdge(sourceVertex, targetVertex)));
    }
}
