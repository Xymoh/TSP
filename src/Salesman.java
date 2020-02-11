import com.google.common.primitives.Ints;

import java.util.*;

public class Salesman {

    public Data data;
    private Random r;

    private int iterations;

    private int populationSize; // Generation size is the number of genomes/individuals in each generation/population
    private int numberOfCities;

    private List<SalesmanGenome> population;

    private int totalFitness;

    private SalesmanGenome bestLocalMember = null;
    public SalesmanGenome bestOverallMember = null;

    private float reproductionRate;
    private float mutationRate; // Mutation rate refers to the frequency of mutations when creating a new generation.
    private int tournamentSize; // Tournament size is the size of the tournament for tournament selection.

    public Salesman(String fn, int generations, int populationSize, float breedingChance, float mutationChance) throws Exception {
        data = new Data(fn);
        this.r = new Random();
        // copy parameters to the fields
        List<SalesmanGenome> temp;
        //
        this.numberOfCities = data.numberOfCities;
        this.iterations = generations;
        this.tournamentSize = 2;
        this.populationSize = populationSize;
        this.reproductionRate = breedingChance;
        this.mutationRate = mutationChance;
        // creating init population
        Boolean roulette = true;
        this.createPopulation();
        for (int g = 0; g < this.iterations; g++) {
            // selection, crossover and mutation
            temp = roulette ? this.rouletteSelection() : this.tournamentSelection();
            this.population = createGeneration(temp);
            // calculate the parameters
            for (SalesmanGenome m : this.population) {
            	m.calculateDistance(data);
            }
            this.calculateFitness();
        }
    }

    public void createPopulation(){
        this.population = new ArrayList<>();
        for (int i=0; i<this.populationSize; i++){
            this.population.add(new SalesmanGenome(this.data));
        }
        this.calculateFitness();	
    }

    public void calculateFitness() {
        this.totalFitness = 0;
        int fitness, mFitness;
        int bMemberId = 0;
        // find the worst distance for fitness calculation
        int worst = this.population.get(0).distance;
        for (SalesmanGenome g : this.population) {
            // find the worst distance from population
            if (g.distance > worst) {
                worst = g.distance;
            }
        }
        // calculate the fitness
        mFitness = fitness = worst - this.population.get(0).distance + 1;
        this.population.get(0).fitness = fitness;
        this.totalFitness += fitness;
        for (int i = 1; i < this.population.size(); i++) {
            fitness = worst - this.population.get(i).distance + 1;
            this.population.get(i).fitness = fitness;
            // find maxFitness from the population
            if (fitness > mFitness) {
                mFitness = fitness;
                bMemberId = i;
            }
            this.totalFitness += fitness;
        }
        // set best member
        this.bestLocalMember = this.population.get(bMemberId);
        if (this.bestOverallMember == null) {
            this.bestOverallMember = this.bestLocalMember;
        }
        // check if the best local member is not better than the best overall
        if (this.bestLocalMember.distance < this.bestOverallMember.distance) {
            this.bestOverallMember = this.bestLocalMember;
        }
    }

    public List<SalesmanGenome> tournamentSelection() {
        SalesmanGenome temp;
    	List<SalesmanGenome> sMembers = new ArrayList<>();
    	List<Integer> sMembersIds = new ArrayList<>();
    	List<int[]> groups = new ArrayList<>();
    	int bMemberId, bMemberFitness;
    	int cGroup = -1;

    	// divide
    	for (int i = 0; i < this.populationSize; i++) {
    		if (i % this.tournamentSize == 0) {
    			// create a new group
    			cGroup++;
    			groups.add(new int[this.tournamentSize]);
    		}
            groups.get(cGroup)[i % this.tournamentSize] = this.population.get(i).fitness;
    	}


    	// pick the best
        cGroup = 0;
        for (int[] g : groups) {
            bMemberId = 0;
            bMemberFitness = g[0];
            for (int i = 0; i < this.tournamentSize; i++) {
                if (bMemberFitness < g[i]) {
                    bMemberId = i * cGroup;
                    bMemberFitness = g[i];
                }
            }
            // add
            for (int i = 0; i < this.tournamentSize; i++) {
                sMembersIds.add(bMemberId);
            }
            cGroup++;
        }

    	for (int i = 0; i < this.populationSize; i++) {
            temp = this.population.get(i);
            sMembers.add(new SalesmanGenome(Ints.toArray(temp.genome), temp.distance, temp.fitness));
    	}
    	return sMembers;
    }
    
    public List<SalesmanGenome> rouletteSelection() {
        List<SalesmanGenome> sMembers = new ArrayList<>();
        int[] sMembersIds = new int[this.populationSize];
        int rShoot;
        // creating the ranges
        int[] rShootRanges = new int[this.populationSize + 1];
        rShootRanges[0] = 0;
        rShootRanges[1] = this.population.get(0).fitness;
        for (int i = 2; i <= this.populationSize; i++) {
        	rShootRanges[i] = rShootRanges[i - 1] + this.population.get(i - 1).fitness;
        }
        for (int i = 0; i < this.populationSize; i++) {
        	rShoot = this.r.nextInt(this.totalFitness);
            // find the valid range
            for (int j = 0; j < this.populationSize; j++) {
                if (j == 0) {
                    if (rShoot >= rShootRanges[0] && rShoot < rShootRanges[1]) {
                    	sMembersIds[i] = j;
                        break;
                    }
                } else {
                    if (rShoot >= rShootRanges[j] && rShoot < rShootRanges[j + 1]) {
                    	sMembersIds[i] = j;
                        break;
                    }
                }
            }
        }
        // rewrite the members
        for (int i = 0; i < this.populationSize; i++) {
        	sMembers.add(this.population.get(sMembersIds[i]));
        }
        return sMembers;
    }

