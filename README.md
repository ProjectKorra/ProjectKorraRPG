# ProjectKorraRPG

**The official RPG addon for [ProjectKorra](https://www.projectkorra.com).**  
ProjectKorraRPG extends the core ProjectKorra plugin with immersive RPG-style features, offering a rich roleplaying experience for benders.

## Overview

ProjectKorraRPG introduces dynamic RPG elements into the world of bending. With exciting world events, a robust (upcoming) leveling system and skill tree, randomized element assignments, and a true-to-life avatar cycle, this plugin transforms gameplay into an epic adventure.

## Features

### World Events
- **Sozin's Comet:**  
  Experience the awe of a celestial event that supercharges your bending powers. When Sozin's Comet passes, firebenders and other benders feel an unprecedented boost, turning battles into blazing spectacles of power.

- **Full Moon:**  
  Under the mystic glow of the full moon, bending abilities can be either enhanced or challenged. This event introduces strategic twists to gameplay as the natural cycles of the moon affect every bender.

*Customize your server’s world events by simply adding or editing YAML files in the `plugins/ProjectKorraRPG/WorldEvents` directory.*

### RPG Level System & Skill Tree *(Upcoming)*
- **Player Progression:**  
  Earn experience through your bending battles and challenges. Level up to unlock new abilities.

- **Skill Tree:**  
  Tailor your character’s abilities by selecting unique branches on a customizable skill tree. Whether you want to focus on raw power or nuanced techniques, your choices define your play style.

- **Deep Customization:**  
  Invest your points into specialized talents that suit your preferred bending style. Each branch offers unique bonuses that enhance your roleplaying experience.

*Note: This feature is actively under development and will be available in upcoming releases.*

### Random Element Assignment
- **Dynamic Gameplay:**  
  Experience bending in entirely new ways as the plugin randomly assigns elements. This feature breaks away from the conventional bending roles, offering unexpected twists in every play session.

- **Replayability:**  
  With each random assignment, no two gameplay sessions are the same. Explore new combinations of bending skills for a truly unique challenge every time.

### Real Avatar Cycle
- **Authentic Avatars:**  
  Witness a genuine cycle of the avatar where transitions occur automatically, reflecting a natural, time-based progression.

- **Timed Transitions:**  
  The cycle changes based on realistic time intervals, influencing bending abilities and the overall gameplay environment. Every cycle brings new challenges and opportunities.

- **Living World:**  
  This feature creates a dynamic game world where every moment counts, and the fate of benders can change with each new cycle.

## Installation

1. **Requirements:**
    - A compatible Minecraft server (Spigot/Paper).
    - [ProjectKorra Core](https://www.projectkorra.com) installed.

2. **Download:**
    - Download the latest release from the [ProjectKorraRPG GitHub releases](https://github.com/ProjectKorra/ProjectKorraRPG/releases).

3. **Install:**
    - Place the `ProjectKorraRPG.jar` into your server’s `plugins` folder.
    - Restart your server.

4. **Configure:**
    - World event configurations can be managed in the `plugins/ProjectKorraRPG/WorldEvents` folder.
    - Customize existing events or create new ones by adding YAML files. The file name (without the `.yml` extension) will be used as the event key for commands.

## Usage

To start a world event, use the command: `/bending rpg event start <Event>`

Replace `<Event>` with the event key (which corresponds to the YAML file name without the extension). For example, if your file is named `SozinsComet.yml`, the command would be: `/bending rpg event start SozinsComet`
## Credits

Developed as part of the official [ProjectKorra](https://www.projectkorra.com) ecosystem. Special thanks to all contributors and community members who continue to support and improve the experience for benders worldwide.

---

Embrace your destiny, master your elements, and shape your story with ProjectKorraRPG!
