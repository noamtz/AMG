#include "Grid.h"
#include <math.h> 
//For Debug
#include <iostream> 
using namespace std;

Grid::Grid()
{
	
}


Grid::~Grid()
{
}


int Grid::getNumOfGrids(int number) // note: no static keyword here
{
	//Calculate log2 of number of points in the finest grid
	//Note: casting to int.
	return log( (double)number ) / log( 2.0 );
}
