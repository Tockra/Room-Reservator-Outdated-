package de.nvw_servers.Controller;
import java.util.Scanner;



public class ConsoleListener extends Thread {
	/**
	 * Scanner für die Konsoleneingaben
	 */
	private Scanner consoleIn;
	private CommandListener cl;
	
	/**
	 * Erzeugt einen CommandListener
	 * 
	 */
	public ConsoleListener(SettingManager sm) {
		consoleIn = new Scanner(System.in); // Erzeugt einen neuen Scanner, der die Eingaben scannt
		System.out.println("Bitte log dich bei der IRB Raumreservierung ein: /login <Nutzername> <Kennwort>");
		cl = new CommandListener(sm);
	}
	
	public ConsoleListener(ReservationManager rm,SettingManager sm) {
		System.out.println("Du hast dich erfolgreich in die IRB Lernraumreservierung eingeloggt!");
		consoleIn = new Scanner(System.in); // Erzeugt einen neuen Scanner, der die Eingaben scannt
		cl = new CommandListener(rm,sm);
	}
	
	/*private void listRooms() {
		HashMap<String,String> rooms = rc.getRooms();
		System.out.println("Tag    |Raum");
		System.out.println("=======|=============");
		for(String key : rooms.keySet()) {
			System.out.print(key);
			for(int i = key.length()+1 ; i<=7;i++)
				System.out.print(" ");
			System.out.print("|");
			System.out.println(rooms.get(key).replace("\\", ""));
		}
		System.out.println("rand   |Erster zufällig freier Raum");
	}*/

	
	/**
	 * Überwacht die Konsole und reagiert auf die definierten Kommandos
	 */
	public void listenCommands() {
		
		while(true) {
			System.out.print(">> ");
			String inputText = consoleIn.nextLine();
			System.out.print(cl.submitCommand(inputText));
			/*System.out.print(">> "); // printet zwei Pfeile an jedem Zeilenanfang
			String inputText = consoleIn.nextLine(); // Liest die eingegebene Zeile ein
			
			if(inputText.trim().length() == 0) { //Überprüft, ob etwas eingegeben wurde
				continue;
			}
			
			String[] inputWords = inputText.trim().split(" ");  // Jedes Wort wird an einen eigenen Arrayindex geschrieben
			boolean open;
			LocalDateTime from;
			LocalDateTime to;
			
			switch(inputWords[0].toLowerCase()) { //toLowerCase sorgt dafür, dass die Groß und Kleinschreibung der Befehle unrelevant ist
			case "/login": // Der Nutzer hat /login eingegeben
				if(loggedin) {
	                	System.out.println("Du bist bereits eingeloggt.");
	                	break;
                }
				
                if(inputWords.length != 3) {
                	System.out.println("Bitte /login <Nutzername> <Kennwort> verwenden");
                	break;
                }
         
                try {
					rc = new ReservationManager(inputWords[1], inputWords[2]);
					loggedin = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/reservate":  
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length < 8) {
					System.out.println("Geb bitte /reservate <day> <month> <from> <to> <room> <open> <comment> ein!");
					break;
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
						System.out.println("Reservierung am Wochenende nicht möglich!");
						break;
					}
				}
				catch(NumberFormatException e) {
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zum Datum konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[5]) && !inputWords[5].equals("rand")) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				if(!inputWords[6].equals("true") && !inputWords[6].equals("false") ) {
					System.out.println("<open> muss true oder false sein!");
					break;
				}
				try {
					if(!inputWords[5].equals("rand"))
						rc.addOwnReservation(from, to, inputWords[5], inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+7),open);
					else
						rc.addOwnReservation(from, to, inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+7),open);
					
					System.out.println("Raum wurde erfolgreich am "+from+" reserviert!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/reservatea":  
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length < 9) {
					System.out.println("Geb bitte /reservateA <day> <month> <year> <from> <to> <room> <open> <comment> ein!");
					break;
				}
				try {
					from = LocalDateTime.of(Integer.parseInt(inputWords[3]), Integer.parseInt(inputWords[2]), Integer.parseInt(inputWords[1]), Integer.parseInt(inputWords[4]), 0);
					to = LocalDateTime.of(Integer.parseInt(inputWords[3]), Integer.parseInt(inputWords[2]), Integer.parseInt(inputWords[1]), Integer.parseInt(inputWords[5]),0);
					open = Boolean.parseBoolean(inputWords[7]);
				}
				catch(NumberFormatException e) {
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zum Datum konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[6]) && !inputWords[6].equals("rand")) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				if(!inputWords[7].equals("true") && !inputWords[7].equals("false") ) {
					System.out.println("<open> muss true oder false sein!");
					break;
				}
				try {
					if(!inputWords[6].equals("rand"))
						rc.addOwnReservation(from, to, inputWords[6], inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+ inputWords[7].length()+8),open);
					else
						rc.addOwnReservation(from, to, inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+ inputWords[5].length()+ inputWords[6].length()+ inputWords[7].length()+8),open);
					
					System.out.println("Raum wurde erfolgreich am "+from+" reserviert!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/resnow":
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length < 6) {
					System.out.println("Geb bitte /resnow <from> <to> <room> <open> <comment> ein!");
					break;
				}
				try {
					from = LocalDateTime.now().withHour(Integer.parseInt(inputWords[1])).withMinute(0);
					to = LocalDateTime.now().withHour(Integer.parseInt(inputWords[2])).withMinute(0);
					open = Boolean.parseBoolean(inputWords[4]);
				}
				catch(NumberFormatException e) {
					System.out.println("Die Uhrzeiten müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zum Datum konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[3]) && !inputWords[3].equals("rand")) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				if(!inputWords[4].equals("true") && !inputWords[4].equals("false") ) {
					System.out.println("<open> muss true oder false sein!");
					break;
				}
				try {
					if(!inputWords[3].equals("rand"))
						rc.addOwnReservation(from, to, inputWords[3], inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+5),open);
					else
						rc.addOwnReservation(from, to, inputText.trim().substring(inputWords[0].length() + inputWords[1].length()+ inputWords[2].length()+ inputWords[3].length()+ inputWords[4].length()+5),open);
					
					System.out.println("Raum wurde erfolgreich reserviert!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/remove":
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length  != 6) {
					System.out.println("Geb bitte /remove <day> <month> <from> <to> <room> ein!");
					break;
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
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zum Datum konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[5])) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				try {
					rc.removeOwnReservation(from, to, inputWords[5]);
					System.out.println("Die Raumreservierung wurde erfolgreich gelöscht!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
				
			case "/remnow":
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length  != 4) {
					System.out.println("Geb bitte /remnow <from> <to> <room> ein!");
					break;
				}
				
				try {
					from = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[1]));
					to = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[2]));
				}
				catch(NumberFormatException e) {
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zur Uhrzeit konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[3])) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				try {
					rc.removeOwnReservation(from, to, inputWords[3]);
					System.out.println("Die Raumreservierung wurde erfolgreich gelöscht!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
				
				
			case "/wipe":
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length  != 6) {
					System.out.println("Geb bitte /wipe <day> <month> <from> <to> <room> ein!");
					break;
				}
				
				try {
					int hour = Integer.parseInt(inputWords[3]);
					int day = Integer.parseInt(inputWords[1]);
					int month = Integer.parseInt(inputWords[2]);
					int year = month < LocalDate.now().getMonthValue() ? LocalDate.now().getYear()+1 : LocalDate.now().getYear();
					from = LocalDateTime.of(year, month, day, hour, 0);
					hour = Integer.parseInt(inputWords[4]);
					to = LocalDateTime.of(year, month, day, hour, 0);
				}
				catch(NumberFormatException e) {
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zum Datum konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[5])) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				try {
					rc.removeForeignReservation(from, to, inputWords[5]);
					System.out.println("Die Raumreservierung wurde erfolgreich gewiped!");
				} catch (AuthentificationFailedException | IllegalTimeException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/wipenow":
				if(!loggedin) {
					System.out.println("Du musst eingeloggt sein um diese Aktion auszuführen!");
					break;
				}
				if(inputWords.length  != 4) {
					System.out.println("Geb bitte /wipenow <from> <to> <room> ein!");
					break;
				}
				
				try {
					from = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[1]));
					to = LocalDateTime.now().withMinute(0).withHour(Integer.parseInt(inputWords[2]));
				}
				catch(NumberFormatException e) {
					System.out.println("Das Datum und die Uhrzeit müssen gültige Zahlen sein!");
					break;
				}
				catch(DateTimeException e) {
					System.out.println("Die Eingabe kann nicht zur Uhrzeit konvertiert werden!");
					break;
				}
				if(!rc.existsRoom(inputWords[3])) {
					System.out.println("Der angegebene Raumtag existiert nicht. Für eine Auflistung tippe /rooms");
					break;
				}
				try {
					rc.removeForeignReservation(from, to, inputWords[3]);
					System.out.println("Die Raumreservierung wurde erfolgreich gewiped!");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "/showo": // Der Nutzer hat /quit eingegeben
				for(Reservation r : rc.getPlanedReservations()) {
					System.out.println(r);
					System.out.println("_______________________________________");
				}
				break;
			case "/shows": // Der Nutzer hat /quit eingegeben
				for(Reservation r : sm.getPlaned()) {
					System.out.println(r);
					System.out.println("_______________________________________");
				}
				break;	
			case "/rooms":
				if(loggedin) {
					listRooms();
					break;
				}
			default: // Der Nutzer hat einen unbekannten Befehl eingegeben
				if(loggedin)
					System.out.println("Der eingegebene Befehl ist nicht bekannt. Hier eine Liste aller Befehle:");	
			case "/help": // Der Nutzer hat /help eingegeben oder einen Unbekannten Befehl eingegeben  (Liste anpassen)
				if(loggedin) {
					System.out.println("Kommandos                                                            |Nutzen");
					System.out.println("=====================================================================|====================================");
					System.out.println("/help                                                                |Zeigt diese Nachricht an");
					System.out.println("/showo                                                               |Zeigt die einmalig zukünftig geplanten Termine.");
					System.out.println("/shows                                                               |Zeigt die regelmäßig zukünftig geplanten Termine.");
					System.out.println("/reservate <day> <month> <from> <to> <room> <open> <comment>         |Reserviert am Datum einen Raum im gegebenen Zeitfenster.");
					System.out.println("/reservateA <day> <month> <year> <from> <to> <room> <open> <comment> |Wie /reservate nur ohne Einschränkungen.");
					System.out.println("/resnow <from> <to> <room> <open> <comment>                          |Reserviert heute einen Raum im gegebenen Zeitfenster.");
					System.out.println("/remove <day> <month> <from> <to> <room>                             |Gibt eigene angegebene Reservierung wieder frei.");
					System.out.println("/remnow <from> <to> <room>                                           |Gibt eigene heutige angegebene Reservierung wieder frei.");
					System.out.println("/wipe <day> <month> <from> <to> <room>                               |Nutzt einen Exploit um beliebige Reservierungen zu löschen.");
					System.out.println("/wipenow <from> <to> <room>                                          |Nutzt einen Exploit um beliebige Reservierungen heute zu löschen.");
					System.out.println("/rooms                                                               |Listet alle Räume auf");
					break;
				}
				else
					System.out.println("Bitte log dich bei der IRB Raumreservierung ein: /login <Nutzername> <Kennwort>");
			}*/
		}
	}
	public CommandListener getCommandListener() {
		return cl;
	}
}
