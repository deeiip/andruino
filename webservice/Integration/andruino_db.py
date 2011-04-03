
import sqlite3
import hashlib
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

	sql = "SELECT username FROM users WHERE username='"\
		+username+"' AND password='"+pwdhash+"';"

	dbc = self.db.cursor()
	check = dbc.execute(sql)
	try:
		return True
	except:
		return False

    def getEmail(self, username):
	'''
	    Check Login Credentials
	'''

	sql = "SELECT email FROM users WHERE username='"+wi.username+"';"

	dbc = self.db.cursor()
	check = dbc.execute(sql)

       	try:
       		return check[0].email
       	except:
       		return "ERROR"
	
