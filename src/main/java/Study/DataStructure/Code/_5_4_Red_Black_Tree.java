package Study.DataStructure.Code;

/*
  - RB Tree 5가지 규칙
    -> 모든 노드는 RED or BLACK
    -> Root는 항상 BLACK
    -> 모든 Leaf(NIL Node, 즉 null)은 BLACK으로 간주
    -> RED 노드 자식은 반드시 BLACK, RED가 연속 불가, BLACK - BLACK은 가능
    -> 임의의 노드에서 노드의 하위 Leaf(NIL)까지 가는 모든 경로에 포함된 BLACK 노드의 개수가 동일 (Black-Height, bh(x))
*/

public class _5_4_Red_Black_Tree {

  enum COLOR {RED, BLACK}

  static class Node<E> {

    E data;
    Node<E> left;
    Node<E> right;
    Node<E> parent;
    COLOR color;

    public Node(E data, COLOR color) {
      this.data = data;
      this.color = color == null ? COLOR.BLACK : color;
    }
  }

  static class RedBlackTree<E extends Comparable<E>> {

    Node<E> root;

    public RedBlackTree(Node<E> root) {this.root = root;}

    static <E> COLOR getColor(Node<E> node) {
      return node == null ? COLOR.BLACK : node.color;
    }

    // 기존 BST 삽입에서 parent 상태 추가, 삽입은 항상 RED
    public void insert(E e) {
      if (e == null) {throw new IllegalArgumentException("삽입 시도 데이터가 null");}

      Node<E> parent = null;
      Node<E> curNode = root;

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

      Node<E> newNode = new Node<>(e, COLOR.RED);
      newNode.parent = parent;

      if (parent == null) {
        root = newNode;
      } else if (e.compareTo(parent.data) < 0) {
        parent.left = newNode;
      } else {
        parent.right = newNode;
      }

      insertFixup(newNode);
    }

    private void insertFixup(Node<E> node) {
      while (getColor(node.parent) == COLOR.RED) {
        Node<E> parent = node.parent;

        Node<E> grandParent = parent.parent;
        Node<E> uncle = grandParent.left == parent ? grandParent.right : grandParent.left;

        if (getColor(uncle) == COLOR.RED) {
          // 1번 케이스(P = RED, UC = RED)

          parent.color = COLOR.BLACK;
          uncle.color = COLOR.BLACK;
          grandParent.color = COLOR.RED;
          // 삽입된 노드는 RED로 유지, 부모와 삼촌은 BLACK & 할아버지는 RED 로 변경해 할아버지 부터 색 재배치
          node = grandParent;

        } else {
          // } else if (getColor(uncle) == COLOR.BLACK) {
          // 2, 3번 케이스(P = RED, UC = BLACK/null)

          /*
            parent는 반환 받고 grandParent는 반환 받지 않는 이유
            rotate는 회전 후 새로운 서브트리 루트 반환, grandParent가 반환을 받으면 parent 를 가르키기 때문
            ex) LL 상황에서 rotateRight 한 번
                        G(B)  <- 기존 grandParent
                       /
                     P(R)     <-  parent
                     /
                   N(R)
            회전 후
                    P(R)      <- grandParent 변수가 이제 P를 가리킴
                   /   \
                 N(R)  G(B)
          */
          if (grandParent.left == parent) {
            // LL, gp rotateRight 1회
            if (parent.right == node) {
              // LR, p Left 1회, gp right 1회
              parent = rotateLeft(parent);
            }
            rotateRight(grandParent);
          } else if (grandParent.right == parent) {
            // RR, gp left 1회
            if (parent.left == node) {
              // RL, p right 1회, gp left 1회
              parent = rotateRight(parent);
            }
            rotateLeft(grandParent);
          }
          grandParent.color = COLOR.RED;
          parent.color = COLOR.BLACK;
          // 
          break;
        }
      }

      root.color = COLOR.BLACK;
    }

