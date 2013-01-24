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
 * DeepSpaceSatellite.
 * 
 * @author g4dpz
 * 
 */
public class DeepSpaceSatellite extends AbstractSatellite implements Serializable {

    private static final long serialVersionUID = -9151311937099118037L;
    private double c1;
    private double c4;
    private double x1mth2;
    private double x3thm1;
    private double xlcof;
    private double xnodcf;
    private double t2cof;
    private double aycof;
    private double x7thm1;

    private boolean sdp4Init;

    private final DeepSpaceValueObject dsv;

    private final DeepSpaceCalculator deep;

    /**
     * DeepSpaceSatellite Constructor.
     * 
     * @param tle the three line elements
     */
    public DeepSpaceSatellite(final TLE tle) {
        super(tle);
        this.dsv = new DeepSpaceValueObject();
        this.deep = new DeepSpaceCalculator();
        initSDP4();
    }

    /**
     * This function is used to calculate the position and velocity of deep-space (period > 225
     * minutes) satellites. tsince is time since epoch in minutes, tle is a pointer to a tle_t
     * structure with Keplerian orbital elements and pos and vel are vector_t structures returning
     * ECI satellite position and velocity. Use Convert_Sat_State() to convert to km and km/S.
     * 
     * @param tsince time since the epoch
     * @param position the position
     * @param velocity the velocity
     * @param satPos the position of the satellite
     */
    @Override
    protected synchronized void calculateSDP4(final double tsince) {

        final double[] temp = new double[12];

        /* Initialization */

        if (!sdp4Init) {

            initSDP4();
        }

        final double xmdf = getTLE().getXmo() + dsv.xmdot * tsince;
        final double tsq = tsince * tsince;
        final double templ = t2cof * tsq;
        dsv.xll = xmdf + dsv.xnodp * templ;

        dsv.omgadf = getTLE().getOmegao() + dsv.omgdot * tsince;
        final double xnoddf = getTLE().getXnodeo() + dsv.xnodot * tsince;
        dsv.xnode = xnoddf + xnodcf * tsq;
        final double tempa = 1.0 - c1 * tsince;
        final double tempe = getTLE().getBstar() * c4 * tsince;
        dsv.xn = dsv.xnodp;

        dsv.t = tsince;

        deep.dpsec(getTLE());

        final double a = Math.pow(XKE / dsv.xn, TWO_THIRDS) * tempa * tempa;
        dsv.em = dsv.em - tempe;
        deep.dpper(getTLE());

        final double xl = dsv.xll + dsv.omgadf + dsv.xnode;
        final double beta = Math.sqrt(1.0 - dsv.em * dsv.em);
        dsv.xn = XKE / Math.pow(a, 1.5);

        /* Long period periodics */
        final double axn = dsv.em * Math.cos(dsv.omgadf);
        temp[0] = AbstractSatellite.invert(a * beta * beta);
        final double xll = temp[0] * xlcof * axn;
        final double aynl = temp[0] * aycof;
        final double xlt = xl + xll;
        final double ayn = dsv.em * Math.sin(dsv.omgadf) + aynl;

        /* Solve Kepler'S Equation */
        final double capu = AbstractSatellite.mod2PI(xlt - dsv.xnode);
        temp[2] = capu;

        AbstractSatellite.converge(temp, axn, ayn, capu);

        calculatePositionAndVelocity(temp, a, axn, ayn);

        calculatePhase(xlt, dsv.xnode, dsv.omgadf);
    }

    private void calculatePositionAndVelocity(final double[] temp, final double a, final double axn, final double ayn) {
        final double ecose = temp[5] + temp[6];
        final double esine = temp[3] - temp[4];
        final double elsq = axn * axn + ayn * ayn;
        temp[0] = 1.0 - elsq;
        final double pl = a * temp[0];
        temp[9] = a * (1.0 - ecose);
        temp[1] = AbstractSatellite.invert(temp[9]);
        temp[10] = XKE * Math.sqrt(a) * esine * temp[1];
        temp[11] = XKE * Math.sqrt(pl) * temp[1];
        temp[2] = a * temp[1];
        final double betal = Math.sqrt(temp[0]);
        temp[3] = AbstractSatellite.invert(1.0 + betal);
        final double cosu = temp[2]
                * (temp[8] - axn + ayn * esine * temp[3]);
        final double sinu = temp[2]
                * (temp[7] - ayn - axn * esine * temp[3]);
        final double u = Math.atan2(sinu, cosu);
        final double sin2u = 2.0 * sinu * cosu;
        final double cos2u = 2.0 * cosu * cosu - 1;
        temp[0] = AbstractSatellite.invert(pl);
        temp[1] = CK2 * temp[0];
        temp[2] = temp[1] * temp[0];

        /* Update for short periodics */
        final double rk = temp[9] * (1.0 - 1.5 * temp[2] * betal * x3thm1) + 0.5
                * temp[1] * x1mth2 * cos2u;
        final double uk = u - 0.25 * temp[2] * x7thm1 * sin2u;
        final double xnodek = dsv.xnode + 1.5 * temp[2] * dsv.cosio * sin2u;
        final double xinck = dsv.xinc + 1.5 * temp[2] * dsv.cosio
                * dsv.sinio * cos2u;
        final double rdotk = temp[10] - dsv.xn * temp[1] * x1mth2 * sin2u;
        final double rfdotk = temp[11] + dsv.xn * temp[1]
                * (x1mth2 * cos2u + 1.5 * x3thm1);

        super.calculatePositionAndVelocity(rk, uk, xnodek, xinck, rdotk, rfdotk);
    }

