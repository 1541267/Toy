package Study.Algorithm.Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
- Kruskal MST(Minimum Spanning Tree, 최소 신장 트리, 간선 중심 접근)
  - 전제: 가중치 있는 무방향 연결 그래프에서 정의 (비연결 그래프면 신장 트리 자체가 불가능, 대신 최소 신장 숲(forest)이 나옴)
  - 신장 트리: 모든 정점을 포함하면서 사이클 없는 부분 그래프, 정점 V개면 간선은 항상 V-1개
  - MST: 신장 트리 중 간선 가중치 합이 최소인 것
  - 매 단계에서 최적성을 해치지 않는 가장 가벼운 간선을 선택하는 Greedy 알고리즘

  - 목적: 최소 가중치의 신장 트리(Spanning Tree)를 찾기

  - 동작 순서
    1) 모든 간선을 가중치 오름차순 정렬
    2) 간선을 하나씩 꺼내 양 끝 정점이 이미 같은 집합인지 확인
    3) 다른 집합이면 채택 & union, 같은 집합이면 사이클이라 버림
    4) 채택한 간선이 V-1개가 되면 종료 (early exit)

  - Union-Find와의 관계
    -> 현재까지 선택된 간선들만으로 두 정점이 연결되어 있는지 DFS/BFS로 확인하면 최악의 경우 간선마다 O(V+E)가 필요
    -> Union-Find(경로 압축 + union by size) 사용 시 이 판단이 사실상 O(1)에 가까움(O(α(n)))
    -> 결과적으로 알고리즘의 병목이 간선 정렬(O(E log E))로 이동

  - 시간복잡도: O(E log E) (E ≤ V^2 이므로 O(E log V)와 사실상 동일)
    -> Prim(O(E log V), 우선순위 큐 기반)과 점근적으로 유사하나, 간선이 적은 sparse 그래프에서 특히 유리

  - 비연결 그래프 처리
    -> 모든 간선을 다 순회해도 채택된 간선이 V-1개 미만이면 비연결 그래프
    -> 정점 0~V-1을 순회하며 findRoot() 결과의 distinct 개수를 세면 연결 요소(Connected Component) 개수를 알 수 있음
    -> 이 경우 결과는 MST가 아니라 최소 신장 숲(Minimum Spanning Forest), 연결 요소 개수만큼 트리가 나뉨

  - 설계 포인트
    -> union()이 성공 여부(boolean)를 리턴하도록 하면, 같은 집합인지 확인 + union 실행을 한 번의 호출로 처리 가능
       -> findRoot를 이중으로 호출하는 비효율 제거
    -> UnionFind는 정점 집합 연산만 책임지고, Edge/MST 관련 로직(정렬, 연결 요소 출력 등)은 UnionFind 밖의 별도 메서드로 분리
       -> Union-Find가 특정 도메인(그래프의 간선 개념)에 의존하지 않는 범용 자료구조로 유지됨

  - Greedy하게 골라도 최적해가 보장되는 이유 (Cut Property)
    - Kruskal은 가중치가 작은 간선부터 확인하면서 사이클을 만들지 않는 간선을 선택

    - 핵심: 현재 가장 가벼운 선택을 하더라도 이후 더 좋은 MST를 만들 기회를 잃지 않음

    - Cut
      -> 전체 정점을 논리적으로 두 그룹 A, B로 나누는 것
      -> 실제 그래프를 분할하거나 변경하는 것이 아니라, 간선의 안전성을 설명하기 위한 개념
      -> A와 B를 연결하는 간선을 Crossing Edge(또는 cut을 가로지르는 간선)라고 함
      -> ex) Edge.from (A), Edge.to (B) 이 둘을 시각적으로 좌우로 놔두면 그 사이가 cut

    - Cut Property
      -> 어떤 cut이 주어졌을 때, 그 cut을 가로지르는 간선 중 가중치가 가장 작은 간선은 그 간선을 포함하는 MST가 존재함
      -> 따라서 해당 간선을 선택해도 전체 최적성을 잃지 않음
      -> 최소 가중치 간선이 여러 개라면 MST가 여러 개 존재할 수 있음
      -> 선택된 간선이 유일한 MST를 결정한다는 의미가 아니라 해당 간선을 포함하는 MST 중 하나를 만들 수 있다는 의미

    - Kruskal과 Cut Property
      -> Kruskal에서 현재까지 선택된 간선들은 여러 개의 연결 요소(component)를 만듦
      -> 하나의 component를 A, 나머지 정점을 B로 생각하면 하나의 cut을 논리적으로 만들 수 있음
      -> 서로 다른 component를 연결하는 간선은 해당 cut을 가로지르는 Crossing Edge가 됨
      -> Kruskal은 가중치 오름차순으로 간선을 확인하므로 현재 component와 다른 component를 연결하는 간선 중 가장 가벼운 간선을 선택하게 됨
      -> Cut Property에 의해 이러한 선택은 MST의 최적성을 해치지 않는 안전한 선택

    - 사이클 판별과의 관계
      -> x와 y의 root가 같음
        = x와 y가 이미 같은 연결 요소에 있음
        = 해당 간선을 추가하면 사이클 발생
        = 간선을 버림

      -> x와 y의 root가 다름
        = 서로 다른 연결 요소에 있음
        = 해당 간선을 추가해도 사이클이 발생하지 않음
        = 두 component를 연결하는 간선이므로 선택 가능

    - 따라서 Kruskal의 각 선택은
      1. 현재 서로 다른 연결 요소를 연결하고
      2. 사이클을 만들지 않으며
      3. 가능한 간선 중 가중치가 가장 작은 간선을 선택하는 과정

    - 역할 정리
      -> Greedy = 가중치가 작은 간선부터 선택
      -> Union-Find = 두 정점이 같은 component인지 빠르게 판단하고 component를 합침
      -> Cut Property = 이러한 Greedy 선택이 MST의 최적성을 해치지 않는다는 근거

    - 결론
      -> Kruskal이 선택하는 최소 가중치의 Crossing Edge는 안전한 선택
      -> 따라서 현재의 Greedy 선택 때문에 이후 더 좋은 MST를 만들 기회를 잃지 않음
      -> 동일한 가중치의 간선이 존재하면 여러 MST가 만들어질 수 있음
      -> 선택된 간선이 포함된 MST 역시 여러 가능한 MST 중 하나가 될 수 있음
      -> 즉, Greedy 선택은 최적성을 보장하지만 반드시 유일한 MST를 만드는 것은 아님

  - 동일 가중치 간선이 여러 개 있을 때
    -> MST의 총 가중치 합(비용)은 항상 유일하게 같음
    -> 그러나 그 합을 만드는 구체적인 간선 조합은 여러 개 존재할 수 있음
    -> 가중치만으로 정렬 기준을 잡으면, 가중치가 같은 간선들 사이의 상대적 순서는 정렬 알고리즘의 안정성이나 from/to 같은 tie-break 기준 추가 여부에 따라 달라지고, 그 순서에 따라 실제로 채택되는 간선 조합이 달라질 수 있음

  - path compression / union by size(또는 rank) 없이 구현했을 때
    -> 매번 단순하게 한쪽을 다른 쪽 밑에 붙이기만 하면 트리가 한쪽으로 계속 길어지는 편향 구조가 될 수 있음
    -> 이 경우 정점 개수만큼 깊이가 길어진 연결리스트와 다를 바 없는 최악의 형태가 되어, findRoot가 매번 O(n)까지 나빠질 수 있음
    -> union by size/rank는 낮은 트리를 높은 트리 밑에 붙여서 이런 편향을 막고, path compression은 한 번 찾은 경로를 눌러서 이후 조회를 더 빠르게 만듦
    
  - 활용
    -> 그래프가 희소한 경우 (간선 수가 정점 수에 비해 적음, E ≈ V 근처)
    -> 간선 리스트 형태로 데이터가 이미 주어져 있는 경우 (정점 연결 정보보다 간선 자체가 주 데이터인 상황)
    -> 여러 컴포넌트를 점진적으로 합치는 과정 자체가 필요한 경우 (Union-Find가 다른 목적으로도 필요할 때)
