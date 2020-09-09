import java.io.*;
import java.util.*;
public class P1 
{
    ArrayList<String> docList = new ArrayList<>();
    int docCount=0;
    int hashcount = 100;
    int prime = 3;
    List<String> shingleList;
    int[][] hashArray, minhash;
    HashSet<String> shingleSet = new HashSet<>();
    int shingleMatrix[][], setsPermMatrix[][], shingleSortMatrix[][];
    ArrayList<Integer> possible_band;
    ArrayList<Integer> possible_row;
    ArrayList<Double> probablities;
    int ideal_item=0;
    public static void main(String[ ] args) throws IOException
    {
        String s;
        P1 p1 = new P1();
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //reading the console
            System.out.println("******Input files*********");
	    while ( (s = in.readLine( )) != null )
            {
                p1.readFiles(s); // reading the files

            }
            if(p1.docList.size()>1) {
                p1.create_shingleMatrix(); //creating shingles matrix
                p1.create_randomperm(); //random permutation generation
                p1.append_randperm_to_shingles(); //display input document term table and random permutations
                p1.create_minHashing();//generating signature matrix
                p1.choose_bands(0.8); //hyperparameter tuning with assumed similiarity 0.8
                p1.lsh(); //lsh to find candidate pairs and their similarities
            }
        }
        catch(FileNotFoundException f)
        {
            System.out.println("File not found");
            return;
        }
    }
    public void readFiles(String s) throws IOException
    {
        String s1[] = s.split("\\s+");
        for(int i=0;i<s1.length;i++)
        {
            System.out.println(s1[i]);
            docCount= docCount+1;
            String line1;
            try 
	    {
                BufferedReader bufferedReader1=new BufferedReader(new FileReader(s1[i]));
                while ((line1 = bufferedReader1.readLine()) != null) 
		{
                    String preprocessed_line = preprocess(line1); // handling punctuation and lowercase
                    docList.add(preprocessed_line);
                    create_shingles(preprocessed_line);
                }
            }
            catch(FileNotFoundException fe)
            {
                System.out.println("File not found");
            }
        }
    }
    public String preprocess(String line1)
    {
        String remove_punc = line1.replaceAll("[^a-zA-Z0-9]"," ").toLowerCase().replaceAll("( )+", " ");
        return remove_punc;
    }
    public void create_shingles(String preprocessed_line)
    {
        for(int i=0;i<preprocessed_line.length()-8;i++)
        {
            shingleSet.add(preprocessed_line.substring(i,i+9));
        }
    }
    public void create_shingleMatrix()
    {
        System.out.println("\n\nShingle size:" + shingleSet.size());
        System.out.println("Doc Count:" + docCount);
        shingleList  = new ArrayList<>(shingleSet);
        shingleMatrix = new int[shingleList.size()][docCount];
        for(int i=0;i<shingleList.size();i++)
        {
            for(int j=0;j<docList.size();j++)
            {
                shingleMatrix[i][j] =0;
            }
        }
        for(int k=0;k<shingleList.size();k++) 
	{
            for (int j = 0; j < docList.size(); j++) 
	    {
                for (int i = 0; i < docList.get(j).length() - 8; i++) 
		{
                    if (shingleList.get(k).equals(docList.get(j).substring(i, i + 9))) 
		    {
                        shingleMatrix[k][j]=1;
                    }
                }
            }
        }
        shingleSortMatrix = new int[shingleList.size()][docCount+1];
        for(int i=0;i<shingleList.size();i++)
        {
            int t=0;
            for(int j=0;j<docList.size();j++)
            {
                shingleSortMatrix[i][j]=shingleMatrix[i][j];
                t+=shingleSortMatrix[i][j];

            }
            shingleSortMatrix[i][docList.size()] = t;
        }
    }
    public void create_randomperm()
    {
        ArrayList<String> hashFunctions = new ArrayList<>();
        int[][] hashTempArray= new int[hashcount][shingleList.size()];
        hashArray = new int[shingleList.size()][hashcount];
        find_prime(); //finding the highest prime as the hash table size
        int a,b;
        Random random= new Random(10); //random generation and setting seed 
        for (int i = 0; i < hashcount; i++)
        {
            a = random.nextInt(shingleList.size());
            b = random.nextInt(shingleList.size());
            hashFunctions.add("(" + a + " * x + " + b + ") mod " + prime) ;//hash function of form (ax+b) mod p
            for(int j=0;j<shingleList.size();j++) 
	    {
                hashTempArray[i][j] = (a * j + b) % prime;
            }
        }
        for(int i=0;i<shingleList.size();i++)
	{
            for(int j=0;j<hashcount;j++)
	    {
                hashArray[i][j]=hashTempArray[j][i];
            }
        }
    }
    public void find_prime()
    {
        if(shingleList.size()<2)
        {
            prime = 3;
        }
        else 
	{
            for (int n = shingleList.size(); n < 2 * shingleList.size(); n++) 
	    {
                int h = n / 2;
                int f = 0;
                for (int i = 2; i <= h; i++) 
 		{
                    if (n % i == 0) 
		    {
                        f = 1;
                    }
                }
                if (f == 0) 
		{
                    //System.out.println("prime number:"+n);
                    prime=n;
                    break;
                }
            }
        }
    }
    public void append_randperm_to_shingles()
    {
        int length= docList.size() + hashcount;
        setsPermMatrix = new int[shingleList.size()][length];
        System.out.println("\n*********Document Term table and random permutations**********");
        for(int i=0;i<shingleList.size();i++)
        {
            for(int j=0;j<hashcount;j++)
            {
                setsPermMatrix[i][j]=hashArray[i][j];
            }
        }
        for(int i=0;i<shingleList.size();i++)
        {
            for(int j=hashcount;j<length;j++)
            {
                setsPermMatrix[i][j]=shingleMatrix[i][j-hashcount];
            }
        }
        for(int i=0;i<shingleList.size();i++)
	{
            System.out.print(shingleList.get(i) + "\t\t");
            for(int j=0;j<length;j++)
	    {
                System.out.format("%4d",setsPermMatrix[i][j]);
                System.out.print("\t");
            }
            System.out.println();
        }
    }
    public void create_minHashing()
    {
        int minhash_initial = Integer.MAX_VALUE; //assigning max value of integer instead of infinity
        minhash = new int[hashcount][docList.size()];
        for(int i=0;i<hashcount;i++)
        {
            for(int j=0;j<docList.size();j++)
            {
                minhash[i][j]=minhash_initial;
            }
        }
        for(int i=0;i<shingleList.size();i++)
        {
            for(int j=0;j<docList.size();j++)
            {
                if(shingleMatrix[i][j]==1)
                {
                    for(int k=0;k<hashcount;k++)
                    {
                        if(hashArray[i][k] < minhash[k][j])
                        {
                            minhash[k][j] = hashArray[i][k];
                        }
                    }
                }
            }
        }

        System.out.println("\n\n*********Minhash table***********");
        for(int i=0;i<docList.size();i++) 
	{
            System.out.print("Doc" + (i + 1) + "\t");
        }
        System.out.print("\n");
        for(int i=0;i<hashcount;i++)
	{
            for(int j=0;j<docList.size();j++)
	    {
                System.out.format("%4d",minhash[i][j]);
                System.out.print("\t");
            }
            System.out.println();
        }
    }
    public void choose_bands(double similarity)
    {
        possible_band = new ArrayList<>();
        possible_row= new ArrayList<>();
        probablities = new ArrayList<>();
        for(int i=0;i<hashcount;i++)
        {
            if(hashcount% (i+1) == 0)
            {
                possible_band.add(i+1);
            }
        }

        for(int i=0;i<possible_band.size();i++)
        {
            possible_row.add(hashcount/possible_band.get(i));
        }

        for(int i=0;i<possible_band.size();i++)
        {
            double t = 1 - Math.pow(1- Math.pow(similarity,possible_row.get(i)), possible_band.get(i)); //probability calculation
            probablities.add(t);
        }
        System.out.println("\n*********hyperparameters tuning********");
        System.out.println("Assumed Similarity threshold:" + similarity);
        System.out.println("Possible band and rows:");
        System.out.println("Band" + "\t" + "Rows" + "\t" + "Probability");
        for(int i=0;i<possible_band.size();i++)
        {
            System.out.println(possible_band.get(i) + "\t" + possible_row.get(i) + "\t" + probablities.get(i));
        }
        for(int i=0;i<possible_band.size();i++) //choosing the ideal row and band
        {
            if(similarity==0.8) 
	    {
                if (probablities.get(i) > 0.997 && probablities.get(i) < 1) {
                    ideal_item = i;
                    break;
                }
            }
            else
            {
                if (probablities.get(i) > 0.003) {
                    ideal_item = i;
                    break;
                }
            }
        }
        System.out.println("Assumed Similarity threshold:" + similarity);
        System.out.println("Ideal band and row that achieves atleast 0.997 probability: " +possible_band.get(ideal_item) + " , " + possible_row.get(ideal_item));
        System.out.println("Probability with the ideal band and row achieved is:" + probablities.get(ideal_item));
    }
    public void lsh()
    {
        int b, r;
        b=possible_band.get(ideal_item);
        r = possible_row.get(ideal_item);
        int br_mat[][] = new int[hashcount][docList.size()+2];
        for(int i=0;i<hashcount;i++) //band and row matrix
        {
            br_mat[i][0]= (i/r) + 1;
            br_mat[i][1] = (i%r) + 1;
        }
        for(int i=0;i<hashcount;i++)
        {
            for(int j=2;j<docList.size()+2;j++)
            {
                br_mat[i][j]=minhash[i][j-2];
            }
        }
        int a1=0,b1=0;
        int hash_bucket[][]= new int[hashcount][docList.size()+2];
        Random random= new Random(100);//random generation
        a1 = random.nextInt(shingleList.size());
        b1 = random.nextInt(shingleList.size());
        for(int i=0;i<hashcount;i++) 
	{
            for(int j=0;j<2;j++)
                hash_bucket[i][j] = br_mat[i][j];
        }
        for(int i=0;i<hashcount;i++) 
	{
            for(int j=2;j<docList.size()+2;j++)
             hash_bucket[i][j] = (a1 * br_mat[i][j] +b1) % prime;//hashing
        }
        int hash_band[][] = new int[b][docList.size()+1];
        for(int k=0;k<b;k++)
        {
            for(int j=0;j<docList.size();j++)
            {
                int sum=0;
                for(int i=0;i<r;i++)
                {
                    sum += hash_bucket[(k*r)+i][j+2];
                }
                hash_band[k][j+1]=sum;
            }
        }
	//finding candidate pairs
        for(int i=0;i<b;i++)
            hash_band[i][0]=i+1;
        ArrayList<HashMap<Integer,HashSet<Integer>>> similar_hash_arrays = new ArrayList<>();
        for(int i=0;i<b;i++) 
	{
            similar_hash_arrays.add(new HashMap<>());
            for (int j = 1; j < docList.size()+1; j++) 
	    {
                   if(similar_hash_arrays.get(i).containsKey(hash_band[i][j]))
		   {
                       similar_hash_arrays.get(i).get(hash_band[i][j]).add(j);
                   }
                   else
                   {
                       HashSet<Integer> similar_hashset = new HashSet<>();
                       similar_hashset.add(j);
                       similar_hash_arrays.get(i).put(hash_band[i][j],similar_hashset);
                   }
            }
        }
        HashSet<HashSet<Integer>> c_pairs= new HashSet<>();
        //choosing unique candidate pairs
	for(int i=0;i<similar_hash_arrays.size();i++)
        {
            Collection<HashSet<Integer>> list = similar_hash_arrays.get(i).values();
            for(HashSet<Integer> set : list)
            {
                if(set.size()==2) 
		{
                    c_pairs.add(set);
                }
                else if(set.size()>2)
                {
                    for(Integer item: set)
                    {
                        for(Integer item2:set)
                        {
                            if(item!=item2)
                            {
                                HashSet<Integer> new_pairs = new HashSet<>();
                                new_pairs.add(item);
                                new_pairs.add(item2);
                                c_pairs.add(new_pairs);
                            }
                        }
                    }
                }
            }

        }
        System.out.println("\n*******All plagiarism pairs ***********");
        if(c_pairs.size()<1)
	{
		System.out.println("No plagiarsim pairs exist");
	}
	Iterator<HashSet<Integer>> val = c_pairs.iterator();
        while(val.hasNext()) 
	{
            Iterator<Integer> items_iter = val.next().iterator();
            int first = items_iter.next();
            int second = items_iter.next();
            int intersection =0;
            for(int i=0;i<hashcount;i++)
            {
                if(minhash[i][first-1] == minhash[i][second-1])//jacard similarity
                {
                    intersection += 1;
                }
            }
            double sim = (double)intersection/(double)hashcount;
            System.out.println("Document pair: " + first + "," + second + " with signature similarity:" + sim);
        }
    }
}



