import cherrypy
from andruino_db2 import *

#import sys, os
#import datetime
#from struct import *
#from andruino_api import *

AnDB = AndruinoDb()

# get the reference to the thread manager
#ws_api = AndruinoApi()

class Root:
	@cherrypy.expose
	def index(self):
		return '[{"command":"index","response":"Hello, World!"}]'

class Login:
	@cherrypy.expose
	def default(self,username,password):
		if (AnDB.getlogin(username,password)):

	#		session.username = username
	#		session.email = AnDB.getEmail(username)
	#		session.loggedin = True

			return '[{"command":"login","response":"pass"}]'
		else:

	#		session.loggedin = False

			return '[{"command":"login","response":"fail"}]'

class Read:
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

		return '[{"command":"read","response":"'+str(len(responseList))+'"},'\
			+",".join(responseList)+']'


if __name__ == '__main__':

	#AnDB.initDB()

	root = Root()
	root.login = Login()
	root.read = Read()
	#root.write = Write()

	cherrypy.server.socket_host = '0.0.0.0'
	cherrypy.tools.sessions.on = True
	cherrypy.quickstart(root)
