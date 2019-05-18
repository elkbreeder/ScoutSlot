import datetime
import os

import pygame
import pygame.camera


class Camera:
    def __init__(self):
        pygame.camera.init()
        if os.name == 'nt':  #
            print("Windows doesn't support camera functionality")
            return
        self.cam = pygame.camera.Camera("/dev/video0", (640, 480))

    def capture(self, path):
        if os.name == 'nt':  #
            print("Windows doesn't support camera functionality")
            return
        self.cam.start()
        img = self.cam.get_image()
        now = datetime.datetime.now()
        if not os.path.exists(path):
            os.mkdir(path)
        pygame.image.save(img, path + now.strftime("%y-%b-%d_%H:%M:%S") + ".jpg")
        self.cam.stop()
        #print("File: +" str(os.path.isfile(path)))

    def capture_next_winner(self):
        try:
            self.capture("./images/")
        except Exception as e:
            print(e)

    def exit(self):
        return
        #try:
        #except Exception as e:
        #    print(e)


if __name__ == '__main__':
    camera = Camera()
    camera.capture('../img/')
