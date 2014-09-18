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

#ifndef DATA_HEADER
#define DATA_HEADER

/// The ID for this reader
static const char* const FLOOR_ID = "0000000000";

/// The ID for this reader
static const char* const READER_ID = "0000000001";

/// ID for the placements
static const char* const PLACEMENT[] = {
    "01012034F2", "0100A3A560", "010886B011"
};

/// Used to hold the data received from the server
typedef struct SendDataStruct
{
    // 0: OK
    // 1: Just initialized, not populated with data
    // 7: Could not connect to server
    int result;
    char* memory;
    size_t size;
} SendData;

/// Used to hold a pallet movement
typedef struct PalletMovementStruct
{
    int reader_size;
    char *reader;
    int placement_size;
    char *placement;
    int pallet_size;
    char *pallet;
    char pallet2_size;
    char *pallet2;
    int id_size;
    char *id;
} PalletMovement;

/// Used to hold a list of movements to send
typedef struct LinkedMovementStruct
{
    char *URL;
    struct LinkedMovementStruct *next;
} LinkedMovement;

/// Allocates a new SendData
SendData* SendData_create();

/// Sets the given data to the given struct
/// Free previous memory if any
void SendData_set(SendData *send_data, int result, const char *memory, size_t size);

/// Deallocates the given struct
/// Returns NULL so the pointer reference can be set NULL on same line
void* SendData_destroy(SendData *send_data);

/// Allocates a new PalletMovement
PalletMovement* PalletMovement_create();

// Links an pallet/place with an id
void PalletMovement_id(PalletMovement *pallet_movement, const char *id);

/// Allocates a new PalletMovement and copies the data over to it
PalletMovement* PalletMovement_copy(const PalletMovement *pallet_movement);

/// Resets the PalletMovement
/// Instead of destroy and create
void PalletMovement_reset(PalletMovement *pallet_movement);

/// Sets the placement of the given PalletMovement
void PalletMovement_setPlacement(PalletMovement *pallet_movement, const char *placement);

/// Sets the pallet of the given PalletMovement
void PalletMovement_setPallet(PalletMovement *pallet_movement, const char *pallet, const char *pallet2);

/// Deallocates the given struct
/// Returns NULL so the pointer reference can be set NULL on same line
void* PalletMovement_destroy(PalletMovement *pallet_movement);

/// Allocates a new LinkedMovement
LinkedMovement* LinkedMovement_create(const char *URL);

/// Deallocates the given struct
/// Returns NULL so the pointer reference can be set NULL on same line
void* LinkedMovement_destroy(LinkedMovement* LM);

#endif
