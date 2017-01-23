package de.nvw_servers.Controller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.nvw_servers.Model.Reservation;
import de.nvw_servers.exceptions.AuthentificationFailedException;
import de.nvw_servers.exceptions.IllegalTimeException;


public class ReservationManager {
	private final HashMap<String, String> rooms = new HashMap<>();
	private final HashMap<String, String> roomsRegex = new HashMap<>();
	private final HashMap<String, String> roomsURL = new HashMap<>();
	
	private final LinkedList<String> rooms_priority = new LinkedList<>();
	private LinkedList<Reservation> planed = new LinkedList<>();
	
	private final String login_url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/login/dologin.sh";
	private final String refresh_url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id=";
	private final String reserve_url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/dores.sh?id=";
	private final String pre_reserve_url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id=";
	private final String user_agent= "Mozilla/5.0 (X11; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0";
	private String username;
	private String password;
	private String id;
	
	public ReservationManager(String username, String password) throws AuthentificationFailedException {
		fillDatas();
		
		this.username = username;
		this.password = password;
		login();
		
	}
	

	private void fillDatas() {
		rooms.put("2065", "OH12%2F2.065");
		rooms.put("3026", "OH12%2F3.026");
		rooms.put("3028", "OH12%2F3.028");
		rooms.put("3029", "OH12%2F3.029");
		rooms.put("3044", "OH12%2F3.044");
		rooms.put("4026", "OH12%2F4.026");
		rooms.put("4027", "OH12%2F4.027");
		rooms.put("4029", "OH12%2F4.029a");
		rooms.put("4033", "OH12%2F4.033a");
		rooms.put("4037", "OH12%2F4.037a");
		rooms.put("4042", "OH12%2F4.042");
		rooms.put("02", "OH14%2FU.02");
		rooms.put("03", "OH14%2FU.03");
		rooms.put("33", "OH14%2FE.33");
		rooms.put("37", "OH14%2FE.37");
		rooms.put("38", "OH14%2FE.38");
		
		roomsURL.put("2065", "OH12/2.065");
		roomsURL.put("3026", "OH12/3.026");
		roomsURL.put("3028", "OH12/3.028");
		roomsURL.put("3029", "OH12/3.029");
		roomsURL.put("3044", "OH12/3.044");
		roomsURL.put("4026", "OH12/4.026");
		roomsURL.put("4027", "OH12/4.027");
		roomsURL.put("4029", "OH12/4.029a");
		roomsURL.put("4033", "OH12/4.033a");
		roomsURL.put("4037", "OH12/4.037a");
		roomsURL.put("4042", "OH12/4.042");
		roomsURL.put("02", "OH14/U.02");
		roomsURL.put("03", "OH14/U.03");
		roomsURL.put("33", "OH14/E.33");
		roomsURL.put("37", "OH14/E.37");
		roomsURL.put("38", "OH14/E.38");
		
		roomsRegex.put("2065", "OH12/2\\.065");
		roomsRegex.put("3026", "OH12/3\\.026");
		roomsRegex.put("3028", "OH12/3\\.028");
		roomsRegex.put("3029", "OH12/3\\.029");
		roomsRegex.put("3044", "OH12/3\\.044");
		roomsRegex.put("4026", "OH12/4\\.026");
		roomsRegex.put("4027", "OH12/4\\.027");
		roomsRegex.put("4029", "OH12/4\\.029a");
		roomsRegex.put("4033", "OH12/4\\.033a");
		roomsRegex.put("4037", "OH12/4\\.037a");
		roomsRegex.put("4042", "OH12/4\\.042");
		roomsRegex.put("02", "OH14/U\\.02");
		roomsRegex.put("03", "OH14/U\\.03");
		roomsRegex.put("33", "OH14/E\\.33");
		roomsRegex.put("37", "OH14/E\\.37");
		roomsRegex.put("38", "OH14/E\\.38");
		
		rooms_priority.add("4037");
		rooms_priority.add("4033");
		rooms_priority.add("4029");
		rooms_priority.add("4042");
		rooms_priority.add("4027");
		rooms_priority.add("4026");
		rooms_priority.add("3029");
		rooms_priority.add("3028");
		rooms_priority.add("3044");
		rooms_priority.add("3026");
		rooms_priority.add("2065");
		rooms_priority.add("02");
		rooms_priority.add("03");
		rooms_priority.add("33");
		rooms_priority.add("37");
		rooms_priority.add("38");
		
       /* try{
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(".plans"));
                planed = (LinkedList<Reservation>)(is.readObject());
                is.close();
        }
        catch(FileNotFoundException e) {
        	try {
				if(new File(".plans").createNewFile())
					System.out.println("Save File für Plan wurde erstellt.");
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
        }*/
	}
	
