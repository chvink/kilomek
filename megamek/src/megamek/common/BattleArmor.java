/*
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
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

package megamek.common;

import java.io.*;
import java.util.Enumeration;

/**
 * This class represents a squad or point of battle armor equiped infantry,
 * sometimes referred to as "Elementals".  Much of the behaviour of a
 * battle armor unit is identical to that of an infantry platoon, and is
 * rather different than that of a Mek or Tank.
 *
 * @author  Suvarov454@sourceforge.net (James A. Damour )
 * @version $revision:$
 */
/*
 *   PLEASE NOTE!!!  My programming style is to put constants first in
 *                   tests so the compiler catches my "= for ==" errors.
 */
public class BattleArmor
    extends Infantry
    implements Serializable
{
    /*
     * Infantry have no critical slot limitations.
     * IS squads have 4 men, Clan points have 5.
     * Have a location that represents the entire squad.
     */
    private static final int[]    IS_NUM_OF_SLOTS   = {7,1,1,1,1};
    private static final String[] IS_LOCATION_ABBRS = { "Squad",
                                                        "Trooper 1",
                                                        "Trooper 2",
                                                        "Trooper 3",
                                                        "Trooper 4" };
    private static final String[] IS_LOCATION_NAMES = { "Squad",
                                                        "Trooper 1",
                                                        "Trooper 2",
                                                        "Trooper 3",
                                                        "Trooper 4" };
    private static final int[]    CLAN_NUM_OF_SLOTS   = {7,1,1,1,1,1};
    private static final String[] CLAN_LOCATION_ABBRS = { "Point",
                                                          "Trooper 1",
                                                          "Trooper 2",
                                                          "Trooper 3",
                                                          "Trooper 4",
                                                          "Trooper 5"};
    private static final String[] CLAN_LOCATION_NAMES = { "Point",
                                                          "Trooper 1",
                                                          "Trooper 2",
                                                          "Trooper 3",
                                                          "Trooper 4",
                                                          "Trooper 5" };

    /**
     * The number of men alive in this unit at the beginning of the phase,
     * before it begins to take damage.
     */
    private int         troopersShooting = 0;

    /**
     * The battle value of this unit.  This value should
     * be set when the unit's file is read.
     */
    private int         myBV = 0;

    /**
     * Flag that is <code>true</code> when this
     * object's constructor has completed.
     */
    private boolean     isInitialized = false;

    // Public and Protected constants, constructors, and methods.

    /**
     * Model name of the Clan's water elemental.
     */
    public static final String CLAN_WATER_ELEMENTAL = "Undine";

    /**
     * Internal name of the Inner Sphere's disposable SRM2 ammo pack.
     */
    public static final String IS_DISPOSABLE_SRM2_AMMO =
        "BA-SRM2 (one shot) Ammo";

    /**
     * Internal name of the Inner Sphere's disposable NARC ammo pack.
     */
    public static final String IS_DISPOSABLE_NARC_AMMO =
        "BA-Compact Narc Ammo";

    /**
     * The internal name for Boarding Claw equipment.
     */
    public static final String BOARDING_CLAW = "BA-Boarding Claw";

    /**
     * The internal name for Assault Claw equipment.
     */
    public static final String ASSAULT_CLAW = "BA-Assault Claws";

    /**
     * The internal name for Magnetic Clamp equipment.
     */
    public static final String MAGNETIC_CLAMP = "BA-Magnetic Clamp";

    /**
     * The internal name for the Mine Launcher weapon.
     */
    public static final String MINE_LAUNCHER = "BAMineLauncher";

    /**
     * The maximum number of men in an Inner Sphere battle armor squad.
     */
    public static final int     BA_MAX_MEN = 4;

    /**
     * The maximum number of men in a Clan Elemental point.
     */
    public static final int     BA_CLAN_MAX_MEN = 5;

    /**
     * The location for infantry equipment.
     */
    public static final int     LOC_SQUAD       = 0;
    public static final int     LOC_IS_1        = 1;
    public static final int     LOC_IS_2        = 2;
    public static final int     LOC_IS_3        = 3;
    public static final int     LOC_IS_4        = 4;
    public static final int     LOC_CLAN_1      = 1;
    public static final int     LOC_CLAN_2      = 2;
    public static final int     LOC_CLAN_3      = 3;
    public static final int     LOC_CLAN_4      = 4;
    public static final int     LOC_CLAN_5      = 5;

    public String[] getLocationAbbrs() {
        if ( !this.isInitialized || this.isClan() ) {
            return CLAN_LOCATION_ABBRS;
        }
        return IS_LOCATION_ABBRS;
    }
    public String[] getLocationNames() {
        if ( !this.isInitialized || this.isClan() ) {
            return CLAN_LOCATION_NAMES;
        }
        return IS_LOCATION_NAMES;
    }

    /**
     * Returns the number of locations in this unit.
     */
    public int locations() {
        // Return one more than the maximum number of men in the unit.
        if ( !this.isInitialized || this.isClan() ) {
            return BA_CLAN_MAX_MEN + 1;
        }
        return BA_MAX_MEN + 1;
    }

    /**
     * Generate a new, blank, battle armor unit.
     * Hopefully, we'll be loaded from somewhere.
     */
    public BattleArmor() {
        // Instantiate the superclass.
        super();

        // Construction complete.
        this.isInitialized = true;
    };

    /**
     * Generates the display name for this entity.
     * <p/>
     * Sub-classes are allowed to override this method.
     * 
     * The display name is in the format [Short Name] ([Player Name]).
     */
    protected void generateDisplayName() {
        StringBuffer nbuf = new StringBuffer();
        nbuf.append( this.getShortName() );
        
        if (getOwner() != null) {
            nbuf.append(" (").append(getOwner().getName()).append(")");
        }
        
        this.displayName = nbuf.toString();
    }

    /**
     * Generate the short name for a unit
     * <p/>
     * Sub-classes are allowed to override this method.
     * 
     * The display name is in the format [Chassis] ([Weapon]) or
     * [Model] ([Weapon]) [Chassis].
     */
    protected void generateShortName() {
        StringBuffer nbuf = new StringBuffer();
        if (model != null && model.length() > 0) {
            if ( '(' == model.charAt(0) ) {
                nbuf.append( this.getChassis() )
                    .append(" ").append( this.getModel() );
            } else {
                nbuf.append( this.getModel() )
                    .append(" ").append( this.getChassis() );
            }
        } else {
            nbuf.append( this.getChassis() );
        }
        this.shortName = nbuf.toString();
    }

    /**
     * Returns this entity's original jumping mp.
     */
    protected int getOriginalJumpMP() {
        return jumpMP;
    }

    /**
     * Returns this entity's current jumping MP, not effected by terrain.
     * Certain types of equipment prevent a squad from jumping.
     */
    public int getJumpMP() {
        if ( this.isBurdened() ) {
            return 0;
        }
        return jumpMP;
    }

    /**
     * Most Infantry can not enter water.
     */
    public boolean isHexProhibited( Hex hex ) {
        if ( this.getModel().equals(CLAN_WATER_ELEMENTAL) ) {
            return false;
        }
        return (hex.levelOf(Terrain.WATER) > 0);
    }

    /**
     * Returns the name of the type of movement used.
     * This is Infantry-specific.
     */
    public String getMovementString(int mtype) {
        switch(mtype) {
        case MOVE_NONE :
            return "None";
        case MOVE_WALK :
        case MOVE_RUN :
            return "Walked";
        case MOVE_JUMP :
            return "Jumped";
        default :
            return "Unknown!";
        }
    }

    /**
     * Returns the abbreviation of the type of movement used.
     * This is Infantry-specific.
     */
    public String getMovementAbbr(int mtype) {
        switch(mtype) {
        case MOVE_NONE :
            return "N";
        case MOVE_WALK :
        case MOVE_RUN :
            return "W";
        case MOVE_JUMP :
            return "J";
        default :
            return "?";
        }
    }

    /**
     * Battle Armor units can only get hit in undestroyed troopers.
     */
    public HitData rollHitLocation( int table, int side ) {

        // If this squad was killed, target trooper 1 (just because).
        if ( this.isDoomed() ) return new HitData( 1 );

        // Pick a random number between 1 and 6.
        int loc = Compute.d6();

        // Pick a new random number if that trooper is dead or never existed.
        // Remember that there's one more location than the number of troopers.
        while ( loc >= this.locations() ||
                0 >= this.getInternal(loc) ) {
            loc = Compute.d6();
        }

        // Hit that trooper.
        return new HitData( loc );
    }

    /**
     * Battle Armor units don't transfer damage.
     */
    public HitData getTransferLocation(HitData hit) {

        // If any trooper lives, the unit isn't destroyed.
        for ( int loop = 1; loop < this.locations(); loop++ ) {
            if ( 0 < this.getInternal(loop) ) {
                return new HitData(Entity.LOC_NONE);
            }
        }

        // No surviving troopers, so we're toast.
        return new HitData(Entity.LOC_DESTROYED);
    }

    /**
     * Battle Armor units use default behavior for armor and internals.
     *
     * @see     megamek.common.Infantry#isPlatoon()
     */
    protected boolean isPlatoon() { return false; }

    /**
     * Battle Armor units have no armor on their squad location.
     *
     * @see     megamek.common.Infantry#getArmor( int, boolean )
     */
    public int getArmor( int loc, boolean rear ) {
        if ( BattleArmor.LOC_SQUAD != loc ) {
            return super.getArmor( loc, rear );
        }
        return Entity.ARMOR_NA;
    }

    /**
     * Battle Armor units have no armor on their squad location.
     *
     * @see     megamek.common.Infantry#getOArmor( int, boolean )
     */
    public int getOArmor( int loc, boolean rear ) {
        if ( BattleArmor.LOC_SQUAD != loc ) {
            return super.getOArmor( loc, rear );
        }
        return Entity.ARMOR_NA;
    }

    /**
     * Battle Armor units have no internals on their squad location.
     *
     * @see     megamek.common.Infantry#getInternal( int, boolean )
     */
    public int getInternal( int loc ) {
        if ( BattleArmor.LOC_SQUAD != loc ) {
            return super.getInternal( loc );
        }
        return Entity.ARMOR_NA;
    }

    /**
     * Battle Armor units have no internals on their squad location.
     *
     * @see     megamek.common.Infantry#getOInternal( int, boolean )
     */
    public int getOInternal( int loc ) {
        if ( BattleArmor.LOC_SQUAD != loc ) {
            return super.getOInternal( loc );
        }
        return Entity.ARMOR_NA;
    }

    /**
     * Set the troopers in the unit to the appropriate values.
     */
    public void autoSetInternal() {

        // No troopers in the squad location.
        this.initializeInternal( Entity.ARMOR_NA, LOC_SQUAD );

        // Initialize the troopers.
        for ( int loop = 1; loop < this.locations(); loop++ ) {
            this.initializeInternal( 1, loop );
        }

        // Set the initial number of troopers that can shoot
        // to one less than the number of locations in the unit.
        this.troopersShooting = this.locations() - 1;
    }
    
    /**
     * Mounts the specified equipment in the specified location.
     */
    protected void addEquipment(Mounted mounted, int loc, boolean rearMounted)
        throws LocationFullException 
    {
        // Implement parent's behavior.
        super.addEquipment( mounted, loc, rearMounted );

        // Add the piece equipment to our slots.
        addCritical( loc, new CriticalSlot(CriticalSlot.TYPE_EQUIPMENT,
                                           getEquipmentNum(mounted),
                                           true) );
    }

    /**
     * Battle Armor units have as many critical slots as they need to
     * hold their equipment.
     */
    protected int[] getNoOfSlots() {
        if ( !this.isInitialized || this.isClan() ) {
            return CLAN_NUM_OF_SLOTS;
        }
        return IS_NUM_OF_SLOTS;
    }
    
    /**
     * Trooper's equipment dies when they do.
     */
    public boolean hasHittableCriticals(int loc) {
        if ( LOC_SQUAD == loc ) return false;
        return super.hasHittableCriticals( loc );
    }

    /**
     * Calculates the battle value of this platoon.
     */
    public int calculateBattleValue() {
        // Adjust BV for crew skills.
        double pilotFactor = crew.getBVSkillMultiplier();
        return (int)(pilotFactor * (double)myBV);
    }

    /**
     * Prepare the entity for a new round of action.
     */    
    public void newRound()
    {
        // Perform all base-class behavior.
        super.newRound();

        // If we're equipped with a Magnetic Mine
        // launcher, turn it to single shot mode.
        for (Enumeration e = this.getMisc(); e.hasMoreElements(); ) {
            Mounted m = (Mounted)e.nextElement();
            EquipmentType equip = m.getType();
            if ( BattleArmor.MINE_LAUNCHER.equals(equip.getInternalName()) ) {
                m.setMode("Single");
            }
        }
    }

    /**
     * Update the unit to reflect damages taken in this phase.
     */
    public void applyDamage() {
        super.applyDamage();
        troopersShooting = this.getTotalInternal();
    }

    /**
     * Get the number of men in the unit (before damage is applied).
     *
     * @see     megamek.common.Infantry#getShootingStrength
     */
    public int getShootingStrength() { return troopersShooting; }

    /**
     * Sets the battle value of this unit.  Please note that the BV
     * of all Battle Armor units is dictated by the BMRr, page 155.
     *
     * @param   bv - the <code>int</code> battle value of this unit.
     */
    public void setBattleValue( int bv ) { myBV = bv; }

    /**
     * Determines if the battle armor unit is burdened with un-jettisoned
     * equipment.  This can prevent the unit from jumping or using their
     * special Anti-Mek attacks.
     *
     * @return  <code>true</code> if the unit hasn't jettisoned its
     *          equipment yet, <code>false</code> if it has.
     */
    public boolean isBurdened() {

        // Clan Elemental points are never burdened by equipment.
        if ( !this.isClan() ) {

            // As of 2003-01-03, only ammo burdens the jump
            Enumeration enum = this.getAmmo();
            Mounted mounted = null;
            EquipmentType type = null;

            // Loop through the squad's equipment.
            while ( enum.hasMoreElements() ) {
                mounted = (Mounted) enum.nextElement();
                type = mounted.getType();

                // Un-jettisoned ammo packs burden squads.
                if ( mounted.getShotsLeft() > 0 &&
                     (type.getInternalName().equals(IS_DISPOSABLE_SRM2_AMMO) ||
                      type.getInternalName().equals(IS_DISPOSABLE_NARC_AMMO))
                     ) {
                    return true;
                }

            } // Check the next piece of equipment

        } // End is-inner-sphere-squad

        // Unit isn't burdened.
        return false;
    }

} // End public class BattleArmor extends Infantry implements Serializable

