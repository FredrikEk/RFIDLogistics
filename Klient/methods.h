/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot
 *
 *   Holds the methods used to connect to the web service
 *
 *****************************************************************************/

#include "data.h"

#ifndef METHODS_HEADER
#define METHODS_HEADER

/// Starts to move a pallet
void move_start(const PalletMovement *movement);

/// Ends a move with a pallet
void move_end(const PalletMovement *movement);

/// Store a movement to send
void movements_add(const char *URL, const char post_data[],
    const char* (*headers)[], const int num_headers);

/// Send all stored movements
void send_movements();

#endif
