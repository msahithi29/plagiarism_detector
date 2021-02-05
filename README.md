# plagiarism_detector
Program is written in Java.
It finds plagiarised pairs using three steps : shingling, minhashing, and locality sensitive hashing (LSH)
The output shows the following:
  1) shingling matrix and random permutations,
  2) signature matrix,
  3) LSH family atleast (0.2, 0.8, 0.997, 0.003)-sensitive
  4) plagiarism pairs

Input:
  1) Should contain files for finding plagiarism
  2) A file that contains all the step 1 files 
  

Compile the program:
  javac P1.java

Run the program by using the below command:
  java P1 < t10.dat
