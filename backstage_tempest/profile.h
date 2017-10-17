#ifndef _PROFILE_H_
#define _PROFILE_H_

#define PROFILEPATH "./setting.ini"

struct profile {
	//char SERVER_ADDR[16];
	int LISTEN_PORT;
	int RESX;
	int RESY;
	int TOTALX;
	int TOTALY;
	double PC;
	double FC;
	int INTERVAL;
};

void init_setting(struct profile *set_info);
int get_setting(struct profile *set_info);	//return error code

#endif
