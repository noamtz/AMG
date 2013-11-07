#include <math.h>
#include <iostream>
#include <stdio.h> 

#include "Tests.h"
#include "Worker.h"
#include "Smoother.h"
#include "Utils.h"

using namespace std;

#define PI 3.14159265359

Tests::Tests(void)
{
}


Tests::~Tests(void)
{
}


void Tests::testApplyOperator(){
	int N = 16;
	double h = 1.0/N;
	double* v = new double[N];
	double* f = new double[N];

	int level = 0;
	double hfactor = level > 0 ? pow(1.0/(2*level*h) , 2) : pow(1.0/h , 2);

	Worker::apply_operator(v,N,hfactor);
}

void Tests::testRelax(){
	int N = 8 , num_sweeps = 10;
	double h = 1.0/N;
	double* v = new double[N];
	double* f = new double[N];

	for(int i=0; i<N; i++){
		//v[i] = (sin((i*PI)/N) + sin((3*i*PI)/N) + sin((6*i*PI)/N))*(1.0/3);
		v[i] = (sin((i*PI)/N) + sin((3*i*PI)/N) + sin((6*i*PI)/N))*(1.0/3);
		f[i] = 0;
	}

	int level = 0;
	double hfactor = level > 0 ? pow((2*level*h) , 2) : pow(h , 2);

	for(int i=0; i<N; i++){
		printNumeric("v",i,v[i]);
	}

	cout<<endl<<endl<<endl;

	Smoother::relax(v,f,hfactor,N,2);

	for(int i=0; i<N; i++){
		printNumeric("v",i,v[i]);
	}

	getchar();
}

void Tests::addVectors(){
	double a[3] = {1, 2 , -3};
	double b[3] = {-2 ,3 , 10};
	double res[3] = {-1, 5, 7};
	Utils::add(a,b, 3);
	bool isEqual = true;
	for(int i=0; i<3; i++){
		if(res[i] != a[i])
			isEqual = true;
	}
	cout<<"Utils::add 2 vectors is working properly? answer: "<<isEqual<<endl;
}


void Tests::printNumeric(char* identifier, int ind,double d){
	cout.precision(20);
	cout <<identifier<<" at index: "<<ind<<" , value: "<< d<< fixed << endl;
}

//int main(){
//	double v[2], f[2];
//	v[0] = 0;
//	v[1] = 0;
//	f[0] = 1;
//	f[1] = 5;
//	Worker::direct_solve(v,f);
//	Utils::printVector(v,2);
//
//	getchar();
//	return 0;
//}

