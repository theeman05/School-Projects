// Ethan Hartman
// Final Project: Smart Fan
#include <hidef.h>      /* common defines and macros */
#include <mc9s12dg256.h>     /* derivative information */
#pragma LINK_INFO DERIVATIVE "mc9s12dg256b"

#include <math.h> /* math module */
#include <stdio.h> /* strings module*/
#include <stdlib.h>

#include "main_asm.h" /* interface to the assembly module */

// Constants
const double NINE_FIFTHS = 9.0/5.0;
const int TEMP_DIFFERENTIAL = 32;
const int MAX_TEMP_SET = 200; // Max Temperature set point

//   CHARACTERS
const char COLON = ':';
const char * CHAR_NUM[] = {"0","1","2","3","4","5","6","7","8","9"};
const char * BLANK = "                ";

//   TIME CONSTANTS
const int TICK_TO_SEC = 1000; // 1000 Ticks (ms) = 1 sec
const int SEC_TO_HR = 3600;   // 3600 seconds in an hour
const int SEC_TO_MIN = 60;    // 60 seconds in a minute
const int MAX_SECS = 358839;  // The max set time on the timer (s) (Keypad Input: 98:99:99)

//   LCD
const char LINE1 = 0x00;
const char LINE2 = 0x40;
const char DISPLAY_RATE = 250;// How fast we want to update the display (ms)
const char TIME_DISP_LOC = LINE2 + 11;
const char TEMP_DISP_LOC = LINE2 + 6;

//   SERVO   
const int MIN_FAN_DIRECTION = 2250;
const int MAX_FAN_DIRECTION = 6750;
const int MID_FAN_DIRECTION = (MAX_FAN_DIRECTION - MIN_FAN_DIRECTION) / 2.0 + MIN_FAN_DIRECTION;
const int SERVO_ROT_MS = 50;  // How often we rotate the servo (ms)
const int SERVO_INC = 50;     // How much we want to rotate each update

//   MOTOR 
const int FAN_TICK_RATE = 100;        // How fast we update the fan's speed
const int SPEED_DISPLAY_TIME = 2000;  // How long we'd like the speed to be displayed on the lcd (ms)
const int TEMP_TRIG_LENIENCE = 3;     // (How many degrees less than the temp set to turn the fan off (F))
const int MAX_FAN_SPEED = 255;

//   TIMER   
const int CLKS_PER_MS = 24000;

//   AD0
const byte PHOTO_CHANNEL = 4;   // Internal Phototransistor channel
const byte TEMP_CHANNEL = 5;    // Internal Temperature Sensor

//   AD1
const byte EXTERNAL_CHANNEL = 3;// The external potentiometer convertor

//   PHOTOTRANSISTOR
const int LIGHT_ON = 100;

//   SET VALUES
const byte SET_TIME = 0;
const byte SET_TEMP = 1;

// Other Variables in the main scope
int fan_speed;     
int servo_rot;
int servo_dir;        // -1: Left, 0: Not Rotating; 1: Right
int numbers[6];       // Numbers input into keypad for time
int temp_numbers[3];
 
char * remaining;     // Remaining time for timer

long ticks = 0;
long speed_display_off = -1;// The tick we want the speed to stop displaying at
long timer_end = -1;        // The time when the fan should shut off (ticks) -1 means timer is off.

int fan_on = 0;             // 0: Fan is off, 1: Fan is on
int temperature = 0;        // Current temperature of the room
int prev_temperature = 1;
int temp_set = -1;          // Temperature trigger value (F) -1 means temperature trigger is not set
int temp_turn_on = 0;       // If the fan was turned on by a temperature set, = 1
int prev_fan_speed = 0;
int setting_something = -1;
int light_level = 0;

byte presses = 0;

void clock_init(){
  TSCR1 = 0x80;     // Enable Timer
  TSCR2 = 0x00;     // Set counter speed 24MHZ
  
  TIE = 0x80;       // Enable Timer interrupt 7
  TIOS = 0x80;      // Select tc7 as output compare
  
  TC7 = TCNT + CLKS_PER_MS;  // set up initial compare for 1ms of time
                             // after current value of counter
  _asm CLI;      // enable system interrupts
}

