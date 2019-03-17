import pygame;


class Reel:
    def __init__(self, screen):
        self.screen = screen
        reel = []
        curr = pygame.image.load("../cards200x300/pic1.png")
        reel.append((curr, curr.get_rect()))
        self.reel = reel

    def draw(self):
        for curr in self.reel:
            self.screen.blit(curr[0], curr[1])
