package Study.DataStructure.Code;

/*
- Red-Black Tree (RBT): AVL의 완벽한 균형을 포기하고 적당한 균형으로 타협한 자기 균형(Self-Balancing) BST
  - AVL 대비 트레이드오프
    -> AVL: 모든 노드에서 (left height - right height) <= 1 (엄격) -> 탐색 최적, 대신 삽입/삭제 시 rotation 빈번
    -> RBT: 균형 조건이 느슨함 -> 탐색은 AVL보다 살짝 느릴 수 있지만 삽입/삭제가 훨씬 빠름 (recoloring으로 끝나는 경우 많음)
    -> 그래도 height는 O(log n) 보장 (AVL보다 상수배 정도 더 클 뿐)
    -> 삽입/삭제가 잦은 실무 상황에 유리 -> Java TreeMap/TreeSet, C++ std::map, Linux CFS 스케줄러 등이 채택

  - 5가지 규칙
    1. 모든 노드는 RED 또는 BLACK
    2. root는 항상 BLACK
    3. 모든 leaf(NIL/null)는 BLACK으로 간주
    4. RED 노드의 자식은 반드시 BLACK (연속 RED 금지)
    5. 임의의 노드에서 하위 leaf(NIL)까지의 모든 경로에서 BLACK 노드 개수가 동일 (black-height, bh)

  - 규칙 4 & 5가 O(log n)을 보장하는 원리
    -> 규칙 4: 어떤 경로든 Red가 연속 못 나오므로 경로 길이의 최소 절반은 Black
    -> 규칙 5: 모든 경로의 Black 개수(bh)는 동일
    -> 최단 경로(전부 Black) 길이 >= bh, 최장 경로(Black-Red 번갈아) 길이 <= 2*bh
    -> 최장 경로가 최단 경로의 최대 2배를 넘지 않음 -> 적당한 균형이 수학적으로 보장되어 height = O(log n)

  - insert(e)
    -> 새 노드는 항상 RED로 삽입 (BLACK으로 넣으면 그 경로 bh가 무조건 늘어 규칙5가 100% 깨짐,
       RED로 넣으면 운 좋으면 규칙4만 깨지거나 아예 안 깨질 수도 있어 위반을 최소화하는 전략)
    -> BST 삽입과 동일하게 위치 탐색 (parent 포인터로 추적)
    -> 삽입 직후 유일하게 깨질 수 있는 규칙은 4번(부모도 RED인 Red-Red violation)뿐, 나머지는 자동 유지
    -> fixup은 새 노드의 parent와 uncle(parent의 형제) 색으로 3가지 케이스 분기
      1) uncle RED: recoloring만으로 해결 (parent, uncle -> BLACK, grandParent -> RED),
         rotation 없이 문제를 grandParent로 전파 -> node = grandParent, 루프 계속
         (안전한 이유: 아래쪽 관점에선 grandParent(B,+1)->parent(R,+0) = 1+X 에서
          grandParent(R, +0)->parent(B, +1) = 1+X 로 bh 그대로 보존.
          단, 위쪽에서 보면 grandParent가 B->R 되며 그 지점의 기여가 1->0으로 줄어듦
          -> 그래서 문제를 위로 전파. root까지 도달하면 root를 다시 BLACK으로 강제하는데,
          이는 root를 지나는 모든 경로에 +1을 균일하게 더하는 것이므로 경로 간 상대적 동일성(규칙5)은
          그대로 유지되면서 규칙2(root=BLACK)까지 자동으로 고쳐짐)
      2) uncle BLACK, 지그재그(LR/RL) 모양: rotation으로 먼저 일자로 편 뒤 3)으로 이어짐
      3) uncle BLACK, 일자(LL/RR) 모양: rotation 1회 + recoloring(grandParent RED, parent BLACK)으로 즉시 종료
    -> AVL과의 결정적 차이: rotation 판단 기준이 height 차이(balance factor)가 아니라 색

  - delete(e)
    -> BST 삭제(leaf/자식1개/자식2개 successor)와 뼈대는 동일하나,
       실제로 물리적으로 제거되는 노드의 색이 이후 fixup 여부를 결정
    -> 자식 2개는 항상 successor(오른쪽 서브트리 최솟값)를 찾아 값만 복사 후,
       successor 자체를 삭제 대상으로 삼음 -> successor는 왼쪽 자식이 없으므로
       항상 leaf 또는 자식1개(오른쪽만) 케이스로 자연 축소됨 (BST와 동일한 성질 재사용)
    -> 물리적으로 제거되는 노드(y) 색에 따른 4가지 분류
      1) y = RED & leaf: 그냥 제거, 아무 규칙도 안 깨짐 -> fixup 불필요
      2) y = RED & 자식1개: 구조적으로 불가능 (RED 노드가 자식 1개면 규칙5 위반이 되므로 애초에 존재 불가)
      3) y = BLACK & leaf: Double Black 발생(규칙5 위반) -> deleteFixup 필요
      4) y = BLACK & 자식1개(항상 RED): 자식을 그 자리로 올리고 BLACK으로 재색칠하면 bh 그대로 보존
         -> recoloring 한 번으로 즉시 해결, fixup 불필요
         (BLACK-자식1개-자식도 BLACK인 조합은 구조적으로 불가능: 규칙5에 의해 자동 강제되는 조건)

    -> deleteFixup(parent, side): 이 parent의 side 방향 자식 자리가 black 하나 부족을 표현
       leaf는 null이라 parent 필드가 없으므로 parent+side로 위치를 표현하는 방식 채택
       insert가 삼촌 색으로 케이스를 나눴다면, delete는 형제(sibling) 색 + 형제의 자식(조카, near/far)
       색으로 케이스를 나눔 (near: deficient에 가까운 조카, far: 먼 조카)

       sibling RED -> parent는 반드시 BLACK(규칙4). 종결 케이스 아님, BLACK sibling 형태로
         바꾸기 위한 전처리. parent를 deficient 반대 방향으로 rotate + (parent <-> sibling) 색 교환
         -> 회전 후 parent의 자식 포인터가 자동 갱신되므로, 별도 변수 갱신 없이 루프를 다시 돌려
            다음 iteration에서 자연스럽게 새 BLACK sibling을 계산하게 함

       sibling BLACK - near/far 조카 색으로 3가지 세부 케이스
         1) near BLACK, far BLACK: sibling을 RED로 재색칠
            (deficient 쪽과 sibling 쪽이 동등하게 1씩 부족한 대칭 상태로 전환)
            -> parent가 RED면 그 자리에서 BLACK으로 바꿔 즉시 해결(break)
            -> parent가 BLACK이면 문제를 grandParent로 전파 (parent=grandParent, side 재계산, 루프 계속)
            -> grandParent가 null(parent가 root)이면 자연 종료
         2) near RED, far BLACK: 지그재그 모양, 바로 해결 불가
            -> sibling 기준 near 방향으로 rotate + (원래)sibling RED, near BLACK으로 재색칠
            -> 이러면 새로운 sibling(=near)의 far 자식이 RED인 3)번 모양으로 자동 전환
            -> break 없이 루프를 다시 돌아 3)번 케이스로 자연 진입
         3) far RED (near은 무관): 종결 케이스
            -> parent를 deficient 반대 방향으로 rotate
            -> sibling이 원래 parent의 색을 물려받고, parent와 far는 BLACK
            -> deficient 경로 +1, far 경로도 RED->BLACK으로 +1 되며 sibling 위쪽에서 보면
               색 구성이 삭제 전과 동일해짐 -> break로 완전 종료

  - AVL vs RBT 판단 기준 요약
    -> 읽기(탐색)가 압도적으로 많고 삽입/삭제가 드묾 -> AVL
    -> 삽입/삭제가 빈번 -> RBT (실무 표준 선택)
*/