    public void doDelete(E e) {
      if (e == null) {throw new IllegalArgumentException("삭제하려는 데이터가 null");}
      if (root == null) {throw new IllegalStateException("비어있는 RB 트리 삭제 시도");}

      Node<E> parent = null;
      Node<E> curNode = root;

      while (curNode != null) {
        parent = curNode.parent;

        int compare = e.compareTo(curNode.data);

        if (compare == 0) {
          // 삭제, 자식 개수 체크

          Node<E> curLeft = curNode.left;
          Node<E> curRight = curNode.right;

          COLOR curNodeColor = getColor(curNode);

          if (curLeft == null && curRight == null) {
            if (curNodeColor == COLOR.RED) {
              // 자식 없음 & 삭제 노드가 RED = fixup 필요 없음, 삭제만 수행
            } else {
              // 자식 없음 & 삭제 노드가 BLACK = 노드 삭제시 black 카운트(Black Height)가 줄어들어 fixup 필요
            }
          } else if (curLeft == null || curRight == null) {
            Node<E> childNode = curLeft == null ? curRight : curLeft;
            // 자식이 1개
            if (curNodeColor == COLOR.RED) {
              // 구조적으로 불가능 한 상태, RED - 자식 1개 -> Black Height 규칙 위반
              throw new IllegalStateException("삭제 중 규칙 5번 위반 발생");
            } else {
              if (childNode.color == COLOR.RED) {
                // 삭제 노드가 BLACK & 자식이 RED -> 노드 삭제 후 자식을 BLACK 으로 변경
                // 삭제 노드 BLACK 에서 삭제 가능한 유일한 상황
              } else {
                throw new IllegalStateException("삭제 중 삭제 노드가 BLACK & 유일한 자식 색이 BLACK");
                // 삭제 노드가 BLACK & 자식이 BLACK -> 있을 수 없는 경우, 도달 했다면 RB Tree가 아닌것
              }

            }


          } else {
            // 자식이 둘 다 있는 상태, AVL의 successor/predecesso 필요
          }

        } else if (compare < 0) {
          curNode = curNode.left;
        } else {
          curNode = curNode.right;
        }
      }
    }

    private Node<E> rotateLeft(Node<E> node) {
      Node<E> parent = node.parent;
      Node<E> right = node.right;
      Node<E> rightLeft = right.left;

      node.right = rightLeft;
      if (rightLeft != null) {rightLeft.parent = node;}

      right.left = node;
      node.parent = right;

      right.parent = parent;

      if (parent == null) {
        root = right;
      } else if (parent.left == node) {
        parent.left = right;
      } else {
        parent.right = right;
      }

      return right;
    }

    private Node<E> rotateRight(Node<E> node) {
      Node<E> parent = node.parent;
      Node<E> left = node.left;
      Node<E> leftRight = left.right;

      node.left = leftRight;
      if (leftRight != null) {leftRight.parent = node;}

      left.right = node;
      node.parent = left;

      left.parent = parent;

      if (parent == null) {
        root = left;
      } else if (parent.left == node) {
        parent.left = left;
      } else {
        parent.right = left;
      }

      return left;
    }

    public void printTree() {
      printTree(root, "", "");
      System.out.println("==========================================================");
    }

    private void printTree(Node<E> node, String prefix, String branch) {
      if (node == null) {return;}
      System.out.println(prefix + branch + node.data + "(" + node.color + ")");
      printTree(node.left, prefix + "  ", "L─ ");
      printTree(node.right, prefix + "  ", "R─ ");
    }

  }

  public static void main(String[] args) {

    /* 기본 삽입 */
    RedBlackTree<Integer> RB = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    RB.insert(5);
    RB.insert(15);
    RB.insert(3);
    RB.insert(7);
    RB.insert(13);
    RB.insert(17);

    System.out.println("=== 기본 삽입 ===");
    RB.printTree();

    /*
     * LL 삽입, 순서
     *
     *      10(B)
     *      /
     *    5(R)
     *   /
     *  3(R)
     *
     *   rotateRight(10)
     *      5(B)
     *     /   \
     *   3(R)  10(R)
     */
    RedBlackTree<Integer> LL = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    LL.insert(5);
    LL.insert(3);

    System.out.println("\n=== LL ===");
    LL.printTree();


    /*
     * RR 삽입
     *
     *      10(B)
     *         \
     *         15(R)
     *            \
     *            20(R)
     *
     * rotateLeft(10)
     *      15(B)
     *       /   \
     *     10(R) 20(R)
     */
    RedBlackTree<Integer> RR = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    RR.insert(15);
    RR.insert(20);

    System.out.println("\n=== RR ===");
    RR.printTree();


    /*
     * LR 삽입
     *      10(B)
     *      /
     *    5(R)
     *      \
     *       7(R)
     *
     * rotateLeft(5)
     * rotateRight(10)
     *
     *       7(B)
     *      /   \
     *    5(R) 10(R)
     */
    RedBlackTree<Integer> LR = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    LR.insert(5);
    LR.insert(7);

    System.out.println("\n=== LR ===");
    LR.printTree();

    /*
     *  RL 삽입
     *    10(B)
     *       \
     *       15(R)
     *       /
     *     13(R)
     *
     * rotateRight(15)
     * rotateLeft(10)
     *       13(B)
     *      /    \
     *   10(R)  15(R)
     */
    RedBlackTree<Integer> RL = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    RL.insert(15);
    RL.insert(13);

    System.out.println("\n=== RL ===");
    RL.printTree();
  }
}