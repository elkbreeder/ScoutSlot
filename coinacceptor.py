import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
counterPin=12
GPIO.setup(counterPin, GPIO.IN, pull_up_down=GPIO.PUD_UP)
i = 0
while True:
    input_state = GPIO.input(counterPin)
    if input_state == False:
        i +=1
        print(i)
        time.sleep(0.02)
    #else:
        #time.sleep(0.01)
    
        
