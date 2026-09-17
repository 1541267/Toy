package Study.DataStructure.Code;

/*
- AVL (Adelson-Velsky and Landis) 자가 균형 이진 탐색 트리 (Self-Balancing BST): 삽입 순서에 관계없이 트리의 높이를 O(log n)으로 유지
  - 필요한 이유
    -> 일반 BST는 삽입 순서에 따라 모양이 완전히 달라짐
    -> 정렬된 데이터를 순서대로 삽입하면 트리가 한쪽으로 길게 늘어져 사실상 연결리스트가 됨 (높이 O(n))
    -> get/insert/remove 모두 O(log n)을 전제로 설계된 BST의 장점이 이 경우 전부 사라짐
    -> 트리는 균형 잡혀 있다 는 가정이 깨지지 않도록, 삽입/삭제마다 스스로 구조를 재조정하는 자가 균형 트리

  - AVL Tree
    -> 균형 조건: 모든 노드의 balance factor(왼쪽 서브트리 높이 - 오른쪽 서브트리 높이)가 -1, 0, 1 중 하나
    -> 조건을 벗어나면(-2 또는 +2) 즉시 rotation으로 고침 -> 조회(search)가 매우 빠른 대신 삽입/삭제 시 재조정 빈도가 RB Tree보다 잦음
    -> null의 height는 -1, leaf의 height는 0으로 두는 관례 -> updateHeight()의 "1 + max(left, right)" 공식이 leaf에서도 일관되게 성립
    --> 인스턴스 메서드로는 "자기 자신이 null인지" 스스로 판단 불가능 (null에는 메서드 호출조차 안 됨)
    -> null-safe height 조회는 static 메서드로 분리해서 파라미터로 받은 노드를 그대로 판단해야 함

  - 회전(불균형이 생기는) 4가지 패턴
    -> LL: 왼쪽 자식의 왼쪽이 무거움 -> 오른쪽 회전(rotateRight) 1회
    -> LR: 왼쪽 자식의 오른쪽이 무거움 -> 왼쪽 자식 기준 rotateLeft 후 현재 노드 기준 오른쪽 회전
    -> RR: 오른쪽 자식의 오른쪽이 무거움 -> 왼쪽 회전(rotateLeft) 1회
    -> RL: 오른쪽 자식의 왼쪽이 무거움 -> 오른쪽 자식 기준 rotateRight 후 현재 노드 기준 rotateLeft
    -> LR/RL이 2번 회전인 이유: 1번만 돌리면 모양은 LL/RR처럼 바뀌어도 여전히 불균형이 남음
    --> 먼저 자식 기준 회전으로 LR/RL을 LL/RR 모양으로 바꾼 뒤, 같은 회전 로직을 재사용
    -> 회전 시 자식의 서브트리(위 예시의 T3)를 반드시 반대편으로 옮겨야 BST 성질(왼쪽 < 노드 < 오른쪽) 유지됨
    -> 회전 함수는 rotateLeft/rotateRight 두 개만 있으면 됨 (LR/RL은 이 둘의 조합)
    -> 회전 후에는 반드시 자식 노드 -> 새 루트 순서로 updateHeight() 호출 (순서 반대로 하면 잘못된 높이가 나옴)

  - 회전 tkdtp 과정
    -> RotateLeft & Right 간단히 생각하면 기준 노드를 반시계(Left), 시계(Right) 돌리는 동작
    -> 돌리면서 BST의 구조를 유지하기 위해 기존 Left or Right 노드의 자식을 기준 노드의 자식으로 넣어줘야 함
    예로 A < X < Y < C < B 의 BST 상태
         A
          \
           B
          /
         Y
        / \
       X   C
    A 기준 RL의 상태(R인 B의 L가 무거움(높이가 몰려있음)) 에서 먼저 A의 Right 를 rotateRight 1회 진행
         A
          \
           Y
          /\ \
         X  C  B
    Y 와 B가 회전 하며 부분적으로 RR 이되나 그대로 Y가 갖고 있던 자식 X와 C가 BST 구조를 깨트림
    기존 구조인 A < X < Y < C < B 의 상태로 C가 B의 Left로 이동해야함
         A
          \
           Y
          / \
         X   B
            /
           C
    A 기준 RR이 된 상태에서 A 기준 rotateLeft를 1회 진행하여
         Y
        / \
       A,X B
          /
         C
    의 모양을 만드는데 여기서 X 는 기존 트리에서 Y보단 작고 A 보단 크기 때문에 A의 Right로 이동해야함
         Y
       /   \
      A     B
       \   /
        X C
  ---------------------------------------------------------------------------------
    LR 상태에선 B < X < Y < C < A 의 구조
         A
        /
       B
        \
         Y
        / \
       X   C
    A기준 L(B) R(Y) 상태이므로 B를 먼저 rotateLeft로 1회 수행
           A
          /
         Y
        / /\
       B X  C
    BST 구조를 유지하기 위해 B보단 크며 Y보다 작은 X가 B의 Right로 이동
           A
          /
         Y
        / \
       B   C
        \
         X
    A 기준 LL 이 된 상태에서 A 기준 rotateRight 1회 수행
           Y
          /\ \
         B  C A
          \
           X
    여기서 다시 BST 구조를 유지하기 위해 C는 B보다 크고 Y보다
    B < X < Y < C < A 의 상태로 C가 Y보단 크고 A 보단 작기 때문에 A의 Left로 이동
           Y
          /  \
         B    A
          \   /
           X C

  - 재귀 구조에서 재조정 위치
    -> Java는 포인터가 없으므로, 재귀 insert/delete는 처리 후 이 서브트리의 새 루트를 반환해야 부모가 다시 연결 가능
    -> 재귀에서 돌아오는 길(자식 호출 이후, return 직전)마다 updateHeight() -> balance factor 체크 -> 필요시 회전을 반복
    -> insert와 delete 양쪽에서 이 로직이 완전히 동일하므로 rebalance() 공용 메서드로 사용 가능

  - Insert에서의 재조정
    -> 삽입은 한 번에 한 노드만 추가되므로, 불균형이 발생한 자식의 balance factor가 정확히 0이 될 수 없음
    -> 즉 LL/RR과 LR/RL을 구별할 때 0을 어느 쪽으로 볼지 고민할 필요가 없음

  - Delete에서의 재조정 (Insert와 다른 점)
    -> 리프 노드를 삭제해서 node 자체가 null이 되는 경우가 있음 -> rebalance() 호출 전에 null 가드로 먼저 걸러야 함
    -> 삭제는 서브트리 전체가 통째로 낮아질 수 있어서, 불균형 자식의 balance factor가 정확히 0인 상황이 생길 수 있음
       (삽입에서는 없던 케이스) -> 0을 LL/RR과 LR/RL 중 어느 쪽으로 처리해도 결과 트리는 유효한 AVL이지만,
       분기 조건에서 0을 명시적으로 어느 한쪽에 포함시켜야 함
    -> 삭제도 이론상 4가지 케이스(LL/LR/RR/RL) 전부 발생 가능 -> 재조정 로직에서 케이스를 생략하면 안 됨
    -> 자식이 둘인 노드 삭제 시: 오른쪽 서브트리의 최솟값(successor)으로 데이터만 교체하고,
       그 successor 노드를 재귀적으로 삭제 -> 구조가 아닌 값만 옮기는 방식이라 재배선 부담이 적음

  - Red-Black Tree와의 트레이드오프 (개념)
    -> AVL: 균형 조건이 엄격(-1~1) -> 트리가 더 평평함 -> search가 빠름, 대신 insert/delete 시 rotation이 더 잦음
    -> Red-Black: 균형 조건이 느슨함(빨강 노드 규칙 등 5가지 속성) -> AVL보다 트리가 조금 더 기울 수 있지만,
       삽입/삭제 시 recoloring 위주로 처리되어 rotation 빈도가 AVL보다 적음 -> 쓰기가 잦은 워크로드에 유리
    -> 실무 사용처: Java TreeMap/TreeSet, C++ std::map, 리눅스 커널 스케줄러(CFS) 등이 Red-Black Tree 사용
    -> 면접에서는 완벽 구현보다 왜 균형이 필요한지 + AVL/RB의 트레이드오프를 설명할 수 있는지
*/