    /**
     * 
     */
    private void initSDP4() {
        double temp1;
        double temp2;
        double temp3;
        sdp4Init = true;

        /* Recover original mean motion (xnodp) and */
        /* semimajor axis (aodp) from input elements. */

        recoverMeanMotionAndSemiMajorAxis();

        /* For perigee below 156 km, the values */
        /* of S and QOMS2T are altered. */
        setPerigee((dsv.aodp * (1.0 - getTLE().getEo()) - 1.0) * EARTH_RADIUS_KM);

        checkPerigee();

        final double pinvsq = AbstractSatellite.invert(dsv.aodp * dsv.aodp * dsv.betao2 * dsv.betao2);
        dsv.sing = Math.sin(getTLE().getOmegao());
        dsv.cosg = Math.cos(getTLE().getOmegao());
        final double tsi = AbstractSatellite.invert(dsv.aodp - getS4());
        final double eta = dsv.aodp * getTLE().getEo() * tsi;
        final double etasq = eta * eta;
        final double eeta = getTLE().getEo() * eta;
        final double psisq = Math.abs(1.0 - etasq);
        final double coef = getQoms24() * Math.pow(tsi, 4);
        final double coef1 = coef / Math.pow(psisq, 3.5);
        final double c2 = coef1
                * dsv.xnodp
                * (dsv.aodp * (1.0 + 1.5 * etasq + eeta * (4.0 + etasq)) + 0.75
                        * CK2 * tsi / psisq * x3thm1
                        * (8.0 + 3.0 * etasq * (8.0 + etasq)));
        c1 = getTLE().getBstar() * c2;
        dsv.sinio = Math.sin(getTLE().getXincl());
        final double a3ovk2 = -J3_HARMONIC / CK2;
        x1mth2 = 1.0 - dsv.theta2;
        c4 = 2
                * dsv.xnodp
                * coef1
                * dsv.aodp
                * dsv.betao2
                * (eta * (2.0 + 0.5 * etasq) + getTLE().getEo()
                        * (0.5 + 2 * etasq) - 2
                        * CK2
                        * tsi
                        / (dsv.aodp * psisq)
                        * (-3 * x3thm1
                                * (1.0 - 2 * eeta + etasq * (1.5 - 0.5 * eeta)) + 0.75
                                * x1mth2
                                * (2.0 * etasq - eeta * (1.0 + etasq))
                                * Math.cos(2.0 * getTLE().getOmegao())));
        final double theta4 = dsv.theta2 * dsv.theta2;
        temp1 = 3.0 * CK2 * pinvsq * dsv.xnodp;
        temp2 = temp1 * CK2 * pinvsq;
        temp3 = 1.25 * CK4 * pinvsq * pinvsq * dsv.xnodp;
        dsv.xmdot = dsv.xnodp + 0.5 * temp1 * dsv.betao * x3thm1 + 0.0625
                * temp2 * dsv.betao * (13 - 78 * dsv.theta2 + 137 * theta4);
        final double x1m5th = 1.0 - 5 * dsv.theta2;
        dsv.omgdot = -0.5 * temp1 * x1m5th + 0.0625 * temp2
                * (7.0 - 114 * dsv.theta2 + 395 * theta4) + temp3
                * (3.0 - 36 * dsv.theta2 + 49 * theta4);
        final double xhdot1 = -temp1 * dsv.cosio;
        dsv.xnodot = xhdot1
                + (0.5 * temp2 * (4.0 - 19 * dsv.theta2) + 2 * temp3
                        * (3.0 - 7 * dsv.theta2)) * dsv.cosio;
        xnodcf = 3.5 * dsv.betao2 * xhdot1 * c1;
        t2cof = 1.5 * c1;
        xlcof = 0.125 * a3ovk2 * dsv.sinio * (3.0 + 5 * dsv.cosio)
                / (1.0 + dsv.cosio);
        aycof = 0.25 * a3ovk2 * dsv.sinio;
        x7thm1 = 7.0 * dsv.theta2 - 1;

        /* initialize Deep() */

        deep.init(getTLE());
    }

    /**
     * 
     */
    private void recoverMeanMotionAndSemiMajorAxis() {
        final double a1 = Math.pow(XKE / getTLE().getXno(), TWO_THIRDS);
        dsv.cosio = Math.cos(getTLE().getXincl());
        dsv.theta2 = dsv.cosio * dsv.cosio;
        x3thm1 = 3.0 * dsv.theta2 - 1;
        dsv.eosq = getTLE().getEo() * getTLE().getEo();
        dsv.betao2 = 1.0 - dsv.eosq;
        dsv.betao = Math.sqrt(dsv.betao2);
        final double del1 = 1.5 * CK2 * x3thm1
                / (a1 * a1 * dsv.betao * dsv.betao2);
        final double ao = a1
                * (1.0 - del1 * (0.5 * TWO_THIRDS + del1 * (1.0 + 134 / 81 * del1)));
        final double delo = 1.5 * CK2 * x3thm1
                / (ao * ao * dsv.betao * dsv.betao2);
        dsv.xnodp = getTLE().getXno() / (1.0 + delo);
        dsv.aodp = ao / (1.0 - delo);
    }

