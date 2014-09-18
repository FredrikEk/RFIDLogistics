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
#include <string.h>
#include "methods.h"
#include "connection.h"
#include "data.h"
#include "generic.h"

/// Holds all the PalletMovement not yet sent
LinkedMovement *queue = NULL;

/// Starts to move a pallet
void move_start(const PalletMovement *movement)
{
    printf("Starting to move pallet [%s] with forklift [%s]\n", movement->pallet, movement->placement);

    char *time = get_time_string();

    char *URL = concat2(BASE_URL, SERVER_MOVE_START);

    // Allocate an array of one char pointer and store the pointer to the
    // array as `headers`.
    // In other words, `headers` points to an array of pointers to strings.
    // Each of these strings is a header.
    const char* (*headers)[1] = (const char* (*)[1]) malloc(sizeof(char*));
    // Allocate memory for a string and get the pointer to it.
    const char* header = strdup("Content-Type: application/json");
    // Store the header inside the headers array.
    (*headers)[0] = header;

    // Format of the post data string.
    char post_data_format[] = "{\"tags\":[\"%s\",\"%s\"],\"reader\":\"%s\",\"date\":\"%s\",\"put_down\":\"false\"}";
    // Number of characters in the post_data string
    int post_data_length = strlen(post_data_format) + strlen(movement->reader)
        + strlen(movement->placement) + strlen(movement->pallet) + strlen(time)
        - 7;
    // Allocate memory for the post data string
    char* post_data = malloc(post_data_length * sizeof(char));
    // Construct the post data string
    sprintf(post_data, post_data_format, movement->pallet, movement->placement,
            movement->reader, time);

    // Make the movement
    movements_add(URL, post_data, headers, 1);
	
	free(time);
	free(URL);
}

/// Ends a move with a pallet
/// Returns the result code of the connection
void move_end(const PalletMovement *movement)
{
    printf("Placing the pallet [%s] at place [%s]\n", movement->pallet, movement->placement);

    // TODO: Refactor this code and the code above in move_start, since it's
    // mostly the same and should be shared somehow.
    char *time = get_time_string();

    char *URL = concat2(BASE_URL, SERVER_MOVE_START);

    const char* (*headers)[1] = (const char* (*)[1]) malloc(sizeof(char*));
    const char* header = strdup("Content-Type: application/json");
    (*headers)[0] = header;

    char post_data_format[] = "{\"tags\":[\"%s\",\"%s\"],\"reader\":\"%s\",\"date\":\"%s\",\"put_down\":\"true\"}";
    int post_data_length = strlen(post_data_format) + strlen(movement->reader)
        + strlen(movement->placement) + strlen(movement->pallet) + strlen(time)
        - 7;
    char* post_data = malloc(post_data_length * sizeof(char));
    sprintf(post_data, post_data_format, movement->pallet, movement->placement,
            movement->reader, time);

    movements_add(URL, post_data, headers, 1);

    free(time);
    free(URL);
}

/// Store a movement to send
void movements_add(const char *URL, const char post_data[],
        const char* (*headers)[], const int num_headers)
{
    LinkedMovement *LM = LinkedMovement_create(URL, post_data, headers, num_headers);

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

        if (queue->post_data) {
            printf("Post data: %s\n", queue->post_data);
            if (queue->headers) {
                // If there are headers and post data
                int i;
                for (i = 0; i < queue->num_headers; ++i) {
                    printf("Header: %s\n", (*queue->headers)[i]);
                }
                curl_connect5(data, queue->URL, queue->post_data,
                        queue->headers, queue->num_headers);
            } else {
                // If there is post data but no additional headers
                curl_connect3(data, queue->URL, queue->post_data);
            }
        } else {
            // Regular GET request
            curl_connect2(data, queue->URL);
        }

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

