package de.nvw_servers.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocalDateTime start;
	private LocalDateTime end;
	String raum;
	String weekday;
	String comment;
	boolean open;
	boolean fromConig;
	
	public Reservation(LocalDateTime start, LocalDateTime end, String weekday, String raum, String comment, boolean open) {
		this.start = start;
		this.end = end;
		this.open = open;
		this.comment = comment;
		this.weekday = weekday;
		this.raum = raum;
	}
	
	public String getComment() {
		return comment;
	}
	
	public boolean getOpen() {
		return open;
	}
	
	public LocalDateTime getStart() {
		return start;
	}
	
	public LocalDateTime getEnd() {
		return end;
	}
	
	public String getWeekday() {
		return weekday;
	}
	
	public String getRoom() {
		return raum;
	}
	
	public String toString() {
		if(weekday.equals(""))
			return "Von: "+start+"\nBis: "+end+"\nRaum: "+ raum+ "\nKommentar: "+comment+"\nÖffentlich: "+ (open?"ja":"nein") ;
		return "Wochentag: "+ weekday + "\nVon: "+ start.getHour()+":00\nBis: "+end.getHour()+":00\nRaum: "+ raum+ "\nKommentar: "+comment+"\nÖffentlich: "+ (open?"ja":"nein") ;
		
	}
	
}