    final class DeepSpaceCalculator implements Serializable {
        /* This function is used by SDP4 to add lunar and solar */
        /* perturbation effects to deep-space orbit objects. */

        private static final long serialVersionUID = -1154274461279090353L;
        static final double ZSINIS = 3.9785416E-1;
        static final double ZSINGS = -9.8088458E-1;
        static final double ZNS = 1.19459E-5;
        static final double C1SS = 2.9864797E-6;
        static final double ZES = 1.675E-2;
        static final double ZNL = 1.5835218E-4;
        static final double C1L = 4.7968065E-7;
        static final double ZEL = 5.490E-2;
        static final double ROOT22 = 1.7891679E-6;
        static final double ROOT32 = 3.7393792E-7;
        static final double ROOT44 = 7.3636953E-9;
        static final double ROOT52 = 1.1428639E-7;
        static final double ROOT54 = 2.1765803E-9;
        static final double THDT = 4.3752691E-3;
        static final double Q22 = 1.7891679E-6;
        static final double Q31 = 2.1460748E-6;
        static final double Q33 = 2.2123015E-7;
        static final double G22 = 5.7686396;
        static final double G32 = 9.5240898E-1;
        static final double G44 = 1.8014998;
        static final double G52 = 1.0508330;
        static final double G54 = 4.4108898;

        private double thgr;
        private double xnq;
        private double xqncl;
        private double omegaq;
        private double zmol;
        private double zmos;
        private double savtsn;
        private double ee2;
        private double e3;
        private double xi2;
        private double xl2;
        private double xl3;
        private double xl4;
        private double xgh2;
        private double xgh3;
        private double xgh4;
        private double xh2;
        private double xh3;
        private double sse;
        private double ssi;
        private double ssg;
        private double xi3;
        private double se2;
        private double si2;
        private double sl2;
        private double sgh2;
        private double sh2;
        private double se3;
        private double si3;
        private double sl3;
        private double sgh3;
        private double sh3;
        private double sl4;
        private double sgh4;
        private double ssl;
        private double ssh;
        private double d3210;
        private double d3222;
        private double d4410;
        private double d4422;
        private double d5220;
        private double d5232;
        private double d5421;
        private double d5433;
        private double del1;
        private double del2;
        private double del3;
        private double fasx2;
        private double fasx4;
        private double fasx6;
        private double xlamo;
        private double xfact;
        private double xni;
        private double atime;
        private double stepp;
        private double stepn;
        private double step2;
        private double preep;
        private double pl;
        private double sghs;
        private double xli;
        private double d2201;
        private double d2211;
        private double sghl;
        private double sh1;
        private double pinc;
        private double pe;
        private double shs;
        private double zsingl;
        private double zcosgl;
        private double zsinhl;
        private double zcoshl;
        private double zsinil;
        private double zcosil;

        private double a1;
        private double a2;
        private double a3;
        private double a4;
        private double a5;
        private double a6;
        private double a7;
        private double a8;
        private double a9;
        private double a10;
        private double ainv2;
        private double alfdp;
        private double aqnv;
        private double sgh;
        private double sini2;
        private double sinis;
        private double sinok;
        private double sh;
        private double si;
        private double sil;
        private double day;
        private double betdp;
        private double dalf;
        private double bfact;
        private double c;
        private double cc;
        private double cosis;
        private double cosok;
        private double cosq;
        private double ctem;
        private double f322;
        private double zx;
        private double zy;
        private double dbet;
        private double dls;
        private double eoc;
        private double eq;
        private double f2;
        private double f220;
        private double f221;
        private double f3;
        private double f311;
        private double f321;
        private double xnoh;
        private double f330;
        private double f441;
        private double f442;
        private double f522;
        private double f523;
        private double f542;
        private double f543;
        private double g200;
        private double g201;
        private double g211;
        private double pgh;
        private double ph;
        private double s1;
        private double s2;
        private double s3;
        private double s4;
        private double s5;
        private double s6;
        private double s7;
        private double se;
        private double sel;
        private double ses;
        private double xls;
        private double g300;
        private double g310;
        private double g322;
        private double g410;
        private double g422;
        private double g520;
        private double g521;
        private double g532;
        private double g533;
        private double gam;
        private double sinq;
        private double sinzf;
        private double sis;
        private double sl;
        private double sll;
        private double sls;
        private double stem;
        private double temp;
        private double temp1;
        private double x1;
        private double x2;
        private double x2li;
        private double x2omi;
        private double x3;
        private double x4;
        private double x5;
        private double x6;
        private double x7;
        private double x8;
        private double xl;
        private double xldot;
        private double xmao;
        private double xnddt;
        private double xndot;
        private double xno2;
        private double xnodce;
        private double xnoi;
        private double xomi;
        private double xpidot;
        private double z1;
        private double z11;
        private double z12;
        private double z13;
        private double z2;
        private double z21;
        private double z22;
        private double z23;
        private double z3;
        private double z31;
        private double z32;
        private double z33;
        private double ze;
        private double zf;
        private double zm;
        private double zn;
        private double zsing;
        private double zsinh;
        private double zsini;
        private double zcosg;
        private double zcosh;
        private double zcosi;
        private double delt;
        private double ft;

