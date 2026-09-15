package Study.DataStructure.Code;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;


/*
- 트리(Tree)

  - 배열/연결리스트 같은 선형 구조와 다른 비선형 구조
  - 선형 구조 한계
    -> 정렬되지 않은 배열: 탐색 O(n), 인덱스 접근 O(1), 삽입/삭제 O(n)
    -> 연결리스트: 탐색 O(n), 삽입/삭제는 위치를 찾았다는 전제하에만 유리
    -> 정렬된 배열: 이분탐색으로 탐색 O(log n) 가능하지만 삽입/삭제 O(n) (값 shift 필요)
    -> 탐색 빠르면 삽입/삭제 느리고, 삽입/삭제 빠르면 탐색이 느린 트레이드오프
    -> 균형 잡힌 트리(BST, AVL Tree, Red-Black Tree)는 탐색/삽입/삭제 모두 O(log n)을 노림

  - 용어
    root: 부모가 없는 최상위 노드
    leaf: 자식이 없는 노드
    parent / child: 상하 관계
    depth: root에서 해당 노드까지의 간선(edge) 수 (root의 depth = 0)
    height: 노드에서 가장 먼 leaf까지의 간선 수 (leaf의 height = 0)
    degree: 노드가 가진 자식의 수

  - 이진트리(Binary Tree) : 모든 노드의 degree가 최대 2 (left / right로 구분)

  - 예시 트리
         (A)
        (B, C)
     (D, E) (null, F)

  - 순회(Traversal) 방법
    - preOrder: 노드 진입하자마자(자식 보기 전) 저장 -> root가 항상 맨 먼저
      -> [A, B, D, E, C, F]

    - inOrder: 왼쪽 서브트리 끝나고 돌아온 직후, 오른쪽 가기 직전 저장
      -> BST에서는 이 순서가 곧 정렬된 순서가 됨
      -> [D, B, E, A, C, F]

    - postOrder: 왼쪽/오른쪽 다 처리하고 나서야 저장
      -> 자식의 결과가 먼저 필요한 계산(ex. 디렉토리 용량 합산, 트리 삭제)에 사용
      -> [D, E, B, F, C, A]

    - levelOrder: Queue를 이용한 반복문(BFS)
      -> 같은 depth끼리 좌->우로 방문. poll 하면서 자식을 다시 add
      -> [A, B, C, D, E, F]

   - 구현: pre/in/post order는 동일한 재귀 뼈대에서 저장 순서를 어느 줄에 놓느냐 의 차이일 뿐

    private void order(Node<E> node) {
      if (node == null) return;
      // 이곳에서 add 하면 preOrder  (root -> left -> right)
      order(node.left);
      // 이곳에서 add 하면 inOrder   (left -> root -> right)
      order(node.right);
      // 이곳에서 add 하면 postOrder (left -> right -> root)
    }

  - 시간복잡도: 4가지 모두 O(n) - 노드마다 정확히 한 번 방문
  - 공간복잡도(추가 메모리)
    -> pre/in/post: O(h) - 재귀 호출 스택에는 어느 순간이든
    --> root ~ 현재 노드까지의 경로 만 쌓여있음 (그 최댓값 = height)

    -> levelOrder: O(w) - 큐 안에는 항상 같은 레벨(depth)의 노드들이 담겨있고
    --> 그 크기의 최댓값 = 트리에서 가장 넓은 레벨의 노드 수(width)
*/

public class _5_1_BinaryTree {

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

  static class BinaryTree<E> {

    Node<E> root;

    public BinaryTree(Node<E> root) {this.root = root;}

    private void printResultAndResult(String logic, String expectedResult, ArrayList<E> result) {
      System.out.println("==========================================================");
      System.out.println("Logic: " + logic + "\nExpected Result: " + expectedResult + "\nResult: " + result);
    }

