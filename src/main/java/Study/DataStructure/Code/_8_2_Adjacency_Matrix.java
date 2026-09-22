package Study.DataStructure.Code;

import java.util.ArrayList;

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
      System.out.println("==========================================================");
      for (int[] first : matrix) {

        for (int second : first) {
          System.out.print("[" + second + "]");
        }
        System.out.println();
      }
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
      indexCheckWhileVertex(vertex);

      if (vertexExists[vertex]) {
        throw new IllegalStateException("이미 추가 되어있는 Vertex");
      }

      vertexExists[vertex] = true;
      vertexCount++;
    }

    public void removeVertex(int vertex) {
      indexCheckWhileVertex(vertex);

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
      indexCheckWhileVertex(vertex);
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
      indexCheckWhileVertex(v);
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

    private void indexCheckWhileVertex(int idx) {
      if (idx > capacity - 1 || idx < 0) {
        throw new IndexOutOfBoundsException("IOOB, idx: " + idx);
      }
    }

    public void printMatrix() {
      System.out.println("==========================================================");
      for (int[] row : matrix) {
        for (int val : row) {
          System.out.print("[" + val + "]");
        }
        System.out.println();
      }
    }
  }

  public static void main(String[] args) {
    DiagonalExistFlagAdjacencyMatrix matrix = new DiagonalExistFlagAdjacencyMatrix(10);

  }
}
