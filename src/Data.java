import java.io.File;
import java.util.Scanner;

public class Data {
    public int[][] distances;
    public int numberOfCities;

    public Data(String fn) throws Exception {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(fn));
            String line;
            String[] temp;
            int i, j;
            int curr;

            numberOfCities = Integer.parseInt(reader.nextLine());
            distances = new int[numberOfCities][numberOfCities];

            i = 0;
            while (reader.hasNextLine()) {
                j = 0;

                line = reader.nextLine();
                temp = line.trim().split("\\s+");

                while (j < temp.length){
                    curr = Integer.parseInt(temp[j].replaceAll("\\s+", ""));
                    distances[i][j] = curr;
                    distances[j][i] = curr;
                    j++;
                }
                i++;
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
