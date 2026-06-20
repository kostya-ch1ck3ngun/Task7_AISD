package org.algandsd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JTextField graphTextField;
    private JButton createGraphButton;
    private JSpinner addVertexSpinner;
    private JButton addVertexButton;
    private JSpinner deleteVertexSpinner;
    private JSpinner v1AddEdgeSpinner;
    private JSpinner v2AddEdgeSpinner;
    private JButton deleteVertexButton;
    private JButton addEdgeButton;
    private JSpinner v1DeleteEdgeSpinner;
    private JSpinner v2DeleteEdgeSpinner;
    private JButton deleteEdgeButton;
    private JButton resultButton;
    private JSpinner aSpinner;
    private JSpinner bSpinner;
    private JTextField xTextField;
    private JTextField yTextField;
    private JPanel graphPanel;
    private JTextArea resultTextArea;
    private final Graph graph = new Graph();

    public MainForm() {
        super("Таск 7");
        setVisible(true);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(1488, 666);
        bSpinner.setValue(12);
        addVertexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.addVertex((int) addVertexSpinner.getValue());
                deleteVertexSpinner.setValue(addVertexSpinner.getValue());
                addVertexSpinner.setValue((int) addVertexSpinner.getValue() + 1);
                drawGraph(graph.graphMap);
            }
        });
        deleteVertexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.removeVertex((int) deleteVertexSpinner.getValue());
                addVertexSpinner.setValue(deleteVertexSpinner.getValue());
                if ((int) deleteVertexSpinner.getValue() > 0) {
                    deleteVertexSpinner.setValue((int) deleteVertexSpinner.getValue() - 1);
                }
                drawGraph(graph.graphMap);
            }
        });
        addEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.addEdge((int) v1AddEdgeSpinner.getValue(), (int) v2AddEdgeSpinner.getValue());
                v1DeleteEdgeSpinner.setValue(v1AddEdgeSpinner.getValue());
                v2DeleteEdgeSpinner.setValue(v2AddEdgeSpinner.getValue());
                v1AddEdgeSpinner.setValue((int) v1AddEdgeSpinner.getValue() + 1);
                v2AddEdgeSpinner.setValue((int) v2AddEdgeSpinner.getValue() + 1);
                drawGraph(graph.graphMap);
            }
        });
        deleteEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.addEdge((int) v1DeleteEdgeSpinner.getValue(), (int) v2DeleteEdgeSpinner.getValue());
                v1AddEdgeSpinner.setValue(v1DeleteEdgeSpinner.getValue());
                v2AddEdgeSpinner.setValue(v2DeleteEdgeSpinner.getValue());
                if ((int) v1DeleteEdgeSpinner.getValue() > 0) {
                    v1DeleteEdgeSpinner.setValue((int) v1DeleteEdgeSpinner.getValue() - 1);
                }
                if ((int) v2DeleteEdgeSpinner.getValue() > 0) {
                    v2DeleteEdgeSpinner.setValue((int) v2DeleteEdgeSpinner.getValue() - 1);
                }
                drawGraph(graph.graphMap);
            }
        });
        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Integer, Integer> sideMap = graph.getSideMap((int) aSpinner.getValue(), (int) bSpinner.getValue(), Float.parseFloat(xTextField.getText()), Float.parseFloat(yTextField.getText()));
                resultTextArea.setText(mapToText(sideMap));
            }
        });
        createGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.createGraph(graphTextField.getText());
                drawGraph(graph.graphMap);
            }
        });
    }

    private String mapToText(Map<Integer, Integer> map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Результат: \n");
        for (Integer key : map.keySet()) {
            stringBuilder.append(key).append(" : ").append(map.get(key)).append(";\n");
        }
        return stringBuilder.toString();
    }

    private void drawGraph(Map<Integer, Set<Integer>> graphMap) {
        graphPanel.removeAll();

        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (graphMap.isEmpty()) {
                    return;
                }

                int width = getWidth();
                int height = getHeight();

                // BFS layers layout
                List<Integer> vertices = new ArrayList<>(graphMap.keySet());
                Map<Integer, Point> positions = new HashMap<>();
                int n = vertices.size();

                // Find root vertices (vertices with no incoming edges)
                Set<Integer> allTargets = new HashSet<>();
                for (Set<Integer> targets : graphMap.values()) {
                    allTargets.addAll(targets);
                }

                List<Integer> roots = new ArrayList<>();
                for (Integer vertex : vertices) {
                    if (!allTargets.contains(vertex)) {
                        roots.add(vertex);
                    }
                }

                if (roots.isEmpty()) {
                    roots.add(vertices.get(0));
                }

                // BFS to assign layers
                Map<Integer, Integer> layers = new HashMap<>();
                Queue<Integer> queue = new LinkedList<>();
                for (Integer root : roots) {
                    layers.put(root, 0);
                    queue.offer(root);
                }

                while (!queue.isEmpty()) {
                    Integer current = queue.poll();
                    int currentLayer = layers.get(current);
                    Set<Integer> neighbors = graphMap.getOrDefault(current, new HashSet<>());
                    for (Integer neighbor : neighbors) {
                        if (!layers.containsKey(neighbor)) {
                            layers.put(neighbor, currentLayer + 1);
                            queue.offer(neighbor);
                        }
                    }
                }

                // Assign positions by layers
                int maxLayer = layers.values().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
                Map<Integer, List<Integer>> verticesByLayer = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : layers.entrySet()) {
                    verticesByLayer.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
                }

                int layerHeight = (height - 100) / Math.max(maxLayer, 1);
                for (int layer = 0; layer < maxLayer; layer++) {
                    List<Integer> layerVertices = verticesByLayer.getOrDefault(layer, new ArrayList<>());
                    if (!layerVertices.isEmpty()) {
                        int layerWidth = (width - 100) / layerVertices.size();
                        for (int i = 0; i < layerVertices.size(); i++) {
                            int x = 50 + i * layerWidth + layerWidth / 2;
                            int y = 50 + layer * layerHeight;
                            positions.put(layerVertices.get(i), new Point(x, y));
                        }
                    }
                }

                // Add vertices not visited by BFS
                Random random = new Random(42);
                int unassignedY = height - 50;
                for (Integer vertex : vertices) {
                    if (!positions.containsKey(vertex)) {
                        positions.put(vertex, new Point(random.nextInt(width - 100) + 50, unassignedY));
                    }
                }

                // Draw edges
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                for (Map.Entry<Integer, Set<Integer>> entry : graphMap.entrySet()) {
                    Point from = positions.get(entry.getKey());
                    for (Integer toVertex : entry.getValue()) {
                        Point to = positions.get(toVertex);
                        if (from != null && to != null) {
                            drawArrow(g2d, from.x, from.y, to.x, to.y);
                        }
                    }
                }

                // Draw vertices
                int vertexRadius = 30;
                for (Map.Entry<Integer, Point> entry : positions.entrySet()) {
                    Point p = entry.getValue();

                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(p.x - vertexRadius/2, p.y - vertexRadius/2, vertexRadius, vertexRadius);
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(p.x - vertexRadius/2, p.y - vertexRadius/2, vertexRadius, vertexRadius);

                    String label = String.valueOf(entry.getKey());
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(label);
                    int textHeight = fm.getHeight();
                    g2d.drawString(label, p.x - textWidth/2, p.y + textHeight/4);
                }
            }

            private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
                int arrowSize = 10;
                double angle = Math.atan2(y2 - y1, x2 - x1);

                int vertexRadius = 30;
                int startX = x1 + (int)((vertexRadius/2) * Math.cos(angle));
                int startY = y1 + (int)((vertexRadius/2) * Math.sin(angle));
                int endX = x2 - (int)((vertexRadius/2) * Math.cos(angle));
                int endY = y2 - (int)((vertexRadius/2) * Math.sin(angle));

                g2d.drawLine(startX, startY, endX, endY);

                int[] xPoints = {
                        endX,
                        endX - (int)(arrowSize * Math.cos(angle - Math.PI/6)),
                        endX - (int)(arrowSize * Math.cos(angle + Math.PI/6))
                };
                int[] yPoints = {
                        endY,
                        endY - (int)(arrowSize * Math.sin(angle - Math.PI/6)),
                        endY - (int)(arrowSize * Math.sin(angle + Math.PI/6))
                };

                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        };

        drawingPanel.setPreferredSize(new Dimension(graphPanel.getWidth(), graphPanel.getHeight()));
        drawingPanel.setBackground(Color.LIGHT_GRAY);

        graphPanel.setLayout(new BorderLayout());
        graphPanel.add(drawingPanel, BorderLayout.CENTER);
        graphPanel.revalidate();
        graphPanel.repaint();
    }
}
