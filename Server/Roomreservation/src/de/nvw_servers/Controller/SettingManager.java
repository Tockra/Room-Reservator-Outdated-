package de.nvw_servers.Controller;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.nvw_servers.Model.Reservation;
import de.nvw_servers.exceptions.IllegalConfigException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;

public class SettingManager {
	private String username;
	private String password;
	private LocalTime planTime;
	private LinkedList<Reservation> planed = new LinkedList<>();
	
	public SettingManager() throws IllegalConfigException {
		try {
			File fXmlFile = new File("./settings.conf");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			NodeList username = doc.getElementsByTagName("username");
			NodeList password = doc.getElementsByTagName("password");
			
			NodeList planer = doc.getElementsByTagName("planer").getLength() != 1 ? null : doc.getElementsByTagName("planer").item(0).getChildNodes();
			
			if(planer == null || username.getLength() != 1 || password.getLength() != 1) {
				throw new IllegalConfigException("Illegales Config Format.");
			}
		
			this.username = username.item(0).getTextContent();
			this.password = password.item(0).getTextContent();
			
			for(int i = 0; i<planer.getLength();i++) {
				NodeList childs = planer.item(i).getChildNodes();
				switch(planer.item(i).getNodeName()) {
					case "time":
						if(childs.getLength() != 1 || childs.item(0).getTextContent().equals("")) {
							throw new IllegalConfigException("Illegales Config Format.");
						}
						String[] time = childs.item(0).getTextContent().split(":");
						if(time.length != 2 )
							throw new IllegalConfigException("Illegale <time> in der Config.");
						try {
							int hour = Integer.parseInt(time[0]);
							int minute = Integer.parseInt(time[1]);
							planTime = LocalTime.of(hour, minute);
						}
						catch(NumberFormatException e) {
							throw new IllegalConfigException("Illegale <time> in der Config.");
						}
						
						break;
					case "montag":
					case "dienstag":
					case "mittwoch":
					case "donnerstag":
					case "freitag":
						if(childs.getLength() != 11) {
							throw new IllegalConfigException("Illegales Config Format.");
						}
						String start = "";
						String end = "";
						String raum = "";
						String comment = "";
						String offen = "";
						for(int j = 0; j < childs.getLength();j++) {
							switch(childs.item(j).getNodeName()) {
								case "start":
									start = childs.item(j).getTextContent();
									break;
								case "ende" :
									end = childs.item(j).getTextContent();
									break;
								case "raum" :
									raum = childs.item(j).getTextContent();
									break;
								case "kommentar" :
									comment = childs.item(j).getTextContent();
									break;
								case "offen":
									offen = childs.item(j).getTextContent();
									break;
								case "#text":
									break;
								default:
									throw new IllegalConfigException("Illegales Config Format.");
							}
						}
						boolean open = offen.equals("ja");
						try {
							LocalDateTime start_time = LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth(),LocalDateTime.now().getDayOfMonth(),Integer.parseInt(start), 0);
							LocalDateTime end_time = LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth(),LocalDateTime.now().getDayOfMonth(),Integer.parseInt(end), 0);
							planed.add(new Reservation(start_time,end_time, planer.item(i).getNodeName(), raum, comment, open));
						}
						catch(NumberFormatException e) {
							throw new IllegalConfigException("Nur Zahlen bei den Uhrzeiten in der Config angeben.");
						}
						break;
					case "#text":
						break;
					default:
						throw new IllegalConfigException("Illegales Config Format.");
					
						
				}
				
				
			}
			
		} catch(FileNotFoundException e) {
			throw new IllegalConfigException("Config nicht vorhanden.: "+e.getMessage());
		}
		catch(SAXException e){
			throw new IllegalConfigException("Illegales Config Format.");
		}
		catch (ParserConfigurationException | IOException e) {
			throw new IllegalConfigException(e.getMessage() + " : Unknown Error");
		} 
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public LocalTime getReservationTime() {
		return planTime;
	}
	
	public LinkedList<Reservation> getPlaned() {
		return planed;
	}
}
