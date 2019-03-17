import sys
import pygame
import random

from PythonSrc.reel import Reel
cardSize = (200, 300)
roll_offset = 0
roll_speed_range = (5, 10)

pygame.init()

frameSize = (cardSize[0] * 2, cardSize[1] * 3)
screen = pygame.display.set_mode(frameSize)

reel = [Reel(screen, cardSize, roll_offset), Reel(screen, cardSize, roll_offset + cardSize[0])]
roll = [0, 0]
roll_speed = [0, 0]

while 1:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            sys.exit()
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_w:
                if all(i == 0 for i in roll):
                    roll[:] = map((lambda _: random.randint(100, 200)), roll)
                    roll_speed[:] = map((lambda _: random.randint(roll_speed_range[0], roll_speed_range[1])), roll_speed)
    screen.fill((0, 0, 0))  # fill black
    for i in range(0, len(reel)):
        if roll[i] > 0:
            roll[i] = roll[i] - 1
            reel[i].move(roll_speed[i])
        reel[i].draw()
    pygame.display.flip()
