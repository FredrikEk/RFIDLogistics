/*
 * verify_job.cc
 *
 *
 *
 *  Created on: Mar 5, 2014
 *      Author: philip
 */


#include <iostream>
#include <string>
#include <sstream>
#include <algorithm>
#include <ctime>
using namespace std;

long int place_ids [3] = {0x01012034F2, 0x0100A3A560, 0x010886B011};

bool isPlace(long int id);
void liftPallet(long int id);
void liftPlace(long int placeId,long int palletId);
char* timeStamp();

int main(){
	 string mystr;
	 long int id;
	 long int pallet_id;
	 long int place_id;
	 while(true){
		 cout << "Enter pallet/place id: ";
		 getline (cin,mystr);
		 stringstream(mystr) >> hex >> id;

		 if(isPlace(id)){
			 cout << "Enter pallet id: ";
			 getline (cin,mystr);
			 stringstream(mystr) >> hex >> pallet_id;
			 liftPlace(id, pallet_id);
		 }else{
			 liftPallet(id);
			 cout << "Enter place id where pallet is placed: ";
			 getline (cin,mystr);
			 stringstream(mystr) >> hex >> place_id;
			 if(isPlace(place_id)){
				 liftPlace(place_id, id);
			 }else{
				cerr << "Not a valid place" << endl;
			 }
		 }
	 }
	 return 0;
}

// Returns TRUE if id is in place_ids. Works only for arrays right now.
bool isPlace(long int id){
	if((find(place_ids, place_ids+3, id)) != place_ids+3){
		return true;
	}
	return false;
}

// Sends an pallet-ID and timestamp
// pallet-ID:timestamp
void liftPallet(long int id){
	char* ts = timeStamp();
	cout << hex << id;
	cout << ":";
	cout << ts << endl;
}

// Sends an pallet-ID, place-ID and timestamp
// pallet-id:place-id:timestamp
void liftPlace(long int placeId,long int palletId){
	char* ts = timeStamp();
	cout << hex << palletId;
	cout << ":";
	cout << hex << placeId;
	cout << ":";
	cout << ts << endl;
}

// Creates a timestamp for the time NOW.
char* timeStamp(){
	time_t now = time(0);
	char* dt = ctime(&now);
	return dt;
}




