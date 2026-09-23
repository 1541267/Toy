package Study.DataStructure.Code;

import java.util.ArrayList;

/*
- Adjacency Matrix (인접 행렬): V개의 정점을 V×V 2차원 배열로 표현, matrix[i][j] = i-j 간선 존재 여부(또는 가중치)
  - 연산
    -> addVertex(e) & removeVertex(e) -> O(1) / O(V)
    -> addEdge(u,v) & removeEdge(u,v) & hasEdge(u,v) -> O(1), 배열 인덱스 직접 접근이라 상수 시간
    -> getAdjacentVertices(v): v행 전체를 스캔해야 함 -> O(V)

  - 정점 존재 여부 표현 방식
    -> 처음엔 matrix[v][v]를 정점 존재 플래그로 재활용하는 방식으로 시도 (별도 공간 없이 절약)
    -> 문제점: self-loop(자기 자신 참조 간선)을 표현할 공간이 없어짐 (대각선이 이미 존재플래그로 점유됨)
    -> 문제점: 가중치 그래프로 확장 시 대각선에 weight를 넣을지 존재플래그로 쓸지 충돌
    -> 개선: boolean[] vertexExists 배열을 별도로 분리 -> 정점 존재와 간선 연결의 책임을 분리
    --> Circular Queue에서 size 필드를 따로 둬서 꽉 참/빔을 구분했던 것과 동일한 설계 원칙
        (하나의 상태 변수가 두 가지 의미를 겸하면 결국 어딘가에서 충돌)

  - self-loop(자기 참조 간선) 정책
    -> 상태 머신, 오토마타, 마르코프 체인 등 자기 자신으로 돌아오는 전이가 있는 도메인에서는 필요
    -> 도로망/친구관계 등 일반적인 그래프 문제에서는 불필요
    -> 학습 단계에서는 addEdge(u,v)에서 u==v를 예외로 막아 원천 차단 (boolean[] 분리 이후엔 기술적으로는 지원 가능한 상태)
    -> 지원 시 추가로 고려할 점: getAdjacentVertices에서 자기 자신 포함 여부, degree 계산 시 +2 처리, BFS/DFS의 무한루프 방지(visited)

  - removeVertex(vertex) 시 처리
    -> 해당 정점의 행(row)과 열(column) 전체를 0으로 초기화해야 완전히 제거
    -> 무방향 그래프는 matrix[i][v] == matrix[v][i]로 대칭이므로 한 루프에서 양쪽을 동시에 처리 가능 (이중 루프 불필요)
    -> 비용: O(V) (정점 수만큼 행/열을 순회)

  - 장단점
    -> 장점: 간선 존재 확인이 O(1), 구현이 직관적, dense 그래프(간선이 촘촘)에서 공간 낭비 적음
    -> 단점: 공간복잡도 O(V²) 고정 (실제 간선 수와 무관하게 항상 V² 칸 확보)
    -> 단점: 인접 정점 조회 시 연결 안 된 칸까지 전부 스캔해야 함 -> sparse 그래프에서 비효율
    -> 적합한 경우: 정점 수가 적고 고정적, 간선이 촘촘함, 두 정점이 연결됐는가를 자주 O(1)로 확인해야 하는 경우
*/

public class _8_2_Adjacency_Matrix {

  static class DiagonalExistFlagAdjacencyMatrix {

    private int[][] matrix;
    private int vertexCount;
    private int capacity;

    public DiagonalExistFlagAdjacencyMatrix(int capacity) {
      if (capacity < 1) {throw new IllegalArgumentException("배열 초기화 길이가 너무 작음");}
      this.capacity = capacity;
      vertexCount = 0;
      initMatrix(capacity);
    }

    public int getVertexCount() {
      return vertexCount;
    }

    public void initMatrix(int capacity) {
      this.matrix = new int[capacity][capacity];
    }

    public void addVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (matrix[vertex][vertex] != 0) {
        throw new IllegalStateException("이미 추가 되어있는 Vertex");
      }

