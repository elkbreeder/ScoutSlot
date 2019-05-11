import pygame
import pygame.camera
import datetime
import os


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
        pygame.image.save(img, now.strftime("%m.%d %H.%M.%S") + ".jpg")
        self.cam.stop()

    def capture_next_winner(self):
        # print("NOT IMPLEMENTED")
        self.capture("~/images/")


if __name__ == '__main__':
    camera = Camera()
    camera.capture('test.jpg')
