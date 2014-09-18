from ctypes import cdll, c_void_p, c_char_p, cast


rfidcom = cdll.LoadLibrary('./rfidcom.so')
rfidcom.restype = c_void_p


def call():
    """Wraps the binary rfidcom.so file which handles the communication with
    the RFID reader.

    Returns:
        A string returned by rfidcom.so.
    """
    # Returns a pointer to a char array response
    pointer = rfidcom.call_reader()
    # Cast the void pointer to a char pointer, get the value and decode it as
    # unicode
    string = cast(pointer, c_char_p).value
    if string is not None:
        # Only decode if the C module didn't return NULL
        string = string.decode('utf-8')
    # Free the memory
    rfidcom.free(pointer)
    return string
