/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot
 *
 *   The main entry of the client program
 *
 *****************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "main.h"
#include "data.h"
#include "methods.h"
#include "rfidcom.h"

// Local temporary data holder
PalletMovement *movement;

// The current state; 
/// 0: listening for a tag 
/// 1: listening for tag or placement
int state;

int main(void){
    printf("\n\n-----------------------------------");
    printf("\nStarting RFID Logistics system 0.01\n");
    printf("-----------------------------------\n\n");
    bool running = true;
    state = 0;
    
    // Allocate new structure for holding the pallet movement
    movement = PalletMovement_create(READER_ID);
    
    // Debug: Count how many times the main loop has iterated
    int iterations = 0;

    while(running){
        // New command
        printf("\n");
    
        // Get data from reader
        char* data = call_reader();
        if(data == NULL){
            continue;
        }
        
        // Debug: Print the amount of iterations and the data received this iteration
//        printf("Iteration [%i] State [%i] Tag: %s\n", iterations++, state, data);

        // Process data
        // Tag is a placement
        if(is_place(data)){
//            printf("Read place with ID: [%s]\n", data);
            if(state == 1 && movement->pallet != NULL){
                PalletMovement_setPlacement(movement, data);
                move_end(movement);
                PalletMovement_setPallet(movement, NULL);
                state = 0;
            } else {
                printf("Waiting for pallet but place was scanned, did nothing.\n");
            }
        }
        
        // Tag is a pallet
        else {
//            printf("main: Given tag was pallet\n");
            
            // If we are holding the scanned pallet, don't do anything
            // TODO: Support to update the time we last had it on the reader
            if(movement->pallet == NULL || strcmp(movement->pallet, data) != 0){
                if(state == 1 && movement->pallet != NULL){
                    PalletMovement_setPlacement(movement, FLOOR_ID);
                    move_end(movement);
                }
            
                PalletMovement_setPlacement(movement, READER_ID);
                PalletMovement_setPallet(movement, data);
                move_start(movement);
                state = 1;
            }
        }
        
        // Always free the data pointer
        free(data);
        
        // Try to send to the server
        // TODO: This should be done more often then every read in real environment
        // What if the reader only lacks internet when moving stuff?
        send_movements();
    }
    return 0;
}

/// Returns if the given tag is a placement instead of pallet
int is_place(const char *tag){
    int i;
    for(i = 0; i < 3; i++){
        if(strcmp(PLACEMENT[i], tag) == 0){
            return 1;
        }
    }
    return 0;
}


