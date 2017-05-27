#include <UnoWiFiDevEd.h>
#include <MFRC522.h>
#include<SPI.h>



/*TODO : 
  fazer counter para entradas e saidas para conseguir responder ao comando 4 vindo do servidor
    */
//PINS
const int pushbutton = 2;
const int buzzer = 6;
const int ledGreen = 7;
const int ledRed = 8;

//RFID pins 
#define SSPIN 10
#define RSTPIN 9

//Rightside entrance sensor
const int trigPin = 3;
const int echoPin = 5;
long duration;
int distance;

//Rightside exit sensor
const int echoPin2 = 4;
const int trigPin2 = 1;
long duration2;
int distance2;

//Device states
bool exited = false;
bool entrance = true;
bool deviceAtLeft = false;
bool authenticated = false;
String tag = "";
int inputState=0;
int tagCounter = 0;
int entrancesCounter , exitsCounter = 0;
//Device configurations
bool silencedMode = true;
int PRESENCERADIUS = 30;

//instances
//rfidSensor instance
MFRC522 rfidSensor(SSPIN,RSTPIN);


//rfid tags memory
String tagMatrix[12];

void setup() {
  //Initialize serial at 9600 baud rate
  Serial.begin(9600);
  Serial.println("Serial started");
  //Required for rfid sensor comunication with arduino
  SPI.begin();
  //Setup pins
  setupPins();

  //Check if Wifi shield is present or that arduino board has wifi
    Serial.println("Init WIFI");
    Wifi.begin();
    delay(3000);
    Serial.println("REST Server is up");
    Wifi.println("REST Server is up");
  
  //Sensors and actuators testing
  //two flashes of red and one of greed LEDS followed 
  //by a short buzzer sound emition at 2000Hz
  Serial.println("Entering setup");
  for(int i = 0; i<2;i++){
    digitalWrite(ledRed,HIGH);
    delay(500);
    digitalWrite(ledRed,LOW);
    delay(500);
  }
   digitalWrite(ledGreen, HIGH);
   delay(500);
   digitalWrite(ledGreen, LOW);
   delay(250);
   if(!silencedMode){
    tone(buzzer,1500);
    delay(500);
    noTone(buzzer);
   }
   rfidSensor.PCD_Init();
   Serial.println("RFID sensor intialized, can authenticate");
   setupTags();
  
   Serial.println("Setup finished"); 
   
   
}

void loop() 
{
  while(checkPresence());
  // Look for new cards
  if ( ! rfidSensor.PICC_IsNewCardPresent()) {
   
    return;
  }
  // Select one of the cards
  if ( ! rfidSensor.PICC_ReadCardSerial()) {

    return;
  }
  
  //Show UID on serial monitor
  Serial.print("UID tag :");
  String content= "";
  byte letter;
  //Conversao da tag para algo que se possa ler no serial
  for (byte i = 0; i < rfidSensor.uid.size; i++) {
     Serial.print(rfidSensor.uid.uidByte[i] < 0x10 ? " 0" : " ");
     Serial.print(rfidSensor.uid.uidByte[i], HEX);
     content.concat(String(rfidSensor.uid.uidByte[i] < 0x10 ? " 0" : " "));
     content.concat(String(rfidSensor.uid.uidByte[i], HEX));
  }
  Serial.println();
  Serial.print("Message : ");
  
  content.toUpperCase();
  //change here the UID of the card/cards that you want to give access
  if (checkTag(content)){ 
    Serial.println("Authorized access");
   // Wifi.println("Authorized access");
   tag = content;
    authenticated = true;
   
    digitalWrite(ledGreen,HIGH);
   
     inputState = digitalRead(pushbutton);
    // Serial.println("digital");
  // check if the pushbutton is pressed.
  // if it is, the buttonState is HIGH:
    if (inputState == HIGH) {     
    // turn LED on:  
    Serial.println("<<<<<<<<<<<<<SENDING ALERT>>>>>>>>>>>>>>");  
    Wifi.println("P----");
     Wifi.print(EOL); 
    digitalWrite(ledRed, HIGH);
    digitalWrite(ledGreen,HIGH);
    if(!silencedMode){
    tone(buzzer,1500); 
    }
  } 
  else if(inputState==LOW){
    // turn LED off:
    digitalWrite(ledRed, LOW); 
    noTone(buzzer);
  }
    delay(500);
     digitalWrite(ledGreen,LOW);
  }
 
 else{
    Serial.println(" Access denied");
 //   Wifi.println(" Access denied");
  
    digitalWrite(ledRed,HIGH);
    delay(500);
    digitalWrite(ledRed,LOW);
  }
} 

