#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>

#include "profile.h"
#include "tempest.h"

#define MAXREQUESTNUM 4
#define INVALID_SOCKET -1

int main(int argc, char *argv[]) {
	
	int i,j=0;

	int socklistener,sockreceiver[MAXREQUESTNUM];
	struct sockaddr_in server_addr, client_addr;
	int addrlen=sizeof(struct sockaddr_in);
	char recvbuf[256];
	int recvlen,result;
	struct profile setinfo;
	
	result=get_setting(&setinfo);
	if (result) printf("cannot find setting file, use default parameters.\n");
	else printf("use parameters in setting file.\n");
	printf("listen port: %d\n", setinfo.LISTEN_PORT);
	printf("resolution: %d*%d\n", setinfo.RESX, setinfo.RESY);
	printf("real resolution: %d*%d\n", setinfo.TOTALX, setinfo.TOTALY);
	printf("pixel clock: %dHz\n", (int)setinfo.PC);
	printf("carrier frequency: %dHz\n", (int)setinfo.FC);
	printf("message interval: %dms\n", (int)setinfo.INTERVAL);
	
	result=videoinit(setinfo.RESX, setinfo.RESY, setinfo.TOTALX, setinfo.PC);
	
	socklistener=socket(AF_INET, SOCK_STREAM, 0);
	fcntl(socklistener, F_SETFL, O_NONBLOCK);
	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family=AF_INET;
//	server_addr.sin_addr.s_addr=htonl("192.168.142.131");
	server_addr.sin_addr.s_addr=htonl(INADDR_ANY);
	server_addr.sin_port=htons(setinfo.LISTEN_PORT);
	
	result=bind(socklistener, (struct sockaddr *)&server_addr, sizeof(server_addr));
	if (result) {
		printf("error: bind socket failed. error code=%d\n",result);
		return 1;
	}
	result=listen(socklistener, MAXREQUESTNUM);
	if (result) {
		printf("error: cannot set socket to listen.\n");
		return 1;
	}
		
	printf("wait for client connect...\n");
	
	fd_set readfd;
	struct timeval timeout;
	int reqnum=0;
	int maxfd,ret;
	char *strp;
	struct Command{
		char head[7];
		char type;
		char msg[24];
	} command;
	
	maxfd=socklistener+1;
	for (i=0;i<MAXREQUESTNUM;i++) sockreceiver[i]=0;	
	
	while(1) {
		timeout.tv_sec=16;
		timeout.tv_usec=0;
		FD_ZERO(&readfd);
		FD_SET(socklistener, &readfd);		
		for (i=0;i<MAXREQUESTNUM;i++) 
			if (sockreceiver[i]>0) FD_SET(sockreceiver[i], &readfd);
		
//		ret=select(maxfd, &readfd, NULL, NULL, &timeout);
		
//		if (ret<0) printf("error: select return value %d.\n",ret);
//		else if (ret>0) {
			
			if (reqnum < MAXREQUESTNUM && FD_ISSET(socklistener, &readfd)) {
				for (i=0;i<MAXREQUESTNUM;i++)
					if (sockreceiver[i]==0) {
						sockreceiver[i]=accept(socklistener, (struct sockaddr*)&client_addr, &addrlen);
						if (sockreceiver[i]==INVALID_SOCKET) {
							sockreceiver[i]=0;
							break;
						}
						else {
							reqnum++;
							if (maxfd<sockreceiver+1) maxfd=sockreceiver+1;
							printf("new client connect. total=%d\n", reqnum);
							break;
					}
			} else for (i=0;i<MAXREQUESTNUM;i++) {
				if (sockreceiver[i]>0 && FD_ISSET(sockreceiver[i], &readfd)){
					recvlen=recv(sockreceiver[i], recvbuf, sizeof(recvbuf), 0);
					if (recvlen<=0) {
						printf("warning: recvlen=%d, ignore the message.\n",recvlen);
						continue;
					}
					recvbuf[recvlen]='\0';
//					if (memcmp(recvbuf, "GET", 3)) {
//						printf("get unrelated info: %s\n", recvbuf);
//						continue;
//					}
					strp=strstr(recvbuf, "TEMPEST");
					if (strp==NULL) {
						printf("get unrelated info: %s\n", recvbuf);
						continue;
					}
					else 
						strcpy((char*)&command, strp);
					
					
					switch (command.type) {
					case 'q':	
						close(sockreceiver[i]);
						sockreceiver[i]=0;
						reqnum--;
						printf("get quit command, remain %d socket.\n",reqnum);
						if (reqnum) {
							maxfd=socklistener+1;
							for (j=0;j<MAXREQUESTNUM;j++)
								if (maxfd<sockreceiver[j]+1) maxfd=sockreceiver[j]+1;
						}
						else {
							printf("no active socket remain, exit.\n");
							return 0;
						}
							
					case 'm':
						printf("socket get info: %s\n",command.msg);
						break;
						
					case 'p':
						printf("radiate message: %s\n", command.msg);
						result=radiate(command.msg, setinfo.FC, setinfo.INTERVAL, 0);
						switch(result) {
						case 1:
							printf("radiate process stoped by click.\n");
							break;
						case -2:
							printf("error: couldn't set video mode, %s\n", SDL_GetError());
							return 1;
						case -1:
							printf("error: couldn't initialize SDL, %s\n", SDL_GetError());
							return 1;
						default:
							printf("radiate process complete.\n");
						}
						break;
						
					case 'r':
						printf("loop message: %s\n", command.msg);
						result=radiate(command.msg, setinfo.FC, setinfo.INTERVAL, 1);
						switch(result) {
						case 1:
							printf("radiate process stoped by click.\n");
							break;
						case -2:
							printf("error: couldn't set video mode, %s\n", SDL_GetError());
							return 1;
						case -1:
							printf("error: couldn't initialize SDL, %s\n", SDL_GetError());
							return 1;
						default:
							printf("radiate process complete.\n");
						}
						break;
						
					default:
						printf("socket get unknown command: %c\n", command.type);
					}
				}	
			}
		}
//	}
	}
	return 0;
}
	
