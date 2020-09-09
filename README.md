# plagiarism_detector
Program is written in Java 
The program finds plagiarised data using three steps : shingling, minhashing, and locality sensitive hashing (LSH)
The output shows the following:
  a) shingling matrix and random permutations,
  b) signature matrix,
  c) LSH family atleast (0.2, 0.8, 0.997, 0.003)-sensitive
  d) plagiarism pairs

Input:
  1) Should contain files for finding plagiarism
  2) A file that contains all the step 1 files 
  

Compile the program:
  javac P1.java

Run the program by using the below command:
  java P1 t10.dat
