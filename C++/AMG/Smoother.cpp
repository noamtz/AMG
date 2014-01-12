#include "Smoother.h"

Smoother::Smoother(void)
{
}


Smoother::~Smoother(void)
{
}

void Smoother::relax(double* v, double* f , double h, int size , int num_sweeps){
	for(int j=0;j<num_sweeps;j++){
		for(int i=1; i<size-1; i++){
			v[i] = 0.5*(v[i-1] + v[i+1] + h*f[i]);
		}
	}
}