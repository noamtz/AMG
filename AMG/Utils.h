#pragma once
class Utils
{
public:
	Utils(void);
	~Utils(void);

	static double* subtract(double* a, double* b, int size);
	static void add(double* a, double* b, int size);
	static double inf_norm(double* target , int size);
	static void printVector(double* v, int size);
	static double* zerosVector(int size);
	static int log2( double n );
	static double l2_norm(double* target, int size);
};

