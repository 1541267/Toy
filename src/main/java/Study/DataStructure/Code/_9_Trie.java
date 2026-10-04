package Study.DataStructure.Code;

import Study.DataStructure.Code._9_Trie.HashMapBasedTrie.Mode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/*
- Trie (트라이, 접두사 트리): 문자열 집합을 문자 하나당 노드 하나로 쪼개 트리 경로로 저장하는 구조
  - 구조
    -> root는 빈 문자열을 뜻하는 빈 노드, root에서 어떤 노드까지 내려온 경로 = 그 노드가 나타내는 접두사
    -> 공통 접두사를 공유 (app, apple은 a-p-p 경로를 같이 사용)
    -> isEnd 플래그: 이 노드에서 끝나는 단어가 존재하는지 표시
    --> app은 저장했는데 ap는 저장하지 않은 경우를 구분하기 위해 필수
    -> 노드는 문자를 직접 들고 있을 필요가 없음 (부모의 child 인덱스 또는 Map의 키가 이미 글자 정보)
    --> char c 필드는 디버깅에는 편하지만 필수가 아니며, char와 int(0~25 인덱스)를 혼동하는 버그의 원인이 되기 쉬움

  - 필요한 이유 (문자열 길이 L, 저장된 문자열 개수 N)
    -> HashSet/HashMap: 정확히 일치 검색 평균 O(L), 접두사 검색 불가 (전체 순회 O(N*L)), 사전순 순회 불가
    -> TreeMap: 정확히 일치 O(L log N) (비교 한 번이 O(L)), 접두사 검색은 범위 탐색으로 가능, 사전순 순회 가능
    -> Trie: 정확히 일치 O(L), 접두사 검색 O(L), 사전순 순회가 자연스럽게 가능
    -> 탐색 비용이 저장된 개수 N과 무관하고 검색어 길이 L에만 비례
    -> 접두사 질의가 본업이며 해시의 가장 큰 약점을 메움
    -> 대신 노드마다 자식 저장 공간을 들고 있어 메모리 오버헤드가 큼

  - 연산
    -> insert(word): root부터 글자마다 자식이 없으면 생성하며 내려가고, 마지막 노드의 isEnd = true -> O(L)
    -> findNode(s): 경로를 따라 내려가 마지막 노드 반환, 중간에 끊기면 null -> O(L)
    -> search(word): node != null && node.isEnd (전체 일치)
    -> startsWith(prefix): node != null (경로만 존재하면 됨)
    --> search와 startsWith의 차이는 isEnd 검사 한 줄뿐, findNode로 공통화
    --> isEnd가 없으면 app만 저장했을 때 search(ap)가 true로 틀리게 나옴
    --> 반대로 경로 확인 없이 isEnd만 보면 존재하지 않는 경로에서 NPE
    -> collectWordsFromPrefix(prefix): findNode로 접두사 노드까지 내려간 뒤 그 서브트리만 DFS
    -> 한계: 중간 문자열 검색 불가 (apple에서 pp 검색 불가), 루트에서 시작하는 경로만 따라갈 수 있는 구조이기 때문

  - insert()
    -> root 노드를 두면 첫 글자도 root.child[idx]로 똑같이 처리되어 i == 0 분기가 사라짐
    -> 빈 문자열은 root.isEnd로 자연스럽게 표현 가능, 허용 여부는 정책으로 결정
    -> HashMap 기반은 computeIfAbsent로 get, null 검사, put을 한 줄로 처리 가능
    -> 생성자 안에서 mode 대입 후 root를 만들어야 함
    --> 필드 초기화는 생성자 본문보다 먼저 실행되므로, 필드 선언부에서 root를 만들면 mode가 아직 null인 상태로 Map이 만들어짐

  - delete 규칙
    -> 단어 존재 확인: 경로가 끝까지 있고 마지막 노드가 isEnd여야 함
    --> 접두사일 뿐인 단어(ap) 삭제 시도나 저장되지 않은 단어 삭제는 아무것도 건드리지 않고 false
    -> 노드를 지워도 되는 조건: 자식이 없고(!hasChild) 다른 단어의 끝도 아님(!isEnd), 두 조건을 동시에 만족
    --> 자식이 있음 = 그 경로로 만들어지는 다른 단어가 존재한다는 뜻
    --> 삭제 도중 isEnd인 노드를 만남 = 다른 단어의 끝이라는 뜻, 둘 다 지우면 안 되므로 정리 중단
    -> 삭제 대상 마지막 노드는 isEnd를 false로 바꾼 직후이므로 isEnd 검사를 통과하고, 실질적으로 자식 검사만 작동
    -> 반복 구현: 내려가며 root부터 끝 노드까지 list에 저장, 끝에서 역순으로 올라가며 부모가 자식을 끊음
    --> 루프를 i >= 1까지 돌면 i = 1에서 root가 첫 글자 노드를 끊고, root 자체는 child로 검사되지 않으므로 root 보호는 구조상 자동
    -> 재귀 구현: 끝까지 내려간 뒤 되돌아오며 이 노드를 부모가 지워도 되는가를 boolean으로 반환, 호출 스택이 부모-자식 짝을 대신 맞춰 줌
    --> 인덱스 짝 문제가 구조상 사라지지만, 삭제 성공 여부를 위로 전달하는 설계가 추가로 필요 (보통 사전에 search로 존재 확인)
    -> 시간복잡도 O(L), 배열 기반은 hasChild가 O(26)이라 O(26 * L)이지만 상수이므로 O(L)

  - collectWordsFromPrefix (자동완성 수집)
    -> 접두사 노드까지만 내려간 뒤 그 서브트리만 순회, 접두사에 해당하지 않는 가지는 방문하지 않음
    -> 재귀 DFS + 백트래킹: StringBuilder에 글자를 append, 재귀 호출, deleteCharAt(backtracking)으로 되돌림
    --> DFS는 탐색 순서이고 재귀는 그 구현 수단, 반복문 DFS(명시적 Stack)는 깊이가 매우 깊을 때만 필요하므로 영어 단어 길이에서는 재귀로 충분
    -> 시간복잡도: 전체 노드 수가 아니라 접두사 아래 서브트리 크기에 비례
    --> 배열 기반은 노드마다 26칸을 확인하므로 서브트리 노드 수 * 26, Map 기반은 실제 자식만 순회하므로 서브트리 노드 수
    --> 결과 문자열 생성 비용(toString)까지 포함하면 결과 단어 수 * 평균 길이가 추가\
    -> 접두사가 a처럼 짧아 서브트리가 거대하면 사실상 전체 순회와 같아지고 결과 리스트도 수십만 개가 될 수 있음
    --> 해결 방향은 문자열 범위 제한이 아니라 결과 수와 순회량 제한
    --> 상위 K개만 수집하고 K개가 모이면 재귀 즉시 종료 (자동완성은 보통 5~10개만 노출)
    --> 인기도 캐싱: 노드마다 서브트리의 상위 K개 단어를 미리 저장, 질의는 collect 없이 O(L)로 종료, 대신 삽입 시 경로상 노드 갱신 비용 증가
    --> 최소 접두사 길이 제한 (1~2글자에서는 자동완성 안 함), 깊이 제한, 페이지네이션

  - 자식 저장 방식 비교 (배열 Node[26] / HashMap / TreeMap)
    - 배열 기반 (Node[26] = 소문자)
      -> 조회 O(1) 인덱스 접근, 인덱스 순서 순회만으로 사전순 보장
      -> 노드마다 자식 슬롯 26칸을 고정으로 확보 (자식이 없어도 낭비)
      -> 허용 문자 확장 시 노드당 배열 크기가 급증 (유니코드 전체는 사실상 불가)
      -> 자식이 촘촘할수록 유리, 입력 변환(char - a)과 검증 필요
    - HashMap 기반
      -> 조회 평균 O(1), 실제 존재하는 자식만 저장, 순서 보장 없음 (collect 결과가 사전순이 아님)
      -> 키가 임의의 Character이므로 허용 문자 범위가 무제한 (한글, 숫자, 대문자 가능)
      -> 허용 범위가 넓은 이유는 해싱 때문이 아니라 존재하는 자식만 저장하기 때문
      -> 자식이 비었는지 확인이 child.isEmpty() O(1) (배열 기반 hasChild는 O(26))
      -> 단, 엔트리 객체, 박싱된 Character, 버킷 배열 오버헤드가 있어 자식이 촘촘하면 배열보다 오히려 메모리를 더 쓸 수 있음
    - TreeMap 기반
      -> Red-Black Tree(균형 BST)이므로 조회 O(log k) (k는 자식 수), 키 오름차순 보장으로 collect 결과가 사전순
    - HashMap에서 사전순이 필요하면
      -> 노드를 방문할 때마다 자식 k개를 정렬해야 하므로 노드마다 O(k log k) 추가
      -> TreeMap은 조회마다 O(log k)를 쓰는 대신 순회 시 정렬 비용이 없음
      -> 조회 위주면 HashMap, 사전순 순회가 핵심이면 TreeMap 또는 배열
    - 선택 기준은 희소/밀집
      -> 평균 자식 수가 적은(희소) 구조는 Map이 유리, 알파벳이 작고 고정이며 촘촘하면 배열이 유리
    - 이번 구현의 입력 정책
      -> 배열 기반: 소문자로 통일 후 영어 소문자(a~z)만 허용, null과 공백 문자열은 예외
      -> HashMap/TreeMap 기반: 대소문자 구분, null과 공백 문자열은 예외 (두 구현의 정책이 다르므로 의도적 차이로 기록)

  - Trie vs HashSet 선택
    -> 접두사 질의, 자동완성, 사전순 순회가 필요하거나 접두사 공유율이 높으면 Trie
    -> 단순 존재 확인만 필요하고 접두사를 거의 공유하지 않으면 HashSet이 메모리상 유리
    --> 공유가 없으면 Trie는 문자열마다 글자 수만큼 노드를 새로 만들고, 노드 하나가 객체 헤더, 자식 구조, isEnd를 가져 오버헤드가 큼
    --> HashSet은 문자열 한 개를 객체 한 번으로 저장하므로 글자당 오버헤드가 거의 없음
    --> Trie는 노드가 힙에 흩어져 포인터를 따라가야 하므로 캐시 비친화적

  - 변형과 실무 활용
    -> 중간 문자열 검색: 모든 접미사를 삽입하는 Suffix Trie (공간 O(L^2)), 이를 압축한 Suffix Tree / Suffix Array
    -> 본문에서 패턴 하나를 찾는 용도는 KMP 같은 문자열 매칭 알고리즘, 여러 패턴을 한 번에 찾는 용도는 Trie를 확장한 Aho-Corasick
    -> 검색창 자동완성, 사전 앱, 오타 교정 후보 탐색
    -> IP 라우팅의 최장 접두사 매칭 (비트 단위 Trie)
    -> 금칙어 필터, 코딩테스트의 접두사 관련 문제 (전화번호 목록, 문자열 집합)
    -> 공간복잡도: O(전체 노드 수), 최악은 모든 문자열의 글자 수 합, 접두사 공유가 많을수록 줄어듦
*/

