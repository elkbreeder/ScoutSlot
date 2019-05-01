from threading import Thread
import RPi.GPIO as GPIO
import time

try:
    from PythonSrc import game
except:
    import game


class CoinThread(Thread):
    def __init__(self, game):
        super().__init__()
        self.counterPin = 18
        self.game = game
        self.gpioConfig()
        self.end = False
        self.start()

    def add(self, i):
        self.game.coin_add(i)

    def gpioConfig(self):
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.counterPin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    def run(self):
        while not self.end:
            input_state = GPIO.input(self.counterPin)
            if not input_state:
                self.add(1)
                time.sleep(0.07)
            else:
                time.sleep(0.02)


    def quit(self):
        self.end = True

