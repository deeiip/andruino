'''
    TEST CODE
'''


import os, sys
import serial
import threading
from Queue import Queue
import time
import math
from struct import *




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
        self.ThreadRunStatus = False
        threading.Thread.__init__(self)
        self.StopMe = threading.Event()
        
        # setup the serial port
        self.ser = serial.Serial('/dev/ttyAvr', 115200, timeout=0.25)
        # Wait for the serial post to initialize
        time.sleep(20)
        
        
        '''
            Map Pins to Port Interfaces
        '''        
        self.pin2PortMap = {
            0: 'D',
            1: 'D',
            2: 'D',
            3: 'D',
            4: 'D',
            5: 'D',
            6: 'D',
            7: 'D',
            8: 'B',
            9: 'B',
            10:'B',
            11:'B',
            12:'B',
            13:'B'   
        }
        
        '''
            Binary values for pins
        '''
        self.pin2BinMap = { 
            0:1,
            1:2,
            2:4,
            3:8,
            4:16,
            5:32,
            6:64,
            7:128,
            8:1,
            9:2,
            10:4,
            11:8,
            12:16,
            13:32
        }

        
        
        '''
            Translate read string from avr
            example {0:20,1:0,2:0,10:0,11:0,12:0,20:0,21:1,22:1}
        '''
        
        self.deviceMap = {
            'B': {
                'DDR':None,
                'PIN':None,
                'PORT':None
                  },
            'C': {
                'DDR':None,
                'PIN':None,
                'PORT':None
                  },
            'D': {
                'DDR':None,
                'PIN':None,
                'PORT':None
                  }
        }
        
        self.serialMsg = {
            'ID':None,
            'TYPE': None,
            'DATA': None
        }
        
        

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
            self.ThreadRunStatus = True
            data = self.readAvr()
            self.updateMap(data)
            self.printMap()
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
                    self.parseMsg(msg)
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
        print "Reading Avr "
        self.ser.write('r')
        data = self.ser.readline()
        
        return data.strip()
    
    
    def writeAvr(self, addr, data):
        '''
            Write message to AVR controller
            
        '''
               
        HexAddr = pack('B1', int(addr))
        HexData = pack('B1', int(data))
        
        try:
            ser.write('\x77%s%s' % (HexAddr, HexData))
        except SerialException:
            return None
        
        
    
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
        self.ThreadRunStatus = False
        self.ser.close()
        
    def getStatus(self):
        '''
            Return Thread run status
        '''
        return self.ThreadRunStatus
        
    def updateMap(self, data):
       '''
           Update deviceMap dictionary
       '''
       
       toPort = {'1':'B', '2':'C', '3':'D'}
       toReg = {'0':'DDR', '1':'PORT', '2':'PIN'}
        
       # Check to see if the input is valid
       if ((data[0] == '{') and (data[-1] == '}')):
           print "Have Proper Framing Characters"
           '''
               Parse data from controller
               Example data:
               {10:3F,11:2,12:2,20:0,21:0,22:0,30:0,31:0,32:3}
               
           '''
           for portRegPair in data[1:-1].split(','):
               ''' Un-comment for debugging
                   print "Process Port -> %s" % (portRegPair)
               '''
               portRegData = portRegPair.split(':')
               ''' Un-comment for debugging
               print "Process Reg -> %s" % (portRegData)
               print "Type of data %s" % (type(portRegData[0]))
               print "---High Nibble %s" % (portRegData[0][0])
               print "----Low Nibble %s" % (portRegData[0][1])   
               '''    
               '''
                   Convert Hex data to binary
                   binData - convert hex address into binary string
               '''
               binData = bin(int(portRegData[1], 16))[2:]

               self.deviceMap[ toPort[ portRegData[0][0] ] ][ toReg[ portRegData[0][1] ] ] = binData
       else:
           print "Malformed response from controller"
           return None
           
       
    def getMap(self):
        '''
            Return IO map
        '''
        return self.deviceMap
    
    def printMap(self):
        print "Map: %s " % (self.deviceMap)

    def parseMsg(self, msgData):
        '''
            Parse content of messages on queue
            verify data is correct
            read message request, determine what operation to perform
        '''
        if (msgData['TYPE'] == 'WRITE'):
            '''
                Write operation
            '''
            
            
            
            
        elif (msgData['TYPE'] == 'READ'):
            '''
                Read operation
            '''
            
    
    def getMsgSyntax (self):
        '''
            Return message syntax
        '''
        self.serialMsg['ID'] = "<Message ID>"
        self.serialMsg['TYPE'] = "<Type of request read or write>"
        self.serialMsg['DATA'] = "<Message payload>" 
        
        return self.serialMsg

"""

class AndrEmail(threading.Thread):
    '''
        Email generator thread
    '''
    def __init__(self, SmtpServer=None, EmailAcct=None, EmailPw=None):
        threading.Thread.__init__()
        self.MyEmailAddr = EmailAcct


    def senfMsg(self, Subject='No Subject Set', Addr, msg='Default Message' ):

        format = '''\
To: %s
From: %s
Subject: %s
%s
''' % (Addr, self.MyEmailAddr, Subject, msg)
        
"""

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
        '''
            Run application 
        '''
        #foo.stop()
    except KeyboardInterrupt:
        sys.exit(0)
    
    
'''
cmd line examples

format = """\
To: %s
From: %s
Subject: %s
%s
""" % (

'''
        



    