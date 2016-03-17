#include <SPI.h>
#include <PN532_SPI.h>
#include <PN532.h>
#include <Ethernet2.h>

#include "HCE-BackEnd/Packetization/Packetization.h"

/********************************************
*           Fields
*********************************************/
#define ETH_SS 10
#define NFC_SS 9
#define REDLEDPIN 3
#define GREENLEDPIN 4
#define companyId 0
#define MAX_APDU_LENGTH 31
#define maxPacketSize 20       // max size of buffers in bytes

PN532_SPI pn532spi(SPI, NFC_SS);
PN532 nfc(pn532spi);

uint32_t flowState =0;
unsigned long time;
unsigned long responseTime;

byte mac[] = {  0x90, 0xA2, 0xDA, 0x00, 0x00, 0x00 };

EthernetClient client;
EthernetServer server(2016);

byte * nextMessage;
byte * lastMessage;

byte lastAPDU[MAX_APDU_LENGTH];

bool nextMessageSent = false;
bool lastAckReceived = false;
bool waiting = true;

int errorCount = 0;
int ledCount;

byte junk[] = {0x75,0x2e};
/**************************************************
*         Continuous Code Execution
**************************************************/

void loop(void) {
  loopNFC();
}

void loopNFC(){
  
  bool HceActive;
  uint8_t responseLength = MAX_APDU_LENGTH;
  
  // set shield to inListPassiveTarget
  HceActive = nfc.inListPassiveTarget();
  
  if(HceActive) {
    Serial.println("HCE Transaction Started");
    //prepare selectApdu
    uint8_t selectApdu[] = { 0x00, 0xA4, 0x04, 0x00, 
                              0x07, /* Length of AID  */
                              0xF0, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, /* AID defined on Android App */
                              0x00  /* Le  */ };
    //prepare spot for the response
    
    uint8_t response[responseLength];    
    bool apduExchangeSuccess = nfc.inDataExchange(selectApdu, sizeof(selectApdu), response, &responseLength);
    
    if(apduExchangeSuccess) {
      
      byte *respondApdu = junk;
      bool errorProne = false;

      do {
        byte apdu[MAX_APDU_LENGTH];
        byte length = MAX_APDU_LENGTH;

        //size up the message
        int respondLength = 0;
        
        if(respondApdu[0] == 0x75 && respondApdu[1] == 0x2E)
        //if is junk
          respondLength = 4;
        else{
          Serial.println("Message To Phone");
          printByteArray(respondApdu, 31);
          //read the length from the byte array
          respondLength = respondApdu[3];
        }
        apduExchangeSuccess = nfc.inDataExchange(respondApdu, respondLength, apdu, &length);
        
        //deal with response
        if(apduExchangeSuccess){
          if(!isReject(apdu) || isEqual(lastAPDU, apdu)){    
            respondApdu = uniTapOffApduService(apdu);
            copyLastApdu(apdu);
            if (errorCount == 3){
              Serial.print("The connection is too error prone. HCE shut down.");
              blinkFailure();
              errorProne = true;
            }
          }else{
            digitalWrite(GREENLEDPIN, LOW);
            digitalWrite(REDLEDPIN, LOW);
          }
        }
        else {
          Serial.println("\nPhone Removed from the Terminal");
          clearMessages();
          nextMessageSent = false;
          lastAckReceived = false;
          blinkOff();
        }
      }
      while(apduExchangeSuccess&& !errorProne);
    } else {
      Serial.println("Failed sending SELECT AID"); 
      blinkFailure();
    }
  }
  delay(2000);
}
/*****************************************************
*               UniTap Card Service
******************************************************/
byte* uniTapOffApduService(byte apdu[]){
  static byte output [MAX_APDU_LENGTH] = {0x75,0x2e};
  byte * response;
  
  if (isJunk(apdu)){
    Serial.print(".");
    if(nextMessageSent){
      response = junk; //return junk
    }
    else{
      if(lastAckReceived){
        switchMessages();
        lastAckReceived = false;
        nextMessageSent = true;
        response = nextMessage;
      }else{
        response = junk;
      }
    }
  }else{
    response = handleUniTapMessage(apdu);
  }

  for(int i = 0; i < MAX_APDU_LENGTH;i++)
    output[i] = response[i];
  return output;
}

byte* createAck(byte apdu[], int apduLength){
  static byte output [MAX_APDU_LENGTH];
  for(int i = 0; i <apduLength; i++)
    output[i] = apdu[i];
  output[2] = 0x01;
  return output;
}

byte* createError(byte apdu[], int apduLength){
  static byte output [MAX_APDU_LENGTH];
  for(int i = 0; i <apduLength; i++)
    output[i] = apdu[i];
  output[2] = 0x02;
  return output;
}

/***************************************************************
 *                  UniTap Protocol Handlers
 ***************************************************************/
 
