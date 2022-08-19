package com.featureflag.services;

import com.featureflag.data.FeatureFlagMapper;
import com.featureflag.data.entities.FeatureFlagNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
public class FeatureFlagService {

    private final FeatureFlagMapper featureFlagMapper;

    public List<FeatureFlagNode> getAllFeatureFlagNodes() {
        return this.featureFlagMapper.getAllFeatureFlagNodes();
    }

    public void insertFeatureFlag(FeatureFlagNode node) {
        this.featureFlagMapper.insertFeatureFlag(node);
    }

    public void storeFeatureFlags(FeatureFlagNode node, Map<String,FeatureFlagNode> featureNameMap) {
        clearPreviousFeatureFlags();
        sortAndStore(node,featureNameMap);
    }
    void sortAndStore(FeatureFlagNode node, Map<String,FeatureFlagNode> featureNameMap) {
        if(node.getDependentOnFeatures() == null || node.getDependentOnFeatures().isEmpty()){
            insertFeatureFlag(node);
            return;
        }

        for(String child : node.getDependentOnFeatures()){
            sortAndStore(featureNameMap.get(child),featureNameMap);
        }

        insertFeatureFlag(node);
    }

    public void clearPreviousFeatureFlags() {
        this.featureFlagMapper.deleteFeatureFlags();
    }

}
