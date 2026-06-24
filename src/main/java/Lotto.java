import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Lotto extends Application {

	private final List<Set<Integer>> numList = new ArrayList<>();
	private final RandomGenerator rg = RandomGeneratorFactory.getDefault().create();
	private final List<Integer> selectedIndices = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		Label countLabel = new Label("몇 세트 생성?, -1 입력 시 종료");
		countLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 16));
		TextField countField = new TextField();

		Label pickLabel = new Label("출력 할 세트를 하나씩 입력 (Enter 키로 확인):");
		pickLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 16));
		TextField pickField = new TextField();

		TextArea resultArea = new TextArea();
		resultArea.setEditable(false);

		VBox root = new VBox(10,
			countLabel, countField,
			pickLabel, pickField,
			resultArea
		);

		URL imageUrl = Lotto.class.getResource("/3196.png");
		if (imageUrl == null) {
			System.out.println("이미지 못 찾음");
		} else {
			System.out.println("이미지 경로: " + imageUrl.toExternalForm());
		}

		// root.setPadding(new Insets(15));
		// root.setStyle(
		// 	"-fx-background-image: url('/3196.png'); -fx-background-size: cover; -fx-background-repeat: no-repeat; ");

		// [1] 세트 생성: Enter 키로 처리
		countField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				numList.clear();
				selectedIndices.clear();
				resultArea.clear();
				pickField.clear();

				try {
					int n = Integer.parseInt(countField.getText());

					if (countField.getText().trim().equals("-1")) {
						System.exit(0); // 프로그램 종료
					}

					for (int i = 0; i < n; i++) {
						Set<Integer> numbers = new TreeSet<>();
						while (numbers.size() < 6) {
							numbers.add(rg.nextInt(1, 46));
						}
						numList.add(numbers);
					}
					resultArea.setText(n + "개의 세트를 생성했습니다.\n아래에 세트를 하나씩 입력하세요.");
				} catch (NumberFormatException ex) {
					resultArea.setText("숫자를 정확히 입력해주세요.");
				}
			}
		});

		// [2] 세트 하나씩 입력: Enter 키로 처리, 총 5개 입력 시 결과 출력
		pickField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {

				if (selectedIndices.size() == 5) {
					resultArea.setText("이미 5개를 선택했습니다. 세트를 새로 생성하세요.");
					pickField.clear();
					return;
				}


				String input = pickField.getText().trim();
				try {
					int idx = Integer.parseInt(input);
					if (idx < 0 || idx >= numList.size()) {
						resultArea.setText(idx + "는 존재하지 않는 세트 입니다.");
						pickField.clear();
						return;
					}

					if (selectedIndices.contains(idx)) {
						resultArea.setText("중복된 세트입니다. 다시 입력하세요.");
						pickField.clear();
						return;
					}

					selectedIndices.add(idx);
					resultArea.appendText("\n[" + selectedIndices.size() + "/5] 선택됨: " + idx);

					if (selectedIndices.size() == 5) {
						selectedIndices.sort(Integer::compareTo);
						resultArea.clear();
						resultArea.appendText("결과:\n");
						resultArea.appendText("==================================================\n");
						resultArea.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
						for (int i : selectedIndices) {
							resultArea.appendText(i + "번 세트: " + numList.get(i) + "\n");
						}
						resultArea.appendText("==================================================");
					}

					pickField.clear();
				} catch (NumberFormatException ex) {
					resultArea.setText("숫자를 입력해주세요.");
					pickField.clear();
				}
			}
		});

		Scene scene = new Scene(root, 680, 585);
		primaryStage.setTitle("로또 세트 선택기");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
