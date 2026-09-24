package Study.DataStructure.Code;

import Study.DataStructure.Code._8_3_Adjacency_List.AdjacencyList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

/*
- DFS (Depth-First Search, 깊이 우선 탐색): 한 방향으로 갈 수 있는 데까지 끝까지 파고들고, 막다른 길이면 되돌아와(backtrack) 다른 방향을 시도

  - 그래프 순회가 트리 순회와 다른 점
    -> 트리는 root에서 부모 -> 자식으로만 내려가는 구조라 같은 노드를 두 번 방문할 일이 없어 visited 체크가 불필요
    -> 그래프는 사이클이 존재할 수 있고, root라는 개념이 없어 시작점을 직접 지정해야 하며, 모든 정점이 연결되어 있다는 보장도 없음(disconnected)
    -> 따라서 그래프 순회는 반드시 visited 배열(또는 Set)을 들고 다니며 이미 방문한 정점은 다시 방문하지 않아야 함

  - 구현 방식 두 가지
    -> 재귀: 함수 호출 스택을 그대로 이용, 스택 프레임 자체가 backtrack 역할
    -> 명시적 Stack(ArrayDeque): 재귀 없이 push/pop으로 직접 LIFO 구현, 로직은 재귀와 본질적으로 동일(콜스택을 직접 흉내내는 것)
    -> Stack DFS 구현 시 주의: Queue(FIFO)로 구현하면 사실상 BFS가 되어버림, 반드시 push + pop 또는 addFirst로 LIFO 유지 필요

  - visited 처리 시점
    -> DFS는 정점에 진입하는 시점(재귀 호출 시작 시점)에 visited = true 처리
    -> 인접 정점 순회 전에 visited를 확인해 불필요한 재귀 호출/스택 push을 줄일 수 있음

  - dfs(start) vs 전체 그래프 탐색
    -> dfs(start): start와 연결된 하나의 connected component만 탐색
    -> 전체 탐색: 모든 정점을 순회하면서 아직 방문하지 않은 정점에서 dfs를 다시 시작 -> 여러 connected component를 전부 탐색할 때 사용 (disconnected graph 대응)

  - DFS + Backtracking
    -> 모든 가능한 경로를 탐색해야 하는 문제(예: 시작점 ~ 목적지까지의 최소 깊이 탐색)에서 필요
    -> 일반 DFS와 달리 하나의 정점을 방문 처리한 뒤, 그 정점에서 뻗어나가는 모든 경로 탐색이 끝나면(재귀 호출이 전부 반환되면) 다시 visited = false로 되돌려야 함
    -> 이래야 다른 시작 경로가 같은 정점을 다시 거쳐갈 수 있음 (한 번 방문했다고 영구히 막아버리면 안 되는 문제 유형)

  - 최소 깊이(경로) 탐색 시 흔한 실수
    -> 인접 정점이 목적지와 같을 때: 그 정점은 현재 정점보다 한 칸 더 간 것이므로 깊이는 curDepth + 1
    -> 인접 정점을 순회하는 for문 안에서 조건이 맞다고 바로 return하면 안 됨 -> 첫 번째로 찾은 경로 하나만 확인하고 다른 이웃(다른 경로)은 시도조차 못 하게 됨, 모든 이웃을 다 순회한 뒤 그 결과들 중 최솟값을 취해야 함
    -> 못 찾은 경우(-1)를 그대로 Math.min에 넣으면 음수라서 항상 이겨버려 못 찾은 걸 찾은 것으로 오인 -> 유효한 값이 없을 때만 candidate를 그대로 채택하고, 그 외에는 진짜 최솟값 비교를 하도록 분리 필요

  - DFS vs BFS
    -> DFS: 깊이 우선, Stack/재귀, 경로 탐색·연결 요소·영역 탐색·백트래킹 등에 활용
    -> BFS: 너비 우선, Queue, 비가중치 그래프의 최단 거리 탐색에 주로 활용 (같은 거리에 있는 정점을 모두 방문한 뒤 다음 거리로 확장하는 구조라 최단 경로가 자동으로 보장됨)
    -> DFS로 최단 경로를 구하려면 모든 경로를 다 타보고 그 중 최솟값을 골라야 해서 backtracking이 필수가 되고 코드도 복잡해짐 -> BFS가 구조적으로 더 적합한 이유

  - 시간복잡도 (인접리스트 vs 인접행렬)
    -> 인접리스트: O(V + E), 각 정점 한 번씩 방문(V) + 실제 연결된 간선만 순회(총 E)
    -> 인접행렬: O(V^2), 각 정점에서 인접 여부 확인을 위해 행 전체(V개)를 스캔해야 함, 실제 간선 수와 무관
    -> 희소 그래프(sparse graph)일수록 인접리스트가 유리, 순회 계열 알고리즘(DFS/BFS/다익스트라 등)에서 실무 표준으로 쓰이는 핵심 이유

*/

public class _8_4_Depth_First_Search {

  static AdjacencyList graph = new AdjacencyList();
  static boolean[] visited;

