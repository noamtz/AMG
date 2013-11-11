#include "Worker.h"
#include "Utils.h"
#include <iostream>
using namespace std;

Worker::Worker(void)
{
}


Worker::~Worker(void)
{
}

double* Worker::restrict(double* v, int size){
	double* result = new double[size/2];//round down
	for(int i=1; i<size; i+=2){
		result[i/2] = (v[i-1] + 2*v[i] + v[i+1])/4.0;
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
//TODO:: changing  the index from 2
double* Worker::apply_operator(double* v, int size, double h2){
	//cout<<"STENCIL: "<<h2*-1<<", "<<h2*2<<","<<h2*-1<<" GRID SIZE: "<<size<<endl;
	double* temp = new double[size];
	// the v array has to have at least 2 elements
	temp[0] = 0;//h2*(2*v[0] - v[1]); //first edge case
	for(int i=1; i<size-1; i++){
		temp[i] = h2*(2*v[i] - v[i-1] - v[i+1]);
	}
	temp[size - 1] = 0;//h2*(2*v[size-1] - v[size-2]); //last edge case

	return temp;
}


double Worker::computeResidual(double* v, double* f, int size, double h){
	double* av = Worker::apply_operator(v, size, 1.0/h);
	double* residual = Utils::subtract(f, av, size);
	return Utils::inf_norm(residual, size);
}

// det = ad - bc
double det(double A[2][2])
{
	return(A[0][0]*A[1][1] - A[0][1]*A[1][0]);
}
// D = 1/det(A)
//
// .............. | d -b | . | D*d -D*b |
// inv = D * |-c a | = |-D*c D*a |
void inv(double A[2][2], double IA[2][2])
{
	double D = 1/det(A);
	IA[0][0] = +D*A[1][1];
	IA[0][1] = -D*A[0][1];
	IA[1][0] = -D*A[1][0];
	IA[1][1] = +D*A[0][0];
}
// |a b| |x| . |e|
// |c d| |y| = |f|
//
// |x| . |a b|-1 |e|
// |y| = |c d| .. |f|
void solve(double A[2][2], double C[2], double S[2])
{
	double IA[2][2];
	inv(A, IA);
	S[0] = IA[0][0]*C[0] + IA[0][1]*C[1];
	S[1] = IA[1][0]*C[0] + IA[1][1]*C[1];
}
void Worker::direct_solve(double* v, double* f){
	double A[2][2] = { { 2, -1}, { -1, 2} };
	solve(A, f, v);
}
