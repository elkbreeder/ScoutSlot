import sys
import pygame
import random

from PythonSrc.reel import Reel

cardSize = (200, 300)
roll_offset = 0
roll_speed_range = (5, 10)

pygame.init()

frameSize = (cardSize[0] * 3, cardSize[1] * 2)
screen = pygame.display.set_mode(frameSize)


reel = [Reel(screen, cardSize, roll_offset),
        Reel(screen, cardSize, roll_offset + cardSize[0]),
        Reel(screen, cardSize, roll_offset + cardSize[0] * 2)]
roll = [0, 0, 0]
roll_speed = [0, 0, 0]
result = [-1, -1, -1]
while 1:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            sys.exit()
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_w:
                if all(i == 0 for i in roll):
                    result[:] = map(lambda _: -1, result)
                    roll_speed[:] = map(
                        (lambda _: random.randint(roll_speed_range[0], roll_speed_range[1])), roll_speed)
                    roll[:] = map((lambda _: random.randint(100, 200)), roll)
    screen.fill((0, 0, 0))  # fill black
    for i in range(0, len(reel)):
        if roll[i] > 0:
            roll[i] = roll[i] - 1
            reel[i].move(roll_speed[i])
            if roll[i] == 0 and (
                    frameSize[1] // 5 > reel[i].get_current_rect().topleft[1] or
                    frameSize[1] * 2 // 5 < reel[i].get_current_rect().topleft[1]):  # if the result doesn't look
                # clear turn the wheel a bit more
                roll[i] = 10
            elif roll[i] == 0:
                result[i] = reel[i].get_current_id()
                print(str(i) + ": " + reel[i].get_current_id())
        reel[i].draw()
    if all((i == result[0] and i is not -1) for i in result):
        print("Winner")

    pygame.draw.line(screen, (255, 255, 255), (0, frameSize[1] // 2), (frameSize[0], frameSize[1] // 2), 5)
    pygame.display.flip()
