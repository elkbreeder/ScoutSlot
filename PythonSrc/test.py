from PythonSrc.game import Game

runs = 100000
wins = 0

game = Game(False)

for i in range(runs):
    arr = game.calc_result()
    if arr[0] == arr[1] == arr[2]:
        wins += 1

print()
print("Runs: " + str(runs))
print("Wins: " + str(wins))
print("wpr: " + str(wins / runs))
print("rpw: " + str(runs / wins))