// Increments the head rotation based on it's direction
void increment_rotation(){
   if (servo_dir == 1)
      servo_rot+= SERVO_INC;
   else
      servo_rot-= SERVO_INC;
   
   if (servo_rot >= MAX_FAN_DIRECTION){
      servo_rot = MAX_FAN_DIRECTION;
      servo_dir = -1;
   }else if (servo_rot <= MIN_FAN_DIRECTION){
      servo_rot = MIN_FAN_DIRECTION;
      servo_dir = 1;
   }
   
   set_servo54(servo_rot);    
}

// When kepad number is pressed for timer
void number_pressed(int number){
    int rev, i;
    if (presses != 0){
        for (i = 0; i < presses; i++){
            rev = 6 - (presses - i);
            numbers[rev - 1] = numbers[rev];
        }
    }
    numbers[5] = number;
    presses++;
}

// When kepad number is pressed for temperature
void number_pressed_temp(int number){
    int rev, i;
    if (presses != 0){
        for (i = 0; i < presses; i++){
            rev = 3 - (presses - i);
            temp_numbers[rev - 1] = temp_numbers[rev];
        }
    }
    temp_numbers[2] = number;
    presses++;
}

// Moves all numbers in numbers to the right 1 (deletes numbers[5]) for timer
void keypad_del(){
    int i, loc;
    for (i = 5; i >= 0; i--)
        numbers[i] = numbers[i-1];
    
    presses--;
    loc = TIME_DISP_LOC - presses - presses/2;
    if (presses == 4)
      loc--;
    set_lcd_addr(loc);
    if (presses == 4)
      type_lcd(" ");
    
    type_lcd(" ");
    for (i = 6 - presses; i <= 5 ; i++){
        if (i != 0 & i % 2 == 0)
          type_lcd(&COLON);
        type_lcd(CHAR_NUM[numbers[i]]);
     }
     
     if (presses == 4){
       set_lcd_addr(TIME_DISP_LOC - 5);
       type_lcd(" ");
     }else if (presses == 2){
       set_lcd_addr(TIME_DISP_LOC - 2);
       type_lcd(" ");
     }
}

// Moves all numbers in temp_numbers to the right 1 (deletes numbers[3]) for temperature
void temp_keypad_del(){
    int i;
    for (i = 2; i >= 0; i--)
        temp_numbers[i] = temp_numbers[i-1];
    
    presses--;
    set_lcd_addr(TEMP_DISP_LOC + presses);
    type_lcd(" ");
}

// Takes keypad inputs when trying to update timer
void check_timer_update(){
    char key = keyscan();
    char ascii = hex2asc(key);
    int iVal = atoi(&ascii);
    int i, loc;
    
    loc = TIME_DISP_LOC - presses - presses/2;
    
    set_lcd_addr(loc);                                                                                           
    if (key == 0x00 | iVal > 0 & presses != 6){
      for (i = 6 - presses; i <= 5 ; i++){
        type_lcd(CHAR_NUM[numbers[i]]);
        if (i != 0 & i % 2 == 0)
          type_lcd(&COLON);
      }
      
      hex2lcd(key);
      number_pressed(iVal);
    }else if (key == 0xD & presses != 0){
      keypad_del();
    }
    
    if (key != 0x10)
      wait_keyup();  
}

// Takes keypad inputs when trying to update temp setter
void check_temp_update(){
    char key = keyscan();
    char ascii = hex2asc(key);
    int iVal = atoi(&ascii);
    int loc;
    
    loc = TEMP_DISP_LOC + presses;
    
    set_lcd_addr(loc);
    if (key == 0x00 | iVal > 0 & presses != 3){
      hex2lcd(key);
      number_pressed_temp(iVal);
    }else if (key == 0xD & presses != 0){
      temp_keypad_del();
    }
    
    if (key != 0x10)
      wait_keyup();
}

// Converts given_time to seconds. If seconds is greater than MAX_SECS, MAX_SECS will be returned
int calculate_seconds(){
   int seconds = numbers[5] + numbers[4] * 10;
   seconds += (numbers[3] + numbers[2] * 10) * SEC_TO_MIN;
   seconds += (numbers[1] + numbers[0] * 10) * SEC_TO_HR;
   
   return seconds < MAX_SECS ? seconds : MAX_SECS;
}

// Returns the temperature input as a number
int calculate_temp(){
    int new_temp = temp_numbers[2] + (temp_numbers[1] * 10) + (temp_numbers[0] * 100);
    return new_temp <= MAX_TEMP_SET ? new_temp : MAX_TEMP_SET;
}