import javafx.geometry.Side;

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
          break;
        }
      }

      root.color = COLOR.BLACK;
    }

    public void doDelete(E e) {
      if (e == null) {throw new IllegalArgumentException("삭제하려는 데이터가 null");}
      if (root == null) {throw new IllegalStateException("비어있는 RB 트리 삭제 시도");}

      Node<E> curNode = root;

      while (curNode != null) {
        int compare = e.compareTo(curNode.data);

        if (compare == 0) {
          // 삭제, 자식 개수 체크
          Node<E> curLeft = curNode.left;
          Node<E> curRight = curNode.right;

          if (curLeft == null && curRight == null) {
            // 자식이 없는 케이스, 삭제 노드 색상별 처리
            removeLeafOrSingleChild(curNode);
            return;
          } else if (curLeft == null || curRight == null) {
            // 자식이 1개 케이스, 삭제 노드의 색상별 처리
            removeLeafOrSingleChild(curNode);
            return;
          } else {
            // 자식이 둘 다 있는 상태, AVL의 successor/predecessor 필요
            Node<E> successor = findMin(curNode.right);
            curNode.data = successor.data;
            // 교체 후 똑같이 successor 노드를 삭제 하는데 색상 및 자식 검사 필요
            removeLeafOrSingleChild(successor);
            return;
          }

        } else if (compare < 0) {
          curNode = curNode.left;
        } else {
          curNode = curNode.right;
        }
      }
    }

    private void removeLeafOrSingleChild(Node<E> curNode) {
      // 자식이 1개 케이스, 삭제 노드의 색상별 처리

      Node<E> curLeft = curNode.left;
      Node<E> curRight = curNode.right;
      COLOR curNodeColor = getColor(curNode);

      Node<E> childNode = curLeft == null ? curRight : curLeft;
      if (curLeft == null && curRight == null) {
        if (curNodeColor == COLOR.RED) {
          // 자식 없음 & 삭제 노드가 RED = fixup 필요 없음, 삭제만 수행
          removeNode(curNode);
        } else {
          // 자식 없음 & 삭제 노드가 BLACK = 노드 삭제시 black 카운트(Black Height)가 줄어들어 fixup 필요
          if (curNode.parent == null) {
            // 현재 노드가 root 및 유일한 노드
            removeNode(curNode);
          } else {
            // Double Black, Fixup 필요
            Node<E> parent = curNode.parent;
            Side curNodeSide = parent.left == curNode ? Side.LEFT : Side.RIGHT;
            if (curNodeSide == Side.LEFT) {
              parent.left = null;
            } else {
              parent.right = null;
            }
            deleteFixup(parent, curNodeSide);
          }
        }
      } else if (curNodeColor == COLOR.RED) {
        // 구조적으로 불가능 한 상태, RED - 자식 1개 -> Black Height 규칙 위반
        // -> RED 노드는 자식이 Leaf여야 함 (both NULL or BLACK)
        // -> 여기에 도달 한다는 건 삭제노드(RED) - 자식(RED) 라는 것
        throw new IllegalStateException("삭제 중 규칙 5번 위반 발생");
      } else {
        if (childNode.color == COLOR.RED) {
          // 삭제 노드가 BLACK & 자식이 RED -> 노드 삭제 후 자식을 BLACK 으로 변경
          // 삭제 노드 BLACK 에서 삭제 가능한 유일한 상황
          // 삭제 노드 BLACK이니 부모 RED
          Node<E> parent = curNode.parent;

          // 부모가 없는 경우 현재 노드가 root 상태
          if (parent == null) {
            root = childNode;
            root.color = COLOR.BLACK;
            return;
          }

          // 현재 노드 참조를 자식 노드로 변경 & 색 변경
          if (parent.left == curNode) {
            parent.left = childNode;
          } else {
            parent.right = childNode;
          }
          childNode.parent = parent;
          childNode.color = COLOR.BLACK;

        } else {
          throw new IllegalStateException("RB Tree 규칙 위반, 삭제 중 삭제 노드가 BLACK & 유일한 자식 색이 BLACK");
          // 삭제 노드가 BLACK이고 자식이 하나라면
          // RB Tree의 black-height 규칙상 그 자식은 반드시 RED
          // BLACK -> BLACK 구조가 되면 해당 노드의 양쪽 경로에서
          // BLACK 개수가 달라지므로 RB Tree 조건 위반
        }
      }
    }

    private void removeNode(Node<E> curNode) {
      if (curNode.parent == null) {
        root = null;
        return;
      }

      if (curNode.parent.left == curNode) {
        curNode.parent.left = null;
      } else {
        curNode.parent.right = null;
      }
      curNode.parent = null;
    }

    private void deleteFixup(Node<E> parent, Side curNodeSide) {
      // deficient(부족한 자리) = 삭제된 노드 자리

      while (parent != null) {
        Node<E> sibling = curNodeSide == Side.LEFT ? parent.right : parent.left;

        COLOR siblingColor = getColor(sibling);

        if (siblingColor == COLOR.RED) {
          // RED-Sibling, Parent = BLACK

          parent.color = COLOR.RED;
          sibling.color = COLOR.BLACK;

          if (curNodeSide == Side.LEFT) {
            // 삭제 노드는 parent.left 이니 rotateLeft()
            rotateLeft(parent);
          } else {
            // 대칭
            rotateRight(parent);
          }
        } else {
           /*
            BLACK-Sibling, Parent = RED, 가장 복잡, 3가지 케이스
            insert() 는 uncle의 color 로 구분 했다면 deleteFIxup 에선 near/far
            near child: deficient 쪽에 가까운 자식
            far child: deficient 쪽에서 먼 자식
            insert() 에서 LL/LR/RR/RL 나누던 사고방식과 비슷 (어느 쪽으로 꺾여 있는가)
           */
          if (curNodeSide == Side.LEFT) {

            Node<E> nearChild = sibling.left;
            Node<E> farChild = sibling.right;

            COLOR nearChildColor = getColor(nearChild);
            COLOR farChildColor = getColor(farChild);

            if (nearChildColor == COLOR.BLACK && farChildColor == COLOR.BLACK) {
            /* 1번 케이스: sibling 의 두 자식 다 BLACK
                        parent(?)
                       /        \
                  deficient   sibling(B)
                    (부족)     /      \
                            near(B)  far(B)

                삭제된 자리: parent.left, sibling: parent.right
                near child: sibling.left
                far child: sibling.right

                해결: Sibling을 RED로 변경
                  -> deficient 쪽 경로는 BLACK이 1 부족 상태, sibling을 RED로 변경하면
                  -> 양쪽 서브트리(deficient, sibling)이 똑같이 BLACK이 1 부족
                  -> parent 입장에선 밑의 서브트리 전체가 bh 1 부족 상태가 됨
                  --> 이 문제를 parent 로 끌어 올려 다시 처리
            */
              sibling.color = COLOR.RED;

              COLOR parentColor = getColor(parent);

              // 위에서 sibling을 RED 로 바꿨을 때 parent가 이미 RED 였다면
              // BLACK 으로 바꾸면 문제가 즉시 해결
              if (parentColor == COLOR.RED) {
                parent.color = COLOR.BLACK;
                break;
              } else {
                Node<E> grandParent = parent.parent;

                // parent가 root였다면 여기서 종료
                if (grandParent == null) {break;}

                // break 없이 루프 계속, 다음 반복에서 새 parent/curNodeSide 로 Sibling 재계산
                Side newSide = grandParent.left == parent ? Side.LEFT : Side.RIGHT;
                parent = grandParent;
                curNodeSide = newSide;
              }
            } else if (nearChildColor == COLOR.RED && farChildColor == COLOR.BLACK) {
            /* 2번 케이스: near child: RED, far child: BLACK
                           parent
                         /        \
                    deficient   sibling(B)
                      (부족)     /      \
                              near(R)  far(B)

                지그재그 모양(insert의 LR/RL 과 비슷한 발상), 바로 해결 불가
                3번 케이스(near child 의 색은 상관 없으며 far child 가 RED) 모양으로 바꿔주는 전처리 단계

                해결: sibling 기준 near child를 sibling 방향으로 rotate
                  -> deficient가 왼쪽일때
                  -> rotateRight(sibling) & sibling = RED, near = BLACK 으로 변경
                  -> rotateRight(sibling) 후:
                           parent
                         /        \
                    deficient    near(B)      <- BLACK으로
                      (부족)         \
                                  sibling(R)   <- RED로
                                      \
                                     far(B)
                   -> 색 변경 후 sibling 갱신 & break 없이 3번 케이스로 이어지도록 재시도
             */
              rotateRight(sibling);
              sibling.color = COLOR.RED;
              nearChild.color = COLOR.BLACK;
            } else {
              /* 3번 케이스: near child: ANY, far child: RED
                         parent(X)
                        /        \
                    deficient   sibling(B)
                     (부족)     /      \
                             near(?)  far(R)
                 해결: parent를 deficient 반대 방향으로 rotate
                  -> deficient가 왼쪽일 때 rotateLeft(parent)
                  -> rotateLeft(parent) 후:

                         sibling(X였던 parent의 색)
                        /        \
                   parent(B)    far(B)
                   /      \
                deficient near

                  -> 색 변경 3가지
                    --> sibling이 원래 parent의 색을 물려받음(RED or BLACK 그대로)
                    --> parent = BLACK
                    --> far(RED 였던 조카)도 BLACK
                  -> 원리
                    -> deficient가 이제 parent의 자식이 되고 parent가 BLACK이니 deficient 경로에 black 이 1 추가
                    -> 동시에 far도 RED -> BLACK 이 되면서 그쪽 경로도 black 이 1 증가
                    --> 동시에 sibling이 parent 자리를 대신해 원래 parent의 색을 이어받아 
                    --> sibling 위쪽에서 보면 색 구성이 삭제 전과 완전 동일, 여기서 break;
               */
              COLOR parentColor = parent.color;
              rotateLeft(parent);

              sibling.color = parentColor;
              parent.color = COLOR.BLACK;
              farChild.color = COLOR.BLACK;
              break;
            }
          } else {
            // deficient = right, 위와 대칭

            Node<E> nearChild = sibling.right;
            Node<E> farChild = sibling.left;

            COLOR nearChildColor = getColor(nearChild);
            COLOR farChildColor = getColor(farChild);

            if (nearChildColor == COLOR.BLACK && farChildColor == COLOR.BLACK) {
              sibling.color = COLOR.RED;

              COLOR parentColor = getColor(parent);

              if (parentColor == COLOR.RED) {
                parent.color = COLOR.BLACK;
                break;
              } else {
                Node<E> grandParent = parent.parent;

                if (grandParent == null) {break;}

                Side newSide = grandParent.left == parent ? Side.LEFT : Side.RIGHT;
                parent = grandParent;
                curNodeSide = newSide;
              }
            } else if (nearChildColor == COLOR.RED && farChildColor == COLOR.BLACK) {
              rotateLeft(sibling);
              sibling.color = COLOR.RED;
              nearChild.color = COLOR.BLACK;
            } else {
              COLOR parentColor = parent.color;
              rotateRight(parent);

              sibling.color = parentColor;
              parent.color = COLOR.BLACK;
              farChild.color = COLOR.BLACK;
              break;
            }
          }
        }
      }
    }

    private Node<E> findMin(Node<E> node) {
      Node<E> curNode = node;

      while (curNode.left != null) {
        curNode = curNode.left;
      }
      return curNode;
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
      if (root == null) {System.out.println("트리가 비어있음");}
      printTree(root, "", "");
      System.out.println("==========================================================");
    }

    private void printTree(Node<E> node, String prefix, String branch) {
      if (node == null) {return;}
      System.out.println(prefix + branch + node.data + "(" + (node.color == COLOR.RED ? "R" : "B") + ")");
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

    /*
     * Delete - RED leaf 삭제 (fixup 불필요)
     *      10(B)
     *      /    \
     *    5(R)  15(R)
     *
     * delete(5) -> 그냥 제거
     *      10(B)
     *         \
     *         15(R)
     */
    RedBlackTree<Integer> delRedLeaf = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));
    delRedLeaf.insert(5);
    delRedLeaf.insert(15);

    System.out.println("\n=== Delete: RED leaf (fixup 불필요) ===");
    delRedLeaf.printTree();
    delRedLeaf.doDelete(5);
    delRedLeaf.printTree();

    /*
     * Delete - BLACK leaf, sibling RED (전처리 후 재귀적으로 해소)
     *          10(B)
     *         /     \
     *       5(B)    15(B)
     *      /   \    /   \
     *    3(R)     13(R) 20(R)
     *
     * delete(3) -> 5의 왼쪽이 비면서 double black, sibling은 없음(NIL, BLACK)
     * -> near/far 둘 다 BLACK 케이스로 진입, sibling(NIL) RED로 재색칠 시도 후 parent(5, BLACK) 전파 등
     * 실제로는 트리 형태에 따라 케이스 1/2/3 중 어디로 빠지는지 printTree로 직접 확인
     */
    RedBlackTree<Integer> delBlackLeaf = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));
    delBlackLeaf.insert(5);
    delBlackLeaf.insert(15);
    delBlackLeaf.insert(3);
    delBlackLeaf.insert(13);
    delBlackLeaf.insert(20);

    System.out.println("\n=== Delete: BLACK leaf (deleteFixup 진입) ===");
    delBlackLeaf.printTree();
    delBlackLeaf.doDelete(3);
    delBlackLeaf.printTree();

    /*
     * Delete - BLACK 삭제 노드, 자식 1개(RED) -> recolor만으로 해결
     *      10(B)
     *         \
     *         15(B)
     *         /
     *       13(R)
     *
     * delete(15) -> 13이 그 자리를 대신하고 BLACK으로 재색칠
     *      10(B)
     *         \
     *         13(B)
     */
    RedBlackTree<Integer> delSingleChild = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));
    delSingleChild.insert(15);
    delSingleChild.insert(13);

    System.out.println("\n=== Delete: BLACK 노드, 자식 1개(RED) (recolor로 해결) ===");
    delSingleChild.printTree();
    delSingleChild.doDelete(15);
    delSingleChild.printTree();

    /*
     * Delete - 자식 2개, successor 통한 삭제
     *          10(B)
     *         /     \
     *       5(B)    15(B)
     *      /   \    /   \
     *    3(R) 7(R) 13(R) 20(R)
     *
     * delete(5) -> successor(7)의 값을 5 자리로 복사 -> 진짜 삭제는 7(RED leaf)에서 발생
     */
    RedBlackTree<Integer> delTwoChildren = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));
    delTwoChildren.insert(5);
    delTwoChildren.insert(15);
    delTwoChildren.insert(3);
    delTwoChildren.insert(7);
    delTwoChildren.insert(13);
    delTwoChildren.insert(20);

    System.out.println("\n=== Delete: 자식 2개 (successor 대체) ===");
    delTwoChildren.printTree();
    delTwoChildren.doDelete(5);
    delTwoChildren.printTree();

    /*
     * Delete - root 자체가 삭제 대상 (parent == null 방어 코드 확인용)
     *      10(B)  (유일한 노드)
     *
     * delete(10) -> root = null
     */
    RedBlackTree<Integer> delRootOnly = new RedBlackTree<>(new Node<>(10, COLOR.BLACK));

    System.out.println("\n=== Delete: root만 있는 트리 ===");
    delRootOnly.printTree();
    delRootOnly.doDelete(10);
    delRootOnly.printTree();
  }
}