public class _9_Trie {

  static class ArrayBasedBasicTrie {

    static class Node {

      private final char c;
      private Node[] child = new Node[26];
      private boolean isEnd = false;

      public Node(char c) {
        this.c = c;
      }
    }

    private final Node root = new Node('\0');

    public void insert(String word) {
      Node curNode = root;
      int[] idxArr = checkInputAndGetIdxArr(word);

      for (int i = 0; i < word.length(); i++) {
        int idx = idxArr[i];

        if (curNode.child[idx] == null) {
          curNode.child[idx] = new Node(word.toLowerCase().charAt(i));
        }
        curNode = curNode.child[idx];
      }
      curNode.isEnd = true;
    }

    public boolean search(String word) {
      Node node = findNode(word);
      return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
      return findNode(prefix) != null;
    }

    private Node findNode(String word) {
      Node curNode = root;
      int[] idxArr = checkInputAndGetIdxArr(word);

      for (int idx : idxArr) {
        curNode = curNode.child[idx];
        if (curNode == null) {return null;}
      }
      return curNode;
    }

    // 처음에 시도 한 것
    // public boolean search(String word) {
    //   return doSearch(word, false);
    // }
    //
    // public boolean doSearch(String word, boolean isFullSearch) {
    //   word = word.toLowerCase();
    //   int[] idxArr = checkInputAndGetIdxArr(word);
    //
    //   for (Node curNode : arr) {
    //     if (curNode == null) {continue;}
    //
    //     if (curNode.c != idxArr[0]) {
    //       for (int wordIdx : idxArr) {
    //         Node nextNode = curNode.child[wordIdx];
    //         if (nextNode == null) {continue;}
    //         return true;
    //       }
    //
    //       if (!isFullSearch) {
    //         return false;
    //       }
    //     }
    //   }
    //   return false;
    // }
    //
    // public boolean startsWith(String prefix) {
    //   prefix = prefix.toLowerCase();
    //   int[] idxArr = checkInputAndGetIdxArr(prefix);
    //   if (arr[idxArr[0]] == null || idxArr[0] != arr[idxArr[0]].c - 'a') {
    //     return false;
    //   }
    //
    //   return doSearch(prefix, true);
    // }

