 #include <stdio.h> 
#include <iostream> 
#include <math.h> 
#include <string>
using namespace std;


#define N 64
#define PI 3.14159265359
#define MAX 2

//
//int main(int argc, char* argv[])
//{
//	double v[N] , f[N] ;
//	double h = pow(1.0/N , 2);
//	double residual[N];
//	double normBefore =0 ,normAfter =0 ;
//	v[0] = 0;
//	f[0] = 0;
//	//Initialize approximate vector
//	//cout<<"Approximate vector:"<<endl<<endl;
//	for(int i=1; i<N; i++){
//		v[i] = (sin((i*PI)/N) + sin((3*i*PI)/N) + sin((6*i*PI)/N))*(1.0/3);
//		f[i] = 0;
//		//printNumeric("v", i,v[i]);
//	}
//	//cout<<endl<<"Relaxation:"<<endl<<endl;
//	double * result = work(v, f,h);
//
//	/*for(int i=0; i<MAX; i++){
//	printf ("Iteration %d: %f \n",i,result[i]);
//	}*/
//
//
//	getchar();
//	return 0;
//}
//
//
//void test1(double* v, double* f, double h){
//	printf ("\n");
//	double normBefore = norm(v, N);
//	printf ("norm before= %f \n",normBefore );
//	smoother(v, f,h,1);
//
//	printf ("\n");
//
//	for(int i=1; i<N; i++){
//		printf ("v[%d]= %f , ",i,v[i]);
//	}
//	printf ("\n");
//
//	double normAfter= norm(v, N);
//	printf ("norm after= %f \n",normAfter);
//
//	printf ("difference= %f \n",normBefore - normAfter);
//}
//
//void smoother(double* v, double* f, double h, int numIterations){
//	for(int j=0;j<numIterations;j++){
//		for(int i=1; i<N-1; i++){
//			v[i] = 0.5*(v[i-1] + v[i+1] + h*f[i]);
//		}
//	}
//}
//
//double* work(double* v, double* f, double h){
//	double result[MAX];
//	double normVal;
//	for(int i=0; i<MAX; i++){
//		//printNumeric("Iteration", i,norm(v, N));
//		normVal = norm(v, N);
//
//		smoother(v, f, h,1);
//		result[i] = normVal - norm(v, N); 
//	}
//
//	return result;
//}
//
//double norm(double* v, int length){
//	double max = 0.0;
//	for(int i=1; i<length; i++){
//		double val = abs(v[i]);
//		if(val > max)
//			max = val;
//	}
//	return max;
//}
//
//void printNumeric(string identifier, int ind,double d){
//	cout.precision(20);
//	cout <<identifier<<" at index: "<<ind<<" , value: "<< d<< fixed << endl;
//}
//
