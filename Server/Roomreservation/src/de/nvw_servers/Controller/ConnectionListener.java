package de.nvw_servers.Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ConnectionListener implements Runnable {
	private CommandListener cl;
	private ServerSocket socket;
	private BufferedReader in;
	private PrintWriter out;
	private PrivateKey pk;
	private EasyCrypt privKey;
	private EasyCrypt semKey;
	private SecretKeySpec sk;
	
	public ConnectionListener(CommandListener cl) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		loadKey();
		
		this.cl = cl;
		
		try {
			privKey = new EasyCrypt(pk, "RSA");
			socket = new ServerSocket(42234);
		} catch (IOException e) {
			System.out.println("Fehler bei Socketerzeugung!");
			System.out.print(">> ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	public void waitForConnection() {
		
		while(true) {
			Socket s = null;
			try {
				s = socket.accept();
				s.setSoTimeout(10000);
				out = new PrintWriter(s.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				int rand = new Random().nextInt(10000);
				out.println(privKey.encrypt(rand +""));
				
				String input = privKey.decrypt(in.readLine());
				if(!input.equals(rand + "")) {
					System.out.println("Connection from "+s.getLocalAddress()+" Refused!");
					System.out.print(">> ");
					s.close();
					continue;
				}

				out.println(privKey.encrypt(generateKey()));
				semKey = new EasyCrypt(sk, "AES");
				s.setSoTimeout(0);
				System.out.println(LocalDateTime.now().toString().replaceAll("T", " ") + ": Client "+s.getInetAddress().toString().replaceAll("/", "")+" connected.");
				System.out.print(">> ");
				while(true) {
					try {
						out.println(semKey.encrypt(cl.submitCommand(semKey.decrypt(in.readLine()))));
					}
					catch(IOException e) {
						System.out.println("Client disconnected.");
						System.out.print(">> ");
						break;
					}
				}
			}
			catch (IOException e) {
				System.out.println("Fehler beim Verbindungsaufbau.: "+e.getMessage());
				System.out.print(">> ");
			}
			catch (Exception e) {
				System.out.println("Encrypte Problem: "+e.getMessage());
//				e.printStackTrace();
				System.out.print(">> ");
			}
			finally {
				try {
					s.close();
					System.out.println("Socket Closed");
					System.out.print(">> ");
				} catch (Exception e) {
				
				}
			}
			
		}
	}

	@Override
	public void run() {
		waitForConnection();
	}
	
	private void loadKey() {
		try
	      {
	         FileInputStream fileIn = new FileInputStream("server");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         pk = (PrivateKey) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(ClassNotFoundException | IOException e)
	      {
	         throw new IllegalArgumentException("Problem beim Laden");
	      }
	}
	
	private String generateKey() {
		byte[] b = new byte[16];
		new Random().nextBytes(b);
		sk = new SecretKeySpec(b, "AES");
		return Base64.getEncoder().encodeToString(b);
	}

}