    public ArrayList<String> collectWordsFromPrefix(String prefix) {

      Node start = findNode(prefix);
      ArrayList<String> result = new ArrayList<>();

      if (start == null) {return result;}

      StringBuilder stb = new StringBuilder(prefix);

      collect(start, stb, result);

      return result;
    }

    private void collect(Node node, StringBuilder stb, ArrayList<String> result) {
      if (node.isEnd) {result.add(stb.toString());}

      for (int i = 0; i < 26; i++) {
        if (node.child[i] == null) {continue;}

        stb.append((char) ('a' + i));
        collect(node.child[i], stb, result);
        stb.deleteCharAt(stb.length() - 1);
      }
    }

    public boolean delete(String word) {
      int[] idxArr = checkInputAndGetIdxArr(word);

      Node curNode = root;
      Node prevNode;

      ArrayList<Node> list = new ArrayList<>();
      list.add(root);

      for (int idx : idxArr) {
        curNode = curNode.child[idx];
        if (curNode == null) {
          System.out.println("저장되어 있지 않은 단어를 삭제 시도 word: " + word);
          return false;
        }
        list.add(curNode);
      }

      if (!curNode.isEnd) {
        System.out.println("저장되어 있지 않은 단어를 삭제 시도 word: " + word);
        return false;
      }

      curNode.isEnd = false;

      for (int i = list.size() - 1; i >= 1; i--) {
        curNode = list.get(i);
        prevNode = list.get(i - 1);

        // 지우는 요소의 자식 체크
        // 삭제 도중 다른 단어의 끝에 도달
        // 마지막으로 삭제 요소를 지우면 root만 남는 경우
        if (hasChild(curNode) || curNode.isEnd) {break;}

        prevNode.child[idxArr[i - 1]] = null;
      }

      return true;
    }

