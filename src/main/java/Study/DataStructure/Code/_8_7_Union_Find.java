package Study.DataStructure.Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
- Union-Find (Disjoint Set): 원소들을 서로 겹치지 않는 그룹(집합)으로 나누고, 그룹 소속 여부를 빠르게 판단/병합하는 자료구조
  - 그래프의 간선(연결 정보) 자체를 저장하는 자료구조가 아님 (인접리스트/인접행렬과 다른 역할)
    -> parent 배열은 원본 간선 순서/모양을 보존하지 않음, path compression 이후엔 원래 어떤 간선으로 연결됐는지 알 수 없음
    -> 오직 각 원소가 최종적으로 어느 그룹(루트)에 속하는지만 표현
    -> 이웃이 누구인지, 어떤 경로로 연결됐는지 같은 질문에는 답할 수 없음 (필요하면 별도로 그래프 구조를 들고 있어야 함)

  - 필요한 이유
    -> 두 정점이 같은 연결 요소에 속하는지 판단
    -> 간선을 하나씩 추가하며 사이클 발생 여부 판단 (Kruskal MST의 핵심)
    -> 그래프가 고정되어 있지 않고 간선이 점진적으로 추가되는 상황에서, 매번 DFS/BFS로 새로 확인하는 비효율을 피함

  - 핵심 구조: 각 그룹을 하나의 트리로 표현, 트리의 루트를 그 그룹의 대표(representative)로 삼음
    -> parent[i]: i의 부모, 자기 자신이 부모면 루트
    -> 초기화 시 모든 원소가 자기 자신을 부모로 가짐 (n개의 독립된 트리)

  - 연산
    -> find(x): x가 속한 트리의 루트를 찾을 때까지 parent를 계속 타고 올라감
    -> union(x, y): find(x), find(y)로 각각의 루트를 찾고, 한 루트를 다른 루트의 자식으로 붙여 그룹을 병합
    -> isConnected(x, y): find(x) == find(y) 로 즉시 같은 그룹인지 판단

  - 최적화 없이 구현 시 발생하는 문제
    -> union 시 무조건 한쪽 루트를 다른 쪽 밑에 붙이면, 간선이 순차적으로 이어지는 패턴(0-1, 1-2, 2-3, ...)에서 트리가 한쪽으로 계속 길어짐
    -> 결국 연결리스트와 다를 바 없는 모양이 되어 find가 최악의 경우 O(n) (BST의 편향 트리와 같은 메커니즘)

  - Union by Rank / Size
    -> 두 그룹을 합칠 때 더 작은(낮은) 트리를 더 큰(높은) 트리 밑에 붙임 -> 트리 높이가 함부로 늘어나는 것을 막음
    -> rank 기준: 트리 높이(상한선) 기준 / size 기준: 트리에 속한 노드 수 기준 (구현이 더 직관적)
    -> 이 최적화만으로도 트리 높이가 O(log n)으로 보장됨

  - Path Compression
    -> find(x) 실행 후 루트를 찾고 나면, 그 경로에 있던 모든 노드의 parent를 바로 루트로 재연결
    -> 한 번 루트를 찾은 노드는 이후 한 번에 루트로 접근 가능해짐
    -> 재귀 구현: parent[idx] = find(parent[idx]); return parent[idx]; 한 줄로 자연스럽게 표현됨
       (콜스택이 되감기며 경로상의 모든 노드를 순서대로 압축)
    -> 반복문 구현: 재귀와 달리 되감기가 없으므로 두 단계로 명시적으로 분리해야 함
       1) 루트를 찾을 때까지 parent를 타고 올라가는 순회
       2) 루트를 알게 된 후, 시작 인덱스부터 루트까지 다시 순회하며 각 노드의 parent를 루트로 직접 연결

  - 두 최적화를 함께 써야 하는 이유
    -> Path Compression만 있으면: 이미 길어진 트리에서 첫 find 호출은 여전히 느림 (압축은 그 이후에만 효과를 냄)
    -> Union by Rank/Size만 있으면: 높이는 O(log n)으로 보장되지만 find할 때마다 매번 그 높이를 그대로 타고 올라가야 함
    -> 둘을 함께 적용하면 amortized O(α(n)) 근접
       -> α: 역 아커만 함수, n이 아무리 커져도 사실상 4~5를 넘지 않을 정도로 극도로 느리게 증가 -> 실무에서는 사실상 상수 시간으로 취급

  - union 조기 종료와 find의 부수효과
    -> union(x, y)에서 두 원소가 이미 같은 그룹이면(rootX == rootY) size 갱신 없이 조기 리턴
    -> 하지만 그 이전에 호출된 find(x), find(y) 자체는 이미 실행된 것이므로 path compression 효과는 그대로 남음
    -> union 결과가 조기 종료되어도 find의 부수효과(경로 압축)는 별개로 발생

  - 활용처
    -> Kruskal MST: 간선을 가중치 오름차순 정렬 후 하나씩 확인, 이미 같은 그룹이면(사이클) 스킵, 아니면 union
    -> 연결 요소 개수 구하기: 모든 간선에 대해 union 수행 후, 서로 다른 루트 개수를 세면 됨
    -> 네트워크 연결 판별, 이미지 내 연결된 영역 찾기(Connected Component Labeling) 등

  - 그래프 알고리즘과의 관계
    -> Union-Find 자체는 그래프를 만들거나 저장하지 않음 (분할/그룹 소속만 관리)
    -> 원본 그래프(간선 리스트 등)는 별도로 존재하고, Union-Find는 그 위에서 그룹 소속 판단만 빠르게 처리하는 보조 도구로 사용됨
