package Study.DataStructure.Code;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
- BFS (Breadth-First Search, 너비 우선 탐색): 시작 정점에서 가까운(레벨이 낮은) 정점부터 순서대로 방문
  - 같은 레벨(시작점으로부터 간선 수가 같은) 정점을 모두 방문한 뒤에야 다음 레벨로 넘어감

  - DFS와의 근본적 차이
    -> DFS: 한 방향으로 갈 수 있는 데까지 깊이 파고든 후 되돌아옴 -> 되돌아갈 지점을 기억해야 함
       -> Stack(혹은 재귀 호출 스택)의 LIFO 특성이 이 되돌아가기 순서와 자연스럽게 맞음
    -> BFS: 가까운 정점부터 순서대로 넓게 퍼짐 -> 먼저 발견한 정점을 먼저 확장해야 레벨 순서가 유지됨
       -> Queue의 FIFO 특성이 이 순서를 그대로 보장
    -> 둘 다 골격은 거의 동일 (방문 안 한 인접 정점을 자료구조에 넣고, 꺼내서 처리를 반복)
       -> 그 자료구조가 Stack이냐 Queue냐만 다름

  - 방문 처리 시점 (BFS 구현에서 가장 흔한 버그 포인트)
    -> 반드시 큐에 넣는 시점에 visited 체크 & 표시를 해야 함
    -> poll할 때 체크하면, 같은 정점이 여러 인접 정점 경로를 통해 큐에 중복으로 여러 번 들어감
       (아직 poll 전이라 visited로 안 걸림) -> 불필요한 중복 방문, 큐 크기 낭비
    -> DFS(재귀)에서는 노드 진입 시점에 visited 처리해도 크게 문제 없는 경우가 많은 것과 대비됨

  - 최단 경로 보장의 원리
    -> BFS는 레벨 단위로 퍼져나가기 때문에, 어떤 정점에 처음 도달한 순간이 곧 시작점으로부터의 최단 거리
    -> 큐에서 꺼내지는 순서가 곧 거리(레벨) 순서이기 때문에 성립
    -> 가중치 없는 그래프에서만 성립 (가중치 있는 그래프의 최단 경로는 Dijkstra로 확장)

  - 활용처
    -> 가중치 없는 그래프의 최단 경로/최단 거리 계산
    -> 레벨(깊이)별 그룹핑이 필요한 문제 (트리의 levelOrder도 BFS의 한 예)
    -> 연결 요소(Connected Component) 개수 세기, 이분 그래프(bipartite) 판별
    -> DFS는 반대로 경로 존재 여부, 백트래킹, 위상정렬, 사이클 탐지 등에 강점

  - 시간/공간 복잡도
    -> 시간: O(V + E) - 모든 정점과 간선을 정확히 한 번씩(무방향은 상수배) 확인
    -> 공간: O(V) - 큐 + visited 배열
    -> 연결 요소가 여러 개로 쪼개진 그래프에서도 전체 정점/간선을 딱 한 번씩만 처리하므로
       바깥 순회 루프(O(V))가 내부 BFS 비용에 자연스럽게 흡수되어 총합은 여전히 O(V+E)

  - 최단 거리(dist[]) + 경로 복원(parent[]) 패턴
    -> dist[]: 정점별 시작점으로부터의 거리를 기록, 전부 -1로 초기화 후 실제 방문된 정점만 갱신
       -> 끝난 뒤 dist[dest] == -1이면 도달 불가능으로 판정 (초기값과 실제값이 안 헷갈리게 설계)
    -> parent[]: 정점별로 자신을 발견(큐에 넣은) 이전 정점을 기록
       -> 인접 정점을 큐에 넣는 순간 parent[nextVertex] = curVertex로 기록
       -> BFS 종료 후 dest에서 parent를 따라 거슬러 올라가며 경로를 역추적, 뒤집으면 start -> dest 경로
    -> 경로 전체를 매번 복사해서 큐에 들고 다니는 방식(O(V^2)에 가까워짐)보다
       parent[] 역추적 방식(O(V+E) + O(경로 길이))이 표준적이고 효율적
    -> parent[start] = -1로 남아있는 것을 역추적 종료 조건으로 활용 (자기 자신은 누구에게도 발견된 적 없음)

  - 연결 요소(Connected Component) 개수 세기
    -> 전체 정점을 순회하면서, 아직 방문 안 한 정점을 만날 때마다 그 지점에서 새로 BFS 시작 -> count++
    -> 한 번의 BFS 호출이 끝나면 그 컴포넌트에 속한 모든 정점이 visited 처리되므로
       바깥 루프에서 다시 마주쳐도 스킵됨 -> 결과적으로 컴포넌트 개수만큼만 BFS가 실행됨
