package de.nvw_servers.Controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import de.nvw_servers.Model.Reservation;

public class CommandListener {
	private StringBuilder response;
	private SettingManager sm;
	private ReservationManager rm;
	private boolean loggedin;
	
	public CommandListener(SettingManager sm) {
		this.sm = sm;
		loggedin = false;
	}
	
	
	public CommandListener(ReservationManager rm, SettingManager sm) {
		this.sm = sm;
		this.rm = rm;
		loggedin = true;
	}
	
	private void println(String message) {
		response.append(message).append("\n");
	}
	
	private void print(String message) {
		response.append(message);
	}
	
	private void login(String command,String[] inputWords) {
		if(loggedin) {
        	println("Du bist bereits eingeloggt.");
        	return;
	    }
		
	    if(!command.matches(inputWords[0].toLowerCase()+ " [\\p{Punct}\\wäüöÄÜÖ]* [\\p{Punct}\\wäöüÄÖÜ]*")/*inputWords.length != 3*/) {
	    	println("Bitte /login <Nutzername> <Kennwort> verwenden");
	    	return;
	    }
	
	    try {
			rm = new ReservationManager(inputWords[1], inputWords[2]);
			rm.findLastDayToReservate(LocalDate.now());
			loggedin = true;
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	private void reservate(String command) {
		boolean open;
		LocalDateTime from;
		LocalDateTime to;
		String[] inputWords = command.trim().split(" ");
		
		if(!command.matches(inputWords[0].toLowerCase()+" ([1-9]|[12]\\d|3[01]) ([1-9]|1[0-2]) (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d|rand) (true|false) [\\p{Punct}\\wäöüÄÖÜ\\s]*")/*inputWords.length < 8*/) {
			println("Geb bitte /reservate <day> <month> <from> <to> <room> <open> <comment> ein!");
			return;
		}
		
		try {
			int hour = Integer.parseInt(inputWords[3]);
			int month = Integer.parseInt(inputWords[2]);
			int day = Integer.parseInt(inputWords[1]);
			int year = month < LocalDate.now().getMonthValue() ? LocalDate.now().getYear()+1 : LocalDate.now().getYear();
			from = LocalDateTime.of(year, month, day, hour, 0);
			hour = Integer.parseInt(inputWords[4]);
			to = LocalDateTime.of(year, month, day, hour, 0);
			open = Boolean.parseBoolean(inputWords[6]);
			if(from.toLocalDate().getDayOfWeek().getValue() >= 6) {
				println("Reservierung am Wochenende nicht möglich!");
				return;
			}
		}
		catch(NumberFormatException e) {
			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
			return;
		}
		catch(DateTimeException e) {
			println("Die Eingabe kann nicht zum Datum konvertiert werden!");
			return;
		}
		if(!rm.existsRoom(inputWords[5]) && !inputWords[5].equals("rand")) {
			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
			return;
		}
		if(!inputWords[6].equals("true") && !inputWords[6].equals("false") ) {
			println("<open> muss true oder false sein!");
			return;
		}
		try {
			if(!inputWords[5].equals("rand"))
				rm.addOwnReservation(from, to, inputWords[5], command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+7),open);
			else
				rm.addOwnReservation(from, to, command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+7),open);
			
			println("Raum wurde erfolgreich am "+from+" reserviert!");
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	private void reservatea(String command) {
		boolean open;
		LocalDateTime from;
		LocalDateTime to;
		String[] inputWords = command.trim().split(" ");
		
		if(!command.matches(inputWords[0].toLowerCase() + " ([1-9]|[12]\\d|3[01]) ([1-9]|1[0-2]) 20\\d\\d (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d|rand) (true|false) [\\p{Punct}\\wäöüÄÖÜ\\s]*")) {
			println("Geb bitte /reservateA <day> <month> <year> <from> <to> <room> <open> <comment> ein!");
			return;
		}
		try {
			from = LocalDateTime.of(Integer.parseInt(inputWords[3]), Integer.parseInt(inputWords[2]), Integer.parseInt(inputWords[1]), Integer.parseInt(inputWords[4]), 0);
			to = LocalDateTime.of(Integer.parseInt(inputWords[3]), Integer.parseInt(inputWords[2]), Integer.parseInt(inputWords[1]), Integer.parseInt(inputWords[5]),0);
			open = Boolean.parseBoolean(inputWords[7]);
		}
		catch(NumberFormatException e) {
			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
			return;
		}
		catch(DateTimeException e) {
			println("Die Eingabe kann nicht zum Datum konvertiert werden!");
			return;
		}
		if(!rm.existsRoom(inputWords[6]) && !inputWords[6].equals("rand")) {
			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
			return;
		}

		try {
			if(!inputWords[6].equals("rand"))
				rm.addOwnReservation(from, to, inputWords[6], command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+ inputWords[7].length()+8),open);
			else
				rm.addOwnReservation(from, to, command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+ inputWords[7].length()+8),open);
			
			println("Raum wurde erfolgreich am "+from+" reserviert!");
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	private void resnow(String command) {
		boolean open;
		LocalDateTime from;
		LocalDateTime to;
		String[] inputWords = command.trim().split(" ");
		
		if(!command.matches(inputWords[0].toLowerCase()+" (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d|rand) (true|false) [\\p{Punct}\\wäöüÄÖÜ\\s]*")) {
			println("Geb bitte /resnow <from> <to> <room> <open> <comment> ein!");
			return;
		}
		try {
			from = LocalDateTime.now().withHour(Integer.parseInt(inputWords[1])).withMinute(0);
			to = LocalDateTime.now().withHour(Integer.parseInt(inputWords[2])).withMinute(0);
			open = Boolean.parseBoolean(inputWords[4]);
		}
		catch(NumberFormatException e) {
			println("Die Uhrzeiten müssen gültige Zahlen sein!");
			return;
		}
		catch(DateTimeException e) {
			println("Die Eingabe kann nicht zum Datum konvertiert werden!");
			return;
		}
		if(!rm.existsRoom(inputWords[3]) && !inputWords[3].equals("rand")) {
			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
			return;
		}
		if(!inputWords[4].equals("true") && !inputWords[4].equals("false") ) {
			println("<open> muss true oder false sein!");
			return;
		}
		try {
			if(!inputWords[3].equals("rand"))
				rm.addOwnReservation(from, to, inputWords[3], command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+5),open);
			else
				rm.addOwnReservation(from, to, command.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+5),open);
			
			println("Raum wurde erfolgreich reserviert!");
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	private void remove(String command) {
		LocalDateTime from;
		LocalDateTime to;
		String[] inputWords = command.trim().split(" ");
		
		if(!command.matches(inputWords[0].toLowerCase()+" ([1-9]|[12]\\d|3[01]) ([1-9]|1[0-2]) (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d)")) {
			println("Geb bitte /remove <day> <month> <from> <to> <room> ein!");
			return;
		}
		
		try {
			int hour = Integer.parseInt(inputWords[3]);
			int day= Integer.parseInt(inputWords[1]);
			int month= Integer.parseInt(inputWords[2]);
			int year = month < LocalDate.now().getMonthValue() ? LocalDate.now().getYear()+1 : LocalDate.now().getYear();
			from = LocalDateTime.of(year, month, day, hour, 0);
			hour = Integer.parseInt(inputWords[4]);
			to = LocalDateTime.of(year, month, day, hour, 0);
			
		}
		catch(NumberFormatException e) {
			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
			return;
		}
		catch(DateTimeException e) {
			println("Die Eingabe kann nicht zum Datum konvertiert werden!");
			return;
		}
		if(!rm.existsRoom(inputWords[5])) {
			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
			return;
		}
		try {
			rm.removeAllOwnReservations(from, to, inputWords[5]);
			println("Die Raumreservierung wurde erfolgreich gelöscht!");
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
	private void remnow(String command) {
		LocalDateTime from;
		LocalDateTime to;
		String[] inputWords = command.trim().split(" ");
		
		if(!command.matches(inputWords[0].toLowerCase()+" (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d)")) {
			println("Geb bitte /remnow <from> <to> <room> ein!");
			return;
		}
		
		try {
			from = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[1]));
			to = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[2]));
		}
		catch(NumberFormatException e) {
			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
			return;
		}
		catch(DateTimeException e) {
			println("Die Eingabe kann nicht zur Uhrzeit konvertiert werden!");
			return;
		}
		if(!rm.existsRoom(inputWords[3])) {
			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
			return;
		}
		try {
			rm.removeAllOwnReservations(from, to, inputWords[3]);
			println("Die Raumreservierung wurde erfolgreich gelöscht!");
		} catch (Exception e) {
			println(e.getMessage());
		}
	}
	
//	@Deprecated
//	private void wipe(String command) {
//		LocalDateTime from;
//		LocalDateTime to;
//		String[] inputWords = command.trim().split(" ");
//		
//		if(!command.matches(inputWords[0].toLowerCase()+" ([1-9]|[12]\\d|3[01]) ([1-9]|1[0-2]) (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d)")) {
//			println("Geb bitte /wipe <day> <month> <from> <to> <room> ein!");
//			return;
//		}
//		
//		try {
//			int hour = Integer.parseInt(inputWords[3]);
//			int day = Integer.parseInt(inputWords[1]);
//			int month = Integer.parseInt(inputWords[2]);
//			int year = month < LocalDate.now().getMonthValue() ? LocalDate.now().getYear()+1 : LocalDate.now().getYear();
//			from = LocalDateTime.of(year, month, day, hour, 0);
//			hour = Integer.parseInt(inputWords[4]);
//			to = LocalDateTime.of(year, month, day, hour, 0);
//		}
//		catch(NumberFormatException e) {
//			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
//			return;
//		}
//		catch(DateTimeException e) {
//			println("Die Eingabe kann nicht zum Datum konvertiert werden!");
//			return;
//		}
//		if(!rm.existsRoom(inputWords[5])) {
//			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
//			return;
//		}
//		try {
//			rm.removeForeignReservation(from, to, inputWords[5]);
//			println("Die Raumreservierung wurde erfolgreich gewiped!");
//		} catch (AuthentificationFailedException | IllegalTimeException e) {
//			println(e.getMessage());
//		}
//	}
	
//	@Deprecated
//	private void wipenow(String command) {
//		LocalDateTime from;
//		LocalDateTime to;
//		String[] inputWords = command.trim().split(" ");
//		
//		if(!command.matches(inputWords[0].toLowerCase()+" (\\d|1\\d|2[0-4]) (\\d|1\\d|2[0-4]) (\\d|\\d\\d|\\d\\d\\d\\d)")) {
//			println("Geb bitte /wipenow <from> <to> <room> ein!");
//			return;
//		}
//		
//		try {
//			from = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[1]));
//			to = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[2]));
//		}
//		catch(NumberFormatException e) {
//			println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
//			return;
//		}
//		catch(DateTimeException e) {
//			println("Die Eingabe kann nicht zur Uhrzeit konvertiert werden!");
//			return;
//		}
//		if(!rm.existsRoom(inputWords[3])) {
//			println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
//			return;
//		}
//		try {
//			rm.removeForeignReservation(from, to, inputWords[3]);
//			println("Die Raumreservierung wurde erfolgreich gewiped!");
//		} catch (Exception e) {
//			println(e.getMessage());
//		}
//	}
	
