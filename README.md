# ScoutSlot
ScoutSlot is a slot machine game made with Python and PyGame.
It is optimized to run on a Raspberry Pi 3.
A coin acceptor can be connected to GPIO pin 18 to validate cash (coins).
The game then converts the payment into in-game tokens.

## Example Setup

Our slot machine setup consists of the following parts:
1. Raspberry Pi 3
2. Screen (4:3)
3. Coin acceptor (a device which is able to distinguish different kinds of coins; Used in vending machines for example)
4. Lever with an attached gyro sensor (the gyro sensor allows the program to detect when the lever is pulled)
5. Webcam (connected to the raspi)
6. External laptop for later winner-validation

The game can be set up for a party, where a player pays for games, but if he wins he doesn't get money, but a free drink at the bar. The bar can validate the wins by an external laptop.
1. By inserting money, the player gains credits, which can be used to play
2. The player pulls the lever to play
3. If all three wheels show the same image the player won
4. Then a picture is taken using the webcam and sent to the receiver program on the same network
5. The player goes to the bartender, who can see the image of him on his laptop and gives him a free beer
6. Then the barkeeper marks the picture as "done" to prevent people from cheating


## Setup
1. Add a cards300x300 folder with 4 pictures called pic1.png,...,pic4.png with a size of 300x300 each.
2. Install `pygame`
3. run `game.py` with `python3`

## Technical Details
The code for the slot machine is located in the "PythonSrc" folder whilst the code for the external picture-verification computer is in the "JavaSrc" folder.
As the name suggests you need python to run the game and java to run the "picview" program.
The "picview" program uses sftp to retrieve the pictures from the slot machine. You will need to customize some of the variables in [JavaSrc/picview/Vars.java](https://github.com/elkbreeder/ScoutSlot/blob/master/JavaSrc/picview/Vars.java).

