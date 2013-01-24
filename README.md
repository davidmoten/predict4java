predict4java
============

mavenized version of http://code.google.com/p/predict4java/

Quoting from above site:

<pre>
This is a Java port of the core elements of the Open Source (GPL v2) Predict program, Copyright John A. Magliacane, KD2BD 1991-2003:

http://www.qsl.net/kd2bd/predict.html

Dr. T.S. Kelso is the author of the SGP4/SDP4 orbital models, originally written in Fortran and Pascal, and released into the public domain through his website:

http://www.celestrak.com/

Neoklis Kyriazis, 5B4AZ, later re-wrote Dr. Kelso's code in C, and released it under the GNU GPL in 2002. PREDICT's core is based on 5B4AZ's code translation efforts.

The Author of this version is: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>
</pre>

Build instructions
-------------------

    cd <YOUR_WORKSPACE>
    git clone http://github.com/davidmoten/predict4java.git
    cd predict4java
    mvn clean install

View site reports including code coverage, javadocs
----------------------------------------------------
    mvn clean site

Then open in a browser

   predict4java/target/site/index.html
