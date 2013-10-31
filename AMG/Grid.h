#pragma once

class Grid
{
public:
	//DATA
	double* f; //right hand side
	double* v; //current approximation

	//c'tors
	Grid();

	//d'tors
	~Grid();

	static int getNumOfGrids(int);
};

