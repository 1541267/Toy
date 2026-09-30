package Study.Algorithm.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import org.jetbrains.annotations.NotNull;

/*
- Prim 알고리즘 (MST, 최소 신장 트리 구성): 간선이 아닌 정점을 중심으로 트리를 확장하며 MST를 만드는 그리디 알고리즘
  - Kruskal과의 관계
    -> Kruskal: 간선 중심, 전체 간선을 정렬한 뒤 사이클을 만들지 않는 선에서 그리디하게 선택 (Union-Find로 사이클 판별)
    -> Prim: 정점 중심, 하나의 시작 정점에서 트리를 한 정점씩 확장 (우선순위 큐로 최소 간선 판별)
    -> 둘 다 결과적으로 Cut Property(임의의 컷을 가로지르는 최소 간선은 반드시 어떤 MST에 포함된다)에 근거해 정당화됨
    -> 정당화 논리는 서로 다르지만(Kruskal은 사이클 방지, Prim은 컷 기준 최소 선택) 최종적으로 같은 이론적 근거를 공유

  - 핵심 아이디어
    -> 방문한 정점 집합 V와 미방문 집합 V'를 나눴을 때, 매 단계 두 집합을 잇는 간선 중 최소 가중치 간선을 선택
    -> 선택된 간선의 반대쪽 정점을 V에 편입, 모든 정점이 V에 들어올 때까지 반복
    -> 총 V-1개의 간선이 선택되면 MST 완성 (정점 V개짜리 트리는 항상 간선 V-1개)

  - Lazy Prim (현재 구현 방식)
    -> 정점을 방문 처리할 때, 그 정점과 연결된 모든 간선을 조건 없이 우선순위 큐에 추가
    -> 큐에서 꺼낼 때(poll) 반대쪽 정점이 이미 방문 상태면 그냥 버림(continue)
    -> 같은 목적지를 향한 중복 간선이 큐 안에 여러 개 공존할 수 있음 (미방문 상태일 때 넣은 간선들이 나중에 한꺼번에 쌓이는 경우)
    -> 구현이 단순한 대신, 최악의 경우 큐 크기가 간선 수(E)까지 불어날 수 있음 -> O(E log E)

  - Eager Prim (decrease-key 방식, 심화)
    -> 정점마다 지금까지 알려진 최소 연결 비용(dist[v])을 관리
    -> 더 싼 간선을 발견하면 큐에 새로 넣지 않고, 큐 안의 해당 정점 항목 자체를 갱신(decrease-key)
    -> 자바 표준 PriorityQueue는 임의 원소의 우선순위를 낮추는 연산을 지원하지 않아위상 정렬 인덱스 기반 힙을 직접 구현해야 함
    -> 큐 크기가 항상 정점 수(V) 이하로 고정됨 -> O(E log V)
    -> E <= V^2 이므로 log E <= 2 log V, 즉 Big-O 상으로는 Lazy와 오더가 사실상 동일
    -> 실질적 이득은 메모리(큐 크기)와 약간의 상수 배수 정도, 그래프가 매우 커야 체감됨
    -> 구현 복잡도 대비 이득이 크지 않아 학습 우선순위는 낮음, 인덱스 힙 자체를 다뤄보고 싶을 때 도전할 주제

  - 그래프 표현: 인접 행렬 vs 인접 리스트
    -> 인접 행렬(int[v][v])
      --> 간선 없음을 표현은 초기값을 0이 아닌 Integer.MAX_VALUE
      --> 특정 정점의 인접 간선을 찾으려면 항상 O(V) 순회 필요
      --> 공간 O(V^2), 정점 수가 커질수록(특히 희소 그래프에서) 메모리 낭비 심함
    -> 인접 리스트(List<Edge>[] 또는 ArrayList<ArrayList<Edge>>)
      --> 특정 정점의 인접 간선만 바로 조회 가능 (연결된 만큼만 순회)
      --> 무방향 그래프이므로 간선 하나당 양쪽 정점의 리스트에 각각 추가해야 함 (한쪽만 넣으면 탐색 누락 발생)
      --> Prim은 정점별 인접 간선 조회가 반복되는 알고리즘이라 인접 리스트가 구조적으로 더 잘 맞음

  - 방어 로직
    -> 큐가 빈 상태로 루프가 끝났는데 result.size() != v-1 이면 그래프가 연결되어 있지 않다는 의미
    -> 이 경우를 체크하지 않으면, 컴포넌트가 나뉜 그래프에서도 조용히 불완전한 MST가 반환됨 (예외 처리 필요)

  - 시간복잡도 (인접 리스트 + 우선순위 큐 기준)
    -> Lazy Prim: O(E log E)
    -> Eager Prim: O(E log V)
    -> 인접 행렬 + 큐 없이 매 단계 선형 탐색: O(V^2), 밀집 그래프에서는 오히려 이 방식이 유리할 수 있음

  - 활용
    -> 그래프가 조밀함 (간선이 정점 수 대비 많음 E ≈ V²에 가까움) = 인접 행렬 기반이면 O(V²)로 Kruskal의 O(E log E)보다 유리할 수 있음
    -> 그래프가 인접 리스트/행렬 형태로 자연스럽게 주어져 있는 경우 (지도, 네트워크 토폴로지처럼 이 지점에서 어디로 갈 수 있나 가 자연스러운 질문인 도메인)
    -> 시작점이 고정되어 있고 거기서부터 점진적으로 확장하는 그림이 문제 상황과 잘 맞을 떄
*/


