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
            Translate read string from avr
            example {0:20,1:0,2:0,10:0,11:0,12:0,20:0,21:1,22:1}
        '''
        
        self.deviceMap = {
            'B': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'D_PORT':None
                  },
            'C': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'D_PORT':None
                  },
            'D': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'D_PORT':None
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
        
        '''
            Thread looping finished 
            Clean up and exit program
        '''
        self.cleanup()
        

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
               decData = int(portRegData[1], 16)

               self.deviceMap[ toPort[ portRegData[0][0] ] ][ toReg[ portRegData[0][1] ] ] = binData
               self.deviceMap[ toPort[ portRegData[0][0] ] ][ 'D_PORT' ] = decData
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
        print "Parsing message %s" % (msgData)
        if (msgData['TYPE'] == 'WRITE'):
            '''
                Write operation
            '''
            print "Write Message = %s" %(msgData)
            
            if (msgData['STATE'] == 0 ):
                '''
                    Turn off IO
                '''
                
            elif (msgData['STATE'] == 1 ):
                '''
                    Turn On IO
                '''
            
            
            
        elif (msgData['TYPE'] == 'READ'):
            '''
                Read operation
            '''
            print "Write Message = %s" %(msgData)
    
    


       
        
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
        '''
            Remove trailing line feed carriage return 
        '''
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
            '''
                If a message is available on the Queue remove the data 
                pass it to the calling request
            '''
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
        



    