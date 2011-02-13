/*
Arduino Register Scanning
Register Values <x>: B,C,D
PINx  	- Read state of INPUT pins 
PORTx 	- Stores logic state of Outputs HIGH or LOW
DDRx 	- Data Dirrection Register (Is port input or output)
-----
Notes; Pins 0&1 are reserved for Serial Communication 
These pins will showup as inputs on the data register

---- 
AVR Register Mapping

Port	Pin-Range	Collection
B		8-13		0
C		0-5			1 (Analog Channel) 		
D		0-7			2


||7|6|5|4|3|2|1|0|
||1|0|0|0|1|0|1|0|

Pins 3,5,6,9,10,11 support PWM

Messaging Options
1.) XML - Parser too big, will introduce overhead on system
2.) Json - Library does exist "ajson" however current release is not optimized
will encounter similar issues as item 1. 
3.) Custom


Custom Options
1.) Define custom set of control characters for IO mapping.
2.) Use pin numerical assignment.
3.) 


Command Syntax

First release:
Command sent to the arduino will consist of a string of 3 character

Two Command type formats
------------------------------------------
Single Character Command Syntax
------------------------------------------


------------------------------------------
Multi-character Command Syntax
------------------------------------------
Multi-Character Format
<Register Letter><Pin Number><State Condition>

Register Letter: [B, C,or D]: This will tell the controller which IO port to control
Pin Number: [max number 8 [pins 0-7]]
State Condition: [H, L, Q, I]
H - Digital High signal
L - Digital Low signal 
Q - Query Pin State
I - Invert current state
*/

#define MAX_MESSAGE_SIZE 3
#define INIT_BLINK_DELAY 150
#define TOGGLE_BLINK 50
#define RUN_BLINK_DELAY 10
#define StatusLed 13
#define NumOfPorts 3
#define NumOfRegisters 3
#define DDR 0
#define PORT 1
#define PIN 2

int BlinkCount = 0;
int commandBuffer[2];
int *CMD;
byte regMap[NumOfPorts][NumOfRegisters]; 
volatile int led_status_state = LOW;

struct {
	unsigned int low : 4;
	unsigned int high: 4;
} addr;


enum {
	// Define the ascii charaters used in the program
	// 
	CMD_START = 35, // #
	CMD_SEND_MAP = 77, // M 
	CMD_READ = 77,
	CMD_WRITE = 77,
	CMD_REGISTER_B = 66, // B
	CMD_REGISTER_C = 67, // C
	CMD_REGISTER_D = 68, // D
	PIN_11 = 66, // B
	PIN_12 = 67, // C
	PIN_13 = 68, // D
	
};


//byte ddrRegister; // used for testing
//unsigned char Register[] = {"DDRB", "DDRC", "DDRD"};

// Array to hold the bit map

void toggleLed() {
	led_status_state = !led_status_state;
	digitalWrite(StatusLed, led_status_state);
}

void setIOMap() {
	/************************************
	Scan All regsters, Store result in bin map	

	*************************************/	
	// read ddr ports, add map to array
	// TODO: Change this method to memory address instead of Adruino name. 
	// 	Would setup support for multiple controller types 
	regMap[DDR][0] = DDRB;
	regMap[DDR][1] = DDRC;
	regMap[DDR][2] = DDRD;
	// Scan Output States
	regMap[PORT][0] = PORTB;
	regMap[PORT][1] = PORTC;
	regMap[PORT][2] = PORTD;
	// Scan Input states
	regMap[PIN][0] = PINB;
	regMap[PIN][1] = PINC;
	regMap[PIN][2] = PIND;

}



void get_register_state(int map_offset) {
	
	char* map_name[3] = {"DDR", "PORT", "PIN"};
	
	//Serial.print("Map [ ");
	Serial.print(map_name[map_offset]);
	//Serial.print(" ]: ");
	Serial.print(":");
	//Serial.println(map_offset);
	for (int x =0; x < sizeof(regMap[map_offset]); x++ ) {
		Serial.print(x);
		Serial.print(" = ");
		Serial.println(regMap[map_offset][x],HEX);
		//Serial.println(regMap[map_offset][x], BIN);
	}
}



void sendIOMap() {
	// Read Map configuration
	setIOMap();
	// Send each register to control host
	get_register_state(DDR);
    get_register_state(PORT);
    get_register_state(PIN);

}


