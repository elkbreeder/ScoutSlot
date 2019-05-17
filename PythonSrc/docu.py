import os
from os import path

PATH = './docu/'
MONEY_FILE = 'count.txt'
WINNER_FILE = 'wins.txt'

if not path.exists(PATH):
    os.makedirs(PATH)

if not path.exists(PATH + MONEY_FILE):
    file = open(PATH + MONEY_FILE, 'w')
    file.write('0')
    file.close()

if not path.exists(PATH + WINNER_FILE):
    file = open(PATH + WINNER_FILE, 'w')
    file.write('0')
    file.close()


def readAsInt(path: str):
    file1 = open(path, 'r')
    s = file1.read()
    file1.close()
    return int(s)


def writeInt(path: str, val: int):
    file2 = open(path, 'w')
    file2.write(str(val))
    file2.close()


def addMoney():
    curr = readAsInt(PATH + MONEY_FILE)
    writeInt(PATH + MONEY_FILE, curr + 1)


def addWin():
    curr = readAsInt(PATH + WINNER_FILE)
    writeInt(PATH + WINNER_FILE, curr + 1)