  public static void main(String[] args) {
    // 정점 0~6, 무방향 그래프
    // 0 - 1 - 3
    // |   |    \
    // 2 - 4     5
    // |   |    /
    // 6 - 7 - 9    8
    // (0-1-2-0 사이클 존재)
    // 8은 어디와도 연결 안 된 고립 정점 (disconnected 테스트용)

    for (int i = 0; i <= 9; i++) {
      graph.addVertex(i);
    }

    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 2); // 0-1-2 사이클
    graph.addEdge(1, 3);
    graph.addEdge(1, 4);
    graph.addEdge(2, 4); // 1-2-4 사이클 형성
    graph.addEdge(2, 6);
    graph.addEdge(3, 5);
    graph.addEdge(4, 7);
    graph.addEdge(5, 9);
    graph.addEdge(6, 7);
    graph.addEdge(7, 9);

    // 예상
    // dfs(0) / bfs(0) -> 0, 1, 2, 3, 4, 5, 6, 7, 9 는 방문, 8은 방문 안 됨 (visited 배열에 8만 false로 남아야 정상)
    // 사이클(0-1-2)이 있으니 visited 체크 빠지면 무한루프 or 중복 방문 발생 -> 여기서 바로 드러남
    // bfs(0) 결과 순서로 0에서 각 정점까지의 최단 거리(depth)도 검증 가능:
    //   거리 1: 1, 2 / 거리 2: 3, 4 / 거리 3: 5

    visited = new boolean[graph.adjacencyMap.size()];
    System.out.println("==========================================================");
    recursionDFS(0);
    System.out.println("RecursionDFS visited = " + Arrays.toString(visited));

    System.out.println("==========================================================");
    visited = new boolean[graph.adjacencyMap.size()];
    System.out.println("가능한 경로를 모두 탐색 & 목적지 까지 최소 path : " +
        findMinDepthDestinationRecursionDFS(0, 2, 0)
    );

    System.out.println("==========================================================");
    visited = new boolean[graph.adjacencyMap.size()];
    stackDFS(0);
    System.out.println("StackDFS visited = " + Arrays.toString(visited));

    System.out.println("==========================================================");
    visited = new boolean[graph.adjacencyMap.size()];
    System.out.println("fullDFS");
    // disconnected graph에서 사용
    // DFS(0)만 하면 0과 연결된 component만 탐색
    // 모든 정점을 순회 하면서 아직 방문하지 않은 정점에서 다시 DFS를 시작하면
    // 모든 component 탐색 가능
    for (int vertex = 0; vertex < graph.adjacencyMap.size(); vertex++) {
      if (visited[vertex]) {continue;}

      System.out.println("새로운 component 시작: " + vertex);

      recursionDFS(vertex);
    }

  }

  private static void recursionDFS(int start) {
    if (!graph.hasVertex(start) || visited[start]) {
      return;
    }

    visited[start] = true;
    System.out.println("방문 Node idx : " + start);

    List<Integer> vertexList = graph.getAdjacentVertices(start);

    for (Integer nextVertex : vertexList) {
      if (!visited[nextVertex]) {
        recursionDFS(nextVertex);
      }
    }
  }

  // DFS + backtracking
  private static int findMinDepthDestinationRecursionDFS(int start, int destination, int curDepth) {
    if (!graph.hasVertex(start) || visited[start]) {
      return -1;
    }

    visited[start] = true;

    System.out.print("방문 Node idx : " + start);

    List<Integer> vertexList = graph.getAdjacentVertices(start);
    int minDepth = -1;

    System.out.println(", vertexList = " + vertexList);

    for (Integer nextVertex : vertexList) {
      if (destination == nextVertex) {
        minDepth = updateMin(minDepth, curDepth + 1);
        continue;
      }

      if (!visited[nextVertex]) {
        int result = findMinDepthDestinationRecursionDFS(nextVertex, destination, curDepth + 1);

        if (result != -1) {
          minDepth = updateMin(minDepth, result);
        }
      }
    }
    // backtracking, 현재 경로의 탐색이 끝났으므로 다른 경로에서 방문이 가능 하도록 방문 해제
    visited[start] = false;
    return minDepth;
  }

  private static int updateMin(int cur, int candidate) {
    if (cur == -1) {return candidate;}
    return Math.min(cur, candidate);
  }

  // DFS 는 LIFO가 필요 (최근에 발견한 정점을 먼저 탐색), stack 을 사용
  private static void stackDFS(int start) {
    if (!graph.hasVertex(start) || visited[start]) {
      return;
    }

    visited[start] = true;

    // 시작 지점부터 정점들을 순서대로 추가
    ArrayDeque<Integer> stack = new ArrayDeque<>();
    for (Integer adjacentVertex : graph.getAdjacentVertices(start)) {
      stack.push(adjacentVertex);
    }

    while (!stack.isEmpty()) {
      // LIFO
      int curAdcacencyNode = stack.pop();

      if (visited[curAdcacencyNode]) {continue;}
      System.out.println("방문 Node idx : " + curAdcacencyNode);
      visited[curAdcacencyNode] = true;

      // 현재 정점의 인점 정점을 stack에 추가
      for (Integer adjacentVertex : graph.getAdjacentVertices(curAdcacencyNode)) {
        stack.addFirst(adjacentVertex);
      }
    }
  }
}