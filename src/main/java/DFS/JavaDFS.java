package DFS;

// dfs 연습
// 맵을 상하좌우 이동
// 직접 상하좌우를 입력 가능
// 			dfs(x - 1, y);
// 			dfs(x + 1, y);
// 			dfs(x, y - 1);
// 			dfs(x, y + 1);
// or 배열로 시작 dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class JavaDFS {

	static int[][] matrix;
	static int rowLength, columnLength;
	static boolean[][] visited;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		rowLength = Integer.parseInt(st.nextToken());
		columnLength = Integer.parseInt(st.nextToken());
		matrix = new int[rowLength][columnLength];

		for (int i = 0; i < rowLength; i++) {

			st = new StringTokenizer(br.readLine());

			for (int j = 0; j < columnLength; j++) {
				matrix[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		for (int i = 0; i < rowLength; i++) {
			for (int j = 0; j < columnLength; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		visited = new boolean[rowLength][columnLength];
		dfs(0, 0);

		for(boolean[] b : visited) {
			for (boolean p : b) {
				System.out.print(p + " | ");
			}
			System.out.println();
		}

		if (visited[rowLength - 1][columnLength - 1]) {
			System.out.println("YES");
		} else {
			System.out.println("NO");
		}
	}

		private static void dfs(int x, int y) {
			// 범위 체크
			if (x < 0 || y < 0 || x >= rowLength || y >= columnLength) {
				return;
			}

			// 이미 방문 or 도착 못하면 종료
			if (visited[x][y] || matrix[x][y] == 0) {
				return;
			}

			visited[x][y] = true;

			dfs(x - 1, y);
			dfs(x + 1, y);
			dfs(x, y - 1);
			dfs(x, y + 1);

		}
	}
