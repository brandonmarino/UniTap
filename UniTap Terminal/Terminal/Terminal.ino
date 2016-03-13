

#include <PN532.h>
#include <SPI.h>
#include <Ethernet2.h>

// The new PN532 library uses SPI HW implementation from official arduino SPI.h library
// The original PN532 library from seeedstudio uses a dedicated SPI SW implementation in PN532.h library 
//
// The benefit of HW approach is that MISO/MOSI/SCK PINs can be reused between NFC and Ethernet shields. Chip Select (SS) PIN must be different for Ethernet and NFC shields.
// In fact, there are three different SS PINs: 
//      - Ethernet chip hardwired to PIN 10 in Ethernet shield
//      - SD card chip hardwired to PIN 4 in Ethernet shield
//      - NFC chip hardwired to PIN10 in NFC shield
//
// In order to avoid conflict between PIN10 in both shields when NFC shield is stacked on top of Ethernet shield, male PIN 10 of NFC shield must be bent so that it does not
// connect into female PIN 10 of Ethernet shield headers
//
// Connection instructions:
// ========================
// Stack Ethernet shield on top of Arduino Uno (connect all PINs including ICSP)  
// Stack NFC shield on top of Ethernet shield bending male PIN 10 of NFC shield so that it does not connect into Ethernet shield
//
// Both official Ethernet and Seeedstudio NFC shields use the ICSP PINs for SPI protocol (instead of the actual digital PINs). Since the NFC shield ICSP PINS are unconnected,
//  they need to be wired to the appropriate digital PINs in the NFC shield.
// This is the required wiring:
//
//   * Wire digital female PINs in NFC shield:
//            NFC_SS  (NFC)  -> 10 (NFC)  SS    
//      Make sure that you choose a free digital PIN for NFC_SS (do not use PINs 0,1,4,10,11,12,13)
//      In this sample code NFC_SS is defined as digital PIN 3
//
//   * Wire female ICSP PINs of NFC shield to the appropriate female PINs of NFC shield:
//            SCK  (ICSP NFC)  -> 13 (NFC)
//            MISO (ICSP NFC)  -> 12 (NFC)
//            MOSI (ICSP NFC)  -> 11 (NFC)
//
//   * Finally wire female ICSP Vcc PIN of NFC shield with the 5V power female PIN of NFC shield
//            Vcc  (ICSP NFC)  -> 5V (NFC) This is the 5 Volts power PIN (WARNING: it is NOT digital PIN 5 )
//
// SPI configuration parameters of PN532 chip (NFC) are different from the SPI parameters of W5100 chip (Ethernet). 
// In paricular PN532 uses LSBFIRST bit order while W5100 uses MSBFIRST bit order.
// Since both chips are reusing the same library and HW, we need to backup/restore the SPI configuration everytime 
// that we use NFC functionality from the new PN532 library
//
// Instructions for Arduino Mega 2560 or ADK:
// The HW digital PINs for SPI are different in Arduino Mega (MOSI->51; MISO->50; SCK->52)
// The library and sample sketch are fully compatible with Arduino Mega. To make it work, just change the wiring of 
// ICSP header PINs of NFC shield:
//   * Wire female ICSP PINs of NFC shield to the appropriate female PINs ofArduino Mega/ADK shield:
//            SCK  (ICSP NFC)  -> 52 (Mega)
//            MISO (ICSP NFC)  -> 50 (Mega)
//            MOSI (ICSP NFC)  -> 51 (Mega)
//
//
// KNOWN ISSUES
// ============
// The new library does not work when three SPI shields are stacked together (e.g. official Ethernet shield, 
// Seeedstudio NFC shield and Sparkfun MP3 shield). 
// I believe this is due to noise introduced in the system by the wiring. In this case, the official Seeedstudio PN532.h
// library (SW SPI implementation) can still be used with the appropriate wiring but then different PINs must be selected for 
// NFC_MISO, NFC_MOSI, NFC_SCK (SW library is not compatible with HW library so they must use completely dissociated PIN sets)
//
//
// Example sketch
// ==============
// This sample code initialises NFC and Ethernet shields and then enters an infinite loop waiting for the detection of NFC/RFID tags.
// When a tag is detected, its ID is read and then uploaded to Evrythng server in internet using its http REST API.
// Note that in order to use this service, you must obtain your own credentials from Evrythng (check instructions at evrythng.com). Alternatively, 
// you can use other similar services available in Internet.
//
// You have to retrieve your authentication token from https://evrythng.net/settings/tokens and update its value in your Arduino code: 
//      client.println("X-Evrythng-Token: ...yourAPITokenHere...")
// You must also crate a new thng in evrytng with a property called ReadTag. Once created, you have to update its thngId value in your Arduino code: 
//     client.println("PUT http://evrythng.net/thngs/...yourThingIdHere.../properties/ReadTag HTTP/1.1");
//
//
// License
// =======
// Copyright 2012 Javier Montaner
// This software is licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
// OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