public class Prim_Algorithm {

  static class Edge implements Comparable<Edge> {

    private int from;
    private int to;
    private int weight;

    public Edge(int from, int to, int weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    @Override
    public int compareTo(@NotNull Edge o) {
      return Integer.compare(this.weight, o.weight);
    }

    @Override
    public String toString() {
      return "\nEdge [from=" + from + ", to=" + to + ", weight=" + weight + "]";
    }
  }

  public static void main(String[] args) {

    // 정점 개수 12개
    int v = 12;

    int start = 0;
    System.out.println("==========================================================");
    int[][] edgesArr = {{0, 1, 4}, {0, 2, 4}, {1, 2, 2}, {1, 3, 5}, {2, 3, 8}, {2, 4, 10}, {3, 4, 2}, {3, 5, 6}, {4, 5, 3}, {4, 6, 1},
        {5, 6, 7}, {5, 7, 9}, {6, 7, 4}, {6, 8, 11}, {7, 8, 3}, {7, 9, 6}, {8, 9, 2}, {8, 10, 5}, {9, 10, 8}, {10, 11, 3}};

    int[][] graph = new int[v][v];
    ArrayList<ArrayList<Edge>> listGraph = new ArrayList<>();
    for (int i = 0; i < v; i++) {
      listGraph.add(new ArrayList<>());
    }
    for (int[] arr : graph) {
      Arrays.fill(arr, Integer.MAX_VALUE);
    }

    for (int[] edge : edgesArr) {
      int from = edge[0];
      int to = edge[1];
      int weight = edge[2];

      graph[from][to] = weight;
      graph[to][from] = weight;

      listGraph.get(from).add(new Edge(from, to, weight));
      listGraph.get(to).add(new Edge(to, from, weight));
    }

    System.out.println("doPrimsAlgorithm(graph, start, v) = " + doAdjacencyMatrixBasedPrimsAlgorithm(graph, start, v));

    System.out.println("==========================================================");

    System.out.println("doPrimsAlgorithm(graph, start, v) = " + doAdjacencyListBasedPrimsAlgorithm(listGraph, start, v));

  }

  private static ArrayList<Edge> doAdjacencyListBasedPrimsAlgorithm(ArrayList<ArrayList<Edge>> graph, int start, int v) {
    ArrayList<Edge> result = new ArrayList<>();
    boolean[] visited = new boolean[v];

    PriorityQueue<Edge> queue = new PriorityQueue<>(v);

    visited[start] = true;

    queue.addAll(graph.get(start));

    int totalWeight = 0;

    while (!queue.isEmpty()) {
      Edge curEdge = queue.poll();

      // Lazy Prim 삭제, poll()을 이용한 삭제
      // PriorityQueue는 간선이 큐에 쌓였다가 visited 체크로 버려지는 방식
      // lazy prim 은 구현이 간단하나 일단 큐에 불필요한 간선을 쌓는 과정으로 메모리와 연산이 늘어남
      if (visited[curEdge.to]) {continue;}

      visited[curEdge.to] = true;
      queue.addAll(connectedVertexListFromList(graph, visited, curEdge.to));

      totalWeight += curEdge.weight;
      result.add(curEdge);
    }

    if (result.size() != v - 1) {
      throw new IllegalStateException("그래프가 연결되어 있지 않음");
    }

    System.out.println("totalWeight = " + totalWeight);
    return result;
  }

  private static ArrayList<Edge> doAdjacencyMatrixBasedPrimsAlgorithm(int[][] graph, int start, int v) {
    ArrayList<Edge> result = new ArrayList<>();
    boolean[] visited = new boolean[v];

    PriorityQueue<Edge> queue = new PriorityQueue<>(v);

    visited[start] = true;
    queue.addAll(connectedVertexListFromMatrix(graph, visited, start));

    int totalWeight = 0;

    while (!queue.isEmpty()) {
      Edge curEdge = queue.poll();

      if (visited[curEdge.to]) {continue;}

      visited[curEdge.to] = true;

      queue.addAll(connectedVertexListFromMatrix(graph, visited, curEdge.to));
      totalWeight += curEdge.weight;
      result.add(curEdge);
    }

    if (result.size() != v - 1) {
      throw new IllegalStateException("그래프가 연결되어 있지 않음");
    }

    System.out.println("totalWeight = " + totalWeight);

    return result;
  }

  private static ArrayList<Edge> connectedVertexListFromList(ArrayList<ArrayList<Edge>> graph, boolean[] visited, int start) {
    ArrayList<Edge> list = new ArrayList<>();

    for (Edge edge : graph.get(start)) {
      if (!visited[edge.to]) {
        list.add(edge);
      }
    }

    return list;
  }

  private static ArrayList<Edge> connectedVertexListFromMatrix(int[][] graph, boolean[] visited, int start) {
    ArrayList<Edge> list = new ArrayList<>();

    for (int i = 0; i < graph[start].length; i++) {
      if (graph[start][i] == Integer.MAX_VALUE || visited[i]) {continue;}
      list.add(new Edge(start, i, graph[start][i]));
    }

    return list;
  }
}
