import pygame
import pygame.camera
import os
def capture(path):
    if os.name is 'nt': #
        print("Windows doesn't support camera functionality")
        return
    pygame.camera.init()
    cam = pygame.camera.Camera("/dev/video0",(640,480))
    cam.start()
    img = cam.get_image()
    pygame.image.save(img, "filename.jpg")

if __name__ == '__main__':
    capture('test.jpg')