    private boolean hasChild(Node curNode) {
      for (Node node : curNode.child) {
        if (node == null) {continue;}
        return true;
      }
      return false;
    }

    private int[] checkInputAndGetIdxArr(String word) {
      if (word == null || word.isBlank()) {throw new IllegalArgumentException("Null or Blink 입력");}

      word = word.toLowerCase();
      int[] idxArr = new int[word.length()];

      for (int i = 0; i < word.length(); i++) {
        char curChar = word.charAt(i);
        if (curChar < 'a' || curChar > 'z') {
          throw new IllegalArgumentException("영어 이외의 잘못된 요소가 입력");
        }

        idxArr[i] = word.charAt(i) - 'a';
      }
      return idxArr;
    }
  }

  static class HashMapBasedTrie {

    static class Node {

      private final Map<Character, Node> child;
      private boolean isEnd = false;

      public Node(Map<Character, Node> newMap) {
        child = newMap;
      }
    }

    enum Mode {TREE, HASH}

    private final Mode mode;
    private final Node root;

    public HashMapBasedTrie(Mode mode) {
      this.mode = mode;
      root = new Node(createMap());
    }

    private Map<Character, Node> createMap() {
      return mode == Mode.HASH ? new HashMap<>() : new TreeMap<>();
    }

    public void insert(String word) {
      checkInput(word);

      Node curNode = root;
      for (int i = 0; i < word.length(); i++) {

        char curChar = word.charAt(i);

        // 저장된 key 존재 = get(key), key 없을 시 = put
        curNode = curNode.child.computeIfAbsent(curChar, _ -> new Node(createMap()));
      }
      curNode.isEnd = true;
    }

