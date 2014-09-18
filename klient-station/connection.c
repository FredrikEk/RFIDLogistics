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

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <curl/curl.h>
#include "connection.h"
#include "data.h"


/// Sends data and puts the response in the given struct
void curl_connect(SendData *send_data, const char *URL){
    
    // The data to return
    send_data->memory = malloc(1); // Will be grown when receiving data
    send_data->size = 0;
    
    CURL *curl;
    CURLcode res;
    curl = curl_easy_init();
    if(curl){
        // The URL to fetch
    	curl_easy_setopt(curl, CURLOPT_URL, URL);
    	// The method to pass the data
    	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, GotDataFromServer);
    	// Pointer to the structure to fill with data
    	curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)send_data);
    	
    	// Execute the call
    	res = curl_easy_perform(curl);
    	
    	// Store result
    	send_data->result = res;
    	
    	if(res != CURLE_OK){
    		printf("Call to server failed [code: %i]: %s\n", res, curl_easy_strerror(res));
    	}
    	
    	curl_easy_cleanup(curl);
    }
}

/// Turns 2 string[] into a single string ready to be used as GET parameters
/// Make sure to free(var) the given pointer when done
char* to_parameters(const int numKeyValues, const char **keys, const char **values)
{
    if(numKeyValues == 0){
        return NULL;
    }
    // Initial size for html signs (?, =, &), add 1 for null operator
    int totalSize = numKeyValues * 2 + 1;
    int i;
    for(i = 0; i < numKeyValues; i++){
        if(keys[i] == NULL){
            printf("to_parameters: Key #%i was NULL\n", i);
            return NULL;
        }
        if(values[i] == NULL){
            printf("to_parameters: Value #%i was NULL\n", i);
            return NULL;
        }
        totalSize += strlen(keys[i]);
        totalSize += strlen(values[i]);
    }
    char* string = malloc(totalSize); //sizeof(char) * 
    int index = 0;
    string[index++] = '?';
    int size;
    for(i = 0; i < numKeyValues; i++){
        size = strlen(keys[i]);
        memcpy(string+index, keys[i], size);
        index += size;
        string[index++] = '=';
        size = strlen(values[i]);
        memcpy(string+index, values[i], size);
        index += size;
        if(i+1 != numKeyValues){
            string[index++] = '&';
        }
    }
    string[index] = '\0';
    return string;
}

/// Concats 2 strings in new allocated memory
/// Make sure to free(var) the given pointer when done
char* concat2(const char *s1, const char *s2)
{
    size_t len1 = strlen(s1);
    size_t len2 = strlen(s2);
    char *result = malloc(len1+len2+1);
    if(!result) return NULL;
    memcpy(result, s1, len1);
    memcpy(result+len1, s2, len2+1);
    return result;
}

/// Concats 3 strings in new allocated memory
/// Make sure to free(var) the given pointer when done
char* concat3(const char *s1, const char *s2, const char *s3)
{
    size_t len1 = strlen(s1);
    size_t len2 = strlen(s2);
    size_t len3 = strlen(s3);
    char *result = malloc(len1+len2+len3+1);
    if(!result) return NULL;
    memcpy(result, s1, len1);
    memcpy(result+len1, s2, len2);
    memcpy(result+len1+len2, s3, len3+1);
    return result;
}

/// Parses data received from the server and fills the given struct with it
size_t GotDataFromServer(void *contents, size_t size, size_t nmemb, void *pointer)
{
    size_t realsize = size * nmemb;
    SendData *send_data = (SendData *)pointer;
    
    int size_to_change_to = send_data->size + realsize + 1;
    send_data->memory = realloc(send_data->memory, size_to_change_to);
    if(send_data->memory == NULL) {
        printf("GotDataFromServer(...): Not enough memory (realloc returned NULL)\n");
        return 0;
    }
    
    memcpy(&(send_data->memory[send_data->size]), contents, realsize);
    send_data->size += realsize;
    send_data->memory[send_data->size] = 0;
    
    return realsize;
}
