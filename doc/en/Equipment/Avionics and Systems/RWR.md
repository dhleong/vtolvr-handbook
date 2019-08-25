### Radar Warning Receiver

The Radar Warning Receiver (RWR) system in your aircraft detects hostile radar
signals and is used to maintain situational awareness about potential threats.
While in real life the RWR will typically include allied radar sources, in
VTOLVR they are omitted for simplicity and to reduce clutter.

The RWR in all aircraft is displayed in a circle, indicating position relative
to your aircraft's current orientation. Distance from the center of the circle
indicates *relative threat*, with higher *threat* radar sources closer to
the center of the circle. *Position on the RWR does not indicate range*.

Hostile radar sources are indicated on the RWR as a combination of one or two
letters/numbols/symbols surrounded by a symbol. The letters inside are an
abbreviation indicating the type of threat, while the symbology around it
provides more contextual information.

Table: RWR Symbology

Symbol | Meaning
:-:|:-
![Primary Threat](rwr/primary-threat.svg) | Primary Threat
![Locked Threat](rwr/threat-locked.svg) | Threat is locked-on or tracking you
![Newest Threat](rwr/new-threat.svg) | Newest threat
![Aerial Threat](rwr/aerial-threat.svg) | Aerial (flying) threat
![Missile Threat](rwr/missile-threat.svg) | Missile lock

- The *primary threat* indicates what the system believes to be the most
  threatening to your aircraft at the moment.
- A *tracking threat* may fire soon, so listen for warning.
- The *newest threat* can be located in the real world via your HUD/HMCS
  if your aircraft has [ARAD][arad] and its display is enabled on the HUD.
  New radar threats will be displayed as yellow diamonds on the HUD by ARAD.
- A *missle lock threat* is the only one that displays in a different color,
  to make sure you notice it. When you see the red M, you know a hostile
  missile has engaged its terminal targeting radar, which generally means
  it's about 10 seconds from hitting you.

Table: RWR Abbreviations

Abbreviation | Meaning
:-:|:-
A | Fire-control radar for Anti-Aircraft Artillery
DC | Drone Cruiser/Carrier
DF | Drone Fighter (aircraft)
DM | Drone Missile ship
DS | "Dish, SAM" The rotating-dish fire-control radar for a SAM
E4 | AWACS (Airborne Warning & Control System)
F  | Air-to-air Fighter (aircraft)
F+ | Air superiority Fighter (aircraft)
HC | "H-Carrier," an H-shaped aircraft carrier
SA | SAAW tank ("Self-propelled Anti-Air Weapon")
SR | "SAM Radar"; phased array control
