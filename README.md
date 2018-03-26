
# PlayingWithGradleDependencyVersions  
I wrote these projects to play around with declaring Gradle dependency versions in an external properties file.  
  
## using-basic-ext  
A really simple project which does nothing more than load the properties file into an `ExtraPropertiesExtension`
property and then uses them in the `dependencies` block.  

## using-resolution-strategy  
This project plays around with the `ResolutionStrategy` to resolve dependency versions automatically based on the
properties file.

## using-custom-plugin  
Functionally equivalent to *using-resolution-strategy*, except that all of the common logic is defined in the
*dependency-management-plugin* project.

## using-spring-plugin  
This project uses the fantastic
[io.spring.dependency-management](https://github.com/spring-gradle-plugins/dependency-management-plugin) plugin under
the hood, with some additional logic to dynamically configure the `dependencyManagement` block based on the properties
file.