	private void showo() {
		for(Reservation r : rm.getPlanedReservations()) {
			println(r.toString());
			println("_______________________________________");
		}
	}
	
	private void shows() {
		for(Reservation r : sm.getPlaned()) {
			println(r.toString());
			println("_______________________________________");
		}
	}
	
	private void help() {
		println("Kommandos                                                            |Nutzen");
		println("=====================================================================|====================================");
		println("/help                                                                |Zeigt diese Nachricht an");
		println("/showo                                                               |Zeigt die einmalig zukünftig geplanten Termine.");
		println("/shows                                                               |Zeigt die regelmäßig zukünftig geplanten Termine.");
		println("/reservate <day> <month> <from> <to> <room> <open> <comment>         |Reserviert am Datum einen Raum im gegebenen Zeitfenster.");
		println("/reservateA <day> <month> <year> <from> <to> <room> <open> <comment> |Wie /reservate nur ohne Einschränkungen.");
		println("/resnow <from> <to> <room> <open> <comment>                          |Reserviert heute einen Raum im gegebenen Zeitfenster.");
		println("/remove <day> <month> <from> <to> <room>                             |Gibt eigene angegebene Reservierung wieder frei.");
		println("/remnow <from> <to> <room>                                           |Gibt eigene heutige angegebene Reservierung wieder frei.");
//		println("/wipe <day> <month> <from> <to> <room>                               |Nutzt einen Exploit um beliebige Reservierungen zu löschen.");
//		println("/wipenow <from> <to> <room>                                          |Nutzt einen Exploit um beliebige Reservierungen heute zu löschen.");
		println("/rooms                                                               |Listet alle Räume auf");
		
	}
	
