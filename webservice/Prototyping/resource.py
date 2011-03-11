'''
    TEST CODE
'''


import os, sys
import serial
import threading
from Queue import Queue
import time
import math




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
        
        self.ReadSleepTime = 10
        self.QueuePollInterval = 0.5
        self.serialQueue = SerialInterfaceQueue
        '''
            Initialize this thread
        '''
        self.ThreadRunState = 0
        threading.Thread.__init__(self)
        self.StopMe = threading.Event()
        # setup the serial port
        self.ser = serial.Serial('/dev/ttyAvr', 115200, timeout=0.25)
        # Wait for the serial post to initialize
        time.sleep(20)
        

    def run(self):
        '''
            Start the thread
        '''
        self.ThreadRunState = 1
        self.initAvr()
        while self.ThreadRunState:
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
            waitTime = math.ceil(self.ReadSleepTime / self.QueuePollInterval)
            waitTime = int(waitTime)
            print "Going to scan %s times" % (str(waitTime))
            for s in range(1 , waitTime):
                msg = self.getMsg() 
                if msg != None:
                    '''
                        Do something if a message is on the queue
                    '''
                    print "Got a message -> %s " % (msg)
                    
                time.sleep(self.QueuePollInterval)
                    
            #time.sleep(self.ReadSleepTime)
 
        self.cleanup()
        
        
        
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
        
        
    def stop(self):
        '''
            Stop the tread and close out reasources...
        '''
        self.ThreadRunState = 0
    
    def cleanup(self):
        '''
            Clean up open connections prior to exiting
        '''
        self.ser.close()
        
        
if __name__ == '__main__':
    '''
        Run some tests
    '''
    try:
        bar = Queue(0)
        foo = AndrSerial(bar)
        foo.start()
        ''' 
            Wait 5 minutes, then publish a message to the queue
        '''
        time.sleep(45)
        bar.put('Hello')
        time.sleep(30)
        foo.stop()
    except KeyboardInterrupt:
        sys.exit(0)
    
    
    
        
        



    