*/

public class Kruskal_Algorithm {

  static class Edge {

    private final int from;
    private final int to;
    private final int weight;

    public Edge(int from, int to, int weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    @Override
    public String toString() {
      return "Edge [from=" + from + ", to=" + to + ", weight=" + weight + "]\n";
    }
  }

  static class UnionFind {

    private int[] parent;
    private int[] size;
    private int connectedComponentCount;

    public UnionFind(int n) {
      if (n < 1) {
        throw new IllegalArgumentException("배열 초기화 크기가 너무 작음, idx: " + n);
      }

      this.parent = new int[n];
      this.size = new int[n];
      connectedComponentCount = n;
      for (int i = 0; i < n; i++) {
        parent[i] = i;
        size[i] = 1;
      }
    }

    public int findRoot(int idx) {
      if (idx < 0 || idx >= parent.length) {
        throw new IndexOutOfBoundsException("잘못 된 인덱스, idx: " + idx);
      }

      int root = idx;

      // find root
      while (root != parent[root]) {
        root = parent[root];
      }

      int cur = idx;

      // path compression
      while (cur != parent[cur]) {
        parent[cur] = root;
        cur = parent[cur];
      }

      return root;
    }

    public boolean union(int x, int y) {
      int rootX = findRoot(x);
      int rootY = findRoot(y);

      if (rootX == rootY) {return false;}

      if (size[rootX] < size[rootY]) {
        size[rootY] += size[rootX];
        parent[rootX] = rootY;
      } else {
        size[rootX] += size[rootY];
        parent[rootY] = rootX;
      }

      connectedComponentCount--;
      return true;
    }

