
#include <SPI.h>
#include <PN532_SPI.h>
#include <PN532Interface.h>
#include <PN532.h>
#include <Ethernet2.h>

/*Chip select pin can be connected to D10 or D9 which is hareware optional*/
/*if you the version of NFC Shield from SeeedStudio is v2.0.*/
#define PN532_CS 9
#define ETHNET_CS 10
PN532 nfc(PN532_CS);

/* Network Settings */
byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x3B, 0xB9 }; // MAC address
IPAddress ip(192,168,0,10);       // Static IP if DHCP fails
IPAddress server(74,125,232,128);  // numeric IP for Google (no DNS)
EthernetClient client;

/* Chip select routine */
void spiSelect(int CS) {
  // disable all SPI
  pinMode(PN532_CS,OUTPUT);
  pinMode(ETHNET_CS,OUTPUT);
  digitalWrite(PN532_CS,HIGH);
  digitalWrite(ETHNET_CS,HIGH);
  // enable the chip we want
  digitalWrite(CS,LOW); 
}


/* Startup routine */
void setup(void) {

  /* Start serial output */   
  Serial.begin(57600);
  Serial.println("Arduino Powered On");

  // disable the NFC SPI here so just network is active
  spiSelect(ETHNET_CS);
// ethernet uses the default MSB first so making sure that is set
  SPI.setBitOrder(MSBFIRST);
 
  /* Initialise Network */
  Serial.println("Sending DHCP request");
  if (Ethernet.begin(mac) == 0) {
      Serial.println("Failed to configure Ethernet using DHCP");
      Serial.println("Setting static IP address instead");
      Ethernet.begin(mac, ip);
      Serial.print("Arduino IP is ");
      Serial.println(Ethernet.localIP());
    }
    else {
      Serial.println("Obtained DHCP lease");
      Serial.print("Arduino IP is ");
      Serial.println(Ethernet.localIP());     
    }
 
  /* Configure web client */
  if (client.connect(server, 80)) {
    Serial.println("Connected to web server");
    client.println("GET /search?q=arduino HTTP/1.1");
    client.println("Host: www.google.com");
    client.println("Connection: close");
    client.println();
  } else {
    Serial.println("Connection failed");
  }
 
  // disable the ethernet SPI here so just NFC is active
  spiSelect(PN532_CS);
// NFC uses LSB first so we have to explicitly set that
  SPI.setBitOrder(LSBFIRST);
 
  /* Initialise NFC reader */
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  Serial.print("Found chip PN5");
  Serial.println((versiondata>>24) & 0xFF, HEX);
  Serial.print("Firmware ver. ");
  Serial.print((versiondata>>16) & 0xFF, DEC);
  Serial.print('.');
  Serial.println((versiondata>>8) & 0xFF, DEC);
  Serial.print("Supports ");
  Serial.println(versiondata & 0xFF, HEX);
  nfc.SAMConfig();

}

/* Main program loop */
void loop(void) {

  // disable the ethernet SPI here so just NFC is active
  spiSelect(PN532_CS);
// NFC uses LSB first so we have to explicitly set that
  SPI.setBitOrder(LSBFIRST);
   
  /* look for MiFare type cards */
  uint32_t id;
  id = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A);
  if (id != 0) {
    Serial.print("Read card #");
    Serial.println(id);   
  }
 
  // disable the NFC SPI here so just network is active
  spiSelect(ETHNET_CS);
// ethernet uses the default MSB first so we need to switch back
  SPI.setBitOrder(MSBFIRST);
   
  // check ethernet is still working
  Serial.print("Arduino IP is ");
  Serial.println(Ethernet.localIP());
 
}
