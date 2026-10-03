package Study.DataStructure.Code;

import java.util.ArrayList;

public class _9_Trie {

  static class Node {

    private final char c;
    private Node[] child = new Node[26];
    private boolean isEnd = false;

    public Node(char c) {
      this.c = c;
    }
  }

  static class ArrayBasedBasicTrie {

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

      if (start == null) {return null;}

      ArrayList<String> result = new ArrayList<>();

      StringBuilder stb = new StringBuilder();

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

    public void delete(String word) {
      int[] idxArr = checkInputAndGetIdxArr(word);
      word = word.toLowerCase();


    }

    private int[] checkInputAndGetIdxArr(String word) {
      if (word == null) {throw new IllegalArgumentException("Null 입력");}

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

  static class HashMapBasedTrie {}

  public static void main(String[] args) {
    ArrayBasedBasicTrie trie = new ArrayBasedBasicTrie();
    trie.insert("app");
    trie.insert("apple");
    trie.insert("apt");
    trie.insert("bat");

    System.out.println("trie.search(\"app\") = " + trie.search("app"));
    System.out.println("trie.search(\"ap\") = " + trie.search("ap"));
    System.out.println("trie.search(\"apples\") = " + trie.search("apples"));
    System.out.println("trie.startsWith(\"ap\") = " + trie.startsWith("ap"));
    System.out.println("trie.startsWith(\"b\") = " + trie.startsWith("b"));
    System.out.println("trie.startsWith(\"c\") = " + trie.startsWith("c"));

    System.out.println("trie.collectWordsFromPrefix(\"ap\") = " + trie.collectWordsFromPrefix("ap"));


  }
}


