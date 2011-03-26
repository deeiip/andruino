'''
    Provide data and cross reference mappings for AVR devices
    
'''

class deviceMap():
    '''
        Class 
        
    '''
    def __init__(self, device_type=None):
        '''
            Save current map type
        '''
        self.deviceMap = {}
        self.AvrMap = {}
        
               
        if (device_type == 'arduino'):
           self.loadAvr168()
        else:
            return None  
        
            
        
        
    def loadAvr168(self):
        
        '''
            Defines data dictionary for storing AVR config data
        '''
        """
        self.AvrMap = {
            'B': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'Dec_PORT':None
                  },
            'C': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'Dec_PORT':None
                  },
            'D': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
                'Dec_PORT':None
                  }
        }
        """
        self.AvrMap = {
            'B': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
            },
            'C': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
            },
            'D': {
                'DDR':None,
                'PIN':None,
                'PORT':None,
            }
        }
        self.deviceMap['DDRMap'] = {
            'B':'16', # hex 10
            'C':'32',
            'D':'48'                          
        }
        self.deviceMap['PORTMap'] = {
            'B':'17',
            'C':'33',
            'D':'49'                          
        }
        
        self.deviceMap['IOMap'] = {
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
        self.deviceMap['AddrMap'] = { 
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
    
    
    def getMap(self):
        ''' 
            Return avr dictionary map
        '''
        return self.AvrMap
    
       
    def updateMap(self, data):
       '''
           Update deviceMap dictionary
       '''
       
       toPort = {'1':'B', '2':'C', '3':'D'}
       toReg = {'0':'DDR', '1':'PORT', '2':'PIN'}
        
       # Check to see if the input is valid
       if ((data[0] == '{') and (data[-1] == '}')):
           '''
               Parse data from controller
               Example data:
               {10:3F,11:2,12:2,20:0,21:0,22:0,30:0,31:0,32:3}
               
           '''
           for portRegPair in data[1:-1].split(','):
               '''
               print "Process Port -> %s" % (portRegPair)
               '''
               portRegData = portRegPair.split(':')
               '''
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
               
               ''' Store data values in data dictionary '''
               self.AvrMap[ toPort[ portRegData[0][0] ] ][ toReg[ portRegData[0][1] ] ] = binData
               ''' Save PORT information is decimal format '''
               #print "Low Nibble is what %s " % (portRegData[0][1])
               """
               if (portRegData[0][1] == '1'): 
                   '''
                       Only capture integer value for PORT data
                   '''
                   decData = int(portRegData[1], 16)
                   self.AvrMap[ toPort[ portRegData[0][0] ] ][ 'Dec_PORT' ] = decData
               """
       else:
           print "Malformed response from controller"
           return None


    def pinToMap(self, AvrReq):
        ''' 
            Translate a pin assignment to an AVR address and byte setting
            Pin - AVR Pin Number
            State - On or Off
        '''
        '''
            'DATA': '11:1'
        '''
        data = AvrReq['DATA'].split(':')
        '''
            Get the register number
        '''
        
        PortAddr = self.deviceMap['IOMap'][int(data[0])]
        PortBinVal = self.AvrMap[PortAddr]['PORT']
        ReqVal = self.deviceMap['AddrMap'][int(data[0])]
        
        PortDecVal = int(portRegData[1], 16)
        
        print "Current BIN= %s :: DEC= %s :: REQ_VAL %s" % (PortBinVal, ReqVal) 
        
        
        '''
            Get Operation
        '''
        if int(data[1]) == 1:
            '''
                Add value
            '''
            print "OPERATION = ADD"
            NewVal = PortVal + ReqVal
             
            
        else: 
            '''
                Subtract value
            '''
            print "OPERATION = SUBTRACT"
            if PortVal > ReqVal:
                '''
                    Prevent writing negative value to register
                '''
                NewVal = PortVal - ReqVal
            else:
                NewVal = PortVal 
        
        
        print "New Value = %s" % (NewVal)
        
        if AvrReq['TYPE'] == 'WRITE':
            '''
                Write an IO bit to the controller
                Prior to changing the bit inspect the condition of other 
                outputs
            '''
            
            print "Write To PORT = %s , INT_VAL -> %s, NewVal = %s " % (PortAddr, self.deviceMap['PORTMap'][PortAddr], NewVal)    
            convert = {'ADDR': self.deviceMap['PORTMap'][PortAddr], 'VALUE': NewVal }
            return convert
        elif AvrReq['TYPE'] == 'CFG':
            print "Write To DDR = %s , INT_VAL -> %s, NewVal = %s  " % (PortAddr, self.deviceMap['DDRMap'][PortAddr], NewVal)     
            convert = {'ADDR': self.deviceMap['DDRMap'][PortAddr], 'VALUE': NewVal }
            return convert
    