public class _5_3_AVL_Tree {

  static class Node<E> {

    E data;
    int height;
    Node<E> left;
    Node<E> right;

    public Node(E data) {
      this.data = data;
      this.height = 0;
    }

    // null-safe, 인스턴스(this) 동작이 아닌 어떤 노드든 적용 가능한 범용 유틸리티
    static <E> int getHeight(Node<E> node) {
      return node == null ? -1 : node.height;
    }

    public int getBalanceFactor() {
      return getHeight(this.left) - getHeight(this.right);
    }

    private void updateHeight() {
      this.height = 1 + Math.max(getHeight(this.left), getHeight(this.right));
    }
  }

  static class AVL<E extends Comparable<E>> {

    Node<E> root;

    public AVL(Node<E> root) {this.root = root;}

    public void insert(E e) {
      if (e == null) {throw new IllegalArgumentException("삽입 하려는 데이터가 null");}
      root = insert(root, e);
    }

    private Node<E> insert(Node<E> curNode, E e) {
      if (curNode == null) {return new Node<>(e);}

      int compare = e.compareTo(curNode.data);

      if (compare == 0) {
        throw new IllegalArgumentException("중복 요소 삽입");
      } else if (compare < 0) {
        curNode.left = insert(curNode.left, e);
      } else {
        curNode.right = insert(curNode.right, e);
      }
      return rebalance(curNode);
    }

