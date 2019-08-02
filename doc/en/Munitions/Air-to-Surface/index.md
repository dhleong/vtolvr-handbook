## Air-to-Surface

The widest variety of available munitions are currently in the air-to-surface
category, for taking out tanks, SAM sites, radars, ships—you name it.

### Optically-guided

Optically guided weapons generally have a shorter range and stricter lock
requirements than GPS-guided weapons, but they are also vastly better for
hitting mobile targets, since they aren't simply moving towards a fixed
position. Make sure to be aware of any anti-air defences when going in for a
strike with an optically guided weapon!

#### Employment

The optically-guided missiles initially acquire their targets using the TGP.
First, open the TGP page on an MFD and slew it to a target until it locks on
in POINT mode—making sure the ITT reads "FOE," of course! Then, turn towards
the target. Like the air-to-air missiles, when selecting an optically-guided
missile you will initially get a circle in the middle of your HUD. Once you
are in range and the missile has a lock, you will hear a tone. At this point,
squeeze the trigger and move on to your next target

### Laser-guided

TK

#### Employment

TK

### GPS-guided

GPS-Guided weapons are a bit more complicated than other weapons, but are also
very accurate and can generally be employed from much safer distances, as
well.

#### Employment

There are currently two types of GPS-guided weapons: the bombs, and the cruise missiles.

*GPS-Guided Bombs*

Selecting a GPS-guided bomb will put two concentric rings on your HUD, with
the outer and inner rings representing the max radius the bomb can steer to
and the optimal steering radius, respectively. Any GPS targets from the
current GPS group will appear on the hud, which a diamond around the current
target. Simply maneuver until the target is in one of the rings (preferably
the "optimal" inner ring, especially if you're not very high up) and squeeze
the trigger.

Creating GPS targets can seem a bit daunting, but is easy enough once you get
used to it.  Currently, you can create GPS targets from either the MAP or the
TGP. In either place, slew onto the target and press the `GPS-S` (GPS "Send")
MFD button. This will add the location to the current GPS *group*, creating a
new group if there is none.

You can manage GPS targets and groups from the GPS page of the MFD, as you
might expect.

GPS-guided bombs also have a couple configuration options:

- Targeting Mode:
    - Manual: The default mode
    - Auto: After each trigger pull, the next target in the GPS group will be
      selected
    - Dumb: The bomb acts like an unguided bomb using [CCRP](#ccrp-employment)
      (see below).
- Deploy Rate: TK?

> **NOTE**: If you want a bit more accuracy from your MAP-created GPS targets,
> you can use the `GPS-A` (GPS "Acquire") MFD button on the TGP to slew the
> TGP onto the GPS target you created from the MAP, then manually slew the TGP
> until it locks, and use `GPS-S` one more time to create a new, more
> accurate, target.

*GPS-Guided Cruise Missiles*

Cruise Missiles are designed to follow a *path* of GPS points. To use them,
you generally create a new GPS group, switch to PATH mode, and add GPS points
as above. You may also use it in non-PATH mode, however, and the missile will
cruise directly to the GPS point and search for a target nearby.

### ARAD (Anti-radiation)

TK
