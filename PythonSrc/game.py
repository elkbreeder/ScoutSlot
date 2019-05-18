import os
import random
import sys
from datetime import datetime, timedelta
from threading import Lock

import pygame

try:
    import gui, model, cam, docu
except:
    from PythonSrc import gui, cam, model, docu

NO_RESULT = -1
PHOTOCOUNTER = pygame.USEREVENT + 1
roll_speed_range = (50, 100)
roll_range = (5, 8)
fps = 24
photo_seconds = 3
ROLL_COST = 2
lastfuckupsound = datetime.now() - timedelta(seconds=5)


# 1024x 768
# 300

class Game:
    def __init__(self, isdebug):
        pygame.init()
        pygame.font.init()
        pygame.mixer.init()
        self.is_running = False
        self.to_move = [0, 0, 0]
        self.isdebug = isdebug
        '''GameInit'''
        frame_size = (gui.card_size[0] * 3,
                      gui.card_size[1] * 2 + gui.height_interface_bottom + gui.height_interface_top)  # init frame
        print(frame_size)
        if os.uname().nodename == 'raspberrypi' and not self.isdebug:
            self.screen = pygame.display.set_mode(frame_size,
                                                  pygame.FULLSCREEN | pygame.HWSURFACE | pygame.DOUBLEBUF)  # display frame ,pygame.FULLSCREEN
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
        self.coins = 1000

        pygame.mouse.set_visible(False)

        self.photo_seconds = 0
        self.camera = cam.Camera()
        if os.uname().nodename == 'raspberrypi':  # first check if the os is windows(windows doesn't provide uname) | os.name is not 'nt' and
            print('CoinThread started')
            from coins import CoinThread
            from trigger import TriggerThread
            self.triggerThread = TriggerThread(self)
            self.coinThread = CoinThread(self)

    def game_loop(self):
        last_img = [self.reel[0].get_current_id(),
                    self.reel[1].get_current_id(),
                    self.reel[2].get_current_id()]

        while 1:  # endless loop
            self.event_manager()  # manage events
            if all(i == 0 for i in self.roll) and all(
                    i == 0 for i in self.to_move) and self.is_running:  # if all rolls are stopped
                self.sound_chatter.stop()
                if all((j == self.result[0] and j is not NO_RESULT) for j in
                       self.result):
                    self.win()
                self.is_running = False
            for i in range(0, len(self.reel)):  # loop over all reels
                if (self.roll[i] <= 0 and self.to_move[i] == 0) or self.result[
                    i] == NO_RESULT:  # skip if roll doesn't run
                    self.roll[i] = 0
                    continue
                if self.to_move[i] > 0:
                    if self.to_move[i] / self.roll_speed[i] >= 1.0:
                        self.reel[i].move(self.roll_speed[i])
                        self.to_move[i] -= self.roll_speed[i]
                    else:
                        self.reel[i].move(self.to_move[i])
                        self.to_move[i] = 0
                    continue
                if self.reel[i].get_current_id() == self.result[i] and \
                        self.reel[i].get_current_id() != last_img[i]:
                    self.roll[i] -= 1
                    if self.roll[i] == 0:
                        self.to_move[i] = int(0.5 * gui.card_size[1]) + gui.card_size[1] - \
                                          self.reel[i].get_current_rect().bottomleft[1]
                        if self.to_move[i] / self.roll_speed[i] >= 1.0:
                            self.reel[i].move(self.roll_speed[i])
                            self.to_move[i] -= self.roll_speed[i]
                        else:
                            self.reel[i].move(self.to_move[i])
                            self.to_move[i] = 0
                        continue

                last_img[i] = self.reel[i].get_current_id()
                self.reel[i].move(self.roll_speed[i])  # move roll
            self.draw()
            self.clock.tick(fps)

    def win(self):
        self.sound_win.play()  # player won
        self.photo_seconds = photo_seconds
        pygame.time.set_timer(PHOTOCOUNTER, 1000)
        self.interface.show_winner_window()
        self.result[:] = map(lambda _: NO_RESULT, self.result)
        self.roll[:] = map(lambda _: 0, self.roll)

    def draw(self):
        self.screen.fill((0, 0, 0))  # fill black
        for i in range(0, len(self.reel)):
            self.reel[i].draw()
        pygame.draw.line(self.reel_screen, gui.white, (0, gui.card_size[1] // 2),
                         (self.reel_screen.get_width(), gui.card_size[1] // 2), 8)
        pygame.draw.line(self.reel_screen, gui.white, (0, 3 * gui.card_size[1] // 2),
                         (self.reel_screen.get_width(), 3 * gui.card_size[1] // 2), 8)
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
                    self.coin_add(100)
                elif event.key == pygame.K_ESCAPE:
                    sys.exit()
                elif event.key == pygame.K_F6:
                    self.reel[0].move(100)
            if event.type == PHOTOCOUNTER:
                print(self.photo_seconds)
                if self.photo_seconds == 0:
                    pygame.time.set_timer(PHOTOCOUNTER, 0)
                    self.camera.capture_next_winner()
                    self.interface.hide_winner_window()
                else:
                    self.photo_seconds -= 1

    def start_roll(self):
        if all(i == 0 for i in self.roll) and self.photo_seconds <= 0 and all(i == 0 for i in self.to_move) \
                and not self.is_running:  # if no reel runs
            self.interface.hide_winner_window()
            if self.coins < ROLL_COST:
                global lastfuckupsound
                if datetime.now() - lastfuckupsound > timedelta(milliseconds=2000):
                    self.sound_no_money.play(maxtime=400)
                    lastfuckupsound = datetime.now()
                return
            self.sound_chatter.play(-1)  # start rolling
            self.coin_add(-ROLL_COST)
            self.result = self.calc_result()
            print(self.result)
            self.roll_speed[:] = map(
                (lambda _: random.randint(roll_speed_range[0], roll_speed_range[1])),
                self.roll_speed)
            self.roll[:] = map((lambda _: random.randint(roll_range[0], roll_range[1])), self.roll)
            self.is_running = True

    def calc_result(self):
        # Gregor move
        # return map(lambda _: random.randint(0, model.Reel.card_count - 1), self.result)

        WIN_CHANCE = 0  # runs per win

        def calc_nowin():
            r = list(map(lambda _: random.randint(0, model.Reel.card_count - 1), self.result))
            if len(r) < 3:
                print("ERROR FUCK!!!")
            elif r[0] == r[1] == r[2]:
                return calc_nowin()
            return r

        random.seed(os.urandom(10))
        win = (random.randint(0, WIN_CHANCE - 1) == 0)
        if win:
            winval = random.randint(0, model.Reel.card_count - 1)
            docu.addWin()
            return [winval, winval, winval]
        else:
            return calc_nowin()

    def exit(self):
        if hasattr(self, 'coinThread'):
            self.coinThread.quit()
            self.triggerThread.quit()
            self.camera.exit()
        sys.exit()

    def coin_add(self, coins):
        if coins > 0:
            for i in range(coins):
                docu.addMoney()
        self.coinLock.acquire()
        self.coins += coins
        self.interface.showed_coins = self.coins
        self.coinLock.release()


if __name__ == '__main__':
    print()
    g = Game(sys.argv.__contains__("--debug"))
    g.game_loop()  # start game loop
