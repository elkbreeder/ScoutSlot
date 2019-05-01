import sys
import pygame
import random

try:
    import gui
    import model
except:
    from PythonSrc import gui
    from PythonSrc import model

NO_RESULT = -1
roll_speed_range = (50, 100)
roll_range = (50, 100)
fps = 25


class Game:
    def __init__(self):
        pygame.init()
        pygame.font.init()
        pygame.mixer.init()

        '''GameInit'''
        frame_size = (gui.card_size[0] * 3 + gui.width_interface_left + gui.width_interface_right,
                      gui.card_size[1] * 2 + gui.height_interface_bottom + gui.height_interface_top)  # init frame
        self.screen = pygame.display.set_mode(frame_size)  # display frame ,pygame.FULLSCREEN
        self.clock = pygame.time.Clock()
        self.sound_no_money = pygame.mixer.Sound("../sounds/error0.wav")
        self.sound_chatter = pygame.mixer.Sound("../sounds/chattering_teeth.wav")
        self.sound_win = pygame.mixer.Sound("../sounds/win.wav")  # init all sound effects
        self.interface = gui.GUI(self)  # init GUI
        reel_rect = pygame.Rect(gui.width_interface_left, gui.height_interface_top, gui.card_size[0] * 3,
                                gui.card_size[1] * 2)
        self.reel_screen = self.screen.subsurface(reel_rect)  # define reel subsurface
        self.reel = [model.Reel(self.reel_screen, 0),
                     model.Reel(self.reel_screen, gui.card_size[0]),
                     model.Reel(self.reel_screen, gui.card_size[0] * 2)]  # init all reels
        self.roll = [0, 0, 0]
        self.roll_speed = [0, 0, 0]
        self.result = [NO_RESULT, NO_RESULT, NO_RESULT]
        self.extra_rolls = 50

        self.current_extra_rolls = 0
        self.coins = 0
        self.coins_won = 0

    def game_loop(self):
        while 1:  # endless loop
            self.event_manager()  # manage events
            if all(i == 0 for i in self.roll):  # if all rolls are stopped
                self.sound_chatter.stop()
            for i in range(0, len(self.reel)):  # loop over all reels
                if self.roll[i] == 0:  # skip if roll doesn't run
                    continue
                self.roll[i] -= 1
                self.reel[i].move(self.roll_speed[i])  # move roll
                if self.current_extra_rolls < self.extra_rolls and self.roll[i] == 0 and (
                        self.reel[i].get_current_rect().topleft[1] < self.reel_screen.get_height() // 7 or
                        self.reel[i].get_current_rect().topleft[1] > self.reel_screen.get_height() * 2 // 7):
                    # if the result doesn't look clear, turn the wheel a bit more but max self.extra_rolls times
                    # per spin
                    self.roll[i] = 1
                    self.current_extra_rolls += 1
                elif self.roll[i] == 0:
                    self.result[i] = self.reel[i].get_current_id()  # save the result if the current roll stops
            self.draw()
            if all((j == self.result[0] and j is not NO_RESULT) for j in
                   self.result):  # if all reels are stopped and show the same
                self.sound_win.play()  # player won
                self.interface.show_winner_window()
                self.coins_won += 10
                self.interface.showed_coins_won = self.coins_won
                self.result[:] = map(lambda _: NO_RESULT, self.result)
            self.clock.tick(fps)

    def draw(self):
        self.screen.fill((0, 0, 0))  # fill black
        for i in range(0, len(self.reel)):
            self.reel[i].draw()
        pygame.draw.line(self.screen, (255, 255, 255), (0, self.screen.get_height() // 2),
                         (self.screen.get_width(), self.screen.get_height() // 2), 5)
        self.interface.draw()
        pygame.display.flip()

    def event_manager(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_w:  # Start Roll
                    self.start_roll()
                elif event.key == pygame.K_F3:  # Show FPS
                    self.interface.show_fps_clicked()
                elif event.key == pygame.K_F4:  # Show Winner Window
                    self.interface.show_winner_window()
                elif event.key == pygame.K_F5:
                    self.coins_inserted(10)
                elif event.key == pygame.K_ESCAPE:
                    sys.exit()

    def start_roll(self):
        self.interface.hide_winner_window()
        if all(i == 0 for i in self.roll):  # if no reel runs
            self.sound_chatter.play(-1)  # start rolling
            self.current_extra_rolls = 0
            if self.coins == 0:
                self.sound_no_money.play(maxtime=400)
                return
            self.coins = self.coins - 1
            self.interface.showed_coins = self.coins
            self.result[:] = map(lambda _: NO_RESULT, self.result)
            self.roll_speed[:] = map(
                (lambda _: random.randint(roll_speed_range[0], roll_speed_range[1])),
                self.roll_speed)
            self.roll[:] = map((lambda _: random.randint(roll_range[0], roll_range[1])), self.roll)

    def coins_inserted(self, coins):
        self.coins += coins
        self.interface.showed_coins = self.coins


g = Game()
g.game_loop()  # start game loop
