/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */
/*
 * Created on Sep 25, 2004
 *
 */
package megamek.common.weapons;

import megamek.common.TechConstants;

/**
 * @author Andrew Hunter
 */
public class ISAC20 extends ACWeapon {
    /**
     * 
     */
    private static final long serialVersionUID = 4780847244648362671L;

    /**
     * 
     */
    public ISAC20() {
        super();
        this.techLevel = TechConstants.T_IS_LEVEL_1;
        this.name = "Auto Cannon/20";
        this.setInternalName(this.name);
        this.addLookupName("IS Auto Cannon/20");
        this.addLookupName("ISAC20");
        this.addLookupName("IS Autocannon/20");
        this.heat = 7;
        this.damage = 20;
        this.rackSize = 20;
        this.shortRange = 3;
        this.mediumRange = 6;
        this.longRange = 9;
        this.extremeRange = 12;
        this.tonnage = 14.0f;
        this.criticals = 10;
        this.bv = 178;
        this.flags |= F_SPLITABLE;
        this.cost = 300000;
        this.shortAV = 20;
        this.maxRange = RANGE_SHORT;
    }
}
