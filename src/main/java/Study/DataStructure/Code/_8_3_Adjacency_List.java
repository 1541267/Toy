package Study.DataStructure.Code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/*
- Adjacency List (인접 리스트): 정점마다 자신과 직접 연결된 정점들의 목록을 따로 보관 (Map<정점, 인접정점집합>)
  - 연산
    -> addVertex(e) -> O(1)
    -> removeVertex(e) -> O(degree), 이 정점과 연결된 이웃들의 수만큼만 순회 (Matrix의 O(V)와 대비됨)
    -> addEdge(u,v) & removeEdge(u,v) & hasEdge(u,v) -> 이론상 O(degree)이지만, 내부 자료구조를 Set으로 선택해 O(1) 달성
    -> getAdjacentVertices(v) -> O(degree), 실제 연결된 만큼만 순회 (불필요한 스캔 없음)

  - 내부 자료구조 선택: List가 아닌 LinkedHashSet 사용
    -> List(ArrayList 등)로 구현 시: addEdge를 중복 호출하면 같은 간선이 여러 번 쌓임
      --> Matrix는 matrix[u][v]=1을 여러 번 대입해도 값 불변이라 문제 없었지만, List는 add()가 중복을 그대로 허용
    -> Set으로 전환 시: 구조적으로 중복 간선을 원천 차단, contains()가 O(1)이라 addEdge/hasEdge 성능도 개선
    -> LinkedHashSet 선택 이유: 중복 제거 + 삽입 순서 보존을 동시에 만족 (순회 시 항상 추가한 순서대로 나옴, HashSet은 순서 보장 안 됨)
    -> Java 21+ 에서는 LinkedHashSet이 SequencedSet을 구현 -> addLast()/getFirst() 등 순서 명시적 조작 메서드 사용 가능
      --> add()와 결과는 동일하나, 순서가 의미를 가진다는 의도를 코드로 더 명확히 표현

  - 정점 존재 여부 표현: Matrix와 동일한 원칙 적용
    -> Set<Integer> vertexFlag를 별도로 분리해서 정점 존재와 간선 연결(adjacencyMap)의 책임을 완전히 분리
    -> Map은 정점 인덱스를 key로 사용하므로 capacity를 미리 고정할 필요 없음
      --> Matrix 대비 정점 개수를 몰라도 유연하게 확장 가능하다는 것이 실질적 장점

  - removeVertex(vertex)에서 반드시 처리해야 하는 것: dangling reference(허상 참조) 방지
    -> 이 정점 자신의 리스트(adjacencyMap.remove(vertex))만 지우면 안 됨
    -> 이 정점을 인접 목록에 들고 있는 다른 모든 이웃 정점들의 리스트에서도 이 정점을 제거해야 함
    -> 누락 시 이미 삭제된 정점이 다른 정점의 getAdjacentVertices 결과에 계속 남아있는 버그 발생
    -> 비용: O(degree) - Matrix의 O(V)와 다른 특성
      --> 연결이 적은 정점을 지울 땐 List가 더 빠르고, 연결이 많은(dense) 정점을 지울 땐 Matrix와 비슷해짐

  - addEdge/removeEdge 작성 시 주의: 대칭성
    -> 무방향 간선 u-v는 u의 리스트에 v 저장 + v의 리스트에 u 저장 두 곳에 나뉘어 저장됨

  - Matrix vs List 최종 비교
    -> 공간: Matrix O(V²) 고정 vs List O(V+E) (실제 간선 수만큼만)
    -> hasEdge: 이론상 Matrix O(1) > List O(degree)이지만, List의 내부구조를 Set으로 택하면 List도 O(1) 달성 가능 (자료구조 선택이 이론적 복잡도를 실제로 바꾼 사례)
    -> removeVertex: Matrix O(V) (행/열 전체 스캔) vs List O(degree) (연결된 만큼만)
    -> 순회 순서: Matrix는 인덱스 오름차순 고정, List(LinkedHashSet)는 삽입 순서 그대로
    -> 실무 기준: 정점 수 많고 간선이 희소(sparse)한 경우가 대부분이라 List가 표준 선택 (SNS, 도로망, 웹 그래프 등)
*/

public class _8_3_Adjacency_List {

  static class AdjacencyList {

    Map<Integer, LinkedHashSet<Integer>> adjacencyMap;
    Set<Integer> vertexFlag = new HashSet<>();

    private int vertexCount = 0;

    public AdjacencyList() {
      this.adjacencyMap = new HashMap<>();
    }

    public void addVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (vertexFlag.contains(vertex)) {
        throw new IllegalStateException("이미 추가 되어있는 정점");
      }

      vertexFlag.add(vertex);
      adjacencyMap.put(vertex, new LinkedHashSet<>());
      vertexCount++;
    }

    public void removeVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (!vertexFlag.contains(vertex) || adjacencyMap.get(vertex) == null) {
        throw new IllegalStateException("추가 되어있지 않은 정점 삭제 시도");
      }

