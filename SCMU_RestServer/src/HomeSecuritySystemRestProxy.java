import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;





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
 * "Comando persistente para 
 * 	ler as notificações de entrada e saida"
 * http://192.168.1.6/arduino/digital/4/1 ou 0 
 *  */


public class HomeSecuritySystemRestProxy {
		final static String ARDUINO_INET_ADDR = "192.168.1.6";
		final static int PORT = 8000;
		
		//Diretoria de ficheiros
		static String basePath = "./storage";
		static String residentsFilePath = "./storage/houseResidents";
		static String authenticPassagesFilePath = "./storage/authenticatedPassages";
		static String nonAuthenticatedPassages = "./storage/nonAuthenticatedPassages";
		static File baseFile;
		static File residentsFile;
		static File authenticPassages;
		static File nonAuthenticPassages;
		// Key : String with smartphone credentials concatenated with a magnetic card tag
		// Value : String with the resident Name
		
		static HashMap<String,String>residentsColl;
		// Key : String with the date
		// Value : Boolean with false as Exit and True as Entrance
		static HashMap<String,Boolean>authenticatedEntrances;
		static HashMap<String,Boolean>nonAuthenticatedEntrances;
		
		//ServerSocket para receber do arduino
		//Socket 
		public static void main(String[] args) {
			
			 InputStream fromClient,fromArduino;
			 OutputStream toClient,toArduino;
			 //Porta em que corre este proxy?
			 Socket client;
			residentsColl = new HashMap<String,String>();
			authenticatedEntrances = new HashMap<String,Boolean>();
			nonAuthenticatedEntrances = new HashMap<String,Boolean>();
			 //Verifica se a diretoria de ficheiros existe, se não existir cria
			 baseFile = new File(basePath);
			 residentsFile = new File(residentsFilePath);
			 authenticPassages = new File(authenticPassagesFilePath);
			 nonAuthenticPassages = new File(nonAuthenticatedPassages);
			 
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
			 
			 //Connecção com arduino e cliente
			try {
			//	client = new Socket(ARDUINO_INET_ADDR, PORT);URL url = new URL("http://192.168.1.6/");
				//URLConnection connection = url.openConnection();
		//		BufferedReader in = new BufferedReader(
			//			new InputStreamReader(connection.getInputStream()));
				
				client = new Socket(ARDUINO_INET_ADDR,PORT);
			 System.out.println("Setup complete");
			/* String inputLine;
			 	while((inputLine = in.readLine())!=null)
			 	{
			 		System.out.println(inputLine);
			 	}*/
			 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				 //System.out.println("Setup complete");
				 	while(true)
				 	{
				 		System.out.println(in.readLine());
				 	}
			} catch (UnknownHostException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			} 
	}
		private static void uploadResidents(File residentsFile) {
		
			File[] residents = residentsFile.listFiles();
			for(File resident:residents){
				List<String> list = Arrays.asList(resident.list());
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
				List<String> list = Arrays.asList(passage.list());
				if(passage.isDirectory()){
					
					try {
						RandomAccessFile raf = new RandomAccessFile(passage.getAbsolutePath(),"r");
						byte[] bArray = new byte[(int) raf.length()];
						raf.read(bArray);
						String rawData = new String(bArray);
						String key = rawData.split("\\s")[0];
						Boolean value = Boolean.valueOf(rawData.split("\\s")[1]); 
						authenticatedEntrances.put(key,value);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		}
		private static void uploadNonAuthenticatedPassages(File nonAuthenticatedPassagesFile){
			File[] passages = nonAuthenticatedPassagesFile.listFiles();
			for(File passage:passages){
				List<String> list = Arrays.asList(passage.list());
				if(passage.isDirectory()){
					
					try {
						RandomAccessFile raf = new RandomAccessFile(passage.getAbsolutePath(),"r");
						byte[] bArray = new byte[(int) raf.length()];
						raf.read(bArray);
						String rawData = new String(bArray);
						String key = rawData.split("\\s")[0];
						Boolean value = Boolean.valueOf(rawData.split("\\s")[1]); 
						authenticatedEntrances.put(key,value);
					} catch (IOException e) {
					
						e.printStackTrace();
					}
				}
				
			}
		}
		private static void sendAtHome(){
			
		}
		private static void receiveDataFromArduino(InputStream fromArduino){
			
			byte[]messageBuffer = new byte[1024];
			
				
					
				try {
					int bytesAvailable = fromArduino.available();
					if( bytesAvailable >0)
						System.out.println(fromArduino.read(messageBuffer));
					
				} catch (IOException e) {
				
					e.printStackTrace();
				}
					
							//filter data output from arduino
					//		processData(message,toClient);
					
					
				
				
			}
		
		/*Process data from arduino*/
		private static void processData(String message, OutputStream toClient) {
			
			String date = String.valueOf(Calendar.getInstance().getTime());
			//concatenacao credentials e date por _
			String key = "";
			switch(message)
			{
			case "NEA-":
					authenticatedEntrances.put(date,true);
				break;
			case "NEF-":
					nonAuthenticatedEntrances.put(date,true);
				break;
			case "NSA-":
					authenticatedEntrances.put(date,false);
				break;
			case "NSF-":
					nonAuthenticatedEntrances.put(date,false);
				break;
			case "P----":
				broadcastAlert();
			}
			
			//Saves in file system
			
			//Broadcast changes to android clients
			
		}
		private static void broadcastAlert() {
		
			
		}
		//Writes commands to arduino
		//
		private void sendOnDemandCommand(InputStream fromClient,OutputStream toArduino,int pinValue){
			String urlToSend = "http://"+ARDUINO_INET_ADDR+"/arduino/digital/"+pinValue+"/1";
			
		}
		
		private void addResident(String credentials,String name){
			residentsColl.put(credentials, name);
			String toStore = credentials+" "+name;
			File resident = new File(residentsFile.getAbsolutePath()+"/"+credentials);
			if(!residentsColl.containsKey(credentials)){
				if(!resident.exists())
				{
					try {
						Files.write(resident.toPath(), toStore.getBytes(), StandardOpenOption.CREATE_NEW);
					
						FileOutputStream fos = new FileOutputStream(resident.getAbsolutePath());
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						bos.write(toStore.getBytes());
						bos.close();
					} catch (IOException e) {
					
						e.printStackTrace();
					}
				}
			}
		}
		
		private static URI getBaseURI() {
		    return UriBuilder.fromUri("http://"+ARDUINO_INET_ADDR).build();
		  }
}
