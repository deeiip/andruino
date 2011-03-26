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
        self.AvrMap = {
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
        self.deviceMap['DDRMap'] = {
            'B':'10',
            'C':'20',
            'D':'30'
                                    
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
        return self.AvrMap
    
       
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

               self.AvrMap[ toPort[ portRegData[0][0] ] ][ toReg[ portRegData[0][1] ] ] = binData
               self.AvrMap[ toPort[ portRegData[0][0] ] ][ 'D_PORT' ] = decData
       else:
           print "Malformed response from controller"
           return None




    