        private boolean lunarTermsDone;
        private boolean resonance;
        private boolean synchronous;
        private boolean doLoop;
        private boolean epochRestart;

        private DeepSpaceCalculator() {
        }

        /**
         * Entrance for deep space initialization.
         * 
         * @param tle the three line elements
         */
        private void init(final TLE tle) {
            thgr = thetaG(tle.getRefepoch());
            eq = tle.getEo();
            xnq = dsv.xnodp;
            aqnv = AbstractSatellite.invert(dsv.aodp);
            xqncl = tle.getXincl();
            xmao = tle.getXmo();
            xpidot = dsv.omgdot + dsv.xnodot;
            sinq = Math.sin(tle.getXnodeo());
            cosq = Math.cos(tle.getXnodeo());
            omegaq = tle.getOmegao();

            /* Initialize lunar solar terms */
            /* Days since 1900 Jan 0.5 */
            initLunarSolarTerms();

            /* Do solar terms */
            doSolarTerms();

            /* Geopotential resonance initialization for 12 hour orbits */
            resonance = false;
            synchronous = false;

            if (!((xnq < 0.0052359877) && (xnq > 0.0034906585))) {
                if ((xnq < 0.00826) || (xnq > 0.00924)) {
                    return;
                }

                if (eq < 0.5) {
                    return;
                }

                calculateResonance(tle);
            }
            else {
                initSynchronousResonanceTerms(tle);
            }

            xfact = bfact - xnq;

            /* Initialize integrator */
            xli = xlamo;
            xni = xnq;
            atime = 0;
            stepp = 720;
            stepn = -720;
            step2 = 259200;

        }

        /**
         * @param tle
         */
        private void calculateResonance(final TLE tle) {
            resonance = true;
            eoc = eq * dsv.eosq;
            g201 = -0.306 - (eq - 0.64) * 0.440;

            if (eq <= 0.65) {
                g211 = 3.616 - 13.247 * eq + 16.290 * dsv.eosq;
                g310 = -19.302 + 117.390 * eq - 228.419 * dsv.eosq + 156.591
                        * eoc;
                g322 = -18.9068 + 109.7927 * eq - 214.6334 * dsv.eosq
                        + 146.5816 * eoc;
                g410 = -41.122 + 242.694 * eq - 471.094 * dsv.eosq + 313.953
                        * eoc;
                g422 = -146.407 + 841.880 * eq - 1629.014 * dsv.eosq + 1083.435
                        * eoc;
                g520 = -532.114 + 3017.977 * eq - 5740 * dsv.eosq + 3708.276
                        * eoc;
            }
            else {
                g211 = -72.099 + 331.819 * eq - 508.738 * dsv.eosq + 266.724
                        * eoc;
                g310 = -346.844 + 1582.851 * eq - 2415.925 * dsv.eosq
                        + 1246.113 * eoc;
                g322 = -342.585 + 1554.908 * eq - 2366.899 * dsv.eosq
                        + 1215.972 * eoc;
                g410 = -1052.797 + 4758.686 * eq - 7193.992 * dsv.eosq
                        + 3651.957 * eoc;
                g422 = -3581.69 + 16178.11 * eq - 24462.77 * dsv.eosq
                        + 12422.52 * eoc;

                if (eq <= 0.715) {
                    g520 = 1464.74 - 4664.75 * eq + 3763.64 * dsv.eosq;
                }
                else {
                    g520 = -5149.66 + 29936.92 * eq - 54087.36 * dsv.eosq
                            + 31324.56 * eoc;
                }
            }

            if (eq < 0.7) {
                g533 = -919.2277 + 4988.61 * eq - 9064.77 * dsv.eosq + 5542.21
                        * eoc;
                g521 = -822.71072 + 4568.6173 * eq - 8491.4146 * dsv.eosq
                        + 5337.524 * eoc;
                g532 = -853.666 + 4690.25 * eq - 8624.77 * dsv.eosq + 5341.4
                        * eoc;
            }
            else {
                g533 = -37995.78 + 161616.52 * eq - 229838.2 * dsv.eosq
                        + 109377.94 * eoc;
                g521 = -51752.104 + 218913.95 * eq - 309468.16 * dsv.eosq
                        + 146349.42 * eoc;
                g532 = -40023.88 + 170470.89 * eq - 242699.48 * dsv.eosq
                        + 115605.82 * eoc;
            }

            sini2 = dsv.sinio * dsv.sinio;
            f220 = 0.75 * (1.0 + 2 * dsv.cosio + dsv.theta2);
            f221 = 1.5 * sini2;
            f321 = 1.875 * dsv.sinio * (1.0 - 2 * dsv.cosio - 3.0 * dsv.theta2);
            f322 = -1.875 * dsv.sinio * (1.0 + 2 * dsv.cosio - 3.0 * dsv.theta2);
            f441 = 35 * sini2 * f220;
            f442 = 39.3750 * sini2 * sini2;
            f522 = 9.84375
                    * dsv.sinio
                    * (sini2 * (1.0 - 2 * dsv.cosio - 5 * dsv.theta2) + 0.33333333 * (-2
                            + 4 * dsv.cosio + 6 * dsv.theta2));
            f523 = dsv.sinio
                    * (4.92187512 * sini2
                            * (-2 - 4 * dsv.cosio + 10 * dsv.theta2) + 6.56250012
                    * (1.0 + 2 * dsv.cosio - 3.0 * dsv.theta2));
            f542 = 29.53125
                    * dsv.sinio
                    * (2.0 - 8 * dsv.cosio + dsv.theta2
                            * (-12 + 8 * dsv.cosio + 10 * dsv.theta2));
            f543 = 29.53125
                    * dsv.sinio
                    * (-2 - 8 * dsv.cosio + dsv.theta2
                            * (12 + 8 * dsv.cosio - 10 * dsv.theta2));
            xno2 = xnq * xnq;
            ainv2 = aqnv * aqnv;
            temp1 = 3.0 * xno2 * ainv2;
            temp = temp1 * ROOT22;
            d2201 = temp * f220 * g201;
            d2211 = temp * f221 * g211;
            temp1 = temp1 * aqnv;
            temp = temp1 * ROOT32;
            d3210 = temp * f321 * g310;
            d3222 = temp * f322 * g322;
            temp1 = temp1 * aqnv;
            temp = 2.0 * temp1 * ROOT44;
            d4410 = temp * f441 * g410;
            d4422 = temp * f442 * g422;
            temp1 = temp1 * aqnv;
            temp = temp1 * ROOT52;
            d5220 = temp * f522 * g520;
            d5232 = temp * f523 * g532;
            temp = 2.0 * temp1 * ROOT54;
            d5421 = temp * f542 * g521;
            d5433 = temp * f543 * g533;
            xlamo = xmao + tle.getXnodeo() + tle.getXnodeo() - thgr - thgr;
            bfact = dsv.xmdot + dsv.xnodot + dsv.xnodot - THDT - THDT;
            bfact = bfact + ssl + ssh + ssh;
        }

