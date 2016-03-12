#include <stdint.h>
#define HCE_MAX_MESSAGE 31
#define OVERHEAD 9
int combineBytes(uint8_t input[]);

/**
 * A Full CRC Generator
 * Created by Brandon Marino on 2015-11-26.
 */

int generateUniTapCrc(uint8_t input[],int sizeOfArray)
{
	uint16_t crc = 0xffff;
	for(int i = 0; i < sizeOfArray; i++){
   		crc = crc ^ (input[i] << 8);
 		for (int j=0; j<8; j++){
   			if (crc & 0x8000)
     			crc = (crc << 1) ^ 0x1021;
   			else
     			crc <<= 1;
     	}
 	}
	return crc;
}

    /**
     * This function will take a full byte array, compare it's original Two bytes against the rest of the message
     * The entirety of the rest of the packet will be error checked. Nothing should have been changed
     * @param input
     * @return
     */
int verifyCRC (uint8_t input[], int sizeOfArray) {
	uint8_t actualMessage [HCE_MAX_MESSAGE];
	//get the actual message
	for (int i = 0; i < sizeOfArray; i++)
		actualMessage[i]=input[i+2];

	uint8_t crcBytes[2] = {input[0], input[1]};
	int crcFromMessage = combineBytes(crcBytes);
	int generated = generateUniTapCrc(actualMessage, sizeOfArray-2);
	return (crcFromMessage == generated);
}

    /**
     * Combine two bytes into one integer
     * @param bytes
     * @return
     */
int combineBytes(uint8_t bytes[]){
	return (int)( (bytes[1] << 8)|(bytes[0])&0xff );
}

    /**
     * Take a message and add a crc of the message to the beginning
     * @param input
     * @return
     */
uint8_t * appendCRCBytes(uint8_t input[], int sizeOfArray){
	static uint8_t output [HCE_MAX_MESSAGE] = {0x00};

	//generate crc of crc-target string
	int crcInt = generateUniTapCrc(input, sizeOfArray);

	//mask out the two important bytes
	
	output[0] = crcInt & 0xFF;
	output[1] = (crcInt >> 8) & 0xFF;

	//now merge those bytes with the original message
		//copy previous message
	for (int i = 0; i<sizeOfArray;i++){
		output[i+2]=input[i];
	}
	//return the concatenated message
	return output;
}

    /**
     * This will take one message (in byte array 1), and append a CRC generated from another (int byte array 2)
     * @param arrayToAppend the byte array to add the CRC to
     * @param arrayToCRC the byte array to generate the CRC from
     * @return the appended array
     */
uint8_t * appendCRCOnDifferentArray(uint8_t appendArray[], int appendArrayLength, uint8_t crcArray[], int crcArrayLength){
	static uint8_t output [HCE_MAX_MESSAGE]= {0X00};
	
	//generate crc of crc-target string
	int crcInt = generateUniTapCrc(crcArray, crcArrayLength);

	//mask out the two important bytes
	uint8_t crcBytes [2] = {};
	crcBytes[1] = (crcInt >> 8) & 0xFF;
	crcBytes[0] = crcInt & 0xFF;

	//now merge those bytes with the original message
	static int outputSize = 0;
	outputSize = appendArrayLength+2;
	
		//copy previous message
	for (int i = 0; i<outputSize;i++){
		output[i+2]=appendArray[0];
	}
		//add crc to message
	output[0] = crcBytes[0];
	output[1] = crcBytes[1];

	//return the concatenated message
	return output;
}