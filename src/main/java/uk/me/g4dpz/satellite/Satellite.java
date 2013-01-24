/**
    predict4java: An SDP4 / SGP4 library for satellite orbit predictions

    Copyright (C)  2004-2010  David A. B. Johnson, G4DPZ.

    This class is a Java port of one of the core elements of
    the Predict program, Copyright John A. Magliacane,
    KD2BD 1991-2003: http://www.qsl.net/kd2bd/predict.html

    Dr. T.S. Kelso is the author of the SGP4/SDP4 orbital models,
    originally written in Fortran and Pascal, and released into the
    public domain through his website (http://www.celestrak.com/). 
    Neoklis Kyriazis, 5B4AZ, later re-wrote Dr. Kelso's code in C,
    and released it under the GNU GPL in 2002.
    PREDICT's core is based on 5B4AZ's code translation efforts.

    Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>

    Comments, questions and bugreports should be submitted via
    http://sourceforge.net/projects/websat/
    More details can be found at the project home page:

    http://websat.sourceforge.net

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, visit http://www.fsf.org/
 */
package uk.me.g4dpz.satellite;

import java.util.Date;

public interface Satellite {

    double DEG2RAD = 1.745329251994330E-2;
    double TWO_PI = Math.PI * 2.0;
    double EPSILON = 1.0E-12;
    double TWO_THIRDS = 2.0 / 3.0;
    double EARTH_RADIUS_KM = 6.378137E3;
    double XKE = 7.43669161E-2;
    double CK2 = 5.413079E-4;
    /** J2 Harmonic (WGS '72). */
    double J2_HARMONIC = 1.0826158E-3;
    /** J3 Harmonic (WGS '72). */
    double J3_HARMONIC = -2.53881E-6;
    /** J4 Harmonic (WGS '72). */
    double J4_HARMONIC = -1.65597E-6;

    @Deprecated
    void getPosition(GroundStationPosition qth, SatPos satPos, Date time);

    boolean willBeSeen(GroundStationPosition qth);

    void calculateSatelliteVectors(Date time);

    SatPos calculateSatelliteGroundTrack();

    SatPos calculateSatPosForGroundStation(GroundStationPosition gsPos);

    TLE getTLE();

    SatPos getPosition(GroundStationPosition qth, Date time);
}