    public boolean isConnected(int x, int y) {
      return findRoot(x) == findRoot(y);
    }

    public int getConnectedComponentCount() {
      return connectedComponentCount;
    }
  }

  // 모든 정점을 조사해 부모의 개수를 파악해 연결 요소의 개수를 찾을 수 있고
  // 혹은 초기에 생성시 연결 요소 개수를 정점 개수로 초기화 하고
  // union()의 연결하는 과정에서 연결 요소 개수를 -- 해서 O(V) -> O(1)로 만들 수 있음
  private static void findAndPrintConnectedComponents(UnionFind uf, int v) {
    Set<Integer> roots = new HashSet<>();

    // 직접 반복문으로 검사
    for (int i = 0; i < v; i++) {
      roots.add(uf.findRoot(i));
    }
    System.out.println("Distinct Roots 개수: " + roots.size() + ", 연결 요소들: " + roots);
    // UnionFind의 ComponentCount 상태
    System.out.println("uf.connectedComponentCount = " + uf.connectedComponentCount);
  }


  private static List<Edge> createEdgeListAndSortByWeightOrder(int[][] edges) {
    List<Edge> edgesList = new ArrayList<>();

    for (int[] arr : edges) {
      edgesList.add(new Edge(arr[0], arr[1], arr[2]));
    }

    // 간선 가중치 오름차 정렬
    edgesList.sort(Comparator.comparingInt(e -> e.weight));

    return edgesList;
  }

  private static void DoKruskalMST(List<Edge> edges, UnionFind uf, List<Edge> result, int v) {
    for (Edge edge : edges) {
      if (uf.union(edge.from, edge.to)) {
        result.add(edge);
        if (result.size() == v - 1) {break;}
      }
    }
  }

  public static void main(String[] args) {

    // 정점 개수 12개
    int v = 12;

    System.out.println("==========================================================");
    System.out.println("모두 연결되어 있는(Distinct Root가 없는) 간선 예시");

    int[][] edgesArr = {{0, 1, 4}, {0, 2, 4}, {1, 2, 2}, {1, 3, 5}, {2, 3, 8}, {2, 4, 10}, {3, 4, 2}, {3, 5, 6}, {4, 5, 3}, {4, 6, 1},
        {5, 6, 7}, {5, 7, 9}, {6, 7, 4}, {6, 8, 11}, {7, 8, 3}, {7, 9, 6}, {8, 9, 2}, {8, 10, 5}, {9, 10, 8}, {10, 11, 3}};

    List<Edge> edges = createEdgeListAndSortByWeightOrder(edgesArr);

    UnionFind uf = new UnionFind(v);

    List<Edge> result = new ArrayList<>();

    DoKruskalMST(edges, uf, result, v);

    int resultSize = result.size();

    System.out.println("결과 사이즈: " + resultSize);
    if (resultSize != v - 1) {
      System.out.println("그래프가 연결 되어있지 않아 결과가 V - 1이 아님");
    } else {
      System.out.println("그래프가 모두 연결 되어있음");
    }

    findAndPrintConnectedComponents(uf, v);

    System.out.println("==========================================================");
    System.out.println("Distinct Root가 존재하는 간선 예시");
    edgesArr = new int[][]{{0, 1, 4}, {0, 2, 4}, {1, 2, 2}, {1, 3, 5}, {2, 3, 8}, {2, 4, 10}, {3, 4, 2}, {3, 5, 6}, {4, 5, 3}, {4, 6, 1},
        {5, 6, 7}, {5, 7, 9}, {6, 7, 4}, {6, 8, 11}, {7, 8, 3}, {9, 10, 2}, {10, 11, 3}};

    edges = createEdgeListAndSortByWeightOrder(edgesArr);

    uf = new UnionFind(v);

    result = new ArrayList<>();

    DoKruskalMST(edges, uf, result, v);

    resultSize = result.size();

    System.out.println("결과 사이즈: " + resultSize);
    if (resultSize != v - 1) {
      System.out.println("그래프가 연결 되어있지 않아 결과가 V - 1이 아님");
    } else {
      System.out.println("그래프가 모두 연결 되어있음");
    }

    findAndPrintConnectedComponents(uf, v);
  }
}