byte* handleUniTapMessage(byte apdu[]){
  static byte output [MAX_APDU_LENGTH] = {0x75,0x2e};
  byte * response;
  
  int length = apdu[3];
  if (isAckApdu(apdu)){
    Serial.println("Message is an Ack");
    response = junk;
  }
  else if (isErrorApdu(apdu)){
    Serial.println("Error Received, resending last message");
    response = lastMessage;
  }
  else if(isGenericApdu(apdu)){
    Serial.println("Received Authorization Request");
    response = handleUniTapGeneric(apdu, length);
  }
  
  for(int i = 0; i < MAX_APDU_LENGTH;i++){
    output[i] = response[i];
  }
  return output;
}

byte* handleUniTapGeneric(byte apdu[], int apduLength){
  static byte output [MAX_APDU_LENGTH] = {0x75,0x2e, 0x00, 0x04};
  byte * response = output;
  //verifyCRC
  bool isValidCRC = checkCRC(apdu, apduLength);
  if(getCompanyId(apdu)==companyId){
    if (isValidCRC){
      Serial.println("The CRC has been validated"); 
      Serial.println("Attempting to send the message to the proxy server.");
      errorCount=0;
      //response = createAck(apdu, apduLength);
      output[0] = 0x00;
      output[1] = 0x00;
      output[2] = 0x01;
      output[3] = 0x06;
      //send to server here
      //sendApduToServer(apdu);
    } else {
      Serial.println("The CRC was not validated.");
      Serial.println("Reporting error to the phone and requesting the message again");
      errorCount++;
      //response = createError(apdu, apduLength);
      output[0] = 0x00;
      output[1] = 0x00;
      output[2] = 0x02;
      output[3] = 0x06;
    }
  }else
      Serial.println("This tag is not linked to this company");
  return output;
}


/***************************************************************
 *                  Message Type Comparators
 ***************************************************************/
bool isGenericApdu(byte apdu[]){
  if(apdu[2] == 0x00)
    return true;
  return false;
}
bool isAckApdu(byte apdu[]){
  if(apdu[2] == 0x01)
    return true;
  return false;
}
bool isErrorApdu(byte apdu[]){
  if(apdu[2] == 0x02)
    return true;
  return false;
}
bool isJunk(byte apdu[]){
  if (apdu[0] == 0x75)
    if (apdu[1] == 0x2e)
      return true;
  return false;
}
bool isReject(byte apdu[]){
  if (apdu[1] == 0x2e){
    if (apdu[0] == 0x6f)
      return true;
    if (apdu[0] == 0x6a)
      return true;
  }
  return false;
}

/***************************************************
 *          Other Stuff
 ****************************************************/
void switchMessages(){
  static byte newLast [MAX_APDU_LENGTH] = {0x75,0x2e};
  nextMessage = lastMessage;
  lastMessage = newLast;
}

void clearMessages(){
  static byte newNext [MAX_APDU_LENGTH] = {0x75,0x2e};
  static byte newLast [MAX_APDU_LENGTH] = {0x75,0x2e};
  for(int i = 2; i < MAX_APDU_LENGTH; i++){
    newNext[i] == 0;
    newLast[i] == 0;
  }
  nextMessage = newNext;
  lastMessage = newLast;
}

/******************************************************
*         Server Communication
******************************************************/
void sendApduToServer(byte apdu[]){
  int apduLength = apdu[3];
  enableETH();  //switching the ethernet shield on
  if (client.connected()) {
    Serial.println("Connection to proxy-server established");
    Serial.println("Forwarding APDU to the proxy-server");
    client.write(apdu, apduLength); 
    Serial.println("Waiting for the server response");
  } else {
    Serial.println("Unable to connect to the proxy-server");
  }
  reInitPN532(); //switching the pn532 back on
}
/**************************************************************
 *                Acting on External IO 
 *************************************************************/
void printByteArray(byte array[], int arrayLength){
  for(int i = 0; i < arrayLength; i++){
    Serial.print("0x");
    Serial.print(array[i], HEX);
    Serial.print(", ");
  }
  Serial.println();
}

void printResponse(uint8_t *response, uint8_t responseLength) {
  
   String respBuffer;

    for (int i = 0; i < responseLength; i++) {
      
      if (response[i] < 0x10) 
        respBuffer = respBuffer + "0"; //Adds leading zeros if hex value is smaller than 0x10
      
      respBuffer = respBuffer + String(response[i], HEX) + " ";                        
    }
    Serial.print("response: "); Serial.println(respBuffer);
}

void hardwareResponseToMessage(byte apdu[]){
  if(isJunk(apdu)){
    //show continued communication of useless packets in the serial monitor
    Serial.print(".");
  }else{
    Serial.print("Byte-Stream from the Android Phone:- \n ");
    printByteArray(apdu, 32);
  }
}

bool isEqual(byte array1[], byte array2[]){
  for(int i = 0; i < sizeof(array1) && i < sizeof(array2); i++){
    if ( !(array1[i] == array2[i]) )
      return false;
  }
  return true;
}

bool copyLastApdu(byte oldApdu[]){
  for(int i = 0; i < sizeof(oldApdu); i++)
    lastAPDU[i] = oldApdu[i];
}
/****************************************************************
 *            Blink for result of transaction handler
 ***************************************************************/
bool REDON = false;
bool GREENON = false;

