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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient2 extends JFrame implements ActionListener {
	private JTextField clientMsg = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private Receiver serverMsg = null;
	private JTextField ipAddr = null;
	private JTextField portNo = null;
	private JButton startBtn = null;
	//소켓
	private Socket socket = null;
	
	public ChatClient2() {
		setTitle("채팅 클라이언트");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//클라이언트에게 보낼 메시지 입력할 텍스트 필드에 액션리스너 달기
		clientMsg = new JTextField();
		clientMsg.addActionListener(this);
		
		//레이아웃 설정
		Container c= getContentPane();
		c.setLayout(new BorderLayout());
		//위쪽에 텍스트 필드 붙이기
		c.add(clientMsg, BorderLayout.NORTH);
		
		//아래에 ip주소, 포트넘버 입력하고 서버에 접속하는 패널 붙이기
		JPanel southPanel = new JPanel();
		ipAddr = new JTextField(10);
		portNo = new JTextField(6);
		startBtn = new JButton("접속");
		southPanel.add(ipAddr);
		southPanel.add(portNo);
		southPanel.add(startBtn);
		c.add(southPanel, BorderLayout.SOUTH);
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ipAddr.getText();
				int pNo = Integer.parseInt(portNo.getText());
				setup(ip, pNo);
				
				Thread th = new Thread(serverMsg);
				th.start();
			}
		});
		
		//가운데에 clientMsg 붙이기
		serverMsg = new Receiver();
		c.add(new JScrollPane(serverMsg), BorderLayout.CENTER);
		
		setSize(400, 200);
		setVisible(true);
		 
	}
	
	private void setup(String ip, int portNo) {
			
			try {
				socket = new Socket(ip, portNo);
				serverMsg.append("서버에 접속 성공!\n");
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				handleError(e.getMessage());
			}		
	}

	//엔터쳤을때
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField t = (JTextField)e.getSource();
		String msg = t.getText();
		serverMsg.append(msg+"\n");
		t.setText("");
		
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
		@Override
		public void run() {
			while(true) {
				String msg;
				try {
					msg = in.readLine();
					this.append(msg+"\n");
				} catch (IOException e) {
					handleError(e.getMessage());
				}
				
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatClient2();
	}
	
}