*/

public class _8_7_Union_Find {

  static class Edge {

    private int from;
    private int to;
    private int weight;

    public Edge(int from, int to, int weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    public String toString() {
      return "(" + from + ", " + to + ", " + weight + ")";
    }
  }

  static class UnionFind {

    private int[] parent;
    private int[] size;

    public UnionFind(int n) {
      if (n < 1) {throw new IllegalArgumentException("배열 초기화 크기가 너무 작음");}
      this.parent = new int[n];
      this.size = new int[n];

      for (int i = 0; i < n; i++) {
        parent[i] = i;
        size[i] = 1;
      }
    }

    public int recursionFindRoot(int idx) {
      if (idx >= size.length || idx < 0) {throw new IndexOutOfBoundsException("잘못된 인덱스 범위, Idx: " + idx);}
      // x의 루트를 찾아서 반환
      if (parent[idx] == idx) {
        return idx;
      }

      // path compression 적용 지점
      int root = recursionFindRoot(parent[idx]);

      parent[idx] = root;

      return root;
    }

    // 재귀호출은 크기가 클 경우 스택 오버플로우 가능성, 반복문이 기본
    public int LoopingfindRoot(int idx) {
      if (idx >= size.length || idx < 0) {throw new IndexOutOfBoundsException("잘못된 인덱스 범위, Idx: " + idx);}

      int root = idx;

      // root 찾기
      while (parent[root] != root) {
        root = parent[root];
      }

      int cur = idx;

      // idx부터 root까지 다시 한번 순회, 모든 노드의 parent를 root로 직접 연결
      // path compression
      while (parent[cur] != root) {
        int next = parent[cur];
        parent[cur] = root;
        cur = next;
      }

      return root;
    }

    public void union(int x, int y) {
      int rootX = LoopingfindRoot(x);
      int rootY = LoopingfindRoot(y);

      if (rootX == rootY) {return;}

      // 사이즈 비교없이 무조건 한쪽의 루트에 붙인다면 연결리스트처럼 한쪽으로 몰릴 가능성
      if (size[rootX] < size[rootY]) {
        size[rootY] += size[rootX];
        parent[rootX] = rootY;
      } else {
        size[rootX] += size[rootY];
        parent[rootY] = rootX;
      }
    }

    public boolean isConnected(int x, int y) {

      int rootX = LoopingfindRoot(x);
      int rootY = LoopingfindRoot(y);

      return rootX == rootY;
    }
  }

  public static void main(String[] args) {
    int v = 7;
    List<Edge> edges = new ArrayList<>();

    edges.add(new Edge(0, 1, 4));
    edges.add(new Edge(1, 2, 2));
    edges.add(new Edge(3, 4, 5));
    edges.add(new Edge(5, 6, 3));
    edges.add(new Edge(2, 3, 6));
    edges.add(new Edge(0, 4, 7));

    UnionFind uf = new UnionFind(v);

    for (Edge edge : edges) {
      uf.union(edge.from, edge.to);
    }

    int x = 3;
    int y = 4;

    System.out.println("uf.parent = " + Arrays.toString(uf.parent));
    System.out.println("uf.size = " + Arrays.toString(uf.size));
    System.out.println("X: " + x + ", LoopingfindRoot(): " + uf.LoopingfindRoot(x));
    // System.out.println("X: " + x + ", RecursionFindRoot(): " + uf.recursionFindRoot(x));

    System.out.println("X: " + x + ", Y: " + y + ", IsConnected: " + uf.isConnected(x, y));
  }
}
