#include "../CRC/CRC16.h"
#include "Encapsulation.c"
#include "DeEncapsulation.c"


//Encapsulation
uint8_t * encapsulate(uint8_t message[], int messageLength, int type, int messageNumber, int companyId);

//DeEncapsulation
int checkCRC(uint8_t message[], int length);

int getType(uint8_t message[]);

int getPacketNumber(uint8_t message[]);

int getPhoneIdCrc(uint8_t message[]);

int getCompanyId(uint8_t message[]);

uint8_t * getMessage(uint8_t message[], int length);