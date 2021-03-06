package com.scmu.ws.rest.scmuService;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;




@Path("/homeSecurity")
public class HomeSecuritySystem {

	final static  String ARDUINO_INET_ADDR = "192.168.1.6";
	final int SERVER_PORT = 8000;
	

	
	//Diretoria de ficheiros
	 String basePath = "./storage";
	 String residentsFilePath = "./storage/houseResidents";
	 String authenticPassagesFilePath = "./storage/authenticatedPassages";
	 String nonAuthenticatedPassagesPath = "./storage/nonAuthenticatedPassages";
	 File baseFile;
	 File residentsFile;
	 static File authenticPassages;
	 static File nonAuthenticPassages;
	 static int actualPassageState;
	//tag para efectuar a authenticação
	 static String rfidTagInstance;
	// Key : String with smartphone credentials concatenated with a magnetic card tag
	// Value : String with the resident Name
	
	 static HashMap<String,String>residentsColl;
	 static HashMap<String,String>residentsAtHome;
	// Key : String with the date
	// Value : Boolean with false as Exit and True as Entrance
	 static HashMap<String,String>authenticatedEntrances;
	 static HashMap<String,String>nonAuthenticatedEntrances;
	 ServerSocket serverSocket;
	 URI baseUri;
	
	int starting = initResource();
	private static boolean stopThread;
	/* {
	 * name :
	 * 
	 * arrive at: <hora>}*/
	/* departure at: <hora>*/
	
	@GET
	@Path("/residents/List")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListResidents(){
//		JSONObject teste = new JSONObject();
//	
//		teste.put("name", "teste");
//		teste.put("email","teste@teste.com" );
//		teste.put("number", "12345678");
//		teste.put("rfid", "testeR");
//		teste.put("data", "departure at 12:50:00");
		JSONArray jsonA = new JSONArray();
	//	jsonA.put(teste);
		if(residentsColl==null){
			return Response.status(400).build();
		}
		else if(residentsColl.isEmpty()){
			String empty = "Server does not contain any residents";
					return Response.ok().entity(jsonA).build();
		}
		else{
			jsonA = new JSONArray();
			
			Set<String> keys = residentsColl.keySet();
			for(String key:keys){
				JSONObject obj = new JSONObject();
				String content = residentsColl.get(key);
		
				obj.put("name", content.split("_")[0]);
				obj.put("email", content.split("_")[1]);
				obj.put("number", content.split("_")[2]);
				obj.put("rfid", content.split("_")[3]);
				jsonA.put(obj);
			//	obj.put("date", content.split("_")[4]);
			}
			return Response.ok(jsonA).build();
			}
	}
	
