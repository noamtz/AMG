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

#define N 16384 // 2^14
#define NUM_SWEEPS_DOWN 2

#define PI 3.14159265359

void plot(Engine *ep, double* v, string name, int size);

int gridSize(int level){
	return pow(2, Utils::log2(N) - level);
}

void vcycle(int num, Grid* grids){
	//cout<<"V-Cycle: "<<num<<endl;
	clock_t begin = clock();

	int coarsest = Utils::log2(N) - 1;

	for (int i = 0; i<coarsest; i++){
		int grid_size = gridSize(i);
		double h = 1.0 / (grid_size );

		// coefficient of the grid
		double h2 = i > 0 ? pow(h, 2) : pow(1.0 / N, 2);

		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, h2, grid_size, NUM_SWEEPS_DOWN);

		//Compute residual
		double* Av = Worker::apply_operator(grids[i].v, grid_size, 1.0 / h2);
		grids[i].residual = Utils::subtract(grids[i].f, Av, grid_size);
		if (num == 25)
			cout << "";
		//Restrict
		grids[i + 1].f = Worker::restrict(grids[i].residual, grid_size);

		//initialize v on i+1 grid
		grids[i + 1].v = Utils::zerosVector(gridSize(i + 1));

	}


	for (int i = coarsest - 1; i >= 0; i--){
		int grid_size = gridSize(i);
		double h = 1.0 / (grid_size);
		// coefficient of the grid
		double h2 = i > 0 ? pow(h, 2) : pow(1.0 / N, 2);

		//Interpolate
		double* vh = Worker::interpolate(grids[i + 1].v, gridSize(i + 1));

		//Correction
		Utils::add(grids[i].v, vh, grid_size);

		//Relax 
		Smoother::relax(grids[i].v, grids[i].f, h2, grid_size, NUM_SWEEPS_DOWN);
	}

	clock_t end = clock();
	double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
	//cout<<"Elapsed time: "<<elapsed_secs<<" sec."<<endl;
}

int main(int argc, char* argv[]){
	Engine *ep = engOpen(NULL);

	Grid* grids = new Grid[Utils::log2(N)];

	//initialize data:
	grids[0].v = new double[N];
	grids[0].f = new double[N];



	for (int i = 0; i < N; i++){
		grids[0].v[i] = (sin((30 * i*PI) / N) + sin((28 * i*PI) / N) + sin((20 * i*PI) / N))*(1.0 / 3);
		grids[0].f[i] = 0;
	}
	double* residualPrevious = new double[N];
	//Compute residual
	double h2 = pow(1.0 / N, 2);
	double* Av = Worker::apply_operator(grids[0].v, gridSize(0), 1.0 / h2);
	residualPrevious = Utils::subtract(grids[0].f, Av, gridSize(0));


	plot(ep, grids[0].v, "vFirst", N);


	for (int i = 0; i < 60; i++){
		vcycle(i, grids);
		double rate = Utils::inf_norm(grids[0].residual, N) / Utils::inf_norm(residualPrevious, N) * 100;
		cout << "After " << i + 1 << " V-cycles rate: " << rate << endl;
		for (int j = 0; j < N; j++){
			residualPrevious[j] = grids[0].residual[j];
		}
	}

	//vcycle(2, grids);

	plot(ep, grids[0].v, "vLast", N);
	plot(ep, grids[0].residual, "residual", N);
	getchar();
	engClose(ep);

	return 0;
}


void plot(Engine *ep, double* v, string name, int size){
	mxArray* v_array = mxCreateDoubleMatrix(size, 1, mxREAL);
	double* pv = mxGetPr(v_array);
	for (int i = 0; i < size; i++)
		pv[i] = v[i];
	engPutVariable(ep, name.c_str(), v_array);

	string cmd = "plot(";
	cmd.append(name);
	cmd.append(")");

	engEvalString(ep, cmd.c_str());
}
