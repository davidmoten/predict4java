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

import java.io.Serializable;

/**
 * 
 * @author g4dpz
 * 
 */
public class LEOSatellite extends AbstractSatellite implements Serializable {

    private static final long serialVersionUID = 1206152575764077691L;
    private double aodp;
    private double aycof;
    private double c1;
    private double c4;
    private double c5;
    private double cosio;
    private double d2;
    private double d3;
    private double d4;
    private double delmo;
    private double omgcof;
    private double eta;
    private double omgdot;
    private double sinio;
    private double xnodp;
    private double sinmo;
    private double t2cof;
    private double t3cof;
    private double t4cof;
    private double t5cof;
    private double x1mth2;
    private double x3thm1;
    private double x7thm1;
    private double xmcof;
    private double xmdot;
    private double xnodcf;
    private double xnodot;
    private double xlcof;

    private boolean sgp4Init;
    private boolean sgp4Simple;

    /**
     * Creates a Low Earth Orbit Satellite.
     * 
     * @param tle the three line elements
     */
    public LEOSatellite(final TLE tle) {
        super(tle);
        sgp4Init();
    }

    @Override
    protected void calculateSGP4(final double tsince) {

        final double[] temp = new double[9];

        /* Initialization */

        if (!sgp4Init) {

            sgp4Init();
        }

        /* Update for secular gravity and atmospheric drag. */
        final double xmdf = getTLE().getXmo() + xmdot * tsince;
        final double omgadf = getTLE().getOmegao() + omgdot
                * tsince;
        final double xnoddf = getTLE().getXnodeo() + xnodot
                * tsince;
        double omega = omgadf;
        double xmp = xmdf;
        final double tsq = AbstractSatellite.sqr(tsince);
        final double xnode = xnoddf + xnodcf * tsq;
        final double bstar = getTLE().getBstar();
        double tempa = 1.0 - c1 * tsince;
        double tempe = bstar * c4 * tsince;
        double templ = t2cof * tsq;

        if (!sgp4Simple) {
            final double delomg = omgcof * tsince;
            final double delm = xmcof
                    * (Math.pow(1.0 + eta * Math.cos(xmdf), 3) - delmo);
            temp[0] = delomg + delm;
            xmp = xmdf + temp[0];
            omega = omgadf - temp[0];
            final double tcube = tsq * tsince;
            final double tfour = tsince * tcube;
            tempa = tempa - d2 * tsq - d3 * tcube - d4 * tfour;
            tempe = tempe + bstar * c5
                    * (Math.sin(xmp) - sinmo);
            templ = templ + t3cof * tcube + tfour * (t4cof + tsince * t5cof);
        }

        final double a = aodp * Math.pow(tempa, 2);
        final double eo = getTLE().getEo();
        final double e = eo - tempe;
        final double xl = xmp + omega + xnode + xnodp * templ;
        final double beta = Math.sqrt(1.0 - e * e);
        final double xn = XKE / Math.pow(a, 1.5);

        /* Long period periodics */
        final double axn = e * Math.cos(omega);
        temp[0] = AbstractSatellite.invert(a * AbstractSatellite.sqr(beta));
        final double xll = temp[0] * xlcof * axn;
        final double aynl = temp[0] * aycof;
        final double xlt = xl + xll;
        final double ayn = e * Math.sin(omega) + aynl;

        /* Solve Kepler'S Equation */
        final double capu = AbstractSatellite.mod2PI(xlt - xnode);
        temp[2] = capu;

        AbstractSatellite.converge(temp, axn, ayn, capu);

        calculatePositionAndVelocity(temp, xnode, a, xn, axn, ayn);

        calculatePhase(xlt, xnode, omgadf);
    }

