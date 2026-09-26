package Study.DataStructure.Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import org.jetbrains.annotations.NotNull;

/*
- Dijkstra(다익스트라) Algorithm
  -> 가중치가 있는 그래프에서 하나의 출발 정점(source)으로부터, 모든 정점까지의 최단 거리를 구하는 알고리즘
  - 조건: 모든 간선의 가중치가 음수가 아니어야 함

  - BFS와의 차이
    -> BFS: 모든 간선을 동일한 무게(1)로 취급 -> 최단 경로 = 간선 개수(hop(최단 경로 중 한 구간) 수)가 최소인 경로
    -> Dijkstra: 간선마다 가중치가 다름 -> 최단 경로 = 거쳐온 가중치의 총합이 최소인 경로
    --> 모든 간선의 가중치를 1로 통일한 그래프에 Dijkstra를 돌리면 BFS와 결과가 동일해짐
        (BFS는 Dijkstra의 특수 케이스로도 볼 수 있음)
    --> 먼저 방문한 순서가 곧 최단 거리라는 BFS의 전제가 가중치 도입 순간 깨짐
    --> 그래서 단순 큐(FIFO) 대신, 지금까지 계산된 거리가 가장 작은 정점을 매번 선택해야 함
        -> 우선순위 큐(min-heap) 필요

  - 전체 흐름
    1. 모든 정점의 거리를 INF로 초기화, 출발점만 0
    2. 우선순위 큐에 (거리, 정점) 형태로 출발점 push
    3. 큐에서 거리가 가장 작은 정점을 poll
    4. 지연 삭제: poll된 거리 > dist[정점] 이면 이미 더 짧은 경로로 갱신된 항목이므로 스킵
    5. 인접 정점들에 대해 relax(더 짧은 경로를 발견했을 때 값을 갱신) 수행
     -> newDist = 현재거리 + 간선가중치
     -> newDist < dist[인접정점] 이면 dist 갱신 + 큐에 새로 push
    6. 큐가 빌 때까지 반복

  - 지연 삭제(Lazy Deletion)가 필요한 이유
    -> 표준 PriorityQueue는 이미 들어있는 원소의 우선순위를 직접 수정(decrease-key)하기 어려움
    -> 그래서 더 짧은 거리를 찾을 때마다 갱신 대신 새로 push, 큐 안에 낡은 항목이 남을 수 있음
    -> poll 시점에 dist[]와 비교해서 낡았으면 스킵
    -> curNode.distance는 push되던 시점의 dist[] 값과 항상 같게 시작하며, 이후 dist[]는
       더 작아질 수만 있으므로, poll 시점엔 항상 curNode.distance >= dist[vertex] 성립
       (크면 낡은 정보, 같으면 유효한 최신 정보)
    -> visited[] 로 영구 확정하는 최적화가 없는 버전이라, 음수 간선이 있어도 사이클만
       없다면 결과적으로 정답이 나올 수 있음 (단, 효율 보장은 깨짐)

  - 경로 복원 (parent[] 배열)
    -> dist[] 는 거리(숫자)만 저장, 실제로 어떤 정점들을 거쳤는지는 알 수 없음
    -> parent[]를 별도로 두고, relax 성공 시 parent[edge.dest] = curVertex 로 갱신
    -> parent[]는 이 정점 직전에 어디서 왔는가라는 정보 하나만 담고 있으므로 목적지(target)에서부터 거꾸로 추적해야 실제 경로가 나옴
    -> cur = target 에서 시작해서 parent를 따라 -1(시작점의 이전 노드)이 나올 때까지 역추적,
       모은 뒤 뒤집으면(reverse) 시작점 -> 목적지 순서의 실제 경로가 됨
    -> 모든 목적지에 대한 경로를 미리 다 저장하지 않고, parent[] O(V) 하나로 필요할 때마다 원하는 목적지의 경로를 재구성 -> 메모리 효율적

  - 그리디로 동작하는 이유
    -> 음수 가중치가 없다는 전제 덕분에, 한 번 poll(확정)된 정점의 거리는 이후 더 짧은 경로로 다시 갱신될 수 없음
    -> 그래서 큐에서 꺼낸 순간 바로 그 정점의 최단 거리를 확정 지어도 안전함

  - 시간복잡도
    -> 인접리스트 + 우선순위 큐(이진 힙): O((V + E) log V)
    -> 인접행렬 + 배열 선형 탐색: O(V^2)
      --> 인접행렬에서는 이웃 확인 자체가 이미 O(V)라서, 최소값 탐색에 O(V)를 더 써도 전체 복잡도에 영향 없음
      ---> 힙을 써도 이득이 없어 단순 배열 탐색이 오히려 적합
    -> decrease-key를 O(log n)에 하려면 정점 -> 힙 배열 인덱스를 매핑하는 별도
       해시맵/배열이 필요 (Indexed Priority Queue), 배열 기반 힙만으로는 특정 원소
       위치를 못 찾아 O(n) 탐색이 필요해짐

- Bellman-Ford Algorithm
  -> 다익스트라와 동일하게 출발 정점으로부터 모든 정점까지의 거리를 구하지만 음수 가중치 간선이 있어도 동작함
  -> 조건: 음수 사이클은 없어야 함

  - 핵심 아이디어
    -> 우선순위 큐로 순서를 고르는 대신, 그래프의 모든 간선을 V-1번 반복해서 무차별적으로 relax
    -> 사이클이 없는 그래프에서 최단 경로는 최대 V-1개의 간선으로 구성됨
    -> 한 번의 전체 패스(모든 간선 순회)마다 최소 하나의 새로운 hop(최단 경로 중 한 구간)이 확정된다고
       보장할 수 있으므로, V-1번 반복하면 아무리 복잡한 최단 경로도 전부 수렴

  - 전체 흐름
    1. 거리 배열 INF 초기화, 출발점만 0
    2. V-1번 반복
      -> 그래프의 모든 간선(from -> dest, weight)을 순회하며 relax 시도
      -> dist[from] + weight < dist[dest] 이면 dist[dest] 갱신
      -> 한 패스 전체를 다 돈 뒤에도 갱신이 하나도 없었다면 이미 수렴 완료 -> 조기 종료 가능
      -> 주의: 이 조기 종료 체크(updated 플래그 확인)는 반드시 패스 전체가 끝난 뒤 딱 한 번만
    3. V번째 패스를 한 번 더 시도 -> 그래도 갱신이 일어나면 음수 사이클 존재

  - V-1번 반복하는 이유
    -> 정점이 V개면 사이클 없는 최단 경로가 거칠 수 있는 간선의 최대 개수는 V-1개
    -> 각 전체 패스마다 못해도 하나의 hop은 새로 확정된다고 보장되므로
       V-1번 반복하면 모든 가능한 최단 경로가 다 반영됨

  - 음수 사이클 탐지 원리
    -> V-1번으로 이미 모든 최단 경로가 수렴했어야 정상
    -> V번째 패스에서도 여전히 더 짧아지는 간선이 있다면, 그건 사이클을 돌 때마다
       거리가 계속 줄어드는 경로가 있다는 뜻 -> 음수 사이클 존재
    -> Dijkstra는 이 탐지 자체가 불가능 (애초에 음수 가중치를 전제하지 않음)

  - 시간복잡도
    -> O(V x E) - Dijkstra(O((V+E) log V))보다 느림
    -> 대신 음수 가중치 처리 + 음수 사이클 탐지라는 Dijkstra에 없는 기능 보유

- Dijkstra vs Bellman-Ford 비교
  - 음수 가중치: Dijkstra 불가(오답 위험) / Bellman-Ford 가능
  - 음수 사이클 탐지: Dijkstra 불가 / Bellman-Ford 가능 (고유 기능)
  - 자료구조: Dijkstra는 우선순위 큐 필수 / Bellman-Ford는 단순 반복문만으로 충분
  - 매 반복의 순회 대상: Dijkstra는 poll된 정점의 인접 정점만 / Bellman-Ford는 매 패스마다 모든 간선
  - 시간복잡도: Dijkstra O((V+E)logV) / Bellman-Ford O(V*E)
  - 접근 방식: Dijkstra는 그리디(한 번 확정하면 안 바뀜) / Bellman-Ford는 반복적으로 계속 완화
  -> 다익스트라는 똑똑하게 순서를 골라 한 번씩만 확정하는 방식, 벨만포드는 순서
     상관없이 여러 번 반복해서 수렴시키는 방식 - 그 무차별함 덕분에 음수 가중치도
     버티고 사이클 존재 여부까지 검증 가능
*/

