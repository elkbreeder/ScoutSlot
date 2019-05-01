import pygame
import pygame.camera

def capture(path):
    pygame.camera.init()
    cam = pygame.camera.Camera("/dev/video0",(640,480))
    cam.start()
    img = cam.get_image()
    pygame.image.save(img, "filename.jpg")

if __name__ == '__main__':
    capture('test.jpg')