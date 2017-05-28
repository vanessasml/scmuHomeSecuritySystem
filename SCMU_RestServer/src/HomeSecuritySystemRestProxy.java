import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

/*	
 * 
 *  Arduino <=> Servidor <=> Android App
 *  Coisa parecida com o client do trabalho de SRSC 
 *  Necessario criar dois sockets:
 * 	um para comunicar com arduino e outro para comunicar com app android (Server socket)
 * 	Colecoes: tags cartoes e endereços ip do telemovel para autenticar
 * 	 - residentes da casa
 * 	 - enradas e saidas
 *  Como guardar os dados? Através de leitura e escrita de ficheiro.txt
 * Teste inicial: ver se o programa consegue receber na consola as mensagens do arduino
 * commands:
 * 
 * Autenticar usando a app:
 * http://192.168.1.6/arduino/digital/0/1 ou 0
 * enviar notificação (pushbutton)
 * http://192.168.1.6/arduino/digital/1/1 ou 0
 * ativar/desativar modo silencioso
 * http://192.168.1.6/arduino/digital/2/1 ou 0
 * adicionar tag cartao/porta chaves
 * http://192.168.1.6/arduino/digital/3/1 ou 0
 * "Comando persistente para ler as notificações de entrada e saida"
 * http://192.168.1.6/arduino/digital/4/1 ou 0 
 *  */


public class HomeSecuritySystemRestProxy {
	
		//Arduino communication parameters
		final static String ARDUINO_INET_ADDR = "192.168.1.6";
		
		//Server communication parameters
		final static String SERVER_INET_ADDR = "192.168.240.2";
		final static int SERVER_PORT = 8000;
		final static int PORT = 8080;
		
		//Diretoria de ficheiros
		static String basePath = "./storage";
		static String residentsFilePath = "./storage/houseResidents";
		static String authenticPassagesFilePath = "./storage/authenticatedPassages";
		static String nonAuthenticatedPassagesPath = "./storage/nonAuthenticatedPassages";
		static File baseFile;
		static File residentsFile;
		static File authenticPassages;
		static File nonAuthenticPassages;
		static int actualPassageState;
		// Key : String with smartphone credentials concatenated with a magnetic card tag
		// Value : String with the resident Name
		
		static HashMap<String,String>residentsColl;
		static HashMap<String,String>residentsAtHome;
		// Key : String with the date
		// Value : Boolean with false as Exit and True as Entrance
		static HashMap<String,String>authenticatedEntrances;
		static HashMap<String,String>nonAuthenticatedEntrances;
		
