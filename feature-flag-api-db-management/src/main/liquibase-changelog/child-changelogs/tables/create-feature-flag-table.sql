CREATE TABLE FeatureFlagNode
(
    id   INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dependentOnFeatures NVARCHAR(MAX) NOT NULL
);