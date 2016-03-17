#include <stdint.h>

#define HCE_MAX_MESSAGE 31
#define OVERHEAD 9

/**
 * This will decompile a packet into it's included information.
 * 0                    2             3                      4                     6                   9
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-number | 2 byte phone-id CRC | 3-byte Company id |  <=22 byte message user's id
 * Packet types: (0=Generic, 1=Acknowledgement, 2=Error, 4 = split transfer)
 *							   10                     11
 * If a message is split, then  | Split-transfer-message |
 * 
 * Split messages:
 * Not sure if we will implement the split transfer or take it out before submission.
 * This will allow us to send more complicated/larger streams of bytes in one generic message.
 * 
 * The largest message we will most likely send (in the near future) would be URL that is generated on the fly. This URL would open an anonymous iframe inside of
 * the app which will present ads to the user and thank them for useing the service.
 * Author: Brandon Marino
 */

uint8_t * getSubArray(uint8_t message[], int start, int end);

/**
	Crc starts at 0, ends at 2.
*/
int checkCRC(uint8_t message[], int length){
	int crcResult = 0;
	crcResult = verifyCRC(message, length);
	return crcResult;
}

/**
	Starts at 2, ends at 3
*/
int getType(uint8_t message[]){
	uint8_t * typeBytes = getSubArray(message, 2, 3);
	return typeBytes[0];
}

/**
	Starts at 3, ends at 4
*/
int getPacketNumber(uint8_t message[]){
	uint8_t * pnumBytes = getSubArray(message, 3, 4);
	return pnumBytes[0];
}

/**
	Starts at 4, ends at 6
*/

int getPhoneIdCrc(uint8_t message[]){
	uint8_t * pnumBytes = getSubArray(message, 4, 6);
	int pnumInt = (int)( (pnumBytes[1] << 8)|(pnumBytes[0])&0xff );
	return pnumInt;
}

/**
	Starts at 6, ends at 9
*/
int getCompanyId(uint8_t message[]){
	uint8_t * cidBytes = getSubArray(message, 6, 9);
	int cidInt = (int)( (cidBytes[2] << 16)|(cidBytes[1] << 8)|(cidBytes[0])&0xff );
	return cidInt;
}

/**
	Get the max 22 byte message that starts at byte 9.
*/
uint8_t * getMessage(uint8_t message[], int length){
	static uint8_t output [128]= {};
	
	for (int i = 0; i < HCE_MAX_MESSAGE && i < sizeof(message) && i < length; i++ )
		output[i] = message[i+OVERHEAD];
	
	return output;
}

/**
	
*/
uint8_t * getSubArray(uint8_t message[], int start, int end){
	static uint8_t output [128]= {};
	int length = end-start;
	if(end > sizeof(message) | end > HCE_MAX_MESSAGE | (length > sizeof(output) ))
		return output;
	//need to make sure that it doesnt overflow either the message or the output string
	for (int i = start; i < end ; i++)
		output[i-start] = message[i];

	return output;
}