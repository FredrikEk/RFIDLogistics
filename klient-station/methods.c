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

#include <stdio.h>
#include <stdlib.h>
#include "methods.h"
#include "connection.h"
#include "data.h"
#include "generic.h"

/// Holds all the PalletMovement not yet sent
LinkedMovement *queue = NULL;

// move
void pallet_id(const PalletMovement *movement)
{
    printf("pallet_id: Adding id [%s] to pallet with rfid [%s] and [%s]\n", movement->id, movement->pallet, movement->pallet2);
    char *time = get_time_string();
    
    const char *keys[5] = {
        "reader", "palletId", "tag1", "tag2", "time"
    };
    const char *values[5] = {
        movement->reader,
        movement->id, 
        movement->pallet,
        movement->pallet2,
        time
    };
    
    char *parameters = to_parameters(5, keys, values);
    char *URL = concat3(BASE_URL, SERVER_MOVE_START, parameters);
    
	movements_add(URL);
	
	free(time);
	free(parameters);
	free(URL);
}

void place_id(const PalletMovement *movement)
{
   printf("place_id: Adding tag to place [%s], with rfid tag [%s]\n", movement->id, movement->placement);
    char *time = get_time_string();
    
    const char *keys[4] = {
        "reader", "place", "p", "time"
    };
   printf("Values place_id: reader: [%s] id: [%s] tag: [%s] time: [%s]\n", movement->reader, movement->id, movement->placement, time);
    const char *values[4] = {
        movement->reader,
        movement->id, 
        movement->placement,
        time
    };
    printf("URL snart");
    char *parameters = to_parameters(4, keys, values);
    char *URL = concat3(BASE_URL, SERVER_MOVE_START, parameters);
   printf("URL: %s", URL);
    
	movements_add(URL);
	
	free(time);
	free(parameters);
	free(URL);
}

/// Starts to move a pallet
void move_start(const PalletMovement *movement)
{
    printf("Starting to move pallet [%s] with forklift [%s]\n", movement->pallet, movement->placement);
    char *time = get_time_string();
    
    const char *keys[4] = {
        "reader", "place", "tag", "time"
    };
    const char *values[4] = {
        movement->reader,
        movement->placement, 
        movement->pallet,
        time
    };
    
    char *parameters = to_parameters(4, keys, values);
    char *URL = concat3(BASE_URL, SERVER_MOVE_START, parameters);
    
	movements_add(URL);
	
	free(time);
	free(parameters);
	free(URL);
}

/// Ends a move with a pallet
/// Returns the result code of the connection
void move_end(const PalletMovement *movement)
{
    printf("Placing the pallet [%s] at place [%s]\n", movement->pallet, movement->placement);
    char *time = get_time_string();
    
    const char *keys[4] = {
        "reader", "place", "pallet", "time"
    };
    const char *values[4] = {
        movement->reader,
        movement->placement, 
        movement->pallet,
        time
    };
    
    char *parameters = to_parameters(4, keys, values);
    char *URL = concat3(BASE_URL, SERVER_MOVE_END, parameters);
    
	movements_add(URL);
	
	free(time);
	free(parameters);
	free(URL);
}

/// Store a movement to send
void movements_add(const char *URL)
{
    LinkedMovement *LM = LinkedMovement_create(URL);
    
    if(queue == NULL){
        queue = LM;
    }
    else {
        LinkedMovement *p = queue;
        while(p->next != NULL){
            p = p->next;
        }
        p->next = LM;
    }
}

/// Send all stored movements
void send_movements()
{
    if(queue == NULL)
        return;
        
    printf("\nSending data to server\n");
    SendData *data;
    int result;
    while(queue != NULL){
        data = SendData_create();
        printf("URL: %s\n", queue->URL);
        curl_connect(data, queue->URL);
        result = data->result;
        if(result == 0){
            if(data->size > 10)
                printf("Sent successfully with response: {%s}\n", data->memory);
            else
                printf("Sent successfully with response: { ... }\n");
        } else
            printf("Failed to send with error: [%i]\n", result);
        SendData_destroy(data);
        if(result != 0){
            return;
        }
        LinkedMovement *LM = queue;
        queue = queue->next;
        LinkedMovement_destroy(LM);
    }
}

