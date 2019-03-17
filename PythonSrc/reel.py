import pygame;


class Reel:
    def __init__(self, screen):
        self.reelPosition = 0
        self.screen = screen
        reel = []
        for i in range(0, 4):
            curr = pygame.image.load("../cards200x300/pic" + str(i) + ".png")
            currRect = curr.get_rect()
            currRect = currRect.move((0, -currRect.height * i))
            reel.append([curr, currRect])

        self.reel = reel

    def draw(self):
        for curr in self.reel:
            self.screen.blit(curr[0], curr[1])

    def move(self, y):
        for curr in self.reel:
            curr[1].move_ip((0, y))
        reelPositionRect = (self.reel[self.reelPosition])[1]
        if reelPositionRect.bottomleft[1] > reelPositionRect.height * 2:  # if currentreelposition leaves screen

            self.reelPosition = (self.reelPosition + 1) % len(self.reel)  # update reel Position
            reelPositionRect = (self.reel[self.reelPosition])[1]
            newPosition = reelPositionRect.topleft[1] - reelPositionRect.height  # determine where the 2nd reel after
            # the current reel position at the beginning has to be placed to make the reel goes round and round
            movePositionRect = (self.reel[(self.reelPosition + 1) % len(self.reel)])[1]
            movePositionRect.y = newPosition  # place the 2nd reel
