import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.*;

public class BPMCounter extends JFrame implements KeyListener, MouseListener {
	private ArrayList<Long> clickTimes = new ArrayList<>();
	private ArrayList<Double> bpmHistory = new ArrayList<>();
	private JLabel bpmLabel;
	private static final int MOVING_AVERAGE_WINDOW = 5; // 이동 평균을 위한 윈도우 크기

	public BPMCounter() {
		setTitle("Global BPM Detector");
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BPM 표시 라벨
		bpmLabel = new JLabel("BPM: 0", SwingConstants.CENTER);
		bpmLabel.setFont(new Font("Arial", Font.BOLD, 24));

		add(bpmLabel, BorderLayout.CENTER);

		// 키보드 & 마우스 이벤트 감지 설정
		addKeyListener(this);
		addMouseListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		// 글로벌 마우스 및 키보드 리스너 등록
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				handleKeyPress(e);
			}
			return false;
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event instanceof MouseEvent && ((MouseEvent) event).getID() == MouseEvent.MOUSE_PRESSED) {
				recordClick();
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	private void recordClick() {
		long currentTime = System.currentTimeMillis(); // 밀리초 단위로 기록
		clickTimes.add(currentTime);

		if (clickTimes.size() > 5) {
			clickTimes.remove(0);
		}

		if (clickTimes.size() > 1) {
			double bpm = calculateBPM();
			bpmHistory.add(bpm);
			if (bpmHistory.size() > MOVING_AVERAGE_WINDOW) {
				bpmHistory.remove(0); // 오래된 BPM 값 제거
			}

			double averageBPM = calculateMovingAverage();
			bpmLabel.setText(String.format("BPM: %.2f", averageBPM));
		}
	}

	private double calculateBPM() {
		if (clickTimes.size() < 2) return 0;

		long totalInterval = 0;
		for (int i = 1; i < clickTimes.size(); i++) {
			totalInterval += (clickTimes.get(i) - clickTimes.get(i - 1)); // 밀리초 단위 간격 계산
		}

		double averageInterval = totalInterval / (clickTimes.size() - 1); // 평균 간격 계산 (밀리초)
		double secondsPerBeat = averageInterval / 1000.0; // 초 단위로 변환
		return 30 / secondsPerBeat; // BPM 계산
	}

	private double calculateMovingAverage() {
		double sum = 0;
		for (double bpm : bpmHistory) {
			sum += bpm;
		}
		return sum / bpmHistory.size();
	}

	private void resetBPM() {
		clickTimes.clear();
		bpmHistory.clear();
		bpmLabel.setText("BPM: 0");
	}

	private void handleKeyPress(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_TAB) {
			resetBPM(); // Tab 키로 초기화
		} else {
			recordClick(); // 다른 키 입력 시 BPM 기록
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		handleKeyPress(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		recordClick();
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			BPMCounter bpmSensor = new BPMCounter();
			bpmSensor.setVisible(true);
		});
	}
}
