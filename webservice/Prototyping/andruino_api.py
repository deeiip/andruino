'''
    API Provides the following:
    1.) Define API for interacting with shared database resource...
    2.) Interface for andruino services

Arduino Pin Map

0 = Port


'''
import sqlite3
import datetime
from Queue import Queue
from andruino_services import *
import time



sql = sqlite3.connect('Arduino.db')



class AndruinoApi():
    def __init__(self):
        '''
            Queue interfaces for messaging
        '''
        self.serialQueue = Queue(0)
        self.emailQueue = Queue(0)
        self.serialThread = None
        self.emailThread = None

        
        '''
            Map Pins to Port Interfaces
        '''        
        self.pin2PortMap = {
            '0': 'D',
            '1': 'D',
            '2': 'D',
            '3': 'D',
            '4': 'D',
            '5': 'D',
            '6': 'D',
            '7': 'D',
            '8': 'B',
            '9': 'B',
            '10':'B',
            '11':'B',
            '12':'B',
            '13':'B'   
        }
        
        '''
            Binary values for pins
        '''
        self.pin2BinMap = { 
            '0':'1',
            '1':'2',
            '2':'4',
            '3':'8',
            '4':'16',
            '5':'32',
            '6':'64',
            '7':'128',
            '8':'1',
            '9':'2',
            '10':'4',
            '11':'8',
            '12':'16',
            '13':'32'
        }




        
    def startSerial(self):
        self.serialThread = AndrSerial(self.serialQueue)
        self.serialThread.start()
        
    def stopSerial(self):
        self.serialThread.stop()
        
        
    def setOutput(self, pinNumber, pinState):
        '''
            This method is used to set 
            Set ouput state of a pin attached to the avr controller
            Pin Numbers = 2-13 (0 & 1 reserved for serial communication)
            Pin State = 0 or 1 
            THIS SECTION SUPPORTS PORT INTERFACE ONLY (Change IO) 
        '''
        '''
            Cross reference from Ports to Hex commands
        '''
        PortMap = {
            'B':'11',
            'C':'21',
            'D':'31',
        }
        
        

        

    def isOutput(self):
        '''
            Verify pin is an output
        '''
        
    
    def configOutput(self):
        '''
            
        '''
        DDRMap = {
            'B':'10',
            'C':'20',
            'D':'30',
        }    




if __name__ == '__main__':
    '''
        seed the database
    '''
    foo = AndruinoApi()
    foo.startSerial()
    
    for x in range(1,50):
        print "wait # %s" % (x)
        time.sleep(1.25)

    foo.stopSerial()