    // insert(), doDelete()에서 사용
    private Node<E> rebalance(Node<E> node) {
      node.updateHeight();

      int balanceFactor = node.getBalanceFactor();

      if (balanceFactor > 1) {
        // LL or LR 검사 필요
        balanceFactor = node.left.getBalanceFactor();

        //(LL = Left 1회, LR = Left 1회 후에 Right 1회)
        if (balanceFactor < 0) {
          node.left = rotateLeft(node.left);
        }
        node = rotateRight(node);

      } else if (balanceFactor < -1) {
        // RR or RL 검사 필요
        balanceFactor = node.right.getBalanceFactor();

        // RR = Right 1회, RL = Right 1회 후에 Left 1회
        if (balanceFactor > 0) {
          node.right = rotateRight(node.right);
        }

        node = rotateLeft(node);
      }

      return node;
    }

    private Node<E> rotateLeft(Node<E> node) {
      Node<E> right = node.right;
      Node<E> rightLeft = right.left;

      right.left = node;
      node.right = rightLeft;

      node.updateHeight();
      right.updateHeight();

      return right;
    }

    private Node<E> rotateRight(Node<E> node) {
      Node<E> left = node.left;

      Node<E> leftRight = left.right;

      left.right = node;
      node.left = leftRight;

      node.updateHeight();
      left.updateHeight();
      return left;
    }

    public void delete(E e) {
      if (root == null) {throw new IllegalStateException("비어있는 AVL 트리 삭제 시도");}
      if (e == null) {throw new IllegalArgumentException("삭제 시도 하려는 데이터가 Null");}
      root = doDelete(root, e);
    }

