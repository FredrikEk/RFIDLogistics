/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot
 *
 *   Convenient methods
 *
 *****************************************************************************/

#ifndef GENERIC_HEADER
#define GENERIC_HEADER

#include <time.h>

/// Gets the current time in milliseconds
time_t get_time();

/// Gets the current time in milliseconds as a string
/// Make sure to free(var) the given pointer when done
char* get_time_string();

#endif
