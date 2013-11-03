#include <stdio.h> 
#include <iostream> 
#include <math.h> 
#include <string>

#include "Grid.h"
#include "Smoother.h"
#include "Worker.h"
#include "Utils.h"


using namespace std;

#define NUM_GRIDS  10
#define N 1024
#define NUM_SWEEPS_DOWN 2

#define PI 3.14159265359

int gridSize(int level){
	return pow(2, NUM_GRIDS-level);
}




void init(Grid* grids){
	Grid finest;
	finest.v = new double[N];
	finest.f = new double[N];

	for(int i=0; i<N; i++){
		finest.v[i] = (sin((i*PI)/N) + sin((3*i*PI)/N) + sin((6*i*PI)/N))*(1.0/3);
		finest.f[i] = 0;
	}

	grids[0] = finest;

	double h = 1.0/N;
	int i = 0;
	double hfactor = i > 0 ? pow(1.0/(2*i*h) , 2) : pow(1.0/h , 2);

	//DEBUG
	double* tempAv = Worker::apply_operator(grids[0].v, N, hfactor);
	double* tempResidual = Utils::subtract(grids[0].f, tempAv, N);
	double inf_norm = Utils::inf_norm(tempResidual, N);
	printf("BEFORE:: residual inf norm is: %f", inf_norm);
	//DEBUG

}


int main(int argc, char* argv[]){

	Grid grids[NUM_GRIDS];

	int coarsest = NUM_GRIDS-1;

	double h = 1.0/N;

	/*init(grids);
	getchar();*/

	//initialize data:
	grids[0].v = new double[N];
	grids[0].f = new double[N];

	for(int i=0; i<N; i++){
		grids[0].v[i] = (sin((i*PI)/N) + sin((3*i*PI)/N) + sin((6*i*PI)/N))*(1.0/3);
		grids[0].f[i] = 0;
	}



	for(int i=0; i<coarsest;i++){
		int grid_size = gridSize(i);
		// coefficient of the grid
		double hfactor = i > 0 ? pow(1.0/(2*i*h) , 2) : pow(1.0/h , 2);

		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, hfactor, grid_size, NUM_SWEEPS_DOWN);

		//Compute residual
		double* av = Worker::apply_operator(grids[i].v, grid_size, hfactor);
		double* residual = Utils::subtract(grids[i].f, av, grid_size);

		//Restrict
		grids[i+1].f = Worker::restrict(residual, grid_size);
		//initialize v on i+1 grid
		grids[i+1].v =  Worker::restrict(grids[i].v, grid_size);
	}

	Smoother::relax(grids[coarsest].v, grids[coarsest].f, pow(1.0/(2*coarsest*h) , 2), gridSize(coarsest), NUM_SWEEPS_DOWN);
	//Worker::direct_solve(grids[coarsest].v ,grids[coarsest].f);

	for(int i=coarsest-1; i >= 0; i--){
		int grid_size = gridSize(i);
		// coefficient of the grid
		double hfactor = i > 0 ? pow(1.0/(2*i*h) , 2) : pow(1.0/h , 2);

		//Interpolate
		double* vh = Worker::interpolate(grids[i+1].v, grid_size);

		//Correction
		grids[i].v = Utils::add(grids[i].v, vh, grid_size);

		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, hfactor, grid_size, NUM_SWEEPS_DOWN);
	}


	int i = 0;
	double hfactor = i > 0 ? pow(1.0/(2*i*h) , 2) : pow(1.0/h , 2);

	//DEBUG
	double* tempAv = Worker::apply_operator(grids[0].v, N, hfactor);
	double* tempResidual = Utils::subtract(grids[0].f, tempAv, N);
	double inf_norm = Utils::inf_norm(tempResidual, N);
	cout.precision(20);
	cout <<"AFTER:: residual inf norm is: "<<inf_norm<< fixed << endl;
	//DEBUG

	getchar();

	return 0;
}