    // if tree likes
    //     (A)
    //    (B, C)
    // (D, E) (null, F)
    public void doSelectedOrderThenPrint(String logic) {
      switch (logic.toLowerCase()) {
        case "preorder":
          printResultAndResult("PreOrder Result", "[A, B, D, E, C, F]", preOrder());
          break;
        case "inorder":

          printResultAndResult("InOrder Result", "[D, B, E, A, C, F]", inOrder());
          break;
        case "postorder":
          printResultAndResult("PostOrder Result", "[D, E, B, F, C, A]", postOrder());
          break;
        case "levelorder":
          printResultAndResult("LevelOrder Result", "[A, B, C, D, E, F]", levelOrder());
      }
    }

    //     (A)
    //    (B, C)
    // (D, E) (null, F)
    // root -> left -> right
    // Result: [A, B, D, E, C, F]
    public ArrayList<E> preOrder() {
      ArrayList<E> result = new ArrayList<>();
      if (root != null) {preOrder(root, result);}
      return result;
    }

    private void preOrder(Node<E> node, ArrayList<E> result) {
      if (node == null) {return;}

      result.add(node.data);
      preOrder(node.left, result);
      preOrder(node.right, result);
    }

    //     (A)
    //    (B, C)
    // (D, E) (null, F)
    // left -> root -> right
    // [D, B, E, A, C, F]
    public ArrayList<E> inOrder() {
      ArrayList<E> result = new ArrayList<>();
      if (root != null) {inOrder(root, result);}
      return result;
    }

    private void inOrder(Node<E> node, ArrayList<E> result) {
      if (node == null) {return;}

      inOrder(node.left, result);
      result.add(node.data);
      inOrder(node.right, result);
    }

    //     (A)
    //    (B, C)
    // (D, E) (null, F)
    // left -> right -> root
    // [D, E, B, F, C, A]
    public ArrayList<E> postOrder() {
      ArrayList<E> result = new ArrayList<>();
      if (root != null) {postOrder(root, result);}
      return result;
    }

    private void postOrder(Node<E> node, ArrayList<E> result) {
      if (node == null) {return;}

      postOrder(node.left, result);
      postOrder(node.right, result);
      result.add(node.data);
    }

    //     (A)
    //    (B, C)
    // (D, E) (null, F)
    // BFS, Queue 사용
    // [A, B, C, D, E, F]
    private ArrayList<E> levelOrder() {
      ArrayList<E> result = new ArrayList<>();

      if (root == null) {return result;}

      Queue<Node<E>> queue = new ArrayDeque<>();
      queue.add(root);

      while (!queue.isEmpty()) {
        Node<E> curNode = queue.poll();

        result.add(curNode.data);

        if (curNode.left != null) {
          queue.add(curNode.left);
        }
        if (curNode.right != null) {
          queue.add(curNode.right);
        }
      }
      return result;
    }
  }

  public static void main(String[] args) {

    Node<String> root = new Node<>("A");
    root.left = new Node<>("B");
    root.right = new Node<>("C");
    root.left.left = new Node<>("D");
    root.left.right = new Node<>("E");
    root.right.right = new Node<>("F");

    _5_1_BinaryTree.BinaryTree<String> tree = new _5_1_BinaryTree.BinaryTree<>(root);

    tree.doSelectedOrderThenPrint("PreOrder");
    tree.doSelectedOrderThenPrint("InOrder");
    tree.doSelectedOrderThenPrint("PostOrder");
    tree.doSelectedOrderThenPrint("LevelOrder");
  }
}