// Returns the current temperature in F
int get_temp_f(){
    int temp_c = ad0conv(TEMP_CHANNEL);
    return (temp_c * NINE_FIFTHS) + TEMP_DIFFERENTIAL;
}

// Calculates the remaining time and formats it into the normal time format h/h:mm:ss
// If h or m is 0, only seconds will be displayed. If h is 0, mins and secs will be displayed.
void calculate_remaining(){
  long tick_difference = timer_end - ticks;
  int idx_shift = 0;
  long long_s;
  int h, m, s;
  char temp_char[2];
  
  remaining = (char *) malloc(8);
  
  long_s = tick_difference / TICK_TO_SEC;
  
  if (long_s <= 0){
    remaining[0] = '0';
    return; 
  }
  
  h = long_s / SEC_TO_HR;
  long_s -= h * SEC_TO_HR;
  
  m = long_s / SEC_TO_MIN;
  long_s -= m * SEC_TO_MIN;
   
  s = long_s;
    
  if (h > 0){
    sprintf(temp_char, "%d", h);
    if (h >= 10)
       strcat(remaining, temp_char);
    else
       remaining[1 - ++idx_shift] = temp_char[0]; 
      
    remaining[2 - idx_shift] = COLON;
  }else{
    idx_shift = 3;
  }
  
  sprintf(temp_char, "%d", m);
  if (m >= 10){
      strcat(remaining, temp_char);
  }else if (m > 0 | h > 0){
    if (h != 0)  
        remaining[3 - idx_shift] = '0';
    else
        idx_shift++;
    
    remaining[4 - idx_shift] = temp_char[0];
  }
  
  sprintf(temp_char, "%d", s);
  if (h != 0 | m != 0){
    remaining[5 - idx_shift] = COLON;
    if (s >= 10){
        strcat(remaining, temp_char);
    }else{
        remaining[6 - idx_shift] = '0';
        remaining[7 - idx_shift] = s + '0'; 
     }
  }else{
      strcat(remaining, temp_char);
  }
}

// Does the main updating of the lcd
void update_lcd(){
   set_lcd_addr(LINE1);
   if (ticks > speed_display_off & speed_display_off != -1){ // When the user isn't changing the speed
      if (timer_end == -1)
        type_lcd("Timer Is Off");
      else{
        calculate_remaining();
        type_lcd(remaining);
      }
      type_lcd(BLANK);
      
      set_lcd_addr(LINE2);
      type_lcd("Room Temp:");
      write_int_lcd(temperature);
      type_lcd("F");
   }else if(ticks < speed_display_off){
      type_lcd("Fan Speed:");
      write_int_lcd(fan_speed); 
   }
   
   if (fan_on == 0)
    clear_lcd();
   
   if (setting_something == SET_TIME){
      clear_lcd();
      set_lcd_addr(LINE1);
      type_lcd("Insert a Time:");
   }else if (setting_something == SET_TEMP){
      clear_lcd();
      set_lcd_addr(LINE1);
      type_lcd("Temperature (F):");
   }
}

void outputString(char * given){  // Output's a string to the miniIDE
  int i = 0;
  while ( given[i] != 0 ) {
    outchar0(given[i]);
    i = i + 1;
  }
}

// Toggles the state of the fan and clears any necessary values
void toggle_fan_state(){
  if (fan_on == 0){
    ticks = 0;
    speed_display_off = 0;
    timer_end = -1;
    setting_something = -1;
    
    fan_on = 1;
    outputString("FAN IS ON\n"); 
  }else{
    timer_end = -1;
    temp_turn_on = 0;
    PTT &= 0xFD; // Toggle Light off
    
    leds_off();
    clear_lcd(); 
    fan_on = 0;
    outputString("FAN IS OFF\n");  
  }
  
  // Make a sound
  PTT |= 0x04;
  ms_delay(100);
  PTT &= 0xFB;
}