		//ServerSocket para receber do arduino
		//Socket 
		public static void main(String[] args) {
			
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
			 
			  (new Thread(new ServerHandler())).start();
			  (new Thread(new ClientHandler())).start();
	}
		public class HomeSystemResource 
		{
			
			
			@GET
			@Path("at/residents")
			public Response getListResidents(){
				if(residentsColl==null){
					return Response.status(400).build();
				}
				return Response.ok(residentsColl.keySet().toArray()).build();
			}
			@GET
			@Path("at/residents/at/home")
			public Response getListAtHome(){
				if(residentsColl==null){
					return Response.status(400).build();
				}
				return Response.ok(residentsAtHome.keySet().toArray()).build();
			}
			@GET
			@Path("at/residents/at/home/at/passages/at/valid")
			public Response getListValidPassages(){
				if(authenticatedEntrances==null){
					return Response.status(400).build();
				}
				return Response.ok(authenticatedEntrances.keySet().toArray()).build();
			}
			@GET
			@Path("at/residents/at/home/at/passages/at/invalid")
			public Response getlistNonValidPassages(){
				if(nonAuthenticatedEntrances==null){
					return Response.status(400).build();
				}
				return Response.ok(nonAuthenticatedEntrances.keySet().toArray()).build();
			}
			@POST
			@Path("/new/{tag}")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response addMember(@PathParam("tag")String name, String email,int phoneNr,String tag )
			{
				try{
				Resident r = new Resident(name, email, phoneNr, tag);
				String content = name+"_"+email+"_"+phoneNr+"_"+tag;
				File newEntrance = new File(authenticPassages.getAbsolutePath()+"/"+tag+"_"+email);
				Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
				FileOutputStream fos;
				fos = new FileOutputStream(tag);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(content.getBytes());
				
				return Response.ok().build();
				}catch(Exception e){
					return Response.status(400).build();
				}
			}
			/////////Commands////////
			//SilenceMode
			@POST
			@Path("/new/{name}")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response toggleSilenceMode()
			{
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
						return Response.ok().build();
				} catch (Exception e) {
				
					e.printStackTrace();
					return Response.status(400).build();
				}
			}
			//Disparar alarme
			@POST
			@Path("/new/")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response firePushButton()
			{
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
						return Response.ok().build();
				} catch (Exception e) {
				
					e.printStackTrace();
					return Response.status(400).build();
				}			
			}
			//Disparar alarme
			@POST
			@Path("/new/")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response authenticate()
			{
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
						return Response.ok().build();
				} catch (Exception e) {
				
					e.printStackTrace();
					return Response.status(400).build();
				}			
			}

		}
		public static class ClientHandler implements Runnable{
			ServerSocket serverSocket;
			URI baseUri;
			public ClientHandler(){
				baseUri = UriBuilder.fromUri("http://"+SERVER_INET_ADDR+"/").port(PORT).build();
				ResourceConfig config = new ResourceConfig();
				config.register(HomeSystemResource.class);
				try {
					 serverSocket = new ServerSocket(SERVER_PORT);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask(){

					@Override
					public void run() {
						try {
							Socket socket = serverSocket.accept();
							byte[]address=null;
							address = baseUri.toString().getBytes();
							DatagramPacket reply = new DatagramPacket(address,address.length,
									InetAddress.getByName(SERVER_INET_ADDR),SERVER_PORT);
							socket.getOutputStream().write(reply.getData());
						} catch (Exception e) {
						
							e.printStackTrace();
						}
					} 
					
				}, 0, 2000);
			}
			@Override
			public void run() {
				
				while(true){
					
					
				}
			}
		}
		/*Handles comunication with arduino*/
		public static class ServerHandler implements Runnable{
			
			@Override
			public void run() {
				
				
				while(true){
					try {
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
						
							e.printStackTrace();
						}
				}
			}
			
		}
		private static void uploadResidents(File residentsFile) {
		
			File[] residents = residentsFile.listFiles();
			for(File resident:residents){
				
				if(resident.isDirectory()){
					
					try {
						RandomAccessFile raf = new RandomAccessFile(resident.getAbsolutePath(),"r");
						byte[] bArray = new byte[(int) raf.length()];
						raf.read(bArray);
						String rawData = new String(bArray);
						String key = rawData.split("\\s")[0];
						String name = rawData.split("\\s")[1]; 
						residentsColl.put(key,name);
					} catch (IOException e) {
					
						e.printStackTrace();
					}
				}
				
			}
		}
		private static void uploadAuthenticatedPassages(File authenticatedPassagesFile){
			File[] passages = authenticatedPassagesFile.listFiles();
			for(File passage:passages){
				if(passage.isDirectory()){
					
					try {
						RandomAccessFile raf = new RandomAccessFile(passage.getAbsolutePath(),"r");
						byte[] bArray = new byte[(int) raf.length()];
						raf.read(bArray);
						String rawData = new String(bArray);
			
						authenticatedEntrances.put(passage.getName(),rawData);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
			}
			System.out.println("Stored:"+authenticatedEntrances.size());
		}
		private static void uploadNonAuthenticatedPassages(File nonAuthenticatedPassagesFile){
			try{
			File[] passages = nonAuthenticatedPassagesFile.listFiles();
			for(File passage:passages){
			
				if(!passage.isDirectory()){
					
					try {
						RandomAccessFile raf = new RandomAccessFile(passage.getAbsolutePath(),"r");
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
			System.out.println("Stored:"+nonAuthenticatedEntrances.size());
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
			String date = String.valueOf(current.getTime().getMonth())
							+"_"+String.valueOf(current.getTime().getDay())
							+"_"+String.valueOf(current.getTime().getDay())
							+"_"+String.valueOf(current.getTime().getHours()
							+"_"+String.valueOf(current.getTime().getMinutes())
							+"_"+String.valueOf(current.getTime().getSeconds()));
			message.replace("-", "na");
			System.out.println(date);
			String keyTag = "";
			for(int i = 3;i<passageData.length;i++){
				keyTag=keyTag+passageData[i];
			}
			System.out.println(keyTag);
			
			switch(message)
			{
			case "NEA-":
					
					String content = keyTag+true+true;
					
					authenticatedEntrances.put(date,content);
					File newEntrance = new File(authenticPassages.getAbsolutePath()+"/"+date+message);
					Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
					FileOutputStream fos;
					fos = new FileOutputStream(date);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bos.write(content.getBytes());
				break;
			case "NEF-":
					
					content = keyTag+true+false;
					nonAuthenticatedEntrances.put(date,content);
					newEntrance = new File(nonAuthenticPassages.getAbsolutePath()+"/"+date+message);
					Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
					
					fos = new FileOutputStream(date);
					bos = new BufferedOutputStream(fos);
					bos.write(content.getBytes());
				break;
			case "NSA-":
					
					content = keyTag+false+true;
					authenticatedEntrances.put(date,content);
					newEntrance = new File(authenticPassages.getAbsolutePath()+"/"+date+message);
					Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
					
					fos = new FileOutputStream(date);
					bos = new BufferedOutputStream(fos);
					bos.write(content.getBytes());
				break;
			case "NSF-":
					
					content = keyTag+false+false;
					nonAuthenticatedEntrances.put(date,content);
					 newEntrance = new File(nonAuthenticPassages.getAbsolutePath()+"/"+date+message);
					 Files.write(newEntrance.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
						 
						fos = new FileOutputStream(date);
						 bos = new BufferedOutputStream(fos);
						bos.write(content.getBytes());
				break;
			case "P----":
				broadcastAlert();
			}
			System.out.println("New entrance added");
			
			//Saves in file system
			
			//Broadcast changes to android clients
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		private static void broadcastAlert() {
		
			
		}
}
