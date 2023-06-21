# CraftMine Parkour

A 3D platforming game, where the objective is to reach the highest block in the sky. Everytime you touch the block, you gain a point, and you must touch the ground before you can go back up and get another one. Watch out for the enemies in the middle! If they catch you, you will get launched.

## Features:

- Enemies that roam in the middle, and move towards the player and launches them if they get too close

- You can connect with other people on a local network and compete against them.

- There are 4 different avatars that can be selected at start-up, and other players on the network can see the avatar you chose in their client

## Controls:

- W: accelerates the player forward

- S: accelerates the player backwards

- A: turns the player left

- D: turns the player right

- Space: The player jumps

- X: jumps the player, but downwards instead of up

- Q: increase player move speed

- E: decrease player move speed

- P: toggle candle lights

- J: orbit camera left

- L: orbit camera right

- I: orbit camera up

- K: orbit camera down

- U: zoom in

- O: zoom out

- Esc: quit game

## Screenshots:

A view of the layout

<img src="screenshots\screen1.png"></img>

The enemies up close!

<img src="screenshots\screen2.png"></img>

An aerial shot

<img src="screenshots\screen3.png"></img>

## How To Run:

- Install Java, and add these files from the dependencies folder to CLASSPATH:

  - jogl\gluegen-rt.jar

  - jogl\joal.jar

  - jogl\jogl-all.jar

  - jbullet\jbullet.jar

  - jinput\jinput.jar

  - joml\joml-1.10.0.jar

  - vecmath\vecmath.jar

- Run compile.bat

- The host runs runServer.bat

- Everyone runs run.bat

## Credits:

Sounds from https://pixabay.com

Skybox from https://opengameart.org/content/space-skybox-1

Avatar and enemy model + enemy texture made by [JarodS132](https://github.com/JarodS132)

Candle model and texture + avatar skins made by me
