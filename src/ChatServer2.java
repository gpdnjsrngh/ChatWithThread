import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatServer2 extends JFrame implements ActionListener {
	private JTextField serverMsg = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private Receiver clientMsg = null;
	
	//소켓
	private ServerSocket listener = null;
	private Socket socket = null;
	
	public ChatServer2() {
		setTitle("채팅 서버");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//클라이언트에게 보낼 메시지 입력할 텍스트 필드에 액션리스너 달기
		serverMsg = new JTextField();
		serverMsg.addActionListener(this);
		
		//레이아웃 설정
		Container c= getContentPane();
		c.setLayout(new BorderLayout());
		//위쪽에 텍스트 필드 붙이기
		c.add(serverMsg, BorderLayout.NORTH);
		
		//가운데에 clientMsg 붙이기
		clientMsg = new Receiver();
		c.add(new JScrollPane(clientMsg), BorderLayout.CENTER);
		
		setSize(400, 200);
		setVisible(true);
		
		setup();
		
		Thread th = new Thread(clientMsg);
		th.start();
	}
	
	private void setup() {
			
			try {
				//9999에서 접속 기다림
				listener = new ServerSocket(9999);
				//accept()는 blocking call
				socket = listener.accept();
				clientMsg.append("클라이언트 접속\n");
				
				//reader, writer 생성
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				//다른 사람이 9999 소켓을 쓰고 있다.
				handleError(e.getMessage());
			}
			
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField t= (JTextField)e.getSource();
		String msg = t.getText();
		t.setText("");
		//서버가 보내는 메시지 text area에 출력
		clientMsg.append(msg+"\n");
		try {
			out.write(msg+"\n");
			out.flush();
		} catch (IOException e1) {
			handleError(e1.getMessage());
		}
		
		
	}
	
	private void handleError(String message) {
		System.out.println(message);
		System.exit(1);
	}
	
	//클라이언트로부터 온 메시지 출력할 JTextArea
	class Receiver extends JTextArea implements Runnable {
		public void run() {
			while(true) {
				String msg;
				try {
					msg = in.readLine();
					this.append(msg+"\n");
				} catch (IOException e) {
					//상대가 접속을 끊으면
					handleError(e.getMessage());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatServer2();
	}
	
}