    private Node<E> doDelete(Node<E> node, E e) {

      if (node == null) {return null;}

      int compare = e.compareTo(node.data);

      if (compare < 0) {
        node.left = doDelete(node.left, e);
      } else if (compare > 0) {
        node.right = doDelete(node.right, e);
      } else {

        if (node.left == null && node.right == null) {
          return null;
        } else if (node.left == null || node.right == null) {
          node = node.left == null ? node.right : node.left;
        } else {
          Node<E> successor = findMin(node.right);
          node.data = successor.data;
          node.right = doDelete(node.right, successor.data);
        }
      }
      return rebalance(node);
    }

    private Node<E> findMin(Node<E> node) {
      Node<E> curNode = node;

      while (curNode.left != null) {
        curNode = curNode.left;
      }
      return curNode;
    }

    public void printTree() {
      printTree(root, "", "");
      System.out.println("==========================================================");
    }

    private void printTree(Node<E> node, String prefix, String branch) {
      if (node == null) {return;}
      System.out.println(prefix + branch + node.data);
      printTree(node.left, prefix + "  ", "L─ ");
      printTree(node.right, prefix + "  ", "R─ ");
    }

    public String toString(Node<E> node) {
      return node != null ? "Node Data: " + node.data : "Cannot Find Node";
    }
  }

  public static void main(String[] args) {
    // LL Case, RotateRight 1회 
    _5_3_AVL_Tree.AVL<Integer> LLAVL = new AVL<>(new Node<>(30));
    LLAVL.insert(20);
    LLAVL.insert(10);
    System.out.println("[삽입 LL]");
    LLAVL.printTree();

    // LR Case, Left 1회 Right 1회
    AVL<Integer> LRAVL = new AVL<>(new Node<>(30));
    LRAVL.insert(10);
    LRAVL.insert(20);
    System.out.println("[삽입 LR]");
    LRAVL.printTree();

    // RR Case, Left 1회
    AVL<Integer> RRAVL = new AVL<>(new Node<>(10));
    RRAVL.insert(20);
    RRAVL.insert(30);
    System.out.println("[삽입 RR]");
    RRAVL.printTree();

    // RL Case, Right 1회 Left 1회
    AVL<Integer> RLAVL = new AVL<>(new Node<>(10));
    RLAVL.insert(30);
    RLAVL.insert(20);
    System.out.println("[삽입 RL]");
    RLAVL.printTree();

    // 삭제로 인한 LL 재조정: 오른쪽 노드를 지워서 왼쪽이 상대적으로 무거워짐
    AVL<Integer> deleteLL = new AVL<>(new Node<>(50));
    for (int v : new int[]{30, 70, 20, 40, 60, 80, 10}) {deleteLL.insert(v);}
    deleteLL.printTree();
    System.out.println("80 삭제, LL 발생 및 재조정");
    deleteLL.delete(80);
    deleteLL.printTree();

    // 삭제로 인한 RR 재조정: 위 트리를 좌우 대칭시킨 버전
    AVL<Integer> deleteRR = new AVL<>(new Node<>(50));
    for (int v : new int[]{70, 30, 80, 60, 40, 20, 90}) {deleteRR.insert(v);}
    deleteRR.printTree();
    System.out.println("20 삭제, RR 발생 및 재조정");
    deleteRR.delete(20);
    deleteRR.printTree();

    // 리프 노드 삭제 (재조정 없음, 구조만 확인)
    AVL<Integer> deleteLeaf = new AVL<>(new Node<>(50));
    for (int v : new int[]{30, 70, 20, 40}) {deleteLeaf.insert(v);}
    deleteLeaf.printTree();
    System.out.println("20(리프 노드) 삭제");
    deleteLeaf.delete(20);
    deleteLeaf.printTree();

    // 자식이 둘인 노드 삭제 (successor로 대체되는 경로 확인)
    AVL<Integer> deleteTwoChildren = new AVL<>(new Node<>(50));
    for (int v : new int[]{30, 70, 20, 40, 60, 80}) {deleteTwoChildren.insert(v);}
    deleteTwoChildren.printTree();
    deleteTwoChildren.delete(30);
    deleteTwoChildren.printTree();
  }
}
