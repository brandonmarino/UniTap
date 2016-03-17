#include <stdint.h>

#define HCE_MAX_MESSAGE 31
#define OVERHEAD 4

/**
 * This Class will add all encapsulating data to an initial message. That message must be below
 *
 * Packet Dimensions
 * 0                    2             3                      4                     6                   9
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-number | 2 byte phone-id CRC | 3-byte Company id |<=22 byte message user's id
 * Packet types: (0=Generic, 1=Acknowledgemen, 2=Error, 4=split message)
 *
 * We decided on headers because we don't know what information/what length the company would want to send. So we are giving them as much room at the back as possible.
 *
 * Its efficiency is about O(5n) where n is the length of the byte message (up to 26 bytes)
 * Created by Brandon Marino on 2/7/2016.
 */

uint8_t * addCRC(uint8_t message[], int length);
uint8_t * addType(uint8_t message[], int type);
uint8_t * addPacketNumber(uint8_t message[], int num);
uint8_t * appendHeadIntValue(uint8_t message[], int value, int significantDigits);
int sizeArray(uint8_t message[]);
/**
	
*/
uint8_t * encapsulate(uint8_t message[], int messageLength, int type, int messageNumber){
	static uint8_t * output;
	output = message;
	if(messageLength+OVERHEAD < HCE_MAX_MESSAGE){
		output = addPacketNumber(output, messageNumber);
		output = addType(output, type);
		output = addCRC(output, messageLength);
	}
	return output;
}

/**
	between 0 and 2
*/
uint8_t * addCRC(uint8_t message[], int length){
	static uint8_t output[HCE_MAX_MESSAGE] = {0x00};
	uint8_t * appended = appendCRCBytes(message, length+OVERHEAD-2);
	
	for(int i = 0; i<length+OVERHEAD; i++){
		output[i] = appended[i];
	}
	//memcpy(output, appended, HCE_MAX_MESSAGE);
	return output;
}

/**
	between 2 and 3
*/
uint8_t * addType(uint8_t message[], int type){
	static uint8_t output[HCE_MAX_MESSAGE] = {0x00};
	uint8_t * appended = appendHeadIntValue(message, type, 1);
	for(int i = 0; i<HCE_MAX_MESSAGE; i++){
		output[i] = appended[i];
	}
	//memcpy(output, appended, HCE_MAX_MESSAGE);
	return output;
}

/**
	between 3 and 4
*/
uint8_t * addPacketNumber(uint8_t message[], int num){
	static uint8_t output[HCE_MAX_MESSAGE] = {0x00};
	uint8_t * appended = appendHeadIntValue(message, num, 1);
	//memcpy(output, appended, HCE_MAX_MESSAGE);
	for(int i = 0; i<HCE_MAX_MESSAGE; i++){
		output[i] = appended[i];
	}
	return output;
}

/**
	Take 'significatFigures's bytes of a value integer and append the onto the start of an array  
*/
uint8_t * appendHeadIntValue(uint8_t message[], int value, int significantDigits){
	
	static uint8_t output[HCE_MAX_MESSAGE] = {0x00};
	//output = message;
	for(int i = 0; (i < HCE_MAX_MESSAGE) || (i < sizeArray(message)); i++)
		output[i+significantDigits] = message[i];

	for (int i = 0; i < significantDigits; i++)
		output[i] = (value >> 8*(i)) & 0xFF;
	return output;
}

int sizeArray(uint8_t array[]){
	return sizeof(array)/sizeof(array[0]);
}