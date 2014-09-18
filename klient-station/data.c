/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot
 *
 *   Main data structure and access methods
 *
 *****************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "data.h"

/// Allocates a new SendData
SendData* SendData_create()
{
    SendData *SD = malloc(sizeof(SendData));
    SD->result = 1;
    SD->memory = NULL;
    SD->size = 0;
    return SD;
}

/// Sets the given data to the given struct
/// Free previous memory if any
void SendData_set(SendData *send_data, int result, const char *memory, size_t size)
{
    if(send_data == NULL){
        printf("SendData_set was given NULL as structure");
        return;
    }
    send_data->result = result;
    if(send_data->memory != NULL){
        free(send_data->memory);
    }
    send_data->memory = strdup(memory);
    send_data->size = size;
}

/// Deallocates the given struct
/// Returns NULL so the pointer reference can be set NULL on same line
void* SendData_destroy(SendData *send_data)
{
    free(send_data->memory);
    free(send_data);
    return NULL;
}

/// Allocates a new PalletMovement
PalletMovement* PalletMovement_create(char *reader)
{
    printf("Created new palletmovment for reader [%s]\n", reader);
    PalletMovement *PM = malloc(sizeof(PalletMovement));
    PM->reader_size = strlen(reader);
    PM->reader = strdup(reader);
    PM->placement_size = 0;
    PM->placement = NULL;
    PM->pallet_size = 0;
    PM->pallet = NULL;
    PM->pallet2_size = 0;
    PM->pallet2 = NULL;
    PM->id_size = 0;
    PM->id = NULL;
    return PM;
}

/// Allocates a new PalletMovement and copies the data over to it
PalletMovement* PalletMovement_copy(const PalletMovement *pallet_movement)
{
    PalletMovement* PM = PalletMovement_create(pallet_movement->reader);
    PM->reader_size = pallet_movement->reader_size;
    PM->reader = strdup(pallet_movement->reader);
    PM->placement_size = pallet_movement->placement_size;
    PM->placement = strdup(pallet_movement->placement);
    PM->pallet_size = pallet_movement->pallet_size;
    PM->pallet = strdup(pallet_movement->pallet);
    return PM;
}

//Links an pallet or place with an id
void PalletMovement_id(PalletMovement *pallet_movement, const char* id)
{
    printf("Adding id [%s] to pallet\n", id);
    if(pallet_movement == NULL){
        printf("PalletMovement_id was given NULL as structure");
        return;
    }
    if(pallet_movement->id != NULL){
        free(pallet_movement->id);
    }
    if(id == NULL){
        pallet_movement->id = NULL;
        pallet_movement->id_size = 0;
    } else {
        pallet_movement->id = strdup(id);
        pallet_movement->id_size = strlen(id);
    }
}

/// Resets the PalletMovement
/// Instead of destroy and create
void PalletMovement_reset(PalletMovement *pallet_movement)
{
    if(pallet_movement->placement != NULL)
        free(pallet_movement->placement);
    if(pallet_movement->pallet != NULL)
        free(pallet_movement->pallet);
    pallet_movement->placement = NULL;
    pallet_movement->placement_size = 0;
    pallet_movement->pallet = NULL;
    pallet_movement->pallet_size = 0;
}

/// Sets the placement of the given PalletMovement
void PalletMovement_setPlacement(PalletMovement *pallet_movement, const char *placement)
{
    printf("Set placement\n");
    if(pallet_movement == NULL){
        printf("PalletMovement_setPlacement was given NULL as structure");
        return;
    }
    if(pallet_movement->placement != NULL){
        free(pallet_movement->placement);
    }
    if(placement == NULL){
        pallet_movement->placement = NULL;
        pallet_movement->placement_size = 0;
    } else {
        pallet_movement->placement = strdup(placement);
        pallet_movement->placement_size = strlen(placement);
    }
}

/// Sets the pallet of the given PalletMovement
void PalletMovement_setPallet(PalletMovement *pallet_movement, const char *pallet, const char *pallet2)
{
    printf("Set pallet\n");
    if(pallet_movement == NULL){
        printf("PalletMovement_setPallet was given NULL as structure");
        return;
    }
    if(pallet_movement->pallet != NULL){
        free(pallet_movement->pallet);
    }
    if(pallet_movement->pallet2 != NULL){
        free(pallet_movement->pallet2);
    }
    if(pallet == NULL){
        pallet_movement->pallet = NULL;
        pallet_movement->pallet_size = 0;
    } else {
        pallet_movement->pallet = strdup(pallet);
        pallet_movement->pallet_size = strlen(pallet);
    }
        if(pallet2 == NULL){
        pallet_movement->pallet2 = NULL;
        pallet_movement->pallet2_size = 0;
    } else {
        pallet_movement->pallet2 = strdup(pallet2);
        pallet_movement->pallet2_size = strlen(pallet2);
    }
}

/// Deallocates the given struct
/// Returns NULL so the pointer reference can be set NULL on same line
void* PalletMovement_destroy(PalletMovement *pallet_movement)
{
    free(pallet_movement->reader);
    free(pallet_movement->placement);
    free(pallet_movement->pallet);
    free(pallet_movement);
    return NULL;
}

/// Allocates a new LinkedMovement
LinkedMovement* LinkedMovement_create(const char *URL)
{
    LinkedMovement *LM = malloc(sizeof(LinkedMovement));
    LM->URL = strdup(URL);
    LM->next = NULL;
    return LM;
}

/// Deallocates the given struct; Does NOT deallocate next
/// Returns NULL so the pointer reference can be set NULL on same line
void* LinkedMovement_destroy(LinkedMovement* LM)
{
    free(LM->URL);
    free(LM);
    return NULL;
}





