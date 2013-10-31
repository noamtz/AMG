#pragma once
class Smoother
{
public:
	Smoother(void);
	~Smoother(void);


	static void relax(double* v, double* f, double h, int size, int num_sweeps);
};