	private void login() throws AuthentificationFailedException {
		String result;
		try {
			result = Jsoup.connect(login_url).userAgent(user_agent).data("account", username).data("passwort",password).post().select("meta").attr("content");
		} catch (IOException e) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		}
		
		String[] splited = result.split("id=");
		if(splited.length != 2||result.contains("fehler") || result.contains("error")) {
			writeLog(result.toString());
			System.out.println(result.toString());
			throw new AuthentificationFailedException("Problem with authentification. Response from Server: " + result);
		}
	
		this.id = splited[1];
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new IllegalArgumentException("Thread Problem");
		}
	}
	
	private void refreshID() throws AuthentificationFailedException{
		String result;
		try {
			result = Jsoup.connect(refresh_url+id).userAgent(user_agent).post().select("meta").attr("content");
		} catch (IOException e) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		}
		
		if(result.contains("fehler.html")) {
			System.out.println("["+LocalDateTime.now()+ "]: ID refreshed");
			System.out.print(">> "); // Zeile nur im Kontext dieses Programmes sinnvoll.
			login();
		}
		
	}
	
	public void removeAllOwnReservations(LocalDateTime from, LocalDateTime to, String room ) throws AuthentificationFailedException {
		try {
			String url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id="+id+"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear();
			
			Elements es = Jsoup.connect(url).userAgent(user_agent).post().getElementsByAttributeValueMatching("class", ".*eigenbelegt*");
			for(Element e:es) {
				System.out.println(e);
				int time = Integer.parseInt(e.toString().split(".*von=")[1].split(":00.*")[0]);
				int dauer = Integer.parseInt(e.toString().split(".*colspan=\"")[1].split("\">")[0]);
				if(from.getHour() <= time && to.getHour() >=(time+dauer)&&e.toString().contains("raum="+roomsURL.get(room))) {
					LocalDateTime delete_from = LocalDateTime.of(from.getYear(), from.getMonthValue(), from.getDayOfMonth(), time, 0);
					LocalDateTime delete_to = LocalDateTime.of(from.getYear(), from.getMonthValue(), from.getDayOfMonth(), time+dauer, 0);
					removeOwnReservation(delete_from, delete_to, room);
					System.out.println("Rem");
					Thread.sleep(1000+(new Random().nextInt(6000)));
				}
			}
		} catch (IOException e) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		} catch (InterruptedException e1) {
			throw new IllegalArgumentException("Thread Problem");
		}
	}

	@Deprecated
	private LinkedList<LocalDateTime> getPreSlots(LocalDateTime from,LocalDateTime to, String room) throws AuthentificationFailedException {
		if(from.getMinute() != 0 || to.getMinute() != 0) {
			throw new IllegalArgumentException("You have to use full hours.");
		}
		if(!from.toLocalDate().equals(to.toLocalDate())) 
			throw new IllegalArgumentException("The end of your reservation have to be at the same day, like the start.");
		
		if(rooms.get(room) == null) {
			throw new IllegalArgumentException("You chose a unknown room. We don't know room: "+room);
		}
		String url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id="+id+"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear();
		LinkedList<LocalDateTime> list = new LinkedList<>();
		try {
			Elements e = Jsoup.connect(url).userAgent(user_agent).post().getElementsByAttributeValueMatching("href", ".*"+roomsRegex.get(room)+".*");
			for(Element el:e) {
				if(!el.text().equals("frei")) {
					int start = el.toString().indexOf("von=");
					if(new Integer(el.toString().substring(start+4,start+6)) >=from.getHour() && new Integer(el.toString().substring(start+4,start+6)) <to.getHour()) {
						list.add(LocalDateTime.of(from.getYear(), from.getMonthValue(), from.getDayOfMonth(), new Integer(el.toString().substring(start+4,start+6))-1, 0));
					}
				}
			}
		} catch (IOException e) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		}
		
		return list;
	}
	
	private String getResID(LocalDateTime from, String room) throws AuthentificationFailedException {
		String pre_url = pre_reserve_url + id +"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear()+"&raum="+roomsURL.get(room)+"&von="+from.getHour()+":00&";
		
		String res_id ="";
		try {
			Elements resID = Jsoup
					   .connect(pre_url)
					   .userAgent(user_agent)
					   .post().select("input");
			
			for(Element e : resID) {
				if(!e.getElementsByAttributeValue("name", "rid").isEmpty()) {
					String[] splitted = e.getElementsByAttributeValue("name", "rid").toString().split(".*value=\"");
					res_id = splitted[1].split("\".*")[0];
				}
			}
		} catch (IOException e1) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		}
		
		try {
			Thread.sleep(1000+(new Random().nextInt(4000)));
		} catch (InterruptedException e) {
			throw new IllegalArgumentException("Thread Problem");
		}
		
		return res_id;
	}
	
	public void addOwnReservation(LocalDateTime from, LocalDateTime to, String comment) throws IllegalTimeException,AuthentificationFailedException, IOException {
		addOwnReservation(from, to, comment,true);
	}
	
	public void addOwnReservation(LocalDateTime from, LocalDateTime to, String comment,boolean open) throws IllegalTimeException,AuthentificationFailedException, IOException {
		for(String room:rooms_priority) {
			try {
				addOwnReservation(from,to,room,comment,open);
				break;
			}
			catch(IllegalTimeException e) {
				if(room.equals(rooms_priority.getLast())) {
					throw new IllegalTimeException("There is no free room, in your chosen time.");
				}
			}
		}
	}
	
	public void addOwnReservation(LocalDateTime from, LocalDateTime to,String room, String comment) throws IllegalTimeException,AuthentificationFailedException, IOException {
		addOwnReservation(from,to,room,comment,true);
	}
	
	public void addOwnReservation(LocalDateTime from, LocalDateTime to,String room,String comment,boolean open) throws IllegalTimeException,AuthentificationFailedException, IOException {
		if(from.getMinute() != 0 || to.getMinute() != 0) {
			throw new IllegalArgumentException("You have to use full hours.");
		}
		if(!from.toLocalDate().equals(to.toLocalDate())) 
			throw new IllegalArgumentException("The end of your reservation have to be at the same day, like the start.");
		if(comment.equals("")) 
			throw new IllegalArgumentException("Please choose a comment for your Reservation.");
		
		if(rooms.get(room) == null) {
			throw new IllegalArgumentException("You chose a unknown room. We don't know room: "+room);
		}
		if(from.getHour() <8 || from.getHour() > 18 || to.getHour() < 9 || to.getHour() > 19) {
			throw new IllegalArgumentException("You chose an time, when you can't reservate rooms.");
		}
		
		/*Funktionalität entfernt, da die 9 Tage in der Zukunft nicht mehr konstant.*/
		/*if(ChronoUnit.DAYS.between(LocalDateTime.now(), from) > 9)  {
			planed.add(new Reservation(from, to, "", room, comment, open));
			serializePlan();
			System.out.println("Reservierung dem Plan hinzugefügt!");
			return;
		}	*/
		
		refreshID();
		
		
		
		LocalDateTime oldStart = from.plusDays(0);
		
		try {
			while(to.getHour() - from.getHour() > 3) {
				String response = Jsoup
						   .connect(reserve_url+id)
						   .userAgent(user_agent)
						   .referrer(pre_reserve_url + id +"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear()+"&raum="+roomsURL.get(room)+"&von="+from.getHour()+":00&")
						   .data("datum", (from.getDayOfMonth()<=9?"0":"") + from.getDayOfMonth()+"."+(from.getMonth().getValue()<=9?"0":"") + from.getMonth().getValue()+"."+from.getYear())
						   .data("raum", rooms.get(room))
						   .data("von", (from.getHour()<=9?"0":"") + from.getHour()+"%3A00")
						   .data("rid",getResID(from, room))
						   .data("comment", comment)
						   .data("bis", (from.getHour()+3<=9?"0":"") + (from.getHour()+3) + "%3A00%A0")
						   .data("mitlerner", open ? "yes":"no")
						   .data("action", "Reservieren")
						   .post().select("font").text().toString();
				if(!response.matches(".*Sie haben Raum.*am.*von.*Uhr bis.*Uhr reserviert.*")) {
					writeLog(response);
					throw new IllegalTimeException("Room "+room+" isn't free in your chosen time.");
				}

				from = from.plusHours(3);
			}
			String response = Jsoup
					   .connect(reserve_url+id)
					   .userAgent(user_agent)
					   .referrer(pre_reserve_url + id +"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear()+"&raum="+roomsURL.get(room)+"&von="+from.getHour()+":00&")
					   .data("datum", (from.getDayOfMonth()<=9?"0":"") + from.getDayOfMonth()+"."+(from.getMonth().getValue() <= 9? "0":"")+from.getMonth().getValue()+"."+(from.getMonth().getValue() <= 9? "0":"")+from.getMonth().getValue())
					   .data("raum", rooms.get(room))
					   .data("von", (from.getHour()<=9?"0":"")+from.getHour()+"%3A00")
					   .data("rid",getResID(from, room))
					   .data("comment", comment)
					   .data("bis", (to.getHour()<=9?"0":"")+to.getHour() + "%3A00%A0")
					   .data("mitlerner", open ? "yes":"no")
					   .data("action", "Reservieren")
					   .post().select("font").text().toString();
			if(!response.matches(".*Sie haben Raum.*am.*von.*Uhr bis.*Uhr reserviert.*")) {
				writeLog(response);
				removeAllOwnReservations(oldStart, to, room);
				throw new IllegalTimeException("Room "+room+" isn't free in your chosen time.");
		   }
			Thread.sleep(1000+(new Random().nextInt(1000)));
		} catch (IOException | InterruptedException e) {
			throw new AuthentificationFailedException("Problem with connection to URL: "+e.getMessage());
		}
	}

	public void removeOwnReservation(LocalDateTime from, LocalDateTime to, String room) throws AuthentificationFailedException {
		if(from.getMinute() != 0 || to.getMinute() != 0) {
			throw new IllegalArgumentException("You have to use full hours.");
		}
		if(!from.toLocalDate().equals(to.toLocalDate())) 
			throw new IllegalArgumentException("The end of your reservation have to be at the same day, like the start.");
		
		if(rooms.get(room) == null) {
			throw new IllegalArgumentException("You chose a unknown room. We don't know room: "+room);
		}
		
		/*Funktionalität entfernt, da die 9 Tage in der Zukunft nicht mehr konstant.*/
		/*if(from.getYear() > LocalDateTime.now().getYear() || from.getDayOfYear() > LocalDateTime.now().getDayOfYear()+9) {
			for(int i = 0; i < planed.size();i++) {
				if(planed.get(i).getStart().equals(from) && planed.get(i).getEnd().equals(to)) {
					planed.remove(i);
					System.out.println("Termin wurde aus dem Plan gelöscht!");
					return;
				}
			}
		}*/
		
		refreshID();
		
		try {
			while(to.getHour() - from.getHour() > 3) {
				Jsoup
				   .connect(reserve_url+id)
				   .referrer(pre_reserve_url + id +"&datum"+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear()+"&raum="+roomsURL.get(room)+"&von="+from.getHour()+":00&")
				   .userAgent(user_agent)
				   .data("datum", (from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonth().getValue()<=9?"0":"")+from.getMonth().getValue()+"."+from.getYear())
				   .data("raum", rooms.get(room))
				   .data("von", (from.getHour()<=9 ? "0":"")+from.getHour()+"%3A00")
				   .data("bis", ((3+from.getHour())<=9?"0":"")+(3+from.getHour()) + "%3A00%A0")
				   .data("rid",getResID(from, room))
				   .data("action", "Freigeben")
				   .post();
				from = from.plusHours(3);
			}
		
		
			Jsoup
			   .connect(reserve_url+id)
			   .userAgent(user_agent)
			   .referrer(pre_reserve_url + id +"&datum="+(from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonthValue()<=9?"0":"")+from.getMonthValue()+"."+from.getYear()+"&raum="+roomsURL.get(room)+"&von="+from.getHour()+":00&")
			   .data("datum", (from.getDayOfMonth()<=9?"0":"")+from.getDayOfMonth()+"."+(from.getMonth().getValue()<=9 ? "0":"")+from.getMonth().getValue()+"."+from.getYear())
			   .data("raum", rooms.get(room))
			   .data("von", (from.getHour()<=9?"0":"")+from.getHour()+"%3A00")
			   .data("bis", (to.getHour()<=9?"0":"")+to.getHour() + "%3A00%A0")
			   .data("rid",getResID(from, room))
			   .data("action", "Freigeben")
			   .post();
			
		} catch (IOException e) {
			throw new AuthentificationFailedException("Problem with connection to URL.");
		}
	}
	
	@Deprecated 
	public void removeForeignReservation(LocalDateTime from, LocalDateTime to, String room) throws IllegalTimeException, AuthentificationFailedException  {
		if(from.getMinute() != 0 || to.getMinute() != 0) {
			throw new IllegalArgumentException("You have to use full hours.");
		}
		if(!from.toLocalDate().equals(to.toLocalDate())) 
			throw new IllegalArgumentException("The end of your reservation have to be at the same day, like the start.");
		
		if(rooms.get(room) == null) {
			throw new IllegalArgumentException("You chose a unknown room. We don't know room: "+room);
		}
		refreshID();
		
		LinkedList<LocalDateTime> reservations = getPreSlots(from, to, room);
		for(LocalDateTime date:reservations) {
			try {
				addOwnReservation(date, date.plusHours(1), room, "DAP");
				Jsoup
				   .connect(reserve_url+id)
				   .userAgent(user_agent)
				   .data("datum", (date.getDayOfMonth()<=9?"0":"")+date.getDayOfMonth()+"."+(date.getMonth().getValue()<=9?"0":"")+date.getMonth().getValue()+"."+date.getYear())
				   .data("raum", rooms.get(room))
				   .data("von", (date.getHour()<=9?"0":"")+date.getHour() +"%3A30")
				   .data("bis", ((date.getHour()+1)<=9 ? "0":"")+(date.getHour()+1) + "%3A00%A0")
				   .data("action", "Freigeben")
				   .post();
				removeOwnReservation(date, date.plusHours(1), room);
			}
			catch(IllegalTimeException e) {
				throw new IllegalTimeException("There is no free Slot before your chosen slot!");
			}
			catch (IOException e) {
				throw new AuthentificationFailedException("Problem with connection to URL.");
			}
			
		}
	}
	
	public boolean existsRoom(String room) {
		return rooms.containsKey(room);
	}
	
	public HashMap<String,String> getRooms() {
		return roomsRegex;
	}
	
	public LinkedList<Reservation> getPlanedReservations() {
		return planed;
	}
	
	/* Diese Methode findet den letzten Tag an dem man reservieren kann, 
	 * also den neusten freigeschalteten. Dieser liegt mind. 9 Tage in der Zukunft.
	 * 
	 * Das Programm benötigt einen Neustart, falls das nextRes Datum von der IRB verschoben wird.
	 * 
	 * 
	 */
	public LocalDate findLastDayToReservate(LocalDate lastRes) throws AuthentificationFailedException,IOException,InterruptedException {
		LocalDate nextRes;
		if(lastRes != null) 
			nextRes = lastRes;
		else
			nextRes = LocalDate.now().plusDays(9);
		if(nextRes.getDayOfWeek().getValue() == 6) {
			nextRes = nextRes.plusDays(2);		
		}
		else if(nextRes.getDayOfWeek().getValue() == 7) {
			nextRes = nextRes.plusDays(1);
		}
		
		refreshID();
			
		String url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id="+id+"&datum="+(nextRes.getDayOfMonth()<=9?"0":"")+nextRes.getDayOfMonth()+"."+(nextRes.getMonthValue()<=9?"0":"")+nextRes.getMonthValue()+"."+nextRes.getYear();
		
		while(!Jsoup.connect(url).userAgent(user_agent).post().getElementsByAttributeValueMatching("title", ".*Reservieren von Raum.*").isEmpty()) { 
			if(nextRes.getDayOfWeek().getValue() == 5) {
				nextRes = nextRes.plusDays(3);
			}
			else
				nextRes = nextRes.plusDays(1);
			url = "https://irb.cs.tu-dortmund.de/fbi/raumadm/lernraum/ssl/kalender.sh?id="+id+"&datum="+(nextRes.getDayOfMonth()<=9?"0":"")+nextRes.getDayOfMonth()+"."+(nextRes.getMonthValue()<=9?"0":"")+nextRes.getMonthValue()+"."+nextRes.getYear();
			
			Thread.sleep(1813);
		}
		
		if(nextRes.getDayOfWeek().getValue() == 1) {
			nextRes = nextRes.minusDays(3);
		}
		else
			nextRes = nextRes.minusDays(1);
		
		return nextRes;
	}
	
	
	/*public void serializePlan() throws IOException {
		try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(".plans"));
            os.writeObject(planed);
            os.close();
	    }
		catch(FileNotFoundException e) {
				if(new File(".plans").createNewFile())
					System.out.println("Save File für Plan wurde erstellt.");
        }
	}*/

	public void writeLog(String log) {
		
		try {
			new File("response.log").createNewFile();
		} catch (IOException e) {
			
		}
		
       
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("response.log",true));
			out.append(LocalDateTime.now() + ": \n"+log+"\n______________________________________________________________________________\n");
		    out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			System.out.println("Fehler beim Schreiben des Logs.\n>> ");
		}
    
	    
		
	}
}
