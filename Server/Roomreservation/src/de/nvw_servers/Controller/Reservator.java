package de.nvw_servers.Controller;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.nvw_servers.Model.Reservation;
import de.nvw_servers.exceptions.AuthentificationFailedException;
import de.nvw_servers.exceptions.IllegalTimeException;

public class Reservator implements Runnable {
	private SettingManager sm;
	private ReservationManager rm;
	private List<LocalDate> reservated;

	public Reservator(SettingManager sm, ReservationManager rm) {
		this.rm = rm;
		this.sm = sm;
		
		loadPlaned();
	}
	
	public void checkTime() throws AuthentificationFailedException, IOException {
		while(true) {
			try {
				if(sm.getReservationTime().getHour() == LocalTime.now().getHour() && sm.getReservationTime().getMinute() == LocalTime.now().getMinute()) {
					Thread.sleep(30000+(new Random().nextInt((60000*4))));
					reservateRegularys();
					
					System.out.println("["+LocalDateTime.now()+"]: Automatische Reservierung durchgeführt.");
					System.out.print(">> ");
				}
				Thread.sleep(800*60);
			} 
			catch (InterruptedException e) {
				throw new IllegalArgumentException("Thread Problem");
			}
		}
	}
	
	//TODO: reservated Speichern + mehrere Reservierungen an einem Tag (statt days speichern Tupel mit (Day, wann reserviert)
	
	
	private void reservateRegularys() throws AuthentificationFailedException, IOException,InterruptedException {
		LocalDate res = rm.findLastDayToReservate(reservated.size() != 0 ? reservated.get(reservated.size() -1 ):null);
		System.out.println("[ "+ LocalDateTime.now()+  "] Nächste Reservierung an "+res);
		for(int i=0;i<=4;i++) {
			boolean reservated_yet = false;
			for(Reservation r : sm.getPlaned()) {
				LocalDateTime start = LocalDateTime.of(res.plusDays(i).getYear(), res.plusDays(i).getMonth(), res.plusDays(i).getDayOfMonth(), r.getStart().getHour(), 0);
				LocalDateTime end = LocalDateTime.of(res.plusDays(i).getYear(), res.plusDays(i).getMonth(), res.plusDays(i).getDayOfMonth(), r.getEnd().getHour(), 0);
				
				try {
					switch(r.getWeekday()) {
					case "montag" :
						if(res.plusDays(i).getDayOfWeek().getValue() == 1 && !reservated.contains(res.plusDays(i))) {
							reservated_yet = true;
							rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
						}
						break;
					case "dienstag" :
						if(res.plusDays(i).getDayOfWeek().getValue() == 2 && !reservated.contains(res.plusDays(i))) {
							reservated_yet = true;
							rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
						}
						break;
					case "mittwoch" :
						if(res.plusDays(i).getDayOfWeek().getValue() == 3 && !reservated.contains(res.plusDays(i))) {
							reservated_yet = true;
							rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
						}
						break;
					case "donnerstag" :
						if(res.plusDays(i).getDayOfWeek().getValue() == 4 && !reservated.contains(res.plusDays(i))) {
							reservated_yet = true;
							rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
						}
						break;
					case "freitag" :
						if(res.plusDays(i).getDayOfWeek().getValue() == 5 && !reservated.contains(res.plusDays(i))){						
							reservated_yet = true;
							rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
						}
						break;
						
					}
				}
				catch(IllegalTimeException e) {
					System.out.println("["+LocalDateTime.now()+"]: Regelmäßige Reservierung um "+ start+ " fehlgeschlagen: "+e);
					System.out.print(">> ");
				}
			}
			if(reservated_yet) {
				reservated.add(res.plusDays(i));
				if(reservated.size() > 5) {
					reservated.remove(0);
				}
				
				serializePlaned();
			}
		}
	
		/* Outdated . Funktionalität wird entfernt 
		for(Reservation r : rm.getPlanedReservations()) {
			if(res.getYear() == r.getStart().getYear() && res.getDayOfYear() == r.getStart().getDayOfYear()) {
				LocalDateTime start = LocalDateTime.of(res.getYear(), res.getMonth(), res.getDayOfMonth(), r.getStart().getHour(), 0);
				LocalDateTime end = LocalDateTime.of(res.getYear(), res.getMonth(), res.getDayOfMonth(), r.getEnd().getHour(), 0);
				
				rm.addOwnReservation(start, end, r.getRoom(), r.getComment(), r.getOpen());
				
			}
		}*/
	}
	
	public void serializePlaned() throws IOException {	
		try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(".reservated"));
            os.writeObject(reservated);
            os.close();
	    }
		catch(FileNotFoundException e) {
				if(new File(".reservated").createNewFile())
					System.out.println("Save File für die letzten 5 Reservierungen wurde erstellt.");
        }
	}
	
	@SuppressWarnings("unchecked")
	private void loadPlaned() {
		//reservated aus Datei laden
		try{
	        ObjectInputStream is = new ObjectInputStream(new FileInputStream(".reservated"));
	        reservated = (LinkedList<LocalDate>)(is.readObject());
	        if(reservated != null)
	        	System.out.println("Liste mit " + reservated.size() + " Einträgen geladen.");
	        is.close();
		}
		catch(FileNotFoundException e) {
			try {
				if(new File(".reservated").createNewFile())
					System.out.println("Save File für die letzten 5 Reservierungen wurde erstellt.");
			} catch (IOException e1) {
				System.out.println("IOException occured :(");
				e.printStackTrace();  
			}
		}
		catch(EOFException e) {
			
		}
		catch(Exception e){
		        System.out.println("IOException occured :(");
		        e.printStackTrace();    
		}
		
		if(reservated == null) {
			System.out.println("Keine Liste in Datei (.reservated) gefunden.");
			reservated = new LinkedList<>();
		}
		else
		{
			for(LocalDate r: reservated )
				System.out.println(r);
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				checkTime();
			} catch (AuthentificationFailedException | IOException e) {
				System.out.println(e.getMessage());
			} 
		}
	}
	
}
