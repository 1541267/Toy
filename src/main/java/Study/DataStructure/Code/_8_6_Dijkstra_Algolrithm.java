package Study.DataStructure.Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import org.jetbrains.annotations.NotNull;

public class _8_6_Dijkstra_Algolrithm {

  static class Edge {

    private int dest;
    private int weight;

    public Edge(int dest, int weight) {
      this.dest = dest;
      this.weight = weight;
    }
  }

  static class NodeDistance implements Comparable<NodeDistance> {

    private int vertex;
    private int distance;

    public NodeDistance(int vertex, int distance) {
      this.vertex = vertex;
      this.distance = distance;
    }

    @Override
    public int compareTo(@NotNull NodeDistance o) {
      return Integer.compare(this.distance, o.distance);
    }
  }

  private static int[] dijkstra(List<List<Edge>> graph, int start, int v) {

    int[] dist = new int[v];
    Arrays.fill(dist, Integer.MAX_VALUE);

    dist[start] = 0;

    PriorityQueue<NodeDistance> queue = new PriorityQueue<>();

    queue.add(new NodeDistance(start, 0));

    while (!queue.isEmpty()) {
      NodeDistance curNode = queue.poll();

      // 음수 가중치가 있지만 사이클이 없는 경우
      // 정점의 방문 체크를 하던 visited[] 가 없어 무한 루프
      if (curNode.distance > dist[curNode.vertex]) {continue;}

      for (Edge edge : graph.get(curNode.vertex)) {
        int newDist = curNode.distance + edge.weight;

        if (newDist < dist[edge.dest]) {
          dist[edge.dest] = newDist;
          queue.add(new NodeDistance(edge.dest, newDist));
        }
      }
    }

    return dist;
  }

  public static void main(String[] args) {

    int V = 5;
    List<List<Edge>> graph = new ArrayList<>();
    for (int i = 0; i < V; i++) {
      graph.add(new ArrayList<>());
    }

    graph.get(0).add(new Edge(1, 4));
    graph.get(0).add(new Edge(2, 1));
    graph.get(2).add(new Edge(1, 2));
    graph.get(1).add(new Edge(3, 1));
    graph.get(2).add(new Edge(3, 5));
    graph.get(3).add(new Edge(4, 3));
    graph.get(1).add(new Edge(4, 7));

    int[] result = dijkstra(graph, 0, V);
    // 기대값: [0, 3, 1, 4, 7]
    System.out.println(Arrays.toString(result));
  }

}
