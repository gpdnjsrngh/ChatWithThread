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
	//����
	private Socket socket = null;
	
	public ChatClient2() {
		setTitle("ä�� Ŭ���̾�Ʈ");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Ŭ���̾�Ʈ���� ���� �޽��� �Է��� �ؽ�Ʈ �ʵ忡 �׼Ǹ����� �ޱ�
		clientMsg = new JTextField();
		clientMsg.addActionListener(this);
		
		//���̾ƿ� ����
		Container c= getContentPane();
		c.setLayout(new BorderLayout());
		//���ʿ� �ؽ�Ʈ �ʵ� ���̱�
		c.add(clientMsg, BorderLayout.NORTH);
		
		//�Ʒ��� ip�ּ�, ��Ʈ�ѹ� �Է��ϰ� ������ �����ϴ� �г� ���̱�
		JPanel southPanel = new JPanel();
		ipAddr = new JTextField(10);
		portNo = new JTextField(6);
		startBtn = new JButton("����");
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
		
		//����� clientMsg ���̱�
		serverMsg = new Receiver();
		c.add(new JScrollPane(serverMsg), BorderLayout.CENTER);
		
		setSize(400, 200);
		setVisible(true);
		 
	}
	
	private void setup(String ip, int portNo) {
			
			try {
				socket = new Socket(ip, portNo);
				serverMsg.append("������ ���� ����!\n");
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				handleError(e.getMessage());
			}		
	}

	//����������
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
	
	//Ŭ���̾�Ʈ�κ��� �� �޽��� ����� JTextArea
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
