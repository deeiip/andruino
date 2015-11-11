**This page describes the command sets used to interact with Arduino.**

# Instruction Map #

This section describes the command mapping used to alter the state of an arduino connected via the andruino web service. Each instruction in the protocol will consist to 3 bytes.

## References / Resources ##

  * http://arduino.cc/en/Reference/HomePage
  * http://pyserial.sourceforge.net/


### AVR PORT Description ###

|Register Name| Function|
|:------------|:--------|
|DDR          |Data direction register, sets pin states as Input or Output|
|PORT         |Output register |
|PIN          | Input register |


### AVR IO Layout ###
|Port|Pins|Notes|
|:---|:---|:----|
|B   |8-13|9-11 support PWM|
|C   |0-5 |Analog Inputs only|
|D   |2-7 |3,5,6 Support PWM Pins 0-1 reserved for serial IO|

## Serial Communication ##
Communication between the host machine and avr consists of two main command types (Read / Write). A read command request will trigger the avr to return
a full list of all IO buffers on the device (See Read-Output below). Write commands on the other hand are more complex.

Write commands contain the following syntax:
Note: current design does not include analog ports

  1. Command byte
  1. Register Address
  1. Register Data



## Read Command Output ##
|Hex|H|L|PORT|REGISTER|
|:--|:|:|:---|:-------|
|0x10|0001|0000|B   |DDR     |
|0x11|0001|0001|B   |PORT    |
|0x12|0001|0010|B   |PIN     |
|0x20|0010|0000|C   |DDR     |
|0x21|0010|0001|C   |PORT    |
|0x22|0010|0010|C   |PIN     |
|0x30|0011|0000|D   |DDR     |
|0x31|0011|0001|D   |PORT    |
|0x32|0011|0010|D   |PIN     |





## Register Addressing ##

### _Note: Map specific to Atmel 168 (Standard) Arduino Current release will not include support for PORT-C (This is the analog interface for the ardunio)_ ###


|Hex|H|L|PORT|REGISTER|
|:--|:|:|:---|:-------|
|0x00|0000|0000|B   |DDR     |
|0x01|0000|0001|B   |PORT    |
|0x10|0001|0000|C   |DDR     |
|0x11|0001|0001|C   |PORT    |
|0x20|0010|0000|D   |DDR     |
|0x21|0010|0001|D   |PORT    |


## Examples ##

  1. Setting Outputs

```
 Start program 
 Send: #

 Read IO state
 Send: \x72

 Set Pins 12, 13 to outputs
 Send: \x77\x10\x30



 
```