    public boolean search(String word) {
      checkInput(word);
      Node node = findNode(word);
      return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
      checkInput(prefix);
      return findNode(prefix) != null;
    }

    private Node findNode(String word) {

      Node curNode = root;
      for (int i = 0; i < word.length(); i++) {
        char curChar = word.charAt(i);

        Node node = curNode.child.get(curChar);

        if (node == null) {
          return null;
        } else {
          curNode = node;
        }
      }
      return curNode;
    }

    public boolean delete(String word) {
      checkInput(word);

      Node curNode = root;
      Node prevNode;

      ArrayList<Node> list = new ArrayList<>();
      list.add(root);

      for (int i = 0; i < word.length(); i++) {
        char curChar = word.charAt(i);

        Node node = curNode.child.get(curChar);

        if (node == null) {
          System.out.println("저장되어 있지 않은 단어 삭제 시도");
          return false;
        }
        list.add(node);
        curNode = node;
      }

      if (!curNode.isEnd) {
        System.out.println("저장되어 있지 않은 단어 삭제 시도");
        return false;
      }

      curNode.isEnd = false;

      for (int i = list.size() - 1; i >= 1; i--) {
        curNode = list.get(i);
        prevNode = list.get(i - 1);

        if (!curNode.child.isEmpty() || curNode.isEnd) {break;}

        prevNode.child.remove(word.charAt(i - 1));
      }

      return true;
    }


    public ArrayList<String> collectWordsFromPrefix(String prefix) {
      checkInput(prefix);
      ArrayList<String> result = new ArrayList<>();
      Node start = findNode(prefix);

      if (start == null) {return result;}

      StringBuilder stb = new StringBuilder(prefix);

      collect(start, stb, result);

      return result;
    }

    private void collect(Node start, StringBuilder stb, ArrayList<String> result) {
      if (start.isEnd) {result.add(stb.toString());}
      for (Entry<Character, Node> entry : start.child.entrySet()) {

        stb.append(entry.getKey());
        collect(entry.getValue(), stb, result);
        stb.deleteCharAt(stb.length() - 1);
      }
    }

    private void checkInput(String word) {
      if (word == null || word.isBlank()) {
        throw new IllegalArgumentException("입력 단어가 Null or Blink");
      }
    }
  }

