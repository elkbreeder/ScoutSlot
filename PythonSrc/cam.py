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
        print(1)
        if not os.path.exists(path):
            print(2)
            os.mkdir(path)
            print(3)
        pygame.image.save(img, path + now.strftime("%m.%d %H.%M.%S") + ".jpg")
        print(4)
        self.cam.stop()

    def capture_next_winner(self):
        # print("NOT IMPLEMENTED")
        try:
            self.capture("./images/")
        except Exception as e:
            print(e)


if __name__ == '__main__':
    camera = Camera()
    camera.capture('test.jpg')
