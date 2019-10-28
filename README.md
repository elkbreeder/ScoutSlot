# ScoutSlot
ScoutSlot is a Slotmachine Game made with Python and PyGame. It is optimized to run on a Raspberry Pi 3.
It can be used in combination with an coin acceptor connected to GPIO pin 18.

This program is used in combination with a selfmade Slotmachine, which runs a Raspberry Pi 3.
It can be setup for a party, where a player pays for games, but if he wins he doesn't get money, but a free drink at the bar.
The Slot machine consits of following parts:
1. Raspberry Pi
2. Screen
3. Coin Acceptor (A Device which is able to distinguish different Coins. Used in vending machines for example)
5. Lever with an attached Gyrosensor (The gyro sensor allows the program to detect if the Lever is moved)
6. Webcam

## Example Situation
+Through inserting money, the player gets credits, which can be used to play
+The Player pulls the lever to play
+If all three wheels show the same image the Player won
+Then a Picture is taken using the webcam and sent to the reciever program on the same network
+The bar has a computer up and running the reciever program
+The Player goes to the Bartender, which see the image of him and give him a free beer


## Setup
1. add a cards300x300 folder with 4 pictures called pic1.png,...,pic4.png with a size of 300x300 each.
2. install pygame
3. run game.py with python 3

