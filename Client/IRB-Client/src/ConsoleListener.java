import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;


public class ConsoleListener {
	private Scanner consoleIn;
	private Socket s;
	private PrintWriter out;
	private BufferedReader in;
	private PublicKey pk;
	private EasyCrypt pubKey;
	private EasyCrypt semKey;
	private SecretKeySpec sk;
	/**
	 * Erzeugt einen CommandListener
	 * 
	 */
	public ConsoleListener(String host) throws IllegalArgumentException {
		loadKey();
		
		try {
			pubKey = new EasyCrypt(pk, "RSA");
			
			s = new Socket(host,42234);
			
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(),true);
			out.println(pubKey.encrypt(pubKey.decrypt(in.readLine())));

			genKey(pubKey.decrypt(in.readLine()));
			semKey = new EasyCrypt(sk, "AES");
		} 
		catch (IOException e) {
			throw new IllegalArgumentException("Probleme beim Verbindungsaufbau mit dem Server.: "+e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		consoleIn = new Scanner(System.in); // Erzeugt einen neuen Scanner, der die Eingaben scannt
		System.out.println("Willkommen bei der IRB Lernraumreservierung.");
	}
	
	public void listen() {
		while(true) {
			System.out.print(">> ");
			String inputText = consoleIn.nextLine();
			
			try {
				out.println(semKey.encrypt(inputText));
				System.out.print(semKey.decrypt(in.readLine()));
				
			} catch (IOException e) {
				System.out.println("Fehler bei Netzwerkkommunikation");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadKey() {
		try
	      {
	         FileInputStream fileIn = new FileInputStream("client");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         pk = (PublicKey) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(ClassNotFoundException | IOException e)
	      {
	         throw new IllegalArgumentException("Problem beim Laden");
	      }
	}
	
	private void genKey(String s) {
		s = s.replace("\n", "");
		byte[] b = Base64.getDecoder().decode(s);
		sk = new SecretKeySpec(b, "AES");
	}

}
