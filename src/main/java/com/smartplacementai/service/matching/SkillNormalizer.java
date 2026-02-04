package com.smartplacementai.service.matching;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class SkillNormalizer {

    public static Set<String> normalize(Set<String> skills) {
         return skills.stream()
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .map(skill -> skill.replaceAll("[^a-z0-9+.# ]", " "))
            .map(skill -> skill.replaceAll("\\s+", " "))
            .map(String::trim)
            .collect(Collectors.toSet());
}
}
