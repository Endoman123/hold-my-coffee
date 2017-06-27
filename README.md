# Hold My Coffee

“Hold My Coffee” is a top-down bullet hell. To win the game, players will try to defeat a large boss while managing to weave through heavy waves of its artillery. The entirety of the game takes place in one view. High score can be achieved by defeating the boss swiftly and with taking minimal damage.

## Features
- **LibGDX** - The LibGDX cross-platform game-development libraries provides plenty of resources to create the game, from the entity-component system, to asset management, as well as OpenGL rendering.
Screen Size Adaptable Using LibGDX Viewports and Cameras, the game can adapt to all screen sizes, allowing for great displays no matter what screen you are on.
- **Ashley ECS** - Objects in the game are all going to be entities with components attached onto them, which is better for handling game objects. This entity-component system is provided by Ashley ECS, a Java fork of the Artemis ECS that is provided with the LibGDX development library. This provides the necessary classes to create components and group entities into systems for processing.
- **Asset Management** - LibGDX’s built-in Asset Manager will help with load times by loading in all of our assets at runtime, and allowing us to access them via Asset Manager’s functions.
- **Texture Atlas Compilation** - Textures such as sprites and images will be compiled into one image rather than separated into separate files; this will speed up load times for the game.
- **One Player** - Play as one ship trying to fight some absurdly large and overpowered boss. Controllable with your keyboard. The player does have health and 3 lives, so if you do get hit, it’s not necessarily game over immediately.
- **Scoring** - The game tracks your score, and give benefits based on time, lives left, and accuracy.
Bullet Hell Player will try and shoot down enemies while outmaneuvering an incredibly absurd amount of bullets. In turn, the player have to be qpu aligned to survive.
- **Power Ups** - Give powerups that can help the player on their way to a high score. These can include faster speed, stronger bullets, time slow, etc. These stack up to 5 each, and you lose them when you die.
- **One Heavy Boss** - A bullet hell is hard enough, so we only have one heavily armored boss for you to fight to the death.

## Build Instructions
This is made with LibGDX and Gradle, so start a command window in the root of the project and type the following:

**Windows Shell:**

	> gradlew run

**Bash/Terminal:**

    $ ./gradlew run

This will run the game.
