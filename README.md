# RKot - Use R in Kotlin using Renjin!
RKotlin is a set of Kotlin wrappers for the Renjin JVM to R Scripting Engine

Installation
======

### Gradle Dependency 

You must add the gradle dependency to your _build.gradle_ file

```    
dependencies {
    compile "org.renjin:renjin-script-engine:0.8.2413"
} 
```

Sample 
-----

This sample is used in the Renjin JVM docs but works in the same way for Kotlin. 


```
fun main(args: Array<String>) {

    R("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))")    // creates a data frame with an x and y.
   
    R.print("df")   

}

```

calling R.print() will give you an output similar to the following:

```

 x      y
 1  1     -0.188
 2  2      3.144
 3  3      1.625
 4  4      3.426
 5  5       6.45
 6  6       5.85
 7  7      7.774
 8  8      8.495
 9  9      9.276
10 10     10.603

Call:
lm(formula = y ~ x, data = df)

Coefficients:
(Intercept) x
-0.582       1.132

```

_*Similar to the JVM version of this module, the script engine will not print everything to std.out like it does in R. So you'll have to use R.print() explicitly_

#### Types in RKot

To specify a particular R type in RKot you can use one of the helper functions included. These are lambda extensions on the SEXP class that you find in Renjin.

```

val r = R("a <- 2; b <- 3; a*b").double()  // the java type on this class will be DoubleArrayVector
val x = R("a <- 2; b <- 3; a*b").integer() 

```

*TODO: Currently, the R.startEngine() method does not accept the needed defaultFileSystemManager, PackageLoader, and ClassLoader packages that enable one to start new sessions using Renjin. 





