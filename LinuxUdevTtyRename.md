#Udev rules to rename locally connected USB serial devices

# Introduction #



# Details #

```
#Usb Xbee interface board
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="A600dSA5",  SYMLINK+="ttyXbee"

# Arduino Decima
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="A9003Yv3",  SYMLINK+="ttyDecima"

# FDTI Sparkfun serial converter (Red Jumper)
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="A6007nES",  SYMLINK+="ttyRed"


# FDTI Sparkfun serial converter (Blue Jumper)
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="A6007nEX",  SYMLINK+="ttyBlue"

# FDTI Serial Cable Avr Programmer
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="FTDQ28V9",  SYMLINK+="ttyAvr"

# Arduino NG
KERNEL=="ttyUSB?", SUBSYSTEM=="tty", ATTRS{serial}=="A4001dDJ",  SYMLINK+="ttyNg"


```