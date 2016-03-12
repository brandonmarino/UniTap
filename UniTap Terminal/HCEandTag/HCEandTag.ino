#include <SPI.h>
#include <PN532_SPI.h>
#include <PN532Interface.h>
#include <PN532.h>
#include <stdint.h>

#include "HCE-BackEnd/Packetization/Packetization.h"

const int COMPANYID = 1;

PN532_SPI pn532spi(SPI, 10);
PN532 nfc(pn532spi);

const int MAX_APDU_LENGTH = 31;

uint8_t * nextMessage;
uint8_t * lastMessage;

bool nextMessageSent = false;
bool lastAckReceived = false;
const int REDLEDPIN = 3;
const int GREENLEDPIN = 4;

int errorCount = 0;
int ledCount;

byte junk[] = {0x75,0x2e};


void loop()
{
  bool HceActive;
  
  uint8_t responseLength = MAX_APDU_LENGTH;
  
  Serial.println("Waiting for an ISO14443A card or phone");
  
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
          respondLength = 2;
        else
          //read the length from the byte array
          respondLength = respondApdu[3];
          
        apduExchangeSuccess = nfc.inDataExchange(respondApdu, respondLength, apdu, &length);
        
        //deal with response
        if(apduExchangeSuccess){
          if(!isReject(apdu)){    
            respondApdu = uniTapOffApduService(apdu);
            hardwareResponseToMessage(apdu);
              
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
          clearMessages;
          nextMessageSent = false;
          lastAckReceived = false;
          blinkOff();
        }
      }
      while(apduExchangeSuccess&& !errorProne);
    }
    else {
      Serial.println("Failed sending SELECT AID"); 
      blinkFailure();
    }
  }
  else {
    Serial.println("Didn't find anything!");
  }
  delay(1000);
}

byte* uniTapOffApduService(byte apdu[]){
  static byte output [MAX_APDU_LENGTH] = {0x75,0x2e};
  byte * response;
  
  if (isJunk(apdu)){
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


/****************************************************************
 *                  Create Protocol Responses
 ***************************************************************/
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
  if(isGenericApdu(apdu)){
    Serial.println("Received Authorization Request");
    response = handleUniTapGeneric(apdu, length);
  }
  else if (isAckApdu(apdu)){
    Serial.println("Message is an Ack");
    response = junk;
  }
  else if (isErrorApdu(apdu)){
    Serial.println("Error Received, resending last message");
    response = lastMessage;
  }
  
  for(int i = 0; i < MAX_APDU_LENGTH;i++){
    output[i] = response[i];
  }
  return output;
}

byte* handleUniTapGeneric(byte apdu[], int apduLength){
  static byte output [MAX_APDU_LENGTH] = {0x75,0x2e};
  byte * response;
  //verifyCRC
  bool isValidCRC = checkCRC(apdu, apduLength);
  
  if (isValidCRC){
    //check companyID
      Serial.println("The CRC has been validated"); 
      Serial.println("The message is being sent to the proxy server.");
      errorCount=0;
      //send to server here
      response = createAck(apdu, apduLength);
    //else
      //Serial.println("This tag is not linked to this company")
  } else {
    Serial.println("The CRC was not validated.");
    Serial.println("Reporting error to the phone and requesting the message again");
    errorCount++;
    response = createError(apdu, apduLength);
  }
  for(int i = 0; i < MAX_APDU_LENGTH;i++)
    output[i] = response[i];
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
  if(apdu[3] == 0x02)
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
  nextMessage = newNext;
  lastMessage = newLast;
}


 /***************************************************************
 *                  Terminal Setup
 ***************************************************************/
bool bootSuccess = true;
void setup()
{   
    Serial.begin(9600);
    Serial.println("-------Peer to Peer HCE--------");
    
    ledCount = 0;
    
    pinMode(REDLEDPIN, OUTPUT);
    pinMode(GREENLEDPIN, OUTPUT);
    
    setupNFC();
}


void setupNFC() {
  
  nfc.begin();
    
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    Serial.print("Please Reboot Me!");
    while (1){
      blinkFailure();
    }
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  
  // configure board to read RFID tags
  nfc.SAMConfig();
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
    Serial.print("Message FROM Phone: ");
    printByteArray(apdu, 32);
  }
  if(lastAckReceived)
    blinkSuccess();
  else
    blinkWaiting();
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
