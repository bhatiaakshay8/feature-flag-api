package com.featureflag.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.data.entities.FeatureFlagNode;
import com.featureflag.services.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feature-flags")
@SecurityRequirement(name = "Bearer Authentication")
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @PutMapping("/sort")
    @Operation(description = "Used to Read feature flags from json, sort and persist in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sorted and stored",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint"))
    })
    public ResponseEntity<Void> sortFeatureFlags() throws IOException {
        System.out.println("sorting and storing FeatureFlags");
        /*TODO: Assumptions if changed:
            1. Each feature can have more than 2 dependency.
            Since it is not written I am considering dependency as array list
            instead of 2 separate variables which I would have taken in case of a classic Tree (with 2 branches at each point)
            2. Clearing previous Feature Flags before inserting new (that is why this is a PUT request)
        */
        ObjectMapper mapper = new ObjectMapper();
        List<FeatureFlagNode> features = mapper.readValue(Paths.get("./BOOT-INF/classes/feature_flags.json").toFile(), new TypeReference<>() {});
        Map<String,FeatureFlagNode> parentMap = new HashMap<>();
        Map<String,FeatureFlagNode> featureNameMap = new HashMap<>();

        FeatureFlagNode rootNode = null;

        for(FeatureFlagNode feature: features){
            List<String> featureDependency = feature.getDependentOnFeatures();
            String currentFeatureName = feature.getName();
            if(featureDependency!= null && !featureDependency.isEmpty()){
                for(String dependent: featureDependency){
                    parentMap.put(dependent,feature);
                }
            }
            featureNameMap.put(currentFeatureName,feature);
        }

        for(Map.Entry<String,FeatureFlagNode> entry: featureNameMap.entrySet()){
            if(!parentMap.containsKey(entry.getKey())){
                rootNode = entry.getValue();
                break;
            }
        }

        if(rootNode == null) {
            System.out.println("Problem Occurred parsing JSON/TREE");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println("Root Node feature: " + rootNode.getName() + " with " + rootNode.getDependentOnFeatures().size() + " dependencies");

        /*TODO: Assumptions if changed
           3. Sort and Store both done as part of one.
           Not using additional DS to store sorted order. But if needed we can split the steps and create a list
         */
        featureFlagService.storeFeatureFlags(rootNode,featureNameMap);

        return new ResponseEntity<>(HttpStatus.OK);
    }




    @GetMapping("/getFeatureFlags")
    @Operation(description = "Used for getting current state of feature flags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint"))
    })
    public ResponseEntity<List<FeatureFlagNode>> getFeatureFlags() {
        System.out.println("getting FeatureFlags");
        List<FeatureFlagNode> nodes = featureFlagService.getAllFeatureFlagNodes();
        System.out.println("Found : " + ( nodes != null ? nodes.size() : 0));
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }

}
