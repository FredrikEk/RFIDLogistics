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

#include <stdlib.h>
#include <stdio.h>
#include "generic.h"

/// Gets the current time in milliseconds
time_t get_time()
{
    time_t t = time(NULL);
	return t;
}

/// Gets the current time in milliseconds as a string
/// Make sure to free(var) the given pointer when done
char* get_time_string()
{
    time_t t = get_time();
    char *t_string = malloc(sizeof(char) * 10);
    sprintf(t_string, "%d", t);
    return t_string;
}

