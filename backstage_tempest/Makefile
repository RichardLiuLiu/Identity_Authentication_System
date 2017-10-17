main: main.c tempest.o profile.o
	cc -g -o main main.c profile.o tempest.o -lm `sdl-config --cflags --libs`
	
sender: sender.c
	cc -g -o sender sender.c
	
test: test.c
	cc -g -o test test.c `sdl-config --cflags --libs`



profile.o: profile.c profile.h
	cc -g -c profile.c
tempest.o: tempest.c tempest.h
	cc -g -c tempest.c `sdl-config --cflags --libs`

	
clean:
	rm *.o test main sender
