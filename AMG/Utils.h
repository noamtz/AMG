#pragma once
class Utils
{
public:
	Utils(void);
	~Utils(void);

	static double* subtract(double* a, double* b, int size);
	static double* add(double* a, double* b, int size);
	static double inf_norm(double* target , int size);
};