boolean checkTag(String content){
  boolean result = false;
  content.trim();
  Wifi.println(content);
  Wifi.println(content.length());
  for(int i = 0; i < tagCounter; i++){
    Wifi.print("STR: ");
    Wifi.println(tagMatrix[i]);
      if(content.equals(tagMatrix[i])){
        Wifi.println("found: ");
        Wifi.println(tagMatrix[i]);
        result = true;
        }
  }
  return result;
}

void setupTags(){
   tagMatrix[tagCounter] = "11 B0 F8 65";
   tagCounter++;
}

boolean checkPresence(){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration = pulseIn(echoPin, HIGH);
  // Calculating the distance : speed of sound = 340 m/s
  distance= duration*0.034/2;
  // Prints the distance on the Serial Monitor
  Serial.print("Distance: ");
  Serial.println(distance);



  digitalWrite(trigPin2, LOW);
  delayMicroseconds(2);
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin2, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration2 = pulseIn(echoPin2, HIGH);
  // Calculating the distance : speed of sound = 340 m/s
  distance2= duration2*0.034/2;
  // Prints the distance on the Serial Monitor
  Serial.print("Distance2: ");
  Serial.println(distance2);
  if((distance<=PRESENCERADIUS && distance!=0) || (distance2<=PRESENCERADIUS && distance2!=0)){
    if(!silencedMode){
    tone(buzzer,750);
    }
  if((distance<=PRESENCERADIUS&&distance2>PRESENCERADIUS)&&!entrance&&!deviceAtLeft){
      exited =true;
      Serial.println("Exit");
      if(authenticated){
        String toSend = "NSA"+tag;
       toSend.replace(" ","");
        Serial.println("Authenticated Exit!!!");
        Wifi.println(toSend);
       }
       else{
        Wifi.println("NSF-");
        }
         Wifi.print(EOL); 
      delay(250);
      noTone(buzzer);
     // LED debug entrance sequence         
      for(int i = 0; i < 4; i++ ){
        digitalWrite(ledGreen,HIGH);
        delay(50);
        digitalWrite(ledRed,HIGH);
        delay(50);
        digitalWrite(ledGreen,LOW);
        delay(50);
        digitalWrite(ledRed,LOW);
        delay(50);
      }
      //enviar mensagem saida para app (WIFI)
      authenticated = false;
      return false;
  }
  else if(distance2<=PRESENCERADIUS&&distance>PRESENCERADIUS&&!exited&&!deviceAtLeft)
  {
    entrance =true;
    Serial.println("Entrance"); 
 
    if(authenticated){
        String toSend = "NEA"+tag;
         toSend.replace(" ","");
        Serial.println("Authenticated Exit!!!");
        Wifi.println(toSend);
       }
     else{
      Wifi.println("NEF-");
      }
        Wifi.print(EOL); 
    delay(250);
    noTone(buzzer);
    // LED debug entrance sequence
      for(int i = 0; i < 4; i++ ){
        digitalWrite(ledRed,HIGH);
        delay(50);
        digitalWrite(ledGreen,HIGH);
        delay(50);
        digitalWrite(ledRed,LOW);
        delay(50);
        digitalWrite(ledGreen,LOW);
        delay(50);
      }
        //enviar mensagem entrada para app (WIFI)
    authenticated = false;
    return false; 
  }
  else if(distance2<=PRESENCERADIUS&&distance>PRESENCERADIUS&&!exited&&deviceAtLeft){
    exited =true;
    Serial.println("Exit"); 
    if(authenticated){
         String toSend = "NSA"+tag;
          toSend.replace(" ","");
        Serial.println("Authenticated Exit!!!");
        Wifi.println(toSend);
       }
       else
       {
        Wifi.println("NSF-");
       }
        Wifi.print(EOL); 
    delay(250); 
    noTone(buzzer);
    // LED debug entrance sequence
     for(int i = 0; i < 4; i++ ){
        digitalWrite(ledGreen,HIGH);
        delay(50);
        digitalWrite(ledRed,HIGH);
        delay(50);
        digitalWrite(ledGreen,LOW);
        delay(50);
        digitalWrite(ledRed,LOW);
        delay(50);
      }
       //enviar mensagem saida para app (WIFI)
    authenticated = false;
     return false; 
  }
  else if(distance<=PRESENCERADIUS&&distance2>PRESENCERADIUS&&!entrance&&deviceAtLeft){
    entrance =true;
    Serial.println("Entrance");
    if(authenticated){
         String toSend = "NEA"+tag;
        Serial.println("Authenticated Exit!!!");
         toSend.replace(" ","");
        Wifi.println(toSend);
       }
      else{
      Wifi.println("NEF-");
      }
      Wifi.print(EOL); 
        
        
    delay(250);
    noTone(buzzer);
       // LED debug entrance sequence
      for(int i = 0; i < 4; i++ ){
        digitalWrite(ledRed,HIGH);
        delay(50);
        digitalWrite(ledGreen,HIGH);
        delay(50);
        digitalWrite(ledRed,LOW);
        delay(50);
        digitalWrite(ledGreen,LOW);
        delay(50);
      }
       //enviar mensagem entrada para app (WIFI)
    authenticated = false;
     return false;   
  }
}
else{
  noTone(buzzer);
  entrance = false;
  exited = false;
}
//getoption();
 while(Wifi.available()){
      process(Wifi);
    }
  
}
//possible commands
// '/digital/PIN/1 ou 0' para acender alguma luz
// '/V/EnderecoIP' para authenticar on demand da app para o arduino
// mais comandos a adicionar em breve
void process(WifiData Wifi) {
  // read the command
  String command = Wifi.readStringUntil('/');
 int pin, value;

  // Read pin number

  // is "digital" command?
  if(command == "digital")
  {
    //indice do comando
    pin = Wifi.parseInt();
     if(pin==0){
      if (Wifi.read() == '/') {
        value = Wifi.parseInt();
        authenticateClient();  
        }
      
    }
    if(pin == 1){
       if (Wifi.read() == '/'){
         value = Wifi.parseInt();
         sendAlarmNotification();
        }
      }
      if(pin == 2){
         if (Wifi.read() == '/'){
          value = Wifi.parseInt();
          silencedMode = !silencedMode;
          }
        }
      if(pin == 3){
         if (Wifi.read() == '/'){
          value = Wifi.parseInt();
           while(addTag());
          }
          }
      if(pin == 4){
         if (Wifi.read() == '/'){
          value = Wifi.parseInt();
          sendOutputStatus();
          }
        }
   Wifi.println("HTTP/1.1 200 OK\n");
  }
}
void sendOutputStatus(){
  
  }