      for (Integer num : adjacencyMap.get(vertex)) {
        adjacencyMap.get(num).remove(vertex);
      }

      vertexFlag.remove(vertex);
      adjacencyMap.remove(vertex);
      vertexCount--;
    }

    public boolean hasVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      return vertexFlag.contains(vertex) && adjacencyMap.get(vertex) != null;
    }


    public void addEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      LinkedHashSet<Integer> list = adjacencyMap.get(u);
      LinkedHashSet<Integer> list2 = adjacencyMap.get(v);

      if (list.contains(v)) {
        throw new IllegalArgumentException("이미 추가 되어있는 간선 추가 시도, U: " + u + ", V: " + v);
      }

      list.addLast(v);
      list2.addLast(u);
    }


    public void removeEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      LinkedHashSet<Integer> list = adjacencyMap.get(u);
      LinkedHashSet<Integer> list2 = adjacencyMap.get(v);

      if (!list.contains(v)) {
        throw new IllegalArgumentException("추가 되어있지 않은 간선 제거 시도");
      }

      list.remove(v);
      list2.remove(u);
    }

    public boolean hasEdge(int u, int v) {
      idxCheckWhileEdge(u, v);
      LinkedHashSet<Integer> list = adjacencyMap.get(u);

      return list.contains(v);
    }

    public ArrayList<Integer> getAdjacentVertices(int vertex) {
      idxCheckWhileVertex(vertex);

      if (!adjacencyMap.containsKey(vertex)) {
        throw new IllegalArgumentException("정점이 존재하지 않는 인덱스 조회");
      }

      return new ArrayList<>(adjacencyMap.get(vertex));
    }

    private void idxCheckWhileVertex(int idx) {
      if (idx < 0) {
        throw new IndexOutOfBoundsException("잘못된 Vertex idx: " + idx);
      }
    }

    private void idxCheckWhileEdge(int u, int v) {
      if (u == v) {throw new IllegalArgumentException("Self Looping");}
      if (u < 0 || v < 0) {
        throw new IndexOutOfBoundsException("IOOB, U: " + u + ", V: " + v);
      }

      if (!vertexFlag.contains(u) || !vertexFlag.contains(v)) {
        throw new IllegalArgumentException("Vertex가 없는 곳에 Edge 추가 혹은 삭제 시도, U: " + u + ", V: " + v);
      }
    }

    public void printAllAdjacency(int size) {
      for (int i = 0; i < size; i++) {
        if (vertexFlag.contains(i)) {
          System.out.println(i + ": " + getAdjacentVertices(i));
        }
      }
      System.out.println("==========================================================");
    }
  }

  public static void main(String[] args) {
    AdjacencyList graph = new AdjacencyList();

    // 정점 추가
    for (int i = 0; i < 5; i++) {
      graph.addVertex(i);
    }

    // 간선 연결: 0-1, 0-2, 1-2, 2-3, 3-4
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    graph.addEdge(3, 4);

    graph.printAllAdjacency(5);
    System.out.println("""
            기대 결과
            [1, 2]
            [0, 2]
            [0, 1, 3]
            [2, 4]
            [3]
        """);

    // hasEdge 확인
    System.out.println("\nhasEdge(0,1): " + graph.hasEdge(0, 1)); // true
    System.out.println("hasEdge(0,3): " + graph.hasEdge(0, 3));   // false
    System.out.println("==========================================================");

    // removeEdge 테스트 -> 양쪽 리스트 모두에서 대칭적으로 지워졌는지가 핵심 검증 포인트
    graph.removeEdge(0, 2);
    System.out.println("\nremoveEdge(0,2) 이후:");
    System.out.println("""
        기대 결과
        [1]        <- 2가 빠짐
        [1, 3]      <- 0이 빠짐 (여기가 이전 버그로 안 빠지던 지점)
        """);
    graph.printAllAdjacency(5);

    // removeVertex 테스트 -> 이 정점을 참조하던 "다른" 정점들의 리스트에서도 지워지는지가 핵심
    graph.removeVertex(2);
    System.out.println("\nremoveVertex(2) 이후:");
    System.out.println("vertexCount = " + graph.vertexCount);
    System.out.println("""
            기대 결과
            [1]         <- 원래 2가 없었으니 그대로
            [0]          <- 2가 빠짐
            조회 시 예외 (정점 자체가 삭제됨)
            [4]          <- 2가 빠짐
            [3]
        """);

    graph.printAllAdjacency(5);

    // 예외 케이스 확인
    try {
      graph.getAdjacentVertices(2);
    } catch (IllegalArgumentException e) {
      System.out.println("\n예상된 예외 발생: " + e.getMessage());
    }

    try {
      graph.addEdge(0, 0); // self-loop 시도
    } catch (IllegalArgumentException e) {
      System.out.println("예상된 예외 발생: " + e.getMessage());
    }

    try {
      graph.addEdge(1, 1); // 이미 없는 정점 간 간선
    } catch (Exception e) {
      System.out.println("예상된 예외 발생: " + e.getMessage());
    }
  }
}
