#include "Utils.h"


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