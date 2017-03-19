import java.util.*;

public class RandomJava {
  Random r;
  RandomJava() { r = new Random(); }
  public double getRandomValue() { return r.nextDouble(); }

  public static void main(String[] args) {
    RandomJava rj = new RandomJava();
    for (int i = 0; i < 10; i++) {
      System.out.println("random: " + rj.getRandomValue());
    }
  }
}
