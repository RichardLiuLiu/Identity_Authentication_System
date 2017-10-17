#ifndef _TEMPEST_H_
#define _TEMPEST_H_

#include <SDL/SDL.h>

int videoinit(int x, int y, int hspan, int pc);
void mkrealsound(double freq, double carrier);
inline void pixelset (const int x, const int y, const Uint32 pixel);
int radiate(char* msg, double carrierfq, int interval, int mode);
int str2data(char* data, char* str);

#endif
