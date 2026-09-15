package Study.DataStructure.Code;

import java.util.ArrayList;
import java.util.List;

/*
- 이진 탐색 트리(Binary Search Tree, BST)
  - 규칙: 모든 노드에 대해 [왼쪽 서브트리의 모든 값] < [노드 값] < [오른쪽 서브트리의 모든 값]

  - 이 규칙 하나로 얻는 이점
    -> 탐색: 찾는 값이 크면 오른쪽, 작으면 왼쪽으로만 이동하기 때문에
    --> 매 단계 후보가 절반씩 줄어듦(이분탐색과 동일 원리) -> 균형 시 O(log n)

    -> 삽입/삭제 : 탐색과 같은 방식으로 위치를 찾고 링크만 변경 -> 균형 시 O(log n)
    -> inOrder 순회 = 오름차순 정렬된 결과 (BST의 핵심 성질, 검증 수단으로도 사용)

  - 균형이 깨지면? 하는 함정 존재
    -> ex) [1, 2, 3, 4, ~] 처럼 이미 정렬된 순서로 insert하면 매번 compare > 0만 나와서
    --> 오른쪽으로만 자식이 붙는 편향 트리(사실상 연결리스트) 형태가 됨
    --> 탐색/삽입/삭제 모두 최악의 경우 O(n)으로 저하
    -> 이 문제를 해결하는 게 AVL / Red-Black Tree

  - insert(e): 반복문 + parent 추적 방식
    -> curNode를 root부터 시작해서 compareTo 결과에 따라 left/right로 내려가며
    -> 동시에 parent도 한 칸씩 따라 내려감
    -> curNode가 null이 되는 시점(= 삽입할 빈 자리를 찾은 시점)에 반복 종료
    -> 마지막에 parent와 비교해서 그 자리(left/right)에 새 노드 연결
    -> 중복 값 삽입 시 예외 처리

  - search(e)
    -> insert와 동일한 탐색 로직(compareTo로 left/right 이동)
    -> 단, 값을 찾으면 그 노드를 즉시 반환, 끝까지 못 찾으면(curNode == null) null 반환

  - delete(e) - 재귀 + 서브트리의 새 루트를 리턴 방식
    -> insert/search와 달리 parent를 별도로 추적하지 않고
    -> doDelete(node, e)가 이 서브트리의 (바뀌었을 수도 있는) 루트를 리턴하고
    -> 호출부에서 node.left = doDelete() / node.right = doDelete() 형태로
    -> 그 결과를 자기 자식 자리에 그대로 대입하는 패턴
    -> 이 대입 패턴 하나로 모든 링크 갱신이 자동 처리됨

    -> delete(e) 호출부 에서도 root = doDelete(root, e)로 재대입 필요
    --> doDelete() 내부에서의 변화는 지역 변수의 변화 뿐
    --> 리턴값을 명시적으로 대입해줘야 실제 링크가 끊기거나 교체

  - delete() 삭제시 해당 노드의 상태별 변화
    -> 1번 케이스, 자식이 없는(leaf)경우:  node = null 리턴 (실제 삭제가 되는 순간)
    --> 이 null이 호출부의 node.left(or right) = doDelete()에 대입되면서 부모 -> 이 노드로의 링크가 끊어짐

    -> 2번 케이스, 자식이 하나(left or right)만 있는 경우: 있는 자식을 그대로 리턴 -> 그 자식이 삭제된 노드의 자리를 대신 차지

    -> 3번 케이스, 자식이 둘 다 있음 (값 바꿔치기 전략)
    --> 원리: 구멍 자리에는 (왼쪽 서브트리 전체보다 크면서 오른쪽 서브트리 전체보다는 작거나 같은 값]이 들어가야 규칙이 안 깨짐
    --> 그 조건을 만족하는 값 = 오른쪽 서브트리에서 가장 작은 값(successor)
    --> 오른쪽 서브트리 중 최소이므로 오른쪽 값들보다 작거나 같고, 애초에 오른쪽 서브트리 소속이므로 왼쪽 값들보다는 큼
    ---> 과정
       1. successor = findMin(node.right)  // 오른쪽 서브트리의 최솟값 노드 탐색
       2. node.data = successor.data       // 노드 객체(링크)는 그대로 두고 값만 교체
                                           // 이 시점엔 트리에 같은 값이 2개 존재하는 과도 상태
       3. node.right = doDelete(node.right, successor.data)
                                           // 오른쪽 서브트리에서 진짜 successor 노드를 재귀 재사용으로 삭제

    ---> 원리
      -> successor는 정의상 왼쪽 자식을 가질 수 없음, 왼쪽 자식이 있었다면 그게 더 작은 successor가 되어야 하므로 모순
      -> successor는 최대 오른쪽 자식 하나만 가능 -> 1번 혹은 2번 케이스로 귀결됨
      -> 3번 케이스가 다시 3번 케이스를 무한히 재귀하는 상황은 발생하지 않음
      -> 즉 3번 케이스는 삭제를 직접 수행하는 게 아니라, 문제를 1번 2번 케이스로 축소시키는 전처리 과정에 가까움

   - findMin(node) : 왼쪽 자식이 없을 때까지 계속 left로 이동 -> 서브트리의 최솟값
     (predecessor를 쓰려면 반대로 findMax: 오른쪽이 없을 때까지 right로 이동,
      node.left = doDelete(node.left, predecessor.data) 형태로 대칭 구현 가능)

  - 동작 검증 수단
    -> inOrder() : BST의 inOrder = 정렬된 순서라는 성질을 이용해 insert/delete 후 오름차순이 유지되는지로 정합성 확인
*/

