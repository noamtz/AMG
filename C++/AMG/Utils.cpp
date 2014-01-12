#include "Utils.h"
#include <iostream>

using namespace std;


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

void Utils::add(double* a, double* b, int size)
{
	for(int i=0; i<size; i++)
		a[i] += b[i];
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

void Utils::printVector(double* v, int size){
	cout<<"Print Vector:"<<endl<<endl;
	for(int i=0; i<size; i++){
		cout.precision(20);
		cout <<"v["<<i<<"]= "<<v[i]<< fixed << endl;
	}
	cout<<endl;
}

double* Utils::zerosVector(int size){
	double* v = new double[size];
	//cout<<"CREATE V WITH SIZE: "<<size<<" POINTER: "<<v<<endl;
	for(int i=0; i<size; i++)
		v[i] = 0;
	return v;
}

// Calculates log2 of number.  
int Utils::log2( double n )  
{  
    // log(n)/log(2) is log2.  
	int log22 = log( n ) / log( 2 );
    return log22;  
}

double* Utils::solve2var(double* v, double* f){

	double A[2][2] = { { 2, -1}, { -1, 2} };

	double IA[2][2];

	double D = 1/(A[0][0]*A[1][1] - A[0][1]*A[1][0]);//1/det(A)
	IA[0][0] = +D*A[1][1];
	IA[0][1] = -D*A[0][1];
	IA[1][0] = -D*A[1][0];
	IA[1][1] = +D*A[0][0];

	v[0] = IA[0][0]*f[0] + IA[0][1]*f[1];
	v[1] = IA[1][0]*f[0] + IA[1][1]*f[1];

}