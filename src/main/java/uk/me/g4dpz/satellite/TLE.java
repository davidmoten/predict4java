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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * TLE representation to aid SGP4 calculations.
 */
public class TLE implements Serializable {

    private static final long serialVersionUID = 716922882884628016L;

    private static final int THREELINES = 3;
    private static final double DEG2RAD = 1.745329251994330E-2;
    private static final double TWO_PI = Math.PI * 2.0;
    private static final double MINS_PERDAY = 1.44E3;
    private static final double XKE = 7.43669161E-2;
    private static final double TWO_THIRDS = 2.0 / 3.0;
    private static final double CK2 = 5.413079E-4;

    private int catnum;
    private String name;
    private int setnum;
    private int year;
    private double refepoch;
    private double incl;
    private double raan;
    private double eccn;
    private double argper;
    private double meanan;
    private double meanmo;
    private double drag;
    private double nddot6;
    private double bstar;
    private int orbitnum;
    private double epoch;
    private double xndt2o;
    private double xincl;
    private double xnodeo;
    private double eo;
    private double omegao;
    private double xmo;
    private double xno;
    private boolean deepspace;
    private java.util.Date createddate;

    // Constructors

    /**
     * Copy constructor.
     * 
     * @param tle
     */
    public TLE(final TLE tle) {
        this.catnum = tle.catnum;
        this.name = tle.name;
        this.setnum = tle.setnum;
        this.year = tle.year;
        this.refepoch = tle.refepoch;
        this.incl = tle.incl;
        this.raan = tle.raan;
        this.eccn = tle.eccn;
        this.argper = tle.argper;
        this.meanan = tle.meanan;
        this.meanmo = tle.meanmo;
        this.drag = tle.drag;
        this.nddot6 = tle.nddot6;
        this.bstar = tle.bstar;
        this.orbitnum = tle.orbitnum;
        this.epoch = tle.epoch;
        this.xndt2o = tle.xndt2o;
        this.xincl = tle.xincl;
        this.xnodeo = tle.xnodeo;
        this.eo = tle.eo;
        this.omegao = tle.omegao;
        this.xmo = tle.xmo;
        this.xno = tle.xno;
        this.deepspace = tle.deepspace;
        this.createddate = tle.createddate;
    }

    /**
     * Constructor.
     * 
     * @param tle the three line elements
     * @throws IllegalArgumentException here was something wrong with the TLE
     */
    public TLE(final String[] tle) throws IllegalArgumentException {

        if (null == tle) {
            throw new IllegalArgumentException("TLE was null");
        }

        if (tle.length != THREELINES) {
            throw new IllegalArgumentException("TLE had " + tle.length
                    + " elements");
        }

        int lineCount = 0;

        for (final String line : tle) {

            testArguments(lineCount, line);

            lineCount++;
        }

        catnum = Integer.parseInt(StringUtils.strip(tle[1].substring(2, 7)));
        name = tle[0].trim();
        setnum = Integer.parseInt(StringUtils.strip(tle[1].substring(64, 68)));
        year = Integer.parseInt(StringUtils.strip(tle[1].substring(18, 20)));
        refepoch = Double.parseDouble(tle[1].substring(20, 32));
        incl = Double.parseDouble(tle[2].substring(8, 16));
        raan = Double.parseDouble(tle[2].substring(17, 25));
        eccn = 1.0e-07 * Double.parseDouble(tle[2].substring(26, 33));
        argper = Double.parseDouble(tle[2].substring(34, 42));
        meanan = Double.parseDouble(tle[2].substring(43, 51));
        meanmo = Double.parseDouble(tle[2].substring(52, 63));
        drag = Double.parseDouble(tle[1].substring(33, 43));

        double tempnum = 1.0e-5 * Double.parseDouble(tle[1].substring(44, 50));
        nddot6 = tempnum
                / Math.pow(10.0, Double.parseDouble(tle[1].substring(51, 52)));

        tempnum = 1.0e-5 * Double.parseDouble(tle[1].substring(53, 59));

        bstar = tempnum
                / Math.pow(10.0, Double.parseDouble(tle[1].substring(60, 61)));

        orbitnum = Integer.parseInt(StringUtils.strip(tle[2].substring(63, 68)));

        /* reassign the values to thse which get used in calculations */
        epoch = (1000.0 * getYear()) + getRefepoch();

        xndt2o = drag;

        double temp = incl;
        temp *= DEG2RAD;
        xincl = temp;

        temp = raan;
        temp *= DEG2RAD;
        xnodeo = temp;

        eo = eccn;

        temp = argper;
        temp *= DEG2RAD;
        omegao = temp;

        temp = meanan;
        temp *= DEG2RAD;
        xmo = temp;

        xno = meanmo;

        /* Preprocess tle set */

        preProcessTLESet();
    }