#define NFC_SS 9
#define ETH_SS 10
#define SD_SS 4

PN532 nfc(NFC_SS);

byte mac[] = {  0x90, 0xA2, 0xDA, 0x00, 0x00, 0x00 };

unsigned long time;
unsigned long responseTime;

uint32_t tagId=0;
uint32_t xmitId=0;
char  tagIdString [11]= "1234567890";

uint32_t flowState =0;

#define STATE_IDDLE 0
#define STATE_SENDDATA 15
#define STATE_RESPONSE 20


//------Variables-------------
IPAddress serverIP(192,168,2,15);
int serverPort = 2015;

EthernetClient client;
EthernetClient client1;
EthernetServer server(2016);

const int maxPacketSize   = 20;       // max size of buffers in bytes
int msg;
int visit=0;

String readString="";
String s=" ";




//-------functions used by loop----------

String getCommand(String str);

void sendToServer(int i);

//----------end of functions


void setup()
{
  pinMode (ETH_SS, OUTPUT); 
  pinMode (SD_SS, OUTPUT);   
  pinMode (NFC_SS, OUTPUT);  
  digitalWrite(ETH_SS, HIGH);
  digitalWrite(SD_SS, HIGH);  

  time = millis();
  Serial.begin(9600);
  Serial.println("Starting setup method...");

  //Initialise NFC reader
  nfc.backupSPIConf();
  nfc.begin();
  nfc.RFConfiguration(0x14); // default is 0xFF (try forever; ultimately it does time out but after a long while
                             // modifies NFC library to set up a timeout while searching for RFID tags
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    // stop
    for(;;);
  }    
  // ok, print received data!
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  Serial.print("Supports "); Serial.println(versiondata & 0xFF, HEX);
  // configure board to read RFID tags and cards
  nfc.SAMConfig();
  nfc.restoreSPIConf();
 
 
  //Initialise Ethernet connection
  Serial.println("StartEthernet");
  digitalWrite(10, LOW); //SPI select Ethernet
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // stop
    for(;;);
  }
  digitalWrite(10, HIGH); //SPI deselect Ethernet
  
//----------ETHERNET SETUP
  server.begin();
  Serial.print("server is: ");
  Serial.println(server.available());
  Serial.print("Serving from ");
  Serial.println(Ethernet.localIP());

 delay(1000);
 Serial.println("connecting to Server ...");
// if you get a connection to the JAVA Server
  
  if (client.connect(serverIP, serverPort)) {
    Serial.println("Conected");//report it to the Serial
    String msg="Hello Server";//Message to be sent
    Serial.println("sending Message:"+msg);//log to serial
    client.println(msg);//send the message
  }
  else {
  // if you didn't get a connection to the server:
  Serial.println("connection failed");
  }//end of ELSE

  //---------DONE WITH ETHERNET 
  
  Serial.println("NFC and Ethernet initialised OK");   
  
  flowState=STATE_IDDLE;
  delay(1000);
} //END setup



