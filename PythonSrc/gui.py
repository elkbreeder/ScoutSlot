import pygame

card_size = (300, 300)
height_interface_bottom = 50
height_interface_top = 100
width_interface_left = 50
width_interface_right = 50

black = (0, 0, 0)
white = (255, 255, 255)
yellow = (255, 203, 4)
blue = (29, 72, 153)
brown = (107, 92, 72)
magenta = (145, 47, 86)


class GUI:
    def __init__(self, game):
        self.font_arial = pygame.font.SysFont("Arial", 30)
        self.font_arial.set_bold(True)
        self.game = game
        self.interface_bottom = self.game.screen.subsurface(
            pygame.Rect(0, self.game.screen.get_height() - height_interface_bottom,
                        self.game.screen.get_width(), height_interface_bottom))
        self.interface_top = self.game.screen.subsurface(
            pygame.Rect(0, 0, self.game.screen.get_width(), height_interface_top))

        '''GameStatesInit'''
        self.show_fps = 0
        self.show_winner = 0

        self.winner_string = "Du hast gewonnen!"

    def show_fps_clicked(self):
        self.show_fps = (self.show_fps + 1) % 2

    def show_winner_window(self, text="Du hast gewonnen!"):
        self.winner_string = text
        self.show_winner = 1

    def hide_winner_window(self):
        self.show_winner = 0

    def draw(self):
        self.interface_bottom.fill(magenta)
        self.interface_top.fill(magenta)
        if self.show_fps:
            text_fps = self.font_arial.render("Fps: " + str('{0:.0f}'.format(self.game.clock.get_fps())),
                                              False, white)
            self.game.screen.blit(text_fps, (0, 0))
        if self.show_winner:
            text_winner = self.font_arial.render(self.winner_string, True, white)
            text_winner_rect = text_winner.get_rect(
                center=[self.interface_top.get_width() // 2,
                        (self.interface_top.get_height() // 2) -5 - 0.5*self.font_arial.get_height()])
            self.game.screen.blit(text_winner, text_winner_rect)  # erste zeile
            if self.game.photo_seconds == 0:
                text_winner = self.font_arial.render("Foto wurde gemacht und an die Bar geschickt", True, white)
            else:
                text_winner = self.font_arial.render("Wir machen ein Foto in "+ str(self.game.photo_seconds)+" Sekunden", True, white)
            text_winner_rect = text_winner.get_rect(
                center=(self.interface_top.get_width() // 2,
                        (self.interface_top.get_height() // 2)+5 + 0.5*self.font_arial.get_height()))
            self.game.screen.blit(text_winner, text_winner_rect)
        text_coins = self.font_arial.render("Coins " + str(self.game.coins), True, white)
        text_coins_rect = text_coins.get_rect(x=0, y=0)
        self.interface_bottom.blit(text_coins, text_coins_rect)
