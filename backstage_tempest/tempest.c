#include <math.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <unistd.h>
#include <stdint.h>

#include "tempest.h"

SDL_Surface *screen;
int resx, resy, horizontalspan;
double pixelclock;
Uint32 white, black;

int videoinit(int x, int y, int hspan, int pc) {
	resx=x; 
	resy=y;
	horizontalspan=hspan;
	pixelclock=pc;
	
	return 0;
}

inline void pixelset (const int x, const int y, const Uint32 pixel)
{
	Uint8 *bits=((Uint8*)screen->pixels)+y*screen->pitch+x*screen->format->BytesPerPixel;
	Uint8 r, g, b;
	
	switch(screen->format->BytesPerPixel) {
	case 1:
		*((Uint8 *)(bits)) = (Uint8)pixel;
		break;
	case 2:
		*((Uint16 *)(bits)) = (Uint16)pixel;
		break;
	case 3:
		/* Format/endian independent */
		r = (pixel>>screen->format->Rshift)&0xFF;
		g = (pixel>>screen->format->Gshift)&0xFF;
		b = (pixel>>screen->format->Bshift)&0xFF;
		*((bits)+screen->format->Rshift/8) = r; 
		*((bits)+screen->format->Gshift/8) = g;
		*((bits)+screen->format->Bshift/8) = b;
		break;
	case 4:
		*((Uint32 *)(bits)) = (Uint32)pixel;
		break;
	};
};

void mkrealsound(double freq, double carrier)
{
	SDL_Rect rect;
	rect.x=rect.y=0;
	rect.w=resx;
	rect.h=resy;
	SDL_FillRect(screen,&rect,black);

	int x, y;
	double t;

	double ftfp2 = freq / pixelclock * 2.0;
	double fcfp2 = carrier / pixelclock * 2.0;
/*
	t = 0;
	for (y=0; y<resy; y++) {
		if (((int)(t*ftfp2))%2) {
			for (x=0; x<resx; x++) {
				if (((int)(t*fcfp2))%2)
					pixelset(x,y,white);
				t++;
			}
		}
		else {
			t+=resx;
		}
	}

*/

	t = 0;
	for (y = 0; y < resy; y++) {
		if (((int)(t*ftfp2))%2) {
			// 1
			t+=resx;
		} else {
			// 0
			for (x = 0; x < resx; x++) {
				if (((int)(t*fcfp2))%2)
					pixelset(x,y,white);
				t++;
			};
		};

		t += horizontalspan - resx;
	};

}

int str2data(char* data, char* str) {
	int i=0,j=0,num=0;
		while (str[i]!='\0') {
			if (str[i]==' ') {
				data[j++]=num;
				num=0;
//				printf("%d\n",data[j-1]);
			}
			else {
				num*=10;
				num+=str[i]-'0';
			}
			i++;
		}
		if (num!=0)
			data[j++]=num;
		return j;
}

void encode(char* temp,char* msg) {
	char codeX[3]="16";
	char codeY[3]="17";
	char codeZ[3]="18";
	int i=0;
	char code[256];
	int len0=strlen(msg);
	for(i=0;i<len0;i++) {
		*(code+5*i)=*(msg+i);
		*(code+5*i+1)=' ';
		*(code+5*i+2)=codeZ[0];
		*(code+5*i+3)=codeZ[1];
		*(code+5*i+4)=' ';
	}

	int len1=strlen(code);
	int shift=0;
	for(i=0;i<len1;i++) {
		shift=i+3;
		temp[shift]=code[i];
	}
	int start=0;
//	for(i=0;i<2*2;i+=2) {
//		temp[i]='0';
//		temp[i+1]=' ';
//	}	
	temp[start+0]=codeX[0];  temp[start+1]=codeX[1];
	temp[start+2]=' ';

	temp[len0*5+start+3]=codeY[0];  temp[len0*5+start+4]=codeY[1];
	temp[len0*5+start+5]=' ';
	temp[len0*5+start+6]='\0';
/*
	for(i=0;i<len1;i++)
		temp[i]=code[i];
*/
//	printf("%d\n",len1);
/*
	int shift=0;
	for(i=0;i<len1;i++) {
		shift=i+15;
		temp[shift]=code[i];
	}
	int start=6;
	for(i=0;i<2*3;i+=2) {
		temp[i]='0';
		temp[i+1]=' ';
	}	
	temp[start+0]=codeX[0];  temp[start+1]=codeX[1];
	temp[start+2]=' ';
	temp[start+3]=codeY[0];  temp[start+4]=codeY[1];
	temp[start+5]=' ';
	temp[start+6]=codeX[0];  temp[start+7]=codeX[1];
	temp[start+8]=' ';

	temp[len0*5+start+9]=codeY[0];  temp[len0*5+start+10]=codeY[1];
	temp[len0*5+start+11]=' ';
	temp[len0*5+start+12]=codeX[0];  temp[len0*5+start+13]=codeX[1];
	temp[len0*5+start+14]=' ';
	temp[len0*5+start+15]=codeY[0];  temp[len0*5+start+16]=codeY[1];
	temp[len0*5+start+17]=' ';
	temp[len0*5+start+18]='\0';
*/
//	printf("%s\n",temp);
}

int radiate(char* msg, double carrierfq, int interval, int mode) {
	char radibuf[32];
	char code[256];
	int i,len,k;
	struct timeval timer;
	SDL_Rect rect;
	encode(code,msg);
	len=str2data(radibuf, code);
//	for(k=0;k<len;k++)
//		printf("%s",radibuf[k]);
//	printf("\n");
	rect.x=rect.y=0;
	rect.w=resx;
	rect.h=resy;
	if ( SDL_Init(SDL_INIT_VIDEO) < 0 ) return -1;
	screen = SDL_SetVideoMode(resx, resy,8, SDL_SWSURFACE|SDL_ANYFORMAT|SDL_FULLSCREEN);
	if ( screen == NULL ) {
		SDL_Quit();
		return -2;
	}
	//printf("have Set %d bits-per-pixel mode\n", screen->format->BitsPerPixel);
	
	SDL_Color col[2];
	col[0].r=col[0].g=col[0].b=0xff; // white
	col[1].r=col[1].g=col[1].b=0x00; // black
	SDL_SetColors(screen,col,0,2);
	white = SDL_MapRGB(screen->format,0xff,0xff,0xff);
	black = SDL_MapRGB(screen->format,0x00,0x00,0x00);
	
	do {
		for (i=0;i<len;i++) {
			SDL_Event event;
			while (SDL_PollEvent(&event)) 
				if (event.type == SDL_MOUSEBUTTONDOWN) {
					SDL_Quit();
					return 1;
				}
			double voicefreq;
//			voicefreq=800.0+radibuf[i]*50.0;
//			mkrealsound(voicefreq, carrierfq);
			mkrealsound(pow(2.0,(radibuf[i]+2)/12.0)*440.0*2, carrierfq);
			SDL_UpdateRect(screen,0,0,resx,resy);
			timer.tv_sec=interval/1000;
			timer.tv_usec=(interval%1000)*1000;
			select(0, NULL, NULL, NULL, &timer);
		}

		SDL_FillRect(screen,&rect,black);
		SDL_UpdateRect(screen,0,0,resx,resy);
	} while (mode);
	
	SDL_Quit();
	return 0;
}