    /**
     * 
     */
    private synchronized void preProcessTLESet() {
        double temp;
        temp = TWO_PI / MINS_PERDAY / MINS_PERDAY;
        xno = xno * temp * MINS_PERDAY;
        xndt2o *= temp;

        double dd1 = XKE / xno;
        final double a1 = Math.pow(dd1, TWO_THIRDS);
        final double r1 = Math.cos(xincl);
        dd1 = 1.0 - eo * eo;
        temp = CK2 * 1.5f * (r1 * r1 * 3.0 - 1.0)
                / Math.pow(dd1, 1.5);
        final double del1 = temp / (a1 * a1);
        final double ao = a1
                * (1.0 - del1
                        * (TWO_THIRDS * .5 + del1
                                * (del1 * 1.654320987654321 + 1.0)));
        final double delo = temp / (ao * ao);
        final double xnodp = xno / (delo + 1.0);

        /* Select a deep-space/near-earth ephemeris */

        deepspace = TWO_PI / xnodp / MINS_PERDAY >= 0.15625;
    }

    /**
     * @param lineCount the current line
     * @param line the line under test
     * @throws IllegalArgumentException there was a problem with the data
     */
    private void testArguments(final int lineCount, final String line)
            throws IllegalArgumentException {
        if (null == line) {
            throw new IllegalArgumentException(
                    createIllegalArgumentMessage(lineCount, "was null"));
        }

        if (0 == line.length()) {
            throw new IllegalArgumentException(
                    createIllegalArgumentMessage(lineCount, "was zero length"));
        }
    }

    /**
     * Default constructor cannot be invoked.
     */
    @SuppressWarnings("unused")
    private TLE() {
    }

    /**
     * @return the catalog number
     */
    public int getCatnum() {
        return this.catnum;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the element set number
     */
    public int getSetnum() {
        return this.setnum;
    }

    /**
     * @return the year part of the date of the elements
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @return the reference epoch of the elements
     */
    public double getRefepoch() {
        return this.refepoch;
    }

    /**
     * @return the inclination of the satellite orbit
     */
    public double getIncl() {
        return this.incl;
    }

    /**
     * @return the Right Ascention of the Acending Node of the orbit
     */
    public double getRaan() {
        return this.raan;
    }

    /**
     * @return the Eccentricity of the orbit
     */
    public double getEccn() {
        return this.eccn;
    }

    /**
     * @return the Argument of Perigee of the orbit
     */
    public double getArgper() {
        return this.argper;
    }

    /**
     * @return the Mean Anomoly of the orbit
     */
    public double getMeanan() {
        return this.meanan;
    }

    /**
     * @return the Mean Motion of the satellite
     */
    public double getMeanmo() {
        return this.meanmo;
    }

    /**
     * @return the Drag factor
     */
    public double getDrag() {
        return this.drag;
    }

    /**
     * @return Nddot6
     */
    public double getNddot6() {
        return this.nddot6;
    }

    /**
     * @return Bstar
     */
    public double getBstar() {
        return this.bstar;
    }

    /**
     * @return Orbitnum
     */
    public int getOrbitnum() {
        return this.orbitnum;
    }

    /**
     * @return Deepspace
     */
    public boolean isDeepspace() {
        return deepspace;
    }

    /**
     * @return Eo
     */
    public double getEo() {
        return eo;
    }

    /**
     * @return Epoch
     */
    public double getEpoch() {
        return epoch;
    }

    /**
     * @return Omegao
     */
    public double getOmegao() {
        return omegao;
    }

    /**
     * @return Xincl
     */
    public double getXincl() {
        return xincl;
    }

    /**
     * @return Xmo
     */
    public double getXmo() {
        return xmo;
    }

    /**
     * @return Xndt2o
     */
    public synchronized double getXndt2o() {
        return xndt2o;
    }

    /**
     * @return Xno
     */
    public synchronized double getXno() {
        return xno;
    }

    /**
     * @return Xnodeo
     */
    public double getXnodeo() {
        return xnodeo;
    }

    /**
     * @return the createddate
     */
    public Date getCreateddate() {
        return new Date(createddate.getTime());
    }

    /**
     * @param createddate the createddate to set
     */
    public void setCreateddate(final Date createddate) {
        this.createddate = new Date(createddate.getTime());
    }

    /**
     * @param lineCount the line count
     * @param problem the problem
     * @return the description
     */
    private String createIllegalArgumentMessage(final int lineCount, final String problem) {
        return "TLE line[" + lineCount
                + "] " + problem;
    }

    public static List<TLE> importSat(final InputStream fileIS) throws IOException {

        final List<TLE> importedSats = new ArrayList<TLE>();

        final BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
        String readString;

        int j = 0;

        final String[] lines = new String[3];

        while ((readString = buf.readLine()) != null) {

            switch (j) {
                case 0:
                case 1:
                    lines[j] = readString;
                    j++;
                    break;
                case 2:
                    lines[j] = readString;
                    j = 0;
                    importedSats.add(new TLE(lines));
                    break;
                default:
                    break;
            }
        }

        return importedSats;
    }

    @Override
    public String toString() {
        return name;
    }

}
