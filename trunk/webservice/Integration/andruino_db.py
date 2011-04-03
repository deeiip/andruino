

import sqlite3
import datetime
import time, sys


class AndruinoDb():
    def __init__(self):
        '''
            Connect to the database
        '''
        self.db_file = 'andruino.db'
        self.db = sqlite3.connect(self.db_file)
        
        
    def getDeviceById(self, DeviceId):
        '''
            Get device information using id 
        '''
        
        sql = "SELECT * from devices WHERE id = '%s'"
        # get a db cursor
        self.db.cusor()
        
   
    def login(self, username, password):
	'''
	    Check Login Credentials
	'''

	pwdhash = hashlib.md5(password).hexdigest()
	check = db.query("SELECT username FROM users WHERE username='"
		+username+"' AND password='"+pwdhash+"';")

	try:
		return true
	except:
		return false


    def getEmail(self, username):
	'''
	    Check Login Credentials
	'''

	check = db.query("SELECT email FROM users WHERE username='"+wi.username+"';")

       	try:
       		return check[0].email
       	except:
       		return "ERROR"
	
