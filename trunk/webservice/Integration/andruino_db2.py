import sqlite3
import hashlib
import datetime
import time, sys
import hashlib
from datetime import datetime

class AndruinoDb():
    def __init__(self):
        '''
            Connect to the database
        '''
        self.db_file = 'andruino.db'
        self.db = sqlite3.connect(self.db_file, check_same_thread = False)
        
        '''
            Set the database API to return dictionary of column names to data rows
        '''
        self.db.row_factory = sqlite3.Row
        '''
        self.sql = None
        self.columns = None
        '''
        self.pl = {'name':'NewDevice_001', 'port':'/dev/ttyNewDev_001', 'type':'arduino','enabled':'1', 'submit':'This is Submit'} 
        
    
    def initDB(self):
        '''
            Execute SQL to inintialize DB
        '''
        sql = []
        sql.append("""
        CREATE TABLE "devices" (
        "id" integer primary key, 
        "name" varchar(100) not null, 
        "port" varchar(100) not null, 
        "type" integer not null, 
        "ts_added" datetime default current_timestamp, 
        "ts_updated" datetime default current_timestamp, 
        "enabled" integer not null
        );
        """)
        sql.append( """
        CREATE TABLE "details" (
        "id" integer primary key, 
        "device_id" integer not null references "devices" ("id"), 
        "label" varchar(100) not null, 
        "config" integer not null, 
        "pin" integer not null, 
        "value" integer not null, 
        "ts_value" datetime default current_timestamp,  
        "ts_output" datetime default current_timestamp, 
        "enabled" integer not null
        );
        """)
        sql.append( """
        CREATE TABLE "sessions" (
            "session_id" char(128) UNIQUE NOT NULL,
            "atime" NOT NULL default current_timestamp,
            "data" text
        );
        """)
        sql.append( """
        CREATE TABLE "users" (
        "id" integer primary key autoincrement,
        "username" varchar(32) not null,
        "password" varchar(32) not null,
        "email" varchar(64) not null
        );
        """)
        sql.append("""
        CREATE TABLE "statusreg" (
        "device_id" integer not null references "devices" ("id"), 
        "ts_value" datetime default current_timestamp
        );
        """)
        sql.append("""
        CREATE TABLE "rules" (
        "device_id" integer not null references "devices" ("id"), 
        "value" integer not null
        );
        """)
        
        
        sql.append("""
        INSERT INTO "users" VALUES (NULL,"default","5f4dcc3b5aa765d61d8327deb882cf99","broken@email.addr");
        """)
                
        sql.append("""
         INSERT INTO "users" VALUES (NULL,"matt","5f4dcc3b5aa765d61d8327deb882cf99","matt@email.addr");
        """)
        for stmt in sql:
            self.exec_sql(stmt)
        
    def reinitDB(self):
        '''
            Drop all tables and re-initialize DB
        '''
        sql = []
        sql.append("""
        drop table if exists "devices";
        """)
        sql.append("""
        drop table if exists "details";
        """)
        sql.append("""
        drop table if exists "sessions";
        """)
        sql.append("""
        drop table if exists "users";
        """)
        sql.append("""
        drop table if exists "rules";
        """)
        sql.append("""
        drop table if exists "statusreg";
        """)
        for stmt in sql:
            self.exec_sql(stmt)
        '''
            Call the database creation function
        '''
        self.initDB()
        
    
    def query(self, sql):
        '''
            
        '''
        cursor = self.db.cursor()
        result = cursor.execute(sql)
        return result
    
    
    
    def exec_sql(self, sql):
        '''
            Perform inserts updates and deletes
            ACTIONS that do no require a return field
            
            ~Post Conditions~
                call commit on database.
        '''
        cursor = self.db.cursor()
        cursor.execute(sql)
        self.db.commit()
        
    def getDeviceById(self, DeviceId):
        '''
            Get device information using id 
        '''
        
        sql = "SELECT * from devices WHERE id = '%s'"  % (DeviceId)
        # Not tested 
        result = self.query(sql)
        
        return result
        
        
    def setDevice(self, dataset):
        '''
            Dataset is a dictionary of data elements device table
        '''
        sql = """INSERT INTO devices 
        (name, port, type, enabled, submit) 
        VALUES 
        ('%s', '%s', '%s', '%s', '%s')""" % (dataset['name'], dataset['port'], dataset['type'], dataset['enabled'], dataset['submit'])
        self.exec_sql(sql)
   

    def getlogin(self, username, password):
        ''' 
            Check Login Credentials
        '''
        pwdhash = hashlib.md5(password).hexdigest()
        sql = """SELECT COUNT(username) AS count
        FROM users 
        WHERE 
        username='%s'
        AND
        password='%s'
        """ % (username, pwdhash)
    
        ''' 
            Test to see if the database contains this record
            In this case a data dictionary is not requierd.
            So only test for a valid row returned from the database.
        '''
        result = self.query(sql)
        ''' 
           Get a single row from the database 
           Pass back a dictionary object for the row
        '''
        login = result.next()
    
        if login['count'] == 1:
            return True
        else:
            return False

    def read(self):
        sql = """SELECT dev.id as did, det.id, det.label, det.config,
			det.pin, det.value, det.ts_value
		 FROM devices dev, details det
		 WHERE dev.id=det.device_id
		   AND dev.enabled=1
		   AND det.enabled=1;
        """
        result = self.query(sql)
	return result
    