        /**
         * 
         */
        private void doSolarTerms() {
            savtsn = 1E20;
            zcosg = 1.945905E-1;
            zsing = ZSINGS;
            zcosi = 9.1744867E-1;
            zsini = ZSINIS;
            zcosh = cosq;
            zsinh = sinq;
            cc = C1SS;
            zn = ZNS;
            ze = ZES;
            xnoi = AbstractSatellite.invert(xnq);

            /* Loop breaks when Solar terms are done a second */
            /* time, after Lunar terms are initialized */

            while (true) {
                /* Solar terms done again after Lunar terms are done */
                calculateSolarTerms();

                if (lunarTermsDone) {
                    break;
                }

                /* Do lunar terms */
                calculateLunarTerms();
            }

            sse = sse + se;
            ssi = ssi + si;
            ssl = ssl + sl;
            ssg = ssg + sgh - dsv.cosio / dsv.sinio * sh;
            ssh = ssh + sh / dsv.sinio;
        }

        /**
         * 
         */
        private void calculateLunarTerms() {
            sse = se;
            ssi = si;
            ssl = sl;
            ssh = sh / dsv.sinio;
            ssg = sgh - dsv.cosio * ssh;
            se2 = ee2;
            si2 = xi2;
            sl2 = xl2;
            sgh2 = xgh2;
            sh2 = xh2;
            se3 = e3;
            si3 = xi3;
            sl3 = xl3;
            sgh3 = xgh3;
            sh3 = xh3;
            sl4 = xl4;
            sgh4 = xgh4;
            zcosg = zcosgl;
            zsing = zsingl;
            zcosi = zcosil;
            zsini = zsinil;
            zcosh = zcoshl * cosq + zsinhl * sinq;
            zsinh = sinq * zcoshl - cosq * zsinhl;
            zn = ZNL;
            cc = C1L;
            ze = ZEL;
            lunarTermsDone = true;
        }

