package DFS;

// dfs 연습
// 연결된 요소 확인 과정

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaDFS2 {
	static int count = 0;
	static boolean[] visited;
	static List<Integer>[] list;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int n = readNumber(br);
		int m = readNumber(br);

		list = new ArrayList[n + 1];
		visited = new boolean[n + 1];

		for (int i = 1; i < n + 1; i++) {
			list[i] = new ArrayList<>();
		}

		for (int i = 0; i < m; i++) {
			int a = readNumber(br);
			int b = readNumber(br);

			list[a].add(b);
			list[b].add(a);
		}

		dfs(1);

		System.out.println("list = " + Arrays.toString(list));
		System.out.print(count);
	}

	private static void dfs(int node) {
		visited[node] = true;
		System.out.println("node = " + node);

		for (int connected : list[node]) {
			if (!visited[connected]) {
				count++;
				dfs(connected);
			}
		}

	}

	private static int readNumber(BufferedReader br) throws IOException {

		int value = 0;
		int c = br.read();

		do {
			value = value * 10 + (c - '0');
		} while ((c = br.read()) >= '0' && c <= '9');

		return value;
	}
}
