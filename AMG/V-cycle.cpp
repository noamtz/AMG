#include <stdio.h> 
#include <iostream> 
#include <math.h> 
#include <string>
#include <ctime>

#include "Grid.h"
#include "Smoother.h"
#include "Worker.h"
#include "Utils.h"
#include "engine.h"

using namespace std;

#define N 1048576
#define NUM_SWEEPS_DOWN 2

#define PI 3.14159265359

void plot(Engine *ep, double* v,string name, int size);

int gridSize(int level){
	return pow(2, Utils::log2(N)-level)-1;
}

void vcycle(int num, Grid* grids){
	cout<<"V-Cycle: "<<num<<endl;
	clock_t begin = clock();

	int coarsest = Utils::log2(N)-1;

	for(int i=0; i<coarsest;i++){
		int grid_size = gridSize(i);
		double h =  1.0/(grid_size+1);

		// coefficient of the grid
		double h2 = i > 0 ? pow(h , 2) : pow(1.0/N , 2);

		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, h2, grid_size, NUM_SWEEPS_DOWN);

		//Compute residual
		double* Av = Worker::apply_operator(grids[i].v, grid_size, 1.0/h2);
		double* residual = Utils::subtract(grids[i].f, Av, grid_size);

		//Restrict
		grids[i+1].f = Worker::restrict(residual, grid_size);

		//initialize v on i+1 grid
		grids[i+1].v =  Utils::zerosVector(gridSize(i+1));
				
	}

	double ch2 = pow(1.0/(gridSize(coarsest)) , 2);
	Smoother::relax(grids[coarsest].v, grids[coarsest].f, ch2, gridSize(coarsest), NUM_SWEEPS_DOWN);

	for(int i=coarsest-1; i >= 0; i--){
		int grid_size = gridSize(i);
		double h =  1.0/(grid_size+1);
		// coefficient of the grid
		double h2 = i > 0 ? pow(h , 2) : pow(1.0/N , 2);

		//Interpolate
		double* vh = Worker::interpolate(grids[i+1].v, grid_size);

		//Correction
		Utils::add(grids[i].v, vh, grid_size);
		
		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, h2, grid_size, NUM_SWEEPS_DOWN);
	}

	clock_t end = clock();
	double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
	cout<<"Elapsed time: "<<elapsed_secs<<" sec."<<endl;
}

int main(int argc, char* argv[]){
	Engine *ep = engOpen(NULL);

	Grid* grids = new Grid[Utils::log2(N)];

	//initialize data:
	grids[0].v = new double[N-1];
	grids[0].f = new double[N-1];

	for(int i=1; i<N; i++){
		grids[0].v[i-1] = (sin((3*i*PI)/N) + sin((6*i*PI)/N) + sin((i*PI)/N))*(1.0/3);
		grids[0].f[i-1] = 0;
	}

	plot(ep,grids[0].v,"vFirst",N);

	vcycle(1, grids);
	//vcycle(2, grids);
	plot(ep,grids[0].v,"vLast",N);

	getchar();
	engClose(ep);

	return 0;
}


void plot(Engine *ep, double* v,string name, int size){
	mxArray* v_array = mxCreateDoubleMatrix(size,1,mxREAL);
	double* pv = mxGetPr(v_array);
	for(int i=0; i<size; i++)
		pv[i] = v[i];
	engPutVariable(ep,name.c_str(),v_array);

	string cmd = "plot(";
	cmd.append(name);
	cmd.append(")");

	engEvalString(ep,cmd.c_str());
}
