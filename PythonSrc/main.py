import sys
import pygame

from PythonSrc.reel import Reel

pygame.init()
cardSize = (200, 300)
frameSize = (cardSize[1] * 2, 500)
screen = pygame.display.set_mode(frameSize)

reel = Reel(screen)

while 1:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            sys.exit()
    screen.fill((0, 0, 0))  # fill black
    reel.draw()
    pygame.display.flip()
