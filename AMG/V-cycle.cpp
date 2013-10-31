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
#define N 64
#define NUM_SWEEPS_DOWN 2

int gridSize(int level){
	return pow(2, NUM_GRIDS-level);
}



//int main(int argc, char* argv[]){
//
//	Grid grids[NUM_GRIDS];
//
//	int coarsest = NUM_GRIDS-1;
//
//	double h = 1.0/N;
//  double  = pow(1.0/N , 2);
//
//	for(int i=0; i<coarsest;i++){
//		int grid_size = gridSize(i);
//
//		//Relax 
//		Smoother::relax(grids[i].v, grids[i].f, h, grid_size, NUM_SWEEPS_DOWN);
//		
//		//Compute residual
//		Worker::apply_operator(grids[i].v);
//		double* residual = Utils::subtract(grids[i].f, grids[i].v, grid_size);
//
//		//Restrict
//		grids[i+i].f = Worker::restrict(residual, grid_size);
//	}
//
//	Worker::direct_solve(grids[coarsest].v ,grids[coarsest].f);
//
//	for(int i=coarsest-1; i >= 0; i--){
//		int grid_size = gridSize(i);
//		//Interpolate
//		double* vh = Worker::interpolate(grids[i+1].v);
//		
//		//Correction
//		grids[i].v = Utils::add(grids[i].v, vh, grid_size);
//
//		//Relax 
//		Smoother::relax(grids[i].v, grids[i].f, h, grid_size, NUM_SWEEPS_DOWN);
//	}
//
//
//	return 0;
//}