void blinkSuccess(){
  digitalWrite(REDLEDPIN, LOW);
  digitalWrite(GREENLEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(300);              // wait for a second
  digitalWrite(GREENLEDPIN, LOW);    // turn the LED off by making the voltage LOW
  delay(300);              // wait for a second
  digitalWrite(GREENLEDPIN, HIGH);    // turn the LED off by making the voltage LOW
  delay(300);              // wait for a second
  digitalWrite(GREENLEDPIN, LOW);    // turn the LED off by making the voltage LOW
  //blink green lights, slowly (beep [2 quick beeps] if I can find my speaker)
}

void blinkWaiting(){
  //blink alternate quickly
  if(ledCount == 0){
    if(REDON == true){
      digitalWrite(GREENLEDPIN, HIGH);
      digitalWrite(REDLEDPIN, LOW);
      REDON = false;
      GREENON = true;
    }else{
      digitalWrite(GREENLEDPIN, LOW);
      digitalWrite(REDLEDPIN, HIGH);
      REDON = true;
      GREENON = false;
    }
  }
  ledCount++;
  if(ledCount == 10)
    ledCount = 0;
}

void blinkFailure(){
  digitalWrite(GREENLEDPIN, LOW);
  digitalWrite(REDLEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(300);              // wait for a second
  digitalWrite(REDLEDPIN, LOW);    // turn the LED off by making the voltage LOW
  delay(300);              // wait for a second
  digitalWrite(REDLEDPIN, HIGH);    // turn the LED off by making the voltage LOW
  delay(300);              // wait for a second
  digitalWrite(REDLEDPIN, LOW);    // turn the LED off by making the voltage LOW
  
  //blink red lights, slowly (beep [one long beep] if i can find the speaker)
}
void blinkOff(){
  digitalWrite(GREENLEDPIN, LOW);    // turn the LED off by making the voltage LOW
  digitalWrite(REDLEDPIN, LOW);    // turn the LED off by making the voltage LOW
}

/*********************************************************
*         Setup and Initializers
***********************************************************/
void setup(void) {
  Serial.begin(9600);

  //leds
  pinMode(REDLEDPIN, OUTPUT);
  pinMode(GREENLEDPIN, OUTPUT);
  //boards
  pinMode (ETH_SS, OUTPUT); 
  pinMode (NFC_SS, OUTPUT);  
  digitalWrite(NFC_SS, LOW);
  digitalWrite(ETH_SS, HIGH);

  initETH();
  initPN532();

  blinkSuccess();
}

void initETH() {
  //Initialise Ethernet connection
  Serial.println("Initializing Ethernet Shield");
  enableETH();
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // stop
    while(1){
      blinkFailure();
    }
  }
  Serial.println("Ethernet Shield Initialized");
  //ETHERNET SETUP
  server.begin();
  Serial.print("The server is ");
  if (server.available())
    Serial.println("available");
  else
    Serial.println("unavailable");
  Serial.print("Terminal IP ");
  Serial.println(Ethernet.localIP());
}

void initPN532() {
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1){ // halt
      blinkFailure();
    }
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5");
  Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); 
  Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); 
  Serial.println((versiondata>>8) & 0xFF, DEC);
  
  Serial.println("Waiting for a phone to be tapped.");
  // configure board to read RFID tags
  nfc.SAMConfig();
}
void reInitPN532() {
  enablePN();
  nfc.begin();
  // configure board to read RFID tags
  nfc.SAMConfig();

  bool success;
  uint8_t responseLength = 31;
  success = nfc.inListPassiveTarget();
  if(success) {   
    uint8_t selectApdu[] = { 0x00, /* CLA */
                              0xA4, /* INS */
                              0x04, /* P1  */
                              0x00, /* P2  */
                              0x07, /* Length of AID  */
                              0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, /* AID defined on Android App */
                              0x00  /* Le  */ };
    uint8_t response[31];  
    success = nfc.inDataExchange(selectApdu, sizeof(selectApdu), response, &responseLength);
    if(success) {
      uint8_t *apdu = junk;
      uint8_t back[32];
      uint8_t length = 32;
      nfc.inDataExchange(apdu, 2, back, &length);
    }
  }
}
/*************************************************************
*               SPI_bus clashing workaround
***************************************************************/
void enablePN() {
  digitalWrite(ETH_SS, HIGH);
  digitalWrite(NFC_SS, LOW);
  SPI.setDataMode(SPI_MODE0);
  SPI.setBitOrder(LSBFIRST);
  SPI.setClockDivider(SPCR & SPI_CLOCK_MASK);
  delay(10);
}

void enableETH() {
  digitalWrite(ETH_SS, LOW);
  digitalWrite(NFC_SS, HIGH);
  SPI.setBitOrder(MSBFIRST);
  SPI.setClockDivider(SPI_CLOCK_DIV4); 
  SPI.setDataMode(SPCR & SPI_MODE_MASK);
  SPCR &= ~(_BV(DORD));
  SPI.setClockDivider( SPCR & SPI_CLOCK_MASK);
  delay(10);
}