      matrix[vertex][vertex] = 1;
      vertexCount++;
    }

    public void removeVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (matrix[vertex][vertex] == 0) {
        throw new IllegalStateException("저장되어있는 Vertex가 없음");
      }

      matrix[vertex][vertex] = 0;
      vertexCount--;

      // 이 정점과 연결된 모든 간선(자기 자신에 대한 self-loop 포함) 제거
      for (int i = 0; i < capacity; i++) {
        matrix[i][vertex] = 0;
        matrix[vertex][i] = 0;
      }
    }

    private boolean isVertexExist(int u) {
      return matrix[u][u] == 1;
    }

    public void addEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      if (matrix[u][v] == 1) {return;}

      matrix[u][v] = 1;
      matrix[v][u] = 1;
    }

    public void removeEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      if (matrix[u][v] == 0) {return;}
      matrix[u][v] = 0;
      matrix[v][u] = 0;
    }

    public boolean hasEdge(int u, int v) {
      idxCheckWhileEdge(u, v);
      return matrix[u][v] == 1;
    }

    // v 와 인접한 정점들 반환
    public ArrayList<Integer> getAdjacentVertices(int v) {
      idxCheckWhileVertex(v);
      ArrayList<Integer> result = new ArrayList<>();

      for (int i = 0; i < matrix[v].length; i++) {
        if (matrix[v][i] == 1 && i != v) {
          result.add(i);
        }
      }
      return result;
    }

    private void idxCheckWhileEdge(int u, int v) {
      if (u == v) {throw new IllegalArgumentException("Self Looping");}
      if (u > capacity - 1 || v > capacity - 1 || u < 0 || v < 0) {
        throw new IndexOutOfBoundsException("IOOB, U: " + u + ", V: " + v);
      }

      if (!isVertexExist(u) || !isVertexExist(v)) {
        throw new IllegalArgumentException("Vertex가 없는 곳에 Edge 추가 혹은 삭제 시도, U: " + u + ", V: " + v);
      }
    }

    private void idxCheckWhileVertex(int idx) {
      if (idx > capacity - 1 || idx < 0) {
        throw new IndexOutOfBoundsException("IOOB, idx: " + idx);
      }
    }

    public void printMatrix() {
      for (int[] first : matrix) {

        for (int second : first) {
          System.out.print("[" + second + "]");
        }
        System.out.println();
      }
      System.out.println("==========================================================");
    }
  }

  static class BooleanExistFlagAdjacencyMatrix {

    private int[][] matrix;
    private boolean[] vertexExists;
    private int vertexCount;
    private int capacity;

    public BooleanExistFlagAdjacencyMatrix(int capacity) {
      if (capacity < 1) {throw new IllegalArgumentException("배열 초기화 길이가 너무 작음");}
      this.capacity = capacity;
      this.vertexCount = 0;
      this.matrix = new int[capacity][capacity];
      this.vertexExists = new boolean[capacity];
    }

    public int getVertexCount() {
      return vertexCount;
    }

    public void addVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (vertexExists[vertex]) {
        throw new IllegalStateException("이미 추가 되어있는 Vertex");
      }

      vertexExists[vertex] = true;
      vertexCount++;
    }

    public void removeVertex(int vertex) {
      idxCheckWhileVertex(vertex);

      if (!vertexExists[vertex]) {
        throw new IllegalStateException("저장되어있는 Vertex가 없음");
      }

      vertexExists[vertex] = false;
      vertexCount--;

      for (int i = 0; i < capacity; i++) {
        matrix[i][vertex] = 0;
        matrix[vertex][i] = 0;
      }
    }

    public boolean hasVertex(int vertex) {
      idxCheckWhileVertex(vertex);
      return vertexExists[vertex];
    }

    public void addEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      if (matrix[u][v] == 1) {return;}

      matrix[u][v] = 1;
      matrix[v][u] = 1;
    }

    public void removeEdge(int u, int v) {
      idxCheckWhileEdge(u, v);

      if (matrix[u][v] == 0) {return;}
      matrix[u][v] = 0;
      matrix[v][u] = 0;
    }

    public boolean hasEdge(int u, int v) {
      idxCheckWhileEdge(u, v);
      return matrix[u][v] == 1;
    }

    // v 와 인접한 정점들 반환
    public ArrayList<Integer> getAdjacentVertices(int v) {
      idxCheckWhileVertex(v);
      ArrayList<Integer> result = new ArrayList<>();

      for (int i = 0; i < capacity; i++) {
        if (matrix[v][i] == 1 && i != v) {
          result.add(i);
        }
      }
      return result;
    }

    private void idxCheckWhileEdge(int u, int v) {
      if (u == v) {throw new IllegalArgumentException("Self Looping");}
      if (u > capacity - 1 || v > capacity - 1 || u < 0 || v < 0) {
        throw new IndexOutOfBoundsException("IOOB, U: " + u + ", V: " + v);
      }

      if (!vertexExists[u] || !vertexExists[v]) {
        throw new IllegalArgumentException("Vertex가 없는 곳에 Edge 추가 혹은 삭제 시도, U: " + u + ", V: " + v);
      }
    }

    private void idxCheckWhileVertex(int idx) {
      if (idx > capacity - 1 || idx < 0) {
        throw new IndexOutOfBoundsException("IOOB, idx: " + idx);
      }
    }

    public void printMatrix() {
      for (int[] row : matrix) {
        for (int val : row) {
          System.out.print("[" + val + "]");
        }
        System.out.println();
      }
      System.out.println("==========================================================");
    }
  }

  public static void main(String[] args) {
    DiagonalExistFlagAdjacencyMatrix graph = new DiagonalExistFlagAdjacencyMatrix(5);

    // 정점 5개 추가
    for (int i = 0; i < 5; i++) {
      graph.addVertex(i);
    }

    // 간선 연결: 0-1, 0-2, 1-2, 2-3, 3-4
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    graph.addEdge(3, 4);

    graph.printMatrix();
    System.out.println("""
        기대 결과
        0: [1, 2]
        1: [0, 2]
        2: [0, 1, 3]
        3: [2, 4]
        4: [3]
        """);
    System.out.println("==========================================================");
    // hasEdge 확인
    System.out.println("\nhasEdge(0,1): " + graph.hasEdge(0, 1)); // true
    System.out.println("hasEdge(0,3): " + graph.hasEdge(0, 3));   // false

    // removeEdge 테스트 -> 대칭 삭제 확인 (matrix[u][v], matrix[v][u] 둘 다)
    System.out.println("==========================================================");
    graph.removeEdge(0, 2);
    System.out.println("\nremoveEdge(0,2) 이후:");
    System.out.println("""
        기대 결과:
        0: [1]
        2: [1, 3]
        """);
    graph.printMatrix();

    // 5. removeVertex 테스트 -> 이 정점의 행/열 전체가 다 지워지는지 확인
    graph.removeVertex(2);
    System.out.println("\nremoveVertex(2) 이후:");
    System.out.println("vertexCount = " + graph.getVertexCount());
    System.out.println("""
        기대 결과:
        0: [1]
        1: [0]
        2: hasVertex(2) == false, getAdjacentVertices(2) 호출 시 확인 필요
        3: [4]
        4: [3]
        """);
    graph.printMatrix();

    // 예외 케이스 확인
    try {
      graph.addEdge(0, 0); // self-loop 시도
    } catch (IllegalArgumentException e) {
      System.out.println("\n예상된 예외 발생: " + e.getMessage());
    }

    try {
      graph.addEdge(2, 3); // 이미 삭제된 정점 2로 간선 추가 시도
    } catch (IllegalArgumentException e) {
      System.out.println("예상된 예외 발생: " + e.getMessage());
    }

    try {
      graph.getAdjacentVertices(10); // capacity 밖의 인덱스
    } catch (IndexOutOfBoundsException e) {
      System.out.println("예상된 예외 발생: " + e.getMessage());
    }
  }
}
