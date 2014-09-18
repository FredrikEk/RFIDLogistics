/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot, Philip Dahlstedt
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

// Local temporary tag holder
PalletMovement *movement;

// The current state; 
/// 0: listening for a tag 
/// 1: listening for tag or placement
//int state;


int main(void){
    printf("\n\n-----------------------------------");
    printf("\nStarting RFID Logistics system 0.01\n");
    printf("\nStation version\n");
    printf("-----------------------------------\n\n");
    bool running = true;
    state = 0;
    
    // Allocate new structure for holding the pallet movement
    movement = PalletMovement_create(READER_ID);
    
    // Debug: Count how many times the main loop has iterated
    int iterations = 0;

    // Pallet or place (if other than 2 -> pallet)
    printf("Select mode:\n");
    printf("1. Pallet\n");
    printf("2. Place\n");
    int mode = 1;
    int i;
    scanf("%d", &i);
    if(i == 2){
      mode = 2;
      printf("mode: place\n");
    }else printf("mode: pallets\n");
    
    while(running){
        // New command
        printf("\n");
    
        // Get tag from reader
        printf("Scan first tag\n");
        char* tag = call_reader();
        char* tag2;
        char id [20];

        if(tag != NULL){
            // Could be replaced by using a barcodescanner
            printf("Enter id for the pallet/place: ");
            scanf("%19s", id);
            //printf("id: %s",id);
            //Check if mode is pallet
            if(mode == 1){
               printf("\nScan second tag\n");
               tag2 = call_reader();
               while(strcmp(tag, tag2) == 0){
                   printf("Same tag, scan again!\n");
                   tag2 = call_reader();
                }
               if(tag2 != NULL){
                 printf("Scan was sucesful\n");
                 PalletMovement_setPallet(movement, tag, tag2);
                 PalletMovement_id(movement, id);
                 pallet_id(movement);
               }else printf("Scan of second tag failed\n");
               //free(tag2);
            //Place
            }else{
             PalletMovement_setPlacement(movement, tag);
             PalletMovement_id(movement, id);
             place_id(movement);
            }
        }else printf("Scan of first tag failed\n");

	
        
        // Debug: Print the amount of iterations and the tag received this iteration
        printf("Iteration [%i] Id: [%s] Tag1: [%s] Tag2: [%s]\n", iterations++, id, tag, tag2);


        
        // Always free the tag pointer
        
        //free(tag);
        //free(tag2);
        //free(id);
        
        printf("Will send movements\n");
        send_movements();

    }
    return 0;
}


