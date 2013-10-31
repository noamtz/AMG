#include "Worker.h"

Worker::Worker(void)
{
}


Worker::~Worker(void)
{
}

double* Worker::restrict(double* v, int size){
	double* result = new double[size/2];//round down
	for(int i=1; i<size; i+=2){
		result[i/2] = (v[i-1] + 2*v[i] + v[i+1]) * (0.25);
	}
	return result;
}

double* Worker::interpolate(double* v, int size){
	int target = size*2 + 1;//round up

	double* result = new double[target];
	for(int i=0; i<target; i++){

		//first edge case
		if(i == 0) 
			result[i] = v[i];
		//last edge case
		else if(i == (target-1))
			result[i] = v[i/2 - 1];
		// odd
		else if(i % 2 == 1)
			result[i] = 2*v[i/2];
		//even
		else
			result[i] = v[i/2 - 1] + v[i/2];

		//normalizing
 		result[i] = 0.5 * result[i]; 
	}
	return result;
}

void Worker::apply_operator(double* v, int hfactor){

}

void Worker::direct_solve(double* v, double* f){

}