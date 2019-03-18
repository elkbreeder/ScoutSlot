import pygame

card_size = (200, 300)
height_interface_bottom = 100
height_interface_top = 100
width_interface_left = 50
width_interface_right = 50

black = (0, 0, 0)
white = (255, 255, 255)
yellow = (255, 203, 4)
blue = (29, 72, 153)


class GUI:
    def __init__(self, game):
        self.font_arial = pygame.font.SysFont("Arial", 30)
        self.game = game
        self.interface_bottom = self.game.screen.subsurface(
            pygame.Rect(0, self.game.screen.get_height() - height_interface_bottom,
                        self.game.screen.get_width(), height_interface_bottom))
        self.interface_top = self.game.screen.subsurface(
            pygame.Rect(0, 0, self.game.screen.get_width(), height_interface_top))
        self.winner_box = pygame.Rect(0, 0, 100, 50)
        # center box on screen
        self.winner_box.center = (self.game.screen.get_width() // 2, self.game.screen.get_height() // 2)

        '''GameStatesInit'''
        self.show_fps = 0
        self.show_winner = 0
        self.showed_coins = 0
        self.showed_coins_won = 0

    def show_fps_clicked(self):
        self.show_fps = (self.show_fps + 1) % 2

    def show_winner_window(self):
        self.show_winner = 1

    def hide_winner_window(self):
        self.show_winner = 0

    def draw(self):
        self.interface_bottom.fill(yellow)
        self.interface_top.fill(yellow)
        if self.show_fps:
            text_fps = self.font_arial.render("Fps: " + str('{0:.0f}'.format(self.game.clock.get_fps())),
                                              False, white)
            self.game.screen.blit(text_fps, (0, 0))
        if self.show_winner:
            pygame.draw.rect(self.game.screen, blue, self.winner_box)
            text_winner = self.font_arial.render("Winner", True, white)
            text_winner_rect = text_winner.get_rect(
                center=(self.game.screen.get_width() // 2, self.game.screen.get_height() // 2))
            self.game.screen.blit(text_winner, text_winner_rect)
        text_coins = self.font_arial.render("Coins " + str(self.showed_coins), True, blue)
        text_coins_won = self.font_arial.render("Coins won: " + str(self.showed_coins_won), True, blue)
        text_coins_rect = text_coins.get_rect(x=0, y=0)
        text_coins_won_rect = text_coins_won.get_rect(x=0, y=self.interface_bottom.get_height() // 2)
        self.interface_bottom.blit(text_coins, text_coins_rect)
        self.interface_bottom.blit(text_coins_won, text_coins_won_rect)
