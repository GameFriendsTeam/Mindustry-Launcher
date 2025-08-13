package ru.gmp.ml;

import java.util.*;
import java.util.stream.Collectors;

public class VersionSorter {
    public static List<String> sort(Set<String> versions) {
        return versions.stream().sorted((s1, s2) -> {
                    String[] parts1 = s1.substring(1).split("\\.");
                    String[] parts2 = s2.substring(1).split("\\.");
                    int mainCompare = Integer.compare(Integer.parseInt(parts2[0]), Integer.parseInt(parts1[0]));
                    if (mainCompare != 0) { return mainCompare; }
                    double sub1 = parts1.length > 1 ? Double.parseDouble(parts1[1]) : 0;
                    double sub2 = parts2.length > 1 ? Double.parseDouble(parts2[1]) : 0;
                    return Double.compare(sub2, sub1);
                }).collect(Collectors.toList());
    }
}