int getSerialByte() {
	// Set return value to 0 this is ASCII NULL Character
	// System will not attempt to process this 
	
	int serialByte = 0;
	// Check to make certain the byte has not been 
	// removed from the queue.
	if (Serial.available() > 0 ) {
		serialByte = Serial.read();
		// Set high and low nibbles
		// Mask only high bytes then shift to lowest position
		addr.high = (serialByte & 0xF0) >> 4;
		// Mas low bits and shift high position
		addr.low = serialByte & 0x0F;
		//addr = serialByte;
		return serialByte;
	} 
	//	return serialByte;
}

void wait_for_host(){
	// Initial state when device is powered on. 
	// Initialize state. 
	// Wait for the controlling software to start
	int result;
	Serial.flush();
	while(1) {
		toggleLed();
		//result = 		
		if (Serial.available() > 0) {
			// Wait for web controller to initialize the system 
			// Device will exit this state once a '#' character is sent.
			if (getSerialByte() == CMD_START) {
				// Send # character to break out of loop
				return;
			}
		}
		delay(INIT_BLINK_DELAY);
	}
}


void setup() {
	Serial.begin(115200);
	delay(100);
	pinMode(StatusLed, OUTPUT);
	pinMode(1, OUTPUT);
	pinMode(3, OUTPUT);
	pinMode(4, INPUT);
	pinMode(7, OUTPUT);
	DDRB = 0x2d;
//	pinMode(8, INPUT);
//	pinMode(10, OUTPUT);
//	pinMode(12, OUTPUT);
//	pinMode(9, INPUT);
//	Serial.flush();
	wait_for_host();
	Serial.println("Starting...");
}



void serialParser() {
	// parse incomming data
	//If there is data available on the serial port	
	int currentByte;
	bool expectingCommand = false;
	while (Serial.available() != 0) {
		// Read all bytes off the serial queue
		// Save the current byte
		currentByte = getSerialByte();
		// print Debug message
		Serial.print("-Got > ");
		Serial.println(currentByte, HEX);
		Serial.print("High (");
		Serial.print(addr.high, HEX);
		Serial.print(") Low (");
		Serial.print(addr.low, HEX);
		Serial.println(")");
		
		

		// Determine if the byte is a command or command sequence		

		if (expectingCommand) {
			Serial.println("Expecting Command Loop");
		} else {

			if (currentByte == CMD_SEND_MAP ) {
				// Display map 'M' key
				sendIOMap();			
			} else if ( (currentByte == CMD_REGISTER_B ) || (currentByte == CMD_REGISTER_C) || (currentByte == CMD_REGISTER_D) ) {
				// If this is a command then look for the execution path 
				// Expecting next byte to be command
				if (expectingCommand) {
					// If another request is sent before a command sequnce is complete
					// Reset the buffer and try again
					expectingCommand = false;
					// debug
					Serial.println("Command repeated, resetting");
				}  else {
					expectingCommand = true;
					Serial.println("Waiting for command");
				}
			}			
				
		} 
	

	}
}





void loop() {
	
	// ---------------------------------------------------------
	// System should only be interrupted 
	// When a serial command is received.
	// ---------------------------------------------------------
	//Read Serial Queue Depth
	// If data is available and over depth limit
	// Read data, determine if it is a valid command to process
	// ---------------------------------------------------------
	if (Serial.available() > 0 ) {
		serialParser();
	}
	if (BlinkCount >= TOGGLE_BLINK ) {
		BlinkCount =0;
		toggleLed();
	}
	BlinkCount++;

	/*
	// Blink a LED or two
	if (LedState) {	
		// If LED state is true (on) turn light off
		digitalWrite(StatusLed, LOW);
		LedState = false;
	} else {
		digitalWrite(StatusLed, HIGH);
		LedState = true;
	}

	Serial.println("Reading DDR");
	//for (int x = 0; x < sizeof(Register); x++) {
		Serial.print("Reading Register -> ");
		//Serial.print(Register[x]);
		Serial.print(" = ");
		ddrRegister = 0x04;
		Serial.priTOntln(&ddrRegister, BIN);
	//}

	Serial.println("DDR Map");
	for (int x =0; x < sizeof(regMap[DDR]); x++ ) {
		Serial.print(x);
		Serial.print(" = ");
		Serial.println(regMap[DDR][x], BIN);

	}
*/

//	delay(2000);
	delay(RUN_BLINK_DELAY);
	/*
	scanIO();
	get_register_state(DDR);
	get_register_state(PORT);
	get_register_state(PIN);
	*/
	

}