  public static void main(String[] args) {
    System.out.println("==========================================================");
    ArrayBasedBasicTrie trie = new ArrayBasedBasicTrie();
    trie.insert("app");
    trie.insert("apple");
    trie.insert("apt");
    trie.insert("bat");

    System.out.println("trie.search(\"app\") = " + trie.search("app"));
    System.out.println("trie.search(\"ap\") = " + trie.search("ap"));
    System.out.println("trie.search(\"apples\") = " + trie.search("apples"));
    System.out.println("trie.search(\"apple\") = " + trie.search("apple"));
    System.out.println("trie.startsWith(\"ap\") = " + trie.startsWith("ap"));
    System.out.println("trie.startsWith(\"b\") = " + trie.startsWith("b"));
    System.out.println("trie.startsWith(\"c\") = " + trie.startsWith("c"));

    System.out.println("trie.collectWordsFromPrefix(\"ap\") = " + trie.collectWordsFromPrefix("ap"));

    trie.delete("apple");
    System.out.println("trie.search(\"apple\") = " + trie.search("apple"));
    System.out.println("trie.search(\"app\") = " + trie.search("app"));
    System.out.println("trie.search(\"apt\") = " + trie.search("apt"));

    System.out.println("==========================================================");
    HashMapBasedTrie hashMapTrie = new HashMapBasedTrie(Mode.HASH);
    hashMapTrie.insert("app");
    hashMapTrie.insert("apple");
    hashMapTrie.insert("apt");
    hashMapTrie.insert("bat");

    System.out.println("hashMapTrie.search(\"app\") = " + hashMapTrie.search("app"));
    System.out.println("hashMapTrie.search(\"ap\") = " + hashMapTrie.search("ap"));
    System.out.println("hashMapTrie.search(\"apples\") = " + hashMapTrie.search("apples"));
    System.out.println("hashMapTrie.search(\"apple\") = " + hashMapTrie.search("apple"));
    System.out.println("hashMapTrie.startsWith(\"ap\") = " + hashMapTrie.startsWith("ap"));
    System.out.println("hashMapTrie.startsWith(\"b\") = " + hashMapTrie.startsWith("b"));
    System.out.println("hashMapTrie.startsWith(\"c\") = " + hashMapTrie.startsWith("c"));
    System.out.println("hashMapTrie.collectWordsFromPrefix(\"ap\") = " + hashMapTrie.collectWordsFromPrefix("ap"));

    hashMapTrie.delete("apple");
    System.out.println("hashMapTrie.search(\"apple\") = " + hashMapTrie.search("apple"));
    System.out.println("hashMapTrie.search(\"app\") = " + hashMapTrie.search("app"));
    System.out.println("hashMapTrie.search(\"apt\") = " + hashMapTrie.search("apt"));

    System.out.println("==========================================================");
    HashMapBasedTrie treeMapTrie = new HashMapBasedTrie(Mode.TREE);
    treeMapTrie.insert("app");
    treeMapTrie.insert("apple");
    treeMapTrie.insert("apt");
    treeMapTrie.insert("bat");

    System.out.println("treeMapTrie.search(\"app\") = " + treeMapTrie.search("app"));
    System.out.println("treeMapTrie.search(\"ap\") = " + treeMapTrie.search("ap"));
    System.out.println("treeMapTrie.search(\"apples\") = " + treeMapTrie.search("apples"));
    System.out.println("treeMapTrie.search(\"apple\") = " + treeMapTrie.search("apple"));
    System.out.println("treeMapTrie.startsWith(\"ap\") = " + treeMapTrie.startsWith("ap"));
    System.out.println("treeMapTrie.startsWith(\"b\") = " + treeMapTrie.startsWith("b"));
    System.out.println("treeMapTrie.startsWith(\"c\") = " + treeMapTrie.startsWith("c"));
    System.out.println("treeMapTrie.collectWordsFromPrefix(\"ap\") = " + treeMapTrie.collectWordsFromPrefix("ap"));

    treeMapTrie.delete("apple");
    System.out.println("treeMapTrie.search(\"apple\") = " + treeMapTrie.search("apple"));
    System.out.println("treeMapTrie.search(\"app\") = " + treeMapTrie.search("app"));
    System.out.println("treeMapTrie.search(\"apt\") = " + treeMapTrie.search("apt"));
  }
}


