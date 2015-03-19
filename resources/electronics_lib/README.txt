The Blocks folder contains a number of known-good schematics.  

Until a automated ulp is created, the instructions for making a new board are as follows:
1. Make a new schematic: File>New>Schematic
2. Import schematics: File>Import> find the schematic files you want included
	a) The only nets that should be connected by default (old name = new name) are V+, +5V, +3V3, GND or any other power rails or busses
3. Eagle does not carry over the sheet descriptions when imported.  Open the original files, right click on the sheet, select description, copy the html code.  
	a) Repeat the above steps in the new, combined schematic file, and paste the description in the appropriate location.
4. Read the instructions in the description.  It should instruct you to connect pins and anything else required for the circuit.  Look out for optional components.
	a) To connect pins, draw nets that are connected to the pins (they may already exist).  Do the same for all connected pins.  Then, rename all nets that should be connected to the same name.
5. Your schematic should be done at this point.  It is time to create a board from the schematic.
	a) To make a board from a schematic, File>Switch to Board> select yes if it asks to create from schematic.

Making new blocks:
1. Make a single sheet schematic
2. Add and connect symbols until you have a well contained part
3. For all nets that interact with outside schematic sheets, place a BLOCK_CONNECTOR symbol for each net and connect them
4. Name the nets according to the naming convention below:
	a) Serivce,Service#,1-to-many-symbol,Input-output-symbol,SuperService,SuperService#,AlternateFunction#/ ... (repeat for all possible services).PORT,PININ
	b) service (super service) options: GPIO (#), PWM (TIMER), PWM_N (TIMER), QEP (TIMER), TXRX (UART), SCK (SPI), MISO (SPI), MOSI (SPI), SCL (I2C), SDA (I2C), D+ (USB), D- (USB)
