import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws NumberFormatException {

    	Salesman salesman = null;
        try {
        	/*
        	 * berlin52: 65000, 32, 0.985, 0.0165
        	 * pr1002: 5000, 32, 0.985, 0.0165
             */

            java.io.BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Creating the salesman...");
            System.out.println("Insert the txt file name for traveling problem");
            String travellingFile = reader.readLine();
            System.out.println("Insert the generation number");
            int generations = Integer.parseInt(reader.readLine());
            System.out.println("Insert the population size");
            int populationSize = Integer.parseInt(reader.readLine());
            System.out.println("Put the breeding chance");
            float breedingChance = Float.parseFloat(reader.readLine());
            System.out.println("Put the mutation chance");
            float mutationChance = Float.parseFloat(reader.readLine());

            long startTime = System.nanoTime();

            salesman = new Salesman(travellingFile, generations, populationSize, breedingChance, mutationChance);
     //     salesman = new Salesman("berlin52.txt", 65000, 32, 0.985f, 0.0165f);
            System.out.println(String.format("Best member distance : %s", salesman.bestOverallMember.distance));
            System.out.println(salesman.bestOverallMember.toString());

            long endTime = System.nanoTime();

            long timeElapsed = endTime - startTime;

            System.out.println("Execution time in seconds: " + timeElapsed / 1000000000);
            System.out.println("Execution time in miliseconds: " + timeElapsed / 1000000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}