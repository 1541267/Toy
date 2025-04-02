import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH시 mm분 ss초");

	private Label dateTimeLabel;
	private Label christmasLabel;
	private Label progressLabel;
	private ProgressBar progressBar;
	private Label passingTimeLabel;
	private Label remainingTimeLabel;

	private ScheduledExecutorService scheduler;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Year Progress");
		primaryStage.getIcons().add(new Image("/icon.png"));
		primaryStage.setAlwaysOnTop(true);
		// 메인 컨테이너
		VBox root = new VBox(10);
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);
		root.setStyle("-fx-background-color: #ffffff; -fx-background-image: url('/maxresdefault.jpg');"
			+ "-fx-background-size: cover; -fx-background-repeat: no-repeat");

		// 현재 날짜/시간 레이블
		dateTimeLabel = new Label();
		dateTimeLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 16));
		dateTimeLabel.setTextFill(Color.rgb(0, 0, 0));

		// 진행률 섹션
		VBox progressSection = new VBox(5);
		progressSection.setAlignment(Pos.CENTER);

		progressLabel = new Label();
		progressLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		progressLabel.setTextFill(Color.rgb(0, 0, 0));

		progressBar = new ProgressBar(0);
		progressBar.setPrefWidth(300);
		progressBar.setStyle("-fx-accent: #ff0063;");

		progressSection.getChildren().addAll(progressLabel, progressBar);

		// 크리스마스까지 레이블
		christmasLabel = new Label();
		christmasLabel.setAlignment(Pos.CENTER_LEFT);
		christmasLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		christmasLabel.setTextFill(Color.rgb(0, 0, 0));

		// 경과 시간 정보
		GridPane timeGrid = new GridPane();
		timeGrid.setAlignment(Pos.CENTER_LEFT);
		timeGrid.setHgap(10);
		timeGrid.setVgap(10);
		timeGrid.setPadding(new Insets(10));

		Label passingTimeTitle = new Label("보낸 시간");
		passingTimeTitle.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		passingTimeTitle.setTextFill(Color.rgb(0, 0, 0));
		passingTimeLabel = new Label();
		passingTimeLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		passingTimeLabel.setTextFill(Color.rgb(0, 0, 0));

		Label remainingTimeTitle = new Label("남은 시간");
		remainingTimeTitle.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		remainingTimeTitle.setTextFill(Color.rgb(0, 0, 0));
		remainingTimeLabel = new Label();
		remainingTimeLabel.setFont(Font.font("Malgun Gothic", FontWeight.BOLD, 14));
		remainingTimeLabel.setTextFill(Color.rgb(0, 0, 0));

		timeGrid.add(christmasLabel, 0, 0);
		timeGrid.add(passingTimeTitle, 0, 1);
		timeGrid.add(passingTimeLabel, 0, 2);
		timeGrid.add(remainingTimeTitle, 0, 3);
		timeGrid.add(remainingTimeLabel, 0, 4);

		// 모든 요소를 root에 추가
		root.getChildren().addAll(
			dateTimeLabel,
			progressSection,
			// christmasLabel,
			timeGrid
		);

		// 씬 생성 및 표시
		Scene scene = new Scene(root, 640, 360);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);

		// 1초마다 데이터 업데이트
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(this::updateData, 0, 1, TimeUnit.SECONDS);
	}

	private void updateData() {
		Platform.runLater(() -> {
			// 현재 날짜/시간 정보 한 번만 가져오기
			LocalDateTime currentDateTime = LocalDateTime.now();
			int currentYear = currentDateTime.getYear();
			LocalDate currentDate = currentDateTime.toLocalDate();

			// 연초와 연말 계산
			LocalDate firstDay = LocalDate.of(currentYear, 1, 1);
			LocalDateTime firstDateTime = firstDay.atStartOfDay();
			LocalDate lastDay = LocalDate.of(currentYear, 12, 31);

			// 다음 해 계산
			LocalDateTime nextYear = LocalDateTime.of(currentYear + 1, 1, 1, 0, 0);

			// 크리스마스 계산
			LocalDate christmasDay = LocalDate.of(
				currentDate.isAfter(LocalDate.of(currentYear, 12, 25)) ? currentYear + 1 : currentYear,
				12, 25);
			long daysUntilChristmas = ChronoUnit.DAYS.between(currentDate, christmasDay);

			// 올해 총 일수
			double daysUntilNextYear = ChronoUnit.DAYS.between(currentDate, lastDay.plusDays(1));

			// 다음 해까지 남은 시간
			long hoursUntilNextYear = ChronoUnit.HOURS.between(currentDateTime, nextYear);
			long minutesUntilNextYear = ChronoUnit.MINUTES.between(currentDateTime, nextYear);
			long secondsUntilNextYear = ChronoUnit.SECONDS.between(currentDateTime, nextYear);

			// 경과 시간 계산
			long passingTimeDays = ChronoUnit.DAYS.between(firstDateTime, currentDateTime);
			long passingTimeHours = ChronoUnit.HOURS.between(firstDateTime, currentDateTime);
			long passingTimeMinutes = ChronoUnit.MINUTES.between(firstDateTime, currentDateTime);
			long passingTimeSeconds = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);

			// 진행률 계산 (초 단위 비교)
			double totalSeconds = ChronoUnit.SECONDS.between(firstDateTime, nextYear);
			double currentSeconds = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);
			double progressPercentageSeconds = (currentSeconds / totalSeconds * 100);

			// UI 업데이트
			dateTimeLabel.setText(String.format("%s %s, %s",
				currentDateTime.format(DATE_FORMAT),
				currentDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA),
				currentDateTime.format(TIME_FORMAT)));

			christmasLabel.setText(String.format("크리스마스까지 %d일 남았습니다", daysUntilChristmas));

			progressLabel.setText(String.format("%d년 진행률: %.2f%%", currentYear, progressPercentageSeconds));
			progressBar.setProgress(progressPercentageSeconds / 100);

			passingTimeLabel.setText(String.format("%d일 | %d시간 | %d분 \n%d초",
				passingTimeDays, passingTimeHours, passingTimeMinutes, passingTimeSeconds));

			remainingTimeLabel.setText(String.format("%d일 | %d시간 | %d분 \n%d초",
				(int)daysUntilNextYear, hoursUntilNextYear, minutesUntilNextYear, secondsUntilNextYear));
		});
	}

	@Override
	public void stop() {
		// 애플리케이션 종료 시 스케줄러 정리
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}