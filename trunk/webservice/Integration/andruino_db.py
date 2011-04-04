
import sqlite3
import hashlib
import datetime
import time, sys
import hashlib

class AndruinoDb():
    def __init__(self):
        '''
            Connect to the database
        '''
        self.db_file = 'andruino.db'
        self.db = sqlite3.connect(self.db_file)
        '''
        self.sql = None
        self.columns = None
        '''
        
        
    def _query(self, sql, Columns=None, GetOne=None):
        '''
            Execute the query and return the result set
            
            ~Pre-Conditions~:
                
            Columns set:
                return a dictionary of data
                Otherwise return an array
            GetOne set:
                Return a single row regardless of sql restriction     
        '''
        cursor = self.db.cusor()
        cursor.execute(sql)
        
        if not GetOne:
            '''
                Return all row
            '''
            rows = cursor.fetchall()
            
            
        else:
            '''
                return Single row
            '''
            rows = cursor.fetchone()
        
        if not Columns:
            '''
                return array of results
            '''
            return rows
                
        else: 
            '''
                Return an array of rows indexed as a dictionary 
                based on the listing provided by Columns
            '''
            if not GetOne:
                '''
                    Return array
                '''
                rows_dict = [dict(zip(Columns, row)) for row in rows]
                
            else:
                '''
                    Return single dictionary
                '''
                rows_dict = dict(zip(Columns, rows))
        
            return rows_dict
    
        
    def _exec_sql(self, sql):
        '''
            Perform inserts updates and deletes
            ACTIONS that do no require a return field
            
            ~Post Conditions~
                call commit on database.
        '''
        cursor = self.db.cusor()
        cursor.execute(sql)
        self.db.commit()
        
    def getDeviceById(self, DeviceId):
        '''
            Get device information using id 
        '''
        
        sql = "SELECT * from devices WHERE id = '%s'"  % (DeviceId)
        columns = ['id', 'name', 'port', 'type', 'ts_added', 'ts_updated', 'enabled', 'submit']
        # Not tested 
        result = self._query(sql, columns, True)
        
        
   
   
    def getlogin(self, username, password):
    	'''
    	    Check Login Credentials
    	'''
        pwdhash = hashlib.md5(password).hexdigest()
        sql = """SELECT username 
        FROM users 
        WHERE 
        username='%s'
        AND
        password='%s'
        """ % (username, pwdhash)
        columns=['username']
        
        '''
            Test to see if the database contains this record
            In this case a data dictionary is not requierd.
            So only test for a valid row returned from the database.
            
            
        '''
        result = self._query(sql)
        
        
        