void loop()
{ 
  if ((millis()-time > 1000)&&(flowState==STATE_IDDLE)) {
      Serial.println("Checking NFC...");
    // look for Mifare type cards every second
    time=millis();
    digitalWrite(10, HIGH);//SPI deselect Ethernet    
    nfc.backupSPIConf();
    Serial.println("Start NFC read");
    tagId = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A);
    Serial.println("End NFC read");
    nfc.restoreSPIConf();
    if (tagId != 0) 
    { 
      Serial.print("Read card #"); Serial.println(tagId);
      xmitId=0;
      uint32_t divisor= 1000000000;
      for (int i=0;i<10; i++){
         tagIdString [i]= char(48 + tagId/divisor);
         tagId=tagId%divisor;
         divisor /=10;
      }
      Serial.print("Converted String: "); 
      Serial.println(tagIdString);
      time=millis();
      flowState=STATE_SENDDATA;
      return;
     }
  }   

/**
 * Here we need to set up the connection to our server 
 **/

 

 
if (flowState==STATE_SENDDATA) {
  
    Serial.println("Connecting to server ...");
    delay(2000);
  /*
 * Send to server start
 * 
 */
sendToServer(tagIdString);
delay(5000);
responseTime=millis();
flowState=STATE_RESPONSE;
// if the server's disconnected, stop the client:
if (!client.connected()) {
 Serial.println();//report it to the serial
 Serial.println("disconnected");
 client.stop();
 flowState=STATE_IDDLE;
}

/*
 * End send to server
 * 
 */  
 
      

}
   if (flowState== STATE_RESPONSE) {
digitalWrite(10, LOW);//SPI deselect Ethernet    
/*
 * Get command start
 */
    char s[maxPacketSize];
    // establish TCP connection
    Serial.println("Server Response: ");
    while (!client1.connected()) {
      
      client1.stop();                  // not connected, so terminate any prior client
      client1 = server.available();
      //Serial.println("IN DA LOOP");
      delay(5000);
      if (client1)
        {
        Serial.println("TCP Connection Established"); 
        }
        
      else
        continue;
      }
        getCommand(readString);
        Serial.println(readString);
        
        if(readString.indexOf("on") >0)//checks for on
          {
            //digitalWrite(4, HIGH);    // set pin 4 high
            Serial.println("Led On");
          }
         
          if(readString.indexOf("off") >0)//checks for off
          {
            //digitalWrite(4, LOW);    // set pin 4 low
            Serial.println("Led Off");
          }
        
    if ((millis() - responseTime)>2000) {
        Serial.println("Closing connection to server");
        client1.stop();
        flowState=STATE_IDDLE;
        delay(5000);
       
        readString="";
    }
  }


} //end loop  

  
  


 
 
 //------------------------------------- Functions

// Get the next command from the PC. If any issue, Return "Error" message 
String getCommand( String str ) { 
    
    char c;
   
  while (client1.connected()) {
    if (client1.available()) {
      c = client1.read();
      readString+=c;
      
    }
  }
 // Serial.print(readString);
  //strcpy(s, "Hola");
  return s;
    } // getCommand


//---------------------End of getcommand 
   

void sendToServer(String i){
Serial.println("visit: ");
Serial.println(visit);
  if(visit==0)
  {
    if (client.connected()) {
      Serial.println("Conected again");//report it to the Serial
      String msg="Hello again";//Message to be sent
      Serial.println("sending Message:"+msg);//log to serial
      client.println(msg);//send the message
    }
    else 
    {
      Serial.println("well too bad");
    // if you didn't get a connection to the server:
      Serial.println("connection failed");
  }//end of ELSE

  }//end if visit 
  visit++;
delay(500);

unsigned int  uS=23; //sonar.ping(); // Send ping, get ping time in microseconds (uS).
int msg=uS;
Serial.print("Ping: ");
Serial.println(i);
client.print("Ping: ");
client.println(i); 


}//end of sendToServer
 
 

