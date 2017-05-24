import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
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
 *  */


public class HomeSecuritySystemRestProxy {
		final static String ARDUINO_INET_ADDR = "192.168.1.6";
		final static int PORT = 80;
		
		//Diretoria de ficheiros
		static String basePath = "./storage";
		static String residentsFilePath = "./storage/houseResidents";
		static String authenticPassagesFilePath = "./storage/authenticatedPassages";
		static String nonAuthenticatedPassages = "./storage/nonAuthenticatedPassages";
		static File baseFile;
		static File residentsFile;
		static File authenticPassages;
		static File nonAuthenticPassages;
		// Key : String with smartphone ip address concatenated with a magnetic card tag
		// Value : String with the resident Name
		
		static HashMap<String,String>residentsColl;
		// Key : String with the date
		// Value : Boolean with false as Exit and True as Entrance
		static HashMap<String,Boolean>authenticatedEntrances;
		static HashMap<String,Boolean>nonAuthenticatedEntrances;
		
		
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
				client = new Socket(ARDUINO_INET_ADDR, PORT);
				
			 DataInputStream dis = new DataInputStream(client.getInputStream());
			 System.out.println("Setup complete");
			 	while(true)
			 	{
			 		System.out.println(dis.readUTF());
			 	}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
		private static void uploadResidents(File residentsFile) {
			// TODO Auto-generated method stub
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
						// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		private static void receiveDataFromArduino(InputStream fromArduino){
			
			byte[]messageBuffer = new byte[1024];
			
				
					
				try {
					int bytesAvailable = fromArduino.available();
					if( bytesAvailable >0)
						System.out.println(fromArduino.read(messageBuffer));
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
							//filter data output from arduino
					//		processData(message,toClient);
					
					
				
				
			}
		
		/*Process data from arduino*/
		private static void processData(String message, OutputStream toClient) {
			String date = String.valueOf(Calendar.getInstance().getTime());
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
			// TODO Auto-generated method stub
			
		}
		//Writes commands to arduino
		//Valid commands : /V/{String ipAddress} 
		//				   /alarm/{String ipAddress} 
		//				   /silenceMode/{String ipAddress}
		private void sendOnDemandCommand(InputStream fromClient,OutputStream toArduino){
		
		}
		
		private void addResident(String credentials,String name){
			residentsColl.put(credentials, name);
		}
		
		private static URI getBaseURI() {
		    return UriBuilder.fromUri("http://"+ARDUINO_INET_ADDR).build();
		  }
}