	private void rooms() {
		HashMap<String,String> rooms = rm.getRooms();
		println("Tag    |Raum");
		println("=======|=============");
		for(String key : rooms.keySet()) {
			print(key);
			for(int i = key.length()+1 ; i<=7;i++)
				print(" ");
			print("|");
			println(rooms.get(key).replace("\\", ""));
		}
		println("rand   |Erster zufällig freier Raum");
	}
	
	public String submitCommand(String command) {
		response = new StringBuilder();
		//print(">> "); // printet zwei Pfeile an jedem Zeilenanfang

		if(command.trim().length() == 0) { //Überprüft, ob etwas eingegeben wurde
			return "";
		}
		
		String[] inputWords = command.trim().split(" ");  // Jedes Wort wird an einen eigenen Arrayindex geschrieben
		
		if(!inputWords[0].toLowerCase().equals("/login") && !loggedin) {
			println("Bitte log dich bei der IRB Raumreservierung ein: /login <Nutzername> <Kennwort>");
		}
		else {
			switch(inputWords[0].toLowerCase()) { //toLowerCase sorgt dafür, dass die Groß und Kleinschreibung der Befehle unrelevant ist
			case "/login": // Der Nutzer hat /login eingegeben
				login(command,inputWords);
				break;
			case "/reservate":  
				reservate(command);
				break;
			case "/reservatea":  
				reservatea(command);
				break;
			case "/resnow":
				resnow(command);
				break;
			case "/remove":
				remove(command);
				break;
			case "/remnow":
				remnow(command);
				break;
//			case "/wipe":
//				wipe(command);
//				break;
//			case "/wipenow":
//				wipenow(command);
//				break;
			case "/showo": // Der Nutzer hat /quit eingegeben
				showo();
				break;
			case "/shows": // Der Nutzer hat /quit eingegeben
				shows();
				break;	
			case "/rooms":
				rooms();
				break;
			case "/help": // Der Nutzer hat /help eingegeben oder einen Unbekannten Befehl eingegeben  (Liste anpassen)
				help();
				break;
			default:
				println("Unbekannter Befehl. Tippe /help ein.");
				break;
			}
			
		}
		return response.toString();
	}

}
