package org.algandsd;

import java.util.*;

public class Graph {
    public final Map<Integer, Set<Integer>> graphMap;
    private int vCount = 0;
    private int eCount = 0;
    private final float friendRatio = 0.9f;

    public Graph() {
        graphMap = new HashMap<>();
    }

    public Graph(int vertexCount) {
        graphMap = new HashMap<>();
        for (int i = 0; i < vertexCount; i++) {
            addVertex(i);
        }
    }

    public int vertexCount() {
        return vCount;
    }

    public int edgeCount() {
        return eCount;
    }

    public void addVertex(int v) {
        if (v < 0) {
            return;
        }
        if (!graphMap.containsKey(v)) {
            graphMap.put(v, new HashSet<>());
            vCount++;
        }
    }

    public void removeVertex(int v) {
        if (!graphMap.containsKey(v)) {
            return;
        }

        List<Integer> vertexList = new ArrayList<>(graphMap.get(v));

        for (Integer v2 : vertexList) {
            removeEdge(v, v2);
        }

        graphMap.remove(v);
        vCount--;
    }

    public void addEdge(int v1, int v2) {
        if (graphMap.containsKey(v1) && graphMap.containsKey(v2)) {
            graphMap.get(v1).add(v2);
            graphMap.get(v2).add(v1);
            eCount++;
        }
    }

    public void removeEdge(int v1, int v2) {
        if (graphMap.containsKey(v1) && graphMap.containsKey(v2)) {
            if (graphMap.get(v1).contains(v2) && graphMap.get(v2).contains(v1)) {
                graphMap.get(v1).remove(v2);
                graphMap.get(v2).remove(v1);
                eCount--;
            }
        }
    }

    public void createGraph(String string) {
        graphMap.clear();
        for (String edge : string.trim().split("\\s*,\\s*")) {
            String[] parts = edge.split("\\s*-\\s*");
            int v1 = Integer.parseInt(parts[0].trim());
            int v2 = Integer.parseInt(parts[1].trim());
            addVertex(v1);
            addVertex(v2);
            addEdge(v1, v2);
        }
    }

    public Map<Integer, Integer> getSideMap (int a, int b, float x, float y) {
        if (!graphMap.containsKey(a) || !graphMap.containsKey(b)) {
            return null;
        }
        Map<Integer, Integer> result = new HashMap<>();
        result.put(a, a);
        result.put(b, b);

        Map<Integer, Float> pointsRatioForA = getPointsRatio(a);
        Map<Integer, Float> pointsRatioForB = getPointsRatio(b);

        for (Integer point : graphMap.keySet()) {
            if (point == a || point == b) {
                continue;
            }

            float aRatio = pointsRatioForA.getOrDefault(point, 0.0f);
            float bRatio = pointsRatioForB.getOrDefault(point, 0.0f);

            if (aRatio > x && aRatio - bRatio >= y) {
                result.put(point, a);
            } else if (bRatio > x && bRatio - aRatio >= y) {
                result.put(point, b);
            } else {
                result.put(point, -1);
            }
        }

        return result;
    }


    private Map<Integer, Float> getPointsRatio (int point) {
        Map<Integer, Float> result = new HashMap<>();
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.addLast(point);
        visited.add(point);
        result.put(point, 1.0f);

        while (!queue.isEmpty()) {
            int current = queue.pollFirst();
            float currentRatio = result.get(current);

            for (Integer nextPoint : graphMap.get(current)) {
                if (visited.contains(nextPoint)) {
                    continue;
                }
                queue.addLast(nextPoint);
                visited.add(nextPoint);
                result.put(nextPoint, currentRatio * 0.9f);
            }
        }

        return result;
    }
}