    public List<SalesmanGenome> createGeneration(List<SalesmanGenome> population) {
        List<SalesmanGenome> generation = new ArrayList<>();
        List<SalesmanGenome> parents = new ArrayList<>();
        List<SalesmanGenome> children;
        int cPair = 0;
        while(cPair < this.populationSize) {
        	// get parents
        	parents.add(population.get(cPair));
        	parents.add(population.get(cPair + 1));
        	// crossover
            children = crossover(parents);
            // mutate
            children.set(0, mutate(children.get(0)));
            children.set(1, mutate(children.get(1)));
            generation.addAll(children);
            cPair += 2;
        }
        return generation;
    }

    public SalesmanGenome mutate(SalesmanGenome genome){
        float mutate = this.r.nextFloat();
        if (mutate < mutationRate) {
            List<Integer> genomeSeq = genome.getGenome();
            Collections.swap(genomeSeq, r.nextInt(data.numberOfCities - 1), this.r.nextInt(data.numberOfCities - 1));
            return new SalesmanGenome(genomeSeq, data);
        }
        return genome;
    }

    public List<SalesmanGenome> crossover(List<SalesmanGenome> parents) {
        int[][] childrenSeq = new int[2][this.numberOfCities];
        int[][] breakFragmentsSeq;
        int bp1, bp2, bLength, bOffset;
        int tGene, tPos, t;
        int tFirst, tSecond;
        Boolean foundPlaceForTheCity = false;
        float reproduce = this.r.nextFloat();
        if (reproduce < reproductionRate) {
            // find the breakpoints
            do {
                bp1 = this.r.nextInt(this.numberOfCities);
                bp2 = this.r.nextInt(this.numberOfCities);
            } while (bp1 == bp2 || bp1 >= bp2 || bp1 == 0 || bp2 == this.numberOfCities - 1 || bp2 == this.numberOfCities - 2);
            bLength = bp2 - bp1 + 1;
            bOffset = bp1;
            breakFragmentsSeq = new int[2][bLength];
            // add genes
            for (int i = 0; i < bLength; i++) {
                // break
                t = parents.get(1).genome.get(bOffset);
                childrenSeq[0][bOffset] = t;
                breakFragmentsSeq[0][i] = t;
                t = parents.get(0).genome.get(bOffset);
                childrenSeq[1][bOffset] = t;
                breakFragmentsSeq[1][i] = t;
                bOffset++;
            }

            // fill the children with genes
            for (int child = 0; child < 2; child++) {
                tFirst = child;
                tSecond = child == 0 ? 1 : 0;
                // left
                for (int i = 0; i < bp1; i++) {
                    tGene = parents.get(tFirst).genome.get(i);
                    tPos = Ints.indexOf(childrenSeq[tFirst], tGene);
                    if (tPos == -1) {
                        // city not found, add it
                    	childrenSeq[tFirst][i] = tGene;
                    } else {
                        // city was already added, find another one
                        while (!foundPlaceForTheCity) {
                            // find the gene in the break
                            tPos = Ints.indexOf(breakFragmentsSeq[tFirst], tGene);
                            if (tPos == -1) {
                            	childrenSeq[tFirst][i] = tGene;
                                foundPlaceForTheCity = true;
                            } else {
                                tGene = breakFragmentsSeq[tSecond][tPos];
                                tPos = Ints.indexOf(breakFragmentsSeq[tFirst], tGene);
                                if (tPos == -1) {
                                	childrenSeq[tFirst][i] = tGene;
                                    foundPlaceForTheCity = true;
                                } else {
                                    tGene = breakFragmentsSeq[tSecond][tPos];
                                }
                            }
                        }
                        foundPlaceForTheCity = false;
                    }
                }
                // right
                for (int i = bp2 + 1; i < this.numberOfCities; i++) {
                    tGene = parents.get(tFirst).genome.get(i);
                    tPos = Ints.indexOf(childrenSeq[tFirst], tGene);
                    if (tPos == -1) {
                        // city not found, add it
                    	childrenSeq[tFirst][i] = tGene;
                    } else {
                        // city was already added, find another one
                        while (!foundPlaceForTheCity) {
                            // find the gene in the break
                            tPos = Ints.indexOf(breakFragmentsSeq[tFirst], tGene);
                            if (tPos == -1) {
                            	childrenSeq[tFirst][i] = tGene;
                                foundPlaceForTheCity = true;
                            } else {
                                tGene = breakFragmentsSeq[tSecond][tPos];
                                tPos = Ints.indexOf(breakFragmentsSeq[tFirst], tGene);
                                if (tPos == -1) {
                                    childrenSeq[tFirst][i] = tGene;
                                    foundPlaceForTheCity = true;
                                } else {
                                    tGene = breakFragmentsSeq[tSecond][tPos];
                                }
                            }
                        }
                        foundPlaceForTheCity = false;
                    }
                }
            }
            // return the children
            return new ArrayList<>(
                    Arrays.asList(
                            new SalesmanGenome(childrenSeq[0], data),
                            new SalesmanGenome(childrenSeq[1], data)));
        }
        return parents;
    }
}