*/

public class _8_5_Breadth_First_Search {

  static boolean[] visited;

  public static List<List<Integer>> initGraph(int[][] edges, int V) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < V; i++) {graph.add(new ArrayList<>());}

    for (int[] e : edges) {
      graph.get(e[0]).add(e[1]);
      graph.get(e[1]).add(e[0]); // 무방향이므로 양방향 추가
    }
    return graph;
  }

  public static void main(String[] args) {

/*
        0
      / | \
     1  2  3
     |     |
     4     5
      \   /
        6
*/

    int V = 7;

    int[][] edges = {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {3, 5}, {4, 6}, {5, 6}};

    List<List<Integer>> graph = initGraph(edges, V);

    System.out.println("==========================================================");
    visited = new boolean[V];
    System.out.println("기본 BFS, 순회 & 방문 순서 출력:  " + BasicBFS(0, graph));

    System.out.println("==========================================================");
    System.out.println("출발 정점의 최단 거리 계산");
    visited = new boolean[V];

    int start = 6;
    int dest = 1;

    int[] dist = findMinPathBFS(start, dest, graph);
    System.out.println("Vertex: " + dist[0] + ", dest: " + dest + ", min path: " + dist[1]);

    System.out.println("==========================================================");
    System.out.println("최단 거리 및 경로");
    visited = new boolean[V];
    Object[] result = pathRecordWithFindMinPathBFS(start, dest, graph);
    System.out.println("Vertex: " + result[0] + "\ndest: " + result[1] + "\ndepth: " + result[2] + "\npath: " + result[3]);

    System.out.println("==========================================================");
    V = 32;
    visited = new boolean[V];


/*
Component 1 — Tree

         0
       / | \
      1  2  3
     / \    |
    4   5   6
       / \
      7   8

Component 2 — Cycle

       9
      / \
     10  11
     |    |
     12---13
      \   /
       14

Component 3 — Long path + branch

 15 ─ 16 ─ 17 ─ 18 ─ 19 ─ 20
            |          |
            21         22
                        |
                       23

Component 4 — Dense

      24 ─ 25
      |\  /|
      | \/ |
      | /\ |
      |/  \|
      26 ─ 27

Component 5 — Small

      28 ─ 29
       \  /
        30

Component 6 — Isolated

      31*/
    edges = new int[][]{
        // Component 1
        {0, 1}, {0, 2}, {0, 3},
        {1, 4}, {1, 5},
        {3, 6},
        {5, 7}, {5, 8},

        // Component 2
        {9, 10}, {9, 11},
        {10, 12},
        {11, 13},
        {12, 13},
        {12, 14},
        {13, 14},

        // Component 3
        {15, 16},
        {16, 17},
        {17, 18},
        {18, 19},
        {19, 20},
        {17, 21},
        {19, 22},
        {22, 23},

        // Component 4
        {24, 25},
        {24, 26},
        {24, 27},
        {25, 26},
        {25, 27},
        {26, 27},

        // Component 5
        {28, 29},
        {28, 30},
        {29, 30}

        // Component 6
        // 31은 고립 정점
    };
    graph = initGraph(edges, V);
    System.out.println("연결 요소 (Connected Component) 개수 체크: " + countConnectedComponent(graph));

  }

  public static List<Integer> BasicBFS(int start, List<List<Integer>> graph) {
    if (graph.isEmpty() || visited[start]) {return null;}
    List<Integer> result = new ArrayList<>();

    ArrayDeque<Integer> queue = new ArrayDeque<>();

    queue.add(start);
    visited[start] = true;

    while (!queue.isEmpty()) {
      int curVertex = queue.poll();

      result.add(curVertex);

      for (Integer nextVertex : graph.get(curVertex)) {
        if (!visited[nextVertex]) {
          visited[nextVertex] = true;
          queue.add(nextVertex);
        }
      }
    }
    return result;
  }

  private static int[] findMinPathBFS(int start, int dest, List<List<Integer>> graph) {
    if (start == dest) {
      return new int[]{start, 0};
    }
    if (graph.get(start).isEmpty()) {
      return new int[]{start, -1};
    }

    ArrayDeque<int[]> queue = new ArrayDeque<>();
    int depth = -1;

    queue.add(new int[]{start, 0});
    visited[start] = true;

    while (!queue.isEmpty()) {
      int[] curNode = queue.poll();

      int curVertex = curNode[0];
      int curDepth = curNode[1];

      if (dest == curVertex) {
        depth = curDepth;
        break;
      }

      for (Integer nextVertex : graph.get(curVertex)) {
        if (!visited[nextVertex]) {
          visited[nextVertex] = true;
          queue.add(new int[]{nextVertex, curDepth + 1});
        }
      }
    }
    return new int[]{start, depth};
  }

  // 기존에 무겁던 큐를 가볍게
  // 경로 복사는 parent[nextVertex] = curVertex 한 줄로 대체 
  // -> BFS 가 끝난 뒤 딱 한 번만 dest에서 start까지 거슬러 올라가며 경로를 생성
  // dist[] 를 사용해 실제로 갱신된 정점만 값 갱신, 끝났는데 -1 이면 도달 못함 판정
  // parent[start]는 처음부터 -1(자기 자신은 누구한테도 발견(도달) 하지 않음), cur != -1 조건으로 start 에서 탈출
  private static Object[] pathRecordWithFindMinPathBFS(int start, int dest, List<List<Integer>> graph) {
    // 0 = start, 1 = dest, 2 = depth, 3 = path(arrayList)
    Object[] result = new Object[4];
    result[0] = start;
    result[1] = dest;

    int[] parent = new int[graph.size()];
    int[] dist = new int[graph.size()];

    Arrays.fill(parent, -1);
    Arrays.fill(dist, -1);

    if (start == dest) {
      result[2] = 0;
      result[3] = new ArrayList<>(List.of(start));
      return result;
    }

    ArrayDeque<Integer> queue = new ArrayDeque<>();

    visited[start] = true;
    dist[start] = 0;
    queue.add(start);

    while (!queue.isEmpty()) {
      int curVertex = queue.poll();

      if (curVertex == dest) {break;}

      for (Integer nextVertex : graph.get(curVertex)) {
        if (!visited[nextVertex]) {
          visited[nextVertex] = true;
          dist[nextVertex] = dist[curVertex] + 1;
          parent[nextVertex] = curVertex;
          queue.add(nextVertex);
        }
      }
    }

    // 도달 불가능 -> dist가 갱신된 적 없으면 -1
    if (dist[dest] == -1) {
      result[2] = -1;
      result[3] = new ArrayList<>();
      return result;
    }

    // parent 를 거슬러 올라가며 경로 역추적
    ArrayList<Integer> path = new ArrayList<>();
    int cur = dest;

    while (cur != -1) {
      path.add(cur);
      cur = parent[cur];
    }

    Collections.reverse(path);

    result[2] = dist[dest];
    result[3] = path;
    return result;
  }

  // 직접 구현 해 본 것, 결과는 같은데 도달 불가능 경우 처리가 누락
  // queue 추가 동작이 비효율적 -> 인접 정점을 큐에 넣을 떄 마다 curPath 를 통째로 복사
  // @SuppressWarnings("unchecked")
  // private static Object[] pathRecordWithFindMinPathBFS(int start, int dest, List<List<Integer>> graph) {
  //   // 0 = start, 1 = dest, 2 = depth, 3 = path(arrayList)
  //   Object[] result = new Object[4];
  //
  //   result[0] = start;
  //   result[1] = dest;
  //   result[3] = new ArrayList<>();
  //
  //   if (start == dest) {
  //     result[2] = 0;
  //     return result;
  //   }
  //   if (graph.get(start).isEmpty()) {
  //     result[2] = -1;
  //     return result;
  //   }
  //
  //   ArrayDeque<Object[]> queue = new ArrayDeque<>();
  //
  //   Object[] temp = new Object[4];
  //   temp[0] = start;
  //   temp[1] = dest;
  //   temp[2] = 0;
  //   temp[3] = new ArrayList<>();
  //
  //   visited[start] = true;
  //   queue.add(temp);
  //
  //   while (!queue.isEmpty()) {
  //     Object[] curInfo = queue.poll();
  //
  //     int curVertex = (int) curInfo[0];
  //     int curDepth = (int) curInfo[2];
  //     ArrayList<Integer> curPath = (ArrayList<Integer>) curInfo[3];
  //
  //     curPath.add(curVertex);
  //
  //     if (dest == curVertex) {
  //       result[2] = curDepth;
  //       result[3] = curPath;
  //       return result;
  //     }
  //
  //     for (Integer nextVertex : graph.get(curVertex)) {
  //       if (!visited[nextVertex]) {
  //         visited[nextVertex] = true;
  //         queue.add(new Object[]{nextVertex, dest, curDepth + 1, new ArrayList<>(curPath)});
  //       }
  //     }
  //
  //   }
  //   return result;
  // }

  private static int countConnectedComponent(List<List<Integer>> graph) {
    int componentCount = 0;

    for (int vertex = 0; vertex < graph.size(); vertex++) {
      if (!visited[vertex]) {
        BasicBFS(vertex, graph);
        componentCount++;
      }
    }
    return componentCount;
  }
}
