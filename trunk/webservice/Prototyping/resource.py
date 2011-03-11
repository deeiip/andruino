'''
    TEST CODE
'''


import os, sys
import serial
import threading
import Queue
import time




class AndrSerial(threading.Thread):
    
    def __init__(self, SerialInterfaceQueue):
        ''' 
            Setup serial interface
            
            readSleepInterval - This is the amount of time the thread will wait before 
                            sending a read instruction to the AVR and return the result.
                            Each read operation will pull the state of the device from the AVR
                            and store the response in the database. 
                            In debug mode, thread will display raw output to the screen.
            QueuePollInterval - Amount of time tread will wait before checking for messages 
                            on the serialInterfaceQueue...
        
            
        '''
        
        self.ReadSleepTime = 20
        self.QueuePollInterval = 0.5
        self.serialQueue = SerialInterfaceQueue
        '''
            Initialize this thread
        '''
        
        threading.Thread.__init__(self)
        # setup the serial port
        self.ser = serial.Serial('/dev/ttyAvr', 115200, timeout=0.25)
        # Wait for the serial post to initialize
        time.sleep(20)
        

    def run(self):
        '''
            Start the thread
        '''
        self.initAvr()
        while 1:
            '''
                do this alot
            '''
            print "Reading Avr "
            self.ser.write('r')
            data = self.readAvr()
            print "AVR status = %s" % (data)
            
            '''
                Sleep for a period of time before starting up again...
            '''
            for s in range(1 , (self.ReadSleepTime / self.QueuePollInterval)):
                if self.getMsg() != None:
                    '''
                        Do something if a message is on the queue
                    '''
                    
            time.sleep(self.ReadSleepTime)
        
    def initAvr(self):
        '''
            Put arduino in run mode
        '''
        print "waking up avr"
        self.ser.write('#')
        data = self.readAvr()
        print "Arduino Said %s" % (data)
        
    def readAvr(self):
        '''
            Read data from arduino
        '''
        data = self.ser.readline()
        return data
    
    
    def getMsg(self):
        '''
            Check for requests on the command queue
            If a message is found then read the data and 
            pass the message back
        '''
        
        if self.serialQueue.empty():
            '''
                If there are no instructions found waiting on the queue
                then return None. 
            '''
            return None
        else: 
            return self.serialQueue.get()
    
        
if __name__ == '__main__':
    '''
        Run some tests
    '''
    bar = Queue(0)
    foo = AndrSerial()
    foo.start(bar)
    
    
        
        



    