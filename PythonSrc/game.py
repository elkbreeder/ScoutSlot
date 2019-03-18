import sys
import pygame
import random

from PythonSrc import gui
from PythonSrc import model

cardSize = (200, 300)


class Game:
    def __init__(self):
        pygame.init()
        pygame.font.init()

        self.roll_speed_range = (5, 10)

        '''GameInit'''
        self.frame_size = (cardSize[0] * 3 + gui.width_interface_left + gui.width_interface_right,
                           cardSize[1] * 2 + gui.height_interface_bottom + gui.height_interface_top)
        self.screen = pygame.display.set_mode(self.frame_size)
        self.clock = pygame.time.Clock()

        self.interface = gui.GUI(self.screen, self.frame_size, self.clock)

        self.reel = [model.Reel(self.screen, cardSize, gui.width_interface_left),
                     model.Reel(self.screen, cardSize, gui.width_interface_left + cardSize[0]),
                     model.Reel(self.screen, cardSize, gui.width_interface_left + cardSize[0] * 2)]
        self.roll = [0, 0, 0]
        self.roll_speed = [0, 0, 0]
        self.result = [-1, -1, -1]

    def game_loop(self):
        while 1:
            self.event_manager()
            self.draw()
            if all((i == self.result[0] and i is not -1) for i in self.result):
                self.interface.show_winner_window()

    def draw(self):
        self.screen.fill((0, 0, 0))  # fill black
        for i in range(0, len(self.reel)):
            if self.roll[i] > 0:
                self.roll[i] = self.roll[i] - 1
                self.reel[i].move(self.roll_speed[i])
                if self.roll[i] == 0 and (
                        self.frame_size[1] // 5 > self.reel[i].get_current_rect().topleft[1] or
                        self.frame_size[1] * 2 // 5 < self.reel[i].get_current_rect().topleft[
                            1]):  # if the result doesn't look
                    # clear turn the wheel a bit more
                    self.roll[i] = 10
                elif self.roll[i] == 0:
                    self.result[i] = self.reel[i].get_current_id()
                    print(str(i) + ": " + self.reel[i].get_current_id())
            self.reel[i].draw()
        pygame.draw.line(self.screen, (255, 255, 255), (0, self.frame_size[1] // 2),
                         (self.frame_size[0], self.frame_size[1] // 2), 5)
        self.interface.draw()
        pygame.display.flip()
        self.clock.tick(60)

    def event_manager(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_w:  # Start Roll
                    self.interface.hide_winner_window()
                    if all(i == 0 for i in self.roll):
                        self.result[:] = map(lambda _: -1, self.result)
                        self.roll_speed[:] = map(
                            (lambda _: random.randint(self.roll_speed_range[0], self.roll_speed_range[1])),
                            self.roll_speed)
                        self.roll[:] = map((lambda _: random.randint(100, 200)), self.roll)
                elif event.key == pygame.K_F3:  # Show FPS
                    self.interface.show_fps_clicked()
                elif event.key == pygame.K_F4:
                    self.interface.show_winner_window()


g = Game()
g.game_loop()
