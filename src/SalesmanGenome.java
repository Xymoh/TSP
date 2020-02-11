import java.util.*;

public class SalesmanGenome implements Comparable {

    List<Integer> genome;

    public int startingCity;
    public int numberOfCities;
    public int distance;
    public int fitness;

    // Generates a random salesman
    public SalesmanGenome(int numberOfCities) {
        Integer[] t = new Integer[numberOfCities];
        this.numberOfCities = numberOfCities;
        this.genome = new ArrayList<Integer>(Arrays.asList(t));
    }

    public SalesmanGenome(Data data) {
        Random r = new Random();
        this.startingCity = r.nextInt(data.numberOfCities);
    	this.numberOfCities = data.numberOfCities;
    	this.genome = this.randomSalesman();
        this.distance = this.calculateDistance(data);
        this.fitness = 0;
    }

    public SalesmanGenome(int[] genome, Data data) {
        this.startingCity = genome[0];
    	this.numberOfCities = data.numberOfCities;
    	// rewrite the genome
    	this.genome = new ArrayList<Integer>();
    	for(int g : genome) {
    		this.genome.add(g);
    	}
    	this.distance = this.calculateDistance(data);
    	this.fitness = 0;
    }

    public SalesmanGenome(int[] genome) {
        this.startingCity = genome[0];
        this.numberOfCities = genome.length;
        // rewrite the genome
        this.genome = new ArrayList<Integer>();
        for(int g : genome) {
            this.genome.add(g);
        }
        this.fitness = 0;
    }

    public SalesmanGenome(int[] genome, int distance, int fitness) {
        this.startingCity = genome[0];
        // rewrite the genome
        this.genome = new ArrayList<>();
        this.distance = distance;
        this.fitness = fitness;
        for(int g : genome) {
            this.genome.add(g);
        }
        this.fitness = 0;
    }


    
    public SalesmanGenome(List<Integer> genome, Data data) {
        this.startingCity = genome.get(0);
    	this.numberOfCities = data.numberOfCities;
    	this.genome = genome;
    	this.distance = this.calculateDistance(data);
    	this.fitness = 0;
    }

    public int calculateDistance(Data data) {
    	int dist = 0;
    	int startingCity = this.genome.get(0);
    	int currentCity = this.genome.get(0);
    	for (int gene : this.genome) {
    		dist += data.distances[currentCity][gene];
    		currentCity = gene;
    	}
    	// return
    	dist += data.distances[currentCity][startingCity];
    	return dist;
    }

    // Generates a random genome
    private List<Integer> randomSalesman(){
        List<Integer> result = new ArrayList<Integer>();
        for (int i=0; i<numberOfCities; i++) {
            result.add(i);
        }
        Collections.shuffle(result);
        return result;
    }

    public List<Integer> getGenome() {
        return genome;
    }

    public int getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        for ( int gene: genome ) {
            sb.append(gene);
            sb.append("-");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
        SalesmanGenome genome = (SalesmanGenome) o;
        if(this.fitness > genome.getFitness())
            return 1;
        else if(this.fitness < genome.getFitness())
            return -1;
        else
            return 0;
    }
}