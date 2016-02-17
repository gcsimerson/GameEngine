To compile and run:

1. Import project into Eclipse.
2. Make sure Eclipse is able to build Processing PApplet by importing Processing's
core.jar file and add it to the build path of this project.
	https://processing.org/tutorials/eclipse/
3. Run Server first. It is located in the MainGameLoop package.
4. Then run Client. You can run as many Clients as you want.

Note: It will throw an exception whenever a client disconnects by terminating the processes.

To play:

space - jump/shoot
left - move left
right - move right

The Platformer.zip contains the code that implements scripting.
In the main game loop, a script is loaded to move the cloud across the screen.
In the cloud's event handler, it will change the color of the cloud when it the player
dies.

The SpaceInvader.zip contains code that implements my second game.
Press the space bar to shoot