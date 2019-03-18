import pygame

height_interface_bottom = 100
height_interface_top = 100
width_interface_left = 50
width_interface_right = 50


black = (0, 0, 0)
white = (255, 255, 255)
yellow = (255, 203, 4)
blue = (29, 72, 153)

class GUI:
    def __init__(self, screen, frame_size, clock):
        self.font_arial = pygame.font.SysFont("Arial", 30)
        self.screen = screen
        self.frame_size = frame_size
        self.clock = clock
        self.interface_bottom = pygame.Rect(0, self.frame_size[1] - height_interface_bottom,
                                            self.frame_size[0], height_interface_bottom)
        self.interface_top = pygame.Rect(0, 0, self.frame_size[0], height_interface_top)
        self.winner_box = pygame.Rect(0, 0, 100, 50)
        self.winner_box.center = (self.screen.get_width()//2, self.screen.get_height()//2)#center box on screen
        '''GameStatesInit'''
        self.show_fps = 0
        self.show_winner = 0

    def show_fps_clicked(self):
        self.show_fps = (self.show_fps + 1) % 2

    def show_winner_window(self):
        self.show_winner = 1

    def hide_winner_window(self):
        self.show_winner = 0

    def draw(self):
        pygame.draw.rect(self.screen, yellow, self.interface_bottom)
        pygame.draw.rect(self.screen, yellow, self.interface_top)
        if self.show_fps:
            text_fps = self.font_arial.render("Fps: " + str('{0:.0f}'.format(self.clock.get_fps())),
                                              False, white)
            self.screen.blit(text_fps, (0, 0))
        if self.show_winner:
            pygame.draw.rect(self.screen,blue,self.winner_box)
            text_winner = self.font_arial.render("Winner", True, white)
            text_winner_rect = text_winner.get_rect(center=(self.screen.get_width()//2, self.screen.get_height()//2))
            self.screen.blit(text_winner, text_winner_rect)