    private void calculatePositionAndVelocity(final double[] temp, final double xnode, final double a, final double xn,
            final double axn, final double ayn) {
        final double ecose = temp[5] + temp[6];
        final double esine = temp[3] - temp[4];
        final double elsq = AbstractSatellite.sqr(axn) + AbstractSatellite.sqr(ayn);
        temp[0] = 1.0 - elsq;
        final double pl = a * temp[0];
        final double r = a * (1.0 - ecose);
        temp[1] = AbstractSatellite.invert(r);
        final double rdot = XKE * Math.sqrt(a) * esine * temp[1];
        final double rfdot = XKE * Math.sqrt(pl) * temp[1];
        temp[2] = a * temp[1];
        final double betal = Math.sqrt(temp[0]);
        temp[3] = AbstractSatellite.invert(1.0 + betal);
        final double cosu = temp[2] * (temp[8] - axn + ayn * esine * temp[3]);
        final double sinu = temp[2] * (temp[7] - ayn - axn * esine * temp[3]);
        final double u = Math.atan2(sinu, cosu);
        final double sin2u = 2.0 * sinu * cosu;
        final double cos2u = 2.0 * cosu * cosu - 1;
        temp[0] = AbstractSatellite.invert(pl);
        temp[1] = CK2 * temp[0];
        temp[2] = temp[1] * temp[0];

        /* Update for short periodics */
        final double rk = r * (1.0 - 1.5 * temp[2] * betal * x3thm1) + 0.5 * temp[1]
                * x1mth2 * cos2u;
        final double uk = u - 0.25 * temp[2] * x7thm1 * sin2u;
        final double xnodek = xnode + 1.5 * temp[2] * cosio * sin2u;
        final double xinck = getTLE().getXincl() + 1.5 * temp[2]
                * cosio * sinio * cos2u;
        final double rdotk = rdot - xn * temp[1] * x1mth2 * sin2u;
        final double rfdotk = rfdot + xn * temp[1]
                * (x1mth2 * cos2u + 1.5 * x3thm1);

        super.calculatePositionAndVelocity(rk, uk, xnodek, xinck, rdotk, rfdotk);
    }