        /**
         * 
         */
        private void calculateSolarTerms() {
            a1 = zcosg * zcosh + zsing * zcosi * zsinh;
            a3 = -zsing * zcosh + zcosg * zcosi * zsinh;
            a7 = -zcosg * zsinh + zsing * zcosi * zcosh;
            a8 = zsing * zsini;
            a9 = zsing * zsinh + zcosg * zcosi * zcosh;
            a10 = zcosg * zsini;
            a2 = dsv.cosio * a7 + dsv.sinio * a8;
            a4 = dsv.cosio * a9 + dsv.sinio * a10;
            a5 = -dsv.sinio * a7 + dsv.cosio * a8;
            a6 = -dsv.sinio * a9 + dsv.cosio * a10;
            x1 = a1 * dsv.cosg + a2 * dsv.sing;
            x2 = a3 * dsv.cosg + a4 * dsv.sing;
            x3 = -a1 * dsv.sing + a2 * dsv.cosg;
            x4 = -a3 * dsv.sing + a4 * dsv.cosg;
            x5 = a5 * dsv.sing;
            x6 = a6 * dsv.sing;
            x7 = a5 * dsv.cosg;
            x8 = a6 * dsv.cosg;
            z31 = 12 * x1 * x1 - 3.0 * x3 * x3;
            z32 = 24 * x1 * x2 - 6 * x3 * x4;
            z33 = 12 * x2 * x2 - 3.0 * x4 * x4;
            z1 = 3.0 * (a1 * a1 + a2 * a2) + z31 * dsv.eosq;
            z2 = 6.0 * (a1 * a3 + a2 * a4) + z32 * dsv.eosq;
            z3 = 3.0 * (a3 * a3 + a4 * a4) + z33 * dsv.eosq;
            z11 = -6 * a1 * a5 + dsv.eosq * (-24 * x1 * x7 - 6 * x3 * x5);
            z12 = -6 * (a1 * a6 + a3 * a5) + dsv.eosq
                    * (-24 * (x2 * x7 + x1 * x8) - 6 * (x3 * x6 + x4 * x5));
            z13 = -6 * a3 * a6 + dsv.eosq * (-24 * x2 * x8 - 6 * x4 * x6);
            z21 = 6.0 * a2 * a5 + dsv.eosq * (24 * x1 * x5 - 6 * x3 * x7);
            z22 = 6.0 * (a4 * a5 + a2 * a6) + dsv.eosq
                    * (24 * (x2 * x5 + x1 * x6) - 6 * (x4 * x7 + x3 * x8));
            z23 = 6.0 * a4 * a6 + dsv.eosq * (24 * x2 * x6 - 6 * x4 * x8);
            z1 = z1 + z1 + dsv.betao2 * z31;
            z2 = z2 + z2 + dsv.betao2 * z32;
            z3 = z3 + z3 + dsv.betao2 * z33;
            s3 = cc * xnoi;
            s2 = -0.5 * s3 / dsv.betao;
            s4 = s3 * dsv.betao;
            s1 = -15 * eq * s4;
            s5 = x1 * x3 + x2 * x4;
            s6 = x2 * x3 + x1 * x4;
            s7 = x2 * x4 - x1 * x3;
            se = s1 * zn * s5;
            si = s2 * zn * (z11 + z13);
            sl = -zn * s3 * (z1 + z3 - 14 - 6 * dsv.eosq);
            sgh = s4 * zn * (z31 + z33 - 6);
            sh = -zn * s2 * (z21 + z23);

            if (xqncl < 5.2359877E-2) {
                sh = 0;
            }

            ee2 = 2.0 * s1 * s6;
            e3 = 2.0 * s1 * s7;
            xi2 = 2.0 * s2 * z12;
            xi3 = 2.0 * s2 * (z13 - z11);
            xl2 = -2 * s3 * z2;
            xl3 = -2 * s3 * (z3 - z1);
            xl4 = -2 * s3 * (-21 - 9 * dsv.eosq) * ze;
            xgh2 = 2.0 * s4 * z32;
            xgh3 = 2.0 * s4 * (z33 - z31);
            xgh4 = -18 * s4 * ze;
            xh2 = -2 * s2 * z22;
            xh3 = -2 * s2 * (z23 - z21);
        }

        /**
         * 
         */
        private void initLunarSolarTerms() {
            day = dsv.ds50 + 18261.5;

            if (Math.abs(day - preep) > 1.0E-6) {
                preep = day;
                xnodce = 4.5236020 - 9.2422029E-4 * day;
                stem = Math.sin(xnodce);
                ctem = Math.cos(xnodce);
                zcosil = 0.91375164 - 0.03568096 * ctem;
                zsinil = Math.sqrt(1.0 - zcosil * zcosil);
                zsinhl = 0.089683511 * stem / zsinil;
                zcoshl = Math.sqrt(1.0 - zsinhl * zsinhl);
                c = 4.7199672 + 0.22997150 * day;
                gam = 5.8351514 + 0.0019443680 * day;
                zmol = AbstractSatellite.mod2PI(c - gam);
                zx = 0.39785416 * stem / zsinil;
                zy = zcoshl * ctem + 0.91744867 * zsinhl * stem;
                zx = Math.atan2(zx, zy);
                zx = gam + zx - xnodce;
                zcosgl = Math.cos(zx);
                zsingl = Math.sin(zx);
                zmos = 6.2565837 + 0.017201977 * day;
                zmos = AbstractSatellite.mod2PI(zmos);
            }
        }

        /**
         * Initialises the Synchronous resonance terms.
         * 
         * @param tle The <code>TLE</code>
         * @param dsv The <code>DeepSpaceValueObject</code>
         */
        private void initSynchronousResonanceTerms(final TLE tle) {
            resonance = true;
            synchronous = true;

            g200 = 1.0 + dsv.eosq * (-2.5 + 0.8125 * dsv.eosq);
            g310 = 1.0 + 2 * dsv.eosq;
            g300 = 1.0 + dsv.eosq * (-6 + 6.60937 * dsv.eosq);
            f220 = 0.75 * (1.0 + dsv.cosio) * (1.0 + dsv.cosio);
            f311 = 0.9375 * dsv.sinio * dsv.sinio * (1.0 + 3.0 * dsv.cosio) - 0.75
                    * (1.0 + dsv.cosio);
            f330 = 1.0 + dsv.cosio;
            f330 = 1.875 * f330 * f330 * f330;
            del1 = 3.0 * xnq * xnq * aqnv * aqnv;
            del2 = 2.0 * del1 * f220 * g200 * Q22;
            del3 = 3.0 * del1 * f330 * g300 * Q33 * aqnv;
            del1 = del1 * f311 * g310 * Q31 * aqnv;
            fasx2 = 0.13130908;
            fasx4 = 2.8843198;
            fasx6 = 0.37448087;
            xlamo = xmao + tle.getXnodeo() + tle.getOmegao() - thgr;
            bfact = dsv.xmdot + xpidot - THDT;
            bfact = bfact + ssl + ssg + ssh;
        }

