#include "Utils.h"
#include <iostream>

Utils::Utils(void)
{
}


Utils::~Utils(void)
{
}


double* Utils::subtract(double* a, double* b, int size)
{
	double* result = new double[size];
	for(int i=0; i<size; i++)
		result[i] = a[i]-b[i];
	return result;
}

double* Utils::add(double* a, double* b, int size)
{
	double* result = new double[size];
	for(int i=0; i<size; i++)
		result[i] = a[i]+b[i];
	return result;
}

double Utils::inf_norm(double* target , int size){
	double norm = 0;
	for(int j=0; j<size; j++){
		double temp;
		if(target[j] < 0)
			temp = -1*target[j];
		else
			temp = target[j];

		if(temp > norm)
			norm = temp;
	}

	return norm;

}