//
// public class _5_1_BinaryTree {
//
//   static class Node<E> {
//
//     private E data;
//     private Node<E> left;
//     private Node<E> right;
//
//     public Node(E data) {
//       this.data = data;
//       this.left = null;
//       this.right = null;
//     }
//   }
//
//   static class BinaryTree<E> {
//
//     Node<E> root;
//     ArrayArrayList<E> output = new ArrayList<>();
//
//     public BinaryTree(Node<E> root) {this.root = root;}
//
//     private void printResultAndResult(String logic, String expectedResult) {
//       System.out.println("==========================================================");
//       System.out.println("Logic: " + logic + "\nExpected Result: " + expectedResult + "\nResult: " + output);
//       output = new ArrayList<>();
//     }
//
//     // if tree likes
//     //     (A)
//     //    (B, C)
//     // (D, E) (null, F)
//     public void doSelectedOrderThenPrint(String logic) {
//       switch (logic.toLowerCase()) {
//         case "preorder":
//           preOrder(root);
//           printResultAndResult("PreOrder Result", "[A, B, D, E, C, F]");
//           break;
//         case "inorder":
//           inOrder(root);
//           printResultAndResult("InOrder Result", "[D, B, E, A, C, F]");
//           break;
//         case "postorder":
//           postOrder(root);
//           printResultAndResult("PostOrder Result", "[D, E, B, F, C, A]");
//           break;
//         case "levelorder":
//           Queue<Node<E>> queue = new ArrayDeque<>();
//           queue.add(root);
//           levelOrder(queue);
//           printResultAndResult("LevelOrder Result", "[A, B, C, D, E, F]");
//       }
//     }
//
//     //     (A)
//     //    (B, C)
//     // (D, E) (null, F)
//     // root -> left -> right
//     // Result: [A, B, D, E, C, F]
//     private void preOrder(Node<E> node) {
//       if (node == null) {return;}
//
//       // 동작 되게 구현 하긴 했는데 조금 복잡함 단순하게 생각하기
//       // if (node == root) {output.add(root.data);}
//       // Node<E> leftNode = node.left;
//       // Node<E> rightNode = node.right;
//       // if (leftNode != null) {
//       //   output.add(leftNode.data);
//       //   preOrder(leftNode);
//       // }
//       //
//       // if (rightNode != null) {
//       //   output.add(rightNode.data);
//       //   preOrder(rightNode);
//       // }
//
//       output.add(node.data);
//       preOrder(node.left);
//       preOrder(node.right);
//     }
//
//     //     (A)
//     //    (B, C)
//     // (D, E) (null, F)
//     // left -> root -> right
//     // [D, B, E, A, C, F]
//     private void inOrder(Node<E> node) {
//       if (node == null) {return;}
//
//       inOrder(node.left);
//       output.add(node.data);
//       inOrder(node.right);
//     }
//
//     //     (A)
//     //    (B, C)
//     // (D, E) (null, F)
//     // left -> right -> root
//     // [D, E, B, F, C, A]
//     private void postOrder(Node<E> node) {
//       if (node == null) {return;}
//
//       postOrder(node.left);
//       postOrder(node.right);
//       output.add(node.data);
//     }
//
//     //     (A)
//     //    (B, C)
//     // (D, E) (null, F)
//     // BFS, Queue 사용
//     // [A, B, C, D, E, F]
//     private void levelOrder(Queue<Node<E>> queue) {
//       if (queue.isEmpty()) {return;}
//       Node<E> curNode;
//
//       while (!queue.isEmpty()) {
//         curNode = queue.poll();
//
//         output.add(curNode.data);
//
//         if (curNode.left != null) {
//           queue.add(curNode.left);
//         }
//         if (curNode.right != null) {
//           queue.add(curNode.right);
//         }
//       }
//
//       // 이렇게 재귀 구현도 가능은 하나
//       // 재귀 호출 스택 추가 비용 및 StackOverFlowError 위험으로 필요 없음
//       // Node<E> curNode = queue.poll();
//       // output.add(curNode.data);
//       // if (curNode.left != null) {
//       //   queue.add(curNode.left);
//       // }
//       // if (curNode.right != null) {
//       //   queue.add(curNode.right);
//       // }
//       // levelOrder(queue);
//     }
//   }
//
//   public static void main(String[] args) {
//
//     Node<String> root = new Node<>("A");
//     root.left = new Node<>("B");
//     root.right = new Node<>("C");
//     root.left.left = new Node<>("D");
//     root.left.right = new Node<>("E");
//     root.right.right = new Node<>("F");
//
//     _5_1_BinaryTree.BinaryTree<String> tree = new _5_1_BinaryTree.BinaryTree<>(root);
//
//     tree.doSelectedOrderThenPrint("PreOrder");
//     tree.doSelectedOrderThenPrint("InOrder");
//     tree.doSelectedOrderThenPrint("PostOrder");
//     tree.doSelectedOrderThenPrint("LevelOrder");
//   }
// }
