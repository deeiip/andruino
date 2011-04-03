

import sqlite3
import datetime
import time, sys


class AndruinoDb():
    def __init__(self):
        '''
            Connect to the database
        '''
        self.db = web.database(dbn='sqlite', db='andruino.db')
        
        
    def getDeviceById(self, DeviceId):
        '''
            Get device information using id 
        '''
        
        sql = "SELECT * from devices WHERE id = '%s'"
        
        