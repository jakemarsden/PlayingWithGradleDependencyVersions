
# PlayingWithGradleDependencyVersions  
I wrote these projects to play around with declaring Gradle dependency versions in an external properties file.  
  
## MyDumbProject  
A really simple project which does nothing more than load the properties file into an `ExtraPropertiesExtension`
property and then uses them in the `dependencies` block.  

## MyProjectUsingCustomResolutionStrategy  
This project plays around with the `ResolutionStrategy` to resolve dependency versions automatically based on the
properties file.

## MyProjectUsingCustomPlugin  
Functionally equivalent to *MyProjectUsingCustomResolutionStrategy*, except that all of the common logic is defined in
the *dependency-management-plugin* project.

## MyProjectUsingSpringPlugin  
This project uses the fantastic
[io.spring.dependency-management](https://github.com/spring-gradle-plugins/dependency-management-plugin) plugin under
the hood, with some additional logic to dynamically configure the `dependencyManagement` block based on the properties
file.
