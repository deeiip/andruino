import cherrypy
from andruino_db import *
from andruino_api import *
#import os
#from tempfile import gettempdir

#import sys, os
#import datetime
#from struct import *
#from andruino_api import *

AnDB = AndruinoDb()
'''
	Initial version of the andruino application will only 
	support a single device...
	Future releases will add Multiple device support
	
	For configuration purposes, Manually set DeviceId to reference database ID
'''

Api = AndruinoApi(DeviceId=1)



def requireLogin(self): 
		return '{"command":"login","response":"fail"}'

class Root:
	@cherrypy.expose
	def index(self):
		return '{"command":"index","response":"Hello, World!"}'


class Login:
	@cherrypy.expose
	def default(self,username,password):
		if (AnDB.getLogin(username,password)):
			cherrypy.session['username'] = username
			cherrypy.session['email'] = AnDB.getEmail(username)
			cherrypy.session['loggedin'] = True
			return '{"command":"login","response":"pass"}'
		else:
			cherrypy.session['loggedin'] = False
			return '{"command":"login","response":"fail"}'

class Logout:
	_cp_config = { 
		'tools.session_auth.on': True, 
		'tools.session_auth.login_screen' : requireLogin,
	} 
	@cherrypy.expose
	def default(self):
		cherrypy.session['username'] = ""
		cherrypy.session['email'] = ""
		cherrypy.session['loggedin'] = False
		return '{"command":"logout","response":"pass"}'


class UserInfo:
	_cp_config = { 
		'tools.session_auth.on': True, 
		'tools.session_auth.login_screen' : requireLogin,
	} 
	@cherrypy.expose
	def default(self):
		if (cherrypy.session.get('loggedin')):
			u = cherrypy.session.get('username')
			e = cherrypy.session.get('email')
			l = cherrypy.session.get('loggedin')
			return '{"command":"userinfo","response":"loggedin","userinfo":[{'\
				+'"username":"'+u\
				+'","email":"'+e\
				+'"}]}'
		else:
			return '{"command":"userinfo","response":"loggedout"}'


class Read:
	#_cp_config = { 
		#'tools.session_auth.on': True, 
		#'tools.session_auth.login_screen' : requireLogin,
	#} 
	@cherrypy.expose
	def default(self):
		statuses = AnDB.read()
		responseList = list()
		for status in statuses:
			response = '{"did":"'+str(status['did'])+'",'
			response += '"id":"'+str(status['id'])+'",'
			response += '"label":"'+str(status['label'])+'",'
			response += '"ddr":"'+str(status['config'])+'",'
			response += '"pin":"'+str(status['pin'])+'",'
			response += '"value":"'+str(status['value'])+'",'
			response += '"ts_value":"'+str(status['ts_value'])+'"}'
			responseList.append(response)
		return '{"command":"read","response":"'+str(len(responseList))+'","details":['\
			+",".join(responseList)+']}'


class Write:
	_cp_config = { 
		'tools.session_auth.on': True, 
		'tools.session_auth.login_screen' : requireLogin,
	} 
	@cherrypy.expose
	def default(self,did,value):
		if (AnDB.write(did,value)):
			return '{"command":"write","response":"pass"}'
		else:
			return '{"command":"write","response":"fail"}'


class Config:
	_cp_config = { 
		'tools.session_auth.on': True, 
		'tools.session_auth.login_screen' : requireLogin,
	} 
	@cherrypy.expose
	def default(self,did,value):
		if (AnDB.config(did,value)):
			return '{"command":"config","response":"pass"}'
		else:
			return '{"command":"config","response":"fail"}'


if __name__ == '__main__':

	root = Root()
	root.login = Login()
	root.logout = Logout()
	root.userinfo = UserInfo()
	root.read = Read()
	root.write = Write()
	root.config = Config()
	'''
		Start the API
		
	'''
	Api.startSerial()
	cherrypy.server.socket_host = '0.0.0.0'
	cherrypy.config.update({'tools.sessions.on' : True})
	cherrypy.quickstart(root)
