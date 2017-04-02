import java.util.stream.*;

public class Somme {
    public static void main(String args[]) {
        // System.out.println(IntStream.range(1,10).sum());
        // System.out.println(IntStream.range(1,10).map(i -> i * 2).sum());
        // System.out.println(IntStream.range(1,10).map(i -> i * i).sum());

        // Comparer les temps d’exécution entre les deux lignes
        // suivantes :
        // System.out.println(IntStream.range(1,2000000000).map(i -> i * i).sum());
        System.out.println(IntStream.range(1,2000000000).parallel().map(i -> i * i).sum());
    }
}