        /**
         * Entrance for deep space secular effects.
         * 
         * @param tle the three line elements
         * @param dsv the deep space values
         */
        private void dpsec(final TLE tle) {
            dsv.xll = dsv.xll + ssl * dsv.t;
            dsv.omgadf = dsv.omgadf + ssg * dsv.t;
            dsv.xnode = dsv.xnode + ssh * dsv.t;
            dsv.em = tle.getEo() + sse * dsv.t;
            dsv.xinc = tle.getXincl() + ssi * dsv.t;

            if (dsv.xinc < 0) {
                dsv.xinc = -dsv.xinc;
                dsv.xnode = dsv.xnode + Math.PI;
                dsv.omgadf = dsv.omgadf - Math.PI;
            }

            if (!resonance) {
                return;
            }

            do {
                processEpochRestartLoop();
            }
            while (doLoop && epochRestart);

            dsv.xn = xni + xndot * ft + xnddt * ft * ft * 0.5;
            xl = xli + xldot * ft + xndot * ft * ft * 0.5;
            temp = -dsv.xnode + thgr + dsv.t * THDT;

            if (synchronous) {
                dsv.xll = xl - dsv.omgadf + temp;
            }
            else {
                dsv.xll = xl + temp + temp;
            }
        }

        /**
         * 
         */
        private void processEpochRestartLoop() {
            if ((atime == 0)
                    || ((dsv.t >= 0) && (atime < 0))
                    || ((dsv.t < 0) && (atime >= 0))) {
                /* Epoch restart */

                calclateDelt();

                atime = 0;
                xni = xnq;
                xli = xlamo;
            }
            else if (Math.abs(dsv.t) >= Math.abs(atime)) {
                calclateDelt();
            }

            processNotEpochRestartLoop();
        }

        private void calclateDelt() {
            if (dsv.t < 0) {
                delt = stepn;
            }
            else {
                delt = stepp;
            }
        }

        /**
         * 
         */
        private void processNotEpochRestartLoop() {
            do {
                if (Math.abs(dsv.t - atime) >= stepp) {
                    doLoop = true;
                    epochRestart = false;
                }
                else {
                    ft = dsv.t - atime;
                    doLoop = false;
                }

                if (Math.abs(dsv.t) < Math.abs(atime)) {
                    if (dsv.t >= 0) {
                        delt = stepn;
                    }
                    else {
                        delt = stepp;
                    }

                    doLoop |= epochRestart;
                }

                /* Dot terms calculated */
                if (synchronous) {
                    xndot = del1 * Math.sin(xli - fasx2) + del2
                            * Math.sin(2.0 * (xli - fasx4)) + del3
                            * Math.sin(3.0 * (xli - fasx6));
                    xnddt = del1 * Math.cos(xli - fasx2) + 2 * del2
                            * Math.cos(2.0 * (xli - fasx4)) + 3.0 * del3
                            * Math.cos(3.0 * (xli - fasx6));
                }
                else {
                    xomi = omegaq + dsv.omgdot * atime;
                    x2omi = xomi + xomi;
                    x2li = xli + xli;
                    xndot = d2201 * Math.sin(x2omi + xli - G22) + d2211
                            * Math.sin(xli - G22) + d3210
                            * Math.sin(xomi + xli - G32) + d3222
                            * Math.sin(-xomi + xli - G32) + d4410
                            * Math.sin(x2omi + x2li - G44) + d4422
                            * Math.sin(x2li - G44) + d5220
                            * Math.sin(xomi + xli - G52) + d5232
                            * Math.sin(-xomi + xli - G52) + d5421
                            * Math.sin(xomi + x2li - G54) + d5433
                            * Math.sin(-xomi + x2li - G54);
                    xnddt = d2201
                            * Math.cos(x2omi + xli - G22)
                            + d2211
                            * Math.cos(xli - G22)
                            + d3210
                            * Math.cos(xomi + xli - G32)
                            + d3222
                            * Math.cos(-xomi + xli - G32)
                            + d5220
                            * Math.cos(xomi + xli - G52)
                            + d5232
                            * Math.cos(-xomi + xli - G52)
                            + 2
                            * (d4410 * Math.cos(x2omi + x2li - G44) + d4422
                                    * Math.cos(x2li - G44) + d5421
                                    * Math.cos(xomi + x2li - G54) + d5433
                                    * Math.cos(-xomi + x2li - G54));
                }

                xldot = xni + xfact;
                xnddt = xnddt * xldot;

                if (doLoop) {
                    xli = xli + xldot * delt + xndot * step2;
                    xni = xni + xndot * delt + xnddt * step2;
                    atime = atime + delt;
                }
            }
            while (doLoop && !epochRestart);
        }

