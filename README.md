predict4java
============

mavenized version of http://code.google.com/p/predict4java/

Quoting from above site:

*This is a Java port of the core elements of the Open Source (GPL v2) Predict program, Copyright John A. Magliacane, KD2BD 1991-2003:*

http://www.qsl.net/kd2bd/predict.html

*Dr. T.S. Kelso is the author of the SGP4/SDP4 orbital models, originally written in Fortran and Pascal, and released into the public domain through his website:*

http://www.celestrak.com/

*Neoklis Kyriazis, 5B4AZ, later re-wrote Dr. Kelso's code in C, and released it under the GNU GPL in 2002. PREDICT's core is based on 5B4AZ's code translation efforts.*

*The Author of the pre-mavenized version is: David A. B. Johnson, G4DPZ*

Notes
----------

During the mavenizing process the following significant changes to the original project were made:
* TestUtil dependency is not available in a maven repository so commented out the calls to that library in SatPosTest and SatPassTimeTest.
* checkstyle configuration is included in the source but is not referenced yet by the maven checkstyle plugin

Build instructions
-------------------

    cd <YOUR_WORKSPACE>
    git clone http://github.com/davidmoten/predict4java.git
    cd predict4java
    mvn clean install

View site reports including code coverage, javadocs
----------------------------------------------------
    mvn clean site

Then open *target/site/index.html* in a browser

   
