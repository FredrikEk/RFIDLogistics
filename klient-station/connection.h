/*****************************************************************************
 *	  ____  _____ ___ ____  
 *   |  _ \|  ___|_ _|  _ \ 
 *   | |_) | |_   | || | | |
 *   |  _ <|  _|  | || |_| |
 *   |_| \_\_|   |___|____/ 
 *	 Author: Marc Jamot
 *
 *   Handles the connection to the server
 *
 *****************************************************************************/

#ifndef CONNECTION_HEADER
#define CONNECTION_HEADER

#include "data.h"

/// The main URL to the server
static const char* BASE_URL = "http://localhost:9000";

/// URL for start moving a pallet
static const char* SERVER_MOVE_START = "/client/startMove";

/// URL for end moving a pallet
static const char* SERVER_MOVE_END = "/client/endMove";

/// Sends data and puts the response in the given struct
void curl_connect(SendData *send_data, const char *URL);

/// Turns 2 string[] into a single string ready to be used as GET parameters
// Make sure to free(var) the given pointer when done
char* to_parameters(const int numKeyValues, const char **keys, const char **values);

/// Concats 2 strings in new allocated memory
/// Make sure to free(var) the given pointer when done
char* concat2(const char *s1, const char *s2);

/// Concats 3 strings in new allocated memory
/// Make sure to free(var) the given pointer when done
char* concat3(const char *s1, const char *s2, const char *s3);

/// Parses data received from the server and fills the given struct with it
size_t GotDataFromServer(void *contents, size_t size, size_t nmemb, void *pointer);

#endif
