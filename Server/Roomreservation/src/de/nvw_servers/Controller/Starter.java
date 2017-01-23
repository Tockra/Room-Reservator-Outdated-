package de.nvw_servers.Controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import javax.crypto.NoSuchPaddingException;

import de.nvw_servers.exceptions.AuthentificationFailedException;
import de.nvw_servers.exceptions.IllegalConfigException;

public class Starter {

	public static void main(String[] args) {
		System.setProperty("jsse.enableSNIExtension", "false");
		if(LocalDateTime.now().getMonthValue() == 12 && LocalDateTime.now().getDayOfMonth() >= 24) {
			System.out.println("Vom 24.12 bis zum 31.12 funktioniert der Reservator nicht wie er soll.\n Deshalb startet er nicht.");
			return;
		}
			
      
		SettingManager sm = null;
		ReservationManager rm = null;
		ConsoleListener cl;
		try {
			sm = new SettingManager();
		} catch (IllegalConfigException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} 
		try {
			if(sm.getUsername().equals(""))
				throw new AuthentificationFailedException("");
			rm = new ReservationManager(sm.getUsername(), sm.getPassword());
			cl = new ConsoleListener(rm,sm);
			
		}
		catch (NullPointerException | AuthentificationFailedException e) {
			System.out.println("Falscher Nutzername in der Config, versuche manuellen Login! ");
			cl = new ConsoleListener(sm);
		}
		
		if(sm != null && rm != null)  {
			new Thread(new Reservator(sm,rm)).start();
		}
		try {
			new Thread(new ConnectionListener(cl.getCommandListener())).start();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cl.listenCommands();
		
	}

}
