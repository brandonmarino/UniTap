#include "CRC16.c"

int generateUniTapCrc (uint8_t input[], int sizeOfArray);

int verifyCRC (uint8_t input[], int sizeOfArray);

uint8_t* appendCRCBytes(uint8_t input[], int sizeOfArray);

uint8_t* appendCRCOnDifferentArray(uint8_t appendArray[], int appendArrayLength, uint8_t crcArray[], int crcArrayLength);