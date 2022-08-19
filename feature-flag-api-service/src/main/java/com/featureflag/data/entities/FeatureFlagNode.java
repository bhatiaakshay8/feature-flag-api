package com.featureflag.data.entities;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class FeatureFlagNode {
    private Long id;
    private String name;
    private List<String> dependentOnFeatures;
}