public class _5_2_BST {

  static class Node<E> {

    private E data;
    private Node<E> left;
    private Node<E> right;

    public Node(E data) {
      this.data = data;
      this.left = null;
      this.right = null;
    }
  }

  // 기본 이진 탐색 트리 (Binary Search Tree)
  static class BasicBST<E extends Comparable<E>> {

    Node<E> root;

    public BasicBST() {}

    public BasicBST(Node<E> root) {this.root = root;}

    public void insert(E e) {
      if (e == null) {throw new IllegalArgumentException("삽입하려는 요소가 null");}
      Node<E> newNode = new Node<>(e);

      if (root == null) {
        root = newNode;
        return;
      }

      Node<E> curNode = root;
      Node<E> parent = null;

      while (curNode != null) {
        parent = curNode;

        int compare = e.compareTo(curNode.data);

        if (compare == 0) {
          throw new IllegalArgumentException("중복된 요소 삽입 시도");
        } else if (compare < 0) {
          curNode = curNode.left;
        } else {
          curNode = curNode.right;
        }
      }

      if (e.compareTo(parent.data) < 0) {
        parent.left = newNode;
      } else {
        parent.right = newNode;
      }
    }

    public Node<E> search(E e) {
      if (root == null) {return null;}
      if (e == null) {throw new IllegalArgumentException("찾으려는 요소가 null");}

      Node<E> curNode = root;

      while (curNode != null) {
        int compare = e.compareTo(curNode.data);

        if (compare == 0) {
          return curNode;
        } else if (compare < 0) {
          curNode = curNode.left;
        } else {
          curNode = curNode.right;
        }
      }

      return null;
    }

    public void delete(E e) {
      if (root == null) {throw new IllegalStateException("트리가 비어있는 상태");}
      if (e == null) {throw new IllegalArgumentException("삭제하려는 요소가 null");}

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
          node = null;

        } else if (node.left == null || node.right == null) {
          node = node.left == null ? node.right : node.left;

        } else {
          Node<E> successor = findMin(node.right);
          node.data = successor.data;
          node.right = doDelete(node.right, successor.data);
        }
      }

      return node;
    }

    private Node<E> findMin(Node<E> node) {
      Node<E> curNode = node;

      while (curNode.left != null) {
        curNode = curNode.left;
      }
      return curNode;
    }

    // insert(), delete() 동작 확인용, inOrder = 정렬된 순서
    public List<E> inOrder() {
      List<E> result = new ArrayList<>();
      inOrder(root, result);
      return result;
    }

    private void inOrder(Node<E> node, List<E> result) {
      if (node == null) {return;}
      inOrder(node.left, result);
      result.add(node.data);
      inOrder(node.right, result);
    }

    public void printTree() {
      System.out.println("==========================================================");
      printTree(root, "", "");
      System.out.println("==========================================================");
    }

    private void printTree(Node<E> node, String prefix, String branch) {
      if (node == null) {return;}
      System.out.println(prefix + branch + node.data);
      printTree(node.left, prefix + "    ", "L── ");
      printTree(node.right, prefix + "    ", "R── ");
    }

    public String toString(Node<E> node) {
      return node != null ? "Node Data: " + node.data : "Cannot Find Node";
    }
  }

  public static void main(String[] args) {

    Node<Integer> BasicBSTRoot = new Node<>(10);

    _5_2_BST.BasicBST<Integer> BasicBST = new BasicBST<>(BasicBSTRoot);

    BasicBST.insert(1);
    BasicBST.printTree();
    BasicBST.insert(5);
    BasicBST.printTree();
    BasicBST.insert(7);
    BasicBST.printTree();
    BasicBST.insert(4);
    BasicBST.printTree();
    BasicBST.insert(17);
    BasicBST.printTree();
    BasicBST.insert(18);
    BasicBST.printTree();
    BasicBST.insert(15);
    BasicBST.printTree();

    System.out.println("BasicBST.search(5) = " + BasicBST.toString(BasicBST.search(5)));
    BasicBST.delete(5);
    BasicBST.printTree();
    System.out.println("BasicBST.inOrder() = " + BasicBST.inOrder());
    System.out.println("BasicBST.search(5) = " + BasicBST.toString(BasicBST.search(5)));
  }
}
