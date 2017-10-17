#include <SDL/SDL.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

SDL_Surface *screen;

int main() {
	int resx=1366,resy=768;
	int i;
	
	if ( SDL_Init(SDL_INIT_VIDEO) < 0 ) {
		printf("error: couldn't initialize SDL, %s\n", SDL_GetError());
		exit(1);
	};	
	screen = SDL_SetVideoMode(resx, resy, 8, SDL_SWSURFACE|SDL_ANYFORMAT|SDL_FULLSCREEN);
	if ( screen == NULL ) {
		printf("error: couldn't set video mode, %s\n", SDL_GetError());
		exit(1);
	}
	printf("have Set %d bits-per-pixel mode\n", screen->format->BitsPerPixel);
	for (i=0;i<5;i++) {
		SDL_Event event;
		while (SDL_PollEvent(&event)) 
			if (event.type == SDL_MOUSEBUTTONDOWN) {
				SDL_FreeSurface(screen);
				printf("exit manually.\n");
				system("PAUSE");
				exit(1);
			}
		sleep(1);
	}
	SDL_Quit();
	return 0;
}
