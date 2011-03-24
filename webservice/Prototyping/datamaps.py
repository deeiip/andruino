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
               
        if (device_type == 'arduino'):
           loadAvr168()
        else:
            return None  

            
        
        
    def loadAvr168(self):
        
        deviceMap['IOMap'] = {
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
        deviceMap['AddrMap'] = { 
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
        