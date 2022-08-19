package com.featureflag.data;

import com.featureflag.data.entities.FeatureFlagNode;
import com.featureflag.data.typehandler.StringArrayListTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Mapper
public interface FeatureFlagMapper {
    @Results({
            @Result(column = "dependentOnFeatures", property = "dependentOnFeatures", typeHandler = StringArrayListTypeHandler.class)
    })
    @Select("SELECT FeatureFlagNode.id, " +
            "FeatureFlagNode.name, " +
            "FeatureFlagNode.dependentOnFeatures " +
            "FROM FeatureFlagNode " +
            "ORDER BY FeatureFlagNode.id")
    List<FeatureFlagNode> getAllFeatureFlagNodes();

    @Insert("INSERT into FeatureFlagNode (name, dependentOnFeatures)" +
            "Values(#{ff.name},#{ff.dependentOnFeatures,typeHandler=com.featureflag.data.typehandler.StringArrayListTypeHandler})")
    void insertFeatureFlag(@Param("ff")FeatureFlagNode node);

    @Delete("Delete from FeatureFlagNode")
    void deleteFeatureFlags();


}