	@GET
	@Path("/residents/home")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListAtHome(){
		JSONObject teste = new JSONObject();
		teste.put("name", "teste");
		teste.put("email","teste@teste.com" );
		teste.put("number", "12345678");
		teste.put("rfid", "testeR");
		teste.put("data", "departure at 12:50:00");
		JSONArray jsonA = new JSONArray();
		jsonA.put(teste);
		if(residentsAtHome==null){
			
			return Response.status(400).build();
		}
		else if(residentsAtHome.isEmpty()){
			String empty = "Server does not contain any residents or nobody is at home";
					return Response.ok().entity(empty).build();
		}
		else{
			jsonA = new JSONArray();
			Set<String> keys = residentsAtHome.keySet();
			for(String key:keys){
				JSONObject obj = new JSONObject();
				
				String content = residentsAtHome.get(key);
		
				obj.put("name", content.split("_")[0]);
				obj.put("email", content.split("_")[1]);
				obj.put("number", content.split("_")[2]);
				obj.put("rfid", content.split("_")[3]);
				jsonA.put(obj);
			//	obj.put("date", content.split("_")[4]);
			}
		return Response.ok(jsonA).build();
		}
	}
	@GET
	@Path("/residents/history/valid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListValidPassages(){
		if(authenticatedEntrances==null){
			return Response.status(400).build();
		}
		else if(authenticatedEntrances.isEmpty()){
			String empty = "Server does not contain any authenticated entrances";
					return Response.ok().entity(empty).build();
		}
		else
		System.out.println(new JSONObject(authenticatedEntrances));
		return Response.ok(authenticatedEntrances.keySet().toArray()).build();
	}
	@GET
	@Path("/residents/history/invalid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getlistNonValidPassages(){
		
		if(nonAuthenticatedEntrances==null){
			return Response.status(400).build();
		}
		
		else if(nonAuthenticatedEntrances.isEmpty()){
			String empty = "Server does not contain any non-authenticated entrances";
					return Response.ok().entity(empty).build();
		}
		else
			
		return Response.ok(nonAuthenticatedEntrances.keySet().toArray()).build();
	}
	@POST
	@Path("/residents/new")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response addMember(Resident newResident)
	{ 
		try{
		System.out.println("Adding user with tag : "+newResident.getTag());
		String content = newResident.getName()+"_"+newResident.getEmail()+"_"+newResident.getPhoneNr()+"_"+newResident.getTag();
		System.out.println(content);
		residentsColl.put(newResident.getTag(), content);
		System.out.println(residentsFile.getPath()+"/"+newResident.getTag());
		File newResidentF = new File(residentsFile.getPath()+"/"+newResident.getTag());
		System.out.println(newResidentF.toPath());
		Files.write(newResidentF.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
		FileOutputStream fos;
		fos = new FileOutputStream(newResident.getTag());
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(content.getBytes());
		
		
		
		System.out.println(residentsColl.size());
		bos.close();
		return Response.ok("200 OK").build();
		}catch(Exception e){
			System.out.println("Unable to add member.");
			return Response.status(400).build();
		}
	}
	/////////Commands////////
	@POST
	@Path("/residents/settings")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response setSettings(Settings newSettings)
	{
		try{
			System.out.println("Changing settings");
		//Resident r = new Resident(name, email, phoneNr, tag);
		Boolean silentMode = newSettings.getSilentMode();
		Boolean alarmMode = newSettings.getAlarmMode();
		String authenticate = newSettings.getAuthTag();
		if(silentMode){
			return toggleSilenceMode();
		}
		if(alarmMode){
			return firePushButton();
		}
		if(authenticate!=null){
			return authenticate(authenticate);
		}
		else
		{
			return Response.ok("Invalid Settings").build();}
		}
		catch(Exception e){
			return Response.status(400).build();
		}
	}
	
	//SilenceMode
	@GET
	@Path("/silence")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response toggleSilenceMode()
	{
		System.out.println("toggle silence mode...");
		String urlToSend = "http://"+ARDUINO_INET_ADDR+"/arduino/digital/"+2+"/1";
		URL u;
		try {
			u = new URL(urlToSend);
			InputStream is = u.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    is));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
				}
				return Response.ok("200 OK").build();
		} catch (Exception e) {
		
			e.printStackTrace();
			return Response.status(400).build();
		}
	}
	//Disparar alarme
	@GET
	@Path("/pushbutton")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response firePushButton()
	{
		System.out.println("pushbutton");
		String urlToSend = "http://"+ARDUINO_INET_ADDR+"/arduino/digital/"+1+"/1";
		URL u;
		try {
			u = new URL(urlToSend);
			InputStream is = u.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    is));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
				}
				return Response.ok("200 OK "+inputLine).build();
		} catch (Exception e) {
		
			e.printStackTrace();
			return Response.status(400).build();
		}			
	}
	
	
	//Disparar alarme
	@GET
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticate(String rfidTag)
	{
		System.out.println("Authenticating");
		String urlToSend = "http://"+ARDUINO_INET_ADDR+"/arduino/digital/"+0+"/1";
		URL u;
		try {
			u = new URL(urlToSend);
			InputStream is = u.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    is));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
					//parsing rfid
					
					
				}
				rfidTagInstance = rfidTag;
				return Response.ok("200 OK").build();
		} catch (Exception e) {
		
			e.printStackTrace();
			return Response.status(400).build();
		}			
	}
	@GET
	@Path("/broadcast")
	public static Response broadcastAlert() {
		return Response.ok("AlertBroadcast").build();
		
		
		
	}
	private int initResource() {
		System.out.println("Initiating resource");
		// TODO Auto-generated method stub
		
		
		//Arduino communication parameters
		 //Nome, eMail, nº telefone, queue fifo com as chegadas e partidas,
		 //ordenadas com os utilizadores por ordem de chegada
		residentsColl = new HashMap<String,String>();
		//passagesColl = new HashMap<String,Passage>();
		
		residentsAtHome = new HashMap<String,String>();
		
		authenticatedEntrances = new HashMap<String,String>();
		
		nonAuthenticatedEntrances = new HashMap<String,String>();
		 //Verifica se a diretoria de ficheiros existe, se não existir cria
		 baseFile = new File(basePath);
		 residentsFile = new File(residentsFilePath);
		 authenticPassages = new File(authenticPassagesFilePath);
		 nonAuthenticPassages = new File(nonAuthenticatedPassagesPath);
			
		 if(!baseFile.isDirectory()){
			 baseFile.mkdir();
		 }
		 else if(!residentsFile.isDirectory()){
			 residentsFile.mkdir();
		 }
		 else if(!authenticPassages.isDirectory()){
			 authenticPassages.mkdir();
		 }
		 else if(!nonAuthenticPassages.isDirectory()){
			 nonAuthenticPassages.mkdir();
		 }
		 uploadResidents(residentsFile);
		 uploadAuthenticatedPassages(authenticPassages);
		 uploadNonAuthenticatedPassages(nonAuthenticPassages);
		 actualPassageState = authenticatedEntrances.size()
				 +nonAuthenticatedEntrances.size();

		 //Connecção com arduino e cliente
		 killListener();
		  (new Thread(new ServerHandler())).start();
		return 0;
	}
	/*Kill the arduino listener before starting a new one when there is a call*/
	private void killListener() {
		// TODO Auto-generated method stub
		stopThread = true;
		
	}
	private static void uploadResidents(File residentsFile) {
		
		File[] residents = residentsFile.listFiles();
		System.out.println(residents+" residentsF");
		for(File resident:residents){
			
			if(resident.isDirectory()){
				
				try {
					RandomAccessFile raf = new RandomAccessFile(resident.getPath(),"r");
					byte[] bArray = new byte[(int) raf.length()];
					raf.read(bArray);
					String rawData = new String(bArray);
					String key = rawData.split("\\s")[0];
					String content = rawData.split("\\s")[1]; 
					residentsColl.put(resident.getName(), rawData);
					
				} catch (IOException e) {
				
					e.printStackTrace();
				}
			}
			
		}
		System.out.println(residentsColl.size());
		System.out.println("Finished residents");
	}
	private static void uploadAuthenticatedPassages(File authenticatedPassagesFile){
		File[] passages = authenticatedPassagesFile.listFiles();
		System.out.println(passages);
		for(File passage:passages){
			if(passage.isDirectory()){
				
				try {
					RandomAccessFile raf = new RandomAccessFile(passage.getPath(),"r");
					byte[] bArray = new byte[(int) raf.length()];
					raf.read(bArray);
					String rawData = new String(bArray);
		
					authenticatedEntrances.put(passage.getName(),rawData);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			
		}
		System.out.println("Stored : "+authenticatedEntrances.size());
		System.out.println("Finished authenticated entrances");
	}
	private static void uploadNonAuthenticatedPassages(File nonAuthenticatedPassagesFile){
		try{
		File[] passages = nonAuthenticatedPassagesFile.listFiles();
		System.out.println(passages);
		for(File passage:passages){
		
			if(!passage.isDirectory()){
				
				try {
					RandomAccessFile raf = new RandomAccessFile(passage.getPath(),"r");
					byte[] bArray = new byte[(int) raf.length()];
					raf.read(bArray);
					String rawData = new String(bArray);
					System.out.println(rawData);
				
					nonAuthenticatedEntrances.put(passage.getName(),rawData);
				} catch (IOException e) {
				
					e.printStackTrace();
				}
			}
		}
		System.out.println("Stored : "+nonAuthenticatedEntrances.size());
		System.out.println("Finished  non authenticated entrances");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Unable to read non authenticated entrance");
		}
		}
	
	/*Process data from arduino*/
	private static void processData(String message) {
		try {
		System.out.println(message);
		String[]passageData = message.split("");
		Calendar current = Calendar.getInstance();
		String date = String.valueOf(current.getTime().getYear())
						+"_"+String.valueOf(current.getTime().getMonth())
						+"_"+String.valueOf(current.getTime().getDay())
						+"_"+String.valueOf(current.getTime().getHours()
						+"_"+String.valueOf(current.getTime().getMinutes())
						+"_"+String.valueOf(current.getTime().getSeconds()));
		message.replace("-", "na");
		System.out.println(date);
		String keyTag = "";
		String messageS = passageData[0]+passageData[1]+passageData[2];
		for(int i = 3;i<passageData.length;i++){
			keyTag=keyTag+passageData[i];
		}
		System.out.println(keyTag);
		System.out.println(rfidTagInstance);
		switch(messageS)
		{
		case "NEA":
				
				String content = keyTag+rfidTagInstance+"_"+true+"_"+true;
				
				authenticatedEntrances.put(date,content);
				residentsAtHome.put(keyTag+rfidTagInstance, residentsColl.get(keyTag+rfidTagInstance));
				File newEntrance = new File(authenticPassages.getAbsolutePath()+"/"+date+message);
				Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
				FileOutputStream fos;
				fos = new FileOutputStream(date);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(content.getBytes());
			break;
		case "NEF":
				
				content = keyTag+"_"+true+"_"+false;
				nonAuthenticatedEntrances.put(date,content);
				newEntrance = new File(nonAuthenticPassages.getAbsolutePath()+"/"+date+message);
				Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
				
				fos = new FileOutputStream(date);
				bos = new BufferedOutputStream(fos);
				bos.write(content.getBytes());
			break;
		case "NSA":
				
				content = keyTag+rfidTagInstance+"_"+true+"_"+true; 
				authenticatedEntrances.put(date,content);
				residentsAtHome.remove(keyTag+rfidTagInstance);
				newEntrance = new File(authenticPassages.getAbsolutePath()+"/"+date+message);
				Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
				
				fos = new FileOutputStream(date);
				bos = new BufferedOutputStream(fos);
				bos.write(content.getBytes());
			break;
		case "NSF":
				
				content = keyTag+"_"+true+"_"+false;
				nonAuthenticatedEntrances.put(date,content);
				 newEntrance = new File(nonAuthenticPassages.getAbsolutePath()+"/"+date+message);
				 Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
				fos = new FileOutputStream(date);
				bos = new BufferedOutputStream(fos);
				bos.write(content.getBytes());
		break;
		case "P--":
			broadcastAlert();
		}
		System.out.println("New entrance added");
		rfidTagInstance = "";
		//Saves in file system
		
		//Broadcast changes to android clients
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////
	/*Handles comunication with arduino*/
	public static class ServerHandler implements Runnable{
		
		public void run() {
			stopThread = false;
			System.out.println("Arduino listener started!");
			while(!stopThread){
				try {
					//Listen to entrances and exits
						String urlToSend = "http://"+ARDUINO_INET_ADDR+"/arduino/digital/"+4+"/1";
						URL u = new URL(urlToSend);
						InputStream is = u.openStream();
						BufferedReader in = new BufferedReader(new InputStreamReader(
										is));
							String inputLine;
							TimeUnit.MILLISECONDS.sleep(250);
							while ((inputLine = in.readLine()) != null) {
								if(!inputLine.equals("")){
									try{
										String[] stateParser = inputLine.split("_");
										int index = Integer.parseInt(stateParser[0]);
										String passage = stateParser[1];
										if(index!=actualPassageState){
											actualPassageState = index;
											processData(passage);
										}
									}catch(ArrayIndexOutOfBoundsException e){
										System.out.println("");
									}
								}
							}
								
							in.close();
							TimeUnit.MILLISECONDS.sleep(250);
						
					} catch (Exception e) {
						System.out.println("Timeout");
						//e.printStackTrace();
					}
			}
		}
		
	}
}