// The main loop of the program
void main_loop(){
   temperature = get_temp_f();
    if (fan_on == 1){
      prev_fan_speed = fan_speed;
      light_level = ad0conv(4);
      fan_speed = MAX_FAN_SPEED - (ad1conv(EXTERNAL_CHANNEL) >> 2); // Based on external potentiometer
      
      if (setting_something == -1){
        if (abs(fan_speed - prev_fan_speed) >= 3){
          if (speed_display_off < ticks)
            clear_lcd();
          speed_display_off = ticks + SPEED_DISPLAY_TIME;
        }
        
        update_lcd();
      }else if (setting_something == SET_TIME){
        check_timer_update();
      }else if (setting_something == SET_TEMP){
        check_temp_update();
      }
    }

    prev_temperature = temperature;
    
    if (light_level <= LIGHT_ON & fan_on){
        PTT |= 0x02;
    }else{
        PTT &= 0xFD;
    }
    
    if ((SW1_dip() & 0x80) == 0x80){
      if (servo_dir == 0)
        servo_dir = 1;
    }else
       servo_dir = 0;
}

// Monitors switch actions
void monitor_switches(){
  int i;
  long secs = -1;
  int new_temp; 
  if (SW5_down()){
    while (SW5_down()){}
    toggle_fan_state();
  }
  
  if ((PTT & 0x01) != 0x01 & fan_on){
    while ((PTT & 0x01) != 0x01){}
    if (setting_something == -1){
      timer_end = -1;
      presses = 0;
      for (i = 0; i < 6; i++)
        numbers[i] = 0;
       
      setting_something = SET_TIME;
      speed_display_off != -1; 
      update_lcd();
    }else if (setting_something == SET_TIME){
      secs = calculate_seconds() + 1;
      if (secs > 0){ // If the user sets a timer, don't auto turnoff based on temp
        timer_end = ticks + secs * TICK_TO_SEC;
        temp_turn_on = 0;
      }
      
      clear_lcd();
      setting_something = -1;
    }
  }
  
  if ((PTT & 0x08) != 0x08 & fan_on){
    while ((PTT & 0x08) != 0x08){}
    if (setting_something == -1){
      temp_set = -1;
      presses = 0;
      for (i = 0; i < 3; i++)
        temp_numbers[i] = 0;
       
      setting_something = SET_TEMP;
      speed_display_off != -1; 
      update_lcd();
    }else if (setting_something == SET_TEMP){
      new_temp = calculate_temp();
      if (new_temp > 0){
        temp_set = new_temp;
        temp_turn_on = 0;
      }
      
      clear_lcd();
      setting_something = -1;
    }
  }  
}

// Time based actions
void interrupt 15 handler() {
  TFLG1 = TFLG1 | 0x80;       // clear channel 7 interrupt flag
  TC7 = TCNT + CLKS_PER_MS;   // Set output channel 7 to 1ms later
  
  if (fan_on == 1){
    ticks++;
    if (servo_dir != 0 && ticks % SERVO_ROT_MS == 0)
       increment_rotation();
    
    if (ticks % FAN_TICK_RATE == 0){
      leds_on(SW1_dip()); 
      motor0(fan_speed);
    }
  }
  
  if (fan_on == 1 & timer_end != -1 & (timer_end - TICK_TO_SEC) < ticks & timer_end != 0) // Turn Fan Off
    toggle_fan_state();
  else if (fan_on == 1 & temp_turn_on == 1 & temp_set != -1 & temperature <= temp_set - TEMP_TRIG_LENIENCE)
    toggle_fan_state();
  
  if (fan_on == 0 & temp_set != -1 & temperature >= temp_set){ // Turn Fan On
    temp_turn_on = 1;
    toggle_fan_state();
  }
}

// Switch Watcher
void interrupt 7 switches(){
  monitor_switches();
  clear_RTI_flag();
}

void main(void) {
  /* put your own code here */
  PLL_init();         // set system clock frequency to 24 MHz 
  ad0_enable();       // sets up A to D converter
  ad1_enable();
  
  seg7_disable();     // Somewhat Works
  
  lcd_init();
  SW_enable();
  motor0_init();      // Initialize Motor
  servo54_init();     // Initialize Servo
  clock_init();       // Initialize Clock interrupt 7
  RTI_init();         // Initialize RTI
  keypad_enable();    // Enable Keypad
  
  SCI0_init(9600);    // Initialize SCI
  
  DDRT = 0x06;        // Port T used as input
  DDRB = 0xFF;
  
  servo_rot = MID_FAN_DIRECTION;
  servo_dir = 0;

  set_servo54(servo_rot);  // Set the head rotation to the mid-point
  while(1){
    main_loop();
  }
}