#include "profile.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>

void init_setting(struct profile *set_info) {
	set_info->LISTEN_PORT=2016;
	set_info->RESX=1366;
	set_info->RESY=768;
//	set_info->RESX=1920;
//	set_info->RESY=1080;
	set_info->TOTALX=1516;
	set_info->TOTALY=918;
//	set_info->TOTALX=2200;
//	set_info->TOTALY=1125;
	set_info->PC=83500000;
//	set_info->PC=148500000;
	set_info->FC=9000000;
	set_info->INTERVAL=1000;
	//strcpy(set_info->SERVER_ADDR,"192.168.0.254");
}

int get_setting(struct profile *set_info) {
	FILE* setfilep=NULL;
	int length;
	char *buf,*substrp;
	char substr[16];
	int i;

	setfilep=fopen(PROFILEPATH, "r");
	if (setfilep==NULL) {
		init_setting(set_info);
		return 1;
	}
	
	fseek(setfilep, 0, SEEK_END);
	length=ftell(setfilep);
	buf=malloc(length);
	fseek(setfilep, 0, SEEK_SET);
	fread(buf, 1, length, setfilep);

	/*substrp=strstr(buf,"SERVER_ADDR=")+strlen("SERVER_ADDR=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D) {
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	strcpy(set_info->SERVER_ADDR,substr);*/

	substrp=strstr(buf,"LISTEN_PORT=")+strlen("LISTEN_PORT=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D) {
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->LISTEN_PORT=atoi(substr);
	
	substrp=strstr(buf,"RESOLUTIONX=")+strlen("RESOLUTIONX=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D) {
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->RESX=atoi(substr);

	substrp=strstr(buf,"RESOLUTIONY=")+strlen("RESOLUTIONY=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->RESY=atoi(substr);
	
	substrp=strstr(buf,"TOTALX=")+strlen("TOTALX=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->TOTALX=atoi(substr);
	
	substrp=strstr(buf,"TOTALY=")+strlen("TOTALY=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->TOTALY=atoi(substr);
	
	substrp=strstr(buf,"PIXELCLOCK=")+strlen("PIXELCLOCK=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->PC=atol(substr);
	
	substrp=strstr(buf,"CARRIER_FREQUENCY=")+strlen("CARRIER_FREQUENCY=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->FC=atol(substr);
	
	substrp=strstr(buf,"INTERVAL=")+strlen("INTERVAL=");
	i=0;
	while (*substrp!=0x0A && *substrp!=0x0D){
		substr[i++]=*substrp;
		substrp++;
	}
	substr[i]='\0';
	set_info->INTERVAL=atoi(substr);

	free(buf);
	fclose(setfilep);
	return 0;
}
