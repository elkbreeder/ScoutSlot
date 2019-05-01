import pygame
import pygame.camera
import os


class Camera:
    def __init__(self):
        if os.name is 'nt':  #
            print("Windows doesn't support camera functionality")
            return
        self.cam = pygame.camera.Camera("/dev/video0", (640, 480))

    def capture(self, path):
        if os.name is 'nt':  #
            print("Windows doesn't support camera functionality")
            return
        pygame.camera.init()

        self.cam.start()
        img = self.cam.get_image()
        pygame.image.save(img, "filename.jpg")

        self.cam.stop()

    def capture_next_winner(self):
        print("NOT IMPLEMENTED")


if __name__ == '__main__':
    camera = Camera()
    camera.capture('test.jpg')
