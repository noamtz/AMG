#pragma once
class Tests
{
public:
	Tests(void);
	~Tests(void);

	static void testRelax();
	static void testApplyOperator();	
	static void addVectors();

	//Tests utils
	static void printNumeric(char* identifier, int ind,double d);

};

