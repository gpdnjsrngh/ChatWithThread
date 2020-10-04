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
	
	//����
	private ServerSocket listener = null;
	private Socket socket = null;
	
	public ChatServer2() {
		setTitle("ä�� ����");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Ŭ���̾�Ʈ���� ���� �޽��� �Է��� �ؽ�Ʈ �ʵ忡 �׼Ǹ����� �ޱ�
		serverMsg = new JTextField();
		serverMsg.addActionListener(this);
		
		//���̾ƿ� ����
		Container c= getContentPane();
		c.setLayout(new BorderLayout());
		//���ʿ� �ؽ�Ʈ �ʵ� ���̱�
		c.add(serverMsg, BorderLayout.NORTH);
		
		//����� clientMsg ���̱�
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
				//9999���� ���� ��ٸ�
				listener = new ServerSocket(9999);
				//accept()�� blocking call
				socket = listener.accept();
				clientMsg.append("Ŭ���̾�Ʈ ����\n");
				
				//reader, writer ����
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				//�ٸ� ����� 9999 ������ ���� �ִ�.
				handleError(e.getMessage());
			}
			
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField t= (JTextField)e.getSource();
		String msg = t.getText();
		t.setText("");
		//������ ������ �޽��� text area�� ���
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
	
	//Ŭ���̾�Ʈ�κ��� �� �޽��� ����� JTextArea
	class Receiver extends JTextArea implements Runnable {
		public void run() {
			while(true) {
				String msg;
				try {
					msg = in.readLine();
					this.append(msg+"\n");
				} catch (IOException e) {
					//��밡 ������ ������
					handleError(e.getMessage());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatServer2();
	}
	
}
