#pragma once
#include <math.h>

class Worker
{
public:
	Worker(void);
	~Worker(void);

	static double* restrict(double* v, int size);
	static double* interpolate(double* v, int size);
	static double* apply_operator(double* v, int size , int hfactor);
	static void direct_solve(double* v, double* f);
};

