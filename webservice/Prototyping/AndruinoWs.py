import cherrypy
import sys, os
import datetime
from struct import *
import andruino_api



'''
class SerialMgr:
    # Serial manager
    def open(self):
        # Open serial port connection
    
    def close(self):
        # Close serial port connection
     

class DeviceMgr:
    # class that stores the mapping of each device type
    
'''
class Root:
    # Root node
    @cherrypy.expose
    def index(self):
        return "Welcome to the Andruino application"

class Read:
    # Read from arduinp
    @cherrypy.expose
    def index(self):
        ser.write('r')
        result = ser.readlines()
        return "Command Map -> %s " % (result)
        
class Config:
    @cherrypy.expose
    def index(self,addr,mask):
        #return '\x77\%s\%s' % (addr, mask)
        #ser.write('\x77\%s\%s' % (addr, mask))
	Haddr = pack('B1' , int(addr))
	Hmask = pack('B1', int(mask))
	
	ser.write('\x77%s%s' % (Haddr, Hmask))
        result = ser.readlines()
        return "Application Responded with %s " % (result)
    
class Write:
    @cherrypy.expose
    def index(self,addr,state):
        #return '\x77\%s\%s' % (addr, state)
	Haddr = pack('B1' , int(addr))
	Hstate = pack('B1', int(state))
        ser.write('\x77%s%s' % (Haddr, Hstate))
        result = ser.readlines()
        return "Application Responded with %s " % (result)
        


class Start:
    @cherrypy.expose
    def index(self):
        ser.write('#')
        result = ser.readlines()
        return "Application Responded with %s " % (result)
     
        
    
if __name__ == '__main__':
    # Run this code
    root = Root()
    root.read = Read()
    root.start = Start()
    root.config = Config()
    root.write = Write()

    cherrypy.quickstart(root)

    
'''
Note from -> http://tools.cherrypy.org/wiki/HTTPMethodFiltering

http://helpful.knobs-dials.com/index.php/CherryPy
'''