    /**
     * 
     */
    private void sgp4Init() {

        /* Recover original mean motion (xnodp) and */
        /* semimajor axis (aodp) from input elements. */

        final double a1 = Math.pow(XKE / getTLE().getXno(),
                TWO_THIRDS);
        cosio = Math.cos(getTLE().getXincl());
        final double theta2 = AbstractSatellite.sqr(cosio);
        x3thm1 = 3.0 * theta2 - 1.0;
        final double eo = getTLE().getEo();
        final double eosq = AbstractSatellite.sqr(eo);
        final double betao2 = 1.0 - eosq;
        final double betao = Math.sqrt(betao2);
        final double del1 = 1.5 * CK2 * x3thm1 / (AbstractSatellite.sqr(a1) * betao * betao2);
        final double ao = a1
                * (1.0 - del1
                        * (0.5 * TWO_THIRDS + del1
                                * (1.0 + 134.0 / 81.0 * del1)));
        final double delo = 1.5 * CK2 * x3thm1 / (AbstractSatellite.sqr(ao) * betao * betao2);
        xnodp = getTLE().getXno() / (1.0 + delo);
        aodp = ao / (1.0 - delo);

        /* For perigee less than 220 kilometers, the "simple" */
        /* flag is set and the equations are truncated to linear */
        /* variation in sqrt a and quadratic variation in mean */
        /* anomaly. Also, the c3 term, the delta omega term, and */
        /* the delta m term are dropped. */

        sgp4Simple = (aodp * (1.0 - eo)) < (220 / EARTH_RADIUS_KM + 1.0);

        /* For perigees below 156 km, the */
        /* values of S and QOMS2T are altered. */
        setPerigee((aodp * (1.0 - eo) - 1.0) * EARTH_RADIUS_KM);

        checkPerigee();

        final double pinvsq = AbstractSatellite.invert(AbstractSatellite.sqr(aodp) * AbstractSatellite.sqr(betao2));
        final double tsi = AbstractSatellite.invert(aodp - getS4());
        eta = aodp * eo * tsi;
        final double etasq = eta * eta;
        final double eeta = eo * eta;
        final double psisq = Math.abs(1.0 - etasq);
        final double coef = getQoms24() * Math.pow(tsi, 4);
        final double coef1 = coef / Math.pow(psisq, 3.5);
        final double bstar = getTLE().getBstar();
        final double c2 = coef1
                * xnodp
                * (aodp * (1.0 + 1.5 * etasq + eeta * (4.0 + etasq)) + 0.75
                        * CK2 * tsi / psisq * x3thm1
                        * (8.0 + 3.0 * etasq * (8.0 + etasq)));
        c1 = bstar * c2;
        sinio = Math.sin(getTLE().getXincl());
        final double a3ovk2 = -J3_HARMONIC / CK2;
        final double c3 = coef * tsi * a3ovk2 * xnodp * sinio
                / eo;
        x1mth2 = 1.0 - theta2;

        final double omegao = getTLE().getOmegao();

        c4 = 2
                * xnodp
                * coef1
                * aodp
                * betao2
                * (eta * (2.0 + 0.5 * etasq) + eo
                        * (0.5 + 2 * etasq) - 2
                        * CK2
                        * tsi
                        / (aodp * psisq)
                        * (-3
                                * x3thm1
                                * (1.0 - 2 * eeta + etasq
                                        * (1.5 - 0.5 * eeta)) + 0.75
                                * x1mth2
                                * (2.0 * etasq - eeta * (1.0 + etasq))
                                * Math.cos(2.0 * omegao
                                        )));

        c5 = 2.0 * coef1 * aodp * betao2
                * (1.0 + 2.75 * (etasq + eeta) + eeta * etasq);

        final double theta4 = AbstractSatellite.sqr(theta2);
        final double temp1 = 3.0 * CK2 * pinvsq * xnodp;
        final double temp2 = temp1 * CK2 * pinvsq;
        final double temp3 = 1.25 * CK4 * pinvsq * pinvsq * xnodp;
        xmdot = xnodp + 0.5 * temp1 * betao * x3thm1 + 0.0625 * temp2
                * betao * (13.0 - 78.0 * theta2 + 137.0 * theta4);
        final double x1m5th = 1.0 - 5.0 * theta2;
        omgdot = -0.5 * temp1 * x1m5th + 0.0625 * temp2
                * (7.0 - 114.0 * theta2 + 395.0 * theta4) + temp3
                * (3.0 - 36.0 * theta2 + 49.0 * theta4);
        final double xhdot1 = -temp1 * cosio;
        xnodot = xhdot1
                + (0.5 * temp2 * (4.0 - 19.0 * theta2) + 2.0 * temp3
                        * (3.0 - 7.0 * theta2)) * cosio;
        omgcof = bstar * c3
                * Math.cos(omegao);
        xmcof = -TWO_THIRDS * coef * bstar / eeta;
        xnodcf = 3.5 * betao2 * xhdot1 * c1;
        t2cof = 1.5 * c1;
        xlcof = 0.125 * a3ovk2 * sinio * (3.0 + 5 * cosio) / (1.0 + cosio);
        aycof = 0.25 * a3ovk2 * sinio;
        final double xmo = getTLE().getXmo();
        delmo = Math.pow(1.0 + eta
                * Math.cos(xmo), 3);
        sinmo = Math.sin(xmo);
        x7thm1 = 7.0 * theta2 - 1;

        if (!sgp4Simple) {
            final double c1sq = AbstractSatellite.sqr(c1);
            d2 = 4.0 * aodp * tsi * c1sq;
            final double temp = d2 * tsi * c1 / 3.0;
            d3 = (17 * aodp + getS4()) * temp;
            d4 = 0.5 * temp * aodp * tsi * (221 * aodp + 31 * getS4()) * c1;
            t3cof = d2 + 2 * c1sq;
            t4cof = 0.25 * (3.0 * d3 + c1 * (12 * d2 + 10 * c1sq));
            t5cof = 0.2 * (3.0 * d4 + 12 * c1 * d3 + 6 * d2 * d2 + 15 * c1sq
                    * (2.0 * d2 + c1sq));
        }

        sgp4Init = true;
    }
}
