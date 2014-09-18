#ifndef RFIDCOM_HEADER
#define RFIDCOM_HEADER

/// Reads a tag and returns the ID
/// Make sure to free(var) the given pointer when done
char* call_reader();

#endif
