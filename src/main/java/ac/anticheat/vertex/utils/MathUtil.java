package ac.anticheat.vertex.utils;

import java.util.Collection;
import java.util.List;

public class MathUtil {
    public static double correlation(List<Double> actual, List<Double> predicted) {
        int n = actual.size();
        if (n == 0 || predicted.size() != n) return 0.0;

        double meanActual = actual.stream().mapToDouble(d -> d).average().orElse(0.0);
        double meanPredicted = predicted.stream().mapToDouble(d -> d).average().orElse(0.0);

        double numerator = 0.0;
        double denominatorActual = 0.0;
        double denominatorPredicted = 0.0;

        for (int i = 0; i < n; i++) {
            double aDiff = actual.get(i) - meanActual;
            double pDiff = predicted.get(i) - meanPredicted;
            numerator += aDiff * pDiff;
            denominatorActual += aDiff * aDiff;
            denominatorPredicted += pDiff * pDiff;
        }

        double denominator = java.lang.Math.sqrt(denominatorActual * denominatorPredicted);
        if (denominator == 0) return 0.0;

        return numerator / denominator;
    }

    public static double jerk(List<Double> values) {
        if (values.size() < 4) return 0.0;

        double jerk = 0.0;
        for (int i = 3; i < values.size(); i++) {
            double j = values.get(i) - 3*values.get(i - 1) + 3*values.get(i - 2) - values.get(i - 3);
            jerk += Math.abs(j);
        }

        return jerk;
    }

    public static double getMean(final Collection<? extends Number> data) {
        if (data == null || data.isEmpty()) return 0.0;

        double sum = 0.0;
        int count = 0;

        for (Number number : data) {
            sum += number.doubleValue();
            count++;
        }

        return count == 0 ? 0.0 : sum / count;
    }

    public static double autoCorrelation(List<Double> data, int lag) {
        int n = data.size();
        if (n <= lag) return 0;

        double mean = data.stream().mapToDouble(d -> d).average().orElse(0.0);

        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < n - lag; i++) {
            numerator += (data.get(i) - mean) * (data.get(i + lag) - mean);
        }
        for (int i = 0; i < n; i++) {
            denominator += Math.pow(data.get(i) - mean, 2);
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }

    public static double getKurtosis(List<Double> values) {
        int n = values.size();

        double mean = 0;
        for (double v : values) {
            mean += v;
        }
        mean /= n;

        double sum2 = 0;
        for (double v : values) {
            sum2 += Math.pow(v - mean, 2);
        }
        double variance = sum2 / (n - 1);
        double stdDev = Math.sqrt(variance);

        if (stdDev == 0) {
            return 0;
        }

        double sum4 = 0;
        for (double v : values) {
            sum4 += Math.pow(v - mean, 4);
        }
        double m4 = sum4 / n;

        double kurtosis = m4 / Math.pow(stdDev, 4) - 3;

        return kurtosis;
    }
}
