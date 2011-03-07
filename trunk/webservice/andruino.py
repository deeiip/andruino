#import the web py module
import web
#from web import form
import sqlite3
import datetime

#import the CherryPy server with ssl support
from web.wsgiserver import CherryPyWSGIServer

#define the ssl cert and key, yes these are self signed
CherryPyWSGIServer.ssl_certificate = "ssl.key/csu_hrc51_com.crt"
CherryPyWSGIServer.ssl_private_key = "ssl.key/csu_hrc51_com.key"

#define database stuff
db = web.database(dbn='sqlite', db='andruino.db')

urls  = ("/", "index")
urls += ("/sqlts", "dtts")
urls += ("/config", "config")
urls += ("/devdetails", "devdetails")
urls += ("/adddevice", "adddevice")
urls += ("/adddetails", "adddetails")
urls += ("/read", "read")
urls += ("/write", "write")
urls += ("/man", "man")
urls += ("/favicon.ico", "favicon")
app = web.application(urls, globals())

render = web.template.render('templates/')

adddevice_form = form.Form(
	form.Textbox("name", description="Name"),
	form.Textbox("port", description="Port"),
	form.Textbox("type", description="Type"),
	form.Dropdown("enabled", [('0', 'Disabled'), ('1', 'Enabled')]),
	form.Button('submit', type='submit', description="Add Device"),
)

adddetails_form = form.Form(
	form.Hidden("device_id"),
	form.Textbox("label", description="Label"),
	form.Textbox("ddr", description="DDR"),
	form.Textbox("pin", description="Pin"),
	form.Textbox("value", description="Last Value Updated"),
	form.Textbox("ts_value", description="Last Value Received"),
	form.Textbox("last_value", description="Last Value Sent"),
	form.Textbox("ts_output", description="Time Value Sent"),
	form.Dropdown("enabled", [('0', 'Disabled'), ('1', 'Enabled')]),
	form.Button('submit', type='submit', description="Add Device"),
)

class index:
	def GET(self):
		return 'Hello World!'

class man:
	def GET(self):
		return render.man()

class favicon:
	def GET(self):
		fi = open('images/favicon.ico','r')
		return fi.read()

class sqlts:
	def GET(self):
		dbts = db.query('SELECT NOW() AS now;')
		return dbts.now

class config:
	def GET(self):
		deviceList = db.select('devices')
		return render.devices("Device List", deviceList)

class devdetails:
	def GET(self):
		myvar = web.input()
		detailList = db.select('details', myvar, where="device_id = $device_id")
		return render.details("Device Details", detailList)

class read:
	def GET(self):
		statuses = db.query('SELECT dev.id as did, det.id, det.label, det.ddr, det.pin, det.value, det.ts_value FROM devices dev, details det WHERE dev.id=det.device_id AND dev.enabled=1 AND det.enabled=1;')
		return render.status("Status", statuses)

class write:
	def GET(self):
		myvar = web.input()
		db.query("UPDATE details SET value = "+myvar['value']+", last_value = "+myvar['value']+", ts_value=datetime('now'), ts_output=datetime('now') WHERE id = "+ myvar['did'])
		raise web.seeother('/read')

class adddevice:
	def GET(self):
		f = adddevice_form()
		return render.adddevice(f)

	def POST(self):
		f = adddevice_form()
		if not f.validates():
			return render.adddevice(f)
		else:
			return db.insert('devices', **f.d)

class adddetails:
	def GET(self):
		myvar = web.input(device_id='0')
		device = db.select('devices', myvar, where="id = $device_id")
		details = db.select('details', myvar, where="device_id = $device_id")
		f = adddetails_form()
		return render.adddetails(f,device,details)

	def POST(self):
		myvar = web.input(device_id='0')
		f = adddetails_form()
		if not f.validates():
			return render.adddetails(f)
		else:
			db.insert('details', **f.d)
			raise web.seeother('/adddetails?device_id='+myvar['device_id'])

if __name__ == "__main__":
	app.run()
