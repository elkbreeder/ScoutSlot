import sys
import pygame
import random
import math
from PythonSrc.reel import Reel


class Game:
    def __init__(self):
        pygame.init()
        pygame.font.init()

        self.cardSize = (200, 300)
        self.roll_offset = 0
        self.roll_speed_range = (5, 10)

        '''GameStatesInit'''
        self.show_fps = 0
        '''GameInit'''
        self.frameSize = (self.cardSize[0] * 3, self.cardSize[1] * 2)
        self.screen = pygame.display.set_mode(self.frameSize)
        self.clock = pygame.time.Clock()
        self.font_arial = pygame.font.SysFont("Arial", 30)

        self.reel = [Reel(self.screen, self.cardSize, self.roll_offset),
                     Reel(self.screen, self.cardSize, self.roll_offset + self.cardSize[0]),
                     Reel(self.screen, self.cardSize, self.roll_offset + self.cardSize[0] * 2)]
        self.roll = [0, 0, 0]
        self.roll_speed = [0, 0, 0]
        self.result = [-1, -1, -1]

    def game_loop(self):
        while 1:
            self.event_manager()
            self.draw()
            if all((i == self.result[0] and i is not -1) for i in self.result):
                print("Winner")

    def draw(self):
        self.screen.fill((0, 0, 0))  # fill black
        for i in range(0, len(self.reel)):
            if self.roll[i] > 0:
                self.roll[i] = self.roll[i] - 1
                self.reel[i].move(self.roll_speed[i])
                if self.roll[i] == 0 and (
                        self.frameSize[1] // 5 > self.reel[i].get_current_rect().topleft[1] or
                        self.frameSize[1] * 2 // 5 < self.reel[i].get_current_rect().topleft[
                            1]):  # if the result doesn't look
                    # clear turn the wheel a bit more
                    self.roll[i] = 10
                elif self.roll[i] == 0:
                    self.result[i] = self.reel[i].get_current_id()
                    print(str(i) + ": " + self.reel[i].get_current_id())
            self.reel[i].draw()
        pygame.draw.line(self.screen, (255, 255, 255), (0, self.frameSize[1] // 2),
                         (self.frameSize[0], self.frameSize[1] // 2), 5)
        if self.show_fps:
            text_fps = self.font_arial.render("Fps: " + str('{0:.0f}'.format(self.clock.get_fps())),
                                              False, (255, 255, 255))
            self.screen.blit(text_fps, (0, 0))
        pygame.display.flip()
        self.clock.tick(60)


    def event_manager(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_w:  # Start Roll
                    if all(i == 0 for i in self.roll):
                        self.result[:] = map(lambda _: -1, self.result)
                        self.roll_speed[:] = map(
                            (lambda _: random.randint(self.roll_speed_range[0], self.roll_speed_range[1])),
                            self.roll_speed)
                        self.roll[:] = map((lambda _: random.randint(100, 200)), self.roll)
                elif event.key == pygame.K_F3:  # Show FPS
                    self.show_fps = (self.show_fps + 1) % 2


g = Game()
g.game_loop()