        /**
         * Entrance for lunar-solar periodics.
         * 
         * @param tle the three line elements
         * @param dsv the deep space values
         */
        private void dpper(final TLE tle) {
            sinis = Math.sin(dsv.xinc);
            cosis = Math.cos(dsv.xinc);

            if (Math.abs(savtsn - dsv.t) >= 30) {
                savtsn = dsv.t;
                zm = zmos + ZNS * dsv.t;
                zf = zm + 2 * ZES * Math.sin(zm);
                sinzf = Math.sin(zf);
                f2 = 0.5 * sinzf * sinzf - 0.25;
                f3 = -0.5 * sinzf * Math.cos(zf);
                ses = se2 * f2 + se3 * f3;
                sis = si2 * f2 + si3 * f3;
                sls = sl2 * f2 + sl3 * f3 + sl4 * sinzf;
                sghs = sgh2 * f2 + sgh3 * f3 + sgh4 * sinzf;
                shs = sh2 * f2 + sh3 * f3;
                zm = zmol + ZNL * dsv.t;
                zf = zm + 2 * ZEL * Math.sin(zm);
                sinzf = Math.sin(zf);
                f2 = 0.5 * sinzf * sinzf - 0.25;
                f3 = -0.5 * sinzf * Math.cos(zf);
                sel = ee2 * f2 + e3 * f3;
                sil = xi2 * f2 + xi3 * f3;
                sll = xl2 * f2 + xl3 * f3 + xl4 * sinzf;
                sghl = xgh2 * f2 + xgh3 * f3 + xgh4 * sinzf;
                sh1 = xh2 * f2 + xh3 * f3;
                pe = ses + sel;
                pinc = sis + sil;
                pl = sls + sll;
            }

            pgh = sghs + sghl;
            ph = shs + sh1;
            dsv.xinc = dsv.xinc + pinc;
            dsv.em = dsv.em + pe;

            if (xqncl >= 0.2) {
                /* Apply periodics directly */
                ph = ph / dsv.sinio;
                pgh = pgh - dsv.cosio * ph;
                dsv.omgadf = dsv.omgadf + pgh;
                dsv.xnode = dsv.xnode + ph;
                dsv.xll = dsv.xll + pl;
            }

            else {

                applyPeriodics();

                /* This is a patch to Lyddane modification */
                /* suggested by Rob Matson. */

                if (Math.abs(xnoh - dsv.xnode) > Math.PI) {
                    if (dsv.xnode < xnoh) {
                        dsv.xnode += TWO_PI;
                    }
                    else {
                        dsv.xnode -= TWO_PI;
                    }
                }

                dsv.xll = dsv.xll + pl;
                dsv.omgadf = xls - dsv.xll - Math.cos(dsv.xinc) * dsv.xnode;
            }
            return;
        }

        /**
         * Apply periodics with Lyddane modification.
         * 
         * @param dsv the space values
         */
        private void applyPeriodics() {
            sinok = Math.sin(dsv.xnode);
            cosok = Math.cos(dsv.xnode);
            alfdp = sinis * sinok;
            betdp = sinis * cosok;
            dalf = ph * cosok + pinc * cosis * sinok;
            dbet = -ph * sinok + pinc * cosis * cosok;
            alfdp = alfdp + dalf;
            betdp = betdp + dbet;
            dsv.xnode = AbstractSatellite.mod2PI(dsv.xnode);
            xls = dsv.xll + dsv.omgadf + cosis * dsv.xnode;
            dls = pl + pgh - pinc * dsv.xnode * sinis;
            xls = xls + dls;
            xnoh = dsv.xnode;
            dsv.xnode = Math.atan2(alfdp, betdp);
        }

        /**
         * The function ThetaG calculates the Greenwich Mean Sidereal Time for an epoch specified in
         * the format used in the NORAD two-line element sets. It has now been adapted for dates
         * beyond the year 1999, as described above. The function ThetaG_JD provides the same
         * calculation except that it is based on an input in the form of a Julian Date.
         * 
         * Reference: The 1992 Astronomical Almanac, page B6.
         * 
         * @param epoch the epach
         * @param dsv the deep space values
         * @return the Greenwich Mean Sidereal Time
         */
        private double thetaG(final double epoch) {

            /* Modification to support Y2K */
            /* Valid 1957 through 2056 */
            double year = Math.floor(epoch * 1E-3);
            double dayOfYear = (epoch * 1E-3 - year) * 1000.0;

            if (year < 57) {
                year = year + 2000;
            }
            else {
                year = year + 1900;
            }

            final double dayFloor = Math.floor(dayOfYear);
            final double dayFraction = dayOfYear - dayFloor;
            dayOfYear = dayFloor;

            final double jd = AbstractSatellite.julianDateOfYear(year) + dayOfYear;
            dsv.ds50 = jd - 2433281.5 + dayFraction;

            return AbstractSatellite.mod2PI(6.3003880987 * dsv.ds50 + 1.72944494);
        }
    }

    private static final class DeepSpaceValueObject implements Serializable {

        private static final long serialVersionUID = 5230929750062183569L;
        private double eosq;
        private double sinio;
        private double cosio;
        private double betao;
        private double aodp;
        private double theta2;
        private double sing;
        private double cosg;
        private double betao2;
        private double xmdot;
        private double omgdot;
        private double xnodot;
        private double xnodp;

        /* Used by dpsec and dpper parts of Deep() */
        private double xll;
        private double omgadf;
        private double xnode;
        private double em;
        private double xinc;
        private double xn;
        private double t;

        /* Used by thetg and Deep() */
        private double ds50;

        /**
         * Default constructor.
         */
        private DeepSpaceValueObject() {

        }
    }

}
