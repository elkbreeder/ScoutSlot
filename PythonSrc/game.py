import os
import random
import sys
from threading import Lock

import pygame

try:
    import gui, model, cam
except:
    from PythonSrc import gui, cam, model

NO_RESULT = -1
PHOTOCOUNTER = pygame.USEREVENT + 1
roll_speed_range = (50, 100)
roll_range = (50, 100)
fps = 24
photo_seconds = 3
ROLL_COST = 2


# 1024x 768
# 300

class Game:
    def __init__(self, isdebug):
        pygame.init()
        pygame.font.init()
        pygame.mixer.init()
        self.isdebug = isdebug
        '''GameInit'''
        frame_size = (gui.card_size[0] * 3,
                      gui.card_size[1] * 2 + gui.height_interface_bottom + gui.height_interface_top)  # init frame
        print(frame_size)
        if os.uname().nodename == 'raspberrypi' and not self.isdebug:
            self.screen = pygame.display.set_mode(frame_size, pygame.FULLSCREEN | pygame.HWSURFACE | pygame.DOUBLEBUF)  # display frame ,pygame.FULLSCREEN
        else:
            self.screen = pygame.display.set_mode(frame_size)  # display frame ,pygame.FULLSCREEN
        self.clock = pygame.time.Clock()
        self.sound_no_money = pygame.mixer.Sound("../sounds/error0.wav")
        self.sound_chatter = pygame.mixer.Sound("../sounds/chattering_teeth.wav")
        self.sound_win = pygame.mixer.Sound("../sounds/win.wav")  # init all sound effects
        self.interface = gui.GUI(self)  # init GUI
        reel_rect = pygame.Rect(0, gui.height_interface_top, gui.card_size[0] * 3,
                                gui.card_size[1] * 2)
        self.reel_screen = self.screen.subsurface(reel_rect)  # define reel subsurface
        self.reel = [model.Reel(self.reel_screen, 0),
                     model.Reel(self.reel_screen, gui.card_size[0]),
                     model.Reel(self.reel_screen, gui.card_size[0] * 2)]  # init all reels
        self.roll = [0, 0, 0]
        self.roll_speed = [0, 0, 0]
        self.result = [NO_RESULT, NO_RESULT, NO_RESULT]
        self.extra_rolls = 50

        self.coinLock = Lock()
        self.current_extra_rolls = 0
        self.coins = 0

        self.photo_seconds = 0
        self.camera = cam.Camera()
        if os.uname().nodename == 'raspberrypi':  # first check if the os is windows(windows doesn't provide uname) | os.name is not 'nt' and
            print('CoinThread started')
            from coins import CoinThread
            from trigger import TriggerThread
            self.triggerThread = TriggerThread(self)
            self.coinThread = CoinThread(self)

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
                self.win()
            self.clock.tick(fps)

    def win(self):
        self.sound_win.play()  # player won
        self.photo_seconds = photo_seconds
        pygame.time.set_timer(PHOTOCOUNTER, 1000)
        self.interface.show_winner_window()
        self.result[:] = map(lambda _: NO_RESULT, self.result)

    def draw(self):
        self.screen.fill((0, 0, 0))  # fill black
        for i in range(0, len(self.reel)):
            self.reel[i].draw()
        pygame.draw.line(self.screen, (255, 255, 255), (0, self.screen.get_height() // 2),
                         (self.screen.get_width(), self.screen.get_height() // 2), 5)
        self.interface.draw()
        pygame.display.update()

    def event_manager(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_w:  # Start Roll
                    self.start_roll()
                elif event.key == pygame.K_ESCAPE:
                    self.exit()
                elif event.key == pygame.K_F3:  # Show FPS
                    self.interface.show_fps_clicked()
                elif event.key == pygame.K_F4:  # Show Winner Window
                    self.win()
                elif event.key == pygame.K_F5:
                    self.coin_add(1)
                elif event.key == pygame.K_ESCAPE:
                    sys.exit()
            if event.type == PHOTOCOUNTER:
                if self.photo_seconds == 0:
                    pygame.time.set_timer(PHOTOCOUNTER, 0)
                    self.camera.capture_next_winner()
                    self.interface.hide_winner_window()
                else:
                    self.photo_seconds -= 1

    def start_roll(self):
        if all(i == 0 for i in self.roll) and self.photo_seconds <= 0:  # if no reel runs
            self.interface.hide_winner_window()
            self.current_extra_rolls = 0
            self.sound_chatter.play(-1)  # start rolling
            if self.coins < ROLL_COST:
                self.sound_no_money.play(maxtime=400)
                return

            self.coin_add(-ROLL_COST)
            self.result[:] = map(lambda _: NO_RESULT, self.result)
            self.roll_speed[:] = map(
                (lambda _: random.randint(roll_speed_range[0], roll_speed_range[1])),
                self.roll_speed)
            self.roll[:] = map((lambda _: random.randint(roll_range[0], roll_range[1])), self.roll)

    def exit(self):
        if hasattr(self, 'coinThread'):
            self.coinThread.quit()
            self.triggerThread.quit()
            self.camera.exit()
        sys.exit()

    def coin_add(self, coins):
        self.coinLock.acquire()
        self.coins += coins
        self.interface.showed_coins = self.coins
        self.coinLock.release()


if __name__ == '__main__':
    print()
    g = Game(sys.argv.__contains__("--debug"))
    g.game_loop()  # start game loop