boolean addTag(){
   if ( ! rfidSensor.PICC_IsNewCardPresent()) {
   
    return true;
  }
  // Select one of the cards
  if ( ! rfidSensor.PICC_ReadCardSerial()) {

    return true;
  }
  
  //Show UID on serial monitor
  Serial.print("UID tag :");
 String content = "";
  byte letter;
  //Conversao da tag para algo que se possa ler no serial
  for (byte i = 0; i < rfidSensor.uid.size; i++) {
     Serial.print(rfidSensor.uid.uidByte[i] < 0x10 ? " 0" : " ");
     Serial.print(rfidSensor.uid.uidByte[i], HEX);
     content.concat(String(rfidSensor.uid.uidByte[i] < 0x10 ? " 0" : " "));
     content.concat(String(rfidSensor.uid.uidByte[i], HEX));
  }
  Serial.println();
  Serial.print("Message : ");
  
  content.toUpperCase();
  content.trim();
  Wifi.println(content.length());
  char charc[12];
  content.toCharArray(charc,12);
  Wifi.print("NEW TAG ");
  Wifi.println(charc);
   tagMatrix[tagCounter] = content;
   tagCounter++;
   Wifi.println("Contents:");
   for(int i = 0 ; i<tagCounter;i++){
      Wifi.print(i);
      Wifi.println(tagMatrix[i]);
     
    }
   return false;
  }
void sendAlarmNotification(){
  Wifi.println("P----");
   Wifi.print(EOL);
}
void authenticateClient(){
    // Send feedback to client
     digitalWrite(ledGreen,HIGH);
        delay(500);
  Wifi.print(EOL); 
  digitalWrite(ledGreen,LOW);
 
}
void setupPins(){
  pinMode(ledGreen, OUTPUT);
  pinMode(ledRed, OUTPUT);
  pinMode(buzzer, OUTPUT);
  pinMode(pushbutton, INPUT);
  /*Entrance and exit sensors*/
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT);// Sets the echoPin as an Input
  pinMode(trigPin2, OUTPUT); // 
  pinMode(echoPin2, INPUT);
}