public class _8_6_Dijkstra_BellmanFord_Algolrithm {

  static class Edge {

    private int from;
    private int dest;
    private int weight;

    public Edge(int from, int dest, int weight) {
      this.from = from;
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

      // 지연 삭제(Lazy Deletion)
      // 음수 가중치가 있는 경우 무한 반복(거리가 계속 줄어들어서)
      // 음수 가중치가 있지만 사이클이 없다면 무한 루프가 생기지 않고 visited[]가 없는 덕분에
      // 정답이 나옴, 사이클을 끊어주는 위치, 더 짧은 경로로 갱신된 정점을 다시 처리하지 않도록
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

  private static ArrayList<Integer> pathRecordDijkstra(List<List<Edge>> graph, int start, int target, int v) {

    int[] dist = new int[v];
    Arrays.fill(dist, Integer.MAX_VALUE);

    dist[start] = 0;

    int[] parent = new int[v];
    Arrays.fill(parent, -1);

    PriorityQueue<NodeDistance> queue = new PriorityQueue<>();

    queue.add(new NodeDistance(start, 0));
    while (!queue.isEmpty()) {
      NodeDistance curNode = queue.poll();
      if (curNode.distance > dist[curNode.vertex]) {continue;}

      for (Edge edge : graph.get(curNode.vertex)) {
        int newDist = curNode.distance + edge.weight;

        // 기존
        if (newDist < dist[edge.dest]) {
          dist[edge.dest] = newDist;
          queue.add(new NodeDistance(edge.dest, newDist));
          parent[edge.dest] = curNode.vertex;
        }
      }
    }
    ArrayList<Integer> path = new ArrayList<>();

    int cur = target;

    while (cur != -1) {
      path.add(cur);
      cur = parent[cur];
    }

    Collections.reverse(path);
    return path;
  }

  private static int[] bellmanFord(List<List<Edge>> graph, int start, int v) {
    int[] distance = new int[v];
    Arrays.fill(distance, Integer.MAX_VALUE);
    distance[start] = 0;

    // v - 1번 완화
    for (int i = 0; i < v - 1; i++) {

      boolean updated = false;

      for (List<Edge> edges : graph) {
        for (Edge edge : edges) {

          int curVertex = edge.from;
          int nextVertex = edge.dest;

          if (distance[curVertex] != Integer.MAX_VALUE
              && distance[nextVertex] > distance[curVertex] + edge.weight) {
            distance[nextVertex] = distance[curVertex] + edge.weight;
            updated = true;
          }
        }
      }

      // 이번 반복에서 아무것도 갱신되지 않는다면
      // 더 이상 최단 거리가 변하지 않음
      if (!updated) {break;}
    }

    for (List<Edge> edges : graph) {
      for (Edge edge : edges) {
        int curVertex = edge.from;
        int nextVertex = edge.dest;

        if (distance[curVertex] != Integer.MAX_VALUE
            && distance[nextVertex] > distance[curVertex] + edge.weight) {
          throw new IllegalStateException("음수 사이클이 존재");
        }
      }
    }

    return distance;
  }

  static List<List<Edge>> createGraph(int v) {
    List<List<Edge>> graph = new ArrayList<>();

    for (int i = 0; i < v; i++) {
      graph.add(new ArrayList<>());
    }

    return graph;
  }

  public static void main(String[] args) {
    int V = 5;
    List<List<Edge>> graph = createGraph(V);

    graph.get(0).add(new Edge(0, 1, 4));
    graph.get(0).add(new Edge(0, 2, 1));
    graph.get(2).add(new Edge(2, 1, 2));
    graph.get(1).add(new Edge(1, 3, 1));
    graph.get(2).add(new Edge(2, 3, 5));
    graph.get(3).add(new Edge(3, 4, 3));
    graph.get(1).add(new Edge(1, 4, 7));

    int start = 0;
    int target = 4;
    System.out.println("==========================================================");
    System.out.println("기댓값: [0, 3, 1, 4, 7]\n시작 지점부터 각 정점까지 거리,"
        + " Start: " + start + ", target: " + target + "\n"
        + Arrays.toString(dijkstra(graph, start, V)));

    System.out.println("==========================================================");
    System.out.println("경로 복원 다익스트리 (Start: " + start + " to Target: " + target +
        ")\n" + pathRecordDijkstra(graph, start, target, V));

    System.out.println("==========================================================");
    System.out.println("기댓값: [0, -1, 1, 0, -3]\n벨만 포드 알곡리즘, Start: " + start);
    graph = createGraph(V);

    graph.get(0).add(new Edge(0, 1, 4));
    graph.get(0).add(new Edge(0, 2, 1));
    graph.get(2).add(new Edge(2, 1, -2));
    graph.get(1).add(new Edge(1, 3, 1));
    graph.get(2).add(new Edge(2, 3, 5));
    graph.get(3).add(new Edge(3, 4, -3));
    graph.get(1).add(new Edge(1, 4, 7));

    int[] distance = bellmanFord(graph, start, V);

    System.out.println(Arrays.toString(distance));
    System.out.println("==========================================================");
    System.out.println("음수 사이클 존재 체크");
    // 음수 사이클 존재 체크
    graph = createGraph(V);
    graph.get(0).add(new Edge(0, 1, 1));
    graph.get(1).add(new Edge(1, 2, -2));
    graph.get(2).add(new Edge(2, 1, -2));
    graph.get(2).add(new Edge(2, 3, 1));
    System.out.println(Arrays.toString(bellmanFord(graph, start, V)));
  }
}
